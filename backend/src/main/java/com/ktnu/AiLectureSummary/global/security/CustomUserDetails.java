package com.ktnu.AiLectureSummary.global.security;


import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Member를 Security에 맞게 감싸는 어댑터 역할을 하는 클래스
 */
@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id; // JWT의 sub 값을 매핑하거나, 인증된 유저가 본인인지 확인할 때 사용
    private final String email; // username 역할 (로그인 식별자, 고유하게 사용하는 특징)
    private final String password; // 로그인 시 입력된 비번과 비교용
    private final Role role; // 접근 제어, 메뉴 노출, 관리자 여부 등에 사용
    private final String nickname; // 실제 username;

    public CustomUserDetails(Member member){
        this.id=member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.nickname = member.getUsername();
        this.role = member.getRole();  // USER, ADMIN
    }

    /**
     * 사용자에게 어게 어떤 권한이 있는지 판단하는 메서드 (USER, ADMIN)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * getUsername()은 “로그인 식별자”를 반환하는 메서드이다.
     * 대부분의 시스템에서 이메일은 중복 없이 고유하게 사용하므로 email을 반환
     * @return email
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
