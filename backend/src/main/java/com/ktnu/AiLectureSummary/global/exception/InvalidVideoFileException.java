package com.ktnu.AiLectureSummary.global.exception;

public class InvalidVideoFileException extends RuntimeException {
    public InvalidVideoFileException(String message) {
        super(message);
    }
    public InvalidVideoFileException(String message,Throwable cause) {
        super(message,cause);
    }

}
