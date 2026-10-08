# handyman-solution

Java Spring Boot backend for the **Handyman Service v11.14.3** Flutter customer app (`booking_system_flutter`).

This is **not** a Laravel project. The original Iqonic Handyman stack used a separate Laravel admin/API product; this repository is a **greenfield Java migration** that follows the same microservice patterns as [`erp-solution`](../erp-solution) (Spring Boot, Consul, Redis, MinIO, Api-Gateway).

Package naming stays **`com.nepbrain.technology`** for shared libraries and consistency with erp-solution.

---

## Client

| Item | Value |
|------|--------|
| Flutter app | `Handyman Service v11.14.3` |
| Next.js web | `frontend` (customer UI on port 3000) |
| Legacy base path | `BASE_URL = $DOMAIN_URL/api/` → calls like `/api/login` |
| Target gateway | `http://localhost:8090` |
| Target API style | `/api/v2/{domain}/{endpoint}` |

See [docs/MIGRATION.md](docs/MIGRATION.md) for endpoint → controller mapping.

---

## Service map

| Service | Role | Port | Consul name |
|---------|------|------|-------------|
| **Api-Gateway** | Spring Cloud Gateway | **8090** | API Gateway / api-gateway |
| **Handyman-Service** | Domain APIs (auth, booking, catalog, …) | **8020** | `handyman-service` |
| **Auth-Service** | OAuth2 / token introspect only | 8000 | `auth-service` |
| **Bucket-Service** | File uploads (MinIO/S3) | 8006 | `bucket-service` |
| **Communication-Service** | OTP / SMS / email | 8011 | `communication-service` |
| **Customer-Service** | Optional ERP-style customer master | 8004 | `customer-service` |

Infrastructure (Docker): Postgres **5432**, Redis **6379**, Consul **8500**, MinIO **9000/9001**.

---

## Gateway routes → Handyman-Service

All of the following go to `lb://handyman-service`:

| Path | Domain |
|------|--------|
| `/api/v2/auth/**` | Login, register, profile, password |
| `/api/v2/booking/**` | Bookings, status, location |
| `/api/v2/catalog/**` | Categories, services, search, dashboard |
| `/api/v2/provider/**` | Providers, handymen, favourites |
| `/api/v2/payment/**` | Payments, wallet, banks, payouts |
| `/api/v2/postjob/**` | Post-job / custom service requests |
| `/api/v2/review/**` | Service & handyman ratings |
| `/api/v2/helpdesk/**` | Help desk tickets |
| `/api/v2/geo/**` | Country / state / city |
| `/api/v2/config/**` | App configurations |
| `/api/v2/notification/**` | In-app notifications, firebase-detail |
| `/api/v2/blog/**` | Blog list / detail |

Supporting routes:

| Path | Target |
|------|--------|
| `/oauth2/**` | Auth-Service |
| `/api/v2/bucket/**` | Bucket-Service |
| `/api/v2/communication/**` | Communication-Service |
| `/api/v2/customer/**` | Customer-Service (optional) |

---

## Domain modules (Handyman-Service)

Packages under `com.nepbrain.technology` (same layering as erp-solution: annotation → controller → entity → repository → service/impl → payloads):

| Module | Responsibility |
|--------|----------------|
| `auth` | register, login, social-login, profile, password, email verify, delete account |
| `catalog` | dashboard, categories, subcategories, services, search |
| `booking` | booking CRUD, status, provider location tracking |
| `provider` | provider/handyman list & detail, favourites |
| `payment` | gateways, save-payment, wallet, banks, payout, withdraw |
| `postjob` | save/list/detail/delete post-job, service-save |
| `review` | booking & handyman ratings |
| `helpdesk` | helpdesk list/detail/save/activity/close |
| `geo` | country / state / city lists |
| `config` | configurations |
| `notification` | notification-list, firebase-detail |
| `blog` | blog-list, blog-detail |

---

## Flutter path mapping

Flutter calls relative endpoints under `/api/{endpoint}`. Java exposes versioned domain paths:

```
Flutter:  GET  /api/login
Java:     POST /api/v2/auth/login

Flutter:  GET  /api/booking-list?...
Java:     GET  /api/v2/booking/booking-list?...

Flutter:  GET  /api/category-list?...
Java:     GET  /api/v2/catalog/category-list?...
```

The Flutter app can either:

1. Point `DOMAIN_URL` at the gateway and add a thin rewrite layer, or  
2. Update `rest_apis.dart` / `network_utils.dart` to call `/api/v2/{domain}/...` directly.

Full table: [docs/MIGRATION.md](docs/MIGRATION.md).

---

## How to run

### Requirements

- **Java 25** (`JAVA_HOME` → e.g. `C:\Program Files\Java\jdk-25.0.4`)
- Gradle wrapper **9.8.1** (bundled per service)
- Docker (Postgres, Redis, Consul, MinIO)

### 1. Infrastructure

```bash
cd Docker
docker compose up -d
```

Creates Postgres DBs: `handyman`, `users`, `bucket`, `communication`, `customer`.

### 2. Build Handyman-Service

From repo root:

```bat
gradle-clean-build.bat
```

Or a specific service:

```bat
gradle-clean-build.bat Api-Gateway
gradle-clean-build.bat Auth-Service
```

### 3. JVM and process environment

Postgres on this host rejects `Asia/Katmandu`. Start every Java process with:

```bat
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
set JAVA_HOME=C:\Program Files\Java\jdk-25
```

Consul registration uses the process environment. Set the name explicitly and clear a leaked value before each service:

| Service | `SPRING_CLOUD_CONSUL_DISCOVERY_SERVICE_NAME` | Port |
|---------|-----------------------------------------------|------|
| Handyman-Service | `handyman-service` | 8020 |
| Api-Gateway | `API-Gateway` | 8090 |
| Auth-Service | `auth-service` | 8000 |
| Customer-Service | `customer-service` | 8004 |
| Bucket-Service | `bucket-service` | 8006 |
| Communication-Service | `communication-service` | 8011 |

Redis in Docker (`edx-redis`) has no username and no password. If the gateway logs `AUTH failed`, set `SPRING_DATA_REDIS_USERNAME` and `SPRING_DATA_REDIS_PASSWORD` to empty.

Local database password matches Docker (`password`). Override it with `DB_PASSWORD` when the database is not the local compose file. OAuth client id/secret default to `client` / `secret` for local introspect. Override with `AUTH_CLIENT_ID` and `AUTH_CLIENT_SECRET`. SMS credentials are `SMS_USERNAME` and `SMS_PASSWORD`. They are not stored in the service yaml.

Seeded Auth super admin email: `superadmin@hrmsuite.com`. The password is stored as bcrypt only. Local web test customer: `webtest@example.com` / `secret123`.

### 4. Start order

1. Docker infra (Consul, Redis, Postgres, MinIO)
2. Auth-Service
3. Handyman-Service
4. Customer-Service, Bucket-Service, Communication-Service
5. Api-Gateway on **8090**
6. Next.js web: `frontend` on **3000** (`NEXT_PUBLIC_API_BASE_URL=http://localhost:8090`)

The gateway lets `/api/v2/auth`, booking, catalog, provider, payment, postjob, review, helpdesk, notification, blog, config, and geo through without OAuth introspection. Handyman-Service then accepts either an Auth opaque token or the customer `apiToken` stored on the user.

Communication bulk send loads selected users with `POST ${services.user}detail/micro`. There is no user-service module in this repo. `services.user` must point at a running user API.

### 5. Smoke check

- Gateway health: `http://localhost:8090/actuator/health`  
- Consul UI: `http://localhost:8500`  
- MinIO console: `http://localhost:9001`

---

## Notes

- **Handyman-Service** is the primary domain service; do not confuse it with erp-solution’s Employee-Service even if some scaffold files were reused.  
- Auth-Service is **OAuth2 infrastructure**, not the Flutter `login`/`register` API.  
- Customer-Service is optional; customer UX endpoints belong in Handyman-Service.  
- Each supporting service folder has a short `HANDYMAN.md` describing its role in this repo.
