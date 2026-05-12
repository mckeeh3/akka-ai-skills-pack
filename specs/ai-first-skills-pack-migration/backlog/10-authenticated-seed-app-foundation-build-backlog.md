# Sprint 10 Build Backlog: Authenticated AI-First Seed App Foundation

## Purpose

Use the working `examples/poc-user-auth-onboarding/` proof-of-concept to plan an authenticated full-stack seed app foundation for the AI-first DCA reference app.

The goal is not to add a generic security sample. The goal is to make the DCA seed app implementable with clear identity, authorization, admin, audit, and frontend hosting boundaries before more AI-first operational slices are added.

## PoC guidance to incorporate

- WorkOS/AuthKit authenticates browser users.
- React/Vite frontend calls Akka APIs with `Authorization: Bearer <token>`.
- Static frontend routes are public; `/api/...` routes are JWT-protected.
- `/api/me` links WorkOS identity to local Akka account state and returns browser-safe roles/scopes.
- Backend authorization uses local Akka state, not frontend route guards or JWT role claims alone.
- `AuthorizationService`-style centralization keeps role/scope checks consistent.
- Users can be invited, activated on first login, disabled, and assigned scoped roles.
- Startup admin bootstrap comes from backend-only environment variables.
- Admin and impersonation actions are audited.
- Frontend build output is served by Akka, while `frontend/.env.local` and backend secrets remain outside packaged assets.

## DCA seed role model

Initial roles for planning:

- `APP_ADMIN`: platform-wide seed administration, tenant setup, global audit access.
- `DEALER_OWNER`: business owner and outcome owner for one dealer/tenant.
- `OPERATIONS_SUPERVISOR`: supervises delegated work, decisions, and exceptions within assigned tenant/customer scopes.
- `POLICY_OWNER`: manages policy proposals, simulation requests, and governed commits within allowed scope.
- `AUDITOR`: reads work, decision, policy, and admin audit traces within scope.
- `CUSTOMER_ADMIN`: customer-scoped administration and visibility.
- `USER`: authenticated baseline access.

Downstream tasks may map these to the simpler PoC role constants when keeping the seed small, but they must preserve tenant/customer scope and AI-first authority semantics.

## Suggested package layout additions

```text
src/main/java/com/example/domain/security/
src/main/java/com/example/application/security/
src/main/java/com/example/api/security/
src/main/java/com/example/security/
frontend/
src/test/java/com/example/application/security/
```

Keep the existing supplies packages separate. Shared security should be reusable by supplies, policy, lifecycle, audit, and future UI surfaces.

## Endpoint/API target list

- `GET /api/me` — current local account, status, roles, scopes, and UI capability hints.
- `GET /api/admin/users` — scoped user list.
- `POST /api/admin/users/invite` — invite user with roles/scopes.
- `POST /api/admin/users/{userId}/roles` — assign/update allowed roles/scopes.
- `POST /api/admin/users/{userId}/disable` — disable local account.
- `POST /api/admin/users/{userId}/activate` — reactivate if policy allows.
- `GET /api/tenants` and `POST /api/tenants` — seed tenant administration.
- `GET /api/tenants/{tenantId}/customers` and `POST /api/tenants/{tenantId}/customers` — scoped customer administration.
- Optional, blocked pending decision: impersonation endpoints/header support.

## Suggested harness task breakdown

### TASK-10-001: Update DCA auth/security app-description

Output: concrete `40-auth-security/` files for identity/trust, authorization, agent permissions, data protection, and boundary/surface rules, plus traceability links to UI and generation slices.

### TASK-10-002: Implement local account, tenant, customer, role, and audit domain

Output: pure domain records and Akka entities for local accounts, tenants/customers, role assignments, account status, bootstrap input, and admin audit entries, with unit tests.

### TASK-10-003: Implement WorkOS/JWT `/api/me` and backend authorization helper

Output: JWT-protected `/api/me`, WorkOS claim extraction/linking, disabled-user rejection, centralized authorization service, and endpoint tests.

### TASK-10-004: Implement admin APIs and bootstrap lifecycle

Output: startup admin bootstrap, invite/role/status/tenant/customer APIs, audit logging, optional email/WorkOS lookup boundaries as stubs, and authorization tests.

### TASK-10-005: Implement authenticated React/Vite shell and Akka hosting

Output: AuthKit provider, same-origin API client, role-aware navigation for DCA surfaces, unauthorized/forbidden states, generated frontend static hosting, and route/build smoke tests.

### TASK-10-006: Add seed security acceptance tests and PoC alignment notes

Output: end-to-end or integration tests for invite/link/activate, role/scope denial, audit, secret boundaries, frontend API auth, and a short doc mapping implemented seed patterns back to PoC guidance.

## Required checks

- Backend compile and focused security tests.
- Endpoint tests for missing JWT, disabled account, cross-scope denial, and privilege-escalation denial.
- Frontend build/test if frontend project exists.
- Static route/asset tests after frontend build output is packaged.
- Secret scan for backend-only variables in frontend source/build output.

## Done criteria

- Future DCA slices can require authenticated users and scoped authorization without inventing a security model.
- The seed app protects `/api/...` routes while keeping frontend assets public.
- `/api/me` drives UX but backend authorization remains authoritative.
- Admin, role, account-status, and optional impersonation behavior is auditable.
- The PoC remains referenced as a working pattern and caveat source, not copied blindly.

## Explicit defer list

- Full compliance controls, rate limiting, and SIEM integration.
- Production email delivery beyond an adapter/stub boundary.
- Autonomous policy-governance changes.
- Impersonation unless explicitly accepted after security review.
- Reworking the completed supplies domain/workflow semantics except to protect its APIs/UI with the new seed security layer.
