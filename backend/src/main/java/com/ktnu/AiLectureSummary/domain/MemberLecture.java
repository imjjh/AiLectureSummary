package com.ktnu.AiLectureSummary.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * MemberLecture 엔티티 클래스
 * - 회원이 특정 강의를 요약한 이력을 나타냄
 * - Member ↔ Lecture 간 N:M 관계를 중간 테이블로 표현
 * <p>
 * MemberLecture는 특정 사용자와 강의 간의 관계이므로,
 * •	“내가 본 강의의 메모”
 * •	“내가 요약 요청한 날짜”
 * •	“내가 직접 수정한 요약”
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// member_id와 lecture_id을 조합해서 동일한 조합이 두번 이상 들어가지 못하게 함
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "lecture_id"}))
public class MemberLecture {

    /**
     * 고유 ID (Primary Key)
     * - 자동 증가 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름을 'member_id'로 지정하여 Member의 PK(id)를 참조
    private Member member;

    // Lecture 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime enrolledAt;

    @Column(nullable = false)
    @Setter(AccessLevel.PROTECTED) // 사용자가 수정 가능
    private String personalTitle;

    @Setter(AccessLevel.PUBLIC)
    @Column(nullable = true, columnDefinition = "TEXT") // 사용자 개별 메모 저장용 (TEXT 타입)
    private String personalNote;

}
