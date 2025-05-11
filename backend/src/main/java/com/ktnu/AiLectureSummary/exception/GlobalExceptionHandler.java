package com.ktnu.AiLectureSummary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
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
    public ResponseEntity<String> handleDuplicateLoginIdException(DuplicateLoginIdException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    /**
     * 로그인 시도 중 ID 없음 예외 처리 핸들러
     *
     * @param e 로그인 시도 중 ID 없음으로 발생한 예외 객체
     * @return 401 (Unauthorized)
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * 로그인 시도 중 비밀번호 틀림 예외 처리 핸들러
     *
     * @param e 로그인 시도 중 비밀번호 틀림으로 발생한 예외 객체
     * @return 401 (Unauthorized)
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * 파일 처리 중 발생한 예외 처리 핸들러
     * @param e 파일 읽기/쓰기 등 내부 처리 중 발생한 예외 객체
     * @return 500 (Interval Server Erorr)
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleFileProcessingException(FileProcessingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    /**
     *  외부 API(FastAPI 등) 호출 중 발생한 예외 처리 핸들러
     * @param e 외부 API 호출 실패로 발생한 예외 객체
     * @return 502 (Bad Gateway)
     */
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiException(ExternalApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
    }

    /**
     * 사용자의 잘못된 파라미터 입력으로 발생한 예외 처리 핸들러
     * @param e 잘못된 파라미터 입력으로 발생한 예외 객체
     * @return 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    /**
     * DB 제약 조건 위반 예외 처리 핸들러
     *
     * @param e SQL 제약 조건 위반으로 발생한 예외 객체 (예: NOT NULL 컬럼에 null 저장 시)
     * @return 400 (Bad Request) 응답과 함께 상세 에러 메시지 반환
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSqlException(SQLIntegrityConstraintViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "DB 제약 조건 위반", "detail", e.getMessage()));
    }}
