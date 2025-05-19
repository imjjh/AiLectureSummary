"use client"

import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import Link from "next/link"
import Image from "next/image"

// summaryData의 타입 명시
interface SummaryData {
  title?: string
  duration?: string
  uploadDate?: string
  thumbnailUrl?: string
  aiSummary?: string
  originalText?: string
  personalNote?: string
}

export default function Page() {
  const params = useParams()
  const { id } = params as { id: string }

  const [summaryData, setSummaryData] = useState<SummaryData | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchSummary() {
      try {
        const res = await fetch(`http://localhost:8080/api/member-lectures/${id}`, {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        })

        if (!res.ok) throw new Error("요약 데이터를 가져오는 데 실패했습니다.")
        const data = await res.json()
        setSummaryData(data)
      } catch (err) {
        setError(err instanceof Error ? err.message : String(err))
      }
    }
    fetchSummary()
  }, [id])

  if (error) return <div className="container mx-auto px-4 py-12 text-red-500">{error}</div>
  if (!summaryData) return <div className="container mx-auto px-4 py-12">로딩 중...</div>

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <Link href="/" className="text-primary hover:underline mb-4 inline-block">
            ← 홈으로 돌아가기
          </Link>
          <h1 className="text-3xl font-bold tracking-tight mb-2">{summaryData.title ?? "제목 없음"}</h1>
          <p className="text-muted-foreground">
            동영상 길이: {summaryData.duration ?? "정보 없음"} • 업로드: {summaryData.uploadDate ?? "정보 없음"}
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          <div className="md:col-span-2">
            <Image
              src={summaryData.thumbnailUrl ?? "/placeholder.svg"}
              width={500}
              height={300}
              alt={summaryData.title ?? "썸네일"}
              className="rounded-lg w-full object-cover aspect-video mb-4"
            />

            <Tabs defaultValue="key" className="mb-12">
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="key">AI 요약</TabsTrigger>
                <TabsTrigger value="full">원문</TabsTrigger>
              </TabsList>

              <TabsContent value="key" className="mt-6">
                <Card>
                  <CardContent className="p-6">
                    <h2 className="text-xl font-bold mb-4">AI 요약</h2>
                    {summaryData.aiSummary ? (
                      <p className="whitespace-pre-wrap text-muted-foreground">
                        {summaryData.aiSummary}
                      </p>
                    ) : (
                      <p className="text-muted-foreground">AI 요약 정보가 없습니다.</p>
                    )}
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="full" className="mt-6">
                <Card>
                  <CardContent className="p-6">
                    <h2 className="text-xl font-bold mb-4">원문</h2>
                    {summaryData.originalText ? (
                      <p className="whitespace-pre-wrap text-muted-foreground">
                        {summaryData.originalText}
                      </p>
                    ) : (
                      <p className="text-muted-foreground">원문 정보가 없습니다.</p>
                    )}
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>

            {summaryData.personalNote && (
              <Card className="mb-6">
                <CardContent className="p-6">
                  <h2 className="text-xl font-bold mb-4">내 요약</h2>
                  <p className="whitespace-pre-wrap text-muted-foreground">{summaryData.personalNote}</p>
                </CardContent>
              </Card>
            )}
          </div>

          <div>
            <Card>
              <CardContent className="p-6">
                <h3 className="text-lg font-medium mb-4">요약 공유하기</h3>
                <div className="space-y-3">
                  <Button variant="outline" className="w-full gap-2">
                    <Download size={16} />
                    PDF로 저장
                  </Button>
                  <Button variant="outline" className="w-full gap-2">
                    <Share2 size={16} />
                    링크 공유
                  </Button>
                  <Button variant="outline" className="w-full gap-2">
                    <Bookmark size={16} />
                    북마크 추가
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">더 많은 강의를 요약해 보세요</h2>
          <p className="text-muted-foreground mb-6">회원가입하고 모든 요약 기록을 저장하세요</p>
          <div className="flex justify-center gap-4">
            <Link href="/">
              <Button>새 동영상 요약하기</Button>
            </Link>
            <Link href="/login">
              <Button variant="outline">로그인</Button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
