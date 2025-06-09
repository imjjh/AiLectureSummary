package com.ktnu.AiLectureSummary.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정 클래스
 *
 * 외부 API 호출시 사용되는 RestTemplate을 빈으로 등록합니다.
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        // 요청 시간 및 응답 시간 설정을 위한 팩토리 생성
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);  // 연결 타임아웃: 3초
        factory.setReadTimeout(100000); // 응답 타임 아웃: 100초

        // RestTemplate 인스턴스 생성 및 팩토리 설정 적용
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
        // 스프링 컨테이너에 등록될 RestTemplate Bean 반환
        return restTemplate;
    }
}
