package com.ktnu.AiLectureSummary.global.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Configuration
@Validated
@ConfigurationProperties(prefix="jwt")
public class JwtProperties {
    @NotEmpty(message = "JWT secret cannot be empty")
    private String secret;

//    @Min(value=60000,message = "JWT expiration should be at least 60000ms (1 minute)") //Spring이 자꾸 String으로 환경변수를 읽어서 주석처리
    private long expiration;

    private long refreshExpiration;
}
