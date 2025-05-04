import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Clock, FileText, Upload } from "lucide-react"
import Link from "next/link"
import Image from "next/image"

export default function DashboardPage() {
  // 이 데이터는 실제로는 데이터베이스에서 가져와야 합니다
  const summaries = [
    {
      id: "demo-result",
      title: "머신러닝 기초: 지도학습과 비지도학습",
      date: "2023년 11월 5일",
      duration: "52:18",
      thumbnail: "/placeholder.svg?height=200&width=300",
    },
    {
      id: "binary-tree",
      title: "데이터 구조와 알고리즘: 이진 트리의 이해",
      date: "2023년 10월 15일",
      duration: "45:32",
      thumbnail: "/placeholder.svg?height=200&width=300",
    },
    {
      id: "react-hooks",
      title: "React Hooks 완벽 가이드",
      date: "2023년 9월 22일",
      duration: "1:12:45",
      thumbnail: "/placeholder.svg?height=200&width=300",
    },
    {
      id: "python-basics",
      title: "파이썬 기초 프로그래밍",
      date: "2023년 8월 10일",
      duration: "38:21",
      thumbnail: "/placeholder.svg?height=200&width=300",
    },
  ]

  const recentSummaries = summaries.slice(0, 2)
  const allSummaries = summaries

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-6xl mx-auto">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-bold tracking-tight mb-1">마이 페이지</h1>
            <p className="text-muted-foreground">요약한 동영상 목록과 계정 정보를 관리하세요</p>
          </div>
          <Link href="/">
            <Button className="gap-2">
              <Upload size={16} />새 동영상 요약하기
            </Button>
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
          <Card className="col-span-1">
            <CardContent className="p-6 flex flex-col items-center text-center">
              <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center mb-4">
                <FileText className="h-10 w-10 text-primary" />
              </div>
              <h3 className="text-2xl font-bold mb-1">4</h3>
              <p className="text-sm text-muted-foreground">요약한 동영상</p>
            </CardContent>
          </Card>

          <Card className="col-span-1">
            <CardContent className="p-6 flex flex-col items-center text-center">
              <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center mb-4">
                <Clock className="h-10 w-10 text-primary" />
              </div>
              <h3 className="text-2xl font-bold mb-1">3시간 28분</h3>
              <p className="text-sm text-muted-foreground">절약한 시간</p>
            </CardContent>
          </Card>

          <Card className="md:col-span-2">
            <CardContent className="p-6">
              <h3 className="text-lg font-medium mb-4">계정 정보</h3>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">이름:</span>
                  <span className="font-medium">홍길동</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">이메일:</span>
                  <span className="font-medium">hong@example.com</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">가입일:</span>
                  <span className="font-medium">2023년 8월 1일</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">멤버십:</span>
                  <span className="font-medium">무료 회원</span>
                </div>
              </div>
              <div className="mt-4 pt-4 border-t">
                <Button variant="outline" size="sm" className="w-full">
                  계정 설정
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        <Tabs defaultValue="recent" className="mb-12">
          <TabsList className="grid w-full grid-cols-2 max-w-[400px]">
            <TabsTrigger value="recent">최근 요약</TabsTrigger>
            <TabsTrigger value="all">모든 요약</TabsTrigger>
          </TabsList>
          <TabsContent value="recent" className="mt-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {recentSummaries.map((summary) => (
                <Link href={`/summary/${summary.id}`} key={summary.id}>
                  <Card className="overflow-hidden hover:shadow-md transition-shadow">
                    <Image
                      src={summary.thumbnail || "/placeholder.svg"}
                      width={300}
                      height={200}
                      alt={summary.title}
                      className="w-full object-cover aspect-video"
                    />
                    <CardContent className="p-4">
                      <h3 className="font-medium line-clamp-1 mb-1">{summary.title}</h3>
                      <div className="flex justify-between text-sm text-muted-foreground">
                        <span>{summary.date}</span>
                        <span>{summary.duration}</span>
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </div>
          </TabsContent>
          <TabsContent value="all" className="mt-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {allSummaries.map((summary) => (
                <Link href={`/summary/${summary.id}`} key={summary.id}>
                  <Card className="overflow-hidden hover:shadow-md transition-shadow">
                    <Image
                      src={summary.thumbnail || "/placeholder.svg"}
                      width={300}
                      height={200}
                      alt={summary.title}
                      className="w-full object-cover aspect-video"
                    />
                    <CardContent className="p-4">
                      <h3 className="font-medium line-clamp-1 mb-1">{summary.title}</h3>
                      <div className="flex justify-between text-sm text-muted-foreground">
                        <span>{summary.date}</span>
                        <span>{summary.duration}</span>
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}
