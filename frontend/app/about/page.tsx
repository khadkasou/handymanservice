"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import type { AppConfig } from "@/lib/types";
import { Banner } from "@/components/ui";

const PAGES = [
  { key: "about_us", title: "About" },
  { key: "privacy_policy", title: "Privacy policy" },
  { key: "terms_conditions", title: "Terms and conditions" },
  { key: "refund_policy", title: "Refund policy" },
  { key: "help_support", title: "Help and support" },
];

function isLink(value: string) {
  return /^https?:\/\//i.test(value.trim());
}

export default function AboutPage() {
  const [config, setConfig] = useState<Record<string, string>>({});
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api<AppConfig>("/api/v2/config/configurations", { auth: false })
      .then((data) => setConfig(data?.configurations || {}))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const contact = [
    config.inquiry_email ? `Email ${config.inquiry_email}` : "",
    config.helpline_number ? `Helpline ${config.helpline_number}` : "",
    config.website ? config.website : "",
  ].filter(Boolean);

  return (
    <div className="stack">
      <div>
        <p className="kicker">{config.site_name || "Handyman"}</p>
        <h1>About</h1>
        {config.site_description ? <p className="lede">{config.site_description}</p> : null}
      </div>
      <Banner loading={loading} error={error} />
      {contact.length ? <p className="meta">{contact.join(" · ")}</p> : null}
      {PAGES.map((page) => {
        const value = config[page.key];
        if (!value) return null;
        return (
          <section key={page.key} className="panel stack">
            <h2>{page.title}</h2>
            {isLink(value) ? (
              <a className="text-link" href={value} target="_blank" rel="noreferrer">{value}</a>
            ) : (
              <p>{value}</p>
            )}
          </section>
        );
      })}
      {!loading && !error && PAGES.every((page) => !config[page.key]) && !config.site_description ? (
        <p className="meta">No public policies are published yet.</p>
      ) : null}
    </div>
  );
}
