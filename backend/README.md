# Backend - AiLectureSummary

Spring Boot 기반의 API 서버입니다.

## ⚙️ 실행 방법

### 로컬 실행

```bash
./gradlew clean build
java -jar build/libs/AiLectureSummary-0.0.1-SNAPSHOT.jar

도커 실행

docker-compose up --build

📁 주요 디렉토리
	•	src/main/java: Java 소스
	•	src/main/resources: 설정 파일 (application.yml 등)

🛠 사용 기술
	•	Java 17
	•	Spring Boot
	•	Gradle
	•	ffmpeg / tesseract (OCR)

```
