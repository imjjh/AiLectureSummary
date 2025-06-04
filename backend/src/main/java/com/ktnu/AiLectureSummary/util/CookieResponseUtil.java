package com.ktnu.AiLectureSummary.util;

import com.ktnu.AiLectureSummary.config.CookieProperties;
import org.springframework.http.ResponseCookie;

public class CookieResponseUtil {

    /**
     * accessToken을 담은 httponly 쿠키를 생성합니다.
     * 이 쿠키의 만료 시간은 JWT의 exp와는 무관하며,
     * 쿠키 자체가 브라우저에 유지되는 시간(maxAge)만 설정합니다.
     *
     * @param accessToken 생성된 access token 문자열
     * @param properties application.yml에서 정의한 쿠키 설정값
     * @return 생성된 ResponseCookie 객체
     */
    public static ResponseCookie buildAccessTokenCookie(String accessToken, CookieProperties properties) {
        return ResponseCookie.from("access_token", accessToken)
                .httpOnly(properties.isHttpOnly()) // true: JS에서 접근 불가 (XSS 방어)
                .path("/")                         // 루트 경로 이하 요청에 자동 포함
                .sameSite(properties.getSameSite()) // CSRF 방지용. Lax 또는 None (OAuth 등)
                .secure(properties.isSecure())     // HTTPS에서만 전송. 로컬 개발 시 false 가능
                // 아래 주석 중 하나만 선택하여 사용:
                // .maxAge(Duration.ofSeconds(properties.getAccessTokenExpiry())) // 지속 쿠키: JWT 유지 시간과 일치시키는 경우
                // 세션 쿠키로 만들려면 .maxAge() 생략하거나 -1로 설정 (브라우저 종료 시 쿠키 삭제됨)
                .build();
    }

    /**
     * access_token 쿠키를 즉시 만료시킵니다.
     * 기존 쿠키를 덮어씌우고 maxAge=0으로 설정하여 삭제합니다.
     *
     * 일반적으로 로그아웃 처리 시 사용되며,
     * access_token이 Redis 블랙리스트에 등록된 경우
     * 남은 유효시간 동안 서버에서도 거부됩니다.
     *
     * @param properties 쿠키 설정값 (application.yml 등에서 주입)
     * @return 즉시 만료되는 access_token 쿠키
     */
    public static ResponseCookie expireAccessTokenCookie(CookieProperties properties) {
        return ResponseCookie.from("access_token", "")
                .httpOnly(properties.isHttpOnly())
                .secure(properties.isSecure())
                .path("/")
                .sameSite(properties.getSameSite())
                .maxAge(0)
                .build();
    }

    /**
     * refresh_token을 담은 HttpOnly 쿠키를 생성합니다.
     * 이 쿠키는 보통 access token보다 더 긴 유효 기간을 가지며,
     * 클라이언트에 저장되었다가 access token 재발급 요청 시 자동으로 전송됩니다.
     *
     * @param refreshToken
     * @param properties
     * @return
     */
    public static ResponseCookie buildRefreshTokenCookie(String refreshToken, CookieProperties properties) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(properties.isHttpOnly())
                .path("/")
                .sameSite(properties.getSameSite())
                .secure(properties.isSecure())
//                .maxAge(Duration.ofSeconds(properties.getRefreshTokenExpiry())) // 주석처리로 완전한 로그아웃으로 구현 //TODO: 세션 쿠키 + refresh token 탈취 테스트
                .build();
    }

    /**
     * Refresh_token 쿠키를 즉시 만료시킵니다.
     * @param properties
     * @return
     */
    public static ResponseCookie expireRefreshTokenCookie(CookieProperties properties) {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(properties.isHttpOnly())
                .secure(properties.isSecure())
                .path("/")
                .sameSite(properties.getSameSite())
                .maxAge(0)
                .build();
    }
}

// TODO: 세션 쿠키 + refresh token 탈취 테스트
// 공격 시나리오
//	1.	사용자는 access_token을 세션 쿠키로 받음 (브라우저 종료 시 삭제)
//	2.	refresh_token은 지속 쿠키 (maxAge로 살아있음)
//	3.	사용자가 로그아웃 버튼 없이 브라우저만 끔
//	4.	공격자가 같은 PC에서 브라우저 열고 해당 사이트 접속
//	5.	refresh_token이 자동으로 서버에 전송됨 (HttpOnly 쿠키)
//	6.	서버는 이를 유효하다고 판단하고 새로운 access_token 발급
//	7.	로그인 없이 공격자 계정에 접근 성공