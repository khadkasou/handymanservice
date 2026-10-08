"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { LoginResult } from "@/lib/types";
import { Field } from "@/components/ui";

export default function RegisterPage() {
  const router = useRouter();
  const { signIn } = useAuth();
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    contactNumber: "",
    password: "",
    address: "",
  });
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  function set<K extends keyof typeof form>(key: K, value: string) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    setPending(true);
    setError(null);
    try {
      const result = await api<LoginResult>("/api/v2/auth/register", {
        auth: false,
        body: { ...form, username: form.email, userType: "USER", loginType: "user" },
      });
      signIn(result);
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not create the account");
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="auth-wrap panel stack">
      <p className="kicker">New customer</p>
      <h1>Create your account</h1>
      <form className="form" onSubmit={submit}>
        <Field label="First name">
          <input required value={form.firstName} onChange={(event) => set("firstName", event.target.value)} />
        </Field>
        <Field label="Last name">
          <input required value={form.lastName} onChange={(event) => set("lastName", event.target.value)} />
        </Field>
        <Field label="Email">
          <input type="email" required value={form.email} onChange={(event) => set("email", event.target.value)} />
        </Field>
        <Field label="Contact number">
          <input value={form.contactNumber} onChange={(event) => set("contactNumber", event.target.value)} />
        </Field>
        <Field label="Address">
          <input value={form.address} onChange={(event) => set("address", event.target.value)} />
        </Field>
        <Field label="Password">
          <input type="password" required value={form.password} onChange={(event) => set("password", event.target.value)} />
        </Field>
        {error ? <div className="error">{error}</div> : null}
        <button className="button" disabled={pending} type="submit">
          {pending ? "Creating…" : "Create account"}
        </button>
      </form>
      <p className="meta">
        Already registered? <Link href="/login">Sign in</Link>
      </p>
    </div>
  );
}
