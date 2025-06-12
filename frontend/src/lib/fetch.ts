// lib/fetch.ts
const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL!;

export async function customFetch(input: RequestInfo, init?: RequestInit): Promise<Response> {
  const mergedInit: RequestInit = {
    ...init,
    credentials: "include", // ✅ 기본적으로 include
    headers: {
      ...(init?.headers || {}),
    },
  }

  let response = await fetch(input, mergedInit)

  // 토큰 만료 → 401 발생 시 처리
  if (response.status === 401) {
    try {
      const refreshResponse = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
        method: "POST",
        credentials: "include",
      });

      if (refreshResponse.ok) {
        response = await fetch(input, mergedInit);
      } else {
        return new Response(null, { status: 401 });
      }
    } catch (err) {
      return new Response(null, { status: 401 });
    }
  }

  return response
}