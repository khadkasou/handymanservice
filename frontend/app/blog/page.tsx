"use client";

import { useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import type { BlogPost } from "@/lib/types";
import { Banner } from "@/components/ui";

export default function BlogPage() {
  const [posts, setPosts] = useState<BlogPost[]>([]);
  const [selected, setSelected] = useState<BlogPost | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api<BlogPost[]>("/api/v2/blog/blog-list", { method: "GET" })
      .then((data) => setPosts(asList(data)))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  async function open(id: string) {
    setError(null);
    try {
      const detail = await api<BlogPost>("/api/v2/blog/blog-detail", { body: { id } });
      setSelected(detail);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not open the article");
    }
  }

  return (
    <div className="split">
      <section className="stack">
        <div>
          <p className="kicker">Stories</p>
          <h1>Blog</h1>
        </div>
        <Banner loading={loading} error={error} />
        <div className="list">
          {posts.map((post) => (
            <button key={post.id} className="item" type="button" onClick={() => void open(post.id)}>
              <strong>{post.title}</strong>
              <span className="meta">{post.authorName}</span>
            </button>
          ))}
        </div>
      </section>
      <article className="panel stack">
        {selected ? (
          <>
            <p className="kicker">{selected.authorName || "Handyman"}</p>
            <h2>{selected.title}</h2>
            <p>{selected.description}</p>
          </>
        ) : (
          <p className="meta">Choose an article to read it.</p>
        )}
      </article>
    </div>
  );
}
