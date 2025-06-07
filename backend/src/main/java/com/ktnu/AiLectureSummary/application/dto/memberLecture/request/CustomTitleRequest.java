package com.ktnu.AiLectureSummary.application.dto.memberLecture.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomTitleRequest {
    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Schema(description = "수정할 제목", example = "수정된 제목")
    private String title;
}
