package com.ktnu.AiLectureSummary.config;

import com.ktnu.AiLectureSummary.security.JwtAuthenticationFilter;
import com.ktnu.AiLectureSummary.security.handler.CustomAccessDeniedHandler;
import com.ktnu.AiLectureSummary.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring Security 설정 클래스입니다. 세션 없이 JWT 기반 인증을 적용합니다.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Enable CORS with WebMvcConfigurer support
                .csrf(csrf -> csrf.disable())  // API 서버는 보통 CSRF 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // httponly 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/members/login", "/api/members/register", "/health",
                                "/v3/api-docs/**","/api/members/find-password","/api/members/reset-password",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll() // 로그인, 회원가입, 스웨거, 헬스체크, 비밀번호 변경 인증없이 접근 허용
//                        .requestMatchers(HttpMethod.GET, "/api/Lecture/**").permitAll()
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter,  // filter 작동전 Jwt필터 추가 (JWT 필터를 실행하여 토큰 인증 선처리)
                        UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 전에 JWT 필터를 실행하여 토큰 인증 선처리 //Spring Security에 인증된 사용자임을 알려주기 위한 객체
                .build();
    }

    /**
     * 로그인 시 AuthenticationManager를 통해 인증을 수행하기 위해 Bean으로 등록.
     * 인증된 사용자임을 알려주기 위해 사용합니다.
     * @param config
     * @return
     * @throws Exception
     */ @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}