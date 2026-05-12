# AI Review Notes

## Purpose

This directory is a reference sample for building a full-stack Akka application with a React frontend, WorkOS authentication, role-aware authorization, account lifecycle management, and admin audit logging.

Use it as an architectural and implementation reference, not as a drop-in production security system.

## Architecture summary

- **Backend:** Java Akka SDK service under `src/main/java/com/example`.
- **Frontend:** React + Vite SPA under `frontend/`.
- **Hosting model:** the frontend is built into `src/main/resources/static-resources/` and served by the Akka backend.
- **Auth provider:** WorkOS AuthKit/JWTs for user authentication.
- **Persistence model:** Akka key-value entities for users, tenants, customers, and audit entries.
- **API model:** Akka HTTP endpoints under `/api/...`, with static frontend routes served separately.

Important backend areas:

- `api/` — HTTP endpoints.
- `application/` — Akka entities and state transitions.
- `domain/` — immutable records/enums for users, roles, tenants, customers, and audit entries.
- `security/` — authentication context, authorization checks, impersonation handling, and WorkOS lookup.

## Security model summary

- Endpoints that require users are annotated with JWT bearer-token validation.
- `AuthorizationService` centralizes authentication, user resolution, role checks, impersonation checks, and audit logging.
- Users can be invited before first login and activated when the matching WorkOS identity signs in.
- Roles are scoped:
  - `APP_ADMIN` / `ADMIN` across the app.
  - `TENANT_ADMIN` for one tenant.
  - `CUSTOMER_ADMIN` for one customer within a tenant.
- Admin actions write audit entries with actor/effective user IDs, target metadata, and request context.
- App admins may impersonate active users through `X-Impersonate-User-Id`; impersonation is audited.
- Bootstrap admin users can be created from the `ADMIN_USERS` environment variable.

Security caveats for reviewers:

- Treat this as a sample pattern, not a complete compliance implementation.
- Review role checks before adapting any endpoint.
- Validate JWT issuer/audience and WorkOS configuration for the target environment.
- Decide whether impersonation is appropriate for the target product.
- Add rate limits, monitoring, richer audit metadata, and production-grade error handling as needed.
- Do not copy local `.env.local` values or secrets.

## What to copy

Good candidates to copy or closely adapt:

- The directory split between `api`, `application`, `domain`, and `security`.
- Centralized authorization through `AuthorizationService` rather than scattered inline role checks.
- Scoped role assignment concepts from `Role` and `RoleAssignment`.
- User invitation/activation lifecycle from `UserAccountEntity`.
- Audit-entry creation for privileged actions.
- Frontend/backend build integration described in `docs/frontend-with-akka-backend.md`.
- Product/design docs as reference material for AI UI review.

## What to adapt

Adapt these before use in another app:

- Maven artifact name, package name, and service name.
- WorkOS client/API configuration and redirect URIs.
- Role names, scopes, and permission boundaries.
- Tenant/customer domain model fields.
- Admin bootstrap rules and invitation email copy.
- Frontend routes, labels, and product-specific UI flows.
- Error responses and user-facing failure states.
- Audit retention, query strategy, and operational observability.

## What not to copy directly

Avoid copying these without review:

- Generated build output such as `target/`, `node_modules/`, or `static-resources/`.
- Local environment files such as `.env.local`.
- Any real WorkOS, Resend, or deployment credentials.
- Sample package names like `com.example` unless this remains a throwaway example.
- The exact impersonation model if the destination app has stricter privacy/compliance requirements.

## Review checklist for AI agents

When using this sample as context, inspect:

1. API endpoint annotations and route separation.
2. JWT validation and user resolution flow.
3. Role and scope checks before every privileged action.
4. Entity state transitions for invite, activate, disable, delete, and update.
5. Audit logging on admin and impersonation actions.
6. Frontend AuthKit usage and API request headers.
7. Build flow from `frontend/` to Akka static resources.
8. Any target-repo differences in tenancy, identity, deployment, or compliance needs.
