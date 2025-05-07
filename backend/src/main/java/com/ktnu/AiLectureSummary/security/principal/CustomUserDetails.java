package com.ktnu.AiLectureSummary.security.principal;

import com.ktnu.AiLectureSummary.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 미사용 시 비움
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail(); // 로그인 ID 기준 (email)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 체크 안함
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 체크 안함
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비번 유효기간 체크 안함
    }

    @Override
    public boolean isEnabled() {
        return member.isActive(); // 활성 계정 여부
    }
}
