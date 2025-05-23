package com.ktnu.AiLectureSummary.dto.memberLecture;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemoRequest {
    @Size(max = 1000, message = "메모는 최대 1000자 까지 입력 가능합니다.")
    private String memo;
}
