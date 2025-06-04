package com.ktnu.AiLectureSummary.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;


public class CookieUtil {

    /**
     * 요청에서 특정 이름의 쿠키 값을 찾아 반환합니다.
     *
     * @param request
     * @param name 찾고자 하는 쿠키 이름
     * @return 해당 이름의 쿠키 값 (Optional로 감싸서 반환)
     */
    public static Optional<String> getCookieValue(HttpServletRequest request, String name) {
        // 쿠키가 없는 경우 Option.empty() 반환
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        // 요청의 쿠키 중 name과 일치하는 쿠키를 찾아 해당 값을 반환
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst();
    }

}
