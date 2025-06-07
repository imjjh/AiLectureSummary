package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.application.service.PasswordResetService;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

class PasswordResetServiceTest {
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private StringRedisTemplate stringRedisTemplate;
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        stringRedisTemplate = mock(StringRedisTemplate.class);

        passwordResetService = new PasswordResetService(memberRepository, passwordEncoder, stringRedisTemplate);
    }

    @Test
    void verify() {
    }

    @Test
    void resetPassword() {
    }
}