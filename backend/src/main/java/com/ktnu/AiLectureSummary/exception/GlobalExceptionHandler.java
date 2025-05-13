package com.ktnu.AiLectureSummary.exception;

import com.ktnu.AiLectureSummary.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 전역 예외 처리 클래스
 * - controller에서 발생하는 예외를 한 곳에서 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 로그인 ID 중복 예외 처리 핸들러
     *
     * @param e 로그인 ID 중복 시 발생한 예외 객체
     * @return 409 Conflict 응답과 예외 메세지
     */
    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginIdException(DuplicateLoginIdException e,  HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .error("DUPLICATE_LOGIN_ID")
                        .message(e.getMessage())
                        .status(HttpStatus.CONFLICT.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * 로그인 시도 중 ID 없음 예외 처리 핸들러
     *
     * @param e 로그인 시도 중 ID 없음으로 발생한 예외 객체
     * @return 401 (Unauthorized)
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ErrorResponse.builder()
                            .error("MEMBER_NOT_FOUND")
                            .message(e.getMessage())
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .path(request.getRequestURI())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }


    /**
     * 로그인 시도 중 비밀번호 틀림 예외 처리 핸들러
     *
     * @param e 로그인 시도 중 비밀번호 틀림으로 발생한 예외 객체
     * @return 401 (Unauthorized)
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .error("INVALID_PASSWORD")
                        .message(e.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * 파일 처리 중 발생한 예외 처리 핸들러
     *
     * @param e 파일 읽기/쓰기 등 내부 처리 중 발생한 예외 객체
     * @return 500 (Interval Server Erorr)
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleFileProcessingException(FileProcessingException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .error("FILE_PROCESSING_ERROR")
                        .message(e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    /**
     * 외부 API(FastAPI 등) 호출 중 발생한 예외 처리 핸들러
     *
     * @param e 외부 API 호출 실패로 발생한 예외 객체
     * @return 502 (Bad Gateway)
     */
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                ErrorResponse.builder()
                        .error("EXTERNAL_API_ERROR")
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_GATEWAY.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * 사용자의 잘못된 영상 입력으로 발생한 예외 처리 핸들러
     *
     * @param e 잘못된 영상 입력으로 발생한 예외 객체
     * @return 400 (Bad Request)
     */
    @ExceptionHandler(InvalidVideoFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVideoFileException(InvalidVideoFileException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("INVALID_VIDEO")
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    /**
     * DB 제약 조건 위반 예외 처리 핸들러
     *
     * @param e SQL 제약 조건 위반으로 발생한 예외 객체 (예: NOT NULL 컬럼에 null 저장 시)
     * @return 400 (Bad Request) 응답과 함께 상세 에러 메시지 반환
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSqlException(SQLIntegrityConstraintViolationException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .error("SQL_CONSTRAINT_VIOLATION")
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
