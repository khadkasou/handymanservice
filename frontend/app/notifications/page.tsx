"use client";

import { useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { AppNotification } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner } from "@/components/ui";

export default function NotificationsPage() {
  return (
    <RequireAuth>
      <Notifications />
    </RequireAuth>
  );
}

function Notifications() {
  const { session } = useAuth();
  const [items, setItems] = useState<AppNotification[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!session) return;
    api<AppNotification[]>("/api/v2/notification/notification-list", {
      query: { customer_id: session.user.id },
      body: {},
    })
      .then((data) => setItems(asList(data)))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [session]);

  return (
    <div className="stack">
      <h1>Notifications</h1>
      <Banner loading={loading} error={error} />
      {!loading && items.length === 0 ? <p className="meta">No notifications.</p> : null}
      <div className="list">
        {items.map((item) => (
          <article key={item.id} className="item">
            <strong>{item.subject || item.type || "Update"}</strong>
            <span>{item.message}</span>
          </article>
        ))}
      </div>
    </div>
  );
}
