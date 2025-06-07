package com.ktnu.AiLectureSummary.application.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.application.dto.member.request.MemberEditRequest;
import com.ktnu.AiLectureSummary.application.dto.member.response.MemberEditResponse;
import com.ktnu.AiLectureSummary.global.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.global.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.global.exception.NoProfileChangesException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.global.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.global.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 사용자 정보를 수정합니다. (수정 가능한 정보: 이름, 비밀번호)
     *
     * @param user    현재 로그인한 사용자 정보
     * @param request 수정할 정보(이름,비밀번호)를 담은 DTO
     * @return 수정된 사용자 정보
     */
    @Transactional
    public MemberEditResponse editProfile(CustomUserDetails user, MemberEditRequest request) {
        // 필요한 필드 꺼내기
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String newUsername = request.getUsername();

        // 로그인한 사용자의 정보 찾기
        Member member = memberRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("이메일이 존재하지 않습니다."));

        // 사용자가 입력한 비밀번호가 실제 저장된 비밀번호와 일치하는지 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        boolean usernameUnchanged = newUsername == null || newUsername.equals(member.getUsername());
        boolean passwordUnchanged = newPassword == null || passwordEncoder.matches(newPassword, member.getPassword());


        // 수정된 정보가 없는 경우
        if (usernameUnchanged && passwordUnchanged) {
            throw new NoProfileChangesException("변경된 정보가 없습니다.");
        }

        // 새로운 정보로 업데이트가 필요한 경우
        String token = null;
        if (!usernameUnchanged) {
            member.setUsername(newUsername);
        }
        if (!passwordUnchanged) {
            String encoded = passwordEncoder.encode(newPassword);
            member.changePassword(encoded);
            token = jwtProvider.generateAccessToken(member.getId());
        }

        return MemberEditResponse.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .token(token)
                .build();
    }

    public void deactivate(Long id) {
        // 로그인한 사용자의 정보 찾기
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("존재 하지 않는 회원입니다."));
        // 회원 탈퇴
        member.deactivate();
    }
}
