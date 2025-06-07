package com.ktnu.AiLectureSummary.controller;

import com.ktnu.AiLectureSummary.global.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.application.service.LectureDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-lectures")
public class LectureDocumentController
{
    private final LectureDocumentService lectureDocumentService;


    @GetMapping("/{lectureId}/pdf")
    @Operation(summary = "내 특정강의 PDF 다운로드", description = "application/pdf 형식으로 반환")
    public ResponseEntity<byte[]> downloadLecturePdf(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "강의 ID", example = "1") @PathVariable Long lectureId
    ) {
        byte[] pdfBytes = lectureDocumentService.generateLecturePdf(user, lectureId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lecture-summary.pdf")
                .body(pdfBytes);  // 직접 byte[] 반환
    }
}
