"use client";

import { useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { Review } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner } from "@/components/ui";

export default function ReviewsPage() {
  return (
    <RequireAuth>
      <Reviews />
    </RequireAuth>
  );
}

function Reviews() {
  const { session } = useAuth();
  const [reviews, setReviews] = useState<Review[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  async function load() {
    if (!session) return;
    const data = await api<Review[]>("/api/v2/review/get-user-ratings", {
      method: "GET",
      query: { customer_id: session.user.id },
    });
    setReviews(asList(data));
  }

  useEffect(() => {
    load().catch((err: Error) => setError(err.message)).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function remove(id: string) {
    setError(null);
    try {
      await api(`/api/v2/review/booking-rating/${id}/status`, { method: "PUT", query: { status: "INACTIVE" } });
      setNotice("Review deleted.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not delete the review");
    }
  }

  return (
    <div className="stack">
      <div>
        <p className="kicker">Feedback</p>
        <h1>My reviews</h1>
      </div>
      <Banner loading={loading} error={error} />
      {notice ? <div className="notice">{notice}</div> : null}
      {!loading && reviews.length === 0 ? <p className="meta">You have not reviewed a booking yet.</p> : null}
      {reviews.map((item) => (
        <article key={item.id} className="item">
          <strong>{item.rating} / 5</strong>
          <p>{item.review}</p>
          <button className="ghost" type="button" onClick={() => void remove(item.id)}>Delete</button>
        </article>
      ))}
    </div>
  );
}
