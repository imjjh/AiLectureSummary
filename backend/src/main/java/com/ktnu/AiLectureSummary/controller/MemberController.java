package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.LoginResponse;
import com.ktnu.AiLectureSummary.dto.MemberLoginRequest;
import com.ktnu.AiLectureSummary.service.MemberService;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.dto.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.dto.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
     * @return 생성된 회원 정보와 201 CREATED 상태 코드
     */
    @Operation(summary = "회원가입", description="회원 정보를 받아 회원을 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request){
        Member saved = memberService.register(request);
        MemberResponse response = new MemberResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 요청 처리
     * @param request 요청 바디로 전달된 로그인 정보
     * @return 생성된 회원 정보와 200 ok 상태 코드
     */
    @Operation(summary = "로그인", description = "회원 정보를 받아 로그인 시도")
    @PostMapping("/login") // 경로로 POST 요청을 받음
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody MemberLoginRequest request){ // json -> DTO로 받음, 검증
        // 실제 로그인 로직 수행: ID/PW 확인 -> 토큰 발급
        LoginResponse response = memberService.login(request);

        // JWT 토큰을 HttpOnly 쿠키에 저장
        // 쿠키에 HttpOnly 속성을 설정하면 클라이언트(브라우저) 측의 javascript에서 이 쿠키에 접근 불가 -> XSS 공격 방어
        ResponseCookie cookie = ResponseCookie.from("token",response.getToken())
                .httpOnly(true) // 자바스크립트에서 접근 불가
                .secure(false) // https 적용시 true
                .path("/") // 모든 경로에서 쿠키 사용 가능
                .maxAge(3600) // 1시간 유지
                .build();  // 쿠키 객체 최종 생성

            return  ResponseEntity.ok() //http 상태 코드 200
                    .header("Set-Cookie", cookie.toString()) // JWT 쿠키를 클라이언트에 저장 지시
                    .body(response); // 사용자 정보 + 토큰 응답 바디 전달
    }

}
