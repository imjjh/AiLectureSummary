package com.ktnu.AiLectureSummary.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 서명에 사용할 비밀 키 (256비트 이상 필요)
    // Docker 실행 시: .env → docker-compose.yml → 컨테이너 환경변수 → application.yml → @Value 주입
    private final Key key;
    private static final long EXPIRATION_TIME = 1000L * 60 * 60; // JWT 토큰의 유효기간 -> 1시간

    // 환경 변수에서 주입된 비밀 키를 바탕으로 JWT 서명용 Key 객체 생성
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
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
}