package com.ktnu.AiLectureSummary.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


/**
 * AOP 클래스: 로깅, 감사 등을 위한 클래스
 *
 */
@Component
@Slf4j
@Aspect
public class LoggingAspect {
    /**
     * * -> return 타입 상관 없음
     * com~service.. -> 서비스 패키지와 그 하위 패키지 전부 대상
     * * -> 클래스 내 모든 메서드 대상
     * (..) -> 파라미터 갯수 상관없이 감지
     *
     * @param joinPoint AOP에서 현재 실행되고 있는 메서드에 대한 정보를 담고 있는 객체
     */
    @Before("execution(* com.ktnu.AiLectureSummary.service..*(..)) || execution(* com.ktnu.AiLectureSummary.controller..*(..))")
    public void logBeforeService(JoinPoint joinPoint){
        // 메서드 실행 전 로그 출력 (메서드명 + 인자 목록)
        log.info("{} 호출됨 - args: {}",joinPoint.getSignature(),joinPoint.getArgs());

    }



    // Controller 진입 시 로그
    @Before("execution(* com.ktnu.AiLectureSummary.controller..*(..))")    public void logControllerStart(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info(" [Controller] 요청: {}", method);
    }
}
