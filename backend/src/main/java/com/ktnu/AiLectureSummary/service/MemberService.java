package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.config.jwt.JwtTokenProvider;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.dto.member.LoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberResponse;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor // final 필드만 포함한 생성자 자동 생성
// 스프링이 생성자 주입 방식으로 의존성을 주입해줌.

public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public Member register(MemberRegisterRequest request) {
        // 중복체크
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateLoginIdException("이미 존재하는 로그인 ID 입니다.");
        }
        // DTO → Entity 변환
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setUsername(request.getUsername());
        member.setPassword(encoder.encode(request.getPassword())); // 해싱 저장

        return memberRepository.save(member);
    }

    // 로그인 (아이디 + 비밀번호 확인 + 토큰 생성)
    public LoginResponse login(MemberLoginRequest request){
        // 이메일이 존재 여부 확인
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new MemberNotFoundException("존재하지 않는 이메일입니다."));
        // 비밀 번호 확인 (해싱된 비밀번호와 입력값 비교)
        if (!encoder.matches(request.getPassword(), member.getPassword()))
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(member.getEmail());

        // 토큰 + 유저정보 DTO로 감싸서 반환
        return new LoginResponse(new MemberResponse(member), token);
    }

    // 로그아웃 (토큰 제거) -> Controller 에서 바로 처리


}