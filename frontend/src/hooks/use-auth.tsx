"use client" // 클라이언트 컴포넌트임을 명시

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import { useRouter } from "next/navigation"

// 사용자 정보를 표현하는 타입 정의
type User = {
  id: string
  name: string
  email: string
  joinDate: string
}

// 인증 컨텍스트가 제공하는 값들의 타입 정의
type AuthContextType = {
  user: User | null
  isLoading: boolean
  token: string | null
  login: (email: string, password: string) => Promise<boolean>
  logout: () => Promise<void>
  register: (name: string, email: string, password: string) => Promise<boolean>
  refreshUser: () => Promise<void>
}

// 초기 컨텍스트 생성 (기본값은 비어 있음)
const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: true,
  token: null,
  login: async () => false,
  logout: async () => { },
  register: async () => false,
  refreshUser: async () => { },
})

// 인증 관련 상태와 메서드를 제공하는 Provider 컴포넌트
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null) // 로그인한 사용자 정보
  const [isLoading, setIsLoading] = useState(true)    // 사용자 정보 로딩 상태
  const [token, setToken] = useState<string | null>(null)
  const router = useRouter()
  const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL // 환경변수로부터 API base URL 읽기

  // 컴포넌트 마운트 시 자동 로그인 여부 확인
  useEffect(() => {
    const storedToken = localStorage.getItem("access_token")
    if (storedToken) {
      setToken(storedToken)
      fetchUserWithToken(storedToken)
    } else {
      setIsLoading(false)
    }
    async function fetchUserWithToken(token: string) {
      try {
        const res = await fetch(`${API_BASE_URL}/api/members/me`, {
          credentials: "include", // 쿠키 포함
          headers: { Authorization: `Bearer ${token}` }
        })

        if (res.ok) {
          const data = await res.json()
          const loggedInUser: User = {
            id: String(data.data.id),
            name: data.data.username,
            email: data.data.email,
            joinDate: "",
          }
          setUser(loggedInUser)
        }
      } catch (err) {
        console.error("자동 로그인 실패:", err)
      } finally {
        setIsLoading(false)
      }
    }
  }, [])

  // 로그인 함수
  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      })

      if (res.ok) {
        const result = await res.json()
        const token = result.data.token // (필요시 활용 가능)

        setToken(token)
        localStorage.setItem("access_token", token)

        // 로그인 성공 후 사용자 정보 가져오기
        const userRes = await fetch(`${API_BASE_URL}/api/members/me`, {
          credentials: "include",
          headers: {
            "Authorization": `Bearer ${token}`
          }
        })
        if (!userRes.ok) throw new Error("사용자 정보 불러오기 실패")

        const userInfo = await userRes.json()
        const newUser: User = {
          id: String(userInfo.data.id),
          name: userInfo.data.username,
          email: userInfo.data.email,
          joinDate: new Date().toISOString(),
        }

        setUser(newUser)
        return true
      } else {
        // 실패한 경우 에러 메시지 추출
        let message = "알 수 없는 오류가 발생했습니다."
        try {
          const errorJson = await res.json()
          if (typeof errorJson?.message === "string") {
            message = errorJson.message
          }
        } catch {
          const fallback = await res.text()
          if (fallback) message = fallback
        }
        console.error("로그인 실패:", message)
        alert("로그인 실패: " + message)
        return false
      }
    } catch (error) {
      console.error("로그인 요청 중 오류 발생:", error)
      alert("서버 오류로 로그인에 실패했습니다.")
      return false
    }
  }

  // 회원가입 함수
  const register = async (name: string, email: string, password: string): Promise<boolean> => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/auth/register`, {
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
        throw new Error(message)
      }
    } catch (error: any) {
      console.error("API 오류:", error)
      throw new Error(error?.message || "서버 오류가 발생했습니다.")
    }
  }

  // 로그아웃 함수
  const logout = async () => {
    try {
      await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: "POST",
        credentials: "include",
      })
    } catch (error) {
      console.error("로그아웃 요청 중 오류 발생:", error)
    } finally {
      setUser(null)
      setToken(null)       // 사용자 상태 초기화
      router.push("/")     // 홈으로 리디렉션
    }
  }

  const refreshUser = async () => {
    if (!token) return
    try {
      const res = await fetch(`${API_BASE_URL}/api/members/me`, {
        headers: { Authorization: `Bearer ${token}` },
        credentials: "include",
      })
      const data = await res.json()
      if (res.ok && data.data) {
        const refreshedUser: User = {
          id: String(data.data.id),
          name: data.data.username,
          email: data.data.email,
          joinDate: user?.joinDate || new Date().toISOString(), // 기존 joinDate 유지
        }
        setUser(refreshedUser)
      }
    } catch (err) {
      console.error("유저 정보 새로고침 실패:", err)
    }
  }


  // Context Provider로 로그인 상태 및 메서드 전달
  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, register, token, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

// 커스텀 훅으로 인증 컨텍스트 쉽게 사용하도록 함
export const useAuth = () => useContext(AuthContext)