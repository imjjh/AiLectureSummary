package com.ktnu.AiLectureSummary.dto;

import com.ktnu.AiLectureSummary.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
//@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String email;
    private String username;

    public MemberResponse(Member member){
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
    }

}
