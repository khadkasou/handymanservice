import type { GlobalResponse } from "./types";

const STORAGE_KEY = "handyman.session";

export class ApiError extends Error {
  status?: number;

  constructor(message: string, status?: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

export function apiBase(): string {
  return process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8090";
}

export function readToken(): string | undefined {
  if (typeof window === "undefined") return undefined;
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return undefined;
    const parsed = JSON.parse(raw) as { token?: string };
    return parsed.token || undefined;
  } catch {
    return undefined;
  }
}

type Query = Record<string, string | number | boolean | undefined | null>;

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
  query?: Query;
  auth?: boolean;
}

export async function api<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const url = new URL(path, apiBase());
  if (options.query) {
    for (const [key, value] of Object.entries(options.query)) {
      if (value !== undefined && value !== null && value !== "") {
        url.searchParams.set(key, String(value));
      }
    }
  }

  const headers: Record<string, string> = { Accept: "application/json" };
  if (options.body !== undefined) headers["Content-Type"] = "application/json";
  if (options.auth !== false) {
    const token = readToken();
    if (token) headers.Authorization = `Bearer ${token}`;
  }

  let response: Response;
  try {
    response = await fetch(url.toString(), {
      method: options.method ?? (options.body !== undefined ? "POST" : "GET"),
      headers,
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    });
  } catch {
    throw new ApiError(
      `Cannot reach the API at ${apiBase()}. Start Api-Gateway on port 8090.`,
    );
  }

  let payload: GlobalResponse<T> | null = null;
  const text = await response.text();
  if (text) {
    try {
      payload = JSON.parse(text) as GlobalResponse<T>;
    } catch {
      payload = null;
    }
  }

  if (!response.ok || payload?.status === "FAILED") {
    const message =
      payload?.message ||
      (response.status === 401
        ? "Sign in is required. The gateway only accepts a token Auth-Service marks as active."
        : `Request failed (${response.status})`);
    throw new ApiError(message, response.status);
  }

  return payload?.data as T;
}

export function asList<T>(value: T[] | null | undefined): T[] {
  return Array.isArray(value) ? value : [];
}
