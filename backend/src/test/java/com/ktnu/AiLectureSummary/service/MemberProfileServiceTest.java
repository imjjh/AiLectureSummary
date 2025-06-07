package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.application.service.MemberProfileService;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.application.dto.member.request.MemberEditRequest;
import com.ktnu.AiLectureSummary.application.dto.member.response.MemberEditResponse;
import com.ktnu.AiLectureSummary.global.exception.NoProfileChangesException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.global.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.global.security.JwtProvider;
import com.ktnu.AiLectureSummary.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberProfileServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private MemberProfileService memberProfileService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = mock(JwtProvider.class);

        memberProfileService = new MemberProfileService(memberRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void 계정정보수정_성공() {
        // given // 이름 변경 & 다른 비밀번호로 수정 시도
        Member mockMember = TestFixture.mockMember();
        CustomUserDetails user = new CustomUserDetails(mockMember);
        MemberEditRequest request = new MemberEditRequest("newName", "newPassword");

        when(memberRepository.findByEmail(mockMember.getEmail()))
                .thenReturn(Optional.of(mockMember));

        when(passwordEncoder.matches(request.getPassword(), mockMember.getPassword()))
                .thenReturn(false); // 새 비밀번호와 기존 비밀번호가 다름 -> 재설정
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("new_encoded_password");
        when(jwtProvider.generateAccessToken(mockMember.getId()))
                .thenReturn("newToken123"); // 비밀번호가 수정되어 새로운 토큰 발급

        // when
        MemberEditResponse response = assertDoesNotThrow(() ->
                memberProfileService.editProfile(user, request)
        );

        // then // 수정된 이름과 요청한 이름이 일치하는지 검사
        assertEquals(mockMember.getEmail(), response.getEmail()); // 이메일은 변경되지 않음
        assertEquals(response.getUsername(), request.getUsername()); // 이름
        assertEquals("newToken123", response.getToken()); // 토큰
        // 비밀번호는 직접 검증이 어려우므로, 처리 흐름을 통해 간접적으로 수정 여부를 확인함.
    }

    @Test
    void 계정정보수정_실패_NoProfileChange() {
        // given // 이름 변경 없음 & 같은 비밀번호로 수정 시도
        Member mockMember = TestFixture.mockMember();
        CustomUserDetails user = new CustomUserDetails(mockMember);
        MemberEditRequest request = new MemberEditRequest(null, mockMember.getPassword()); // 같은 이름, 같은 비밀번호

        when(memberRepository.findByEmail(mockMember.getEmail()))
                .thenReturn(Optional.of(mockMember));

        when(passwordEncoder.matches(request.getPassword(), mockMember.getPassword()))
                .thenReturn(true); // 새 비밀번호와 기존 비밀번호가 같음

        // when & then
        assertThrows(NoProfileChangesException.class, () -> {
            memberProfileService.editProfile(user, request);
        });
    }
}