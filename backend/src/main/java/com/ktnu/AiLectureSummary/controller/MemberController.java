package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.member.*;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "사용자 정보를 입력받아 이메일 중복 체크후 회원가입")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody MemberRegisterRequest request){
        memberService.register(request);
        return ResponseEntity.status(201).body(ApiResponse.success("회원가입 성공", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인" , description = "사용자 정보를 입력 받아 로그인 시도 성공시 jwt(httponly) 반환")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> login(@Valid @RequestBody MemberLoginRequest request, HttpServletResponse response) { // HttpServletResponse response는 응답 헤더, 쿠키를 직접 조작할수 있게 하기 위해 사용됨
        // 로그인 처리 시도 (memberService에서 인증 처리 + JWT 발급)
        MemberLoginResponse result = memberService.login(request);

        // 발급된 JWT를 Httponly 쿠키에 담아 응답 헤더에 추가
        ResponseCookie cookie = ResponseCookie.from("token", result.getAccessToken())
                .httpOnly(true) // Swagger 테스트 용도 (개발용)  // TODO 배포 전 true 필수
                .path("/")
                .sameSite("None") // SameSite=None: 다른 도메인에서도 쿠키 전송 허용 (HTTPS 필요)
                .secure(true) // https 에서만 전송 현재 http //TODO http->https 변경 이후 수정
                .maxAge(3600) // JWT 만료 시간과 일치시킬 것 (쿠키 만료시 자동 삭제)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.status(200).header("Set-Cookie", cookie.toString()).body(ApiResponse.success("로그인 성공", result));
    }



    //비즈니스 로직이 거의 없고 가공 없는 응답만 주는 경우엔 Controller에서 처리
    /**
     * jwt를 덮어씌움 만료시간 0초로 설정 기존에 토큰은 제대로 삭제되는게 아니라 보완 필요
     * @param response
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "jwt를 덮어씌움 만료시간 0초로 설정 (기존에 토큰은 제대로 삭제되는게 아니라 보완 필요(blacklist, refreshtoken 등)")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        // 즉시 만료되는 JWT를 생성
        ResponseCookie expiredCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true) // https 환경 대응
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite("None") // SameSite=None: 다른 도메인에서도 쿠키 전송 허용 (HTTPS 필요)
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

        // 비밀번호 변경으로 토큰 재발급한 경우
        if (memberEditResponse.getToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("token", memberEditResponse.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None") // SameSite=None: 다른 도메인에서도 쿠키 전송 허용 (HTTPS 필요)
                    .maxAge(3600)
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(ApiResponse.success("수정된 사용자 정보", memberEditResponse));
        }

        // 비밀번호 등 민감한 정보를 변경하지 않은 경우
        return ResponseEntity.ok(ApiResponse.success("수정된 사용자 정보", memberEditResponse));
    }
}
