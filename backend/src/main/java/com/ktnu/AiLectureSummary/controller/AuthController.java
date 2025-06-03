package com.ktnu.AiLectureSummary.controller;

import com.ktnu.AiLectureSummary.config.CookieProperties;
import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.service.MemberAuthService;
import com.ktnu.AiLectureSummary.util.CookieResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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
        // 로그인 처리 시도 (memberAuthService에서 인증 처리 + JWT 발급)
        MemberLoginResponse result = memberAuthService.login(request);

        // AccessToken & RefreshToken 쿠키 설정
        ResponseCookie accessCookie = CookieResponseUtil.buildAccessTokenCookie(result.getAccessToken(), cookieProperties);
        ResponseCookie refreshCookie = CookieResponseUtil.buildRefreshTokenCookie(result.getRefreshToken(), cookieProperties);

//        HTTP/1.1 200 OK
//        Set-Cookie: token=ACCESS_TOKEN_VALUE; Path=/; HttpOnly; Secure; SameSite=Lax
//        Set-Cookie: refresh_token=REFRESH_TOKEN_VALUE; Path=/; HttpOnly; Secure; SameSite=Lax

        return ResponseEntity.status(200)
                .header("Set-Cookie", accessCookie.toString())
                .header("Set-Cookie", refreshCookie.toString())
                .body(ApiResponse.success("로그인 성공", result)); // JWT는 모두 쿠키(HttpOnly)로 전송되며, 바디의 토큰 정보는 프론트에서 사용되지 않음 (개발 편의를 위해 포함함)
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
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {

        ResponseCookie expiredCookie = CookieResponseUtil.expireAccessTokenCookie(cookieProperties);
        ResponseCookie expiredRefreshCookie = CookieResponseUtil.expireRefreshTokenCookie(cookieProperties);

        // 덮어 씌우는 방식으로 로그아웃처리
        response.addHeader("Set-Cookie", expiredCookie.toString());
        response.addHeader("Set-Cookie", expiredRefreshCookie.toString());

        // blacklist 등록 및 refreshToken을 redis에서 삭제
        memberAuthService.logout(request);

        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료", null));
    }

    @PostMapping("/refresh")
    @Operation(summary = "AccessToken 재발급", description = "로그인 후 사용중 흐름이 끊기지 않도록 refreshToken 사용")
    public ResponseEntity<ApiResponse<Void>> refresh(HttpServletRequest request, HttpServletResponse response) {
        // refreshtoken 유효성 검사 & accesstoken 재발급
        String newAccessToken = memberAuthService.reissueAccessToken(request);

        // access_token 쿠키 설정
        ResponseCookie accessCookie = CookieResponseUtil.buildAccessTokenCookie(newAccessToken, cookieProperties);

        return ResponseEntity.ok()
                .header("Set-Cookie", accessCookie.toString())
                .body(ApiResponse.success("AccessToken 재발급", null));
    }
}
