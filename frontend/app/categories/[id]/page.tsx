"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { api, asList } from "@/lib/api";
import type { Category, Service } from "@/lib/types";
import { Banner, ServiceCard } from "@/components/ui";

export default function CategoryDetailPage() {
  const params = useParams<{ id: string }>();
  const [subs, setSubs] = useState<Category[]>([]);
  const [services, setServices] = useState<Service[]>([]);
  const [subId, setSubId] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    Promise.all([
      api<Category[]>("/api/v2/catalog/subcategory-list", {
        auth: false,
        method: "GET",
        query: { category_id: params.id },
      }),
      api<Service[]>("/api/v2/catalog/search-list", {
        auth: false,
        method: "GET",
        query: { category_id: params.id },
      }),
    ])
      .then(([subData, serviceData]) => {
        if (!active) return;
        setSubs(asList(subData));
        setServices(asList(serviceData));
      })
      .catch((err: Error) => active && setError(err.message))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [params.id]);

  const shown = subId ? services.filter((service) => service.subcategoryId === subId) : services;

  return (
    <div className="stack">
      <div>
        <p className="kicker">Category</p>
        <h1>Services</h1>
      </div>
      <Banner loading={loading} error={error} />
      {subs.length ? (
        <div className="row">
          <button className={subId ? "ghost" : "button"} type="button" onClick={() => setSubId("")}>
            All
          </button>
          {subs.map((sub) => (
            <button
              key={sub.id}
              className={subId === sub.id ? "button" : "ghost"}
              type="button"
              onClick={() => setSubId(sub.id)}
            >
              {sub.name}
            </button>
          ))}
        </div>
      ) : null}
      {!loading && shown.length === 0 ? <p className="meta">No services in this category yet.</p> : null}
      <div className="grid services">
        {shown.map((service) => (
          <ServiceCard key={service.id} service={service} />
        ))}
      </div>
    </div>
  );
}
