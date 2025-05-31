package com.ktnu.AiLectureSummary.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberEditRequest {

    @Schema(description = "사용자 비밀번호 (선택 사항)", example = "testpassword123!")
    @Pattern(regexp = "^(?=\\S+$).{8,20}$", message = "비밀번호는 공백 없이 8자 이상 20자 이하여야 합니다.")
    private String password;

    @Pattern(regexp = "^(?!.*\\s{2,}).{1,30}$", message = "사용자 이름에는 연속된 공백을 포함할 수 없으며, 최대 30자까지 가능합니다.")
    @Schema(description = "사용자 이름 (선택 사항)", example = "testUser1")
    private String username;
}
