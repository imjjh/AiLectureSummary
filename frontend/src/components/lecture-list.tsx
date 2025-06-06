"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import Image from "next/image";
import { X } from "lucide-react"

interface Lecture {
  lectureId: number
  customTitle: string
  duration: number;
  thumbnailBase64?: string;
  enrolledAt?: string
}

interface LectureListProps {
  lectures: Lecture[]
  onDelete?: (lectureId: number) => void
}

const formatDate = (isoString: string): string => {
  const date = new Date(isoString)
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  })
}

const formatDuration = (duration: number): string => {
  const mins = Math.floor(duration / 60)
  const secs = duration % 60
  return `${mins}:${secs.toString().padStart(2, "0")}`
}

export default function LectureList({ lectures, onDelete }: LectureListProps) {
  const [thumbnailMap, setThumbnailMap] = useState<Record<number, string>>({})

  useEffect(() => {
    const map: Record<number, string> = {}
    lectures.forEach((lecture) => {
      if (lecture.thumbnailBase64) {
        map[lecture.lectureId] = `data:image/png;base64,${lecture.thumbnailBase64}`
      } else {
        const type = localStorage.getItem(`lecture-type-${lecture.lectureId}`)
        if (type === "youtube") {
          map[lecture.lectureId] = "/images/youtube.jpg"
        } else if (type === "audio") {
          map[lecture.lectureId] = "/images/audio.avif"
        } else {
          map[lecture.lectureId] = "/images/1.png"
        }
      }
    })
    setThumbnailMap(map)
  }, [lectures])
  return (
    <div className="space-y-2">
      {lectures.map((lecture) => {
        const thumbnailSrc = thumbnailMap[lecture.lectureId] || "/images/1.png"

        return (
          <div
            key={lecture.lectureId}
            className="flex gap-4 items-center border rounded-md p-4 hover:bg-muted transition relative"
          >
            <Link
              href={`/summary/${lecture.lectureId}`}
              className="flex gap-4 items-center w-full"
            >
              <div className="w-32 h-20 flex-shrink-0 overflow-hidden rounded-md bg-muted">
                <Image
                  src={thumbnailSrc}
                  alt="썸네일"
                  width={128}
                  height={80}
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="flex-1">
                <p className="font-medium line-clamp-1">{lecture.customTitle}</p>
                <div className="text-sm text-muted-foreground flex gap-4">
                  <span>{formatDuration(lecture.duration)}</span>
                  <span>{lecture.enrolledAt ? formatDate(lecture.enrolledAt) : "-"}</span>
                </div>
              </div>
            </Link>
            {onDelete && (
              <button
                onClick={() => onDelete(lecture.lectureId)}
                className="absolute top-2 right-2 text-muted-foreground hover:text-destructive"
              >
                <X size={18} />
              </button>
            )}
          </div>
        )
      })}
    </div>
  )
}

