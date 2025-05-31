"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/hooks/use-auth"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2 } from "lucide-react"
import Link from "next/link"
import { useSearchParams } from "next/navigation"

import { PasswordInputWithCapsWarning } from "@/components/ui/password-input"

export default function LoginPage() {
  // 인증 관련 훅에서 로그인 함수 가져오기
  const { login } = useAuth()
  // Next.js 라우터 훅
  const router = useRouter()
  // 이메일 상태 관리
  const [email, setEmail] = useState("")
  // 비밀번호 상태 관리
  const [password, setPassword] = useState("")
  // 로딩 상태 관리 (로그인 중 표시용)
  const [isLoading, setIsLoading] = useState(false)
  // 에러 메시지 상태 관리
  const [error, setError] = useState("")

  // 로그인 폼 제출 핸들러
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    // 에러 초기화
    setError("")
    // 로딩 상태 시작
    setIsLoading(true)

    try {
      // 로그인 시도
      const success = await login(email, password)
      if (success) {
        // 로그인 성공 시 대시보드로 이동
        router.push("/dashboard")
      } else {
        // 로그인 실패 시 에러 메시지 설정
        setError("로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.")
      }
    } catch (err) {
      // 로그인 중 예외 발생 시 에러 메시지 설정
      setError("로그인 중 오류가 발생했습니다. 다시 시도해주세요.")
    } finally {
      // 로딩 상태 종료
      setIsLoading(false)
    }
  }

  return (
    // 페이지 배경 및 전체 레이아웃 설정
    <div className="bg-gradient-to-b from-pink-50 to-white dark:from-gray-900 dark:to-gray-950 min-h-screen px-4 pt-40 pb-12">
      <div className="mx-auto w-full max-w-md space-y-6">
        {/* 로그인 제목 및 설명 */}
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold">로그인</h1>
          <p className="text-sm text-muted-foreground">계정에 로그인하여 요약 기록을 관리하세요</p>
        </div>

        {/* 로그인 폼 카드 */}
        <Card className="border-0 shadow-lg">
          <form onSubmit={handleSubmit}>
            {/* 카드 헤더: 제목과 설명 */}
            <CardHeader className="space-y-1">
              <CardTitle className="text-xl">로그인</CardTitle>
              <CardDescription>이메일과 비밀번호를 입력하세요</CardDescription>
            </CardHeader>
            {/* 카드 내용: 입력 폼 */}
            <CardContent className="grid gap-4">
              {/* 이메일 입력 필드 */}
              <div className="grid gap-2">
                <Label htmlFor="email">이메일</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="name@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="rounded-full"
                  required
                />
              </div>
              {/* 비밀번호 입력 필드 및 비밀번호 찾기 링크 */}
              <div className="grid gap-2">
                <div className="flex items-center justify-between">
                  <Label htmlFor="password">비밀번호</Label>
                  <Link href="/forgot-password" className="text-sm text-primary hover:underline">
                    비밀번호 찾기
                  </Link>
                </div>
                <PasswordInputWithCapsWarning
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="rounded-full"
                  required
                />
              </div>
              {/* 에러 메시지 표시 */}
              {error && <p className="text-sm text-red-500">{error}</p>}
            </CardContent>
            {/* 카드 하단: 로그인 버튼 및 회원가입 안내 */}
            <CardFooter className="flex flex-col">
              <Button
                type="submit"
                className="w-full rounded-full bg-gradient-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0"
                disabled={isLoading}
              >
                {/* 로딩 중에는 스피너와 텍스트 표시 */}
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    로그인 중...
                  </>
                ) : (
                  "로그인"
                )}
              </Button>
              {/* 회원가입 링크 안내 */}
              <p className="mt-4 text-center text-sm text-muted-foreground">
                계정이 없으신가요?{" "}
                <Link href="/register" className="text-primary hover:underline">
                  회원가입
                </Link>
              </p>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  )
}
