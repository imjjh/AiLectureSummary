package com.ktnu.AiLectureSummary.global.exception;

public class LectureNotFoundException extends RuntimeException {
    public LectureNotFoundException(String message) {
        super(message);
    }

    public LectureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
