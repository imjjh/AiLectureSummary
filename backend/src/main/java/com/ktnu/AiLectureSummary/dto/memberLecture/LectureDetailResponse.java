package com.ktnu.AiLectureSummary.dto.memberLecture;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Base64;

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
    private LocalDateTime enrolledAt;
    private String thumbnailBase64;

    public static LectureDetailResponse from(MemberLecture memberLecture) {
        Lecture lecture = memberLecture.getLecture();

        return LectureDetailResponse.builder()
                .id(lecture.getId())
                .duration(lecture.getDuration())
                .customTitle(memberLecture.getCustomTitle())
                .aiSummary(lecture.getAiSummary())
                .originalText(lecture.getOriginalText())
                .memo(memberLecture.getMemo())
                .enrolledAt(memberLecture.getEnrolledAt())
                .thumbnailBase64(Base64.getEncoder().encodeToString(memberLecture.getLecture().getThumbnail()))
                .build();
    }
}
