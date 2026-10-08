"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { api, asList } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import type { Place, User } from "@/lib/types";
import { RequireAuth } from "@/components/shell";
import { Field } from "@/components/ui";

export default function ProfilePage() {
  return (
    <RequireAuth>
      <Profile />
    </RequireAuth>
  );
}

function Profile() {
  const { session, updateUser, signOut } = useAuth();
  const router = useRouter();
  const user = session!.user;
  const [form, setForm] = useState({
    firstName: user.firstName || "",
    lastName: user.lastName || "",
    email: user.email || "",
    contactNumber: user.contactNumber || "",
    address: user.address || "",
    displayName: user.displayName || "",
    countryId: user.countryId || "",
    stateId: user.stateId || "",
    cityId: user.cityId || "",
  });
  const [countries, setCountries] = useState<Place[]>([]);
  const [states, setStates] = useState<Place[]>([]);
  const [cities, setCities] = useState<Place[]>([]);
  const [passwords, setPasswords] = useState({ oldPassword: "", newPassword: "" });
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    api<Place[]>("/api/v2/geo/country-list", { auth: false, body: {} })
      .then((data) => setCountries(asList(data)))
      .catch(() => setCountries([]));
  }, []);

  useEffect(() => {
    if (!form.countryId) {
      setStates([]);
      return;
    }
    api<Place[]>("/api/v2/geo/state-list", { auth: false, body: { countryId: form.countryId } })
      .then((data) => setStates(asList(data)))
      .catch(() => setStates([]));
  }, [form.countryId]);

  useEffect(() => {
    if (!form.stateId) {
      setCities([]);
      return;
    }
    api<Place[]>("/api/v2/geo/city-list", { auth: false, body: { stateId: form.stateId } })
      .then((data) => setCities(asList(data)))
      .catch(() => setCities([]));
  }, [form.stateId]);

  async function save(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setNotice(null);
    try {
      const updated = await api<User>("/api/v2/auth/update-profile", {
        body: {
          id: user.id,
          ...form,
          countryId: form.countryId || undefined,
          stateId: form.stateId || undefined,
          cityId: form.cityId || undefined,
        },
      });
      updateUser(updated);
      setNotice("Profile saved.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not update profile");
    }
  }

  async function changePassword(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setNotice(null);
    try {
      await api("/api/v2/auth/change-password", { body: passwords });
      setPasswords({ oldPassword: "", newPassword: "" });
      setNotice("Password updated.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not change password");
    }
  }

  async function verifyEmail() {
    setError(null);
    setNotice(null);
    try {
      await api("/api/v2/auth/user-email-verify", { body: { email: form.email || user.email } });
      updateUser({ ...user, emailVerified: true });
      setNotice("Email marked as verified.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not verify the email");
    }
  }

  async function removeAccount() {
    if (!window.confirm("Delete this account?")) return;
    setError(null);
    try {
      await api("/api/v2/auth/delete-user-account", { method: "POST", query: { id: user.id } });
      await signOut();
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not delete the account");
    }
  }

  return (
    <div className="stack">
      <div className="row">
        <Link href="/favorites" className="ghost">Favourites</Link>
        <Link href="/banks" className="ghost">Banks</Link>
        <Link href="/reviews" className="ghost">My reviews</Link>
        <Link href="/about" className="ghost">About</Link>
        <Link href="/help" className="ghost">Help desk</Link>
      </div>
    <div className="split">
      <form className="panel form" onSubmit={save}>
        <h1>Profile</h1>
        <p className="meta">{user.emailVerified ? "Email verified" : "Email not verified"}</p>
        {error ? <div className="error">{error}</div> : null}
        {notice ? <div className="notice">{notice}</div> : null}
        <Field label="Display name">
          <input value={form.displayName} onChange={(event) => setForm({ ...form, displayName: event.target.value })} />
        </Field>
        <Field label="First name">
          <input value={form.firstName} onChange={(event) => setForm({ ...form, firstName: event.target.value })} />
        </Field>
        <Field label="Last name">
          <input value={form.lastName} onChange={(event) => setForm({ ...form, lastName: event.target.value })} />
        </Field>
        <Field label="Email">
          <input type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} />
        </Field>
        <Field label="Phone">
          <input value={form.contactNumber} onChange={(event) => setForm({ ...form, contactNumber: event.target.value })} />
        </Field>
        <Field label="Country">
          <select
            value={form.countryId}
            onChange={(event) => setForm({ ...form, countryId: event.target.value, stateId: "", cityId: "" })}
          >
            <option value="">Select</option>
            {countries.map((place) => (
              <option key={place.id} value={place.id}>{place.name}</option>
            ))}
          </select>
        </Field>
        <Field label="State">
          <select
            value={form.stateId}
            onChange={(event) => setForm({ ...form, stateId: event.target.value, cityId: "" })}
          >
            <option value="">Select</option>
            {states.map((place) => (
              <option key={place.id} value={place.id}>{place.name}</option>
            ))}
          </select>
        </Field>
        <Field label="City">
          <select value={form.cityId} onChange={(event) => setForm({ ...form, cityId: event.target.value })}>
            <option value="">Select</option>
            {cities.map((place) => (
              <option key={place.id} value={place.id}>{place.name}</option>
            ))}
          </select>
        </Field>
        <Field label="Address">
          <input value={form.address} onChange={(event) => setForm({ ...form, address: event.target.value })} />
        </Field>
        <button className="button" type="submit">Save profile</button>
        {user.emailVerified ? null : (
          <button className="ghost" type="button" onClick={() => void verifyEmail()}>Verify email</button>
        )}
        <button className="button warn" type="button" onClick={() => void removeAccount()}>Delete account</button>
      </form>
      <form className="panel form" onSubmit={changePassword}>
        <h2>Password</h2>
        <Field label="Current password">
          <input type="password" value={passwords.oldPassword} onChange={(event) => setPasswords({ ...passwords, oldPassword: event.target.value })} />
        </Field>
        <Field label="New password">
          <input type="password" required value={passwords.newPassword} onChange={(event) => setPasswords({ ...passwords, newPassword: event.target.value })} />
        </Field>
        <button className="button secondary" type="submit">Change password</button>
      </form>
    </div>
    </div>
  );
}
