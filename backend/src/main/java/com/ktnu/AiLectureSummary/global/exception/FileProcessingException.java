package com.ktnu.AiLectureSummary.global.exception;

import java.io.IOException;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable cause) {
        super(message,cause);
    }
    public FileProcessingException(String message) {
        super(message);
    }
}
