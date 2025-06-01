package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.Role;
import com.ktnu.AiLectureSummary.dto.member.*;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 사용자가 입력한 정보로 회원가입 시도합니다.
     * 이메일이 중복인 경우 실패 중복되지 않으면 성공합니다.
     * 성공 이후 DB에 저장합니다.
     *
     * @param request
     * @return
     */
    @Transactional
    public void register(MemberRegisterRequest request) {
        // 필요한 필드 꺼내기
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getUsername();

        // 중복된 이메일 체크
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateLoginIdException("이미 사용 중인 이메일입니다");
        }

        // 멤버 생성
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password)) // encode()는 내부적으로 bcrypt 알고리즘을 사용한 단방향 해싱 함수
                .username(nickname)
                .role(Role.USER)
                .build();

        // DB 저장
        memberRepository.save(member);
    }


    /**
     * 사용자가 입력한 정보로 로그인을 시도합니다.
     * 이메일이 존재하지 않거나 비밀번호가 일치하지 않으면 로그인에 실패합니다.
     * 이메일이 존재하고 비밀번호가 일치하는 경우 JWT를 생성하여 반환합니다.
     *
     * @param request
     * @return
     */
    public MemberLoginResponse login(MemberLoginRequest request) {
        // 필요한 필드 꺼내기
        String email = request.getEmail();
        String password = request.getPassword();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("이메일이 존재하지 않습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token = jwtProvider.createToken(member.getId());

        return new MemberLoginResponse(token);
    }



}
