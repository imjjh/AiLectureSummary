# AiLectureSummary

AI 기반 강의 요약 시스템

## 📦 프로젝트 구조

AiLectureSummary/
├── frontend/ # Next.js 기반 프론트엔드
├── backend/ # Spring Boot 기반 API 서버
├── ai/ # AI 처리 (Whisper, FastAPI)
└── docker-compose.yml

## 🚀 실행 방법

```bash
docker-compose up --build

	•	백엔드: http://localhost:8080
	•	프론트엔드: http://localhost:3000

🔧 주요 기술 스택
	•	Frontend: Next.js, React, TypeScript
	•	Backend: Java 17, Spring Boot, Gradle
	•	AI: FastAPI, whisper
	•	Docker, Docker Compose





```

## 전체 아키텍쳐 흐름

[ 사용자 ]
   ↓ 브라우저 요청 (Next.js)
[ 프론트엔드 (Next.js)]
   ↓ fetch
[ 백엔드1 (Spring Boot)]  ←→  [DB]
   ↓ HTTP 요청
[ 백엔드2 (FastAPI)]
   ↓ AI 연산
[ 결과 반환 → Spring → 프론트 ]