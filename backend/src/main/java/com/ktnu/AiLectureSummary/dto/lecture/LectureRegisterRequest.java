package com.ktnu.AiLectureSummary.dto.lecture;
// FastAPI -> Spring

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class LectureRegisterRequest {
    private String title;
    private String originalText;
    private String aiSummary;
    private long duration;
//    private String filename;
//    private String timestamp;
}
