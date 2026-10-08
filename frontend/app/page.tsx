"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { api, asList } from "@/lib/api";
import type { Category, Service } from "@/lib/types";
import { Banner, CategoryCard, ServiceCard } from "@/components/ui";

export default function HomePage() {
  const router = useRouter();
  const [query, setQuery] = useState("");
  const [categories, setCategories] = useState<Category[]>([]);
  const [services, setServices] = useState<Service[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    Promise.all([
      api<Category[]>("/api/v2/catalog/category-list", { auth: false }),
      api<Service[]>("/api/v2/catalog/service-list", { auth: false }),
    ])
      .then(([categoryData, serviceData]) => {
        if (!active) return;
        setCategories(asList(categoryData));
        setServices(asList(serviceData));
      })
      .catch((err: Error) => active && setError(err.message))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, []);

  function search(event: FormEvent) {
    event.preventDefault();
    const value = query.trim();
    router.push(value ? `/services?search=${encodeURIComponent(value)}` : "/services");
  }

  const featured = services.filter((service) => service.isFeatured);
  const shown = featured.length ? featured : services.slice(0, 8);

  return (
    <>
      <section className="hero">
        <div className="stack">
          <p className="kicker">On-demand home services</p>
          <h1>Find a handyman. Book the time. Track the job.</h1>
          <p className="lede">
            Browse local services, post a custom job, pay from your wallet, and keep every booking in one place.
          </p>
          <form className="search" onSubmit={search}>
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Search plumbing, cleaning, electrical…"
              aria-label="Search services"
            />
            <button className="button" type="submit">Search</button>
          </form>
        </div>
        <aside className="panel stack">
          <h2>How it works</h2>
          <p>Create an account and complete your profile.</p>
          <p>Pick a service, a time slot, and your address.</p>
          <p>Follow the booking, chat with the provider, and pay when it is done.</p>
        </aside>
      </section>

      <Banner loading={loading} error={error} />

      <section className="section stack">
        <div className="row">
          <h2>Categories</h2>
        </div>
        {categories.length === 0 && !loading ? <p className="meta">No categories yet.</p> : null}
        <div className="grid cats">
          {categories.map((category) => (
            <CategoryCard key={category.id} category={category} />
          ))}
        </div>
      </section>

      <section className="section stack">
        <div className="row">
          <h2>{featured.length ? "Featured services" : "Services"}</h2>
        </div>
        {shown.length === 0 && !loading ? <p className="meta">No services published yet.</p> : null}
        <div className="grid services">
          {shown.map((service) => (
            <ServiceCard key={service.id} service={service} />
          ))}
        </div>
      </section>
    </>
  );
}
