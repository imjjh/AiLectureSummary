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
    private String customTitle;
    private String originalText;
    private String aiSummary;
    private String memo;
    private long duration;

    public static LectureDetailResponse from(MemberLecture memberLecture) {
        Lecture lecture = memberLecture.getLecture();

        return LectureDetailResponse.builder()
                .id(lecture.getId())
                .duration(lecture.getDuration())
                .customTitle(memberLecture.getCustomTitle())
                .aiSummary(lecture.getAiSummary())
                .originalText(lecture.getOriginalText())
                .memo(memberLecture.getMemo())
                .build();
    }
}
