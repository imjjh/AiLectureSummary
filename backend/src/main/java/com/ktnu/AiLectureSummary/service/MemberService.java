package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.Role;
import com.ktnu.AiLectureSummary.dto.member.MemberEditResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberEditRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    /**
     * 사용자가 입력한 정보로 회원가입 시도합니다.
     * 이메일이 중복인 경우 실패 중복되지 않으면 성공합니다.
     * 성공 이후 DB에 저장합니다.
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

        // 회원가입 완료 시 반환값 없음
    }


    /**
     * 사용자가 입력한 정보로 로그인을 시도합니다.
     * 이메일이 존재하지 않거나 비밀번호가 일치하지 않으면 로그인에 실패합니다.
     * 이메일이 존재하고 비밀번호가 일치하는 경우 JWT를 생성하여 반환합니다.
     * @param request
     * @return
     */
    public MemberLoginResponse login(MemberLoginRequest request){
        // 필요한 필드 꺼내기
        String email = request.getEmail();
        String password = request.getPassword();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("이메일이 존재하지 않습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token=jwtProvider.createToken(member.getId());

        return new MemberLoginResponse(token);
    }

    /**
     * 사용자 정보를 수정합니다. (수정 가능한 정보: 이름, 비밀번호)
     *
     * @param user 현재 로그인한 사용자 정보
     * @param request 수정할 정보(이름,비밀번호)를 담은 DTO
     * @return 수정된 사용자 정보
     */
    @Transactional
    public MemberEditResponse editProfile(CustomUserDetails user, MemberEditRequest request){
        // 필요한 필드 꺼내기
        String newPassword=request.getPassword();
        String newUsername = request.getUsername();

        // 로그인한 사용자의 정보 찾기
        Member member = memberRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("이메일이 존재하지 않습니다."));

        // null 또는 현재 정보와 다를경우만 정보를 수정
        String token=null;
        if (newUsername != null && !newUsername.equals(member.getUsername())) {
            member.setUsername(newUsername);
        }
        if (newPassword != null && !passwordEncoder.matches(newPassword, member.getPassword())) {
            member.setPassword(passwordEncoder.encode(newPassword));
            token = jwtProvider.createToken(member.getId());
        }

        return MemberEditResponse.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .token(token)
                .build();
    }
}
