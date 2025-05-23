package com.ktnu.AiLectureSummary.dto.memberLecture;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberLectureListResponse {
    private List<MemberLectureListItemResponse> items;
    private long totalDuration;



    public static MemberLectureListResponse from(List<MemberLecture> memberLectures, long totalDuration) {
        return MemberLectureListResponse.builder()
                .items(MemberLectureListItemResponse.fromList(memberLectures))
                .totalDuration(totalDuration)
                .build();

    }
}
