package com.ktnu.AiLectureSummary.dto.memberLecture;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
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

    public static LectureDetailResponse from(MemberLecture memberLecture) {
        Lecture lecture = memberLecture.getLecture();

        return LectureDetailResponse.builder()
                .id(lecture.getId())
                .title(memberLecture.getPersonalTitle())
                .aiSummary(lecture.getAiSummary())
                .originalText(lecture.getOriginalText())
                .personalNote(memberLecture.getPersonalNote())
                .build();
    }
}
