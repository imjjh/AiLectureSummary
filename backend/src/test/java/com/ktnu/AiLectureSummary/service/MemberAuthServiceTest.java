package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.config.JwtProperties;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.security.JwtProvider;
import com.ktnu.AiLectureSummary.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberAuthServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private StringRedisTemplate stringRedisTemplate;
    private JwtProperties jwtProperties;

    private MemberAuthService memberAuthService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = mock(JwtProvider.class);
        jwtProperties = mock(JwtProperties.class);

        // Redis
        stringRedisTemplate = mock(StringRedisTemplate.class); // Redis 템플릿 자체를 모킹
        ValueOperations<String, String> valueOps = mock(ValueOperations.class); // 내부의 opsForValue()가 반환할 객체도 모킹
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOps); // Redis가 opsForValue() 호출할 때 반환할 mock 지정


        memberAuthService = new MemberAuthService(memberRepository, passwordEncoder, jwtProvider,stringRedisTemplate,jwtProperties);
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

        Member mockMember = TestFixture.mockMember();

        String email = request.getEmail();
        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.of(mockMember));

        when(passwordEncoder.matches(request.getPassword(), mockMember.getPassword()))
                .thenReturn(true);

        when(jwtProvider.generateAccessToken(mockMember.getId()))
                .thenReturn("mocked-jwt-access-token");

        when(jwtProvider.generateRefreshToken(mockMember.getId()))
                .thenReturn("mocked-jwt-refresh-token");

        // when
        MemberLoginResponse response = memberAuthService.login(request);

        // then
        verify(stringRedisTemplate.opsForValue())
                .set("refresh:" + "mocked-jwt-refresh-token", String.valueOf(mockMember.getId()), Duration.ofMillis(jwtProperties.getRefreshExpiration()));

        assertEquals("mocked-jwt-access-token", response.getAccessToken());
        assertEquals("mocked-jwt-refresh-token", response.getRefreshToken());

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
                .generateAccessToken(any());
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        // given
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password123");


        Member mockMember = TestFixture.mockMember();

        String email = request.getEmail();
        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.of(mockMember));

        when(passwordEncoder.matches(request.getPassword(), mockMember.getPassword()))
                .thenReturn(false);

        // when
        assertThrows(InvalidPasswordException.class, () -> memberAuthService.login(request));

        // then
        verify(jwtProvider, never())
                .generateAccessToken(any());
    }
}