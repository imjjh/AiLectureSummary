package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.MemberLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberLectureRepository extends JpaRepository<MemberLecture, Long> {
    Optional<MemberLecture> findByMember_IdAndLecture_Hash(long memberId, String hash); // member.id와 lecture.hash 값으로 이미 등록된 강의인지 확인

    // 특정 회원이 등록한 모든 강의 목록 조회
    List<MemberLecture> findAllByMember_Id(Long memberId);


}
