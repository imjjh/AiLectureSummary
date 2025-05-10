package com.ktnu.AiLectureSummary.util;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * RestTemplate에서 multipart/form-data 전송 시 사용할 파일 리소스 클래스
 * MultipartFile을 Resource로 감싸 전송 가능하게 함
 */
public class MultipartFileResource extends InputStreamResource {
    private final String filename;

    /**
     *  생성자에서 파일 이름을 받아 내부에 저장
     *  Content-Disposition 헤더 설정에 사용됨
     *  ex) Content-Disposition: form-data; name="file"; filename="강의영상.mp4"
     */
    public MultipartFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    /**
     * RestTemplate가 multipart 요청 보낼 때 파일명을 포함시키기 위해 오버라이드
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * 스트림 기반이므로 정확한 파일 크기 알 수 없음, -1 반환
     * @return -1
     */
    @Override
    public long contentLength(){
        return -1;
    }
}