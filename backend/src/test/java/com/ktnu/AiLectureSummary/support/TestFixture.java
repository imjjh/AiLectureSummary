package com.ktnu.AiLectureSummary.support;

import com.ktnu.AiLectureSummary.domain.Member;

public class TestFixture {

    public static Member mockMember() {
        return Member.builder()
                .id(1L) // ID를 명시적으로 설정
                .email("test@example.com")
                .password("encoded_password") // 인코딩된 비밀번호
                .username("TestUser")
                .build();
    }

//    public static CustomUserDetails mockCustomUserDetails() {
//        return new CustomUserDetails(mockMember());
//    }
}

