"use client"
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"
import { useAuth } from "@/hooks/use-auth"
import { toast } from "@/hooks/use-toast"
import Link from "next/link"

interface SummaryData {
  title: string
  summary: string
  originalText: string
  duration: string
  filename: string
  timestamp: string
}

export default function DemoSummaryPage() {
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
    const parseQueryParams = () => {
      const searchParams = new URLSearchParams(window.location.search)
      const encodedData = searchParams.get('data')
      
      if (encodedData) {
        try {
          const decodedData = decodeURIComponent(encodedData)
          console.log("디코딩된 데이터:", decodedData) // 디버깅용
          const result = JSON.parse(decodedData)
          console.log("파싱된 결과:", result) // 디버깅용
          
          setData({
            title: result.title || "제목 없음",
            summary: result.summary || "",
            originalText: result.originalText || "",
            // 수정된 부분: duration이 문자열인지 숫자인지 확인
            duration: typeof result.duration === 'string' 
              ? result.duration // 문자열이면 그대로 사용
              : result.duration 
                ? formatDuration(result.duration) // 숫자면 포맷팅
                : "0:00",
            filename: result.filename || "파일 없음",
            timestamp: result.timestamp 
              ? new Date(result.timestamp).toLocaleString('ko-KR', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit'
                })
              : "시간 정보 없음"
          })
        } catch (error) {
          console.error('데이터 파싱 오류:', error)
          router.push('/error')
        }
      }
    }

    const formatDuration = (seconds: number) => {
      if (isNaN(seconds)) return "0:00"
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = Math.floor(seconds % 60)
      return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
    }

    parseQueryParams()
  }, [router])

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <Link href="/" className="text-blue-600 hover:underline mb-4 inline-block">
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

                <div className="space-y-2 mt-6">
                  <h3 className="text-lg font-semibold">내 메모</h3>
                  {lastUpdated && (
                    <p className="text-sm text-muted-foreground">
                      마지막 저장: {new Date(lastUpdated).toLocaleString("ko-KR")}
                    </p>
                  )}
                  <Textarea
                    placeholder="요약을 보고 생각난 내용을 자유롭게 기록하세요."
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                    className="min-h-[120px] focus-visible:ring-0 focus-visible:ring-transparent focus:outline-none"
                  />
                  <div className="flex justify-end gap-2">
                    <Button
                      onClick={handleSaveNote}
                      className="px-6 bg-muted text-foreground hover:bg-gray-200 active:scale-95 transition-all"
                    >
                      메모 저장
                    </Button>
                    <Button
                      onClick={handleDeleteNote}
                      className="px-6 bg-muted text-foreground hover:bg-gray-200 active:scale-95 transition-all"
                    >
                      삭제
                    </Button>
                  </div>
                </div>

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
            <Link href="/upload">
              <button className="bg-blue-600 text-white px-6 py-2 rounded-full hover:bg-blue-700 transition-colors">
                동영상 업로드
              </button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
