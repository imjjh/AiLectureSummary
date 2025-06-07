package com.ktnu.AiLectureSummary.global.exception;

public class NoProfileChangesException extends RuntimeException {
    public NoProfileChangesException(String message) {
        super(message);
    }
    public NoProfileChangesException(String message, Throwable cause) {
        super(message, cause);
    }

}
