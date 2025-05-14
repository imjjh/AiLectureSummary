"use client"

import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"

interface NoteEditorProps {
  note: string
  setNote: (value: string) => void
  lastUpdated: string | null
  onSave: () => void
  onDelete: () => void
}

export function NoteEditor({
  note,
  setNote,
  lastUpdated,
  onSave,
  onDelete,
}: NoteEditorProps) {
  return (
    <div className="space-y-2 mt-6">
      <h3 className="text-lg font-semibold">내 메모</h3>
      {lastUpdated && (
        <p className="text-sm text-muted-foreground">
          마지막 저장: {new Date(lastUpdated).toLocaleString("ko-KR")}
        </p>
      )}
      <Textarea
        placeholder="요약을 보고 생각난 내용을 자유롭게 기록하세요."
        value={note}
        onChange={(e) => setNote(e.target.value)}
        className="min-h-[120px] focus-visible:ring-0 focus-visible:ring-transparent focus:outline-none"
      />
      <div className="flex justify-end gap-2">
        <Button
          onClick={onSave}
          className="px-6 bg-muted text-foreground hover:bg-gray-200 active:scale-95 transition-all"
        >
          메모 저장
        </Button>
        <Button
          onClick={onDelete}
          className="px-6 bg-muted text-foreground hover:bg-gray-200 active:scale-95 transition-all"
        >
          삭제
        </Button>
      </div>
    </div>
  )
}
