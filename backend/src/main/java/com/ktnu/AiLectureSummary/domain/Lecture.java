package com.ktnu.AiLectureSummary.domain;

import com.ktnu.AiLectureSummary.dto.lecture.LectureRegisterRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *   강의 정보를 나타내는 엔티티 클래스
 *   공통된 정보만 작성,
 *   특정 사용자와 강의 간의 관계는 MemberLecture에 작성
 */

@Entity
@Getter
@Setter
public class Lecture {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titleByAi;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(nullable = false, columnDefinition = "TEXT")
    private  String aiSummary;

    @Column(nullable = false,unique = true)
    private String hash; // 영상 내용 기반 해시 // 중복 저장 방지


    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLecture> memberLectures = new ArrayList<>();

    public static Lecture from(LectureRegisterRequest request, String hash) {
        Lecture lecture = new Lecture();
        lecture.setTitleByAi(request.getTitle()); // Ai 초기값 설정
        lecture.setHash(hash);
        lecture.setAiSummary(request.getAiSummary());
        lecture.setOriginalText(request.getOriginalText());
        lecture.setDuration(request.getDuration());
        return lecture;
    }
}
