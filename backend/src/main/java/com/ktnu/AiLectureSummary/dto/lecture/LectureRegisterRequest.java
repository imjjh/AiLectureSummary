package com.ktnu.AiLectureSummary.dto.lecture;
// FastAPI -> Spring

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureRegisterRequest {
    private String title;
    private String originalText;
    private String aiSummary;
    private long duration;
//    private String filename;
//    private String timestamp;
}
