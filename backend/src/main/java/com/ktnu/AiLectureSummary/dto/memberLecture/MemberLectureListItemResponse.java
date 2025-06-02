package com.ktnu.AiLectureSummary.dto.memberLecture;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberLectureListItemResponse {
    private Long lectureId;
    private String customTitle;
    private Long duration;
    private LocalDateTime enrolledAt;
    private String thumbnailBase64;

    public static List<MemberLectureListItemResponse> fromList(List<MemberLecture> memberLectures) {

        return memberLectures.stream()
                .map(memberLecture -> new MemberLectureListItemResponse(
                        memberLecture.getLecture().getId(),
                        memberLecture.getCustomTitle(),
                        memberLecture.getLecture().getDuration(),
                        memberLecture.getEnrolledAt(),
                        Base64.getEncoder().encodeToString(memberLecture.getLecture().getThumbnail())

                ))
                .toList();
    }
}
