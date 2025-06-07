package com.ktnu.AiLectureSummary.application.dto.member.response;

import com.ktnu.AiLectureSummary.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private String email;
    private String username;

    public MemberResponse(Member member){
        this.email = member.getEmail();
        this.username = member.getUsername();
    }

    public  static  MemberResponse from(Member member){
        return new MemberResponse(member);
    }
}
