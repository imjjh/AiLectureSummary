package com.ktnu.AiLectureSummary.Member.interfaces;


import com.ktnu.AiLectureSummary.Member.application.MemberService;
import com.ktnu.AiLectureSummary.Member.domain.Member;
import com.ktnu.AiLectureSummary.Member.dto.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.Member.dto.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입 요청 처리
     * @param request 요청 바디로 전달된 회원 정보
     * @return 생성된 회원 정보와 201 Created 상태 코드
     */
    @PostMapping("/register")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request){
        Member saved = memberService.register(request);
        MemberResponse response = new MemberResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getUsername()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
