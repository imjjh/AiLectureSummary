package com.ktnu.AiLectureSummary.repository;

import com.ktnu.AiLectureSummary.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByName(String name);
}
