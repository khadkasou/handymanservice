"use client";

import { FormEvent, useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { HelpdeskTicket } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, Field, StatusBadge } from "@/components/ui";

export default function HelpPage() {
  return (
    <RequireAuth>
      <HelpDesk />
    </RequireAuth>
  );
}

function HelpDesk() {
  const { session } = useAuth();
  const [tickets, setTickets] = useState<HelpdeskTicket[]>([]);
  const [selected, setSelected] = useState<HelpdeskTicket | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ subject: "", description: "" });
  const [reply, setReply] = useState("");

  async function load(id?: string) {
    if (!session) return;
    const list = asList(
      await api<HelpdeskTicket[]>("/api/v2/helpdesk/helpdesk-list", {
        method: "GET",
        query: { customer_id: session.user.id },
      }),
    );
    setTickets(list);
    const nextId = id || selected?.id;
    if (nextId) {
      const detail = await api<HelpdeskTicket>("/api/v2/helpdesk/helpdesk-detail", {
        method: "GET",
        query: { id: nextId },
      });
      setSelected(detail);
    }
  }

  useEffect(() => {
    load().catch((err: Error) => setError(err.message)).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function createTicket(event: FormEvent) {
    event.preventDefault();
    if (!session) return;
    setError(null);
    try {
      const created = await api<HelpdeskTicket>("/api/v2/helpdesk/helpdesk-save", {
        body: {
          customerId: session.user.id,
          subject: form.subject,
          description: form.description,
          status: "open",
          mode: "web",
          type: "support",
        },
      });
      setForm({ subject: "", description: "" });
      await load(created?.id);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not open the ticket");
    }
  }

  async function sendReply(event: FormEvent) {
    event.preventDefault();
    if (!session || !selected) return;
    setError(null);
    try {
      await api(`/api/v2/helpdesk/helpdesk-activity-save/${selected.id}`, {
        body: { senderId: session.user.id, messages: reply, senderType: "user" },
      });
      setReply("");
      await load(selected.id);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not send the reply");
    }
  }

  async function closeTicket() {
    if (!selected) return;
    setError(null);
    try {
      await api(`/api/v2/helpdesk/helpdesk-closed/${selected.id}`, { body: {} });
      await load(selected.id);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not close the ticket");
    }
  }

  return (
    <div className="split">
      <section className="stack">
        <div>
          <p className="kicker">Support</p>
          <h1>Help desk</h1>
        </div>
        <Banner loading={loading} error={error} />
        <div className="list">
          {tickets.map((ticket) => (
            <button key={ticket.id} className="item" type="button" onClick={() => void load(ticket.id)}>
              <div className="row">
                <strong>{ticket.subject}</strong>
                <StatusBadge value={ticket.status} />
              </div>
              <span className="meta">{ticket.description}</span>
            </button>
          ))}
        </div>
        {selected ? (
          <article className="panel stack">
            <div className="row">
              <h2>{selected.subject}</h2>
              <StatusBadge value={selected.status} />
            </div>
            <p>{selected.description}</p>
            {(selected.activities || []).map((activity) => (
              <div key={activity.id} className="item">
                <span className="meta">{activity.senderType}</span>
                <span>{activity.messages}</span>
              </div>
            ))}
            <form className="form" onSubmit={sendReply}>
              <Field label="Reply">
                <textarea value={reply} onChange={(event) => setReply(event.target.value)} />
              </Field>
              <div className="row">
                <button className="button" type="submit">Send</button>
                <button className="ghost" type="button" onClick={() => void closeTicket()}>Close ticket</button>
              </div>
            </form>
          </article>
        ) : null}
      </section>
      <form className="panel form" onSubmit={createTicket}>
        <h2>New ticket</h2>
        <Field label="Subject">
          <input required value={form.subject} onChange={(event) => setForm({ ...form, subject: event.target.value })} />
        </Field>
        <Field label="What do you need?">
          <textarea required value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
        </Field>
        <button className="button" type="submit">Open ticket</button>
      </form>
    </div>
  );
}
