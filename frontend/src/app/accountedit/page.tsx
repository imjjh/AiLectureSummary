"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/hooks/use-auth"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2 } from "lucide-react"
import { toast } from "@/hooks/use-toast"

import { PasswordInputWithCapsWarning } from "@/components/ui/password-input"

export default function AccountEditPage() {
  const router = useRouter()
  const { user, token } = useAuth()

  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [confirmPassword, setConfirmPassword] = useState("")

  useEffect(() => {
    if (user) {
      setUsername(user.name)
    }
  }, [user])

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError("")

    // 변경사항 없음 체크
    if (username === user?.name && password.trim() === "") {
      toast({
        variant: "destructive",
        description: "변경할 내용을 입력해주세요.",
        duration: 1000,
      })
      setIsLoading(false)
      return
    }


    if (password && (password.length < 8 || password.length > 20)) {
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
      const res = await fetch(`${process.env.NEXT_PUBLIC_SPRING_API_URL}/api/members/me`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json", "Authorization": `Bearer ${token}` },
        credentials: "include",
        body: JSON.stringify({
          username,
          ...(password ? { password } : {})
        }),
      })

      if (!res.ok) {
        toast({
          variant: "destructive",
          description: "변경할 내용을 입력해주세요.",
          duration: 1000,
        })
      }

      toast({
        description: "계정 정보가 수정되었습니다.",
        duration: 1000,
      })
      router.push("/dashboard")
    } catch (err: any) {
      setError(err.message || "계정 수정 중 오류가 발생했습니다.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="bg-gradient-to-b from-pink-50 to-white dark:from-gray-900 dark:to-gray-950 min-h-screen px-4 py-24">
      <div className="mx-auto flex w-full flex-col justify-center space-y-6 sm:w-[400px]">
        <div className="flex flex-col space-y-2 text-center">
          <h1 className="text-3xl font-bold tracking-tight">계정 설정</h1>
          <p className="text-sm text-muted-foreground">닉네임이나 비밀번호를 수정하세요</p>
        </div>

        <Card className="border-0 shadow-lg">
          <form onSubmit={handleUpdate}>
            <CardHeader>
              <CardTitle className="text-xl">정보 수정</CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4">
              <div className="grid gap-2">
                <Label htmlFor="username">닉네임</Label>
                <Input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="rounded-full"
                  required
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="email">이메일</Label>
                <Input
                  id="email"
                  type="email"
                  value={user?.email || ""}
                  disabled
                  className="rounded-full bg-muted-foreground/10"
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="password">비밀번호</Label>
                <PasswordInputWithCapsWarning
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="rounded-full"
                  placeholder="변경하려면 입력"
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
                  required={!!password}
                />
                {confirmPassword && confirmPassword !== password && (
                  <p className="text-sm text-red-500 mt-1">비밀번호가 일치하지 않습니다.</p>
                )}
              </div>

              {error && <p className="text-sm text-red-500">{error}</p>}
            </CardContent>

            <CardFooter className="flex flex-col">
              <Button
                type="submit"
                className="w-full rounded-full bg-linear-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    저장 중...
                  </>
                ) : (
                  "정보 저장"
                )}
              </Button>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  )
}
