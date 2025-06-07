package com.ktnu.AiLectureSummary.global.config;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cookie")
@Getter
@Setter
public class CookieProperties {
    private boolean httpOnly;
    private boolean secure;

    @Pattern(regexp = "^(None|Lax|Strict)$", message = "SameSite must be None, Lax, or Strict")
    private String sameSite;

    private long accessTokenExpiry;
    private long refreshTokenExpiry;

}
