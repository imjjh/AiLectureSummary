"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/hooks/use-auth"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { Loader2 } from "lucide-react"
import Link from "next/link"
import { toast } from "@/hooks/use-toast"

import { PasswordInputWithCapsWarning } from "@/components/ui/password-input"

// 회원가입 페이지 컴포넌트
export default function RegisterPage() {
  const { register } = useAuth()
  const router = useRouter()
  const [name, setName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [termsAccepted, setTermsAccepted] = useState(false)

  // 폼 제출 처리 함수
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    // 비밀번호 일치 여부 확인
    if (password !== confirmPassword) {
      setError("비밀번호가 일치하지 않습니다.")
      return
    }

    // 이용약관 동의 여부 확인
    if (!termsAccepted) {
      setError("이용약관에 동의해주세요.")
      return
    }

    setIsLoading(true)

    try {
      // 회원가입 시도
      const success = await register(name, email, password)
      if (success) {
        toast({
          title: "회원가입 완료🎉",
          description: "환영합니다! 잠시 후 로그인 페이지로 이동합니다.",
          duration: 1500,
        })

        setTimeout(() => {
          router.push("/login")
        }, 1500)
      } 
    } catch (err: any) {
      setError(err?.message || "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    // 페이지 전체 배경 및 레이아웃 설정
    <div className="bg-gradient-to-b from-pink-50 to-white dark:from-gray-900 dark:to-gray-950 min-h-screen px-4 py-24">
      <div className="mx-auto flex w-full flex-col justify-center space-y-6 sm:w-[400px]">
        {/* 헤더 섹션: 제목 및 설명 */}
        <div className="flex flex-col space-y-2 text-center">
          <h1 className="text-3xl font-bold tracking-tight">회원가입</h1>
          <p className="text-sm text-muted-foreground">계정을 만들고 요약 기록을 저장하세요</p>
        </div>
        {/* 카드 컴포넌트: 회원가입 폼 */}
        <Card className="border-0 shadow-lg">
          <form onSubmit={handleSubmit}>
            {/* 카드 헤더: 제목 */}
            <CardHeader className="space-y-1">
              <CardTitle className="text-xl">새 계정 만들기</CardTitle>
              {/*<CardDescription>소셜 계정으로 가입하거나 이메일을 사용하세요</CardDescription>*/}
            </CardHeader>
            {/* 카드 내용: 입력 필드 및 체크박스 */}
            <CardContent className="grid gap-4">
              {/* 이름 입력 */}
              <div className="grid gap-2">
                <Label htmlFor="name">이름</Label>
                <Input
                  id="name"
                  type="text"
                  placeholder=""
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="rounded-full"
                  required
                />
              </div>
              {/* 이메일 입력 */}
              <div className="grid gap-2">
                <Label htmlFor="email">이메일</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder=""
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="rounded-full"
                  required
                />
              </div>
              {/* 비밀번호 입력 및 유효성 검사 */}
              <div className="grid gap-2">
                <Label htmlFor="password">비밀번호</Label>
                <PasswordInputWithCapsWarning
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="rounded-full"
                  required
                />
                {password && (password.length < 8 || password.length > 20) && (
                  <p className="text-sm text-red-500 mt-1">비밀번호는 8자 이상 20자 이하로 입력해주세요.</p>
                )}
              </div>
              {/* 비밀번호 확인 입력 및 일치 여부 확인 */}
              <div className="grid gap-2">
                <Label htmlFor="confirm-password">비밀번호 확인</Label>
                <PasswordInputWithCapsWarning
                  id="confirm-password"
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="rounded-full"
                  required
                />
                {confirmPassword && confirmPassword !== password && (
                  <p className="text-sm text-red-500 mt-1">비밀번호가 일치하지 않습니다.</p>
                )}
              </div>
              {/* 이용약관 동의 체크박스 */}
              {/* <div className="flex items-center space-x-2">
                <Checkbox
                  id="terms"
                  checked={termsAccepted}
                  onCheckedChange={(checked) => setTermsAccepted(checked as boolean)}
                />
                <label
                  htmlFor="terms"
                  className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                >
                  <span>
                    <Link href="#" className="text-primary hover:underline">
                      이용약관
                    </Link>
                    에 동의합니다
                  </span>
                </label>
              </div> */}
              {/* 에러 메시지 출력 */}
              {error && <p className="text-sm text-red-500">{error}</p>}
            </CardContent>
            {/* 카드 푸터: 제출 버튼 및 로그인 링크 */}
            <CardFooter className="flex flex-col">
              <Button
                type="submit"
                className="w-full rounded-full bg-linear-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    회원가입 중...
                  </>
                ) : (
                  "회원가입"
                )}
              </Button>
              <p className="mt-4 text-center text-sm text-muted-foreground">
                이미 계정이 있으신가요?{" "}
                <Link href="/login" className="text-primary hover:underline">
                  로그인
                </Link>
              </p>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  )
}
