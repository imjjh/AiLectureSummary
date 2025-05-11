package com.ktnu.AiLectureSummary.exception;

import java.io.IOException;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable cause) {
        super(message,cause);
    }
    public FileProcessingException(String message) {
        super(message);
    }
}
