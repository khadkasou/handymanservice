"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { api } from "./api";
import type { LoginResult, Session, User } from "./types";

const STORAGE_KEY = "handyman.session";

interface AuthContextValue {
  session: Session | null;
  ready: boolean;
  signIn: (result: LoginResult) => void;
  updateUser: (user: User) => void;
  signOut: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [session, setSession] = useState<Session | null>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) setSession(JSON.parse(raw) as Session);
    } catch {
      localStorage.removeItem(STORAGE_KEY);
    }
    setReady(true);
  }, []);

  const persist = useCallback((next: Session | null) => {
    setSession(next);
    if (next) localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    else localStorage.removeItem(STORAGE_KEY);
  }, []);

  const signIn = useCallback(
    (result: LoginResult) => {
      persist({
        user: result.userData,
        token: result.userData.apiToken ?? "",
      });
    },
    [persist],
  );

  const updateUser = useCallback(
    (user: User) => {
      setSession((current) => {
        if (!current) return current;
        const next = { ...current, user: { ...current.user, ...user } };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
        return next;
      });
    },
    [],
  );

  const signOut = useCallback(async () => {
    const id = session?.user.id;
    persist(null);
    if (id) {
      try {
        await api("/api/v2/auth/logout", { method: "GET", query: { id }, auth: false });
      } catch {
        /* local session is already cleared */
      }
    }
  }, [persist, session?.user.id]);

  const value = useMemo(
    () => ({ session, ready, signIn, updateUser, signOut }),
    [session, ready, signIn, updateUser, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used inside AuthProvider");
  return context;
}
