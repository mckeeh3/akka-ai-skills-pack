# DCA seed security PoC alignment

## Purpose

Map the implemented authenticated DCA seed foundation back to the working `examples/poc-user-auth-onboarding/` proof-of-concept and record caveats for future slices.

## Aligned patterns

- **Route separation:** static React/Vite assets are served publicly from `/`, `/assets/**`, and `/favicon.ico`; backend APIs stay under JWT-protected `/api/...` routes.
- **WorkOS authenticates, Akka authorizes:** bearer JWTs establish browser identity, while local Akka account state controls status, roles, tenant/customer scopes, and capabilities.
- **`/api/me` bootstrap:** invited users are linked to a WorkOS subject and activated on first authenticated `/api/me`; the response is browser-safe and drives UX navigation.
- **Backend-only authorization:** role-aware frontend navigation is convenience only. Admin, tenant, customer, and account lifecycle APIs re-check authorization server-side.
- **Scoped administration:** app admins can manage platform setup; scoped roles can only operate inside allowed tenant/customer boundaries and cannot grant platform authority.
- **Auditable privileged operations:** bootstrap, user invite, account link, role/status changes, and tenant/customer changes create admin audit entries.
- **Secret boundary:** frontend env examples and build outputs must not contain backend-only variables such as `WORKOS_API_KEY`, `RESEND_API_KEY`, `ADMIN_USERS`, or invite-email settings.
- **Akka-hosted frontend build:** the React/Vite project remains the frontend source of record; generated static resources are packaged for Akka hosting.

## Adaptations from the PoC

- Role names reflect the DCA seed operating model (`APP_ADMIN`, `DEALER_OWNER`, `OPERATIONS_SUPERVISOR`, `POLICY_OWNER`, `AUDITOR`, `CUSTOMER_ADMIN`, `USER`) rather than generic sample roles.
- The frontend shell uses DCA supervision, supplies autopilot, admin, tenant/customer, audit, and profile surfaces instead of the PoC's generic pages.
- Impersonation remains intentionally absent from the seed foundation until a product/security decision accepts it.
- The acceptance tests consolidate PoC caveats into reusable seed guarantees: invite/link/activate, scope denial, privilege-escalation denial, disabled-user rejection, audit creation, public asset hosting, protected API rejection, frontend bearer-token behavior, and no backend secrets in frontend artifacts.

## Caveats for future slices

- Production deployments must configure real WorkOS issuer/audience/key validation; local tests may use unsigned tokens only as an integration-test convenience.
- The seed audit entity proves write-side audit creation, but future audit search/reporting surfaces should add views and retention policy.
- Frontend navigation must continue to be treated as UX only as new DCA surfaces are added.
- New supplies, policy, lifecycle, or audit endpoints must reuse the authorization helper or equivalent centralized checks instead of duplicating ad hoc role logic.
- Rate limiting, monitoring/SIEM integration, compliance retention, and richer operational alerting remain outside this seed acceptance scope.
