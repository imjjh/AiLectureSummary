"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"

type LanguageContextType = {
  language: string
  setLanguage: (lang: string) => void
  t: (key: string) => string
}

const translations: Record<string, Record<string, string>> = {
  ko: {
    home: "Home",
    about: "About",
    login: "로그인",
    register: "회원가입",
    upload: "동영상 업로드",
    analyze: "AI 분석",
    summary: "요약 결과",
    upload_video: "동영상 파일을 업로드하세요",
    drag_drop: "파일을 끌어다 놓거나 클릭하여 선택하세요",
    select_file: "파일 선택",
    start_summary: "요약 시작하기",
    key_summary: "핵심 요약",
    full_summary: "전체 요약",
    key_points: "핵심 포인트",
    my_page: "마이 페이지",
    logout: "로그아웃",
    settings: "설정",
    profile: "프로필",
    service_title: "동영상 강의 요약 서비스",
    service_description: "AI가 동영상 강의를 분석하고 핵심 내용을 요약해 드립니다",
  },
  en: {
    home: "Home",
    about: "About",
    login: "Login",
    register: "Register",
    upload: "Upload Video",
    analyze: "AI Analysis",
    summary: "Summary",
    upload_video: "Upload your video file",
    drag_drop: "Drag and drop or click to select",
    select_file: "Select File",
    start_summary: "Start Summary",
    key_summary: "Key Summary",
    full_summary: "Full Summary",
    key_points: "Key Points",
    my_page: "My Page",
    logout: "Logout",
    settings: "Settings",
    profile: "Profile",
    service_title: "Video Lecture Summary Service",
    service_description: "AI analyzes video lectures and summarizes the key content for you",
  },
}

const LanguageContext = createContext<LanguageContextType>({
  language: "ko",
  setLanguage: () => {},
  t: (key: string) => key,
})

export function LanguageProvider({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState("ko")

  useEffect(() => {
    const savedLanguage = localStorage.getItem("language")
    if (savedLanguage) {
      setLanguageState(savedLanguage)
    }
  }, [])

  const setLanguage = (lang: string) => {
    setLanguageState(lang)
    localStorage.setItem("language", lang)
  }

  const t = (key: string): string => {
    return translations[language]?.[key] || key
  }

  return <LanguageContext.Provider value={{ language, setLanguage, t }}>{children}</LanguageContext.Provider>
}

export const useLanguage = () => useContext(LanguageContext)
