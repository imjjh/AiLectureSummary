"use client"

import { Input } from "@/components/ui/input"
import { AlertTriangle } from "lucide-react"
import { cn } from "@/lib/utils"
import { useState } from "react"

interface PasswordInputProps extends React.InputHTMLAttributes<HTMLInputElement> {}

export function PasswordInputWithCapsWarning({ className, ...props }: PasswordInputProps) {
  const [isCapsLockOn, setIsCapsLockOn] = useState(false)

  const handleKeyUp = (e: React.KeyboardEvent<HTMLInputElement>) => {
    setIsCapsLockOn(e.getModifierState("CapsLock"))
  }

  return (
    <div className="relative">
      <Input
        type="password"
        onKeyUp={handleKeyUp}
        className={cn(className, isCapsLockOn && "border-yellow-500")}
        {...props}
      />
      {isCapsLockOn && (
        <div className="absolute top-1/2 right-3 -translate-y-1/2 flex items-center text-yellow-600">
          <AlertTriangle size={18} />
          <span className="ml-1 text-xs hidden sm:inline">CapsLock 켜짐</span>
        </div>
      )}
    </div>
  )
}
