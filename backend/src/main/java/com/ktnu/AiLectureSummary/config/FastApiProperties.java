package com.ktnu.AiLectureSummary.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fastapi")
@Getter
@Setter
public class FastApiProperties {
    private String url;
}
