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
  const [file, setFile] = useState<File | null>(null)
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
      const droppedFile = e.dataTransfer.files[0]
      if (droppedFile.type.startsWith("video/")) {
        setFile(droppedFile)
      } else {
        alert("비디오 파일만 업로드 가능합니다.")
      }
    }
  }, [])

  const handleFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0])
    }
  }, [])

  const handleUpload = useCallback(() => {
    if (!file) return

    setUploading(true)

    // Simulate upload progress
    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 100) {
          clearInterval(interval)
          setTimeout(() => {
            router.push("/summary/demo-result")
          }, 500)
          return 100
        }
        return prev + 5
      })
    }, 200)

    // In a real application, you would upload the file to your server here
    // and then redirect to the summary page once processing is complete
  }, [file, router])

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

          <input type="file" id="video-upload" className="hidden" accept="video/*" onChange={handleFileChange} />
          <label htmlFor="video-upload">
            <Button variant="outline" className="cursor-pointer rounded-full px-6 bg-background dark:bg-gray-800
             hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors" asChild>
              <span>{t("select_file")}</span>
            </Button>
          </label>
        </div>
      </div>

      {file && (
        <div className="mt-4">
          <p className="text-sm font-medium mb-2">
            선택된 파일: {file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)
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
