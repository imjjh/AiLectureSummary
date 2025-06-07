package com.ktnu.AiLectureSummary.application.dto.member.request;

import com.ktnu.AiLectureSummary.global.constant.ValidationRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계정 정보 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditRequest {

    @Pattern(regexp = ValidationRegex.USERNAME, message = "사용자 이름에는 연속된 공백을 포함할 수 없으며, 최대 30자까지 가능합니다.")
    @Schema(description = "사용자 이름 (선택 사항)", example = "testUser1", required = false)
    private String username;

    @Schema(description = "기존 비밀번호 (필수 사항)", required = true)
    private String currentPassword; // TODO 기존 비밀번호 검사


    @Schema(description = "사용자 비밀번호 (선택 사항)", example = "testpassword123!", required = false)
    @Pattern(regexp = ValidationRegex.PASSWORD, message = "비밀번호는 공백 없이 8자 이상 20자 이하여야 합니다.")
    private String newPassword;
}
