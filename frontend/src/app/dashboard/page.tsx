"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Clock, FileText, Upload } from "lucide-react"
import Link from "next/link"
import Image from "next/image"

export default function DashboardPage() {
  const [lectures, setLectures] = useState([])

  useEffect(() => {
    async function fetchLectures() {
      const res = await fetch("http://localhost:8080/api/member-lectures/dashboard", {
        method: "GET",
        credentials: "include",
        cache: "no-store",
      })

      if (res.ok) {
        const data = await res.json()
        setLectures(data)
      } else {
        setLectures([])
      }
    }

    fetchLectures()
  }, [])

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
              <h3 className="text-2xl font-bold mb-1">{lectures.length}</h3>
              <p className="text-sm text-muted-foreground">요약한 동영상</p>
            </CardContent>
          </Card>

          <Card className="col-span-1">
            <CardContent className="p-6 flex flex-col items-center text-center">
              <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center mb-4">
                <Clock className="h-10 w-10 text-primary" />
              </div>
              <h3 className="text-2xl font-bold mb-1">0시간 0분</h3>
              <p className="text-sm text-muted-foreground">절약한 시간</p>
            </CardContent>
          </Card>

          <Card className="md:col-span-2">
            <CardContent className="p-6">
              <h3 className="text-lg font-medium mb-4">계정 정보</h3>
              <p className="text-muted-foreground">로그인된 사용자 정보가 여기에 표시됩니다.</p>
              <div className="mt-4 pt-4 border-t">
                <Button
                  size="sm"
                  className="w-full bg-background text-foreground border border-border hover:bg-muted"
                >
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
            {lectures.length > 0 ? (
              <ul className="space-y-2">
                {lectures.map((lecture: any) => (
                  <li key={lecture.lectureId}>
                    <Link href={`/summary/${lecture.lectureId}`} className="text-primary hover:underline">
                      {lecture.title}
                    </Link>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-muted-foreground text-center">요약한 동영상이 없습니다.</p>
            )}
          </TabsContent>
          <TabsContent value="all" className="mt-6">
            {lectures.length > 0 ? (
              <ul className="space-y-2">
                {lectures.map((lecture: any) => (
                  <li key={lecture.lectureId}>
                    <Link href={`/summary/${lecture.lectureId}`} className="text-primary hover:underline">
                      {lecture.title}
                    </Link>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-muted-foreground text-center">요약한 동영상이 없습니다.</p>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}
