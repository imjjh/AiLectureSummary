package com.ktnu.AiLectureSummary.application.service;


import com.ktnu.AiLectureSummary.application.port.out.LectureSummaryFromFilePort;
import com.ktnu.AiLectureSummary.global.config.FastApiProperties;
import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import com.ktnu.AiLectureSummary.global.exception.ExternalApiException;
import com.ktnu.AiLectureSummary.global.exception.FileProcessingException;
import com.ktnu.AiLectureSummary.global.exception.InvalidVideoFileException;
import com.ktnu.AiLectureSummary.repository.LectureRepository;
import com.ktnu.AiLectureSummary.util.MultipartFileResource;
import com.ktnu.AiLectureSummary.util.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureSummaryFromFilePort lectureSummaryFromFilePort;

    /**
     * 업로드된 음성 또는 비디오 파일을 해싱하여 중복 여부를 검사하고, (업로드된 적이 있는 "영상 또는 음성파일"에 대해서만 중복여부 판단 가능)
     * FastAPI 서버에 전송하여 요약 정보를 받은 후, 이를 DB에 저장한다.
     *
     * @param file 사용자가 업로드한 비디오 파일
     * @return lecture 객체
     */
    public Lecture processLecture(MultipartFile file) {
        validateMediaFile(file);

        // MediaHasing & DB에 중복되는 영상이 존재하는지 확인
        String mediaHash = generateMediaHash(file);
        Optional<Lecture> optionalLecture = lectureRepository.findByHash(mediaHash);

        // 이미 존재하는 경우 바로 바로 반환
        if (optionalLecture.isPresent()) {
            return optionalLecture.get();
        }

        // FastAPI 호출
        LectureSummaryResponse registerRequest = lectureSummaryFromFilePort.requestSummary(file);

        // 썸네일 이미지는 DB에 저장되며, 프론트 전달 시 Base64로 인코딩되어 전송됨
        // 썸네일 Base64 디코딩 // 음성 파일의 경우 썸네일 없음
        byte[] thumbnailBytes = ThumbnailUtil.decodeBase64ThumbnailSafe(registerRequest.getThumbnail());

        // DB에 강의 내용 저장
        return lectureRepository.save(Lecture.fromUploadedVideo(registerRequest, mediaHash,thumbnailBytes));

    }

    /**
     * 미디어 파일(비디오/오디오)을 SHA-256 해시 알고리즘으로 해싱하여 고유 문자열을 생성한다.
     *
     * @param file 해싱할 미디어 파일
     * @return Base64로 인코딩된 해시 문자열
     * @throws RuntimeException 파일 읽기 실패 또는 해시 처리 실패 시
     */
    private String generateMediaHash(MultipartFile file) {
        // 비디오 해싱
        try (InputStream inputStream = file.getInputStream()) {
            // SHA-256 해시 알고리즘 계산기 객체 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 8KB씩 파일을 읽기 위한 버퍼 생성
            byte[] buffer = new byte[8192];

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) { // 파일 끝이면 InputStream.read()는 파일 끝이 끝이면 -1 을 반환
                // 읽은 데이터를 해시 계산에 누적
                digest.update(buffer, 0, bytesRead);
            }

            // 전체 파일을 읽은 뒤 최종 해시 계산
            byte[] hashBytes = digest.digest();

            // 이진 데이터-> 문자열 형태의 해시값으로 리턴
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("미디어 해싱 실패", e);
        }
    }

    /**
     * 사용자가 업로드한 파일이 기대하는 파일형식과 동일한지 검사한다.
     *
     * @param file
     */
    private void validateMediaFile(MultipartFile file) {
        // 파일 확장자는 사용자가 쉽게 변경할 수 있으므로 신뢰할 수 없음
        // MIME 타입(Content-Type)을 기반으로 파일 형식을 검증함
        if (file == null || file.isEmpty()) {
            throw new InvalidVideoFileException("파일이 비어 있습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("video/")|| contentType.startsWith("audio/"))) {
            throw new InvalidVideoFileException("지원하지 않는 파일 형식입니다. 비디오 또는 오디오 파일만 업로드 가능합니다.");
        }
    }
}
