// frontend/src/components/ServerWakeup.tsx
"use client";

import { useEffect } from "react";

export default function ServerWakeup() {
  useEffect(() => {
    const springURL = process.env.NEXT_PUBLIC_SPRING_API_URL + "/health";
    const fastapiURL = process.env.NEXT_PUBLIC_FASTAPI_API_URL + "/";

    Promise.all([
      fetch(springURL).then(() => console.log("✅ Spring 서버 깨움")),
      fetch(fastapiURL).then(() => console.log("✅ FastAPI 서버 깨움")),
    ]).catch((err) =>
      console.error("서버 깨우기 실패:", err)
    );
  }, []);

  return null; // UI를 렌더링할 필요는 없으므로 null 반환
}