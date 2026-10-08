"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Suspense } from "react";
import { api, asList } from "@/lib/api";
import type { Category, Service } from "@/lib/types";
import { Banner, ServiceCard } from "@/components/ui";

function ServicesBrowser() {
  const params = useSearchParams();
  const router = useRouter();
  const search = params.get("search") ?? "";
  const category = params.get("category") ?? "";
  const [draft, setDraft] = useState(search);
  const [categories, setCategories] = useState<Category[]>([]);
  const [services, setServices] = useState<Service[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => setDraft(search), [search]);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);
    const request = search || category
      ? api<Service[]>("/api/v2/catalog/search-list", {
          auth: false,
          method: "GET",
          query: { search: search || undefined, category_id: category || undefined },
        })
      : api<Service[]>("/api/v2/catalog/service-list", { auth: false, method: "GET" });

    Promise.all([
      request,
      api<Category[]>("/api/v2/catalog/category-list", { auth: false, method: "GET" }),
    ])
      .then(([serviceData, categoryData]) => {
        if (!active) return;
        setServices(asList(serviceData));
        setCategories(asList(categoryData));
      })
      .catch((err: Error) => active && setError(err.message))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [search, category]);

  function submit(event: FormEvent) {
    event.preventDefault();
    const next = new URLSearchParams();
    if (draft.trim()) next.set("search", draft.trim());
    if (category) next.set("category", category);
    router.push(next.size ? `/services?${next}` : "/services");
  }

  return (
    <div className="stack">
      <div className="row">
        <div>
          <p className="kicker">Catalog</p>
          <h1>Services</h1>
        </div>
      </div>
      <form className="search" onSubmit={submit}>
        <input value={draft} onChange={(event) => setDraft(event.target.value)} placeholder="Search services" />
        <select
          value={category}
          aria-label="Category"
          onChange={(event) => {
            const next = new URLSearchParams();
            if (draft.trim()) next.set("search", draft.trim());
            if (event.target.value) next.set("category", event.target.value);
            router.push(next.size ? `/services?${next}` : "/services");
          }}
        >
          <option value="">All categories</option>
          {categories.map((item) => (
            <option key={item.id} value={item.id}>{item.name}</option>
          ))}
        </select>
        <button className="button" type="submit">Search</button>
      </form>
      <Banner loading={loading} error={error} />
      {!loading && services.length === 0 ? <p className="meta">No services match that search.</p> : null}
      <div className="grid services">
        {services.map((service) => (
          <ServiceCard key={service.id} service={service} />
        ))}
      </div>
    </div>
  );
}

export default function ServicesPage() {
  return (
    <Suspense fallback={<div className="notice">Loading services…</div>}>
      <ServicesBrowser />
    </Suspense>
  );
}
