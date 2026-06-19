# SaaS Owner Organization Admin

## Purpose

Create a durable mini-project for adding SaaS Owner Admin capability to manage customer-facing Organizations backed by internal Tenant boundaries in the runnable secure AI-first SaaS core app.

The goal is to provide a backend-authorized, audited, browser-safe Organization administration vertical for SaaS Owner Admins without weakening tenant/customer isolation or turning billing/support authority into application-data access.

## Source discussion / trigger

The user asked to add capability for SaaS owner admins to manage tenants/organizations. Review found that the existing foundation already anticipates the concept:

- UI/product terminology uses **Organization** while code/security isolation uses **Tenant**.
- `FoundationRole.SAAS_OWNER_ADMIN` exists with `saas_owner.tenant.read`, `saas_owner.tenant.manage`, `saas_owner.user.manage`, `saas_owner.audit.read`, and `saas_owner.billing_boundary.manage` capabilities.
- `Tenant` and `Customer` state already exist in the identity repository, with durable Akka persistence seams.
- User Admin app-description already distinguishes app-owner/SaaS Owner support from tenant/customer administration and warns that SaaS Owner support access must not imply tenant application-data access.

Current gaps are a missing Organization Admin contract, missing tenant list/search repository methods, missing SaaS Owner organization service/API/UI, and missing tests for safe authorization/audit semantics.

## Scope

This mini-project targets root app-facing assets:

- `app-description/**`
- `specs/saas-owner-organization-admin/**`
- `src/main/java/ai/first/**`
- `src/test/java/ai/first/**`
- `frontend/**`

## Done state

This mini-project is complete when SaaS Owner Admins can manage Organizations through the intended local backend/API/UI path at the selected scope:

- app-description and frontend API contracts explicitly model Organization Admin semantics;
- backend repository/service support lists, reads, creates, renames, suspends, and reactivates internal Tenants as customer-facing Organizations;
- protected Admin API endpoints use browser-facing Organization DTOs and backend Tenant authorization;
- frontend User Admin or SaaS Owner surface exposes Organization list/detail/actions only to authorized SaaS Owner Admins;
- every read/write is authorized by selected `AuthContext` and capability grants;
- tenant/customer isolation, support-access boundaries, billing-boundary non-authority, idempotency, no-op behavior, safe denials, and AdminAuditEvent/work-trace evidence are validated;
- frontend payloads do not expose provider secrets, hidden application data, raw tenant internals beyond safe ids, or hidden cross-tenant facts.

## Non-goals

- Do not implement full billing/subscription management, payment provider integration, entitlement enforcement, or payment-failure workflows.
- Do not give SaaS Owner Admins tenant/customer application-data access without explicit backend-authorized selected context/support access.
- Do not replace User Admin with page-first CRUD or frontend-only authorization.
- Do not add app-specific business-domain organization features.
- Do not modify `skills-pack/**` for this app-realization mini-project.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or mark blocked with a precise reason, and make one focused git commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `app-description/app.md`
3. `app-description/domains/core-starter/realization/traceability.md`
4. `app-description/global/roles/foundation-roles.md`
5. `app-description/domains/core-starter/workstreams/user-admin/access.md`
6. `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
7. `specs/saas-owner-organization-admin/README.md`
8. `specs/saas-owner-organization-admin/conversation-capture.md`
9. `specs/saas-owner-organization-admin/pending-tasks.md`
10. selected sprint/backlog/task brief
11. task-specific app-description/source/test/frontend files

## Sprint sequence

1. Sprint 01: Intent and contract alignment.
2. Sprint 02: Backend service and protected API.
3. Sprint 03: Frontend Organization Admin surface.
4. Sprint 04: Verification and follow-up queue repair if gaps remain.

## Open concerns

- Whether the first implementation should extend `Tenant` with lifecycle metadata or keep the initial model minimal and surface metadata through service DTOs.
- Whether Organization Admin should live inside User Admin or become a separate SaaS Owner Admin workstream later. The first implementation should use the smallest stable hook and not introduce a sixth core workstream unless evidence warrants it.
- Existing working tree contains unrelated `skills-pack/**` changes; future task commits must avoid those files unless explicitly tasked.
