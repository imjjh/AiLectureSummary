package com.ktnu.AiLectureSummary.dto.memberLecture.response;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.util.ThumbnailUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    private String youtubeUrl;

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
                .youtubeUrl(lecture.getYoutubeUrl())
                .thumbnailBase64(ThumbnailUtil.encodeBase64ThumbnailSafe(memberLecture.getLecture().getThumbnail()))
                .build();
    }

}
