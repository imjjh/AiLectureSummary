package com.ktnu.AiLectureSummary.service;


import com.ktnu.AiLectureSummary.domain.Member;
import com.ktnu.AiLectureSummary.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    public void processVideoUpload(MultipartFile file){
        //VideoHasing

        // FastAPI 전송
                        /*
        응답 예시
                {
                "title": "선형대수학 강의 1주차 요약", → GPT 자동 생성
                "summary": "이 강의에서는 벡터와 행렬의 기본 개념...", → GPT 생성
                "originalText": "오늘은 벡터의 개념에 대해서 이야기해보겠습니다..." → whisperAPI 결과
                }
         */

        // DB 저장
    }

    public void VideoHashing(){
        // 비디오 해싱

        // 해싱 값이 DB에 존재하는 경우 기존에 존재 하던것을 반환
    }

    public void getLectureList(Member member){

    }

//    public LectureDetailResponse getLectureDetail(Long lectureId, Member member){
//
//    }

    //
    //  강의 요약 삭제
    //  public void deleteLecture(Long lectureId, Member member){    }


}
