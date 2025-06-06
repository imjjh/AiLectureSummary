package com.ktnu.AiLectureSummary.application;

import com.ktnu.AiLectureSummary.service.MemberLectureService;
import com.ktnu.AiLectureSummary.service.MemberProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 사용자의 탈퇴 요청을 처리하는 애플리케이션 서비스
 * <p>
 * 사용자와 강의들의 연관 관계를 모두 제거합니다.
 * 사용자의 계정을 비활성화합니다.
 * <p>
 * 내부적으로 MemberLectureService MemberProfileService 조합하여
 * 유스케이스 단위 로직을 처리합니다.
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemberDeleteApplicationService {

    private final MemberLectureService memberLectureService;
    private final MemberProfileService memberProfileService;


    public void deleteMember(Long memberId) {
        // 강의 연관관계 제거
        memberLectureService.deleteLecturesByMemberId(memberId);

        // 회원 비활성화 처리
        memberProfileService.deactivate(memberId);

    }
}
