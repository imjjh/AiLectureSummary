package com.ktnu.AiLectureSummary.application.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.application.dto.member.request.MemberResetPasswordRequest;
import com.ktnu.AiLectureSummary.application.dto.member.request.MemberVerifyRequest;
import com.ktnu.AiLectureSummary.application.dto.member.response.MemberPasswordResetTokenResponse;
import com.ktnu.AiLectureSummary.global.exception.AccountInactiveException;
import com.ktnu.AiLectureSummary.global.exception.InvalidTokenException;
import com.ktnu.AiLectureSummary.global.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;





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

        // 탈퇴한 회원인지 검사
        if (!member.isActive()) {
            throw new AccountInactiveException("탈퇴한 회원입니다.");
        }

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
