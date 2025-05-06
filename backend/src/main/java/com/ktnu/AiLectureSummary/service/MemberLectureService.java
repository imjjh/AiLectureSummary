package com.ktnu.AiLectureSummary.service;


import com.ktnu.AiLectureSummary.repository.MemberLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLectureService {
    private final MemberLectureRepository memberLectureRepository;

}
