package com.ktnu.AiLectureSummary.security;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Controller에서 직접 호출되지 않아도 Spring Security 내부에서 자동으로 사용되는 클래스
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * Spring Security로 Login을 구현하는 경우, AuthenticationManager.authenticate() 내부에서 사용됨
     * @param email
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new CustomUserDetails(member);
    }

    /**
     * JWT 인증 필터에서 토큰 안의 ID로 유저 인증할 때 사용합니다.
     * @param userId
     * @return UserDetails
     */
    public UserDetails loadUserById(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. ID=" + userId));
        return new CustomUserDetails(member);
    }
}
