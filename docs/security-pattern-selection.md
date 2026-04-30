# Security pattern selection

Use this doc when an Akka service needs authentication, authorization, route exposure boundaries, or basic administration.

## Choose the smallest matching skill

| Need | Use |
| --- | --- |
| Description-first security requirements | `app-description-auth-security` |
| WorkOS/AuthKit browser sign-in and JWT-secured `/api/...` calls | `akka-workos-user-auth` |
| Local users, roles, invites, admin bootstrap, `/api/me`, disabling users | `akka-basic-user-admin` |
| HTTP bearer token validation and claim access | `akka-http-endpoint-jwt` |
| HTTP request context, headers, principals, claims | `akka-http-endpoint-request-context` |
| Internal-only HTTP surfaces | `akka-http-endpoint-acl-internal` |
| gRPC JWT security | `akka-grpc-endpoint-jwt` |
| MCP request context/JWT security | `akka-mcp-endpoint-request-context` |

## Common web app security shape

```text
public static frontend
  /, /assets/**

JWT-protected APIs
  /api/me
  /api/admin/...
```

Flow:
1. WorkOS authenticates the browser user.
2. Frontend obtains an access token.
3. Frontend calls Akka with `Authorization: Bearer <token>`.
4. Akka endpoint uses `@JWT` to validate the token.
5. Endpoint reads claims through `requestContext().getJwtClaims()`.
6. Endpoint loads local Akka user/account state.
7. Backend enforces roles and scopes server-side.

## Authentication vs authorization

Authentication answers: who is this caller?
- WorkOS/AuthKit
- JWT issuer/audience/subject/email claims
- Akka `@JWT` validation

Authorization answers: what may this caller do?
- local Akka user status
- roles such as `APP_ADMIN`, `TENANT_ADMIN`, `CUSTOMER_ADMIN`, `USER`
- tenant/customer/self scopes
- business operation-specific rules

Do not let frontend navigation or JWT presence alone authorize admin operations.

## Basic administration defaults

Use these defaults unless product requirements say otherwise:
- `GET /api/me` returns current local profile, status, roles, and scopes
- `APP_ADMIN` can manage global users, tenants, roles
- `TENANT_ADMIN` can manage users/customers only in assigned tenants
- `CUSTOMER_ADMIN` can manage users only in assigned customers
- `USER` can access own profile/dashboard only
- `DISABLED` users are rejected even with valid JWTs
- startup admin bootstrap is idempotent and backend-only

## Secret boundary

Frontend-public:
- WorkOS client id
- redirect URI
- other `VITE_` variables intentionally embedded in the bundle

Backend-only:
- `WORKOS_API_KEY`
- email provider API keys
- invite sender credentials
- JWT key material or deployment secrets
- bootstrap admin configuration when it contains sensitive operational data

## Testing minimum

- missing token rejected
- valid token claims available in request context
- wrong issuer/audience/claim rejected when configured
- `/api/me` returns only browser-facing DTO
- role and tenant/customer scope checks enforced server-side
- disabled user rejected
- admin bootstrap idempotent
- frontend built assets do not contain backend secrets
