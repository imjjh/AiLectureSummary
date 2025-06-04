"use client";

import { Card, CardContent } from "@/components/ui/card";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import Link from "next/link";

interface Lecture {
    lectureId: number;
    customTitle: string;
    duration: string;
    thumbnailBase64?: string;
    enrolledAt: string;
}

interface LectureCardProps {
    lecture: Lecture;
    onDelete?: (id: number) => void;
}

export default function LectureCard({ lecture, onDelete }: LectureCardProps) {
    const formatDuration = (duration: string): string => {
        const seconds = parseInt(duration, 10);
        if (isNaN(seconds)) return duration;
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
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

    const thumbnailSrc = lecture.thumbnailBase64
        ? `data:image/png;base64,${lecture.thumbnailBase64}`
        : "/images/1.png";

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