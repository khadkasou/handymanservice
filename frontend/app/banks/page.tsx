"use client";

import { FormEvent, useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { money } from "@/lib/format";
import type { Bank, Wallet } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Banner, Field } from "@/components/ui";

export default function BanksPage() {
  return (
    <RequireAuth>
      <Banks />
    </RequireAuth>
  );
}

const empty = { bankName: "", branchName: "", accountNumber: "", ifscNo: "", accountHolderName: "" };

function Banks() {
  const { session } = useAuth();
  const [banks, setBanks] = useState<Bank[]>([]);
  const [wallet, setWallet] = useState<Wallet | null>(null);
  const [form, setForm] = useState(empty);
  const [withdraw, setWithdraw] = useState({ bankId: "", amount: "" });
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  async function load() {
    if (!session) return;
    const [bankData, balance] = await Promise.all([
      api<Bank[]>("/api/v2/payment/user-bank-detail", { method: "GET", query: { user_id: session.user.id } }),
      api<Wallet>("/api/v2/payment/user-wallet-balance", { method: "GET", query: { user_id: session.user.id } }),
    ]);
    const list = asList(bankData);
    setBanks(list);
    setWallet(balance);
    const preferred = list.find((bank) => bank.isDefault) || list[0];
    if (preferred) setWithdraw((current) => ({ ...current, bankId: current.bankId || preferred.id }));
  }

  useEffect(() => {
    load().catch((err: Error) => setError(err.message)).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  async function save(event: FormEvent) {
    event.preventDefault();
    if (!session) return;
    setError(null);
    setNotice(null);
    try {
      await api("/api/v2/payment/save-bank", {
        body: { ...form, userId: session.user.id, isDefault: banks.length === 0 },
      });
      setForm(empty);
      setNotice("Bank account saved.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save the bank");
    }
  }

  async function makeDefault(id: string) {
    setError(null);
    try {
      await api("/api/v2/payment/default-bank", { method: "POST", query: { id } });
      setNotice("Default bank updated.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not update the default bank");
    }
  }

  async function remove(id: string) {
    setError(null);
    try {
      await api(`/api/v2/payment/bank/${id}/status`, { method: "PUT", query: { status: "INACTIVE" } });
      setNotice("Bank removed.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not delete the bank");
    }
  }

  async function withdrawFunds(event: FormEvent) {
    event.preventDefault();
    if (!session) return;
    setError(null);
    setNotice(null);
    try {
      await api("/api/v2/payment/withdraw-money", {
        body: { userId: session.user.id, bankId: withdraw.bankId, amount: Number(withdraw.amount) },
      });
      setWithdraw((current) => ({ ...current, amount: "" }));
      setNotice("Withdrawal requested.");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Withdrawal failed");
    }
  }

  return (
    <div className="split">
      <section className="stack">
        <div>
          <p className="kicker">Payouts</p>
          <h1>Bank accounts</h1>
          <p className="meta">Wallet balance {money(wallet?.amount)}</p>
        </div>
        <Banner loading={loading} error={error} />
        {notice ? <div className="notice">{notice}</div> : null}
        {banks.map((bank) => (
          <article key={bank.id} className="item">
            <div className="row">
              <strong>{bank.bankName}</strong>
              {bank.isDefault ? <span className="badge">Default</span> : null}
            </div>
            <span>{bank.accountHolderName}</span>
            <span className="meta">{bank.accountNumber} · {bank.branchName} · {bank.ifscNo}</span>
            <div className="row">
              {bank.isDefault ? null : (
                <button className="ghost" type="button" onClick={() => void makeDefault(bank.id)}>Make default</button>
              )}
              <button className="ghost" type="button" onClick={() => void remove(bank.id)}>Delete</button>
            </div>
          </article>
        ))}
        {!loading && banks.length === 0 ? <p className="meta">No bank accounts yet.</p> : null}
      </section>
      <div className="stack">
        <form className="panel form" onSubmit={save}>
          <h2>Add a bank</h2>
          <Field label="Bank name">
            <input required value={form.bankName} onChange={(event) => setForm({ ...form, bankName: event.target.value })} />
          </Field>
          <Field label="Account holder">
            <input required value={form.accountHolderName} onChange={(event) => setForm({ ...form, accountHolderName: event.target.value })} />
          </Field>
          <Field label="Account number">
            <input required value={form.accountNumber} onChange={(event) => setForm({ ...form, accountNumber: event.target.value })} />
          </Field>
          <Field label="Branch">
            <input value={form.branchName} onChange={(event) => setForm({ ...form, branchName: event.target.value })} />
          </Field>
          <Field label="IFSC">
            <input value={form.ifscNo} onChange={(event) => setForm({ ...form, ifscNo: event.target.value })} />
          </Field>
          <button className="button" type="submit">Save bank</button>
        </form>
        <form className="panel form" onSubmit={withdrawFunds}>
          <h2>Withdraw</h2>
          <Field label="Bank">
            <select required value={withdraw.bankId} onChange={(event) => setWithdraw({ ...withdraw, bankId: event.target.value })}>
              <option value="">Select</option>
              {banks.map((bank) => (
                <option key={bank.id} value={bank.id}>{bank.bankName} · {bank.accountNumber}</option>
              ))}
            </select>
          </Field>
          <Field label="Amount">
            <input type="number" min={1} required value={withdraw.amount} onChange={(event) => setWithdraw({ ...withdraw, amount: event.target.value })} />
          </Field>
          <button className="button secondary" type="submit">Request withdrawal</button>
        </form>
      </div>
    </div>
  );
}
