package com.ktnu.AiLectureSummary.dto.lecture;

import com.ktnu.AiLectureSummary.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LectureDetailResponse {
    private Long id;
    private String title;
    private String originalText;
    private String aiSummary;
    private String personalNote;

    public static LectureDetailResponse from(Lecture lecture,String personalNote){
        return LectureDetailResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .aiSummary(lecture.getAiSummary())
                .originalText(lecture.getOriginalText())
                .personalNote(personalNote)
                .build();
    }
}
