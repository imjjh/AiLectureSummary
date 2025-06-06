package com.ktnu.AiLectureSummary.dto.lecture;

import com.ktnu.AiLectureSummary.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 백엔드에서 클라이언트로 강의 데이터를 전달하기 위한 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class LectureUploadResponse {
    private long id;
//    private String title;
//    private String originalText;
//    private String aiSummary;
//    private long duration;



    public static LectureUploadResponse from(Lecture lecture) {

    return    LectureUploadResponse.builder()
                .id(lecture.getId())
//                .title(lecture.getTitleByAi())
//                .originalText(lecture.getOriginalText())
//                .aiSummary(lecture.getAiSummary())
//                .duration(lecture.getDuration())
                .build();
        }
}
