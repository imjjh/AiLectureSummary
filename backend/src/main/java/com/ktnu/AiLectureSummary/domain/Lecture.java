package com.ktnu.AiLectureSummary.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

/**
 *   강의 정보를 나타내는 엔티티 클래스
 */

@Entity
@Getter
@Setter
public class Lecture {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private  String aisummary;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLecture> memberLectures = new ArrayList<>();


}
