package com.ktnu.AiLectureSummary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    /**
     * 헬스 체크 (서버 깨우기)
     * @return
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

}
