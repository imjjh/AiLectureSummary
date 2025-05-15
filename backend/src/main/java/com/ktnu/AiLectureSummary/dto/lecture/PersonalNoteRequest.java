package com.ktnu.AiLectureSummary.dto.lecture;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalNoteRequest {
    @Size(max = 1000, message = "메모는 최대 1000자 까지 입력 가능합니다.")
    private String note;
}
