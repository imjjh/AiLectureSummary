package com.ktnu.AiLectureSummary.security.jwt;

import com.ktnu.AiLectureSummary.security.principal.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWTAuthenticationFilter
 *
 * 이 필터는 모든 HTTP 요청 전에 실행되어, 사용자의 인증 상태를 확인함.
 * - OncePerRequestFilter를 상속받아 한 요청당 한 번만 실행됨
 * - 요청의 쿠키에서 JWT 토큰을 추출하여 유효한지 검사함
 * - 유효한 토큰이라면, 해당 이메일로 사용자 정보를 불러와 Authentication 객체 생성
 * - 생성된 Authentication 객체를 SecurityContext에 등록함으로써 인증 완료 처리
 * - 이후 컨트롤러 등에서는 @AuthenticationPrincipal 등을 통해 사용자 정보 접근 가능
 *
 * 이 필터는 SecurityFilterChain에서 UsernamePasswordAuthenticationFilter 앞에 추가되어야 함.
 *
 * 	•	OncePerRequestFilter를 상속하면 → HTTP 요청 하나당 한 번만 동작하는 필터가 됨
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    // @RequiredArgsConstructor 사용 불가, 스프링이 관리하는 객체가 아닌 내가 직접 관리하는 수동 객체
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }


        /**
     * 인증 필터 제외 대상 설정
     *
     * 아래 경로들은 인증 없이 접근할 수 있어야 하므로,
     * JWT 인증 필터를 적용하지 않도록 제외 처리한다.
     * - Swagger UI: /swagger-ui, /v3/api-docs
     * - 회원가입 및 로그인: /api/members/register, /api/members/login
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/members/register") ||
               path.startsWith("/api/members/login") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }

    /**
     * JWT 인증 필터
     * - HTTP 요청의 쿠키에서 JWT 토큰을 추출한 후 유효성을 검사함
     * - 유효한 경우 토큰에서 사용자 이메일을 추출하고, 사용자 정보를 로드함
     * - 사용자 정보를 바탕으로 Authentication 객체를 생성하여 SecurityContext에 저장함
     * - 이후 컨트롤러에서 @AuthenticationPrincipal 등을 통해 인증 정보 사용 가능
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //  요청의 쿠키에서 jwt 토큰을 꺼냄
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰이 있고, 유효하면 인증 수행
        if (token != null && jwtTokenProvider.validateToken(token)) {

            String email = jwtTokenProvider.extractEmail(token);
            // DB에서 사용자를 조회하고, CustomUserDetails 객체로 감싸서 반환
            var userDetails = userDetailsService.loadUserByUsername(email); // 애는 또 뭔데 var임? 자바에 이런게 있음?

            // Spring Security에서 사용하는 공식 인증 객체
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 지금 요청에 대해 "인증된 사용자" 정보를 Spring Security에 등록함
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 인증이 끝났으니 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}