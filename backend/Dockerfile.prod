FROM openjdk:17-slim-buster

# 필수 도구 설치 (curl, git)
RUN apt update && apt install -y curl git

# 작업 디렉토리 설정 (없으면 자동 생성됨)
WORKDIR /app

# Gradle 관련 파일 및 wrapper 먼저 복사 (의존성 캐시 활용)
COPY gradlew build.gradle settings.gradle ./
COPY gradle/ gradle/

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 의존성 미리 다운로드 (Gradle 캐시 활용)
RUN ./gradlew clean dependencies --no-daemon

# 소스 코드 복사 (필요한 경우: src/main/java, src/main/resources 등 모두 포함)
COPY src/ src/

# 최종 빌드: 변경된 소스 코드 반영
RUN ./gradlew clean build --no-daemon

# 생성된 실행 가능한 JAR 파일 실행 (와일드카드로 최신 SNAPSHOT JAR 선택)
CMD ["sh", "-c", "java -jar build/libs/*SNAPSHOT.jar"]