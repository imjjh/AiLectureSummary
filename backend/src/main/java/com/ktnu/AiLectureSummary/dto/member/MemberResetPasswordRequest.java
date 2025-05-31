package com.ktnu.AiLectureSummary.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResetPasswordRequest {

    @NotBlank
    @Pattern(regexp = "^(?=\\S+$).{8,20}$", message = "비밀번호는 공백 없이 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "새로운 비밀번호", example = "password123!")
    private String newPassword;

}
