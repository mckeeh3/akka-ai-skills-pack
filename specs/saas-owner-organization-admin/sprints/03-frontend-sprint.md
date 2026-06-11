# Sprint 03: Frontend Organization Admin Surface

## Goal

Expose SaaS Owner Organization Admin through the canonical workstream browser UX without creating frontend-only authorization.

## Scope

- Add typed frontend API client methods and DTOs.
- Add Organization list/detail/action surface under the current User Admin/SaaS Owner shell.
- Add loading, empty, forbidden, validation, no-op, stale/conflict, success, and failure states.
- Add frontend tests/typecheck/build validation.

## Acceptance

- Authorized SaaS Owner Admins see organization management actions.
- Tenant Admin, Customer Admin, and unsupported contexts do not see actions and receive safe denials if deep-linked/API-called.
- Copy consistently says Organization in UI and explains the tenant/app-data boundary.
- Frontend payloads contain no provider secrets, hidden data, or authority-granting state.
