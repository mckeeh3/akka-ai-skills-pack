# Readiness Review: User Admin Workstream Functionalization

## Review scope

This review checks whether the source repository now defines an implementation-ready fullstack User Admin vertical for generated secure AI-first SaaS apps. It reviews the canonical three-surface flow:

```text
user-admin-dashboard -> user-admin-user-list -> user-admin-user-account
```

The assessment is about skills-pack reference specs, docs, and fixtures. It does not claim that starter/generated runtime code has already implemented the backend and frontend APIs.

## Summary

Overall status: **partial**.

The app-description, capability, realization, agent-behavior, and readiness-gate layers are now strong enough to guide downstream implementation. The remaining material gap is in the frontend reference fixture/contract-test layer: it models the canonical surface ids and core flow, but it does not yet cover the complete User Admin action/capability matrix that the surface contracts and API contracts require. That gap should be closed before the final downstream implementation handoff so implementation sessions do not inherit a narrower fixture/test target than the docs define.

## Dimension assessment

| Dimension | Status | Evidence | Assessment |
|---|---|---|---|
| Surface contracts | ready | `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md`; `surface-contracts/02-user-admin-dashboard.md`; `surface-contracts/03-user-admin-user-list.md`; `surface-contracts/04-user-admin-user-account.md` | The former aggregate command-center is decomposed into canonical dashboard, list/search, and account/detail contracts. Each contract defines type/version, owner functional agent, payload summary, allowed actions, loading/empty/error/forbidden/stale states, SaaS Owner Admin/Tenant Admin/Customer Admin variants, auth/security, trace expectations, and rendering tests. |
| Typed API payloads | ready | `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md` | The route group defines shared scope/action/redaction/trace envelopes and complete `UserAdminDashboardPayload`, `UserAdminUserListPayload`, and `UserAdminUserAccountPayload` DTOs for `/api/admin/users/dashboard`, `/api/admin/users`, and `/api/admin/users/{accountId}`. Mutation routes require idempotency keys, correlation ids, audit trace ids, and safe denial/conflict/no-op responses. |
| Capability/action mapping | ready | `docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md`; `70-traceability/surface-to-capability-map.md`; `70-traceability/capability-to-horizontal-map.md` | Every surface action family maps to named `admin.*` capabilities, including dashboard/search/detail, invitations, memberships, roles, account lifecycle, support access, access review, and audit. Denial categories cover cross-scope access, disabled actor, missing capability, Customer Admin Tenant actions, SaaS Owner no-support-access, role escalation, and last-admin loss. |
| SaaS Owner/Tenant/Customer Admin variants | ready | Surface contracts; capability matrix; `frontend-api-contracts.md`; `full-core-acceptance-test-matrix.md` | Variant semantics are explicit across surface payloads, actor rules, denied actions, redaction behavior, and required acceptance/security tests. The shared API group remains scope-aware instead of creating unrelated APIs per role. |
| Backend Akka realization map | ready | `specs/core-app-full-stack-readiness/full-core-realization-map.md`; `docs/examples/ai-first-saas-seed-app-description/app-description/60-generation/horizontal-implementation-map.md`; `70-traceability/capability-to-horizontal-map.md`; `specs/core-app-full-stack-readiness/user-admin-reference-slice.md` | The realization contract names Account/Profile/Settings, Membership/Role, Invitation, SupportAccessGrant, AdminAuditEvent, `UserDirectoryView`, `MembershipView`, `InvitationView`, `AdminAuditView`, `AccessReviewQueueView`, admin HTTP APIs, and User Admin React surfaces. It states the first functional milestone may be read-only except for at least one safe mutation or decision-card-producing action with audit output, and it requires view-backed discovery rather than caller-supplied ids. |
| UserAdminAgent behavior | ready | `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md` | `user-admin-agent` is specified as a governed skilled functional agent for the three-surface vertical. It supports opening dashboard/list/detail, explaining allowed/denied actions, drafting invitation rationale, summarizing audit evidence, recommending least-privilege roles, and routing risky actions to decision cards. Required governed runtime documents and prompt/skill/tool/work trace tests are named. |
| Frontend reference fixtures/tests | partial | `frontend/src/workstream/fixtures/surfaces.ts`; `frontend/src/workstream-user-admin-vertical.contract.test.mjs` | The fixtures/tests now use canonical surface ids, model dashboard-to-list-to-detail navigation, include SaaS Owner Admin/Tenant Admin/Customer Admin variant strings, include loading/empty/error/forbidden/stale state fixtures, and check several capability ids. However, the fixture action set and contract test do not yet cover the complete action/capability matrix in the surface/API contracts: membership add/suspend/reactivate/remove, role remove, account disable/reactivate, identity relink, support-access read/grant/revoke/extend, and full access-review/audit variants are not all represented as concrete fixture actions/assertions. |
| Readiness gates | ready | `skills/app-description-readiness-assessment/SKILL.md`; `skills/app-generate-app/SKILL.md`; `skills/akka-prd-to-specs-backlog/SKILL.md`; `specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md`; `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/**` | Readiness and generation guidance now blocks fixture-only, API-only, or UI-only claims. Acceptance requires selecting User Admin, loading dashboard, opening list, searching/filtering, opening account detail, invoking a safe mutation or decision-card-producing action, and observing audit/trace output. Negative checks include disabled actor, cross-tenant access, Customer Admin Tenant-level denial, SaaS Owner no-support-access denial, role escalation, and last-admin loss. |
| Downstream implementation handoff | partial | `specs/user-admin-workstream-functionalization/pending-tasks.md`; `specs/core-app-full-stack-readiness/pending-tasks.md`; `specs/ai-first-saas-starter-app-template/pending-tasks.md` | The docs/specs contain enough target detail, but the dedicated downstream code-realization handoff task is still pending. That handoff should run after the frontend fixture/action matrix is aligned so implementation queues inherit the same full canonical surface ids, API payloads, capabilities, Akka substrates, auth/audit requirements, and tests. |

## Material gap findings

### GAP-UA-RR-001: Frontend fixture/action contract is narrower than the canonical User Admin capability matrix

- Status: **material partial**.
- Evidence:
  - Canonical surface contracts require broad action families in `02-user-admin-dashboard.md`, `03-user-admin-user-list.md`, and `04-user-admin-user-account.md`.
  - The API contract lists mutation routes for invitations, memberships, roles, account status/profile/identity relink, support access, access review, and audit in `55-ui/frontend-api-contracts.md`.
  - `frontend/src/workstream/fixtures/surfaces.ts` currently includes canonical ids and representative actions such as dashboard read, search, detail read, invitation create/resend/revoke, profile patch, role replace, access-review resolve, and audit read, but not concrete fixture actions for the full matrix.
  - `frontend/src/workstream-user-admin-vertical.contract.test.mjs` asserts representative capability ids but does not assert all required action families and denial categories.
- Risk:
  - Future implementation sessions may satisfy the frontend reference test while omitting visible/actionable support for required membership, account lifecycle, support-access, identity-relink, role-remove, and expanded denial flows.
- Required follow-up:
  - Add a self-sufficient follow-up task to expand fixture actions and contract tests before the final implementation handoff.

## No other material spec gaps found

No additional material gaps were found in the app-description surface contracts, typed API payloads, capability/action mapping, scope variants, backend Akka realization map, UserAdminAgent behavior, or readiness gates. Those layers are implementation-ready at the specification level.

## Final readiness conclusion

The User Admin functionalization package is **implementation-ready except for the frontend fixture/action matrix alignment gap**. After the new follow-up task closes that gap, TASK-UA-010 can create the downstream generated/starter code-realization tasks as the final handoff.
