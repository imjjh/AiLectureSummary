package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.dto.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드만 포함한 생성자 자동 생성
// 스프링이 생성자 주입 방식으로 의존성을 주입해줌.

public class MemberService {
    private final MemberRepository memberRepository;

    public Member register(MemberRegisterRequest request) {
        // 중복체크
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateLoginIdException("이미 존재하는 로그인 ID 입니다.");
        }
        // DTO → Entity 변환
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());

        return memberRepository.save(member);
    }

}