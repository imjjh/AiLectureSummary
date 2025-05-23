"use client"

import type React from "react"
import { useState, useCallback } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Progress } from "@/components/ui/progress"
import { Upload } from "lucide-react"
import { motion } from "framer-motion"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

export default function VideoUploader() {
  const router = useRouter()
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

      setFiles(prevFiles => [
        ...prevFiles,
        ...videoFiles.filter(newFile => 
          !prevFiles.some(existing => 
            existing.name === newFile.name && existing.size === newFile.size
          )
        )
      ])
    }
  }, [])

  const handleFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.length) {
      const newFiles = Array.from(e.target.files).filter(file => 
        file.type.startsWith("video/") && 
        !files.some(existing => 
          existing.name === file.name && existing.size === file.size
        )
      )
      setFiles(prev => [...prev, ...newFiles])
    }
  }, [files])

  const handleUpload = useCallback(async () => {
    if (!files.length) return

    setUploading(true)
    setProgress(0)

    try {
      const file = files[0]
      const formData = new FormData()
      formData.append('file', file)

      const response = await new Promise<any>((resolve, reject) => {
        const xhr = new XMLHttpRequest()

        xhr.upload.addEventListener('progress', (event) => {
          if (event.lengthComputable) {
            setProgress(Math.round((event.loaded / event.total) * 100))
          }
        })

        xhr.onreadystatechange = () => {
          if (xhr.readyState === 4) {
            if (xhr.status === 200) {
              resolve(JSON.parse(xhr.responseText))
            } else {
              reject(new Error(`HTTP ${xhr.status}: ${xhr.responseText || xhr.statusText}`))
            }
          }
        }

        xhr.open('POST', `${API_BASE_URL}/api/lecture/upload`)
        xhr.withCredentials = true
        xhr.send(formData)
      })

      // ✅ 성공 후 해당 ID 페이지로 이동
      const lectureId = response.data.id
      router.push(`/summary/${lectureId}`)

    } catch (error: any) {
      alert("업로드 실패: " + error.message)
      console.error('Upload Error:', error)
    } finally {
      setUploading(false)
      setProgress(0)
    }
  }, [files, router])

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
            <div className="w-20 h-20 rounded-full bg-gradient-to-r from-pink-500 to-orange-500 flex items-center justify-center">
              <Upload className="h-10 w-10 text-white" />
            </div>
          </motion.div>
          <div>
            <h3 className="text-lg font-medium">동영상 업로드</h3>
            <p className="text-sm text-muted-foreground mt-1">여기로 파일을 드래그하거나 클릭해서 선택하세요</p>
          </div>

          <input 
            type="file" 
            id="video-upload" 
            className="hidden" 
            accept="video/*" 
            onChange={handleFileChange}
            multiple
          />
          <label htmlFor="video-upload">
            <Button 
              variant="outline" 
              className="cursor-pointer rounded-full px-6 bg-background dark:bg-gray-800 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
              asChild
            >
              <span>파일 선택</span>
            </Button>
          </label>
        </div>
      </div>

      {files.length > 0 && (
        <div className="mt-4">
          <p className="text-sm font-medium mb-2">
            {files.map((file) => (
              <span key={file.name} className="block">
                {file.name} ({(file.size / (1024 * 1024)).toFixed(2)}MB)
              </span>
            ))}
          </p>

          {uploading ? (
            <div className="space-y-2">
              <Progress value={progress} className="h-2 rounded-full" />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>업로드 진행률: {progress}%</span>
                <span>{progress === 100 ? "요약 생성 중..." : "업로드 중"}</span>
              </div>
            </div>
          ) : (
            <Button
              onClick={handleUpload}
              className="w-full mt-2 rounded-full bg-gradient-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0 text-white"
            >
              요약 시작
            </Button>
          )}
        </div>
      )}
    </div>
  )
}