export function money(value?: number | null): string {
  const amount = Number(value ?? 0);
  return new Intl.NumberFormat(undefined, {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2,
  }).format(Number.isFinite(amount) ? amount : 0);
}

export function displayName(user?: {
  displayName?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
} | null): string {
  if (!user) return "Guest";
  const named = user.displayName || [user.firstName, user.lastName].filter(Boolean).join(" ");
  return named || user.email || "Account";
}

export function initials(label?: string): string {
  const parts = (label || "H").trim().split(/\s+/).slice(0, 2);
  return parts.map((part) => part[0]?.toUpperCase() ?? "").join("") || "H";
}

export function when(value?: string | null): string {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: value.includes("T") ? "short" : undefined,
  }).format(date);
}
