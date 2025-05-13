package com.ktnu.AiLectureSummary.service;


import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.domain.MemberLecture;
import com.ktnu.AiLectureSummary.repository.MemberLectureRepository;
import com.ktnu.AiLectureSummary.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLectureService {
    private final MemberLectureRepository memberLectureRepository;
    private final MemberService memberService;

    public void save(CustomUserDetails user, Lecture lecture) {
        Member member = memberService.findById(user.getId());

        if (!memberLectureRepository.existsByMemberAndLecture(member, lecture)) {
            memberLectureRepository.save(
                    MemberLecture.builder()
                            .member(member)
                            .lecture(lecture)
                            .build()
            );
        }

    }


}
