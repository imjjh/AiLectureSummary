package com.ktnu.AiLectureSummary.dto.lecture;

import com.ktnu.AiLectureSummary.domain.Lecture;
import lombok.Getter;

/**
 * 백엔드에서 클라이언트로 강의 데이터를 전달하기 위한 DTO
 */
@Getter
public class LectureResponse {
    private Long id;  // ✅ ID 필드 추가
    private String title;
    private String originalText;
    private String aiSummary;


    public static LectureResponse from(Lecture lecture){
        LectureResponse response = new LectureResponse();
        response.id = lecture.getId();  // ✅ ID 값 설정
        response.title = lecture.getTitle();
        response.originalText = lecture.getOriginalText();
        response.aiSummary = lecture.getAiSummary();
        return response;
    }
}
