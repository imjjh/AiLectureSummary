package com.ktnu.AiLectureSummary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스
 * - controller에서 발생하는 예외를 한 곳에서 처리
 * - DuplicateLoginIdException 발생 시 HTTP 409 상태 코드와 메시지를 반환
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 로그인 ID 중복 예외 처리 핸들러
     * @param e 로그인 ID 중복 시 발생한 예외 객체
     * @return 409 Conflict 응답과 예외 메세지
     */
    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<String> handleDuplicateLoginIdException(DuplicateLoginIdException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    /**
     * 로그인 시도 중 ID 없음 예외 처리 핸들러
     * @param e 로그인 시도 중 ID 없음으로 발생한 예외 객체
     * @return 401 (Unauthorized)
     */
    @ExceptionHandler(MemberNotFoundException.class)
    public  ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * 로그인 시도 중 비밀번호 틀림 예외 처리 핸들러
     * @param e 로그인 시도 중 비밀번호 틀림으로 발생한 예외 객체
     * @return 401 (Unauthorized)
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public  ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
