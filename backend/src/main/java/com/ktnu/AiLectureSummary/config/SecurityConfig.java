package com.ktnu.AiLectureSummary.config;

import com.ktnu.AiLectureSummary.security.handler.CustomAccessDeniedHandler;
import com.ktnu.AiLectureSummary.security.handler.CustomAuthenicationEntryPoint;
import com.ktnu.AiLectureSummary.security.principal.CustomUserDetailsService;
import com.ktnu.AiLectureSummary.security.jwt.JwtAuthenticationFilter;
import com.ktnu.AiLectureSummary.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenicationEntryPoint customAuthenicationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 완전한 stateless JSESSIONID 같은 쿠키 생성 X, 인증은 JWT 쿠키로만 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/members/login",
                                "/api/members/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenicationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(
                        // SecurityFilterChain에서 .addFilterBefore(...)은 빈 이름이 아닌 "객체 인스턴스"를 직접 요구함
                        // 따라서 JwtAuthenticationFilter는 new를 통해 직접 생성해 넘겨줘야 한다
                        // 물론 이 필터를 @Bean으로 등록해 빈으로 만들 수도 있지만,
                        // 결국 addFilterBefore()에서는 해당 빈을 직접 꺼내서 호출해야 하므로 코드가 복잡해질 수 있음
                        // 이 필터는 new(...)로 수동 생성하여 등록하는 방식이 일반적이다
                        new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

}
