package com.ktnu.AiLectureSummary.constant;

/**
 * 유효성 검사에 사용되는 정규식 상수를 정의한 클래스입니다.
 */
public class ValidationRegex {
    public static final String PASSWORD = "^(?=\\S+$).{8,20}$";
    public static final String USERNAME = "^(?!.*\\s{2,}).{1,30}$";
}
