"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { money } from "@/lib/format";
import type { PostJob } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, StatusBadge } from "@/components/ui";

export default function JobDetailPage() {
  return (
    <RequireAuth>
      <JobDetail />
    </RequireAuth>
  );
}

function JobDetail() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const [job, setJob] = useState<PostJob | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api<PostJob>("/api/v2/postjob/get-post-job-detail", { body: { id: params.id } })
      .then(setJob)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [params.id]);

  async function remove() {
    setError(null);
    try {
      await api(`/api/v2/postjob/post-job/${params.id}/status`, { method: "PUT", query: { status: "INACTIVE" } });
      router.push("/jobs");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not delete the job");
    }
  }

  if (loading) return <Banner loading />;
  if (!job) return <Banner error={error || "Job not found."} />;

  return (
    <article className="panel stack">
      <div className="row">
        <h1>{job.title}</h1>
        <StatusBadge value={job.status} />
      </div>
      <p>{job.description}</p>
      <p className="meta">{job.address}</p>
      <p className="price">{money(job.price)}</p>
      {error ? <div className="error">{error}</div> : null}
      <button className="button warn" type="button" onClick={() => void remove()}>Delete job</button>
    </article>
  );
}
