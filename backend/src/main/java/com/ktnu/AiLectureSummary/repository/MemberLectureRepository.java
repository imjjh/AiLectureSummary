package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberLectureRepository extends JpaRepository<MemberLecture, Long> {
    // 주어진 회원 ID와 강의 ID로 해당 회원이 해당 강의를 등록했는지 확인
    Optional<MemberLecture> findByMember_IdAndLecture_Id(long memberId, long lectureId);

    // 특정 회원이 등록한 모든 강의 목록 조회
    List<MemberLecture> findAllByMember_Id(Long memberId);

    // 해당 강의가 어떤 회원에게라도 등록되어 있는지 확인 (참조 유무 확인)
    boolean existsByLecture(Lecture lecture);

    // 특정 회원이 특정 강의를 등록했는지 여부 확인
    boolean existsByMember_IdAndLecture(Long memberId, Lecture lecture);
}
