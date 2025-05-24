package com.ktnu.AiLectureSummary.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CustomTitleRequest {
    @NotBlank(message = "제목은 공백일 수 없습니다.")
    private String title;
}
