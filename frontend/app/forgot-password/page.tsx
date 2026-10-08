"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { api } from "@/lib/api";
import { Field } from "@/components/ui";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setPending(true);
    setError(null);
    setMessage(null);
    try {
      await api("/api/v2/auth/forgot-password", { auth: false, body: { email } });
      setMessage("If that email is registered, a reset message is on the way.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not send the reset request");
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="auth-wrap panel stack">
      <h1>Reset password</h1>
      <form className="form" onSubmit={submit}>
        <Field label="Email">
          <input type="email" required value={email} onChange={(event) => setEmail(event.target.value)} />
        </Field>
        {error ? <div className="error">{error}</div> : null}
        {message ? <div className="notice">{message}</div> : null}
        <button className="button" disabled={pending} type="submit">Send reset link</button>
      </form>
      <Link href="/login">Back to sign in</Link>
    </div>
  );
}
