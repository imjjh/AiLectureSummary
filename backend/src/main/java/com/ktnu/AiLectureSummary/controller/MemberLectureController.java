package com.ktnu.AiLectureSummary.controller;


import com.ktnu.AiLectureSummary.service.MemberLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberLectureController {
    private final MemberLectureService memberLectureService;


}
