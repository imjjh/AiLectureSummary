"use client"

import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download } from "lucide-react"
import Link from "next/link"
import Image from "next/image"
import { Textarea } from "@/components/ui/textarea"
import { useAuth } from "@/hooks/use-auth"


const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL;

interface SummaryData {
  customTitle?: string
  duration?: number
  thumbnailBase64?: string
  aiSummary?: string
  originalText?: string
  memo?: string
  enrolledAt?: string
}

export default function Page() {
  // URL 파라미터에서 id 추출
  const params = useParams()
  const { id } = params as { id: string }

  // 사용자 인증 상태 가져오기
  const { user, isLoading } = useAuth()
  // 요약 데이터 상태
  const [summaryData, setSummaryData] = useState<SummaryData | null>(null)
  // 에러 상태
  const [error, setError] = useState<string | null>(null)
  const [imgSrc, setImgSrc] = useState<string>("/images/1.png")
  // 개인 메모 상태
  const [memo, setMemo] = useState<string>("")

  // 제목 편집 모드 상태 및 편집 중인 제목 상태
  const [isEditingTitle, setIsEditingTitle] = useState(false)
  const [editedTitle, setEditedTitle] = useState(summaryData?.customTitle || "")

  const formatDate = (isoString: string): string => {
    const date = new Date(isoString);
    return date.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };


  // 요약 데이터 API 호출 및 초기 데이터 설정
  useEffect(() => {
    async function fetchSummary() {
      try {
        const res = await fetch(`${API_BASE_URL}/api/member-lectures/${id}`, {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        })

        if (!res.ok) {
          const errorText = await res.text();
          console.error("요약 요청 실패:", res.status, errorText);
          throw new Error("요약 데이터를 가져오는 데 실패했습니다.")
        }

        const data = await res.json()
        setSummaryData(data.data)
        setMemo(data.data.memo || "")
      } catch (err) {
        setError(err instanceof Error ? err.message : String(err))
      }
    }
    if (id) fetchSummary()
  }, [id])

  // summaryData.customTitle 변경 시 편집 중인 제목도 업데이트
  useEffect(() => {
    if (summaryData?.customTitle) {
      setEditedTitle(summaryData.customTitle)
    }
  }, [summaryData?.customTitle])

  useEffect(() => {
    if (summaryData?.thumbnailBase64) {
      setImgSrc(`data:image/png;base64,${summaryData.thumbnailBase64}`)
    } else {
      setImgSrc("/images/1.png")
    }
  }, [summaryData?.thumbnailBase64])


  // 메모 저장 함수
  const handleNoteSave = async () => {
    if (!memo.trim()) {
      alert("메모 내용을 작성해주세요.");
      return;
    }

    try {
      const res = await fetch(`${API_BASE_URL}/api/member-lectures/${id}/memo`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ memo }),
      })
      if (!res.ok) throw new Error("메모 저장 실패")
      alert("메모가 저장되었습니다.")
    } catch (err) {
      alert("메모 저장 중 오류가 발생했습니다.")
    }
  }

  // 메모 삭제 함수
  const handleNoteDelete = async () => {
    const confirmed = window.confirm("정말 메모를 삭제하시겠습니까?");
    if (!confirmed) return;

    try {
      const res = await fetch(`${API_BASE_URL}/api/member-lectures/${id}/memo`, {
        method: "DELETE",
        credentials: "include",
      });
      if (!res.ok) throw new Error("메모 삭제 실패");
      setMemo("");
      alert("메모가 삭제되었습니다.");
    } catch (err) {
      alert("메모 삭제 중 오류가 발생했습니다.");
    }
  }

  // 제목 저장 함수
  const handleTitleSave = async () => {
    setIsEditingTitle(false)
    if (editedTitle === summaryData?.customTitle) return

    try {
      const res = await fetch(`${API_BASE_URL}/api/member-lectures/${id}/title`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ title: editedTitle }),
      })

      if (!res.ok) throw new Error("제목 저장 실패")

      setSummaryData((prev) => prev ? { ...prev, customTitle: editedTitle } : prev)
      alert("제목이 저장되었습니다.")
    } catch (err) {
      alert("제목 저장 중 오류가 발생했습니다.")
    }
  }

  const formatDuration = (durationInSeconds: number): string => {
    const hours = Math.floor(durationInSeconds / 3600);
    const minutes = Math.floor((durationInSeconds % 3600) / 60);
    const seconds = durationInSeconds % 60;

    if (hours > 0) {
      return `${hours}시간 ${minutes}분 ${seconds}초`;
    } else if (minutes > 0) {
      return `${minutes}분 ${seconds}초`;
    } else {
      return `${seconds}초`;
    }
  };

  // 에러 발생 시 에러 메시지 표시
  if (error) return <div className="container mx-auto px-4 py-12 text-red-500">{error}</div>
  // 데이터 로딩 중일 때 표시
  if (!summaryData) return <div className="container mx-auto px-4 py-12">로딩 중...</div>

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        {/* 상단 네비게이션 및 제목 영역 */}
        <div className="mb-8">
          <Link href="/" className="text-primary hover:underline mb-4 inline-block">
            ← 홈으로 돌아가기
          </Link>
          <div className="flex items-center justify-between gap-4 mb-4">
            {/* 제목 클릭 시 편집 모드 활성화 */}
            <h1
              className="text-3xl font-bold tracking-tight mb-2 cursor-pointer transition-colors hover:text-zinc-600"
              onClick={() => setIsEditingTitle(true)}
            >
              {summaryData.customTitle ?? "제목 없음"}
            </h1>
          </div>
          {/* 제목 편집 모달 */}
          {isEditingTitle && (
            <div className="absolute top-20 left-1/2 -translate-x-1/2 z-50">
              <div className="bg-white dark:bg-zinc-800 text-black dark:text-white rounded-lg shadow-lg p-6 w-full max-w-3xl mx-4">
                <h2 className="text-lg font-bold mb-4">제목 수정</h2>
                <input
                  type="text"
                  className="w-full border border-gray-300 dark:border-zinc-700 rounded px-3 py-2 mb-6 bg-white dark:bg-zinc-800 text-black dark:text-white"
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

          {/* 동영상 길이 및 업로드 날짜 표시 */}
          <p className="text-muted-foreground">
            동영상 길이: {" "}
            {typeof summaryData.duration === "number"
              ? formatDuration(summaryData.duration) : "정보 없음"}{" "}
            • 업로드: {summaryData.enrolledAt ? formatDate(summaryData.enrolledAt) : "정보 없음"}
          </p>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          {/* 왼쪽 주요 콘텐츠 영역 */}
          <div className="md:col-span-2">
            {/* 썸네일 이미지 */}
            <Image
              src={imgSrc}
              width={500}
              height={300}
              alt={summaryData.customTitle ?? "썸네일"}
              className="rounded-lg w-full object-cover aspect-video mb-4"
              onError={() => setImgSrc("/images/1.png")}
            />

            {/* 탭 컴포넌트: AI 요약 / 원문 */}
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

            {/* 로그인된 사용자만 메모 작성 및 저장 가능 */}
            {user && (
              <Card className="mb-6">
                <CardContent className="p-6">
                  <h2 className="text-xl font-bold mb-4">메모</h2>
                  <Textarea
                    className="w-full min-h-[100px] mb-4"
                    placeholder="메모할 내용을 작성하세요."
                    value={memo}
                    onChange={(e) => setMemo(e.target.value)}
                  />
                  <div className="flex justify-end gap-2">
                    <Button variant="secondary"
                      className="dark:hover:bg-zinc-600 hover:bg-zinc-300"
                      onClick={handleNoteSave}>메모 저장</Button>
                    <Button
                      variant="secondary"
                      className="hover:bg-destructive hover:text-white dark:hover:bg-destructive"
                      onClick={handleNoteDelete}
                    >
                      메모 삭제
                    </Button>
                  </div>
                </CardContent>
              </Card>
            )}
          </div>


          {/* 오른쪽 사이드바: 공유 기능 */}
          <div className="md:col-span-1">
            <Card>
              <CardContent className="p-6">
                <h3 className="text-lg font-medium mb-4">요약 저장하기</h3>
                <div className="mt-4 flex justify-center">
                  <Button
                    className="w-full max-w-xs gap-2 border border-border bg-muted hover:bg-zinc-300 text-foreground dark:bg-zinc-800 dark:hover:bg-zinc-700 dark:text-white"
                    onClick={async () => {
                      try {
                        const res = await fetch(`${API_BASE_URL}/api/member-lectures/${id}/pdf`, {
                          credentials: "include",
                        });

                        if (res.status === 404) {
                          alert("해당 강의의 PDF 파일을 찾을 수 없습니다.");
                          return;
                        }

                        if (!res.ok) {
                          alert("PDF 다운로드에 실패했습니다.");
                          return;
                        }

                        const blob = await res.blob();

                        // PDF MIME 타입 확인
                        if (blob.type !== "application/pdf") {
                          alert("서버에서 올바른 PDF 파일이 도착하지 않았습니다.");
                          return;
                        }

                        // PDF 다운로드 처리
                        const url = window.URL.createObjectURL(blob);
                        const link = document.createElement("a");
                        link.href = url;
                        link.download = `${summaryData?.customTitle || "summary"}.pdf`;
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link);
                        window.URL.revokeObjectURL(url);
                      } catch (err) {
                        console.error("PDF 다운로드 오류:", err);
                        alert("PDF 다운로드 중 오류가 발생했습니다.");
                      }
                    }}
                  >
                    <Download size={16} />
                    PDF로 저장
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* 비로그인 사용자에게 회원가입 및 로그인 유도 */}
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
  )
}