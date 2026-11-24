import axios from "axios";

export const api = axios.create({
  baseURL: "/api",
  withCredentials: true,
  headers: { "Content-Type": "application/json" }
});

api.interceptors.request.use((config) => {
  const method = (config.method || "get").toLowerCase();
  if (!["get", "head", "options"].includes(method)) {
    const item = document.cookie.split("; ").find((part) => part.startsWith("XSRF-TOKEN="));
    if (item) {
      config.headers["X-XSRF-TOKEN"] = decodeURIComponent(item.substring("XSRF-TOKEN=".length));
    }
  }
  return config;
});

export async function ensureCsrf() {
  await api.get("/csrf");
}

export async function loadAuth() {
  const { data } = await api.get("/auth/me");
  return data;
}

export async function demoLogin() {
  await ensureCsrf();
  const { data } = await api.post("/auth/demo-login");
  return data;
}

export async function logout() {
  await ensureCsrf();
  await api.post("/auth/logout");
}
