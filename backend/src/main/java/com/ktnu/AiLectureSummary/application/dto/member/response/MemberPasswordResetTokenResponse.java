package com.ktnu.AiLectureSummary.application.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberPasswordResetTokenResponse {
    private String token;
}
