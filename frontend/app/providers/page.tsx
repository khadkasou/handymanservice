"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api, asList } from "@/lib/api";
import { displayName } from "@/lib/format";
import type { User } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner } from "@/components/ui";

export default function ProvidersPage() {
  return (
    <RequireAuth>
      <Providers />
    </RequireAuth>
  );
}

function Providers() {
  const [providers, setProviders] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api<User[]>("/api/v2/provider/user-list", { method: "GET", query: { user_type: "provider" } })
      .then((data) => setProviders(asList(data)))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="stack">
      <div>
        <p className="kicker">People</p>
        <h1>Providers</h1>
      </div>
      <Banner loading={loading} error={error} />
      <div className="list">
        {providers.map((provider) => (
          <Link key={provider.id} href={`/providers/${provider.id}`} className="item">
            <strong>{displayName(provider)}</strong>
            <span className="meta">{provider.designation || provider.address || provider.email}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
