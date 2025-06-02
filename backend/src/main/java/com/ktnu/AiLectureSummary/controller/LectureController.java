package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.lecture.LectureUploadResponse;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.LectureApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture")
public class LectureController {

    private final LectureApplicationService lectureApplicationService;


    @PostMapping("/upload")
    @Operation(summary = "영상 업로드", description = "업로드 된 영상을 Ai를 사용해 요약합니다.")
    public ResponseEntity<ApiResponse<LectureUploadResponse>> uploadLecture(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("file") MultipartFile file) {
        LectureUploadResponse data = lectureApplicationService.uploadLecture(user, file);
        return ResponseEntity.ok(ApiResponse.success("요약 생성 성공", data));
    }
}