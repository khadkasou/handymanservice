"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { money, when } from "@/lib/format";
import type { Booking, BookingStatus } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, StatusBadge } from "@/components/ui";

export default function BookingsPage() {
  return (
    <RequireAuth>
      <BookingList />
    </RequireAuth>
  );
}

function BookingList() {
  const { session } = useAuth();
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [statuses, setStatuses] = useState<BookingStatus[]>([]);
  const [status, setStatus] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!session) return;
    Promise.all([
      api<Booking[]>("/api/v2/booking/booking-list", { query: { customer_id: session.user.id } }),
      api<BookingStatus[]>("/api/v2/booking/booking-status", { method: "GET" }).catch((): BookingStatus[] => []),
    ])
      .then(([bookingData, statusData]) => {
        setBookings(asList(bookingData));
        setStatuses(asList(statusData));
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [session]);

  const shown = status ? bookings.filter((booking) => (booking.status || "").toLowerCase() === status.toLowerCase()) : bookings;

  return (
    <div className="stack">
      <div>
        <p className="kicker">Your jobs</p>
        <h1>Bookings</h1>
      </div>
      <label>
        Status
        <select value={status} onChange={(event) => setStatus(event.target.value)}>
          <option value="">All</option>
          {statuses.map((item) => (
            <option key={item.id} value={item.value || item.label || ""}>{item.label || item.value}</option>
          ))}
        </select>
      </label>
      <Banner loading={loading} error={error} />
      {!loading && shown.length === 0 ? <p className="meta">You have no bookings yet.</p> : null}
      <div className="list">
        {shown.map((booking) => (
          <Link key={booking.id} href={`/bookings/${booking.id}`} className="item">
            <div className="row">
              <strong>{booking.bookingSlot || "Scheduled visit"}</strong>
              <StatusBadge value={booking.status} />
            </div>
            <span className="meta">{when(booking.date || booking.startAt)} · {booking.address || "Address pending"}</span>
            <span className="price">{money(booking.totalAmount ?? booking.amount)}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
