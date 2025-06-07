package com.ktnu.AiLectureSummary.dto.memberLecture.response;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.util.ThumbnailUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberLectureListItemResponse {

    private Long lectureId;
    private String customTitle;
    private Long duration;
    private LocalDateTime enrolledAt;
    private String youtubeUrl;
    private String thumbnailBase64;

    public static List<MemberLectureListItemResponse> fromList(List<MemberLecture> memberLectures) {

        return memberLectures.stream()
                .map(memberLecture -> new MemberLectureListItemResponse(
                        memberLecture.getLecture().getId(),
                        memberLecture.getCustomTitle(),
                        memberLecture.getLecture().getDuration(),
                        memberLecture.getEnrolledAt(),
                        memberLecture.getLecture().getYoutubeUrl(),
                        ThumbnailUtil.encodeBase64ThumbnailSafe(memberLecture.getLecture().getThumbnail())
                ))
                .toList();
    }
}