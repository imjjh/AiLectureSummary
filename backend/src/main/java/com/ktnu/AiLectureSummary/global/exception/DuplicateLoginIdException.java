package com.ktnu.AiLectureSummary.global.exception;

public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException(String message) {
        super(message);
    }
    public DuplicateLoginIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
