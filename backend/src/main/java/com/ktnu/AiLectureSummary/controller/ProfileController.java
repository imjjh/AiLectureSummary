package com.ktnu.AiLectureSummary.controller;

import com.ktnu.AiLectureSummary.application.MemberDeleteApplicationService;
import com.ktnu.AiLectureSummary.config.CookieProperties;
import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberEditRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberEditResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberProfileResponse;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.MemberProfileService;
import com.ktnu.AiLectureSummary.util.CookieResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class ProfileController {

    private final MemberProfileService memberProfileService;
    private final CookieProperties cookieProperties;
    private final MemberDeleteApplicationService memberDeleteApplicationService;


    @GetMapping("/me")
    @Operation(summary = "사용자 정보", description = "로그인된 사용자의 정보를 반환")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보", MemberProfileResponse.from(userDetails)));
    }


    @PatchMapping("/me")
    @Operation(summary = "계정 정보 변경", description = "사용자 입력으로 이름, 비밀번호를 수정합니다. 아이디는 수정할 수 없습니다.")
    public ResponseEntity<ApiResponse<MemberEditResponse>> editProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody MemberEditRequest request) {
        MemberEditResponse memberEditResponse = memberProfileService.editProfile(userDetails, request);
        // 비밀번호 변경으로 토큰 재발급한 경우
        if (memberEditResponse.getToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("access_token", memberEditResponse.getToken())
                    .httpOnly(cookieProperties.isHttpOnly())
                    .secure(cookieProperties.isSecure())
                    .path("/")
                    .sameSite(cookieProperties.getSameSite())
                    .maxAge(3600)
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(ApiResponse.success("수정된 사용자 정보", memberEditResponse));
        }

        // 비밀번호 등 민감한 정보를 변경하지 않은 경우
        return ResponseEntity.ok(ApiResponse.success("수정된 사용자 정보", memberEditResponse));
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "소프트 삭제로 멤버를 비활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@AuthenticationPrincipal CustomUserDetails user) {
        memberDeleteApplicationService.deleteMember(user.getId());

        ResponseCookie expiredAccessToken = CookieResponseUtil.expireAccessTokenCookie(cookieProperties);
        ResponseCookie expiredRefreshToken = CookieResponseUtil.expireRefreshTokenCookie(cookieProperties);

        return ResponseEntity.ok()
                .header("set-cookie", expiredAccessToken.toString())
                .header("set-cookie", expiredRefreshToken.toString())
                .body(ApiResponse.success("정상적으로 탈퇴 처리되었습니다.", null));
    }
}
