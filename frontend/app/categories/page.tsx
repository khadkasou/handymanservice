"use client";

import { useEffect, useState } from "react";
import { api, asList } from "@/lib/api";
import type { Category } from "@/lib/types";
import { Banner, CategoryCard } from "@/components/ui";

export default function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api<Category[]>("/api/v2/catalog/category-list", { auth: false })
      .then((data) => setCategories(asList(data)))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="stack">
      <div>
        <p className="kicker">Browse</p>
        <h1>Categories</h1>
      </div>
      <Banner loading={loading} error={error} />
      {!loading && categories.length === 0 ? <p className="meta">No categories yet.</p> : null}
      <div className="grid cats">
        {categories.map((category) => (
          <CategoryCard key={category.id} category={category} />
        ))}
      </div>
    </div>
  );
}
