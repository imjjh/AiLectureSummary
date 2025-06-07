package com.ktnu.AiLectureSummary.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private final String error;
    private final String message;
    private final int status;
    private final String path;
    private final LocalDateTime timestamp;

    // 헬퍼 메서드
    public static ErrorResponse of(String error, String message, int status, String path) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}