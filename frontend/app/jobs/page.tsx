"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { money } from "@/lib/format";
import type { Category, PostJob } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, Field, StatusBadge } from "@/components/ui";

export default function JobsPage() {
  return (
    <RequireAuth>
      <Jobs />
    </RequireAuth>
  );
}

function Jobs() {
  const { session } = useAuth();
  const [jobs, setJobs] = useState<PostJob[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ title: "", description: "", price: "", address: "", categoryId: "" });

  async function load() {
    if (!session) return;
    const [jobData, categoryData] = await Promise.all([
      api<PostJob[]>("/api/v2/postjob/get-post-job", { method: "GET", query: { customer_id: session.user.id } }),
      api<Category[]>("/api/v2/catalog/category-list", { auth: false, method: "GET" }),
    ]);
    setJobs(asList(jobData));
    setCategories(asList(categoryData));
  }

  useEffect(() => {
    load().catch((err: Error) => setError(err.message)).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    if (!session) return;
    setError(null);
    try {
      await api("/api/v2/postjob/save-post-job", {
        body: {
          title: form.title,
          description: form.description,
          price: Number(form.price),
          address: form.address,
          categoryId: form.categoryId || undefined,
          customerId: session.user.id,
          status: "pending",
        },
      });
      setForm({ title: "", description: "", price: "", address: "", categoryId: "" });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not post the job");
    }
  }

  async function remove(id: string) {
    setError(null);
    try {
      await api(`/api/v2/postjob/post-job/${id}/status`, { method: "PUT", query: { status: "INACTIVE" } });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not delete the job");
    }
  }

  return (
    <div className="split">
      <section className="stack">
        <div>
          <p className="kicker">Custom requests</p>
          <h1>My jobs</h1>
        </div>
        <Banner loading={loading} error={error} />
        {jobs.map((job) => (
          <article key={job.id} className="item">
            <div className="row">
              <Link href={`/jobs/${job.id}`}><strong>{job.title}</strong></Link>
              <StatusBadge value={job.status} />
            </div>
            <p>{job.description}</p>
            <span className="meta">{job.address} · {money(job.price)}</span>
            <button className="ghost" type="button" onClick={() => void remove(job.id)}>Delete</button>
          </article>
        ))}
      </section>
      <form className="panel form" onSubmit={submit}>
        <h2>Post a job</h2>
        <Field label="Title">
          <input required value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} />
        </Field>
        <Field label="Description">
          <textarea required value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
        </Field>
        <Field label="Budget">
          <input type="number" min={0} required value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} />
        </Field>
        <Field label="Address">
          <input required value={form.address} onChange={(event) => setForm({ ...form, address: event.target.value })} />
        </Field>
        <Field label="Category">
          <select value={form.categoryId} onChange={(event) => setForm({ ...form, categoryId: event.target.value })}>
            <option value="">Any</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>{category.name}</option>
            ))}
          </select>
        </Field>
        <button className="button" type="submit">Post job</button>
      </form>
    </div>
  );
}
