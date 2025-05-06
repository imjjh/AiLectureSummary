package com.ktnu.AiLectureSummary.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterRequest {

    @Schema(description = "사용자 이메일", example = "test@example.com")
    @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    @Email(message = "유효한 이메일 주소여야 합니다.")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "testpassword123!")
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "테스트이름")
    @NotBlank(message = "사용자 이름은 비어 있을 수 없습니다.")
    private String username;
}
