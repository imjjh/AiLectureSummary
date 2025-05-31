package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.config.CookieProperties;
import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.member.*;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final CookieProperties cookieProperties;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "사용자 정보를 입력받아 이메일 중복 체크후 회원가입")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody MemberRegisterRequest request) {
        memberService.register(request);
        return ResponseEntity.status(201).body(ApiResponse.success("회원가입 성공", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 정보를 입력 받아 로그인 시도 성공시 jwt(httponly) 반환")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> login(@Valid @RequestBody MemberLoginRequest request, HttpServletResponse response) { // HttpServletResponse response는 응답 헤더, 쿠키를 직접 조작할수 있게 하기 위해 사용됨
        // 로그인 처리 시도 (memberService에서 인증 처리 + JWT 발급)
        MemberLoginResponse result = memberService.login(request);

        // 발급된 JWT를 Httponly 쿠키에 담아 응답 헤더에 추가
        ResponseCookie cookie = ResponseCookie.from("token", result.getAccessToken())
                .httpOnly(cookieProperties.isHttpOnly())
                .path("/")
                .sameSite(cookieProperties.getSameSite()) // CSRF 방어엔 Lax 이상 권장, 배포시 도메인 분리로 None 사용 예정
                .secure(cookieProperties.isSecure()) // https 에서만 전송 // 개발 환경 false
                .maxAge(3600) // JWT 만료 시간과 일치시킬 것 (쿠키 만료시 자동 삭제)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.status(200).header("Set-Cookie", cookie.toString()).body(ApiResponse.success("로그인 성공", result));
    }


    //비즈니스 로직이 거의 없고 가공 없는 응답만 주는 경우엔 Controller에서 처리

    /**
     * jwt를 덮어씌움 만료시간 0초로 설정 기존에 토큰은 제대로 삭제되는게 아니라 보완 필요
     *
     * @param response
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "jwt를 덮어씌움 만료시간 0초로 설정 (기존에 토큰은 제대로 삭제되는게 아니라 보완 필요(blacklist, refreshtoken 등)")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        // 즉시 만료되는 JWT를 생성
        ResponseCookie expiredCookie = ResponseCookie.from("token", "")
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure()) // https 환경 대응
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite(cookieProperties.getSameSite()) //
                .build();

        // TODO (refreshtoken 또는 blacklist 추가 예정)
        // 덮어 씌우는 방식으로 로그아웃처리
        response.addHeader("Set-Cookie", expiredCookie.toString());

        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료", null));
    }

    @GetMapping("/me")
    @Operation(summary = "사용자 정보", description = "로그인된 사용자의 정보를 반환")
    public ResponseEntity<ApiResponse<MemberMeResponse>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success("현재 사용자 정보", MemberMeResponse.from(userDetails)));
    }


    @PatchMapping("/edit")
    @Operation(summary = "계정 정보 변경", description = "사용자 입력으로 이름, 비밀번호를 수정합니다. 아이디는 수정할 수 없습니다.")
    public ResponseEntity<ApiResponse<MemberEditResponse>> editProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody MemberEditRequest request) {
        MemberEditResponse memberEditResponse = memberService.editProfile(userDetails, request);
        // TODO 둘다 NULL이면? 커스텀 예외 처리
        // 비밀번호 변경으로 토큰 재발급한 경우
        if (memberEditResponse.getToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("token", memberEditResponse.getToken())
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


    /**
     * 비밀번호 변경을 위해 사용자 이름과 이메일을 입력받아 일치하면 비밀번호를 변경할 수 있는 임시 토큰을 발급합니다.
     * 발급 받은 임시 토큰은 비밀번호 재설정 요청에서 헤더에 붙여서 전달해 주어야 비밀번호 변경이 가능합니다.
     * 학습/테스트용 기능으로 실서비스에선 이메일 인증이 필요함
     * @param request
     * @return
     */
    @PostMapping("/find-password")
    @Operation(summary = "비밀번호 찾기 요청(이메일 이름 입력)", description = "사용자 이름과 이메일을 입력받아 일치하면 비밀번호를 변경할 수 있는 임시 토큰을 발급합니다.\n" +
            "        발급 받은 임시 토큰은 비밀번호 재설정 요청에서 헤더에 붙여서 전달해 주어야 비밀번호 변경이 가능합니다.")
    public ResponseEntity<ApiResponse<MemberPasswordResetTokenResponse>> findPassword(@Valid @RequestBody MemberFindPasswordRequest request) {        // 실제 이메일 인증 등을 통하지 않아서 위험한 방식
        MemberPasswordResetTokenResponse response = memberService.findPassword(request);
        return ResponseEntity.ok(ApiResponse.success("사용자의 이름과 이메일 일치, 임시 토큰 발급(15분 유효)", response));
    }

    /**
     * /find-password에서 발급받은 임시토큰을 사용하여 비밀번호를 수정합니다.
     * @param request
     * @return
     */
    // TODO @RateLimiter(name="..") 요청 제한 추가 (무차별 대입 방어)
    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정 요청(비밀번호 입력)",
            description = "사용자의 비밀번호를 재설정 합니다. 비밀번호 찾기 요청에서 발급 받은 임시토큰을 커스텀헤더" +
                    "Reset-Token:... 으로 함께 전송합니다.")

    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "비밀번호 재설정을 위한 임시 토큰",required = true) @RequestHeader("Reset-Token") String token,
            @Valid @RequestBody MemberResetPasswordRequest request) {
        memberService.resetPassword(token, request);
        return ResponseEntity.ok(ApiResponse.success("새로운 비밀번호로 수정되었습니다.", null));
    }
}
