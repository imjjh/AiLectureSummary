import axios, { AxiosRequestConfig, AxiosError } from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_SPRING_API_URL!;

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // ✅ 쿠키 포함
});

let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

function onRefreshed(token: string) {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const refreshResponse = await axios.post(`${API_BASE_URL}/api/auth/refresh`, {}, {
            withCredentials: true,
          });

          // ✅ access_token은 HttpOnly 쿠키로 오므로 JS에서 꺼낼 건 없음
          isRefreshing = false;
          onRefreshed(""); // 토큰을 직접 쓸 일이 없더라도 콜백 실행
        } catch (refreshError) {
          isRefreshing = false;
          window.location.href = "/login";
          return Promise.reject(refreshError);
        }
      }

      return new Promise((resolve) => {
        subscribeTokenRefresh(() => {
          resolve(api(originalRequest)); // ✅ 재요청
        });
      });
    }

    return Promise.reject(error);
  }
);

export default api;
