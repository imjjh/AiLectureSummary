package com.ktnu.AiLectureSummary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // JPA 엔티티의 생성일, 수정일을 자동으로 기록해주는 Auditing 기능 활성화
public class JpaConfig {
    // 이 설정으로 @CreatedDate, @LastModifiedDate 어노테이션이 동작하게 됨
}
