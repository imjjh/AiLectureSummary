package com.ktnu.AiLectureSummary.exception;

public class PdfGenerateFailException extends RuntimeException {
    public PdfGenerateFailException(String message) {
        super(message);
    }
    public PdfGenerateFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
