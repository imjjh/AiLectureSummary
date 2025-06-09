package com.ktnu.AiLectureSummary.application.port.out;

import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 주어진 파일(Video 또는 Audio)에 대한 요약을 요청합니다.
 */
public interface LectureSummaryFromFilePort {
    LectureSummaryResponse requestSummary(MultipartFile file);
}
