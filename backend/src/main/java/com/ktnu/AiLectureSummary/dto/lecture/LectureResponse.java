package com.ktnu.AiLectureSummary.dto.lecture;
// Spring -> client


import com.ktnu.AiLectureSummary.domain.Lecture;

public class LectureResponse {
    private String title;
    private String originalText;
    private String aiSummary;


    public static LectureResponse from(Lecture lecture){
        LectureResponse response = new LectureResponse();
        response.title = lecture.getTitle();
        response.originalText = lecture.getOriginalText();
        response.aiSummary = lecture.getAiSummary();
        return response;
    }
}
