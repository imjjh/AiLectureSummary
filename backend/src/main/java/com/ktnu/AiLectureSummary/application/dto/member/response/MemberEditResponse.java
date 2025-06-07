package com.ktnu.AiLectureSummary.application.dto.member.response;

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
