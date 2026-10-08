"use client";

import { FormEvent, useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { money, when } from "@/lib/format";
import type { Booking, Payment, PaymentGateway, ProviderLocation, Review } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, Field, StatusBadge } from "@/components/ui";

export default function BookingDetailPage() {
  return (
    <RequireAuth>
      <BookingDetail />
    </RequireAuth>
  );
}

function BookingDetail() {
  const params = useParams<{ id: string }>();
  const { session } = useAuth();
  const [booking, setBooking] = useState<Booking | null>(null);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [gateways, setGateways] = useState<PaymentGateway[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [rating, setRating] = useState("5");
  const [review, setReview] = useState("");
  const [paymentType, setPaymentType] = useState("wallet");
  const [location, setLocation] = useState<ProviderLocation | null>(null);
  const [handymanRating, setHandymanRating] = useState("5");
  const [handymanReview, setHandymanReview] = useState("");

  async function load() {
    const detail = await api<Booking>("/api/v2/booking/booking-detail", { body: { id: params.id } });
    const [paymentData, reviewData, gatewayData] = await Promise.all([
      api<Payment[]>("/api/v2/payment/payment-list", { method: "GET", query: { booking_id: params.id } }),
      detail.serviceId
        ? api<Review[]>("/api/v2/review/service-reviews", { body: { serviceId: detail.serviceId } }).catch(() => [])
        : Promise.resolve([]),
      api<PaymentGateway[]>("/api/v2/payment/payment-gateways", { method: "GET" }).catch(() => []),
    ]);
    setBooking(detail);
    setPayments(Array.isArray(paymentData) ? paymentData : []);
    setReviews(Array.isArray(reviewData) ? reviewData : []);
    const list = Array.isArray(gatewayData) ? gatewayData : [];
    setGateways(list);
    if (list[0]?.type) setPaymentType(list[0].type);
  }

  useEffect(() => {
    load()
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params.id]);

  async function cancel() {
    if (!booking) return;
    setError(null);
    try {
      const updated = await api<Booking>("/api/v2/booking/booking-update", {
        body: { ...booking, status: "cancelled" },
      });
      setBooking(updated);
      setNotice("Booking cancelled.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not cancel");
    }
  }

  async function pay(event: FormEvent) {
    event.preventDefault();
    if (!booking || !session) return;
    setError(null);
    try {
      await api("/api/v2/payment/save-payment", {
        body: {
          bookingId: booking.id,
          customerId: session.user.id,
          amount: booking.totalAmount ?? booking.amount,
          paymentType,
          txnId: `web-${Date.now()}`,
          paymentStatus: "paid",
        },
      });
      setNotice("Payment recorded.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Payment failed");
    }
  }

  async function rate(event: FormEvent) {
    event.preventDefault();
    if (!booking || !session) return;
    setError(null);
    try {
      await api("/api/v2/review/save-booking-rating", {
        body: {
          bookingId: booking.id,
          serviceId: booking.serviceId,
          customerId: session.user.id,
          providerId: booking.providerId,
          rating: Number(rating),
          review,
        },
      });
      setNotice("Thanks for the rating.");
      setReview("");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save rating");
    }
  }

  async function track() {
    setError(null);
    try {
      const point = await api<ProviderLocation>("/api/v2/booking/get-location", {
        method: "GET",
        query: { booking_id: params.id },
      });
      setLocation(point);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Location is not available yet");
    }
  }

  async function rateHandyman(event: FormEvent) {
    event.preventDefault();
    if (!booking || !session || !booking.handymanId) return;
    setError(null);
    try {
      await api("/api/v2/provider/save-handyman-rating", {
        body: {
          handymanId: booking.handymanId,
          customerId: session.user.id,
          bookingId: booking.id,
          rating: Number(handymanRating),
          review: handymanReview,
        },
      });
      setNotice("Handyman rating saved.");
      setHandymanReview("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not rate the handyman");
    }
  }

  async function invoice() {
    setError(null);
    try {
      await api("/api/v2/booking/download-invoice", { body: { id: params.id } });
      setNotice("Invoice request sent.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not request the invoice");
    }
  }

  if (loading) return <Banner loading />;
  if (!booking) return <Banner error={error || "Booking not found."} />;

  return (
    <div className="split">
      <section className="stack">
        <div className="row">
          <h1>Booking</h1>
          <StatusBadge value={booking.status} />
        </div>
        <div className="panel stack">
          <p>{when(booking.date)} · {booking.bookingSlot || "Time to be confirmed"}</p>
          <p>{booking.address}</p>
          <p className="meta">{booking.description}</p>
          <p className="price">{money(booking.totalAmount ?? booking.amount)}</p>
          <div className="row">
            <button className="ghost" type="button" onClick={() => void track()}>Track provider</button>
            <button className="ghost" type="button" onClick={() => void invoice()}>Email invoice</button>
            <button className="button warn" type="button" onClick={() => void cancel()}>Cancel</button>
          </div>
          {location ? (
            <p>
              {location.address || "Last known location"}{" "}
              {location.latitude != null && location.longitude != null ? (
                <a
                  className="text-link"
                  href={`https://maps.google.com/?q=${location.latitude},${location.longitude}`}
                  target="_blank"
                  rel="noreferrer"
                >
                  Open map
                </a>
              ) : null}
            </p>
          ) : null}
        </div>
        <section className="stack">
          <h2>Payments</h2>
          {payments.length === 0 ? <p className="meta">No payments yet.</p> : null}
          {payments.map((payment) => (
            <div key={payment.id} className="item">
              <div className="row">
                <strong>{money(payment.amount)}</strong>
                <StatusBadge value={payment.paymentStatus} />
              </div>
              <span className="meta">{payment.paymentType} · {when(payment.datetime)}</span>
            </div>
          ))}
        </section>
        <section className="stack">
          <h2>Reviews</h2>
          {reviews.length === 0 ? <p className="meta">No reviews yet.</p> : null}
          {reviews.map((item) => (
            <div key={item.id} className="item">
              <strong>{item.rating} / 5</strong>
              <span>{item.review}</span>
            </div>
          ))}
        </section>
      </section>
      <aside className="stack">
        {error ? <div className="error">{error}</div> : null}
        {notice ? <div className="notice">{notice}</div> : null}
        <form className="panel form" onSubmit={pay}>
          <h2>Pay</h2>
          <Field label="Method">
            <select value={paymentType} onChange={(event) => setPaymentType(event.target.value)}>
              <option value="wallet">Wallet</option>
              {gateways.map((gateway) => (
                <option key={gateway.id} value={gateway.type || gateway.title || gateway.id}>
                  {gateway.title || gateway.type}
                </option>
              ))}
            </select>
          </Field>
          <button className="button" type="submit">Record payment</button>
        </form>
        {booking.handymanId ? (
          <form className="panel form" onSubmit={rateHandyman}>
            <h2>Rate the handyman</h2>
            <Field label="Rating">
              <select value={handymanRating} onChange={(event) => setHandymanRating(event.target.value)}>
                <option>5</option>
                <option>4</option>
                <option>3</option>
                <option>2</option>
                <option>1</option>
              </select>
            </Field>
            <Field label="Review">
              <textarea required value={handymanReview} onChange={(event) => setHandymanReview(event.target.value)} />
            </Field>
            <button className="button secondary" type="submit">Submit handyman rating</button>
          </form>
        ) : null}
        <form className="panel form" onSubmit={rate}>
          <h2>Rate the visit</h2>
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
      </aside>
    </div>
  );
}
