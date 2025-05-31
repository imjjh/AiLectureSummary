package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.Role;
import com.ktnu.AiLectureSummary.dto.member.*;
import com.ktnu.AiLectureSummary.exception.*;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate stringRedisTemplate;

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

        // 회원가입 완료 시 반환값 없음=
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
        String newPassword = request.getPassword();
        String newUsername = request.getUsername();

        // 로그인한 사용자의 정보 찾기
        Member member = memberRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("이메일이 존재하지 않습니다."));

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
            token = jwtProvider.createToken(member.getId());
        }

        return MemberEditResponse.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .token(token)
                .build();
    }

    /**
     * 비밀번호 찾기: 비밀번호 수정을 위한 임시 토큰 발급(15분 유효)
     *
     * @param request
     * @return
     */
    public MemberPasswordResetTokenResponse verify(MemberVerifyRequest request) {
        String name = request.getUsername();
        String email = request.getEmail();

        Member member = memberRepository.findByUsernameAndEmail(name, email)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 사용자 정보입니다."));

        // 이름과 이메일이 일치하는 사용자가 존재함을 확인했으므로, 임시 토큰을 발급 (실제 서비스의 경우 이메일로 토큰 전송)
        String token = UUID.randomUUID().toString(); // 유니버설 고유 식별자 생성 // TODO 예측이 가능한 구조 SecureRandom로 리팩터링
        // redis에 토큰 저장 // key: "reset: token..." value: email, TTL: 15min // TODO 이메일 대신 ID가 더 안전 (정보 노출 위험 적음)
        stringRedisTemplate.opsForValue().set("reset:" + token, email, Duration.ofMinutes(15));

        // 비밀번호 재설정용 임시 토큰 발급 // TODO 이메일 인증 방식으로 구현하여 토큰 노출 개선
        MemberPasswordResetTokenResponse response = new MemberPasswordResetTokenResponse(token);
        return response;
    }

    /**
     * 비밀번호 찾기: 임시 토큰을 가진 사용자가 비밀 번호를 수정합니다. 수정 후 토큰은 삭제됩니다.
     *
     * @param token
     * @param request
     */
    @Transactional
    public void resetPassword(String token, MemberResetPasswordRequest request) {
        // redis에서 이메일 조회
        String key = "reset:" + token;
        String email = stringRedisTemplate.opsForValue().get(key);

        // 토큰 유효성 확인
        if (email == null) {
            throw new InvalidTokenException("유효하지 않거나 만료된 토큰입니다.");
        }

        // 이메일로 사용자 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

        // 새 비밀번호로 변경
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.changePassword(encodedPassword);

        // 토큰 삭제 (1회성 사용)
        stringRedisTemplate.delete(key);
    }
}
