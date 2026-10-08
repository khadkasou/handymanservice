"use client";

import Link from "next/link";
import { initials, money } from "@/lib/format";
import type { Category, Service } from "@/lib/types";

export function StatusBadge({ value }: { value?: string }) {
  const label = (value || "open").toLowerCase();
  const tone =
    label.includes("cancel") || label.includes("fail")
      ? "danger"
      : label.includes("complete") || label.includes("paid") || label.includes("active")
        ? ""
        : "warn";
  return <span className={`badge ${tone}`}>{label.replaceAll("_", " ")}</span>;
}

export function ServiceCard({ service }: { service: Service }) {
  return (
    <Link href={`/services/${service.id}`} className="card">
      <div className="thumb">
        {service.image ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={service.image} alt="" />
        ) : (
          initials(service.name)
        )}
      </div>
      <div className="body stack">
        <div className="row">
          <strong>{service.name || "Service"}</strong>
          {service.isFeatured ? <span className="badge">Featured</span> : null}
        </div>
        <p className="meta">{service.description || service.duration || "On-site service"}</p>
        <div className="row">
          <span className="price">{money(service.price)}</span>
          <span className="meta">{service.duration || service.serviceType || ""}</span>
        </div>
      </div>
    </Link>
  );
}

export function CategoryCard({ category }: { category: Category }) {
  return (
    <Link href={`/categories/${category.id}`} className="card">
      <div className="body stack">
        <strong>{category.name || "Category"}</strong>
        <span className="meta">{category.description || "Browse services"}</span>
      </div>
    </Link>
  );
}

export function Banner({ error, loading }: { error?: string | null; loading?: boolean }) {
  if (loading) return <div className="notice">Loading…</div>;
  if (error) return <div className="error">{error}</div>;
  return null;
}

export function Empty({ children }: { children: React.ReactNode }) {
  return <div className="empty panel">{children}</div>;
}

export function Field({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <label>
      {label}
      {children}
    </label>
  );
}
