package com.ktnu.AiLectureSummary.exception;

public class DuplicateLoginIdException extends RuntimeException{
    public DuplicateLoginIdException(String message) {
        super(message);
    }
}
