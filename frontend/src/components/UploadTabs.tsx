// components/UploadTabs.tsx
"use client";

import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Upload } from "lucide-react";
import { useState, useCallback } from "react";
import VideoUploader from "@/components/video-uploader";
import { toast } from "@/hooks/use-toast";
import { motion } from "framer-motion"
import { Progress } from "@/components/ui/progress";
import { useAuth } from "@/hooks/use-auth";
import { useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

export default function UploadTabs() {
  const [youtubeUrl, setYoutubeUrl] = useState("");
  const [audioFile, setAudioFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const { user } = useAuth();
  const router = useRouter();

  const handleYouTubeSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!youtubeUrl.trim()) return;

    try {
      const res = await fetch(`${API_BASE_URL}/api/lecture/youtube`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ url: youtubeUrl }),
      });

      const data = await res.json();

      if (!res.ok || !data.success) {
        throw new Error(data.message || "요약 실패");
      }

      const lectureId = data.data.id;
      router.push(`/summary/${lectureId}`);
    } catch (error: any) {
      toast({
        title: "요약 실패",
        description: error.message || "유튜브 요약 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  const handleAudioUpload = useCallback(async () => {
    if (!user) {
        toast({
            title: "로그인이 필요합니다‼️",
            description: "녹음 파일을 업로드하려면 먼저 로그인해주세요.",
            duration: 1000,
        });
        return;
    }
    if (!audioFile) return;

    setUploading(true);
    setProgress(0);

    try {
        const formData = new FormData();
        formData.append("file", audioFile);

        const response = await new Promise<any>((resolve, reject) => {
            const xhr = new XMLHttpRequest();

            xhr.upload.addEventListener("progress", event => {
                if (event.lengthComputable) {
                    setProgress(Math.round((event.loaded / event.total) * 100));
                }
            });

            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        resolve(JSON.parse(xhr.responseText));
                    } else {
                        reject(new Error(`HTTP ${xhr.status}: ${xhr.responseText || xhr.statusText}`));
                    }
                }
            };

            xhr.open("POST", `${API_BASE_URL}/api/lecture/upload`);
            xhr.withCredentials = true;
            xhr.send(formData);
        });

        const lectureId = response.data.id;
        router.push(`/summary/${lectureId}`);
    } 
    catch (error: any) {
        toast({  
+            title: "업로드 실패",  
+            description: error.message || "오디오 파일 업로드 중 오류가 발생했습니다.",  
+            variant: "destructive",  
+        });  
+        console.error("Upload Error:", error); 
    } finally {
        setUploading(false);
        setProgress(0);
    }
}, [audioFile, router, user]);

const sharedWrapper = "border-2 border-dashed rounded-xl p-6 text-center flex flex-col items-center justify-center gap-4";

  return (
    <Tabs defaultValue="video" className="w-full">
        <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="video">영상 파일</TabsTrigger>
            <TabsTrigger value="audio">녹음 파일</TabsTrigger>
            <TabsTrigger value="youtube">YouTube 링크</TabsTrigger>
        </TabsList>

        <TabsContent value="video" className="pt-6">
            <VideoUploader />
        </TabsContent>

        <TabsContent value="audio" className="pt-6">
            <div className={sharedWrapper}>
                <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}
                    onClick={() => document.getElementById("audio-upload")?.click()}>
                    <div className="w-20 h-20 rounded-full bg-gradient-to-r from-pink-500 to-orange-500 flex items-center justify-center">
                        <Upload className="h-10 w-10 text-white" />
                    </div>
                </motion.div>
                <div>
                    <h3 className="text-lg font-medium">음성 파일 업로드</h3>
                    <p className="text-sm text-muted-foreground">녹음 파일(MP3, WAV 등)을 업로드하세요</p>
                </div>
                <input
                    id="audio-upload"
                    type="file"
                    accept="audio/*"
                    className="hidden"
                    onChange={(e) => {
                        if (e.target.files?.[0]) setAudioFile(e.target.files[0]);
                    }}
                />
                <label htmlFor="audio-upload" className="cursor-pointer">
                    <Button 
                        variant="outline" 
                        className="cursor-pointer rounded-full px-6 bg-background dark:bg-gray-800 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                        asChild
                        >
                        <span>파일 선택</span>
                    </Button>
                </label>
            </div>
            
            {audioFile && (
                <>
                    <p className="text-sm font-medium mb-2">
                        {audioFile.name} ({(audioFile.size / (1024 * 1024)).toFixed(2)}MB)
                        <Button variant="ghost" size="sm" onClick={() => setAudioFile(null)} className="ml-2 text-destructive">
                            취소
                        </Button>
                    </p>
                    <div className="mt-4 space-y-2">
                        {uploading ? (
                            <>
                                <Progress value={progress} className="h-2 rounded-full" />
                                <div className="flex justify-between text-xs text-muted-foreground">
                                    <span>업로드 진행률: {progress}%</span>
                                    <span>{progress === 100 ? "요약 생성 중..." : "업로드 중"}</span>
                                </div>
                          </>
                        ) : (
                            <Button
                                onClick={handleAudioUpload}
                                className="w-full mt-2 rounded-full bg-gradient-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0 text-white"
                            >
                                요약 시작
                            </Button>
                        )}
                    </div>
                </>
            )}
        </TabsContent>

        <TabsContent value="youtube" className="pt-2">
        <div className={sharedWrapper}>
          <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}>
            <div className="w-20 h-20 rounded-full bg-gradient-to-r from-pink-500 to-orange-500 flex items-center justify-center">
              <Upload className="h-10 w-10 text-white" />
            </div>
          </motion.div>
          <h3 className="text-lg font-medium">유튜브 업로드</h3>
          <p className="text-sm text-muted-foreground">
            YouTube 링크를 붙여넣으면 요약이 시작됩니다.
          </p>
          <Input
            type="url"
            placeholder="https://www.youtube.com/watch?v=..."
            value={youtubeUrl}
            onChange={(e) => setYoutubeUrl(e.target.value)}
          />
        </div>
        {youtubeUrl && (
          <div className="mt-4">
            <Button
              onClick={handleYouTubeSubmit}
              className="w-full flex items-center justify-center gap-2 rounded-full bg-gradient-to-r from-pink-500 to-orange-500 hover:from-pink-600 hover:to-orange-600 border-0 text-white"
            >
              <Upload size={16} /> 요약 시작
            </Button>
          </div>
        )}
      </TabsContent>
    </Tabs>
  );
}

