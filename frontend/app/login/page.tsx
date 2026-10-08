"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { LoginResult } from "@/lib/types";
import { Field } from "@/components/ui";

export default function LoginPage() {
  const router = useRouter();
  const { signIn } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setPending(true);
    setError(null);
    try {
      const result = await api<LoginResult>("/api/v2/auth/login", {
        auth: false,
        body: { email, password, loginType: "user" },
      });
      signIn(result);
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="auth-wrap panel stack">
      <p className="kicker">Welcome back</p>
      <h1>Sign in</h1>
      <form className="form" onSubmit={submit}>
        <Field label="Email">
          <input type="email" required value={email} onChange={(event) => setEmail(event.target.value)} />
        </Field>
        <Field label="Password">
          <input type="password" required value={password} onChange={(event) => setPassword(event.target.value)} />
        </Field>
        {error ? <div className="error">{error}</div> : null}
        <button className="button" disabled={pending} type="submit">
          {pending ? "Signing in…" : "Sign in"}
        </button>
      </form>
      <p className="meta">
        <Link href="/forgot-password">Forgot password?</Link>
        {" · "}
        <Link href="/register">Create an account</Link>
      </p>
    </div>
  );
}
