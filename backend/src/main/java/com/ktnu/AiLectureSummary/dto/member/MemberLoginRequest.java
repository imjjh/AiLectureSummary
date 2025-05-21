package com.ktnu.AiLectureSummary.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
public class MemberLoginRequest {

    @Email
    @Schema(description = "회원 이메일",example = "test@example.com")
    @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    private String email;

    @Schema(description = "회원 비밀번호", example="testpassword123!")
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    private String password;
}
