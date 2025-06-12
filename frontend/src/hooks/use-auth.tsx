"use client" // 클라이언트 컴포넌트임을 명시

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import { useRouter } from "next/navigation"
import { customFetch } from "@/lib/fetch";

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

// 공통 사용자 정보 fetch 함수
async function fetchUserInfo(apiBaseUrl: string): Promise<User | null> {
  try {
    const res = await customFetch(`${apiBaseUrl}/api/members/me`, {
      credentials: "include",
    })

    if (!res.ok) throw new Error("사용자 정보 불러오기 실패")

    const data = await res.json()
    return {
      id: String(data.data.id),
      name: data.data.username,
      email: data.data.email,
      joinDate: new Date().toISOString(), // 필요 시 서버 응답 값 사용
    }
  } catch (err) {
    console.error("사용자 정보 fetch 실패:", err)
    return null
  }
}

// 인증 관련 상태와 메서드를 제공하는 Provider 컴포넌트
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null) // 로그인한 사용자 정보
  const [isLoading, setIsLoading] = useState(true)    // 사용자 정보 로딩 상태
  const [token, setToken] = useState<string | null>(null)
  const router = useRouter()
  const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL! // 환경변수로부터 API base URL 읽기

  // 컴포넌트 마운트 시 자동 로그인 여부 확인
  useEffect(() => {
    const hasAuthCookie = () => {
      // 간단한 방식: document.cookie에서 refresh_token 또는 access_token 포함 여부 검사
      return document.cookie.includes("access_token") || document.cookie.includes("refresh_token");
    }

    if (hasAuthCookie()) {
      fetchUserInfo(API_BASE_URL)
        .then((user) => {
          if (user) setUser(user)
        })
        .catch((err) => {
          console.error("자동 로그인 실패:", err)
        })
        .finally(() => {
          setIsLoading(false)
        });
    } else {
      setIsLoading(false); // 쿠키가 없으면 로그인하지 않은 상태로 간주
    }
  }, []);
  
  // 로그인 함수
  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const res = await customFetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      })

      if (!res.ok) {
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

      const newUser = await fetchUserInfo(API_BASE_URL)
      if (!newUser) throw new Error("사용자 정보 불러오기 실패")
      setUser(newUser)

      return true
    } catch (error) {
      console.error("로그인 요청 중 오류 발생:", error)
      alert("서버 오류로 로그인에 실패했습니다.")
      return false
    }
  }

  // 회원가입 함수
  const register = async (name: string, email: string, password: string): Promise<boolean> => {
    try {
      const res = await customFetch(`${API_BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: name, email, password }),
      })

      if (res.status === 201) return true

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
    } catch (error: any) {
      console.error("API 오류:", error)
      throw new Error(error?.message || "서버 오류가 발생했습니다.")
    }
  }

  // 로그아웃 함수
  const logout = async () => {
    try {
      await customFetch(`${API_BASE_URL}/api/auth/logout`, {
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
    const refreshedUser = await fetchUserInfo(API_BASE_URL)
    if (refreshedUser) {
      setUser({
        ...refreshedUser,
        joinDate: user?.joinDate || new Date().toISOString(),
      })
    }
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, register, token, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

// 커스텀 훅으로 인증 컨텍스트 쉽게 사용하도록 함
export const useAuth = () => useContext(AuthContext)