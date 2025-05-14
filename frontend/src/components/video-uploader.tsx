"use client"

import type React from "react"
import { useState, useCallback } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Progress } from "@/components/ui/progress"
import { Upload } from "lucide-react"
import { useLanguage } from "@/hooks/use-language"
import { motion } from "framer-motion"

export default function VideoUploader() {
  const router = useRouter()
  const { t } = useLanguage()
  const [isDragging, setIsDragging] = useState(false)
  const [files, setFiles] = useState<File[]>([])
  const [uploading, setUploading] = useState(false)
  const [progress, setProgress] = useState(0)

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(true)
  }, [])

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
  }, [])

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)

    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const droppedFiles = Array.from(e.dataTransfer.files)
      const videoFiles = droppedFiles.filter(file => file.type.startsWith("video/"))

      // 중복 제거: 기존 files와 새로 드롭된 videoFiles를 합치되 중복은 제거
      setFiles(prevFiles => {
        const combined = [...prevFiles]
        videoFiles.forEach(newFile => {
          const isDuplicate = combined.some(
            existingFile => existingFile.name === newFile.name && existingFile.size === newFile.size
          )
          if (!isDuplicate) {
            combined.push(newFile)
          }
        })
        return combined
      })
    }
  }, [])

  const handleFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const selectedFiles = Array.from(e.target.files)
      const videoFiles = selectedFiles.filter(file => file.type.startsWith("video/"))

      setFiles(prevFiles => {
        const combined = [...prevFiles]
        videoFiles.forEach(newFile => {
          const isDuplicate = combined.some(
            existingFile => existingFile.name === newFile.name && existingFile.size === newFile.size
          )
          if (!isDuplicate) {
            combined.push(newFile)
          }
        })
        return combined
      })
    }
  }, [])

  const handleUpload = useCallback(async () => {
    if (files.length === 0) return;
  
    setUploading(true);
    setProgress(0);
  
    try {
      const file = files[0]; // 첫 번째 파일만 처리
      const formData = new FormData();
      formData.append('file', file);
  
      // 서버 응답을 받아서 파싱
      const response = await new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        
        xhr.upload.addEventListener('progress', (event) => {
          if (event.lengthComputable) {
            setProgress(Math.round((event.loaded / event.total) * 100));
          }
        });
  
        xhr.onreadystatechange = () => {
          if (xhr.readyState === 4) {
            if (xhr.status === 200) {
              // 성공적으로 응답 받음 - JSON 파싱
              resolve(JSON.parse(xhr.responseText));
            } else {
              reject(new Error('Upload failed'));
            }
          }
        };
  
        xhr.open('POST', 'http://localhost:9090/api/summary');
        xhr.send(formData);
      });

      // 2. 응답에서 요약 결과 추출
      const { title, originalText, aiSummary } = response as {
        title: string
        originalText: string
        aiSummary: string
      }
      const accessToken = localStorage.getItem("accessToken");
      // 3. 요약 데이터를 DB에 저장
      const saveRes = await fetch("http://localhost:8080/api/lectures", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${accessToken}` // ✅ 인증 헤더 추가
        },
        body: JSON.stringify({ title, originalText, aiSummary }),
      })
  
      if (!saveRes.ok) {
        throw new Error("DB 저장 실패");
      }

      const { id } = await saveRes.json();

      // 4. 저장된 강의 ID로 summary/[id] 페이지로 이동
      router.push(`/summary/${id}`);
      
    } catch (error) {
      alert(t("upload_failed"));
      console.error('Upload Error:', error);
    } finally {
      setUploading(false);
      setProgress(0);
    }
  }, [files, router, t]);
  

  return (
    <div className="w-full">
      <div
        className={`border-2 border-dashed rounded-xl p-8 text-center ${
          isDragging ? "border-primary bg-primary/5" : "border-muted-foreground/20"
        } transition-colors`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        <div className="flex flex-col items-center justify-center gap-4">
          <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}>
            <div className="w-20 h-20 rounded-full bg-linear-to-r from-pink-500 to-orange-500 flex items-center justify-center">
              <Upload className="h-10 w-10 text-white" />
            </div>
          </motion.div>
          <div>
            <h3 className="text-lg font-medium">{t("upload_video")}</h3>
            <p className="text-sm text-muted-foreground mt-1">{t("drag_drop")}</p>
          </div>

          <input type="file" id="video-upload" className="hidden" accept="video/*" multiple onChange={handleFileChange} />
          <label htmlFor="video-upload">
            <Button variant="outline" className="cursor-pointer rounded-full px-6 bg-background dark:bg-gray-800
             hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors" asChild>
              <span>{t("select_file")}</span>
            </Button>
          </label>
        </div>
      </div>

      {files.length > 0 && (
        <div className="mt-4">
          <p className="text-sm font-medium mb-2">
            선택된 파일:
            {files.map((file, index) => (
              <span key={file.name + file.size} className="block">
                {file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)
              </span>
            ))}
          </p>

          {uploading ? (
            <div className="space-y-2">
              <Progress value={progress} className="h-2 rounded-full" />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>업로드 중... {progress}%</span>
                <span>{progress === 100 ? "요약 생성 중..." : "업로드 중"}</span>
              </div>
            </div>
          ) : (
            <Button
              onClick={handleUpload}
              className="w-full mt-2 rounded-full bg-linear-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0"
            >
              {t("start_summary")}
            </Button>
          )}
        </div>
      )}
    </div>
  )
}
