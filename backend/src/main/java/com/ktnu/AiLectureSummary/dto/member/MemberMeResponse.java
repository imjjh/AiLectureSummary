package com.ktnu.AiLectureSummary.dto.member;


import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberMeResponse {
    private Long id;
    private String email;
    private String username;

    public static MemberMeResponse from(CustomUserDetails user) {
        return MemberMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getNickname()) // 화면에 표시될 이름
                .build();
    }
}