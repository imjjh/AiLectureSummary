"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import { useRouter } from "next/navigation"

type User = {
  id: string
  name: string
  email: string
  joinDate: string
}

type AuthContextType = {
  user: User | null
  isLoading: boolean
  login: (email: string, password: string) => Promise<boolean>
  logout: () => Promise<void>
  register: (name: string, email: string, password: string) => Promise<boolean>
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: true,
  login: async () => false,
  logout: async () => {},
  register: async () => false,
})

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  // ✅ 로그인 유지용 - 컴포넌트 마운트 시 자동 호출
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/members/me", {
          credentials: "include",
        })
        if (res.ok) {
          const data = await res.json()
          const loggedInUser: User = {
            id: String(data.id),
            name: data.username,
            email: data.email,
            joinDate: "", // 서버에 joinDate가 없으면 빈 값
          }
          setUser(loggedInUser)
        }
      } catch (err) {
        console.error("자동 로그인 확인 실패:", err)
      } finally {
        setIsLoading(false)
      }
    }

    fetchUser()
  }, [])

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const res = await fetch("http://localhost:8080/api/members/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      })

      if (res.ok) {
        const data = await res.json()
        const newUser: User = {
          id: String(data.member.id),
          name: data.member.username,
          email: data.member.email,
          joinDate: new Date().toISOString(),
        }
        setUser(newUser)
        return true
      } else {
        const errorText = await res.text()
        console.error("로그인 실패:", errorText)
        alert("로그인 실패: " + errorText)
        return false
      }
    } catch (error) {
      console.error("로그인 요청 중 오류 발생:", error)
      alert("서버 오류로 로그인에 실패했습니다.")
      return false
    }
  }

  const register = async (name: string, email: string, password: string): Promise<boolean> => {
    try {
      const res = await fetch("http://localhost:8080/api/members/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: name, email, password }),
      })

      if (res.status === 201) {
        return true
      } else {
        let message = "회원가입에 실패했습니다."
        try {
          const errorBody = await res.clone().json()
          if (Array.isArray(errorBody?.errors) && errorBody.errors[0]?.defaultMessage) {
            message = errorBody.errors[0].defaultMessage
          } else if (typeof errorBody?.message === "string") {
            message = errorBody.message
          }
        } catch {
          const fallback = await res.text()
          if (fallback) message = fallback
        }
        alert(`❌ 회원가입 실패: ${message}`)
        console.error("회원가입 실패:", message)
        return false
      }
    } catch (error) {
      console.error("API 오류:", error)
      alert("서버 오류가 발생했습니다.")
      return false
    }
  }

  const logout = async () => {
    try {
      await fetch("http://localhost:8080/api/members/logout", {
        method: "POST",
        credentials: "include",
      })
    } catch (error) {
      console.error("로그아웃 요청 중 오류 발생:", error)
    } finally {
      setUser(null)
      router.push("/")
    }
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
