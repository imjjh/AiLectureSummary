package com.ktnu.AiLectureSummary.application.port.out;

import com.ktnu.AiLectureSummary.application.dto.lecture.response.LectureSummaryResponse;

/**
 * 외부 시스템(FastApi)에 요약 요청을 위임하는 출력 포트 인터페이스
 *
 * 어댑터-포트구에서 어댑터는 이 인터페이스를 구현하며,
 * 유스케이스 계층은 이 인터페이스에만 의존함으로써 DIP을 만족합니다.
 *
 * 이를 통해 유스케이스 로직은 외부 구현(FastAPI->Lambda 등)의 변경에 영향을 받지 않습니다.
 */
public interface YoutubeSummaryPort {
    /**
     * 주어진 유트브 Url에 대한 요약을 요청합니다.
     *
     * @param youtubeUrl
     * @return
     */
    LectureSummaryResponse requestSummary(String youtubeUrl);
}
