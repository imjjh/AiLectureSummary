package com.ktnu.AiLectureSummary.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB 기본키, 자동 증가

    @Column(nullable = false, unique = true)
    private String loginId; // 로그인할 떄 쓸 ID

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    private String name; // 이름

}
