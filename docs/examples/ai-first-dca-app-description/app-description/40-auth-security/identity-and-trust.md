# Identity and Trust

## Purpose

Define the authenticated trust model for the AI-first DCA vertical reference. This file aligns the DCA auth/security layer with `../10-capabilities/01-secure-tenant-user-foundation.md`: WorkOS/AuthKit authenticates browser humans; Akka-owned local state authorizes every foundation and DCA capability.

## Identity provider boundary

- Browser users sign in through WorkOS/AuthKit.
- The React/Vite frontend receives a browser access token from AuthKit and calls Akka APIs with `Authorization: Bearer <token>`.
- Akka API endpoints under `/api/...` validate bearer JWTs with `@JWT` before reading request-context claims.
- Public frontend build assets may be served without JWT; protected `/api/...`, stream, admin, decision, trace, agent-tool, and domain APIs require authenticated request context unless explicitly documented otherwise.
- WorkOS secrets, email provider secrets, bootstrap configuration, signing keys, and service credentials are backend-only and must never appear in frontend `VITE_` variables or built assets.

## Akka-owned local authorization state

WorkOS proves external identity only. Local Akka records determine app authority and must include:

- `Account` linked to a WorkOS subject and normalized email;
- `UserProfile` for display/profile fields only;
- `UserSettings` for preferences only;
- `Membership` records scoped to SaaS Owner, Tenant, or Customer with status, roles, expiry where applicable, and audit metadata;
- `Role` and `Permission/Capability` grants used by endpoints, component commands, view queries, workflows, consumers, timers, and agent tools;
- `Invitation` state for invite-only onboarding, including target scope, requested roles/capabilities, token hash or acceptance context, expiry, delivery status, delivery attempts, resend count, idempotency key, and audit metadata;
- selected `AuthContext` for the current account, membership, tenant/customer scope, roles/capabilities, actor metadata, and correlation id.

`UserProfile`, `UserSettings`, frontend navigation, JWT role claims, hidden fields, and prompt text never grant application authority.

## `/api/me` and first-login linking

`/api/me` is the browser-safe bootstrap and context contract. It must:

- validate the WorkOS JWT;
- resolve or link the local `Account` idempotently from WorkOS subject/email;
- require a valid invitation, acceptance context, or explicitly modeled membership policy before activating or attaching privileged access;
- reject expired, revoked, delivery-failed-without-override, cross-scope, or already-accepted-by-another-subject invitations;
- reject disabled local accounts even when the JWT is valid;
- return pending-invite/not-invited status for unknown identities when self-registration is disabled;
- return only browser-safe account, profile, settings, membership summaries, selected/default context, roles/capabilities, and UI capability hints;
- avoid duplicating accounts, widening scopes, or overwriting administrator-managed roles on repeated calls.

Privileged self-registration from WorkOS claims alone is forbidden. Invitation acceptance must be idempotent for the same linked subject and forbidden for a different subject.

## Startup admin bootstrap

Startup bootstrap may create the first SaaS Owner Admin or Tenant Admin from backend-only configuration such as `ADMIN_USERS` only as a bounded setup mechanism.

Rules:

- bootstrap is idempotent and auditable;
- bootstrap may update only fields explicitly owned by bootstrap policy;
- invalid bootstrap configuration fails loudly or surfaces an operational error;
- bootstrap does not create a permanent bypass around invitations, memberships, roles, support-access, tenant/customer scope checks, or the normal Tenant/Customer onboarding lifecycle;
- bootstrap values and provider secrets are never exposed to frontend code or assets.

## Trust boundaries

- WorkOS authenticates; Akka authorizes.
- Backend endpoints re-check active local account status, active membership, selected `AuthContext`, tenant/customer scope, and named permission/capability for every protected operation.
- SaaS Owner authority is limited to platform-safe Tenant setup, Tenant Admin bootstrap, and subscription/billing metadata unless a Tenant-created support-access membership grants time-limited tenant-scoped access.
- Tenant and Customer boundaries are mechanical filters on reads and writes, not UI conventions.
- Agents, workflows, consumers, timers, and tools do not inherit broad human authority implicitly; their effective principal, allowed scope, and permission grant must be represented in backend state and audited.

## Audit and readiness implications

Identity and trust behavior must emit `AdminAuditEvent` and/or work-trace facts for sign-in/linking, account creation/activation/disable/reactivate, profile/settings changes where required, context switches for privileged users, invitation create/delivery/resend/revoke/expire/acceptance/delivery failure, bootstrap-created admins, role/membership changes, support-access lifecycle/use, billing-boundary changes, denials, and rejected privileged actions.

Generation remains blocked until this identity model is linked to tests for `/api/me`, disabled users, uninvited identities, tenant/customer isolation, admin audit emission, and frontend secret-boundary checks.
