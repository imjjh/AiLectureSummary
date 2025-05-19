"use client"
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useAuth } from "@/hooks/use-auth"
import { toast } from "@/hooks/use-toast"
import Link from "next/link"
import { NoteEditor } from "@/components/NoteEditor"

interface SummaryData {
  title: string
  summary: string
  originalText: string
  duration: string
  filename: string
  timestamp: string
}

export default function SummaryPage({ params }: { params: { id: string } }) {
  const lectureId = params.id
  const router = useRouter()
  const [note, setNote] = useState("")
  const { user } = useAuth()
  const [lastUpdated, setLastUpdated] = useState<string | null>(null)
  const [data, setData] = useState<SummaryData>({
    title: "제목 없음",
    summary: "",
    originalText: "",
    duration: "0:00",
    filename: "파일 없음",
    timestamp: "시간 정보 없음"
  })

  // duration 포맷 함수
  const formatDuration = (seconds: number): string => {
    if (isNaN(seconds)) return "0:00"
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = Math.floor(seconds % 60)
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
  }
  
  useEffect(() => {
    if (!data.filename || data.filename === "파일 없음") return

    const saved = localStorage.getItem(`note-${data.filename}`)
      if (saved) {
        try {
          const parsed = JSON.parse(saved)
          setNote(parsed.content || "")
          setLastUpdated(parsed.updatedAt || null)
        } catch {
          setNote(saved) // fallback for 예전 구조
        }
      }
    }, [data.filename])

    const handleSaveNote = () => {
      if (!user) {
        toast({
          title: "로그인 후 이용 가능합니다",
          description: "메모를 저장하려면 로그인해주세요.",
          variant: "destructive", // 또는 기본으로 바꿔도 됨
        })
        return
      }

      if (!data.filename || data.filename === "파일 없음") {
        toast({
          title: "파일 정보 없음",
          description: "먼저 동영상을 업로드한 후 메모를 저장할 수 있습니다.",
          variant: "destructive",
        })
        return
      }

      const now = new Date().toISOString()
      const storageKey = `note-${data.filename}`
      const saveData = {
        content: note,
        updatedAt: now,
      }

      localStorage.setItem(storageKey, JSON.stringify(saveData))
      setLastUpdated(now)

      toast({
        title: "저장 완료",
        description: "메모가 저장되었습니다.",
      })
    }

    const handleDeleteNote = () => {
      const storageKey = `note-${data.filename}`
      localStorage.removeItem(storageKey)
      setNote("")
      setLastUpdated(null)

      toast({
        title: "삭제 완료",
        description: "메모가 삭제되었습니다.",
      })
    }

  useEffect(() => {
    const searchParams = new URLSearchParams(window.location.search)
    const id = searchParams.get('id') // lectureId

    if (!id) {
      router.push('/error') // ID 없으면 에러 페이지로
      return
    }

    const fetchSummary = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/summary/${lectureId}`, {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        })

        if (!res.ok){
          const errorText = await res.text()
          console.error("🚨 API 요청 실패 상태:", res.status)
          console.error("🚨 API 응답 본문:", errorText)
          return
        }

        const result = await res.json()

        setData({
          title: result.title || "제목 없음",
          summary: result.summary || "",
          originalText: result.originalText || "",
          duration: formatDuration(result.duration || 0),
          filename: result.filename || "파일 없음",
          timestamp: result.createdAt 
            ? new Date(result.createdAt).toLocaleString('ko-KR', {
                year: 'numeric', month: 'long', day: 'numeric',
                hour: '2-digit', minute: '2-digit'
              })
            : "시간 정보 없음"
        })
      } catch (err) {
        console.error("요약 데이터 불러오기 실패", err)
      }
    }

    fetchSummary()
  }, [lectureId, router])


  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <Link href="/" className="text-primary hover:underline mb-4 inline-block">
            ← 홈으로 돌아가기
          </Link>
          <h1 className="text-3xl font-bold tracking-tight mb-2">{data.title}</h1>
          <div className="text-gray-500 space-y-1">
            <p>파일명: {data.filename}</p>
            <p>동영상 길이: {data.duration} • 처리 시간: {data.timestamp}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          <div className="md:col-span-2">
            <div className="aspect-video bg-gray-100 rounded-lg mb-4 flex items-center justify-center">
              <span className="text-gray-400">동영상 미리보기</span>
            </div>
          </div>
          <div>
            <Card>
              <CardContent className="p-6">
                <h3 className="text-lg font-medium mb-4">기능 메뉴</h3>
                <div className="space-y-3">
                  <button className="w-full flex items-center gap-2 p-2 rounded border hover:bg-gray-50">
                    <Download size={16} />
                    PDF 저장
                  </button>
                  <button className="w-full flex items-center gap-2 p-2 rounded border hover:bg-gray-50">
                    <Share2 size={16} />
                    공유하기
                  </button>
                  <button className="w-full flex items-center gap-2 p-2 rounded border hover:bg-gray-50">
                    <Bookmark size={16} />
                    북마크
                  </button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        <Tabs defaultValue="summary" className="mb-12">
          <TabsList className="grid grid-cols-2 w-full">
            <TabsTrigger value="summary">AI 요약</TabsTrigger>
            <TabsTrigger value="original">원문 보기</TabsTrigger>
          </TabsList>

          <TabsContent value="summary">
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-bold mb-4">생성된 요약</h2>
                <div className="space-y-4">
                  {data.summary.split('\n').map((line, index) => (
                    <p key={index} className="text-gray-700">{line}</p>
                  ))}
                  {!data.summary && (
                    <p className="text-gray-400">요약 데이터가 없습니다.</p>
                  )}
                </div>

                <NoteEditor
                  note={note}
                  setNote={setNote}
                  lastUpdated={lastUpdated}
                  onSave={handleSaveNote}
                  onDelete={handleDeleteNote}
                />

              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="original">
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-bold mb-4">원본 텍스트</h2>
                <div className="whitespace-pre-wrap bg-gray-50 p-4 rounded">
                  {data.originalText || (
                    <span className="text-gray-400">원문 데이터가 없습니다.</span>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">새로운 요약 생성하기</h2>
          <div className="flex justify-center gap-4">
            <Link href="/">
              <Button>새 동영상 요약하기</Button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
