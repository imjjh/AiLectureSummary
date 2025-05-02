"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { useLanguage } from "@/hooks/use-language"

export default function Footer() {
  const pathname = usePathname()
  const { t } = useLanguage()

  // 로그인 페이지와 회원가입 페이지에서는 푸터를 표시하지 않음
  if (pathname === "/login" || pathname === "/register") {
    return null
  }

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
            &copy; 2023 LectureSum. All rights reserved.
          </p>
        </div>
        <div className="flex gap-4 md:gap-6">
          <Link href="/about" className="text-sm font-medium hover:text-primary transition-colors">
            {t("about")}
          </Link>
          <Link href="#" className="text-sm font-medium hover:text-primary transition-colors">
            이용약관
          </Link>
          <Link href="#" className="text-sm font-medium hover:text-primary transition-colors">
            개인정보처리방침
          </Link>
          <Link href="#" className="text-sm font-medium hover:text-primary transition-colors">
            고객센터
          </Link>
        </div>
      </div>
    </footer>
  )
}
