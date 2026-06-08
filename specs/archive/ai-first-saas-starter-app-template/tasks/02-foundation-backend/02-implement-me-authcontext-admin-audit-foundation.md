# TASK-STARTER-02-002: Implement `/api/me`, AuthContext, membership, role, and audit foundation

## Purpose

Implement the first executable secure SaaS foundation slice for the starter backend.

## Required reads

- `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `skills/core-saas-foundation/SKILL.md`
- `skills/akka-workos-user-auth/SKILL.md`
- `skills/akka-basic-user-admin/SKILL.md`
- `skills/akka-http-endpoint-jwt/SKILL.md`
- `skills/akka-http-endpoint-request-context/SKILL.md`

## Expected outputs

- Account/Profile/Settings/Tenant/Customer/Membership/Role/Capability/AuthContext/AdminAudit foundation code.
- `/api/me` endpoint and backend tests.

## Done criteria

- `/api/me` returns browser-safe selected context, memberships, capabilities, profile, settings, and functional-agent basis.
- Disabled, no-membership, forbidden, and tenant-mismatch behavior is backend-enforced and tested.
- Required checks pass, queue status is updated, and changes are committed.
