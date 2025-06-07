package com.ktnu.AiLectureSummary.application.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 재설정 전 본인 인증 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberVerifyRequest {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Schema(description = "사용자 이름", example = "testUser")
    private String username;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;
}
