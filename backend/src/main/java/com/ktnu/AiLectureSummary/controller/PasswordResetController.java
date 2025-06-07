package com.ktnu.AiLectureSummary.controller;
import com.ktnu.AiLectureSummary.application.dto.ApiResponse;
import com.ktnu.AiLectureSummary.application.dto.member.response.MemberPasswordResetTokenResponse;
import com.ktnu.AiLectureSummary.application.dto.member.request.MemberResetPasswordRequest;
import com.ktnu.AiLectureSummary.application.dto.member.request.MemberVerifyRequest;
import com.ktnu.AiLectureSummary.application.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * 비밀번호 변경을 위해 사용자 이름과 이메일을 입력받아 일치하면 비밀번호를 변경할 수 있는 임시 토큰을 발급합니다.
     * 발급 받은 임시 토큰은 비밀번호 재설정 요청에서 헤더에 붙여서 전달해 주어야 비밀번호 변경이 가능합니다.
     * 학습/테스트용 기능으로 실서비스에선 이메일 인증이 필요함
     * @param request
     * @return
     */
    @PostMapping("/verify")
    @Operation(summary = "비밀번호 찾기 요청(이메일 이름 입력)", description = "사용자 이름과 이메일을 입력받아 일치하면 비밀번호를 변경할 수 있는 임시 토큰을 발급합니다.\n" +
            "        발급 받은 임시 토큰은 비밀번호 재설정 요청에서 헤더에 붙여서 전달해 주어야 비밀번호 변경이 가능합니다.")
    public ResponseEntity<ApiResponse<MemberPasswordResetTokenResponse>> verify(@Valid @RequestBody MemberVerifyRequest request) {        // 실제 이메일 인증 등을 통하지 않아서 위험한 방식
        MemberPasswordResetTokenResponse response = passwordResetService.verify(request);
        return ResponseEntity.ok(ApiResponse.success("사용자의 이름과 이메일 일치, 임시 토큰 발급(15분 유효)", response));
    }

    /**
     * /find-password에서 발급받은 임시토큰을 사용하여 비밀번호를 수정합니다.
     * @param request
     * @return
     */
    // TODO @RateLimiter(name="..") 요청 제한 추가 (무차별 대입 방어)
    @PostMapping("/reset")
    @Operation(summary = "비밀번호 재설정 요청(비밀번호 입력)",
            description = "사용자의 비밀번호를 재설정 합니다. 비밀번호 찾기 요청에서 발급 받은 임시토큰을 커스텀헤더" +
                    "Reset-Token:... 으로 함께 전송합니다.")

    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "비밀번호 재설정을 위한 임시 토큰",required = true) @RequestHeader("Reset-Token") String token,
            @Valid @RequestBody MemberResetPasswordRequest request) {
        passwordResetService.resetPassword(token, request);
        return ResponseEntity.ok(ApiResponse.success("새로운 비밀번호로 수정되었습니다.", null));
    }
}
