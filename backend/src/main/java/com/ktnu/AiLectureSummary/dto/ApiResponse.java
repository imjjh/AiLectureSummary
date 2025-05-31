package com.ktnu.AiLectureSummary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success; // 요청 성공 (true)
    private String message; //
    private T data;  // optional (예: 로그인 시 user 정보 등)

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
}
