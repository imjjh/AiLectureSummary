"use client";

import { useEffect, useState } from "react";

export default function Home() {
  const [msg, setMsg] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/hello")
      .then((res) => res.text())
      .then(setMsg)
      .catch(() => setMsg("연결 실패 😢"));
  }, []);

  return (
    <main className="p-8 min-h-screen bg-gray-900 text-white">
      <h1 className="text-4xl font-bold text-blue-500">Tailwind CSS 테스트</h1>
      <p className="mt-4 text-lg">
        이 텍스트가 파란색으로 보인다면 Tailwind CSS가 정상 적용된 것입니다.
      </p>
      <p className="mt-2 text-sm">
        추가 테스트: 버튼에 마우스를 올리면 변화가 있어야 합니다.
      </p>
      <button className="mt-4 px-4 py-2 bg-green-500 hover:bg-green-600 rounded">
        클릭해보세요
      </button>
      <p>{msg}</p>
    </main>
  );
}
