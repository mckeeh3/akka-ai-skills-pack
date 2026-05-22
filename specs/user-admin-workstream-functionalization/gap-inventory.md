# User Admin Workstream Functionalization Gap Inventory

## Scope reviewed

Reviewed the current User Admin workstream definition across:

- app-description workstream doctrine and contracts:
  - `docs/agent-workstream-application-architecture.md`
  - `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md`
  - `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md`
  - `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-command-center.md`
- browser/API contract layer:
  - `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md`
- backend reference slice:
  - `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`
- frontend reference assets/tests:
  - `frontend/src/workstream/fixtures/surfaces.ts`
  - `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

This inventory treats this repository as the skills-pack source. The findings are about implementation-readiness of the reference app-description/specification and reference fixtures, not about a deployed end-user application.

## Current coverage summary

| Area | Existing coverage | Current gap |
|---|---|---|
| Functional agent | `user-admin-agent` exists in `functional-agents.md` with purpose, roles, default surface, callable capabilities, and trace/test obligations. | It still names `user-admin-command-center` as the default aggregate surface. It does not yet specify the agent's end-to-end behavior for opening a dashboard, searching the user list, opening a user account detail, explaining allowed/denied actions, or escalating risky changes. |
| Surface index | `surfaces-index.md` lists one User Admin surface: `user-admin-command-center`, type `dashboard+table+forms/v1`. | Dashboard, user list/search, and user account/detail are not three canonical surface contracts. The index cannot yet serve as a fullstack target for separate load/search/detail APIs and rendering tests. |
| Surface contract | `02-user-admin-command-center.md` mentions invitation queue, user directory rows, membership rows, support access, admin audit excerpts, actions, and states. | The contract is broad prose. It does not define separate typed payloads, action matrices, state models, trace ids, or scope-aware variants for SaaS Owner Admin, Tenant Admin, and Customer Admin. |
| Frontend API contracts | `frontend-api-contracts.md` defines generic `/api/admin/users`, invitations, roles, access review, and agent helper routes. | It lacks full typed DTOs for `UserAdminDashboardPayload`, `UserAdminUserListPayload`, and `UserAdminUserAccountPayload`; no canonical dashboard-to-list-to-detail route group exists; correlation ids, trace ids, redaction markers, row/detail actions, loading/error/forbidden semantics, and idempotency keys are not consistently defined for the three surfaces. |
| Backend reference slice | `user-admin-reference-slice.md` is strong on Account/Profile/Settings, Membership, SupportAccessGrant, AdminAuditEvent, views, routes, authorization, audit, and tests. | Its User Admin workstream surface section remains an aggregate checklist. It does not yet map the three canonical surface ids to required Akka views/APIs and first-milestone read/write behavior. |
| Frontend fixtures | `surfaces.ts` already models a dashboard-like surface, list-search surface, and detail-edit surface with action metadata and trace ids. | The fixture ids are implementation-style (`surface-user-admin-dashboard`, `surface-user-admin-list`, `surface-user-admin-detail-admin`) rather than canonical app-description ids (`user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`). They are useful demo data, but the docs do not yet distinguish fixture-only semantics from fullstack contracts. |
| Frontend tests | `workstream-user-admin-vertical.contract.test.mjs` verifies dashboard/list/detail fixture existence, capability ids, trace/audit affordances, and no page-first route dependency. | Tests assert the current aggregate command-center concept and fixture ids. They do not yet verify SaaS Owner Admin, Tenant Admin, Customer Admin variants, forbidden/empty/error state fixtures, or action-to-capability ids for every canonical surface action. |

## Where dashboard, user list, and user account exist today

### Dashboard

Existing coverage:

- `surfaces-index.md` describes `user-admin-command-center` as a dashboard+table+forms surface.
- `02-user-admin-command-center.md` includes dashboard-like content: invitation queue, access review, support access, admin audit excerpts, and states.
- `frontend/src/workstream/fixtures/surfaces.ts` exports `userAdminDashboardSurface` with cards for pending invitations, active users, access review items, and support grants.
- `workstream-user-admin-vertical.contract.test.mjs` checks `userAdminDashboardSurface`, `surface-user-admin-dashboard`, invitation queue, access review, and admin audit strings.

Gap:

- There is no canonical `user-admin-dashboard` app-description surface contract.
- Dashboard payload shape is not typed in `frontend-api-contracts.md`.
- Dashboard load route and backend view composition are not specified as a fullstack contract.
- SaaS Owner Admin, Tenant Admin, and Customer Admin variant rules are not explicit.
- Loading, empty, error, forbidden, and stale dashboard states are mentioned only generally.

### User list/search

Existing coverage:

- `02-user-admin-command-center.md` mentions user directory rows and membership rows.
- `frontend-api-contracts.md` defines `GET /api/admin/users` with filters and `AdminUsersResponse`.
- `user-admin-reference-slice.md` defines `UserDirectoryView`, `MembershipView`, `InvitationView`, and query paths that can back user list/search without caller-supplied known user ids.
- `surfaces.ts` exports `userAdminListSearchSurface` with mixed rows for user directory, invitation queue, membership, support access, and audit excerpts.
- The contract test verifies list fixture navigation and `table-to-card` fallback.

Gap:

- There is no canonical `user-admin-user-list` surface contract.
- `AdminUsersResponse` is too narrow to render the full list/search workstream surface: it lacks scope variant metadata, allowed row actions, redaction markers, trace links, empty/error/forbidden payload states, and dashboard-origin context.
- The fixture list surface is not tied to a documented typed payload or route group.
- Search/list action-to-capability mapping is broad (`secure-tenant-user-foundation`) rather than named capability ids such as read/search/list/detail and invitation/membership sub-capabilities.

### User account/detail

Existing coverage:

- `frontend-api-contracts.md` lists `GET /api/admin/users/{accountId}` and describes a user detail DTO in prose.
- `user-admin-reference-slice.md` defines user detail content: account/profile/settings visibility, scoped memberships, invitation history, support grants, admin audit excerpts, access-review items, and action availability flags.
- `surfaces.ts` exports `userAdminDetailEditSurface` with fields, permission state, audit trace ids, update profile, replace role, and trace actions.
- The contract test verifies detail navigation and no page-first route dependency.

Gap:

- There is no canonical `user-admin-user-account` surface contract.
- Detail payload is prose-only; no complete TypeScript DTO exists for account summary, memberships, invitations, support access, audit excerpts, access-review items, action availability, redactions, trace ids, and decision-card links.
- Scope-aware redaction and denial behavior for SaaS Owner Admin, Tenant Admin, and Customer Admin is not captured per detail section/action.
- Mutating actions on detail (role replace/remove, membership suspend/reactivate/remove, account disable/reactivate, support access grant/revoke/extend, access-review resolve) are not yet mapped one-to-one to named capabilities and API responses.

## Missing implementation-ready contracts

### 1. Surface contracts

Needed files:

- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md`

Each must define:

- stable surface id and type/version;
- owner functional agent (`user-admin-agent`);
- payload summary and schema ownership;
- allowed actions and named capability ids;
- loading, empty, error, forbidden, and stale states;
- auth/security assumptions;
- trace ids/correlation ids/audit links;
- rendering tests;
- scope-aware variants for SaaS Owner Admin, Tenant Admin, and Customer Admin;
- links to reusable decision-card and audit-trace surfaces instead of duplicating those contracts.

### 2. Frontend API and payload schemas

Needed updates in `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md`:

- `UserAdminDashboardPayload`
- `UserAdminUserListPayload`
- `UserAdminUserAccountPayload`
- shared DTOs for selected admin scope, action availability, action denial reasons, redaction markers, trace links, audit links, pagination, filters, state envelopes, correlation ids, and idempotency keys.

Needed routes or route groups:

- dashboard load route, for example `GET /api/admin/users/dashboard` or equivalent canonical workstream surface endpoint;
- list/search route using `/api/admin/users` with canonical user-list payload semantics;
- detail route using `/api/admin/users/{accountId}` with canonical user-account payload semantics;
- mutation routes for invitations, memberships, roles, account status, support access, and access-review actions.

### 3. Capability and authorization matrix

Needed updates in capability and traceability docs:

- Every surface action must map to a named backend capability, not only to broad `secure-tenant-user-foundation`.
- Required capability families include read/search/detail, invite/resend/revoke, membership add/suspend/reactivate/remove, role replace/remove, account disable/reactivate, support-access grant/revoke/extend, access-review read/resolve, and admin-audit read.
- Actor variants must cover SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, and support-access actors.
- Denial behavior must be explicit for cross-tenant, Customer Admin attempting Tenant action, SaaS Owner without support access, disabled actor, missing role/capability, role escalation, and last-admin loss.

### 4. Backend state/views and Akka realization mapping

Needed updates in realization maps:

- Map the three canonical surfaces to Account/Profile/Settings, Membership/Role, Invitation integration, AdminAuditEvent, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView.
- State which APIs can be read-only first and which mutations are required for first functional completion.
- Ensure dashboard/list/detail flow is backed by views and scoped queries, not caller-known fixture ids.

### 5. UserAdminAgent behavior

Needed updates in functional-agent and behavior docs:

- Supported intents: open dashboard, search/list users, open user account, explain allowed actions, explain denied actions, draft invitation rationale, summarize audit evidence, recommend least-privilege roles, and route risky actions to decision cards.
- Governed runtime documents: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace.
- Tool boundary: read/list/detail/summarize can be allowed within AuthContext; consequential mutations default to human-confirmed capability calls or decision-card flows.
- Tests for prompt assembly, skill load, tool allow/deny, and surface/action outcomes.

### 6. UI states and frontend reference fixtures/tests

Needed frontend alignment:

- Fixture ids and labels should align with canonical docs while remaining explicit as reference/demo fixtures.
- Dashboard → user list → user account flow should include action-to-capability ids, trace ids, forbidden/empty/error state fixtures, and scope-aware variants.
- Contract tests should check SaaS Owner Admin, Tenant Admin, Customer Admin variant strings and prevent regression to page-first route dependency.

### 7. Readiness gates

Needed readiness/test updates:

- User Admin must not be considered functional when dashboard, user list, or user account are fixture-only, UI-only, or API-only.
- Acceptance checks must select User Admin, load dashboard, open list, search/filter, open account detail, invoke at least one safe mutation or decision-card-producing action, and verify audit/trace output.
- Negative checks must cover disabled actor, cross-tenant access, Customer Admin Tenant-level denial, SaaS Owner no-support-access denial, role escalation, and last-admin loss.

## Recommended canonical target file set for later tasks

Primary app-description files:

- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/20-behavior/flows/01-onboarding-and-access-flow.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/structured-surface-rendering.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/ui-index.md`

Capability and traceability files:

- `docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/surface-to-capability-map.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/functional-agent-to-capability-map.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/capability-to-horizontal-map.md`

Realization/readiness files:

- `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`
- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- `specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/60-generation/horizontal-implementation-map.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/acceptance/01-seed-app-acceptance.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`

Frontend reference files:

- `frontend/src/workstream/fixtures/surfaces.ts`
- `frontend/src/workstream/fixtures/agents.ts`
- `frontend/src/workstream/fixtures/workstream.ts`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Task queue assessment

The existing follow-up tasks still make sense:

- TASK-UA-002 should split the aggregate `user-admin-command-center` into `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` contracts.
- TASK-UA-003 should define typed fullstack payload/API contracts.
- TASK-UA-004 should map all surface actions to named governed capabilities and denial semantics.
- TASK-UA-005 should make `user-admin-agent` behavior executable as a functional vertical.
- TASK-UA-006 should align Akka realization maps with Account/Profile/Settings, Membership/Role, InvitationView, AdminAuditEvent/AdminAuditView, UserDirectoryView, MembershipView, and AccessReviewQueueView.
- TASK-UA-007 should align reference fixtures/tests with canonical ids and scope variants.
- TASK-UA-008 should add fullstack readiness gates so fixture-only, UI-only, or API-only User Admin coverage cannot count as functional.
- TASK-UA-009 and TASK-UA-010 should review readiness and hand off implementation work.

No new follow-up task is required from this inventory beyond the tasks already queued.
