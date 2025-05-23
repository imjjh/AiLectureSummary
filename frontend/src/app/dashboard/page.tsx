"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Clock, FileText, Upload } from "lucide-react"
import Link from "next/link"
import Image from "next/image"
import axios from "axios"
axios.defaults.withCredentials = true;
type Lecture = {
  lectureId: number;
  title: string;
  createdAt: string;
  duration: string;
  thumbnailUrl?: string;
  email: string;
  username: string;
};

type User = {
  email: string;
  username: string;
};

export default function DashboardPage() {
  // 로그인한 사용자 정보를 저장하는 상태
  const [user, setUser] = useState<User | null>(null);
  // 강의 목록을 저장하는 상태
  const [lectures, setLectures] = useState<Lecture[]>([]);
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;
  // 총 절약한 시간을 저장하는 상태
  const [totalDuration, setTotalDuration] = useState<number>(0);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    async function fetchLectures() {
      try {
        const res = await axios.get(`${API_BASE_URL}/api/member-lectures/dashboard`, {
          withCredentials: true,
        });
        setLectures(res.data.data.items); // 강의 목록
        setTotalDuration(res.data.data.totalDuration); // 총 절약한 시간
      } catch (error) {
        console.error("강의 목록 불러오기 실패", error);
        setLectures([]);
        setTotalDuration(0);
      }
    }

    async function fetchUserInfo() {
      try {
        const res = await axios.get(`${API_BASE_URL}/api/members/me`, {
          withCredentials: true,
        });
        setUser(res.data.data); // 사용자 정보
      } catch (error) {
        console.error("사용자 정보 불러오기 실패", error);
      } finally {
        setIsLoading(false);
      }
    }

    fetchUserInfo();
    fetchLectures();
  }, []);

  // 강의 삭제 처리 함수
  const handleDelete = (lectureId: number) => {
    const updated = lectures.filter((lecture) => lecture.lectureId !== lectureId);
    setLectures(updated);
    // 서버에도 삭제 요청하고 싶다면 여기에 fetch 추가
  };

  // 총 절약한 시간(초)을 "X시간 Y분" 형식으로 변환하는 헬퍼 함수
  const getTotalDurationText = () => {
    const hours = Math.floor(totalDuration / 3600);
    const minutes = Math.floor((totalDuration % 3600) / 60);
    return `${hours}시간 ${minutes}분`;
  };

  //로딩중 흰배경 출력
  if (isLoading) {
    return (
      <div className="w-full h-screen bg-white" />
    );
  }

  if (!user && !isLoading) {
    return (
      <div className="container mx-auto px-4 py-12 text-center">
        <h1 className="text-2xl font-bold mb-4">로그인이 필요합니다</h1>
        <p className="text-muted-foreground mb-6">로그인하고 모든 서비스를 이용해 보세요!</p>
        <Button onClick={() => router.push("/login")}>로그인 하러가기</Button>
      </div>
    );
  }

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
              <h3 className="text-2xl font-bold mb-1">{getTotalDurationText()}</h3>
              <p className="text-sm text-muted-foreground">절약한 시간</p>
            </CardContent>
          </Card>

          <Card className="md:col-span-2">
            <CardContent className="p-6">
              <h3 className="text-lg font-medium mb-4">계정 정보</h3>
              {user ? (
                <div className="text-sm text-muted-foreground space-y-1">
                  <p><strong>이메일:</strong> {user.email}</p>
                  <p><strong>닉네임:</strong> {user.username}</p>
                </div>
              ) : (
                <p className="text-muted-foreground">로그인된 사용자 정보가 없습니다.</p>
              )}
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

          {/* 최근 요약 */}
          <TabsContent value="recent" className="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {lectures.length > 0 ? (
              lectures.slice(0, 2).map((lecture: any) => (
                <Card key={lecture.lectureId} className="relative hover:shadow-lg transition-shadow">
                  <Button
                    variant="ghost"
                    size="sm"
                    className="absolute top-2 right-2 text-xs text-muted-foreground hover:text-destructive"
                    onClick={() => handleDelete(lecture.lectureId)}
                  >
                    X
                  </Button>
                  <Link href={`/summary/${lecture.lectureId}`}>
                    <CardContent className="p-4">
                      <div className="aspect-video bg-muted rounded mb-3 overflow-hidden">
                        <Image
                          src={lecture.thumbnailUrl || "/images/1.png"}
                          alt="썸네일"
                          width={320}
                          height={180}
                          className="w-full h-full object-cover"
                        />
                      </div>
                      <div className="text-base font-medium truncate">{lecture.title}</div>
                      <div className="text-sm text-muted-foreground">{lecture.createdAt}</div>
                      <div className="text-sm text-muted-foreground">{lecture.duration}</div>
                    </CardContent>
                  </Link>
                </Card>
              ))
            ) : (
              <p className="text-muted-foreground text-center col-span-full">요약한 동영상이 없습니다.</p>
            )}
          </TabsContent>

          {/* 모든 요약 */}
          <TabsContent value="all" className="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {lectures.length > 0 ? (
              lectures.map((lecture: any) => (
                <Card key={lecture.lectureId} className="relative hover:shadow-lg transition-shadow">
                  <Button
                    variant="ghost"
                    size="sm"
                    className="absolute top-2 right-2 text-xs text-muted-foreground hover:text-destructive"
                    onClick={() => handleDelete(lecture.lectureId)}
                  >
                    X
                  </Button>
                  <Link href={`/summary/${lecture.lectureId}`}>
                    <CardContent className="p-4">
                      <div className="aspect-video bg-muted rounded mb-3 overflow-hidden">
                        <Image
                          src={lecture.thumbnailUrl || "/placeholder.png"}
                          alt="썸네일"
                          width={320}
                          height={180}
                          className="w-full h-full object-cover"
                        />
                      </div>
                      <div className="text-base font-medium truncate">{lecture.title}</div>
                      <div className="text-sm text-muted-foreground">{lecture.createdAt}</div>
                      <div className="text-sm text-muted-foreground">{lecture.duration}</div>
                    </CardContent>
                  </Link>
                </Card>
              ))
            ) : (
              <p className="text-muted-foreground text-center col-span-full">요약한 동영상이 없습니다.</p>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}