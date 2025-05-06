package com.ktnu.AiLectureSummary.dto.member;

import com.ktnu.AiLectureSummary.domain.Member;
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

    public  static  MemberResponse from(Member member){

        return new MemberResponse(member);
    }
}
