package com.ktnu.AiLectureSummary.dto.memberLecture.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemoRequest {
    @Size(max = 1000, message = "메모는 최대 1000자 까지 입력 가능합니다.")
    @Schema(description = "사용자 작성 메모", example = "메모를 작성합니다. 안녕하세요. 오늘은 0530입니다.")
    private String memo;
}
