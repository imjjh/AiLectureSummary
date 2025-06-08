package com.ktnu.AiLectureSummary.dto.member.request;

import com.ktnu.AiLectureSummary.constant.ValidationRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인없이 본인 인증 이후
 * 비밀번호 재설정 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResetPasswordRequest {

    @NotBlank
    @Pattern(regexp = ValidationRegex.PASSWORD, message = "비밀번호는 공백 없이 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "새로운 비밀번호", example = "password123!")
    private String newPassword;

}
