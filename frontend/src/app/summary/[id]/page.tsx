"use client"

import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import Link from "next/link"
import Image from "next/image"
import { Textarea } from "@/components/ui/textarea"
import { useAuth } from "@/hooks/use-auth"

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

  const { user, isLoading } = useAuth()
  const [summaryData, setSummaryData] = useState<SummaryData | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [note, setNote] = useState<string>("")

  const [isEditingTitle, setIsEditingTitle] = useState(false)
  const [editedTitle, setEditedTitle] = useState(summaryData?.title || "")


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
        setNote(data.personalNote || "")
      } catch (err) {
        setError(err instanceof Error ? err.message : String(err))
      }
    }
    fetchSummary()
  }, [id])

  useEffect(() => {
    if (summaryData?.title) {
      setEditedTitle(summaryData.title)
    }
  }, [summaryData?.title])


  const handleNoteSave = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/member-lectures/${id}/my-note`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ note }),
      })
      if (!res.ok) throw new Error("메모 저장 실패")
      alert("메모가 저장되었습니다.")
    } catch (err) {
      alert("메모 저장 중 오류가 발생했습니다.")
    }
  }

  const handleNoteDelete = async () => {
    const confirmed = window.confirm("정말 메모를 삭제하시겠습니까?");
    if (!confirmed) return;

    try {
      const res = await fetch(`http://localhost:8080/api/member-lectures/${id}/my-note`, {
        method: "DELETE",
        credentials: "include",
      });
      if (!res.ok) throw new Error("메모 삭제 실패");
      setNote(""); // 상태 초기화
      alert("메모가 삭제되었습니다.");
    } catch (err) {
      alert("메모 삭제 중 오류가 발생했습니다.");
    }
  }

  const handleTitleSave = async () => {
    setIsEditingTitle(false)

    if (editedTitle === summaryData?.title) return // 변경사항 없을 때 무시

    try {
      const res = await fetch(`http://localhost:8080/api/member-lectures/${id}/title`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ title: editedTitle }),
      })

      if (!res.ok) throw new Error("제목 저장 실패")

      setSummaryData((prev) => prev ? { ...prev, title: editedTitle } : prev)
      alert("제목이 저장되었습니다.")
    } catch (err) {
      alert("제목 저장 중 오류가 발생했습니다.")
    }
  }

  if (error) return <div className="container mx-auto px-4 py-12 text-red-500">{error}</div>
  if (!summaryData) return <div className="container mx-auto px-4 py-12">로딩 중...</div>

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <Link href="/" className="text-primary hover:underline mb-4 inline-block">
            ← 홈으로 돌아가기
          </Link>
          <div className="flex items-center justify-between gap-4 mb-4">
            <h1
              className="text-3xl font-bold tracking-tight mb-2 cursor-pointer transition-colors hover:text-zinc-600"
              onClick={() => setIsEditingTitle(true)}
            >
              {summaryData.title ?? "제목 없음"}
            </h1>
          </div>
          {isEditingTitle && (
            <div className="absolute top-20 left-1/2 -translate-x-1/2 z-50">
              <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-3x1 mx-4">
                <h2 className="text-lg font-bold mb-4">제목 수정</h2>
                <input
                  type="text"
                  className="w-full border rounded px-3 py-2 mb-6"
                  value={editedTitle}
                  onChange={(e) => setEditedTitle(e.target.value)}
                  autoFocus
                />
                <div className="flex justify-end gap-2">
                  <Button variant="secondary" onClick={() => setIsEditingTitle(false)}>취소</Button>
                  <Button onClick={handleTitleSave}>저장</Button>
                </div>
              </div>
            </div>
          )}



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

            {user && (
              <Card className="mb-6">
                <CardContent className="p-6">
                  <h2 className="text-xl font-bold mb-4">메모</h2>
                  <Textarea
                    className="w-full min-h-[100px] mb-4"
                    placeholder="메모할 내용을 작성하세요."
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                  />
                  <div className="flex justify-end gap-2">
                    <Button variant="secondary" onClick={handleNoteSave}>메모 저장</Button>
                    <Button variant="outline" className="text-destructive border-destructive hover:bg-destructive hover:text-white" 
                    onClick={handleNoteDelete}>메모 삭제</Button>
                  </div>
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

        {/* 로그인하지 않은 경우에만 안내/로그인 섹션 표시 */}
        {!isLoading && !user && (
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
        )}
      </div>
    </div>
  )
}