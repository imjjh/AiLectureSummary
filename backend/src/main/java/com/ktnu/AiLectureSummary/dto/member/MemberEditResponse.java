package com.ktnu.AiLectureSummary.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor
@Builder
@Getter
public class MemberEditResponse {
    private String username;
    private String email;
    private String token;
}
