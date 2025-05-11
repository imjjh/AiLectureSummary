package com.ktnu.AiLectureSummary.dto.lecture;
// FastAPI -> Spring

import lombok.Getter;

@Getter
public class LectureRegisterRequest {
    private String title;
    private String originalText;
    private String aiSummary;
}
