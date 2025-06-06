"use client";

import { Card, CardContent } from "@/components/ui/card";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import Link from "next/link";

interface Lecture {
    lectureId: number;
    customTitle: string;
    duration: number;
    thumbnailBase64?: string;
    enrolledAt: string;
}

interface LectureCardProps {
    lecture: Lecture;
    onDelete?: (id: number) => void;
}


export default function LectureCard({ lecture, onDelete }: LectureCardProps) {
    const formatDuration = (duration: number): string => {
        const mins = Math.floor(duration / 60);
        const secs = duration % 60;
        return `${mins}:${secs.toString().padStart(2, "0")}`;
    };

    const formatDate = (isoString: string): string => {
        const date = new Date(isoString);
        return date.toLocaleDateString("ko-KR", {
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    };

    const getThumbnailSrc = (): string => {
        if (lecture.thumbnailBase64) {
            return `data:image/png;base64,${lecture.thumbnailBase64}`;
        }

        // CSR 환경에서만 localStorage 접근
        if (typeof window !== "undefined") {
            const type = localStorage.getItem(`lecture-type-${lecture.lectureId}`);
            if (type === "youtube") return "/images/youtube.jpg";
            if (type === "audio") return "/images/audio.avif";
        }

        return "/images/1.png"; // 기본 fallback
    };

    const thumbnailSrc = getThumbnailSrc();


    return (
        <Link href={`/summary/${lecture.lectureId}`}>
            <Card className="relative hover:shadow-lg transition-shadow cursor-pointer">
                {onDelete && (
                    <Button
                        variant="ghost"
                        size="sm"
                        className="absolute top-2 right-2 text-xs text-muted-foreground hover:text-destructive"
                        onClick={(e) => {
                            e.preventDefault();
                            e.stopPropagation();
                            onDelete?.(lecture.lectureId);
                        }}
                    >
                        X
                    </Button>
                )}
                <CardContent className="p-4">
                    <div className="aspect-video bg-muted rounded mb-3 overflow-hidden">
                        <Image
                            src={thumbnailSrc}
                            alt="썸네일"
                            width={320}
                            height={180}
                            className="w-full h-full object-cover"
                            onError={(e) => {
                                (e.target as HTMLImageElement).src = "/images/1.png";
                            }}
                        />
                    </div>
                    <div className="text-base font-medium truncate">{lecture.customTitle}</div>
                    <div className="flex justify-between text-sm text-muted-foreground">
                        <span>{formatDuration(lecture.duration)}</span>
                        <span>{formatDate(lecture.enrolledAt)}</span>
                    </div>
                </CardContent>
            </Card>
        </Link>
    );
}