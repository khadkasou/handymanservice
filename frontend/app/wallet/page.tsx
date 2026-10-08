"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { money, when } from "@/lib/format";
import type { Wallet, WalletTransaction } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, Field, StatusBadge } from "@/components/ui";

export default function WalletPage() {
  return (
    <RequireAuth>
      <WalletView />
    </RequireAuth>
  );
}

function WalletView() {
  const { session } = useAuth();
  const [wallet, setWallet] = useState<Wallet | null>(null);
  const [history, setHistory] = useState<WalletTransaction[]>([]);
  const [amount, setAmount] = useState("25");
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  async function load() {
    if (!session) return;
    const [balance, rows] = await Promise.all([
      api<Wallet>("/api/v2/payment/user-wallet-balance", { method: "GET", query: { user_id: session.user.id } }),
      api<WalletTransaction[]>("/api/v2/payment/wallet-history", { method: "GET", query: { user_id: session.user.id } }),
    ]);
    setWallet(balance);
    setHistory(asList(rows));
  }

  useEffect(() => {
    load().catch((err: Error) => setError(err.message)).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function topUp(event: FormEvent) {
    event.preventDefault();
    if (!session) return;
    setError(null);
    setNotice(null);
    try {
      await api("/api/v2/payment/wallet-top-up", {
        body: {
          userId: session.user.id,
          amount: Number(amount),
          paymentType: "card",
          txnId: `topup-${Date.now()}`,
        },
      });
      setNotice("Wallet topped up.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Top up failed");
    }
  }

  return (
    <div className="split">
      <section className="stack">
        <p className="kicker">Balance</p>
        <h1>{money(wallet?.amount)}</h1>
        <Banner loading={loading} error={error} />
        {notice ? <div className="notice">{notice}</div> : null}
        <h2>History</h2>
        {history.map((row) => (
          <div key={row.id} className="item">
            <div className="row">
              <strong>{money(row.amount)}</strong>
              <StatusBadge value={row.type || row.activityType} />
            </div>
            <span className="meta">{when(row.datetime)}</span>
          </div>
        ))}
      </section>
      <form className="panel form" onSubmit={topUp}>
        <h2>Top up</h2>
        <Field label="Amount">
          <input type="number" min={1} value={amount} onChange={(event) => setAmount(event.target.value)} />
        </Field>
        <button className="button" type="submit">Add funds</button>
        <Link href="/banks" className="text-link">Bank accounts and withdrawals</Link>
      </form>
    </div>
  );
}
