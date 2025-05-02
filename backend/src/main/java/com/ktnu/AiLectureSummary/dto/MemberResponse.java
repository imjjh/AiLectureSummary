package com.ktnu.AiLectureSummary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String email;
    private String username;
}
