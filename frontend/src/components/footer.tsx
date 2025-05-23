"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"

export default function Footer() {
  const pathname = usePathname()

  // 로그인 페이지와 회원가입 페이지에서는 푸터를 표시하지 않음
  {/*if (pathname === "/login" || pathname === "/register") {
    return null
  }*/}

  return (
    <footer className="border-t py-8 md:py-12 bg-linear-to-b from-white to-pink-50 dark:from-gray-950 dark:to-gray-900">
      <div className="container flex flex-col items-center justify-between gap-4 md:flex-row">
        <div className="flex flex-col items-center gap-4 md:items-start md:gap-2">
          <Link
            href="/"
            className="font-bold text-xl bg-linear-to-r from-pink-500 to-orange-500 text-transparent bg-clip-text"
          >
            LectureSum
          </Link>
          <p className="text-center text-sm text-muted-foreground md:text-left">
            &copy; 2025 LectureSum. All rights reserved.
          </p>
        </div>
        <div className="text-sm text-muted-foreground text-right leading-6">
          <p><strong>KTNU 팀원</strong></p>
          <p><a href="https://github.com/ddoyo" target="_blank" rel="noopener noreferrer">김서연</a></p>
          <p><a href="https://github.com/jangjunho2" target="_blank" rel="noopener noreferrer">장준호</a></p>
          <p><a href="https://github.com/light-castle" target="_blank" rel="noopener noreferrer">조희성</a></p>
          <p><a href="https://github.com/minwoChoi" target="_blank" rel="noopener noreferrer">최민우</a></p>
        </div>
      </div>
    </footer>
  )
}
