package com.ktnu.AiLectureSummary.Member.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Member 엔티티 클래스
 * - JPA의 @Entity 어노테이션을 사용하여 DB 테이블과 매핑됨
 * - Lombok의 @Getter, @Setter로 접근자 자동 생성
 * - 회원 정보를 저장하는 테이블의 역할을 함
 */
@Entity
@Getter
@Setter
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
     * 생성일시 (레코드 최초 생성 시)
     */
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 수정일시 (업데이트될 때마다 자동 변경)
     */
    private LocalDateTime updatedAt = LocalDateTime.now();


    /**
     * JPA 내부 이벤트로 엔티티가 최초 저장될 때 생성일시와 수정일시를 초기화
     * PrePersist는 JPA 라이프사이클 콜백 어노테이션
     * 해당 엔티티가 DB에 insert되기 직전에 자동으로 호출되는 메서드를 지정하는 기능
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); // 생성 시에도 updatedAt 초기화
    }
    /**
     * JPA 내부 이벤트로 엔티티가 업데이트될 때 수정일 갱신
     * PreUpdate는 JPA 라이프사이클 콜백 어노테이션
     * 해당 엔티티가 DB에 update되기 직전에 자동으로 호출되는 메서드를 지정하는 기능
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
