package com.ktnu.AiLectureSummary.application.service;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.global.exception.LectureNotFoundException;
import com.ktnu.AiLectureSummary.global.exception.PdfGenerateFailException;
import com.ktnu.AiLectureSummary.repository.MemberLectureRepository;
import com.ktnu.AiLectureSummary.global.security.CustomUserDetails;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class LectureDocumentService {

    private final MemberLectureRepository memberLectureRepository;

    /**
     * 사용자의 강의 요약과 메모를 PDF로 생성합니다.
     *
     * @param user      사용자 정보
     * @param lectureId 강의 ID
     * @return PDF 바이트 배열
     */
    public byte[] generateLecturePdf(CustomUserDetails user, Long lectureId) {
        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        com.lowagie.text.pdf.BaseFont baseFont = loadNanumGothicFont();

        // PDF 문서를 생성하고 내용을 추가합니다.
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            com.lowagie.text.Font font = new com.lowagie.text.Font(baseFont, 12);
            document.open();

            document.add(new Paragraph("강의 제목: " + memberLecture.getCustomTitle(), font));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("AI 요약: " + memberLecture.getLecture().getAiSummary(), font));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("원문: " + memberLecture.getLecture().getOriginalText(), font));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("사용자 메모: " + (memberLecture.getMemo() != null ? memberLecture.getMemo() : "(없음)"), font));

            document.close();

            // PDF 생성 중 오류가 발생한 경우 예외를 처리
        } catch (DocumentException e) {
            throw new PdfGenerateFailException("PDF 생성 중 오류 발생", e);
        }

        return outputStream.toByteArray();

    }

    private com.lowagie.text.pdf.BaseFont loadNanumGothicFont() {
        try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/NanumGothic.ttf")) {
            if (fontStream == null) {
                throw new PdfGenerateFailException("폰트 파일을 찾을 수 없습니다.");
            }
            byte[] fontBytes = fontStream.readAllBytes();
            return com.lowagie.text.pdf.BaseFont.createFont("NanumGothic.ttf", com.lowagie.text.pdf.BaseFont.IDENTITY_H, com.lowagie.text.pdf.BaseFont.EMBEDDED, false, fontBytes, null);
        } catch (IOException | DocumentException e) {
            throw new PdfGenerateFailException("폰트 로딩 중 오류 발생", e);
        }
    }
}
