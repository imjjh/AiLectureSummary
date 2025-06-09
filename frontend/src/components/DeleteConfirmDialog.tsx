"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"

interface DeleteConfirmDialogProps {
  open: boolean
  onClose: () => void
  onConfirm: () => void
}

export default function DeleteConfirmDialog({ open, onClose, onConfirm }: DeleteConfirmDialogProps) {
  const [inputValue, setInputValue] = useState("")

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>정말 탈퇴하시겠어요?</DialogTitle>
          <p className="text-sm text-muted-foreground">“탈퇴합니다”를 입력해야 탈퇴가 진행됩니다.</p>
        </DialogHeader>
        <Input
          placeholder="탈퇴합니다"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
        />
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>취소</Button>
          <Button
            onClick={onConfirm}
            disabled={inputValue !== "탈퇴합니다"}
            className="bg-destructive text-white"
          >
            탈퇴하기
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
