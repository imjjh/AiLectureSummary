"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Clock, FileText, Upload, LayoutGrid, List } from "lucide-react"
import Link from "next/link"
import LectureCard from "@/components/lecture-card";
import LectureList from "@/components/lecture-list"
import { toast } from "@/hooks/use-toast"
import { customFetch } from "@/lib/fetch";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"

interface Lecture {
  lectureId: number;
  customTitle: string;
  title: string,
  duration: number;
  thumbnailUrl?: string;
  enrolledAt?: string;
  url?: string;
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
  const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL;
  // 총 절약한 시간을 저장하는 상태
  const [totalDuration, setTotalDuration] = useState<number>(0);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [targetLecture, setTargetLecture] = useState<Lecture | null>(null);
  const [viewMode, setViewMode] = useState<"card" | "list">("card")


  useEffect(() => {
    async function fetchLectures() {
      try {
        const res = await customFetch(`${API_BASE_URL}/api/member-lectures/dashboard`);
        if (!res.ok) throw new Error("강의 데이터 불러오기 실패");

        const json = await res.json();
        const fetchedData = json.data;
        const sortedLectures = fetchedData.items.sort(
          (a: Lecture, b: Lecture) => b.lectureId - a.lectureId
        );

        setLectures(sortedLectures);
        setTotalDuration(fetchedData.totalDuration);

      } catch (error) {
        console.error("강의 목록 불러오기 실패", error);
        setLectures([]);
        setTotalDuration(0);
      }
    }

    async function fetchUserInfo() {
      try {
        const res = await customFetch(`${API_BASE_URL}/api/members/me`);
        if (!res.ok) throw new Error("사용자 정보 불러오기 실패");

        const json = await res.json();
        setUser(json.data); // 사용자 정보
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
  const handleDelete = async (lecture: Lecture) => {
    try {
      const res = await customFetch(`${API_BASE_URL}/api/member-lectures/${lecture.lectureId}`, {
        method: "DELETE",
        credentials: "include",
      });

      if (!res.ok) {
        throw new Error("삭제 실패");
      }

      setLectures(prev => prev.filter(l => l.lectureId !== lecture.lectureId));
      setTotalDuration(prev => Math.max(0, prev - (lecture.duration ?? 0)));

      toast({
        title: "삭제 완료✔️",
        description: `"${lecture.customTitle}" 강의가 삭제되었습니다.`,
        duration: 1000,
      });
    } catch (err) {
      toast({
        title: "삭제 실패✖️",
        description: "강의 삭제 중 오류가 발생했습니다.",
        variant: "destructive",
        duration: 1000,
      });
      console.error(err);
    } finally {
      setIsDialogOpen(false);
      setTargetLecture(null);
    }
  };


  // 총 절약한 시간(초)을 "X시간 Y분" 형식으로 변환하는 헬퍼 함수
  const getTotalDurationText = () => {
    const hours = Math.floor(totalDuration / 3600);
    const minutes = Math.floor((totalDuration % 3600) / 60);
    if (hours === 0 && minutes === 0) return "0분";
    if (hours === 0) return `${minutes}분`;
    if (minutes === 0) return `${hours}시간`;
    return `${hours}시간 ${minutes}분`;
  };

  const openDeleteDialog = (lecture: Lecture) => {
    setTargetLecture(lecture);
    setIsDialogOpen(true);
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
            <p className="text-muted-foreground">요약한 강의 목록과 계정 정보를 관리하세요</p>
          </div>
          <Link href="/">
            <Button className="gap-2">
              <Upload size={16} />새 강의 요약하기
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
              <p className="text-sm text-muted-foreground">요약한 강의</p>
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
                <Link href="/accountedit">
                  <Button
                    size="sm"
                    className="w-full bg-background text-foreground border border-border hover:bg-muted"
                  >
                    계정 설정
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="flex justify-end mb-4 gap-2">
          <Button
            variant={viewMode === "card" ? "default" : "outline"}
            onClick={() => setViewMode("card")}
            size="sm"
            className={viewMode === "card"
              ? ""
              : "bg-white hover:bg-zinc-100 text-black dark:bg-zinc-800 dark:hover:bg-zinc-700 dark:text-white"
            }
          >
            <LayoutGrid className="w-4 h-4 mr-1" /> 카드
          </Button>
          <Button
            variant={viewMode === "list" ? "default" : "outline"}
            onClick={() => setViewMode("list")}
            size="sm"
            className={viewMode === "list"
              ? ""
              : "bg-white hover:bg-zinc-100 text-black dark:bg-zinc-800 dark:hover:bg-zinc-700 dark:text-white"
            }
          >
            <List className="w-4 h-4 mr-1" /> 리스트
          </Button>
        </div>

        <Tabs defaultValue="recent" className="mb-12">
          <TabsList className="grid w-full grid-cols-2 max-w-[400px]">
            <TabsTrigger value="recent">최근 요약</TabsTrigger>
            <TabsTrigger value="all">모든 요약</TabsTrigger>
          </TabsList>

          {/* 최근 요약 */}
          <TabsContent value="recent" className="mt-6">
            {lectures.length > 0 ? (
              viewMode === "card" ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {lectures.slice(0, 2).map((lecture : any) => (
                    <LectureCard
                      key={lecture.lectureId}
                      lecture={lecture}
                      onDelete={() => openDeleteDialog(lecture)}
                    />
                  ))}
                </div>
              ) : (
                <LectureList
                  lectures={lectures.slice(0, 2)}
                  onDelete={(id) => {
                    const lecture = lectures.find((l) => l.lectureId === id);
                    if (lecture) openDeleteDialog(lecture);
                  }}
                />
              )
            ) : (
              <p className="text-muted-foreground text-center">요약한 동영상이 없습니다.</p>
            )}
          </TabsContent>

          {/* 모든 요약 */}
          <TabsContent value="all" className="mt-6">
            {lectures.length > 0 ? (
              viewMode === "card" ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {lectures.map((lecture : any) => (
                    <LectureCard
                      key={lecture.lectureId}
                      lecture={lecture}
                      onDelete={() => openDeleteDialog(lecture)}
                    />
                  ))}
                </div>
              ) : (
                <LectureList
                  lectures={lectures}
                  onDelete={(id) => {
                    const lecture = lectures.find((l) => l.lectureId === id);
                    if (lecture) openDeleteDialog(lecture);
                  }}
                />
              )
            ) : (
              <p className="text-muted-foreground text-center">요약한 동영상이 없습니다.</p>
            )}
          </TabsContent>
        </Tabs>

        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>정말 삭제하시겠습니까?</DialogTitle>
            </DialogHeader>
            <p className="text-sm text-muted-foreground">
              이 작업은 되돌릴 수 없습니다. "{targetLecture?.customTitle || targetLecture?.title}" 강의가 삭제됩니다.
            </p>
            <DialogFooter className="mt-4">
              <Button
                variant="outline"
                onClick={() => setIsDialogOpen(false)}
                className="bg-muted text-foreground hover:bg-muted/80 active:scale-95 transition-transform"
              >
                취소
              </Button>
              <Button
                variant="destructive"
                onClick={() => {
                  if (targetLecture) {
                    handleDelete(targetLecture);
                  }
                }}
                className="active:scale-95 transition-transform"
              >
                삭제
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  )
}