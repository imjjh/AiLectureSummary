package com.ktnu.AiLectureSummary.dto.memberLecture;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberLectureListItemResponse {
    Long lectureId;
    String title;

    public static List <MemberLectureListItemResponse> fromList(List<MemberLecture> memberLectures){
        return memberLectures.stream()
                .map(memberLecture -> new MemberLectureListItemResponse(memberLecture.getLecture().getId(), memberLecture.getPersonalTitle()))
                .toList();
    }
}
