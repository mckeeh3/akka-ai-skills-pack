# Capability: User and access administration

## Purpose

Let authorized administrators manage users, memberships, roles, invitations, support access, access review, identity relink review, and enterprise identity validation without weakening tenant/customer isolation.

## Actors and scope

- Tenant admins manage tenant employees and tenant-owned customer administration within authorized tenant/customer scope.
- Customer admins manage customer users within their customer scope.
- Auditors read scoped evidence without mutating access.
- User Admin functional agent may recommend, draft, summarize, or prepare decision cards but cannot expand authority without backend policy approval.

## Governed tools and exposure

- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped user, membership, invitation, and support access views.
- `create-or-resend-invitation` (`browser-tool`): invitation lifecycle and Resend/captured-outbox delivery boundary.
- `change-membership-role-or-status` (`browser-tool` with approval when risky): membership role/status changes with idempotency and audit.
- `grant-or-revoke-support-access` (`browser-tool` with expiry/approval): scoped support access changes.
- `run-access-review` (`agent-tool`, `internal-tool`): stale/admin/excessive access review recommendations.

## Authorization and denials

Every command/query is scoped by selected `AuthContext`, role/capability grant, tenant/customer ids, membership status, and approval policy. Cross-tenant/customer access, disabled users, email-only authorization, and prompt-only privilege grants are forbidden.

## Outcomes

In scope: governed user/admin operations, access review recommendations, decision cards for risky changes, audit evidence, and safe local/test email outbox behavior.

Out of scope: public self-registration, app-specific customer billing, and autonomous high-impact access changes.

## Linked graph nodes

- Workstream: `../workstreams/user-admin/workstream.md`
- Tests: `../workstreams/user-admin/tests/coverage.md`
- Traces: `../workstreams/user-admin/traces/work-traces.md`
