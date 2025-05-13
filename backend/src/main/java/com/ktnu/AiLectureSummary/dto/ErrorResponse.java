package com.ktnu.AiLectureSummary.dto;

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
}
