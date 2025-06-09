package com.ktnu.AiLectureSummary.infrastructure.adapter.summary;


import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import com.ktnu.AiLectureSummary.application.port.out.LectureSummaryFromFilePort;
import com.ktnu.AiLectureSummary.global.config.FastApiProperties;
import com.ktnu.AiLectureSummary.global.exception.ExternalApiException;
import com.ktnu.AiLectureSummary.global.exception.FileProcessingException;
import com.ktnu.AiLectureSummary.util.MultipartFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FastApiLectureSummaryFromFileAdapter implements LectureSummaryFromFilePort {

    private final RestTemplate restTemplate;
    private final FastApiProperties fastApiProperties;

    @Override
    public LectureSummaryResponse requestSummary(MultipartFile file) {

        // 요청 헤더 설정, FastAPI가 multipart 형식으로 파일을 받을 수 있게 Content-Type을 multipart/form-data로 지정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 하나의 key에 여러 개의 value를 가질 수 있는 Map, multipart 요청에서는 "file" 같은 form 필드명을 키로 설정해야 함
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {
            // FastAPI 파라미터 이름 == Spring 쪽 key 일치해야 동작 "file"
            //  Spring 서버가 FastAPI 서버에 파일을 전송하기 위해 multipart/form-data 요청 본문(body)을 구성
            body.add("file", new MultipartFileResource(file.getInputStream(), file.getOriginalFilename()));
        } catch (IOException e) {
            throw new FileProcessingException("FastAPI 전송 중 파일 읽기 실패", e);
        }
        //  Spring에서 FastAPI로 파일을 보내기 위한 HTTP 요청 객체(requestEntity)를 구성하는 부분
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 동기식 처리로 FastAPI 요청 결과를 기다린 후 응답을 반환함
        try {
            ResponseEntity<LectureSummaryResponse> response = restTemplate.exchange(
                    fastApiProperties.getUrl() + "/api/summary",  // FastAPI 엔드포인트
                    HttpMethod.POST,
                    requestEntity,
                    LectureSummaryResponse.class // 받은 json 응답을 역직렬화할 때 사용할 클래스 지정
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
