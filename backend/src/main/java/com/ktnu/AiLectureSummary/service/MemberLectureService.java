package com.ktnu.AiLectureSummary.service;

import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.exception.PdfGenerateFailException;
import com.ktnu.AiLectureSummary.dto.memberLecture.MemberLectureListResponse;
import com.ktnu.AiLectureSummary.exception.MemberNotFoundException;
import com.ktnu.AiLectureSummary.repository.LectureRepository;
import com.ktnu.AiLectureSummary.repository.MemberRepository;


import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.dto.memberLecture.LectureDetailResponse;
import com.ktnu.AiLectureSummary.exception.LectureNotFoundException;
import com.ktnu.AiLectureSummary.repository.MemberLectureRepository;
import com.ktnu.AiLectureSummary.security.CustomUserDetails;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberLectureService {
    private final MemberLectureRepository memberLectureRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    /**
     * 사용자와 강의 간의 소유 관계를 저장합니다.
     * 이미 해당 관계가 존재하는 경우 중복 저장을 방지합니다.
     */
    @Transactional
    public void save(Long memberId, Lecture lecture) {

        boolean alreadyExists = memberLectureRepository.existsByMember_IdAndLecture(memberId, lecture);
        if (alreadyExists) return;

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 사용자를 찾을 수 없습니다."));

        MemberLecture memberLecture = MemberLecture.builder()
                .member(member)
                .lecture(lecture)
                .customTitle(lecture.getTitleByAi()) // 초기 값은 ai가 생성한 것으로 저장됩니다. 이후 사용자가 변경 가능
                .build();

        memberLectureRepository.save(memberLecture);

    }

    /**
     * 사용자가 등록한 모든 강의 목록을 조회합니다.
     * 내부적으로 MemberLecture를 통해 강의 리스트를 추출하고
     * MemberLectureListResponse를 반환합니다.
     *
     * @param user 현재 로그인한 사용자 정보
     * @return MemberLectureListResponse ((강의 ID,  personTitle), totalDuration)
     */
    public MemberLectureListResponse getUserLectureList(CustomUserDetails user) {

        List<MemberLecture> memberLectures = memberLectureRepository.findAllByMember_Id(user.getId());
        // TODO: N+1
        Long totalDuration = calculateTotalDuration(memberLectures); // 하나도 없는 경우 0?
        return MemberLectureListResponse.from(memberLectures, totalDuration);
    }

    /**
     * 사용자가 등록한 모든 강의 시간의 총 합
     *
     * @param memberLectures
     * @return Long
     */
    private static long calculateTotalDuration(List<MemberLecture> memberLectures) {

        return memberLectures.stream()
                .map(memberLecture -> memberLecture.getLecture().getDuration())
                .mapToLong(Long::longValue)
                .sum(); // sum의 기본 리턴값은 0, list가 비어있어도 0을 반환
    }

    /**
     * 강의 상세 요약 정보를 가져옵니다.
     * member.id, lectureId로  사용자가 등록한 강의가 맞나 확인 하고 강의 상세 요약 정보를 반환합니다.
     *
     * @param user      현재 로그인한 사용자 정보
     * @param lectureId
     * @return
     */
    public LectureDetailResponse getLectureDetail(CustomUserDetails user, long lectureId) {

        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));

        return LectureDetailResponse.from(memberLecture);

    }

    /**
     * 강의에 대한 개인 메모를 저장합니다. 기존 메모가 있으면 덮어씁니다.
     * member.id, lectureId로 사용자가 등록한 강의가 맞는지 확인 후 메모를 저장합니다.
     *
     * @param user      현재 로그인한 사용자 정보
     * @param lectureId 강의 ID
     * @param note      사용자가 입력한 메모 내용
     * @return 수정된 강의 상세 정보
     */
    @Transactional
    public LectureDetailResponse saveMemo(CustomUserDetails user, Long lectureId, String note) {
        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));
        memberLecture.setMemo(note);
        return LectureDetailResponse.from(memberLecture);
    }

    /**
     * 메모를 삭제합니다.
     *
     * @param user
     * @param lectureId
     * @return 메모가 삭제된 강의 상세 정보
     */
    @Transactional
    public LectureDetailResponse deleteMemo(CustomUserDetails user, Long lectureId) {
        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));

        memberLecture.setMemo(null);

        return LectureDetailResponse.from(memberLecture);
    }


    /**
     * 제목을 수정합니다(update), 초기값은 ai가 자동생성으로 클라이언트 요청없이 서버에서 처리하기에 (create)는 없습니다.
     *
     * @param user      현재 로그인한 사용자 정보
     * @param lectureId 강의 Id
     * @param newTitle  새로운 제목
     * @return 수정된 강의 상세 정보
     */
    @Transactional
    public LectureDetailResponse updateCustomTitle(CustomUserDetails user, Long lectureId, String newTitle) {
        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다."));
        memberLecture.setCustomTitle(newTitle);

        return LectureDetailResponse.from(memberLecture);
    }


    /**
     * 사용자의 강의 등록 정보를 삭제하고,
     * 더 이상 아무도 해당 강의를 참조하지 않으면 Lecture 엔티티도 함께 삭제합니다.
     * @param user
     * @param lectureId
     */
    @Transactional
    public void deleteLecture(CustomUserDetails user, Long lectureId) {
        MemberLecture memberLecture = memberLectureRepository.findByMember_IdAndLecture_Id(user.getId(), lectureId)
                .orElseThrow(() -> new LectureNotFoundException("해당 강의를 찾을 수 없습니다"));
        deleteMemberLectureAndCleanupLectureIfOrphan(memberLecture);
    }


    /**
     * 사용자의 모든 강의 등록 정보를 삭제하고,
     * 더 이상 아무도 해당 강의를 참조하지 않으면 Lecture 엔티티도 함께 삭제합니다.
     * @param memberId
     */
    @Transactional
    public void deleteLecturesByMemberId(Long memberId) {
        // 사용자의 모든 강의 조회
        List<MemberLecture> memberLectures = memberLectureRepository.findAllByMember_Id(memberId);

        for (MemberLecture memberLecture: memberLectures ) {
            deleteMemberLectureAndCleanupLectureIfOrphan(memberLecture);
        }
    }

    /**
     * 사용자와 강의의 연관관계를 제거하고,
     * 더 이상 아무도 해당 강의를 참조하지 않으면 Lecture 엔티티도 함께 삭제합니다.
     * @param memberLecture
     */
    @Transactional
    public void deleteMemberLectureAndCleanupLectureIfOrphan(MemberLecture memberLecture) {
        // 강의 정보 // TODO: N+1
        Lecture lecture = memberLecture.getLecture();

        // 강의와 사용자의 연관 관계 제거
        memberLectureRepository.delete(memberLecture);

        // 더 이상 어떤 회원에게도 해당 강의가 등록되어 있지 않으면 강의 자체를 삭제합니다.
        if (!memberLectureRepository.existsByLecture(lecture)) {
            lectureRepository.delete(lecture);
        }
    }
}
