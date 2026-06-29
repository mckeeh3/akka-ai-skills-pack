---
id: RV-MY-ACCOUNT-001
title: Login and account context through My Account
workstream: my-account
surface: my-account-dashboard-profile-context
persona: member
environment: local-dev
dataSetup:
  - base-organization
authMode: workos-test-users
executionMode: human-manual
executionStatus: authored-not-run
readinessClaim: not-run
---

# Purpose

Validate that a signed-in member can enter the My Account workstream, retrieve browser-safe `/api/me` account context, see the correct role/tenant scope, and observe denial or open-disabled behavior without leaking privileged data.

# Prerequisites

- Start the app using `environments/local-dev.md`.
- Prepare `data-setups/base-organization.md`.
- Log in as `personas/member.md` through WorkOS/AuthKit.
- Record whether a disabled or inactive member fixture exists for the denial portion; if absent, classify that portion as `auth/setup gap` rather than passing it.

# Runtime path

`member -> My Account dashboard/profile/context surface -> frontend API client -> /api/me and workstream surface/action endpoints -> account-context-and-profile capability -> identity/MyAccount services and Akka-backed state -> profile/account-context result surfaces plus audit/work trace evidence`

# Surface, adapter, and governed-tool contract

- Surface graph node: My Account dashboard/profile/context.
- Action edge: account-context read and profile/context refresh/update where exposed.
- Actor adapter/source: browser `api_call` and any My Account `surface_action`; human chat plans, if offered, must remain bounded and confirmed before consequential actions.
- Governed tool scope: account/profile/context tools only; no admin, support, provider, or cross-tenant tools exposed to this persona.

# Setup

Use the base organization setup to hand off the local URL, member identity, organization id, and any disabled member id. Setup evidence must be recorded separately from scenario validation evidence.

# Human UI validation script

1. Open the local frontend URL.
2. Log in as `member@example.com` through the configured WorkOS/AuthKit path.
3. Navigate to the My Account dashboard or account/profile surface.
4. Observe the selected account, membership, organization, and browser-safe capability display.
5. Inspect the protected `/api/me` network response if available and confirm it matches the visible account context without server secrets.
6. Attempt to reach an admin-only surface or action from the same session.
7. If a disabled/inactive member is available, repeat login or `/api/me` access with that identity and record the denial or open-disabled behavior.

# Expected results

- The member sees only their own account/profile/context and base organization scope.
- `/api/me` returns browser-safe account, membership, selected AuthContext, and capabilities.
- Admin-only operations are hidden, forbidden, or return a safe denial result.
- Disabled/inactive access fails closed and does not expose protected tenant data.
- Repeating context reads is idempotent and does not create duplicate side effects.
- Audit/work trace evidence records protected context reads, denials, and actor source where implemented.

# Evidence to capture

- Local URL and logged-in persona.
- `/api/me` status and sanitized response excerpt.
- Screenshots or DOM observations for account context and denial behavior.
- Network/API status for forbidden action attempts.
- Audit/work trace ids or logs showing context read and denial evidence.

# Failure classification hints

- `auth/setup gap` for missing WorkOS test-user mapping or disabled-user setup.
- `implementation gap` for missing protected `/api/me`, incorrect tenant scope, or missing denial enforcement.
- `UX/state gap` for ambiguous account context, stale display, or unclear denied/open-disabled copy.
- `test gap` for missing observable trace/audit evidence.
- `frontend secret-boundary gap` if backend/provider secrets appear in browser-visible responses.
