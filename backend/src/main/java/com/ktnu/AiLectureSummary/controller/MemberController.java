package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.security.principal.CustomUserDetails;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.dto.member.LoginResponse;
import com.ktnu.AiLectureSummary.dto.member.MemberLoginRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberRegisterRequest;
import com.ktnu.AiLectureSummary.dto.member.MemberResponse;
import com.ktnu.AiLectureSummary.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 로그아웃 요청 처리
     * @param response 클라이언트에 쿠키 제거 명령을 전달하기 위한 응답 객체
     * @return 생성된 회원 정보와 200 ok 상태 코드
     */
    @Operation(summary = "로그아웃", description = "httpOnly 쿠키에 저장된 JWT 토큰을 제거하여 로그아웃합니다.")
    @PostMapping("/logout")
    public  ResponseEntity<Void> logout(HttpServletResponse response){
        // 쿠키 삭제 -> Max-Age=0; // 덮어쓰기 방식으로 삭제
        Cookie cookie = new Cookie("token", null); // 쿠키를 값 없이 (null)로 생성
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    /**
     * @AuthenticationPrincipal은 SecurityContextHolder.getContext().getAuthentication()에서 Principal을 꺼내서 매핑함
     * JwtAuthenticationFilter에서 넣은 UsernamePasswordAuthenticationToken 안의 userDetails가 여기로 들어옴
     * CustomUserDetails는 내부에 Member 객체를 들고 있음 → .getMember()로 실제 사용자 정보 반환 가능
     */
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(MemberResponse.from(userDetails.getMember()));
    }
}
