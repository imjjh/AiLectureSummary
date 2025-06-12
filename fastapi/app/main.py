from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from dotenv import load_dotenv
from datetime import datetime, timezone, timedelta
import tempfile
import subprocess
import os
import logging
import requests
from PIL import Image
import io
import re
import base64
from youtube_transcript_api import YouTubeTranscriptApi, TranscriptsDisabled, NoTranscriptFound

# 환경 변수 로드
load_dotenv()

# 파일 업로드 최대 크기 (MB 단위, 환경변수로 설정 가능)
MAX_FILE_SIZE = int(os.getenv("MAX_UPLOAD_SIZE_MB", "2000")) * 1024 * 1024  # default 2000MB

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

KST = timezone(timedelta(hours=9))

class SummaryResponse(BaseModel):
    title: str
    aiSummary: str
    originalText: str
    duration: int
    filename: str
    timestamp: str
    thumbnail: str = None

class YoutubeSummaryRequest(BaseModel):
    youtubeUrl: str

class YoutubeSummaryResponse(BaseModel):
    title: str
    aiSummary: str
    originalText: str
    duration: int
    url: str
    timestamp: str

@app.get("/", include_in_schema=False)
async def root():
    return {"status": "ready", "mode": "OpenAI Whisper API + Youtube"}

def get_video_duration(path: str) -> float:
    try:
        result = subprocess.run(
            ["ffprobe", "-v", "error", "-show_entries", "format=duration",
             "-of", "default=noprint_wrappers=1:nokey=1", path],
            stdout=subprocess.PIPE, text=True
        )
        return float(result.stdout.strip())
    except:
        return 0.0

def is_youtube_url(url: str) -> bool:
    yt_regex = r'(https?://)?(www\.)?(youtube\.com|youtu\.be)/'
    return bool(re.match(yt_regex, url))

def clean_caption_text(text):
    text = re.sub(r'<\d{2}:\d{2}:\d{2}\.\d{3}>', '', text)
    text = re.sub(r'</?c>', '', text)
    text = re.sub(r'<v[^>]*>', '', text)
    text = re.sub(r'</v>', '', text)
    text = re.sub(r'\s+', ' ', text)
    return text.strip()

def compress_image_to_webp(image_bytes, target_kb=350, quality=90):
    image = Image.open(io.BytesIO(image_bytes))
    quality_min, quality_max = 85, 95
    result_bytes = None

    while quality_min <= quality_max:
        mid_quality = (quality_min + quality_max) // 2
        buffer = io.BytesIO()
        image.save(buffer, format="WEBP", quality=mid_quality, method=6)
        size_kb = buffer.tell() / 1024

        if size_kb <= target_kb:
            result_bytes = buffer.getvalue()
            quality_min = mid_quality + 1
        else:
            quality_max = mid_quality - 1

    if result_bytes is None:
        buffer = io.BytesIO()
        image.save(buffer, format="WEBP", quality=quality, method=6)
        result_bytes = buffer.getvalue()
    return result_bytes

def extract_video_id(url_or_id: str) -> str:
    # 유튜브 URL 또는 ID에서 영상 ID 추출
    if re.match(r"^[\w-]{11}$", url_or_id):
        return url_or_id
    match = re.search(r"(?:v=|youtu\.be/)([\w-]{11})", url_or_id)
    if match:
        return match.group(1)
    raise HTTPException(status_code=400, detail="유효하지 않은 유튜브 URL 또는 ID입니다.")

def extract_caption_with_auto(video_id: str):
    """
    영어, 한국어(수동/자동 생성) 자막을 우선순위로 추출
    """
    try:
        transcript_list = YouTubeTranscriptApi.list_transcripts(video_id)
        # 우선순위: 영어(수동) → 영어(자동) → 한국어(수동) → 한국어(자동)
        try:
            transcript = transcript_list.find_manually_created_transcript(['en'])
        except NoTranscriptFound:
            try:
                transcript = transcript_list.find_generated_transcript(['en'])
            except NoTranscriptFound:
                try:
                    transcript = transcript_list.find_manually_created_transcript(['ko'])
                except NoTranscriptFound:
                    transcript = transcript_list.find_generated_transcript(['ko'])
        caption_text = " ".join([entry.text for entry in transcript.fetch()])
        return caption_text
    except (TranscriptsDisabled, NoTranscriptFound):
        return None
    except Exception as e:
        logger.error(f"자막 추출 실패: {e}")
        return None

def get_whisper_transcription(audio_path, file_name):
    api_key = os.getenv("GPT_SECRET_KEY")
    if not api_key:
        raise HTTPException(500, "OpenAI API 키 없음")
    with open(audio_path, "rb") as audio_file:
        response = requests.post(
            "https://api.openai.com/v1/audio/transcriptions",
            headers={
                "Authorization": f"Bearer {api_key}",
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
            },
            files={"file": (file_name, audio_file, "audio/wav")},
            data={"model": "whisper-1"}
        )
        if response.status_code != 200:
            logger.error(response.text)
            raise HTTPException(500, "Whisper API 호출 실패")
        result = response.json()
        return result.get("text", "")

def get_gpt_summary(text):
    api_key = os.getenv("GPT_SECRET_KEY")
    if not api_key:
        raise HTTPException(500, "OpenAI API 키 없음")
    try:
        response = requests.post(
            "https://api.openai.com/v1/chat/completions",
            headers={
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json"
            },
            json={
                "model": "gpt-3.5-turbo",
                "messages": [
                    {
                        "role": "system",
                        "content": (
                            "아래의 텍스트에서 핵심 내용만 한국어로 요약해줘. "
                            "제목도 1줄로 요약해줘. 형식은 다음과 같아:\n"
                            "제목: ...\n요약: ...\n"
                            "반드시 한국어로만 답변해."
                        )
                    },
                    {
                        "role": "user",
                        "content": text[:12000]
                    }
                ],
                "temperature": 0.5
            }
        )
        if response.status_code != 200:
            raise HTTPException(500, f"GPT 요약 실패: {response.text}")

        gpt_text = response.json()["choices"][0]["message"]["content"]

        if "제목:" in gpt_text and "요약:" in gpt_text:
            title = gpt_text.split("제목:")[1].split("요약:")[0].strip()
            raw_summary = gpt_text.split("요약:")[1].strip()
            summary_lines = [line.strip() for line in raw_summary.splitlines() if line.strip()]
            ai_summary = "\n".join(summary_lines[:3])
        else:
            title = "자동 생성 제목"
            ai_summary = "\n".join(gpt_text.strip().splitlines()[:3])
        return title, ai_summary
    except Exception as e:
        logger.error(f"GPT 처리 중 오류: {str(e)}")
        return "요약 실패", "요약 중 오류가 발생했습니다."

@app.post("/api/youtubeSummary")
async def process_youtube_video(data: YoutubeSummaryRequest):
    youtube_url = data.youtubeUrl
    if youtube_url and is_youtube_url(youtube_url):
        try:
            video_id = extract_video_id(youtube_url)
            caption_text = extract_caption_with_auto(video_id)

            if not caption_text or not caption_text.strip():
                raise ValueError("자막 없음")

            gpt_title, gpt_summary = get_gpt_summary(caption_text)
            return {
                "title": gpt_title,
                "aiSummary": gpt_summary,
                "originalText": caption_text,
                "duration": 0,
                "url": youtube_url,
                "timestamp": datetime.now(KST).strftime("%Y-%m-%d %H:%M:%S"),
            }
        except Exception as e:
            logger.warning(f"자막 실패, Whisper로 시도: {e}")
            try:
                with tempfile.TemporaryDirectory() as tmpdir:
                    video_id = extract_video_id(youtube_url)
                    output_path = os.path.join(tmpdir, f"{video_id}.mp4")
                    audio_path = os.path.join(tmpdir, f"{video_id}.wav")
                    ydl_cmd = [
                        "yt-dlp", "-f", "best[ext=mp4]", "-o", output_path, youtube_url
                    ]
                    subprocess.run(ydl_cmd, check=True)

                    if not os.path.exists(output_path):
                        raise HTTPException(400, "영상 다운로드 실패")

                    ffmpeg_cmd = [
                        "ffmpeg", "-y", "-i", output_path,
                        "-vn", "-acodec", "pcm_s16le", "-ar", "16000", "-ac", "1", audio_path
                    ]
                    subprocess.run(ffmpeg_cmd, check=True)

                    if not os.path.exists(audio_path):
                        raise HTTPException(400, "오디오 추출 실패")

                    caption_text = get_whisper_transcription(audio_path, f"{video_id}.wav")
                    gpt_title, gpt_summary = get_gpt_summary(caption_text)
                    return {
                        "title": gpt_title,
                        "aiSummary": gpt_summary,
                        "originalText": caption_text,
                        "duration": 0,
                        "url": youtube_url,
                        "timestamp": datetime.now(KST).strftime("%Y-%m-%d %H:%M:%S"),
                    }
            except Exception as e:
                logger.error("Whisper 처리 실패", exc_info=True)
                raise HTTPException(500, "유튜브 자막 및 Whisper 분석 실패")
    raise HTTPException(400, "유효한 유튜브 URL을 입력해주세요.")

@app.post("/api/summary")
async def process_uploaded_video(
    file: UploadFile = File(None)
):
    temp_video_path = None
    temp_audio_path = None
    # 파일 업로드인 경우 (mp4, mov, mp3)
    if file:
        try:
            if not file.filename.lower().endswith(('.mp4', '.mov', '.mp3')):
                raise HTTPException(400, "지원하지 않는 파일 형식입니다.")

            with tempfile.NamedTemporaryFile(delete=False, suffix=os.path.splitext(file.filename)[1]) as temp_file:
                content = await file.read()
                if len(content) > MAX_FILE_SIZE:
                    raise HTTPException(413, f"파일 크기 초과 (최대 {MAX_FILE_SIZE // (1024 * 1024)}MB)")
                temp_file.write(content)
                temp_video_path = temp_file.name

            thumbnail_base64 = None
            # mp4, mov만 썸네일 생성 (webp로 변경)
            if file.filename.lower().endswith(('.mp4', '.mov')):
                try:
                    ffmpeg_cmd = [
                        "ffmpeg", "-y", "-i", temp_video_path,
                        "-ss", "00:00:01",
                        "-vframes", "1",
                        "-an",
                        "-vf", "thumbnail,scale=1920:1080:force_original_aspect_ratio=decrease",
                        "-f", "image2pipe",
                        "-vcodec", "webp",
                        "-"
                    ]
                    thumb_proc = subprocess.run(
                        ffmpeg_cmd,
                        stdout=subprocess.PIPE,
                        stderr=subprocess.PIPE
                    )
                    image_data = thumb_proc.stdout
                    if image_data:
                        compressed_image_data = compress_image_to_webp(image_data, target_kb=300, quality=90)
                        thumbnail_base64 = base64.b64encode(compressed_image_data).decode('utf-8')
                    else:
                        logger.warning("썸네일 이미지 데이터가 비어있음")
                        thumbnail_base64 = None
                except Exception as e:
                    logger.warning(f"썸네일 추출/압축/base64 변환 중 예외 발생: {str(e)}")
                    thumbnail_base64 = None

            # mp3, mp4, mov 모두 오디오 추출(wav 변환)
            temp_audio_path = temp_video_path + ".wav"
            ffmpeg_cmd = [
                "ffmpeg", "-y", "-i", temp_video_path,
                "-vn", "-acodec", "pcm_s16le",
                "-ar", "16000", "-ac", "1",
                temp_audio_path
            ]
            audio_proc = subprocess.run(
                ffmpeg_cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE
            )
            if audio_proc.returncode != 0:
                stderr_str = audio_proc.stderr.decode("utf-8", errors="ignore")
                if (
                    "Stream map 'a'" in stderr_str or 
                    "does not contain any stream" in stderr_str or
                    "Stream specifier 'a'" in stderr_str or
                    "Output file #" in stderr_str and "has no audio stream" in stderr_str or
                    "could not find codec parameters for stream" in stderr_str or
                    "no audio" in stderr_str
                ):
                    logger.error("오디오 트랙 없음: " + stderr_str)
                    raise HTTPException(400, "소리가 없는 영상입니다. 소리가 포함된 영상을 업로드 해주세요.")
                else:
                    logger.error("ffmpeg 에러: " + stderr_str)
                    raise HTTPException(500, "오디오 추출 중 알 수 없는 에러가 발생했습니다.")
            if not os.path.exists(temp_audio_path) or os.path.getsize(temp_audio_path) == 0:
                raise HTTPException(400, "소리가 없는 영상입니다. 소리가 포함된 영상을 업로드 해주세요.")

            text = get_whisper_transcription(temp_audio_path, file.filename)
            gpt_title, gpt_summary = get_gpt_summary(text)
            duration_sec = float(get_video_duration(temp_video_path))

            return {
                "title": gpt_title,
                "aiSummary": gpt_summary,
                "originalText": text,
                "duration": int(duration_sec),
                "filename": file.filename,
                "timestamp": datetime.now(KST).strftime("%Y-%m-%d %H:%M:%S"),
                "thumbnail": thumbnail_base64
            }

        except HTTPException:
            raise
        except Exception as e:
            logger.error(str(e), exc_info=True)
            raise HTTPException(500, "서버 오류")
        finally:
            for path in [temp_video_path, temp_audio_path]:
                if path and os.path.exists(path):
                    try:
                        os.remove(path)
                    except:
                        pass

    raise HTTPException(400, "mp4, mov, mp3 파일 또는 유튜브 URL을 입력해주세요.")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=9090)
