"use client"

import { useToast } from "@/hooks/use-toast"
import {
  Toast,
  ToastClose,
  ToastDescription,
  ToastProvider,
  ToastTitle,
  ToastViewport,
} from "@/components/ui/toast"

export function Toaster() {
  const { toasts } = useToast()

  return (
    <ToastProvider>
      {/*<div className="fixed top-[80px] left-1/2 -translate-x-1/2 z-[9999] flex flex-col gap-2">*/}
        {toasts.map(({ id, title, description, action, ...props }) => (
          <Toast key={id} {...props}>
            <div className="grid gap-1">
              {title && <ToastTitle>{title}</ToastTitle>}
              {description && <ToastDescription>{description}</ToastDescription>}
            </div>
            {action}
            <ToastClose />
          </Toast>
        ))}
      {/*</div>*/}

      {/* 🔽 뷰포트도 확실한 위치 지정 */}
      <ToastViewport className="!fixed !top-[80px] !left-1/2 !-translate-x-1/2 !z-[9999] w-full max-w-sm" />
    </ToastProvider>
  )
}
