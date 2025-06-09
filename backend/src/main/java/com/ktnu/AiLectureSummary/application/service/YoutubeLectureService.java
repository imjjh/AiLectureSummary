package com.ktnu.AiLectureSummary.application.service;

import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import com.ktnu.AiLectureSummary.application.port.out.YoutubeSummaryPort;
import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YoutubeLectureService {
    private final LectureRepository lectureRepository;
    private final YoutubeSummaryPort youtubeSummaryPort;


    public Lecture processYoutubeLecture(String url) {

        // DB에 중복되는 링크가 존재하는지 확인
        Optional<Lecture> optionalLecture = lectureRepository.findByYoutubeUrl(url);

        // 이미 존재하는 경우 바로 바로 반환
        if (optionalLecture.isPresent()) {
            return optionalLecture.get();
        }

        // FastAPI 호출
        LectureSummaryResponse registerRequest = youtubeSummaryPort.requestSummary(url);

        // 썸네일 저장 X
        return lectureRepository.save(Lecture.fromYoutubeUrl(registerRequest, url));

    }

}
