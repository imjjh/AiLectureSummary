package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.dto.lecture.LectureResponse;
import com.ktnu.AiLectureSummary.security.principal.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
public class LectureApplicationService {
    private final LectureService lectureService;
    private final MemberLectureService memberLectureService;

    public LectureResponse uploadLecture(CustomUserDetails user, MultipartFile file) {
        // 1. 강의 처리
        Lecture lecture = lectureService.processLecture(file);

        // 2. 사용자 조회
        memberLectureService.save(user, lecture);

        // 3. 응답 반환
        return LectureResponse.from(lecture);
    }
}
