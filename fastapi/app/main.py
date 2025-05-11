from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from faster_whisper import WhisperModel
import tempfile
import os
import logging
import subprocess
from pydantic import BaseModel
from datetime import datetime
from typing import Optional
from datetime import datetime, timezone, timedelta

# OpenAI 임포트 (키가 없을 때도 에러 안나게)
try:
    from openai import OpenAI
except ImportError:
    OpenAI = None

# 로거 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="AI Lecture Summary API",
    description="동영상 강의 요약 서비스",
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc"
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Whisper 모델 초기화
model = WhisperModel(
    model_size_or_path="base",
    device="cpu",
    compute_type="int8",
    download_root="/app/models"
)
KST = timezone(timedelta(hours=9))

class SummaryResponse(BaseModel):
    title: str
    summary: str
    originalText: str  # camelCase로 통일
    duration: str      # 포맷팅된 문자열로 변경
    filename: str
    timestamp: str     # 포맷팅된 시간 문자열

# OpenAI 클라이언트 초기화
api_key = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=api_key) if (api_key and OpenAI) else None

def get_video_duration(path: str) -> float:
    try:
        result = subprocess.run(
            [
                "/usr/bin/ffprobe", "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", path
            ],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True
        )
        logger.info(f"ffprobe 출력: {result.stdout}")
        return float(result.stdout.strip())
    except Exception as e:
        logger.error(f"ffprobe로 길이 추출 실패: {e}")
        return 0.0

def format_duration(seconds: float) -> str:
    """초를 분:초 형식으로 변환"""
    minutes = int(seconds // 60)
    seconds = int(seconds % 60)
    return f"{minutes}:{seconds:02d}"

@app.post("/api/summary", response_model=SummaryResponse)
async def process_video(file: UploadFile = File(...)):
    temp_video_path = None
    temp_audio_path = None
    
    try:
        # 1. 파일 유효성 검사
        if not file.filename.lower().endswith(('.mp4', '.mov')):
            raise HTTPException(400, "지원하지 않는 파일 형식")

        # 2. 임시 파일 저장
        with tempfile.NamedTemporaryFile(delete=False, suffix=".mp4") as temp_video:
            content = await file.read()
            if len(content) > 500 * 1024 * 1024:
                raise HTTPException(413, "파일 크기 초과 (최대 500MB)")
            temp_video.write(content)
            temp_video_path = temp_video.name

        # 3. 음성 추출
        temp_audio_path = temp_video_path + ".wav"
        ffmpeg_cmd = [
            "ffmpeg", "-i", temp_video_path,
            "-vn", "-acodec", "pcm_s16le",
            "-ar", "16000", "-ac", "1",
            "-af", "highpass=f=300,lowpass=f=3000",
            temp_audio_path
        ]
        subprocess.run(ffmpeg_cmd, check=True, capture_output=True)

        # 4. Whisper 음성 인식
        segments, info = model.transcribe(
            audio=temp_audio_path,
            beam_size=5,
            language="ko",
            vad_filter=True
        )
        original_text = " ".join(segment.text.strip() for segment in segments)
        
        # 동영상 길이 처리 - Whisper duration이 0이면 ffprobe로 직접 추출
        duration_sec = info.duration
        if not duration_sec or duration_sec < 0.1:
            # Whisper duration이 0이면 ffmpeg로 추출
            duration_sec = get_video_duration(temp_video_path)
            logger.info(f"Whisper duration이 0이라 ffprobe로 추출: {duration_sec}초")
        
        duration = format_duration(duration_sec)  # 포맷팅된 시간

        # 5. GPT 요약 생성
        title = "API 키가 필요합니다"
        summary = "OpenAI API 키를 설정하면 자동으로 요약이 생성됩니다."
        
        if client:
            try:
                response = client.chat.completions.create(
                    model="gpt-4",
                    messages=[{
                        "role": "system",
                        "content": "다음 형식으로 한국어 요약 생성:\n제목: [생성된 제목]\n요약: [3줄 요약]"
                    }, {
                        "role": "user", 
                        "content": original_text
                    }]
                )
                generated_text = response.choices[0].message.content
                
                if "제목:" in generated_text and "요약:" in generated_text:
                    title = generated_text.split("\n")[0].replace("제목: ", "").strip()
                    summary = "\n".join(generated_text.split("\n")[1:]).replace("요약: ", "").strip()
                    
            except Exception as e:
                logger.error(f"GPT 요약 실패: {str(e)}")
                title = "요약 생성 오류"
                summary = f"GPT 처리 중 오류 발생: {str(e)}"

        return {
            "title": title,
            "summary": summary,
            "originalText": original_text,  # camelCase 필드명
            "duration": duration,           # 포맷팅된 시간
            "filename": file.filename,
            "timestamp": datetime.now(KST).strftime("%Y-%m-%d %H:%M:%S")  # 포맷팅된 시간
        }

    except Exception as e:
        logger.error(f"처리 실패: {str(e)}", exc_info=True)
        raise HTTPException(500, "서버 오류") from e
        
    finally:
        # 6. 임시 파일 정리
        for path in [temp_video_path, temp_audio_path]:
            if path and os.path.exists(path):
                os.unlink(path)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=9090)
