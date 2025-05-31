package com.ktnu.AiLectureSummary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    /**
     * Redis에서 String 타입의 key-value 저장을 쉽게 다룰 수 있도록 지원하는 템플릿 Bean입니다.
     * 주로 단순 문자열 데이터를 처리할 때 사용됩니다.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * Redis에서 다양한 자료형(Object)을 다룰 수 있도록 설정된 일반적인 RedisTemplate Bean입니다.
     * 복잡한 객체 직렬화 및 다양한 Redis 자료구조(Hash, List 등) 작업에 사용됩니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
