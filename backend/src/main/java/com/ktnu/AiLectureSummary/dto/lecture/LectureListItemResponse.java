package com.ktnu.AiLectureSummary.dto.lecture;

import com.ktnu.AiLectureSummary.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class LectureListItemResponse {
    Long lectureId;
    String title;

    public static List <LectureListItemResponse> fromList(List <Lecture> lectures){
        return lectures.stream()
                .map(lecture -> new LectureListItemResponse(lecture.getId(), lecture.getTitle()))
                .toList();
    }
}
