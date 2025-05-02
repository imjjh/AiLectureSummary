import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent } from "@/components/ui/card"
import { Download, Share2, Bookmark } from "lucide-react"
import Link from "next/link"
import Image from "next/image"

// This is a demo page with hardcoded data
export default function DemoSummaryPage() {
  const summaryData = {
    title: "머신러닝 기초: 지도학습과 비지도학습",
    duration: "52:18",
    thumbnailUrl: "/placeholder.svg?height=300&width=500",
    uploadDate: "2023년 11월 5일",
    keySummary: [
      "머신러닝은 컴퓨터가 명시적인 프로그래밍 없이 데이터로부터 학습하는 능력을 갖추게 하는 인공지능의 한 분야입니다.",
      "지도학습은 레이블이 있는 데이터를 사용하여 입력과 출력 간의 관계를 학습합니다. 분류와 회귀가 대표적인 예입니다.",
      "비지도학습은 레이블이 없는 데이터에서 패턴이나 구조를 찾습니다. 클러스터링과 차원 축소가 주요 기법입니다.",
      "모델 평가는 정확도, 정밀도, 재현율, F1 점수 등 다양한 지표를 사용합니다.",
      "과적합을 방지하기 위해 교차 검증, 정규화, 앙상블 방법 등을 사용합니다.",
    ],
    fullSummary: [
      {
        title: "머신러닝의 개념과 중요성",
        content:
          "머신러닝은 컴퓨터가 명시적인 프로그래밍 없이 데이터로부터 학습하는 능력을 갖추게 하는 인공지능의 한 분야입니다. 빅데이터 시대에 머신러닝의 중요성은 계속 증가하고 있으며, 이미지 인식, 자연어 처리, 추천 시스템 등 다양한 분야에서 활용되고 있습니다. 머신러닝은 데이터에서 패턴을 찾아 미래 결과를 예측하거나 의사결정을 지원합니다.",
      },
      {
        title: "지도학습(Supervised Learning)",
        content:
          "지도학습은 입력 데이터와 그에 대응하는 출력(레이블)이 함께 제공되는 학습 방법입니다. 알고리즘은 입력과 출력 간의 관계를 학습하여 새로운 입력 데이터에 대한 출력을 예측합니다. 지도학습의 주요 유형으로는 분류(Classification)와 회귀(Regression)가 있습니다. 분류는 이메일 스팸 필터링, 이미지 인식 등에 사용되며, 회귀는 주택 가격 예측, 기온 예측 등 연속적인 값을 예측하는 데 사용됩니다.",
      },
      {
        title: "비지도학습(Unsupervised Learning)",
        content:
          "비지도학습은 레이블이 없는 데이터에서 패턴이나 구조를 찾는 학습 방법입니다. 알고리즘은 데이터의 내재된 구조를 스스로 발견합니다. 주요 기법으로는 클러스터링(Clustering)과 차원 축소(Dimensionality Reduction)가 있습니다. 클러스터링은 유사한 특성을 가진 데이터를 그룹화하며, 고객 세분화, 이상 탐지 등에 활용됩니다. 차원 축소는 데이터의 중요한 특성을 유지하면서 차원을 줄여 시각화하거나 계산 효율성을 높이는 데 사용됩니다.",
      },
      {
        title: "모델 평가와 성능 지표",
        content:
          "머신러닝 모델의 성능을 평가하기 위해 다양한 지표가 사용됩니다. 분류 문제에서는 정확도(Accuracy), 정밀도(Precision), 재현율(Recall), F1 점수 등이 사용됩니다. 회귀 문제에서는 평균 제곱 오차(MSE), 평균 절대 오차(MAE) 등이 사용됩니다. 모델 평가를 위해 데이터를 훈련 세트와 테스트 세트로 나누는 것이 일반적이며, 교차 검증(Cross-validation)을 통해 모델의 일반화 능력을 평가합니다.",
      },
      {
        title: "과적합과 해결 방법",
        content:
          "과적합(Overfitting)은 모델이 훈련 데이터에 너무 잘 맞춰져 새로운 데이터에 대한 일반화 능력이 떨어지는 현상입니다. 이를 해결하기 위한 방법으로는 더 많은 훈련 데이터 사용, 모델 복잡도 감소, 정규화(Regularization) 기법 적용, 드롭아웃(Dropout), 앙상블 방법 등이 있습니다. 반대로 과소적합(Underfitting)은 모델이 데이터의 패턴을 충분히 학습하지 못한 상태로, 더 복잡한 모델을 사용하거나 더 많은 특성을 추가하여 해결할 수 있습니다.",
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
