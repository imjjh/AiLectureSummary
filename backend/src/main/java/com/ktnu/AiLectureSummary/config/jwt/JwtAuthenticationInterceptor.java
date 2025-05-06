package com.ktnu.AiLectureSummary.config.jwt;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    // Spring MVC의 Filter/Interceptor 단계에서 실행, 컨트롤러 진입전 요청을 막는 구조
    // Servlet 수준 컴포넌트로 전역 예외 처리기 작동 X
    // preHandle() : 컨트롤러 메서드 실행 이전, 용도(인증, 권확 확인, 요청 차단 등)
    @Override
    public boolean preHandle (HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception{
        // 1. 요청에서 토큰 추출 (httpOnly+JWT)
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            return true; // 토큰 유효, 다음 단계로 진행
        }

        // 유효하지 않은 경우
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); //
        response.getWriter().write("{\"message\": \"Unauthorized: Token is missing or invalid.\"}");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
