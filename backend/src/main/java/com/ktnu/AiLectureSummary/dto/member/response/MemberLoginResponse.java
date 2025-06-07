package com.ktnu.AiLectureSummary.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponse {
    private String accessToken;
    private String refreshToken;

}
