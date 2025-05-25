# ================================
# 1단계: 의존성 설치
# ================================
FROM python:3.9-slim as builder

# 시스템 의존성 설치
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*

# 환경 변수 설정
ENV PYTHONUNBUFFERED=1 \
    PYTHONPATH=/app \
    PATH="/usr/bin:${PATH}"

# 작업 디렉토리 설정
WORKDIR /install

# Docker 캐시를 활용하기 위해 requirements만 복사
COPY requirements.txt .

# Python 의존성 별도 폴더에 설치
RUN pip install --prefix=/install/packages --no-cache-dir -r requirements.txt

# ================================
# 2단계: 실행 환경
# ================================
FROM python:3.9-slim

# 시스템 의존성 다시 설치 (실행용)
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*

# 환경 변수 설정
ENV PYTHONUNBUFFERED=1 \
    PYTHONPATH=/app \
    PATH="/usr/bin:${PATH}"

# 작업 디렉토리 설정
WORKDIR /app

# 빌더에서 설치된 패키지 복사
COPY --from=builder /install/packages /usr/local

# 애플리케이션 코드 복사
COPY . .

# FFmpeg 설치 확인
RUN ffprobe -version

# .env를 명시적으로 로드하고 앱 실행
CMD ["sh", "-c", "python -c 'from dotenv import load_dotenv; load_dotenv()' && uvicorn app.main:app --host 0.0.0.0 --port 9090"]
