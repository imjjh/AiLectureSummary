package com.ktnu.AiLectureSummary.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Member 엔티티 클래스
 * - JPA의 @Entity 어노테이션을 사용하여 DB 테이블과 매핑됨
 * - Lombok의 @Getter, @Setter로 접근자 자동 생성
 * - 회원 정보를 저장하는 테이블의 역할을 함
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    /**
     * 회원 고유 ID (Primary Key)
     * - 자동 증가 전략 사용 (MySQL의 AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 로그인 ID
     * - null 불가, 중복 불가
     * - 로그인 시 사용자 식별자로 사용
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 비밀번호 (암호화 저장 예정)
     * - null 불가
     */
    @Column(nullable = false)
    @Setter(AccessLevel.PROTECTED) // 비밀번호 수정 가능
    private String password;

    /**
     * 사용자 이름
     * - null 불가
     */
    @Column(nullable = false)
    private String username;


    /**
     * 활성 상태 여부 (탈퇴한 계정 관리용)
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * DB에 처음 저장될 떄 자동 생성되는 일시
     */
    @CreatedDate
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 직렬화시 포맷지정
    private LocalDateTime createdAt;

    /**
     * 엔티티가 수정될 때마다 자동 갱신되는 일시
     */
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 직렬화시 포맷지정
    private LocalDateTime updatedAt;

    /**
     * 회원이 요약한 강의 정보 리스트 (MemberLecture 중간 테이블을 통해 연결)
     *
     * - @OneToMany -> 1:N 관계 member:memberLecture
     * - mappedby = "member" -> memberLecture 클래스에 있는 Member 필드가 주인?, 여기서는 읽기 전용 관계
     * - cascade = CascadeType.ALL -> Member를 저장/삭제할 때 관련된 MemberLecture도 같이 저장/삭제함
     * - orphanRemoval = true -> MemberLecture에서 제거된 항목은 DB에서 삭제됨 (고아 객체 제거)
     * - List<MemberLecture> -> 다대다 중간 테이블인 MemberLecture를 담는 리스트
     * - new ArrayList<>() -> null 방지를 위해 초기화
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLecture> memberLectureList = new ArrayList<>();

    // 값의 범위를 제한하고 명확하게 표현하기 위해 enum 사용
    // USER, ADMIN 등의 값만 허용하며, 추후 VIP, MANAGER 등으로 확장 가능
    // 불리언보다 확장성과 가독성이 뛰어남
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
