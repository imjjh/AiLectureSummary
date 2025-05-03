"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import { useRouter } from "next/navigation"

type User = {
  id: string
  name: string
  email: string
  joinDate: string
  membership: string
}

type AuthContextType = {
  user: User | null
  isLoading: boolean
  login: (email: string, password: string) => Promise<boolean>
  logout: () => void
  register: (name: string, email: string, password: string) => Promise<boolean>
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: true,
  login: async () => false,
  logout: () => {},
  register: async () => false,
})

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  useEffect(() => {
    const storedUser = localStorage.getItem("user")
    if (storedUser) {
      setUser(JSON.parse(storedUser))
    }
    setIsLoading(false)
  }, [])

  const login = async (email: string, password: string): Promise<boolean> => {
    await new Promise((resolve) => setTimeout(resolve, 1000))

    if (email && password.length >= 6) {
      const newUser: User = {
        id: "user-" + Math.random().toString(36).substr(2, 9),
        name: email.split("@")[0],
        email,
        joinDate: new Date().toLocaleDateString(),
        membership: "무료 회원",
      }
      setUser(newUser)
      localStorage.setItem("user", JSON.stringify(newUser))
      return true
    }
    return false
  }

  const register = async (name: string, email: string, password: string): Promise<boolean> => {
    try {
      const res = await fetch("http://localhost:8080/api/members/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: name, email, password }),
      });

      if (res.status === 201) {
        const data = await res.json();
        const newUser: User = {
          id: data.id.toString(),
          name,
          email,
          joinDate: new Date().toLocaleDateString(),
          membership: "무료 회원",
        };
        setUser(newUser);
        localStorage.setItem("user", JSON.stringify(newUser));
        return true;
      } else {
        // const error = await res.text();
        // alert(`❌ 회원가입 실패: ${error}`);
        // console.error("회원가입 실패:", error);
///
        let message = "회원가입에 실패했습니다.";
try {
  const errorBody = await res.clone().json();
  if (Array.isArray(errorBody?.errors) && errorBody.errors[0]?.defaultMessage) {
    message = errorBody.errors[0].defaultMessage;
  } else if (typeof errorBody?.message === "string") {
    message = errorBody.message;
  }
} catch {
  const fallback = await res.text();
  if (fallback) message = fallback;
}
alert(`❌ 회원가입 실패: ${message}`);
console.error("회원가입 실패:", message);
///
        return false;
      }
    } catch (error) {
      console.error("API 오류:", error);
      alert("서버 오류가 발생했습니다.");
      return false;
    }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("user")
    router.push("/")
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)