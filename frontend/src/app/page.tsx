"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Upload, PlayCircle, FileText, Info, ArrowRight } from "lucide-react"
import Link from "next/link"
import VideoUploader from "@/components/video-uploader"
import { motion } from "framer-motion"
import { useRef } from "react"
import { useAuth } from "@/hooks/use-auth"
import { useRouter } from "next/navigation"
import UploadTabs from "@/components/UploadTabs";

export default function Home() {

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  }

  const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0 },
  }

  const uploadRef = useRef<HTMLDivElement | null>(null)
  const { user, isLoading } = useAuth()
  const router = useRouter()

  const handleStartClick = () => {
    if (isLoading) return

    if (user) {
      // 로그인된 경우: 업로드 영역으로 스크롤
      uploadRef.current?.scrollIntoView({ behavior: "smooth" })
    } else {
      // 로그인 안된 경우: 회원가입 페이지로 이동
      router.push("/register")
    }
  }

  return (
    <div className="bg-linear-to-b from-pink-50 to-white dark:from-gray-900 dark:to-gray-950">
      <div className="container mx-auto px-4 py-12">
        <div className="max-w-4xl mx-auto">
          <motion.div
            ref = {uploadRef}
            className="text-center mb-12"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <h1 className="text-4xl md:text-5xl font-bold tracking-tight mb-4 bg-linear-to-r from-pink-500 to-orange-500 text-transparent bg-clip-text">
              AI 강의 요약 서비스
            </h1>
            <p className="text-lg text-muted-foreground mb-8">AI가 동영상을 분석하고 요약해주는 서비스입니다.</p>
            <div className="flex justify-center gap-4 mb-8">
              <Link href="/about">
                <Button variant="outline" className="gap-2 rounded-full px-6 bg-background dark:bg-gray-800 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                  <Info size={18} />
                  서비스 소개
                </Button>
              </Link>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            <Card className="mb-12 border-2 border-dashed rounded-xl shadow-lg overflow-hidden bg-white dark:bg-gray-900">
              <CardContent className="p-6">
                <UploadTabs />
              </CardContent>
            </Card>
          </motion.div>

          <motion.div
            className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12"
            variants={container}
            initial="hidden"
            animate="show"
          >
            <motion.div variants={item}>
              <Card className="w-70 h-60 rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-all duration-300 border-0 bg-white dark:bg-gray-900">
                <CardContent className="p-6 flex flex-col items-center text-center">
                  <div className="w-16 h-16 rounded-full bg-linear-to-r from-pink-500 to-orange-500 flex items-center justify-center mb-4">
                    <Upload className="h-8 w-8 text-white" />
                  </div>
                  <h3 className="text-lg font-medium mb-2">동영상 업로드</h3>
                  <p className="text-sm text-muted-foreground">
                    강의 또는 다양한 동영상을 업로드하세요.
                  </p>
                </CardContent>
              </Card>
            </motion.div>

            <motion.div variants={item}>
              <Card className="w-70 h-60 rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-all duration-300 border-0 bg-white dark:bg-gray-900">
                <CardContent className="p-6 flex flex-col items-center text-center">
                  <div className="w-16 h-16 rounded-full bg-linear-to-r from-pink-500 to-orange-500 flex items-center justify-center mb-4">
                    <PlayCircle className="h-8 w-8 text-white" />
                  </div>
                  <h3 className="text-lg font-medium mb-2">AI 분석</h3>
                  <p className="text-sm text-muted-foreground">
                    AI가 동영상 내용을 자동으로 분석합니다. 중요한 내용을 선별하고, 핵심 주제를 파악합니다.
                  </p>
                </CardContent>
              </Card>
            </motion.div>

            <motion.div variants={item}>
              <Card className="w-70 h-60 rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition-all duration-300 border-0 bg-white dark:bg-gray-900">
                <CardContent className="p-6 flex flex-col items-center text-center">
                  <div className="w-16 h-16 rounded-full bg-linear-to-r from-pink-500 to-orange-500 flex items-center justify-center mb-4">
                    <FileText className="h-8 w-8 text-white" />
                  </div>
                  <h3 className="text-lg font-medium mb-2">요약 결과</h3>
                  <p className="text-sm text-muted-foreground">
                    핵심 요약과 전체 요약 두 가지 형태로 결과를 확인하세요.
                  </p>
                </CardContent>
              </Card>
            </motion.div>
          </motion.div>

          <motion.div
            className="text-center"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.6 }}
          >
            <h2 className="text-2xl font-bold mb-4">지금 바로 시작하세요</h2>
            {/*<p className="text-muted-foreground mb-6">
              회원가입 없이도 서비스를 이용할 수 있습니다. 요약 기록을 저장하려면 로그인하세요.
            </p>*/}
            <Button 
            onClick={handleStartClick} className="rounded-full px-8 py-6 text-lg bg-linear-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0 shadow-lg hover:shadow-xl transition-all duration-300">
              <span>무료로 시작하기</span>
              <ArrowRight className="ml-2 h-5 w-5" />
            </Button>
          </motion.div>
        </div>
      </div>
    </div>
  )
}
