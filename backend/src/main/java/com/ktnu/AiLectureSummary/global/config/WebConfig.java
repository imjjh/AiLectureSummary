package com.ktnu.AiLectureSummary.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 API 경로
                .allowedOrigins("http://localhost:3000",    "https://aisummarymono.vercel.app"
)  // 프론트엔드 주소, 배포주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH")
                .allowedHeaders("*")
                .allowCredentials(true); // 인증정보 허용
    }
}