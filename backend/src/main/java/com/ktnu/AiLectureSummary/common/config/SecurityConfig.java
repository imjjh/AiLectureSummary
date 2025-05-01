package com.ktnu.AiLectureSummary.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())  // CORS 설정 기본값 적용 (프론트엔드와의 통신 허용)
                .csrf(csrf -> csrf.disable())     // CSRF 보호 비활성화 (JWT 인증 방식 사용 시 필요 없음)
                .authorizeHttpRequests(auth -> auth // 요청 URL 별로 권한 설정 시작
                        .requestMatchers(  // Swagger 페이지는 인증 없이 열 수 있게 설정
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll() // 정적 리소스 문제로 Swagger는 별도 허용이 안정적
                        .anyRequest().permitAll()     // 모든 요청 인증 없이 허용 (개발용 설정)
                );

        return http.build();
    }
}
