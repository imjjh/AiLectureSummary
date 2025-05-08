package com.ktnu.AiLectureSummary.security.jwt;

import com.ktnu.AiLectureSummary.security.principal.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 서명에 사용할 비밀 키 (256비트 이상 필요)
    // Docker 실행 시: .env → docker-compose.yml → 컨테이너 환경변수 → application.yml → @Value 주입
    private final Key key;
    private final CustomUserDetailsService userDetailsService;
    private static final long EXPIRATION_TIME = 1000L * 60 * 60; // JWT 토큰의 유효기간 -> 1시간

    // 환경 변수에서 주입된 비밀 키를 바탕으로 JWT 서명용 Key 객체 생성
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            CustomUserDetailsService userDetailsService) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.userDetailsService = userDetailsService;
    }


    // 토큰 생성
    public String createToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 이 키로 서명을 검증
                .build()
                .parseClaimsJws(token) // 토큰을 파싱+유효성 검사(서명,만료 등)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            extractEmail(token); // 파싱만 성공하면 유효
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    /**
     * JWT 추출 방식 두 가지:
     *    [1] Authorization 헤더에서 Bearer 토큰 추출
     *    [2] 쿠키에서 token 값 추출
     * 이 프로젝트에서는 HttpOnly 쿠키 사용으로 [2]번 방식 사용
     *
     * @param request 클라이언트의 HTTP 요청
     * @return 유효한 JWT 토큰 문자열, 없으면 null
     */
    public String resolveToken(HttpServletRequest request) {
        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if("token".equals(cookie.getName()))
                    return cookie.getValue();
            }
        }
        return null;
    }
    /**
     * JWT 토큰으로부터 인증 정보(Authentication) 객체 생성
     * - 토큰에서 이메일을 추출한 후 해당 사용자의 UserDetails를 조회하여 Spring Security의 Authentication 반환
     * - SecurityContext에 저장되어 @AuthenticationPrincipal 등에서 사용됨
     *
     * @param token 유효한 JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        String email = extractEmail(token); // 토큰에서 사용자 식별 정보 추출
        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // 사용자 정보 조회
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); // 인증 객체 반환
    }
}