"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth";
import { displayName } from "@/lib/format";

const LINKS = [
  { href: "/", label: "Home" },
  { href: "/categories", label: "Categories" },
  { href: "/services", label: "Services" },
  { href: "/providers", label: "Providers" },
  { href: "/bookings", label: "Bookings" },
  { href: "/jobs", label: "Jobs" },
  { href: "/favorites", label: "Saved" },
  { href: "/wallet", label: "Wallet" },
  { href: "/blog", label: "Blog" },
  { href: "/help", label: "Help" },
];

export function Shell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const { session, signOut } = useAuth();

  return (
    <div className="shell">
      <header className="topbar">
        <Link href="/" className="brand">
          <span className="mark">H</span>
          Handyman
        </Link>
        <nav className="nav">
          {LINKS.map((link) => {
            const active = link.href === "/" ? pathname === "/" : pathname.startsWith(link.href);
            return (
              <Link key={link.href} href={link.href} className={active ? "active" : ""}>
                {link.label}
              </Link>
            );
          })}
        </nav>
        <div className="account">
          {session ? (
            <>
              <Link href="/notifications" className="ghost">Alerts</Link>
              <Link href="/profile">{displayName(session.user)}</Link>
              <button
                className="ghost"
                onClick={() => {
                  void signOut().then(() => router.push("/"));
                }}
              >
                Sign out
              </button>
            </>
          ) : (
            <>
              <Link href="/login" className="ghost">Sign in</Link>
              <Link href="/register" className="button">Create account</Link>
            </>
          )}
        </div>
      </header>
      <main className="page">{children}</main>
    </div>
  );
}

export function RequireAuth({ children }: { children: React.ReactNode }) {
  const { session, ready } = useAuth();
  const router = useRouter();

  if (!ready) return <div className="notice">Loading your account…</div>;
  if (!session) {
    return (
      <div className="panel stack">
        <h2>Sign in to continue</h2>
        <p className="lede">This part of Handyman is tied to your customer account.</p>
        <div>
          <button className="button" onClick={() => router.push("/login")}>Sign in</button>
        </div>
      </div>
    );
  }
  return <>{children}</>;
}
