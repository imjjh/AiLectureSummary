package com.ktnu.AiLectureSummary.dto.lecture.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 강의 업로드 성공 시 클라이언트에게 반환되는 응답 DTO
 * 업로드된 강의의 고유 ID만 포함함
 */
@Getter
@AllArgsConstructor
public class LectureUploadResponse {
    private long id;
}
