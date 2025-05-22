package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.dto.lecture.LectureResponse;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional

/**
 * 사용자의 강의 업로드 요청을 처리하는 애플리케이션 서비스
 *
 * 파일을 받아 Lecture 도메인으로 변환하고 저장하며,
 * 해당 사용자가 업로드한 강의로 기록을 남깁니다.
 *
 * 내부적으로 LectureService와 MemberLectureService를 조합하여
 * 하나의 유스케이스 단위 로직을 처리합니다.
 */
public class LectureApplicationService {
    private final LectureService lectureService;
    private final MemberLectureService memberLectureService;

    public LectureResponse uploadLecture(CustomUserDetails user, MultipartFile file) {
        // 1. 강의 처리 (파일 -> 강의 생성 -> 저장)
        Lecture lecture = lectureService.processLecture(file);

        // 2. 사용자와 강의 연결 저장
        memberLectureService.save(user.getId(), lecture);

        // 3. 응답 반환
        return LectureResponse.from(lecture);
    }
}
