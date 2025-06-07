package com.ktnu.AiLectureSummary.global.security;

import com.ktnu.AiLectureSummary.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 쿠키에서 JWT 추출
        String token = CookieUtil.getCookieValue(request, "access_token").orElse(null);

        if (token != null) {
            // 블랙리스트 확인
            if (jwtProvider.isBlacklisted(token)) {
                log.warn("차단된 access_token 요청: {}", token); // 로그 기록
                filterChain.doFilter(request, response); // 인증 없이 넘김 (Security 설정에 따라 인증 정보가 없어 403, 401 응답)
                return;
            }
            // 유효성 검사
            if (jwtProvider.validateAccessToken(token)) {
                // 토큰에서 사용자 ID 추출
                Long userId = jwtProvider.getUserIdFromAccessToken(token);

                // 해당 ID로 사용자 정보 조회 // 탈퇴 여부 검사 포함
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(userId);

                // 사용자 정보가 없는 경우 인증 실패 처리
                if (userDetails == null) {
                    return;
                }

                // 인증 객체 생성 (사용자 정보 + 권한)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // 사용자 정보
                                null, // 비밀번호, 자격증명용 (JWT로 사용자 인증이 끝났기에 null)
                                userDetails.getAuthorities() // 권한
                        );
                // 요청 정보 기반으로 인증 디테일 설정(부가 기능)
                // 인증객체에 추가적인 정보 (클라이언트 IP, 세션 ID 등)를 담음 -> 로깅, 감사, 트래픽 분석에 사용됨
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 현재 요청에 대해 인증 정보 등록 (SecurityContext에 저장됨)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 8. 인증 실패 또는 토큰이 없는 경우에도 필터는 통과시킴
        //    → 이후 Security 설정에 따라 인증되지 않은 사용자는 접근 거부됨 (로그인, 회원가입 페이지 등 permitAll()로 열어둔 URL만 접근 가능)
        filterChain.doFilter(request, response);
    }

}
