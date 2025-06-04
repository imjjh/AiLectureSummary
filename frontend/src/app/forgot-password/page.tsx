"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2 } from "lucide-react"
import { PasswordInputWithCapsWarning } from "@/components/ui/password-input"

const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL

export default function ForgotPasswordPage() {
  const router = useRouter()
  const [token, setToken] = useState("")

  const [step, setStep] = useState<1 | 2>(1)
  const [name, setName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [successMessage, setSuccessMessage] = useState("")

  const handleVerifyUser = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setIsLoading(true)

    try {
      const res = await fetch(`${API_BASE_URL}/api/password/verify`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username: name, email }),
      })

      if (!res.ok) throw new Error("입력한 정보가 일치하지 않습니다.")
      const result = await res.json()
      setToken(result.data.token)
      setStep(2)
    } catch (err: any) {
      setError(err.message || "확인 중 오류가 발생했습니다.")
    } finally {
      setIsLoading(false)
    }
  }

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setIsLoading(true)

    if (!token) {
      setError("비밀번호 재설정 토큰이 없습니다.")
      setIsLoading(false)
      return
    }

    if (password.length < 8 || password.length > 20) {
      setError("비밀번호는 8자 이상 20자 이하로 입력해주세요.")
      setIsLoading(false)
      return
    }

    if (password !== confirmPassword) {
      setError("비밀번호가 일치하지 않습니다.")
      setIsLoading(false)
      return
    }

    try {
      const res = await fetch(`${API_BASE_URL}/api/password/reset`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Reset-Token": token,
        },
        body: JSON.stringify({ newPassword: password }),
      })

      if (!res.ok) throw new Error("비밀번호 변경 실패")

      setSuccessMessage("비밀번호가 성공적으로 변경되었습니다.")
      setTimeout(() => router.push("/login"), 1500)
    } catch (err: any) {
      setError(err.message || "변경 중 오류가 발생했습니다.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="bg-gradient-to-b from-pink-50 to-white dark:from-gray-900 dark:to-gray-950 min-h-screen px-4 pt-40 pb-12">
      <div className="mx-auto w-full max-w-md space-y-6">
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold">비밀번호 찾기</h1>
          <p className="text-sm text-muted-foreground">
            이름과 이메일을 입력해 계정 정보를 확인하고 비밀번호를 변경하세요
          </p>
        </div>

        <Card className="border-0 shadow-lg">
          <form onSubmit={step === 1 ? handleVerifyUser : handleChangePassword}>
            <CardHeader>
              <CardTitle className="text-xl">
                {step === 1 ? "사용자 정보 확인" : "새 비밀번호 설정"}
              </CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
              {step === 1 ? (
                <>
                  <div className="grid gap-2">
                    <Label htmlFor="name">이름</Label>
                    <Input
                      id="name"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      className="rounded-full"
                      required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="email">이메일</Label>
                    <Input
                      id="email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="rounded-full"
                      required
                    />
                  </div>
                </>
              ) : (
                <>
                  <div className="grid gap-2">
                    <Label htmlFor="password">새 비밀번호</Label>
                    <PasswordInputWithCapsWarning
                      id="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="rounded-full"
                      required
                    />
                    {password && (password.length < 8 || password.length > 20) && (
                      <p className="text-sm text-red-500 mt-1">비밀번호는 8자 이상 20자 이하로 입력해주세요.</p>
                    )}
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="confirm-password">비밀번호 확인</Label>
                    <PasswordInputWithCapsWarning
                      id="confirm-password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="rounded-full"
                      required
                    />
                  </div>
                </>
              )}

              {error && <p className="text-sm text-red-500">{error}</p>}
              {successMessage && <p className="text-sm text-green-600">{successMessage}</p>}
            </CardContent>
            <CardFooter>
              <Button
                type="submit"
                className="w-full rounded-full bg-gradient-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    처리 중...
                  </>
                ) : step === 1 ? "정보 확인" : "비밀번호 변경"}
              </Button>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  )
}
