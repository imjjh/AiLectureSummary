package com.ktnu.AiLectureSummary.application.service;

import com.ktnu.AiLectureSummary.global.config.FastApiProperties;
import com.ktnu.AiLectureSummary.domain.Lecture;
import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import com.ktnu.AiLectureSummary.global.exception.ExternalApiException;
import com.ktnu.AiLectureSummary.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YoutubeLectureService {
    private final LectureRepository lectureRepository;
    private final FastApiProperties fastApiProperties;


    public Lecture processYoutubeLecture(String url) {

        // DB에 중복되는 링크이 존재하는지 확인
        Optional<Lecture> optionalLecture = lectureRepository.findByYoutubeUrl(url);

        // 이미 존재하는 경우 바로 바로 반환
        if (optionalLecture.isPresent()) {
            return optionalLecture.get();
        }

        // FastAPI 호출
        LectureSummaryResponse registerRequest = requestYoutubeSummary(url);

        // 썸네일 저장 X

        return lectureRepository.save(Lecture.fromYoutubeUrl(registerRequest, url));

    }

    private LectureSummaryResponse requestYoutubeSummary(String url) {
        // Spring에서 외부 HTTP 요청을 보낼 수 있는 기본 클라이언트
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 연결 타임아웃: 3초
        factory.setReadTimeout(100000);  // 응답 타임아웃: 100초
        restTemplate.setRequestFactory(factory);

        // FastAPI에서 application/x-www-form-urlencoded 형식으로 youtube_url을 받아야 하므로
        // 헤더를 설정하고, 본문에는 키-값 쌍으로 URL을 담는다
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        org.springframework.util.MultiValueMap<String, String> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("youtube_url", url);

        //  Spring에서 FastAPI로 파일을 보내기 위한 HTTP 요청 객체(requestEntity)를 구성하는 부분
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 동기식 처리로 FastAPI 요청 결과를 기다린 후 응답을 반환함
        try {
            ResponseEntity<LectureSummaryResponse> response = restTemplate.exchange(
                    fastApiProperties.getUrl() + "/api/summary", // FastAPI 엔드포인트
                    org.springframework.http.HttpMethod.POST,
                    requestEntity,
                    LectureSummaryResponse.class // 받은 json 응답을 역직렬화
            );

            if (response.getBody() == null) {
                throw new ExternalApiException("FastAPI 응답이 비어 있습니다.");
            }

            return response.getBody();
        } catch (ResourceAccessException e) {
            // 타임아웃 or 연결 실패
            throw new ExternalApiException("FastAPI 서버와의 연결이 실패했거나 응답 시간이 초과되었습니다.", e);

        } catch (HttpStatusCodeException e) {
            // FastAPI 응답이 400, 500 에러일 때
            String errorBody = e.getResponseBodyAsString();
            throw new ExternalApiException("FastAPI 응답 오류: " + errorBody, e);

        } catch (Exception e) {
            throw new ExternalApiException("FastAPI 요청 중 알 수 없는 오류 발생", e);
        }
    }

}
