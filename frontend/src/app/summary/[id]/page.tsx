// @ts-nocheck
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import Link from "next/link"
import Image from "next/image"


// This is a demo page that would normally fetch data based on the ID
export default async function Page({ params }: { params: { id: string } }) {
  // In a real app, you would fetch the summary data based on the ID
  const summaryData = {
    id: params.id,
    title: "데이터 구조와 알고리즘: 이진 트리의 이해",
    duration: "45:32",
    thumbnailUrl: "/placeholder.svg?height=300&width=500",
    uploadDate: "2023년 10월 15일",
    keySummary: [
      "이진 트리는 각 노드가 최대 두 개의 자식을 가지는 트리 구조입니다.",
      "이진 탐색 트리(BST)는 왼쪽 자식이 부모보다 작고, 오른쪽 자식이 부모보다 큰 값을 가집니다.",
      "트리 순회 방법에는 전위(pre-order), 중위(in-order), 후위(post-order) 순회가 있습니다.",
      "균형 이진 트리는 검색, 삽입, 삭제 연산의 시간 복잡도를 O(log n)으로 유지합니다.",
      "AVL 트리와 레드-블랙 트리는 자동으로 균형을 유지하는 이진 탐색 트리입니다.",
    ],
    fullSummary: [
      {
        title: "이진 트리의 기본 개념",
        content:
          "이진 트리는 컴퓨터 과학에서 널리 사용되는 비선형 자료구조입니다. 각 노드는 최대 두 개의 자식 노드를 가질 수 있으며, 이를 왼쪽 자식과 오른쪽 자식이라고 합니다. 루트 노드에서 시작하여 자식 노드로 내려가는 구조를 가집니다.",
      },
      {
        title: "이진 탐색 트리(BST)",
        content:
          "이진 탐색 트리는 특별한 속성을 가진 이진 트리로, 모든 노드에 대해 왼쪽 서브트리의 모든 노드 값은 현재 노드의 값보다 작고, 오른쪽 서브트리의 모든 노드 값은 현재 노드의 값보다 큽니다. 이 속성 덕분에 이진 탐색 트리는 효율적인 검색, 삽입, 삭제 연산을 제공합니다.",
      },
      {
        title: "트리 순회 방법",
        content:
          "이진 트리를 순회하는 세 가지 주요 방법이 있습니다. 전위 순회(pre-order)는 노드를 방문한 후 왼쪽과 오른쪽 서브트리를 순회합니다. 중위 순회(in-order)는 왼쪽 서브트리를 순회한 후 노드를 방문하고 오른쪽 서브트리를 순회합니다. 후위 순회(post-order)는 왼쪽과 오른쪽 서브트리를 순회한 후 노드를 방문합니다.",
      },
      {
        title: "균형 이진 트리",
        content:
          "균형 이진 트리는 트리의 높이를 최소화하여 연산의 효율성을 보장합니다. 불균형 트리는 최악의 경우 O(n) 시간 복잡도를 가질 수 있지만, 균형 트리는 O(log n)의 시간 복잡도를 유지합니다. AVL 트리와 레드-블랙 트리는 자동으로 균형을 유지하는 대표적인 이진 탐색 트리입니다.",
      },
      {
        title: "실제 응용 사례",
        content:
          "이진 트리는 데이터베이스 인덱싱, 힙 메모리 할당, 허프만 코딩, 라우팅 테이블 등 다양한 분야에서 활용됩니다. 특히 이진 힙은 우선순위 큐를 구현하는 데 사용되며, 다익스트라 알고리즘과 같은 그래프 알고리즘에서 중요한 역할을 합니다.",
      },
    ],
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <Link href="/" className="text-primary hover:underline mb-4 inline-block">
            ← 홈으로 돌아가기
          </Link>
          <h1 className="text-3xl font-bold tracking-tight mb-2">{summaryData.title}</h1>
          <p className="text-muted-foreground">
            동영상 길이: {summaryData.duration} • 업로드: {summaryData.uploadDate}
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
          <div className="md:col-span-2">
            <Image
              src={summaryData.thumbnailUrl || "/placeholder.svg"}
              width={500}
              height={300}
              alt={summaryData.title}
              className="rounded-lg w-full object-cover aspect-video mb-4"
            />
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
              </CardContent>
            </Card>
          </TabsContent>
          <TabsContent value="full" className="mt-6">
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-bold mb-4">전체 요약</h2>
                <div className="space-y-6">
                  {summaryData.fullSummary.map((section, index) => (
                    <div key={index}>
                      <h3 className="text-lg font-medium mb-2">{section.title}</h3>
                      <p className="text-muted-foreground">{section.content}</p>
                    </div>
                  ))}
                </div>
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
