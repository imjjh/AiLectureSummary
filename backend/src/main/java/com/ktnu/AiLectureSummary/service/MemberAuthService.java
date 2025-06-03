package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.config.JwtProperties;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.Role;
import com.ktnu.AiLectureSummary.dto.member.*;
import com.ktnu.AiLectureSummary.exception.DuplicateLoginIdException;
import com.ktnu.AiLectureSummary.exception.InvalidPasswordException;
import com.ktnu.AiLectureSummary.exception.InvalidTokenException;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.security.JwtProvider;
import com.ktnu.AiLectureSummary.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;

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
        String accessToken = jwtProvider.generateAccessToken(member.getId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getId());

        // redis 저장 (refreshToken)
        stringRedisTemplate.opsForValue()
                .set("refresh:" + refreshToken, String.valueOf(member.getId()), Duration.ofMillis(jwtProperties.getRefreshExpiration()));


        return new MemberLoginResponse(accessToken,refreshToken);
    }


    /**
     * refreshToken을 이용한 AccessToken 재발급
     * 현재는 refreshToken을 만료전까지 재사용하는 방식, 보안 더 강화 필요시 rotate 방식으로 새로운 refresh token으로 재발급 하기도함
     *
     * @param request
     * @return
     */
    public String reissueAccessToken(HttpServletRequest request) {
        // refresh token 꺼내기
        String refreshToken = CookieUtil.getCookieValue(request, "refresh_token")
                .orElseThrow(() -> new InvalidTokenException("Refresh Token이 존재하지 않습니다."));

        // redis에서 유효한 refreshToken인지 검사
        String userId = stringRedisTemplate.opsForValue().get("refresh:" + refreshToken);
        if (userId==null) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
        }

        // 새로운 accesstoken 반환
        return jwtProvider.generateAccessToken(Long.parseLong(userId));
    }


    /**
     *
     * @param request
     */
    public void logout(HttpServletRequest request) {
        // access_token 꺼내기
        String accessToken = CookieUtil.getCookieValue(request, "access_token")
                .orElseThrow(() -> new InvalidTokenException("Access Token이 존재하지 않습니다."));

        // access_token 만료시간 계산
        long expiration = jwtProvider.getExpiration(accessToken);

        // 블랙리스트 등록
        stringRedisTemplate.opsForValue()
                .set("blacklist:" + accessToken, "logout", Duration.ofMillis(expiration));

        // refreshToken도 쿠키에서 꺼내서 redis에서 삭제
        CookieUtil.getCookieValue(request,"refresh_token").ifPresent(refreshToken->{
            stringRedisTemplate.delete("refresh:" + refreshToken);
        });

    }
}
