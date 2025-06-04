# AiLectureSummary

AI 기반 강의 요약 시스템

## 📦 프로젝트 구조

```
AiLectureSummary/
├── backend/     # Spring Boot 기반 백엔드 API
├── frontend/    # Next.js 기반 프론트엔드
├── fastapi/     # Whisper 기반 AI 요약 서버
├── .env.*       # 환경 변수 설정 파일들
├── docker-compose.dev.yml
└── docker-compose.prod.yml
```

## 🔗 배포 링크

- 프론트엔드: https://aisummarymono.vercel.app/
- Swagger 문서: https://aisummarymono.vercel.app/api 

## 🔧 주요 기술 스택

- **Frontend**: Next.js, React, TypeScript  
- **Backend**: Java 17, Spring Boot, Gradle  
- **AI**: FastAPI, Whisper  
- **Database**: MySQL 8  
- **Cache**: Redis  
- **DevOps**: Docker, Docker Compose

## 전체 아키텍쳐 흐름


```mermaid
flowchart TD
    user["사용자"]
    frontend["프론트엔드 (Next.js)"]
    backend["백엔드 (Spring Boot)"]
    database["MySQL DB"]
    redis["Redis (캐시/토큰 저장)"]
    ai_server["AI 요약 서버 (FastAPI)"]
    whisper_local["Whisper 로컬 요약"]
    whisper_api["Whisper API 요약"]
    gpt_api["GPT API 요약"]

    user --> frontend
    frontend -->|fetch - 인증 및 인가 수행| backend
    backend -->|DB 저장/조회| database
    backend -->|토큰/캐시 처리| redis
    backend --> ai_server
    ai_server --> whisper_local
    ai_server --> whisper_api
    ai_server --> gpt_api
    ai_server -->|요약 결과 반환| backend
    backend -->|응답 전달| frontend
```
