# Sprint 10: Authenticated AI-First Seed App Foundation

## Sprint goal

Turn the working `examples/poc-user-auth-onboarding/` proof-of-concept into an implementation plan for an authenticated full-stack seed app that can host the DCA supplies autopilot reference slice safely.

This sprint is planning for a future implementation increment. It should not copy the PoC blindly; it should adapt its proven WorkOS/AuthKit, JWT, local user administration, role/scope authorization, audit, and React/Vite hosting patterns to the AI-first DCA seed app described under `docs/examples/ai-first-dca-app-description/`.

Execution rule: each Sprint 10 task must make one git commit before being marked `done`; that commit must include only the task's intended changes and its queue-status update.

## Source references

- `examples/poc-user-auth-onboarding/README.md`
- `examples/poc-user-auth-onboarding/AI_REVIEW_NOTES.md`
- `examples/poc-user-auth-onboarding/docs/frontend-with-akka-backend.md`
- `examples/poc-user-auth-onboarding/docs/AI_AGENT_FIRST_APP_SECURITY.md`
- `examples/poc-user-auth-onboarding/docs/PRODUCT.md`
- `examples/poc-user-auth-onboarding/docs/DESIGN.md`
- `docs/security-workos-auth-and-admin.md`
- `docs/examples/ai-first-dca-app-description/app-description/40-auth-security/`
- `docs/examples/ai-first-dca-app-description/app-description/55-ui/`
- `docs/examples/ai-first-dca-app-description/app-description/60-generation/implementation-slices.md`

## Seed app increment

Add the reusable application shell and security substrate that should exist before or alongside additional DCA slices:

- WorkOS/AuthKit browser sign-in and sign-out;
- public React/Vite static frontend routes served by Akka;
- JWT-protected `/api/...` backend routes;
- `/api/me` local account bootstrap and role/scope response;
- Akka-owned user, tenant, customer, role, and account status state;
- startup admin bootstrap from backend-only environment variables;
- invite/link/activate/disable lifecycle;
- role-aware frontend navigation as UX only;
- centralized backend authorization checks for every privileged operation;
- admin audit entries for invite, role, status, tenant/customer, and impersonation decisions when enabled;
- same-origin frontend API calls with bearer tokens;
- frontend unauthorized/forbidden/loading/empty/error states.

## Explicit adaptation decisions from the PoC

- Keep the package/layer separation: `api`, `application`, `domain`, `security`, and `frontend`.
- Keep centralized authorization through an `AuthorizationService`-style helper; do not scatter role checks in endpoint methods.
- Keep local Akka user/account/role state as the authorization source; WorkOS authenticates, Akka authorizes.
- Keep `/api/me` as the browser-facing account and navigation bootstrap contract.
- Keep the Akka-hosted React/Vite build flow, but implement it as a normal frontend project path, not hand-authored static pages.
- Treat impersonation as optional and blocked by a product/security decision before implementation in the DCA seed app.
- Add tests that the PoC currently lacks before considering the seed app production-guidance quality.
- Preserve DCA-specific roles, tenant/customer/device scopes, and AI-first authority boundaries; do not keep generic sample names where domain names are needed.

## Akka component plan

| Need | Suggested component family |
|---|---|
| Local user, tenant, customer, role assignment current state | Key Value Entities unless audit-grade histories are required |
| Audit-grade admin/security activity | Event Sourced Entity or append-only audit entity pattern |
| Startup admin bootstrap | Timed Action or startup component pattern where available; idempotent application service otherwise |
| `/api/me`, admin APIs, tenant/customer APIs | HTTP endpoints with JWT and request context |
| Authorization helper and WorkOS lookup | Plain application/security services called by endpoints/components |
| Role-aware account/admin views | Views if list/search/reporting is needed beyond direct entity reads |
| React authenticated shell | Akka-hosted React/Vite frontend project with AuthKit |

## Delivery order

1. Authoritative DCA auth/security description update.
2. Backend user/account/tenant/customer/role/admin-audit substrate.
3. WorkOS/JWT `/api/me` and protected admin endpoints.
4. React/Vite authenticated shell and static hosting integration.
5. Security, authorization, audit, and frontend smoke tests.

## Acceptance behavior

The sprint is done when the future seed app plan can demonstrate:

```text
unauthenticated browser -> public app shell/login prompt but protected APIs reject
invited WorkOS user signs in -> /api/me links and activates local account
APP_ADMIN -> can invite users, assign allowed roles, and see audit entries
TENANT_ADMIN -> can manage only assigned tenant/customer scope
CUSTOMER_ADMIN -> can manage only assigned customer scope
DISABLED user with valid JWT -> rejected by backend authorization
frontend hidden navigation -> never substitutes for server-side authorization
backend secrets -> never appear in frontend env or built assets
```

## Required tests

- missing/invalid JWT rejection for protected APIs;
- `/api/me` active/invited/disabled account cases;
- idempotent startup admin bootstrap and invite/link behavior;
- role/scope authorization denial for cross-tenant and privilege-escalation attempts;
- audit entry creation for privileged operations and optional impersonation;
- frontend API client attaches bearer tokens and renders unauthorized/forbidden states;
- static frontend route and generated asset serving tests;
- secret-boundary check for frontend env/build output.

## Done criteria

- Planning artifacts make the authenticated seed foundation a first-class implementation increment.
- The DCA app-description has concrete auth/security semantics rather than placeholders.
- Pending tasks are bounded and can be executed after the completed Sprint 8/9 work without redoing the supplies slice.
- The PoC is cited as guidance and adaptation input, not as a drop-in production security system.
