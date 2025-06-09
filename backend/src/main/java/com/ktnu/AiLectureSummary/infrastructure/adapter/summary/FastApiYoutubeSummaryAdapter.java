package com.ktnu.AiLectureSummary.infrastructure.adapter.summary;

import com.ktnu.AiLectureSummary.application.port.out.YoutubeSummaryPort;
import com.ktnu.AiLectureSummary.global.config.FastApiProperties;
import com.ktnu.AiLectureSummary.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FastApiYoutubeSummaryAdapter implements YoutubeSummaryPort {
    private final RestTemplate restTemplate;
    private final FastApiProperties fastApiProperties;

    @Override
    public LectureSummaryResponse requestSummary(String youtubeUrl) {
        try {
            // 요청 헤더 설정: JSON 타입 명시
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 본문에 YouTube URL 추가
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("youtubeUrl", youtubeUrl);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 외부 FastAPI 서버에 POST 요청 전송
            ResponseEntity<LectureSummaryResponse> response = restTemplate.exchange(
                fastApiProperties.getUrl() + "/api/youtubeSummary",
                HttpMethod.POST,
                requestEntity,
                LectureSummaryResponse.class
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
