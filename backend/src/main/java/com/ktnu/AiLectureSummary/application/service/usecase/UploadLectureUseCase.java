package com.ktnu.AiLectureSummary.application.service.usecase;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureUploadResponse;
import com.ktnu.AiLectureSummary.global.security.CustomUserDetails;
import com.ktnu.AiLectureSummary.application.service.LectureService;
import com.ktnu.AiLectureSummary.application.service.MemberLectureService;
import com.ktnu.AiLectureSummary.application.service.YoutubeLectureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * 사용자의 강의 업로드 요청을 처리하는 애플리케이션 서비스
 * <p>
 * 파일을 받아 Lecture 도메인으로 변환하고 저장하며,
 * 해당 사용자가 업로드한 강의로 기록을 남깁니다.
 * <p>
 * 내부적으로 LectureService와 MemberLectureService를 조합하여
 * 유스케이스 단위 로직을 처리합니다.
 *
 */
@RequiredArgsConstructor
@Service
@Transactional
public class UploadLectureUseCase {
    private final LectureService lectureService;
    private final MemberLectureService memberLectureService;
    private final YoutubeLectureService youtubeLectureService;

    /**
     * 사용자가 강의 파일을 업로드했을 때 처리하는 유스케이스
     * 파일을 분석하여 Lecture 도메인을 생성하고 저장한 뒤,
     * 해당 사용자와의 연관 관계를 저장합니다.
     *
     * @param user 현재 로그인한 사용자 정보
     * @param file 업로드된 강의 파일
     * @return 저장된 강의에 대한 응답 DTO
     */
    public LectureUploadResponse uploadLecture(CustomUserDetails user, MultipartFile file) {
        // 1. 강의 처리 (파일 -> 강의 생성 -> 저장)
        Lecture lecture = lectureService.processLecture(file);

        // 2. 사용자와 강의 연결 저장
        memberLectureService.save(user.getId(), lecture);

        // 3. 응답 반환
        return new LectureUploadResponse(lecture.getId());
    }

    /**
     * 사용자가 YouTube 링크만 업로드했을 때 처리하는 유스케이스
     * 링크를 분석하여 Lecture 도메인을 생성하고 저장한 뒤,
     * 해당 사용자와의 연관 관계를 저장합니다.
     *
     * @param user 현재 로그인한 사용자 정보
     * @param url 업로드된 YouTube 링크
     * @return 저장된 강의에 대한 응답 DTO
     */
    public LectureUploadResponse uploadLectureByYoutubeUrl(CustomUserDetails user, String url) {
        // 1. YouTube 링크로부터 강의 생성 및 저장
        Lecture lecture = youtubeLectureService.processYoutubeLecture(url);

        // 2. 사용자와 강의 연결 저장
        memberLectureService.save(user.getId(), lecture);

        // 3. 응답 반환
        return new LectureUploadResponse(lecture.getId());
    }
}
