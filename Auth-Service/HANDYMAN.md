# Auth-Service in handyman-solution

Copied from `erp-solution/Auth-Service` with package namespace `com.nepbrain.technology` kept for consistency.

**Role here:** OAuth2 authorization server / opaque-token introspection (`/oauth2/**`, port **8000**).

**Not used for Flutter login/register.** The Handyman Flutter client (`Handyman Service v11.14.3`) calls domain auth endpoints (`login`, `register`, `social-login`, etc.). Those are implemented in **Handyman-Service** under `/api/v2/auth/**` and routed by Api-Gateway.

Gateway routes `/oauth2/**` → this service; `/api/v2/auth/**` → `handyman-service`.
