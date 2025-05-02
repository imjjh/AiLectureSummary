package com.ktnu.AiLectureSummary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterRequest {

    @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    @Email(message = "유효한 이메일 주소여야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 비어 있을 수 없습니다.")
    private String username;
}
