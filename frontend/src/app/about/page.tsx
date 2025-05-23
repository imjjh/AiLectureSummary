"use client"

import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ArrowRight, Brain, Clock, Layers } from "lucide-react"
import Link from "next/link"
import Image from "next/image"
import { motion } from "framer-motion"
import { useAuth } from "@/hooks/use-auth"

export default function AboutPage() {
  const { user } = useAuth()

  // 애니메이션 컨테이너 설정
  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  }

  // 애니메이션 아이템 설정
  const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0 },
  }

  return (
    // 페이지 전체 컨테이너 및 애니메이션 적용
    <motion.div
      initial="hidden"
      animate="show"
      variants={container}
      className="container mx-auto px-4 py-16"
    >
      {/* 페이지 타이틀 및 소개 섹션 */}
      <motion.div variants={item} className="max-w-5xl mx-auto text-center mb-20"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <h1 className="text-5xl font-extrabold tracking-tight mb-4 bg-linear-to-r from-pink-500 to-orange-500 text-transparent bg-clip-text">
          서비스 소개
        </h1>
        <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
          AI 기반 동영상 강의 요약 서비스의 기능과 특징을 간결하게 확인하세요.
        </p>
      </motion.div>

      {/* 서비스 작동 방식 섹션 */}
      <motion.section variants={container} className="grid grid-cols-1 md:grid-cols-2 gap-10 mb-24">
        {[
          {
            step: "1. 동영상 업로드",
            imgSrc: "/images/1.png",
            imgAlt: "동영상 업로드 화면",
            desc: "강의 동영상을 업로드하세요. 다양한 형식의 파일을 지원하며, 드래그 앤 드롭 또는 버튼을 통해 손쉽게 업로드할 수 있습니다.",
          },
          {
            step: "2. AI 분석",
            imgSrc: "/images/2.png",
            imgAlt: "AI 분석 과정",
            desc: "AI가 동영상 음성을 텍스트로 변환하고 핵심 내용을 파악하여 요약을 생성합니다.",
          },
          {
            step: "3. 요약 결과 확인",
            imgSrc: "/images/3.png",
            imgAlt: "요약 결과 화면",
            desc: "핵심 요약과 전체 요약 두 가지 방식으로 결과를 제공합니다.",
          },
          {
            step: "4. 요약 저장 및 관리",
            imgSrc: "/images/4.png",
            imgAlt: "마이 페이지 화면",
            desc: "회원 가입 후 로그인하면 요약 결과가 자동 저장되며, 마이 페이지에서 관리할 수 있습니다.",
          },
        ].map((itemData, index) => (
          // 각 단계별 카드 컴포넌트
          <motion.div key={index} variants={item}>
            <Card className="shadow-md hover:shadow-xl transition-shadow">
              <CardContent className="p-6">
                <Image
                  src={itemData.imgSrc}
                  alt={itemData.imgAlt}
                  width={400}
                  height={300}
                  className="rounded-lg mb-4 w-full object-cover"
                />
                <h3 className="text-xl font-semibold mb-2 text-foreground">{itemData.step}</h3>
                <p className="text-muted-foreground text-sm leading-relaxed">{itemData.desc}</p>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </motion.section>

      {/* 서비스가 제공하는 주요 가치 섹션 */}
      <motion.section variants={container} className="mb-24">
        <motion.h2 variants={item} className="text-3xl font-bold mb-10 text-center">
          우리가 제공하는 가치
        </motion.h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            {
              icon: <Brain className="h-12 w-12 text-primary" />,
              title: "고급 AI 기술",
              desc: "최신 자연어 처리 모델을 활용해 더 정확한 요약을 제공합니다.",
            },
            {
              icon: <Layers className="h-12 w-12 text-primary" />,
              title: "이중 요약 시스템",
              desc: "핵심 요약과 전체 요약을 동시에 제공해 원하는 정보만 빠르게 확인할 수 있습니다.",
            },
            {
              icon: <Clock className="h-12 w-12 text-primary" />,
              title: "시간 절약",
              desc: "1시간짜리 강의도 단 5분 만에 요약해 학습 효율을 높여줍니다.",
            },
          ].map((feature, idx) => (
            // 각 가치별 카드 컴포넌트
            <motion.div key={idx} variants={item}>
              <Card className="shadow-xs">
                <CardContent className="p-6 flex flex-col items-center text-center gap-4">
                  {feature.icon}
                  <h3 className="text-lg font-semibold text-foreground">{feature.title}</h3>
                  <p className="text-sm text-muted-foreground leading-relaxed">{feature.desc}</p>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      </motion.section>

      {/* Call To Action 섹션 */}
      <motion.section variants={item} className="bg-muted p-10 rounded-2xl text-center">
        <h2 className="text-2xl font-bold mb-4">지금 바로 시작하세요</h2>
        <p className="text-muted-foreground mb-6">
          강력한 요약 기능을 사용해 보세요. 요약하고 기록을 저장하려면 로그인하면 됩니다.
        </p>
        <div className="flex justify-center gap-4">
          {user ? (
            <Link href="/">
              <Button className="gap-2">시작하기 <ArrowRight size={16} /></Button>
            </Link>
          ) : (
            <>
              <Link href="/login">
                <Button className="w-32 bg-primary text-white hover:bg-primary/90">
                  로그인
                </Button>
              </Link>
              <Link href="/register">
                <Button className="w-32 bg-primary text-white hover:bg-primary/90">
                  회원가입
                </Button>
              </Link>
            </>
          )}
        </div>
      </motion.section>
    </motion.div>
  )
}