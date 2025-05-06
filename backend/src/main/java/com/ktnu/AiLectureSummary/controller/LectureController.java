package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.repository.MemberRepository;
import com.ktnu.AiLectureSummary.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;
}
