"use client";

import { FormEvent, useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { displayName } from "@/lib/format";
import type { Review, User } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, Field } from "@/components/ui";

export default function ProviderDetailPage() {
  return (
    <RequireAuth>
      <ProviderDetail />
    </RequireAuth>
  );
}

function ProviderDetail() {
  const params = useParams<{ id: string }>();
  const { session } = useAuth();
  const [provider, setProvider] = useState<User | null>(null);
  const [handymen, setHandymen] = useState<User[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [selected, setSelected] = useState("");
  const [rating, setRating] = useState("5");
  const [review, setReview] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api<User>("/api/v2/provider/user-detail", { method: "GET", query: { id: params.id } }),
      api<User[]>("/api/v2/provider/user-list", { method: "GET", query: { user_type: "handyman" } }).catch((): User[] => []),
    ])
      .then(([detail, people]) => {
        setProvider(detail);
        const peopleList = asList(people);
        const assigned = peopleList.filter((person) => person.providerId === params.id);
        const crew = assigned.length ? assigned : peopleList;
        setHandymen(crew);
        if (crew[0]) setSelected(crew[0].id);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [params.id]);

  useEffect(() => {
    if (!selected) return;
    api<Review[]>("/api/v2/provider/handyman-reviews", { body: { handymanId: selected } })
      .then((data) => setReviews(asList(data)))
      .catch(() => setReviews([]));
  }, [selected]);

  async function favourite() {
    if (!session) return;
    setError(null);
    try {
      await api("/api/v2/provider/save-favourite-provider", {
        body: { userId: session.user.id, providerId: params.id },
      });
      setNotice("Provider saved.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save provider");
    }
  }

  async function rateHandyman(event: FormEvent) {
    event.preventDefault();
    if (!session || !selected) return;
    setError(null);
    try {
      await api("/api/v2/provider/save-handyman-rating", {
        body: {
          handymanId: selected,
          customerId: session.user.id,
          rating: Number(rating),
          review,
        },
      });
      setReview("");
      setNotice("Handyman rating saved.");
      const data = await api<Review[]>("/api/v2/provider/handyman-reviews", { body: { handymanId: selected } });
      setReviews(asList(data));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not rate the handyman");
    }
  }

  if (loading) return <Banner loading />;
  if (!provider) return <Banner error={error || "Provider not found."} />;

  return (
    <article className="panel stack">
      <p className="kicker">{provider.userType || "Provider"}</p>
      <h1>{displayName(provider)}</h1>
      <p>{provider.designation}</p>
      <p className="meta">{provider.email}</p>
      <p>{provider.address}</p>
      {error ? <div className="error">{error}</div> : null}
      {notice ? <div className="notice">{notice}</div> : null}
      <button className="button" type="button" onClick={() => void favourite()}>Save provider</button>
      <h2>Handymen</h2>
      {handymen.length === 0 ? <p className="meta">No handymen listed for this provider.</p> : null}
      {handymen.map((person) => (
        <button
          key={person.id}
          className={selected === person.id ? "button item" : "item"}
          type="button"
          onClick={() => setSelected(person.id)}
        >
          <strong>{displayName(person)}</strong>
          <span className="meta">{person.designation || person.contactNumber}</span>
        </button>
      ))}
      {reviews.map((item) => (
        <div key={item.id} className="item">
          <strong>{item.rating} / 5</strong>
          <span>{item.review}</span>
        </div>
      ))}
      {selected ? (
        <form className="form" onSubmit={rateHandyman}>
          <h2>Rate this handyman</h2>
          <Field label="Rating">
            <select value={rating} onChange={(event) => setRating(event.target.value)}>
              <option>5</option>
              <option>4</option>
              <option>3</option>
              <option>2</option>
              <option>1</option>
            </select>
          </Field>
          <Field label="Review">
            <textarea required value={review} onChange={(event) => setReview(event.target.value)} />
          </Field>
          <button className="button secondary" type="submit">Submit rating</button>
        </form>
      ) : null}
    </article>
  );
}
