package com.ktnu.AiLectureSummary.exception;

import com.ktnu.AiLectureSummary.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                ErrorResponse.of("DUPLICATE_LOGIN_ID", e.getMessage(), HttpStatus.CONFLICT.value(), request.getRequestURI())
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
                ErrorResponse.of("MEMBER_NOT_FOUND", e.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI())
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
                ErrorResponse.of("INVALID_PASSWORD", e.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI())
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
                ErrorResponse.of("FILE_PROCESSING_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI())
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
                ErrorResponse.of("EXTERNAL_API_ERROR", e.getMessage(), HttpStatus.BAD_GATEWAY.value(), request.getRequestURI())
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
                ErrorResponse.of("INVALID_VIDEO", e.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI())
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
                ErrorResponse.of("SQL_CONSTRAINT_VIOLATION", e.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI())
        );
    }

    /**
     * 조회한 강의를 찾을 수 없어 발생한 예외 처리 핸들러
     *
     * @param e 강의를 찾을 수 없어 발생한 예외 객체
     * @return 404 (Not Found) 응답과 함께 상세 에러 메시지 반환
     */
    @ExceptionHandler(LectureNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLectureNotFoundException(LectureNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of("LECTURE_NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND.value(), request.getRequestURI())
        );
    }

    /**
     * 비밀번호 수정을 위해 발급된 토큰이 유효하지 않아 발생한 예외 처리 핸들러
     *
     * @param e 비밀번호 수정을 위해 발급된 토큰이 유효하지 않아 발생한 예외 객체
     * @param request request 요청 객체 (요청 URI 포함)
     * @return 401, 에러 응답
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of("INVALID_TOKEN", e.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI())
        );
    }


    /**
     * 프로필 수정 요청을 받았지만 수정할 정보가 없어 발생한 예외 처리 핸들러
     * @param e 프로필 수정 요청을 받았지만 수정할 정보가 없어 발생한 예외 객체
     * @param request request 요청 객체 (요청 URI 포함)
     * @return 400, 에러 응답
     */
    @ExceptionHandler(NoProfileChangesException.class)
    public ResponseEntity<ErrorResponse> handleNoProfileChangesException(NoProfileChangesException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("NO_PROFILE_CHANGES", e.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI()));
    }


    /**
     * 유효성 검사 @Valid 실패시 발생한 예외 처리 핸들러
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("VALIDATION_ERROR", errorMessage, HttpStatus.BAD_REQUEST.value(), request.getRequestURI()));
    }


    /**
     * 탈퇴한 계정으로 로그인 시도할 때 발생하는 예외 처리 핸들러
     *
     * @param e 탈퇴 계정 접근 시 발생한 예외 객체
     * @param request request 요청 객체 (요청 URI 포함)
     * @return 401 Unauthorized 응답과 상세 에러 메시지
     */
    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountInactiveException(AccountInactiveException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("ACCOUNT_INACTIVE", e.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI()));
    }
}
