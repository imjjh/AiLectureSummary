package com.ktnu.AiLectureSummary.service;


import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.dto.lecture.LectureListItemResponse;
import com.ktnu.AiLectureSummary.repository.MemberLectureRepository;
import com.ktnu.AiLectureSummary.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberLectureService {
    private final MemberLectureRepository memberLectureRepository;

    /**
     * 사용자와 강의 간의 소유 관계를 저장합니다.
     * 이미 해당 관계가 존재하는 경우 중복 저장을 방지합니다.
     *
     * @param user 현재 로그인한 사용자 정보     * @param lecture
     */
    public void save(CustomUserDetails user, Lecture lecture) {
        if (!memberLectureRepository.existsByMemberAndLecture(user.getMember(), lecture)) {
            memberLectureRepository.save(
                    MemberLecture.builder()
                            .member(user.getMember())
                            .lecture(lecture)
                            .build()
            );
        }

    }

    /**
     * 사용자가 등록한 모든 강의 목록을 조회합니다.
     * 내부적으로 MemberLecture를 통해 강의 리스트를 추출하고
     * LectureListItemResponse DTO 형태로 응답할 수 있도록 변환합니다.
     *
     * @param user 현재 로그인한 사용자 정보
     * @return LectureListItemResponse 리스트 (강의 ID + 제목)
     */
    public List<LectureListItemResponse> getUserLectureList(CustomUserDetails user) {

        List<MemberLecture> memberLectures = memberLectureRepository.findAllByMember_Id(user.getId());

        List<Lecture> lectures = memberLectures.stream()
                .map(MemberLecture::getLecture) // MemberLecture -> Lecture
                .toList();

        return LectureListItemResponse.fromList(lectures);
    }
}
