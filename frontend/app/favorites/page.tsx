"use client";

import { useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { Service, User } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, ServiceCard } from "@/components/ui";
import { displayName } from "@/lib/format";
import Link from "next/link";

export default function FavoritesPage() {
  return (
    <RequireAuth>
      <Favorites />
    </RequireAuth>
  );
}

function Favorites() {
  const { session } = useAuth();
  const [services, setServices] = useState<Service[]>([]);
  const [providers, setProviders] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  async function load() {
    if (!session) return;
    const [serviceData, providerData] = await Promise.all([
      api<Service[]>("/api/v2/catalog/user-favourite-service", { method: "GET", query: { user_id: session.user.id } }),
      api<User[]>("/api/v2/provider/user-favourite-provider", { method: "GET", query: { user_id: session.user.id } }),
    ]);
    setServices(asList(serviceData));
    setProviders(asList(providerData));
  }

  useEffect(() => {
    load().catch((err: Error) => setError(err.message)).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function removeService(serviceId: string) {
    if (!session) return;
    setError(null);
    try {
      await api("/api/v2/catalog/favourite/status", {
        method: "PUT",
        query: { status: "INACTIVE" },
        body: { userId: session.user.id, serviceId },
      });
      setNotice("Service removed.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not remove the service");
    }
  }

  async function removeProvider(providerId: string) {
    if (!session) return;
    setError(null);
    try {
      await api("/api/v2/provider/favourite-provider/status", {
        method: "PUT",
        query: { status: "INACTIVE" },
        body: { userId: session.user.id, providerId },
      });
      setNotice("Provider removed.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not remove the provider");
    }
  }

  return (
    <div className="stack">
      <h1>Favourites</h1>
      <Banner loading={loading} error={error} />
      {notice ? <div className="notice">{notice}</div> : null}
      <h2>Services</h2>
      <div className="grid services">
        {services.map((service) => (
          <div key={service.id} className="stack">
            <ServiceCard service={service} />
            <button className="ghost" type="button" onClick={() => void removeService(service.id)}>Remove</button>
          </div>
        ))}
      </div>
      <h2>Providers</h2>
      <div className="list">
        {providers.map((provider) => (
          <article key={provider.id} className="item">
            <Link href={`/providers/${provider.id}`}><strong>{displayName(provider)}</strong></Link>
            <span className="meta">{provider.designation || provider.email}</span>
            <button className="ghost" type="button" onClick={() => void removeProvider(provider.id)}>Remove</button>
          </article>
        ))}
      </div>
    </div>
  );
}
