package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.dto.lecture.LectureResponse;
import com.ktnu.AiLectureSummary.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture")
public class LectureController {
    private final LectureService lectureService;


    @PostMapping("/upload")
    @Operation(summary = "영상 업로드", description = "업로드 된 영상을 Ai를 사용해 요약합니다.")
    public ResponseEntity<LectureResponse> uploadLecture(@RequestPart("file") MultipartFile file) {

        // 파일 확장자는 사용자가 쉽게 변경할 수 있으므로 신뢰할 수 없음
        // MIME 타입(Content-Type)을 기반으로 파일 형식을 검증함
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. 비디오 파일만 업로드 가능합니다.");
        }

        LectureResponse response = lectureService.processVideoUpload(file);
        return ResponseEntity.ok(response);
    }


}
