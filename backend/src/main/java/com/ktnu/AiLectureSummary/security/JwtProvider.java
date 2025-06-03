package com.ktnu.AiLectureSummary.security;

import com.ktnu.AiLectureSummary.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 검증 및 파싱을 담당하는 유틸 클래스
 */
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final Key key;

    // key 가공이 필요해 생성자 주입 직접 정의
    public JwtProvider(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT을 생성해서 문자열로 반환합니다.
     * (주체+발급시간+만료시간) 정보를 해싱하여 문자열로 반환합니다.
     * @param userId
     * @return jwt (문자열)
     */
    public  String generateAccessToken(Long userId){
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+jwtProperties.getExpiration()))
                .signWith(key) // HS256 알고리즘은 key에서 자동으로 유추됨 0.12.6 JJWT
                .compact(); // // 최종적으로 문자열 생성하는 메서드
    }

    /**
     * refreshToken을 생성해서 문자열로 반환합니다.
     * @param userId 사용자의 id 정보
     * @return 생성된 RefreshToken (문자열)
     */
    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getRefreshExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh") // 토큰 타입 구분용 클레임 추가 + accessToken과 같은 키를 가지지 않기위해 꼭 필요
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }


    /**
     * accessToken 유효성을 검사합니다.
     * 시그니처 검증하고 만료 시간을 검사합니다.
     * -> 서명 위조나 만료된 경우를 예외처리
     * @param accessToken
     * @return true | false (예외 발생시)
     */
    public boolean validateAccessToken(String accessToken){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(accessToken); // JWT의 서명과 만료 여부 등을 검사
            return true; // 이상 없으면 유효한 토큰
        }
        catch (JwtException | IllegalArgumentException e){
            return false; // 파싱 실패하거나 서명 위조, 만료된 경우
        }
    }

    /**
     * 전달받은 JWT 토큰을 파싱하여 Claims(클레임) 객체를 추출합니다.
     * 그 중 subject(sub) 필드에 저장된 사용자 ID를 꺼내 Long 타입으로 변환합니다.
     *
     * @param accessToken
     * @return 사용자의 id 정보
     */
    public Long getUserIdFromAccessToken(String accessToken){
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }


    /**
     * redis 블랙리스트에 등록하기 위해 만료시간까지 남은 시간(TTL)을 계산합니다.
     *
     * @param accessToken
     * @return 남은 시간 반환
     */
    public long getExpiration(String accessToken) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis(); // 남은 시간 (ms)

    }
}
