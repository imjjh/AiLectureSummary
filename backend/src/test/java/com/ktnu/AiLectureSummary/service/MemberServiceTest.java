package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MemberServiceTest {
    private MemberService memberService;
    private MemberRepository memberRepository = mock(MemberRepository.class);

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);

    }
}