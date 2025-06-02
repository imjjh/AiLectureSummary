package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.ApiResponse;
import com.ktnu.AiLectureSummary.dto.lecture.LectureUploadResponse;
import com.ktnu.AiLectureSummary.dto.lecture.YoutubeLectureRequest;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.service.LectureApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture")
public class LectureController {

    private final LectureApplicationService lectureApplicationService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "업로드 영상 요약 생성", description = "업로드 된 영상을 Ai를 사용해 요약합니다.")
    public ResponseEntity<ApiResponse<LectureUploadResponse>> uploadLecture(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "요약할 영상 파일", required = true)
            @RequestPart("file") MultipartFile file) {
        LectureUploadResponse data = lectureApplicationService.uploadLecture(user, file);
        return ResponseEntity.ok(ApiResponse.success("요약 생성 성공", data));
    }

    @PostMapping("/youtubeSummary")
    @Operation(summary = "유트브 영상 요약 생성", description = "유트브 영상을 자막기반으로 Ai를 사용해 요약합니다.")
    public ResponseEntity<ApiResponse<LectureUploadResponse>> uploadYoutubeLecture(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "요약할 영상 youtubeUrl", required = true)
            @Valid @RequestBody YoutubeLectureRequest request) {
        String url = request.getUrl();
        LectureUploadResponse data = lectureApplicationService.uploadYoutubeLecture(user, url);
        return ResponseEntity.ok(ApiResponse.success("요약 생성 성공", data));
    }
}