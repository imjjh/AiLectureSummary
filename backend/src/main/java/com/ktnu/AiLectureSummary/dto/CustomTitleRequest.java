package com.ktnu.AiLectureSummary.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CustomTitleRequest {
    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Schema(description = "수정할 제목", example = "수정된 제목")
    private String title;
}
