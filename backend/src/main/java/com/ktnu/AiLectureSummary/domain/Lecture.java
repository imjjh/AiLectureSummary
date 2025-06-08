package com.ktnu.AiLectureSummary.domain;

import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 *   강의 정보를 나타내는 엔티티 클래스
 *   공통된 정보만 작성,
 *   특정 사용자와 강의 간의 관계는 MemberLecture에 작성
 */

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = true,unique = true)
    private String hash; // 영상 내용 기반 해시 // 중복 저장 방지

    @Column(nullable = true,unique = true)
    private String youtubeUrl; // 요약한 영상의 링크 // 중복 저장 방지

    @Lob
    @Column(columnDefinition = "LONGBLOB") // null 가능
    private byte[] thumbnail; // 썸네일 DB에 저장

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLecture> memberLectures = new ArrayList<>();

    public static Lecture fromUploadedVideo(LectureSummaryResponse request, String hash, byte[] thumbnailBytes) {
        return Lecture.builder()
                .titleByAi(request.getTitle())
                .hash(hash)
                .aiSummary(request.getAiSummary())
                .originalText(request.getOriginalText())
                .duration(request.getDuration())
                .thumbnail(thumbnailBytes)
                .build();
    }


    public static Lecture fromYoutubeUrl(LectureSummaryResponse request, String youtubeUrl) {
        return Lecture.builder()
                .titleByAi(request.getTitle())
                .aiSummary(request.getAiSummary())
                .originalText(request.getOriginalText())
                .duration(request.getDuration())
                .youtubeUrl(youtubeUrl)
                .build();
    }


//    public boolean hasSameHash(String otherHash) {
//        return this.hash != null && this.hash.equals(otherHash);
//    }
//
//    public boolean isYouTubeLecture() {
//        return youtubeUrl != null;
//    }
//
//    public boolean isUploadedLecture() {
//        return hash != null && youtubeUrl == null;
//    }
}
