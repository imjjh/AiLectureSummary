FROM python:3.9

# FFmpeg 및 ffprobe 설치 (더 자세한 설치 명령)
RUN apt-get update && apt-get install -y ffmpeg

# 환경 변수 설정 (FFmpeg 관련 문제 해결 도움)
ENV PATH="/usr/bin:${PATH}"
ENV PYTHONUNBUFFERED=1

WORKDIR /app
COPY . .
RUN pip install --no-cache-dir -r requirements.txt

# 실행 권한 확인
RUN ffprobe -version && chmod -R 755 /app

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "9090"]
