package com.ktnu.AiLectureSummary.controller;

import com.ktnu.AiLectureSummary.config.CookieProperties;
import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.service.MemberAuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberAuthService memberAuthService;
    private final CookieProperties cookieProperties;



    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "사용자 정보를 입력받아 이메일 중복 체크후 회원가입")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody MemberRegisterRequest request) {
        memberAuthService.register(request);
        return ResponseEntity.status(201).body(ApiResponse.success("회원가입 성공", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 정보를 입력 받아 로그인 시도 성공시 jwt(httponly) 반환")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> login(@Valid @RequestBody MemberLoginRequest request, HttpServletResponse response) { // HttpServletResponse response는 응답 헤더, 쿠키를 직접 조작할수 있게 하기 위해 사용됨
        // 로그인 처리 시도 (memberService에서 인증 처리 + JWT 발급)
        MemberLoginResponse result = memberAuthService.login(request);

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
}
