"use client";

import { FormEvent, useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { initials, money } from "@/lib/format";
import type { Coupon, Service } from "@/lib/types";
import { Banner, Field, StatusBadge } from "@/components/ui";

export default function ServiceDetailPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const { session } = useAuth();
  const [service, setService] = useState<Service | null>(null);
  const [coupons, setCoupons] = useState<Coupon[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [pending, setPending] = useState(false);
  const [couponId, setCouponId] = useState("");
  const [form, setForm] = useState({
    date: "",
    bookingSlot: "09:00-11:00",
    quantity: "1",
    address: "",
    description: "",
  });

  useEffect(() => {
    let active = true;
    api<Service>("/api/v2/catalog/service-detail", { auth: false, body: { id: params.id } })
      .then((data) => active && setService(data))
      .catch((err: Error) => active && setError(err.message))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [params.id]);

  useEffect(() => {
    if (!session) return;
    api<Coupon[]>("/api/v2/booking/coupon-list", { query: { service_id: params.id } })
      .then((data) => setCoupons(Array.isArray(data) ? data : []))
      .catch(() => setCoupons([]));
  }, [params.id, session]);

  async function toggleFavourite() {
    if (!session) {
      router.push("/login");
      return;
    }
    setNotice(null);
    setError(null);
    try {
      await api("/api/v2/catalog/save-favourite", {
        body: { userId: session.user.id, serviceId: params.id },
      });
      setNotice("Saved to favourites.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save favourite");
    }
  }

  async function book(event: FormEvent) {
    event.preventDefault();
    if (!session || !service) {
      router.push("/login");
      return;
    }
    setPending(true);
    setError(null);
    try {
      const quantity = Number(form.quantity) || 1;
      const amount = Number(service.price ?? 0) * quantity;
      const coupon = coupons.find((item) => item.id === couponId);
      let discount = Number(service.discount ?? 0);
      if (coupon?.discount != null) {
        const value = Number(coupon.discount);
        discount = (coupon.discountType || "").toLowerCase().includes("percent") ? (amount * value) / 100 : value;
      }
      const booking = await api<{ id: string }>("/api/v2/booking/booking-save", {
        body: {
          customerId: session.user.id,
          serviceId: service.id,
          providerId: service.providerId,
          date: form.date,
          bookingSlot: form.bookingSlot,
          quantity,
          couponId: couponId || undefined,
          amount,
          discount,
          totalAmount: Math.max(amount - discount, 0),
          address: form.address || session.user.address,
          description: form.description,
          status: "pending",
        },
      });
      router.push(booking?.id ? `/bookings/${booking.id}` : "/bookings");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Booking failed");
    } finally {
      setPending(false);
    }
  }

  if (loading) return <Banner loading />;
  if (error && !service) return <Banner error={error} />;
  if (!service) return <Banner error="Service not found." />;

  return (
    <div className="split">
      <article className="stack">
        <div className="card">
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
              <h1>{service.name}</h1>
              <StatusBadge value={service.status || "active"} />
            </div>
            <p className="lede">{service.description || "A local handyman service."}</p>
            <div className="row">
              <span className="price">{money(service.price)}</span>
              <span className="meta">{service.duration || service.serviceType}</span>
            </div>
            <button className="ghost" type="button" onClick={() => void toggleFavourite()}>
              Save favourite
            </button>
          </div>
        </div>
        {coupons.length ? (
          <section className="panel stack">
            <h2>Coupons</h2>
            {coupons.map((coupon) => (
              <div key={coupon.id} className="item">
                <strong>{coupon.code}</strong>
                <span className="meta">{coupon.description || `${coupon.discount} ${coupon.discountType || ""}`}</span>
              </div>
            ))}
          </section>
        ) : null}
      </article>
      <aside className="panel stack">
        <h2>Book this service</h2>
        <form className="form" onSubmit={book}>
          <Field label="Date">
            <input type="date" required value={form.date} onChange={(event) => setForm({ ...form, date: event.target.value })} />
          </Field>
          <Field label="Time slot">
            <select value={form.bookingSlot} onChange={(event) => setForm({ ...form, bookingSlot: event.target.value })}>
              <option>09:00-11:00</option>
              <option>11:00-13:00</option>
              <option>13:00-15:00</option>
              <option>15:00-17:00</option>
            </select>
          </Field>
          <Field label="Quantity">
            <input type="number" min={1} value={form.quantity} onChange={(event) => setForm({ ...form, quantity: event.target.value })} />
          </Field>
          <Field label="Address">
            <input required value={form.address} onChange={(event) => setForm({ ...form, address: event.target.value })} />
          </Field>
          <Field label="Coupon">
            <select value={couponId} onChange={(event) => setCouponId(event.target.value)}>
              <option value="">None</option>
              {coupons.map((coupon) => (
                <option key={coupon.id} value={coupon.id}>
                  {coupon.code} · {coupon.discount} {coupon.discountType || ""}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Notes">
            <textarea value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
          </Field>
          {error ? <div className="error">{error}</div> : null}
          {notice ? <div className="notice">{notice}</div> : null}
          <button className="button" disabled={pending} type="submit">
            {pending ? "Booking…" : "Book now"}
          </button>
        </form>
      </aside>
    </div>
  );
}
