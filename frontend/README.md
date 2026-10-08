# Handyman web

Next.js customer app for the Handyman Service APIs. It talks to **Api-Gateway** at `http://localhost:8090` using `/api/v2/{domain}/...`.

## Run

```bash
cd handymanservice/frontend
npm install
npm run dev
```

Open http://localhost:3000. The gateway already allows that origin.

Copy `.env.example` to `.env.local` if you need a different API host. `.env.local` in this folder points at `http://localhost:8090`.

## What it covers

- Browse categories, search services, and open a service
- Register, sign in, reset password, and edit a profile
- Book a service, review bookings, record a payment, and leave a rating
- Post a custom job, wallet top-up, favourites, providers, blog, help desk, and notifications

Public catalog and auth routes work without a token. Bookings, wallet, help, and the rest send `Authorization: Bearer` using the `apiToken` returned by login. Those routes still go through OAuth introspection on Auth-Service, so they succeed only when that token is accepted as active.
