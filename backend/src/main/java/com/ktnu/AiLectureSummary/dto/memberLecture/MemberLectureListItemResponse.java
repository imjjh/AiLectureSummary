package com.ktnu.AiLectureSummary.dto.memberLecture;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberLectureListItemResponse {
    Long lectureId;
    String customTitle;
    Long duration;
    LocalDateTime enrolledAt;

    public static List <MemberLectureListItemResponse> fromList(List<MemberLecture> memberLectures){

        return memberLectures.stream()
                .map(memberLecture -> new MemberLectureListItemResponse(
                    memberLecture.getLecture().getId(),
                    memberLecture.getCustomTitle(),
                    memberLecture.getLecture().getDuration(),
                    memberLecture.getEnrolledAt()
                ))
                .toList();
    }
}
