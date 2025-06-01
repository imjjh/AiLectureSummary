package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberAuthServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private MemberAuthService memberAuthService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = mock(JwtProvider.class);
        memberAuthService = new MemberAuthService(memberRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void 회원가입_성공() {
        // given
        MemberRegisterRequest request = new MemberRegisterRequest("test@example.com", "password123", "TestUser"); // 회원가입 요청 DTO 생성
        when(memberRepository.existsByEmail("test@example.com"))
                .thenReturn(false); // 이메일 중복 없음으로 설정: existsByEmail("test@example.com") 호출 시 false 반환

        // when
        assertDoesNotThrow(() -> memberAuthService.register(request)); // 예외가 발생할 수 있는 실행 코드 조각을 기대, 람다식으로 넘겨주어야함.

        // then
        verify(memberRepository) // Proxy(mock 객체)를 감시 시작
                .save(any()); // "이 호출이 진짜 있었는지" 검사
    }

    @Test
    void 회원가입_실패_중복된이메일() {
        // given
        MemberRegisterRequest request = new MemberRegisterRequest("test@example.com", "password123", "TestUser"); // 회원가입 요청 DTO 생성
        when(memberRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        // when
        assertThrows(DuplicateLoginIdException.class, () -> memberAuthService.register(request));

        // then
        verify(memberRepository, never())
                .save(any());
    }

    @Test
    void 로그인_성공() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");

        Member mockMember = Member.builder()
                .id(1L) // ID를 명시적으로 설정
                .email("test@example.com")
                .password("encoded_password") // 인코딩된 비밀번호
                .username("TestUser")
                .build();

        String email = request.getEmail();
        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.of(mockMember));

        when(passwordEncoder.matches(request.getPassword(), mockMember.getPassword()))
                .thenReturn(true);

        when(jwtProvider.createToken(mockMember.getId()))
                .thenReturn("mocked-jwt-token");

        // when
        MemberLoginResponse response = memberAuthService.login(request);

        // then
        assertEquals("mocked-jwt-token", response.getAccessToken());
    }

    @Test
    void 로그인_실패_아이디불일치() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");

        String email = request.getEmail();
        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.empty()); // 사용자 없음

        // when
        assertThrows(MemberNotFoundException.class, () -> memberAuthService.login(request));

        // then
        verify(jwtProvider, never())
                .createToken(any());
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");

        Member mockMember = Member.builder()
                .id(1L) // ID를 명시적으로 설정
                .email("test@example.com")
                .password("encoded_password") // 인코딩된 비밀번호
                .username("TestUser")
                .build();

        String email = request.getEmail();
        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.of(mockMember));

        when(passwordEncoder.matches(request.getPassword(), mockMember.getPassword()))
                .thenReturn(false);

        // when
        assertThrows(InvalidPasswordException.class, () -> memberAuthService.login(request));

        // then
        verify(jwtProvider, never())
                .createToken(any());
    }
}