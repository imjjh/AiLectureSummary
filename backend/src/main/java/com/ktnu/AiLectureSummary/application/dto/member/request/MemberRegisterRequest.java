package com.ktnu.AiLectureSummary.application.dto.member.request;

import com.ktnu.AiLectureSummary.global.constant.ValidationRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * 회원가입 요청 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class MemberRegisterRequest {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    @Email(message = "유효한 이메일 주소여야 합니다.")
    private String email;

    @Schema(description = "사용자 이름", example = "testUser")
    @Pattern(regexp = ValidationRegex.USERNAME, message = "이름에는 연속된 공백을 포함할 수 없으며, 최대 30자까지 가능합니다.")
    private String username;

    @Schema(description = "사용자 비밀번호", example = "testpassword123!")
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @Pattern(regexp = ValidationRegex.PASSWORD, message = "비밀번호는 공백 없이 8자 이상 20자 이하여야 합니다.")
    private String password;


}
