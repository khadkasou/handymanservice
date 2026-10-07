# Handyman Docker infrastructure

Unified compose file at `docker-compose.yml` starts:

| Service | Port(s) | Notes |
|---------|---------|--------|
| Postgres 16 | 5432 | user/pass: `postgres` / `password`; DBs: `handyman`, `users`, `bucket`, `communication`, `customer` |
| Redis 7.2 | 6379 | password `admin` (see `Redis/redis.conf`) |
| Consul 1.22 | 8500, 8600 | ACL token same as erp-solution |
| MinIO | 9000 (API), 9001 (console) | `admin` / `password` |

```bash
cd Docker
docker compose up -d
```

Per-service compose files under `Consul/`, `Redis/`, and `Minio/` match erp-solution and can still be run individually.
