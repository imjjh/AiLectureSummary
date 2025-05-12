package com.ktnu.AiLectureSummary.dto.member;

import com.ktnu.AiLectureSummary.domain.Member;
import lombok.Getter;

@Getter
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
