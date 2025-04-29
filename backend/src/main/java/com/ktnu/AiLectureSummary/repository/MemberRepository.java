package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional <Member> findById(long id);
    Optional <Member> findByLoginId(String loginId);
    Optional <Member> findByName(String name);
    List<Member> findAll ();
}
