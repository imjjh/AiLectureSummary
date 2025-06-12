"use client"

import { useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { ModeToggle } from "@/components/mode-toggle"
import { UserMenu } from "@/components/user-menu"
import { Menu, X } from "lucide-react"
import { usePathname } from "next/navigation"
import { useAuth } from "@/hooks/use-auth"
import { motion } from "framer-motion"

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const pathname = usePathname()
  const { isLoading } = useAuth()

  return (
    <header className="sticky top-0 z-50 w-full backdrop-blur-sm supports-backdrop-filter:bg-background/60 border-b border-border/40">
      <div className="container flex h-16 items-center justify-between">
        <div className="flex items-center gap-2">
          <Link
            href="/"
            className="font-bold text-xl bg-linear-to-r from-pink-500 to-orange-500 text-transparent bg-clip-text"
          >
            LectureSum
          </Link>
        </div>

        <nav className="hidden md:flex items-center gap-6">
          <Link
            href="/"
            className={`text-sm font-medium hover:text-primary transition-colors ${
              pathname === "/" ? "text-primary" : ""
            }`}
          >
            Home
          </Link>
          <Link
            href="/about"
            className={`text-sm font-medium hover:text-primary transition-colors ${
              pathname === "/about" ? "text-primary" : ""
            }`}
          >
            About
          </Link>
          <Link
            href="/dashboard"
            className={`text-sm font-medium hover:text-primary transition-colors ${
              pathname === "/dashboard" ? "text-primary" : ""
            }`}
          >
            Mypage
          </Link>
        </nav>

        <div className="hidden md:flex items-center gap-2">
          <ModeToggle />
          {isLoading ? (
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-full bg-muted animate-pulse" />
            </div>
          ) : (
            <UserMenu />
          )}
        </div>

        <div className="flex md:hidden items-center gap-2">
          <ModeToggle />
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            aria-label="Toggle Menu"
            className="rounded-full"
          >
            {isMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </Button>
        </div>
      </div>

      {isMenuOpen && (
        <motion.div
          className="md:hidden border-t"
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: "auto" }}
          exit={{ opacity: 0, height: 0 }}
          transition={{ duration: 0.2 }}
        >
          <div className="container py-4 grid gap-4">
            <Link
              href="/"
              className={`text-sm font-medium hover:text-primary transition-colors ${
                pathname === "/" ? "text-primary" : ""
              }`}
              onClick={() => setIsMenuOpen(false)}
            >
              Home
            </Link>
            <Link
              href="/about"
              className={`text-sm font-medium hover:text-primary transition-colors ${
                pathname === "/about" ? "text-primary" : ""
              }`}
              onClick={() => setIsMenuOpen(false)}
            >
              About
            </Link>
            <div className="flex flex-col gap-2 pt-2 border-t">
              {isLoading ? (
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-muted animate-pulse" />
                </div>
              ) : (
                <UserMenu />
              )}
            </div>
          </div>
        </motion.div>
      )}
    </header>
  )
}
