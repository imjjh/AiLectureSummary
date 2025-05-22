package com.ktnu.AiLectureSummary.service;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.repository.MemberRepository;


import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.dto.lecture.LectureDetailResponse;
import com.ktnu.AiLectureSummary.dto.lecture.LectureListItemResponse;
import com.ktnu.AiLectureSummary.exception.LectureNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberLectureRepository;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberLectureService {
    private final MemberLectureRepository memberLectureRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용자와 강의 간의 소유 관계를 저장합니다.
     * 이미 해당 관계가 존재하는 경우 중복 저장을 방지합니다.
     *
     */
    public void save(Long memberId, Lecture lecture) {
        if (!memberLectureRepository.existsByMember_IdAndLecture(memberId, lecture)) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
            memberLectureRepository.save(
                    MemberLecture.builder()
                            .member(member)
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

    /**
     * 강의 상세 요약 정보를 가져옵니다.
     * member.id, lectureId로  사용자가 등록한 강의가 맞나 확인 하고 강의 상세 요약 정보를 반환합니다.
     *
     * @param user 현재 로그인한 사용자 정보
     * @param lectureId
     * @return
     */
    public LectureDetailResponse getLectureDetail(CustomUserDetails user, long lectureId) {

        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));

        return LectureDetailResponse.from(
                memberLecture.getLecture(),
                memberLecture.getPersonalNote()
        );

    }

    /**
     * 강의에 대한 개인 메모를 작성합니다.
     * member.id, lectureId로  사용자가 등록한 강의가 맞나 확인 하고, 개인 메모를 저장합니다.
     *
     * @param user 현재 로그인한 사용자 정보
     * @param lectureId
     * @param note 사용자가 입력한 요약 정보
     * @return
     */
    public LectureDetailResponse writePersonalNote(CustomUserDetails user, Long lectureId, String note) {
        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));

        memberLecture.setPersonalNote(note);

        return LectureDetailResponse.from(
                memberLecture.getLecture(),
                memberLecture.getPersonalNote()
        );
    }
}
