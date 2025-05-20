"use client"

import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import Link from "next/link"
import Image from "next/image"

// This is a demo page that would normally fetch data based on the ID
export default function Page() {
  const params = useParams()
  const { id } = params

  const [summaryData, setSummaryData] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    async function fetchSummary() {
      try {
        const baseUrl = process.env.NEXT_PUBLIC_API_URL;
        const res = await fetch(`${baseUrl}/api/member-lectures/${id}`, {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        })

        if (!res.ok) {
          throw new Error("요약 데이터를 가져오는 데 실패했습니다.")
        }

        const data = await res.json()
        setSummaryData(data)
      } catch (err) {
        setError(err.message)
      }
    }

    fetchSummary()
  }, [id])

  if (error) {
    return <div className="container mx-auto px-4 py-12 text-red-500">{error}</div>
  }

  if (!summaryData) {
    return <div className="container mx-auto px-4 py-12">로딩 중...</div>
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <Link href="/" className="text-primary hover:underline mb-4 inline-block">
            ← 홈으로 돌아가기
          </Link>
          <h1 className="text-3xl font-bold tracking-tight mb-2">{summaryData?.title ?? "제목 없음"}</h1>
          <p className="text-muted-foreground">
            동영상 길이: {summaryData?.duration ?? "정보 없음"} • 업로드: {summaryData?.uploadDate ?? "정보 없음"}
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          <div className="md:col-span-2">
            <Image
              src={summaryData?.thumbnailUrl ?? "/placeholder.svg"}
              width={500}
              height={300}
              alt={summaryData?.title ?? "썸네일"}
              className="rounded-lg w-full object-cover aspect-video mb-4"
            />
            <div className="space-y-6">
              {summaryData?.originalText && (
                <Card>
                  <CardContent className="p-6">
                    <h2 className="text-xl font-bold mb-4">원문 텍스트</h2>
                    <p className="whitespace-pre-wrap text-muted-foreground">{summaryData.originalText}</p>
                  </CardContent>
                </Card>
              )}
              {summaryData?.aiSummary && (
                <Card>
                  <CardContent className="p-6">
                    <h2 className="text-xl font-bold mb-4">AI 요약</h2>
                    <p className="whitespace-pre-wrap text-muted-foreground">{summaryData.aiSummary}</p>
                  </CardContent>
                </Card>
              )}
              {summaryData?.personalNote && (
                <Card>
                  <CardContent className="p-6">
                    <h2 className="text-xl font-bold mb-4">내 요약</h2>
                    <p className="whitespace-pre-wrap text-muted-foreground">{summaryData.personalNote}</p>
                  </CardContent>
                </Card>
              )}
            </div>
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

        <Tabs defaultValue="key" className="mb-12">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="key">핵심 요약</TabsTrigger>
            <TabsTrigger value="full">전체 요약</TabsTrigger>
          </TabsList>
          <TabsContent value="key" className="mt-6">
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-bold mb-4">핵심 포인트</h2>
                {summaryData.keySummary?.length > 0 ? (
                  <ul className="space-y-3">
                    {summaryData.keySummary.map((point, index) => (
                      <li key={index} className="flex items-start gap-2">
                        <span className="bg-primary text-primary-foreground rounded-full w-6 h-6 flex items-center justify-center text-sm shrink-0 mt-0.5">
                          {index + 1}
                        </span>
                        <span>{point}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-muted-foreground">핵심 요약 정보가 없습니다.</p>
                )}
              </CardContent>
            </Card>
          </TabsContent>
          <TabsContent value="full" className="mt-6">
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-bold mb-4">전체 요약</h2>
                {summaryData.fullSummary?.length > 0 ? (
                  <div className="space-y-6">
                    {summaryData.fullSummary.map((section, index) => (
                      <div key={index}>
                        <h3 className="text-lg font-medium mb-2">{section.title}</h3>
                        <p className="text-muted-foreground">{section.content}</p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-muted-foreground">전체 요약 정보가 없습니다.</p>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

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
