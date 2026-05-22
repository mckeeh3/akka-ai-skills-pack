# Pending Tasks: User Admin Workstream Functionalization

## Queue rules

- Execute one task per fresh harness session.
- Select the first `pending` task whose dependencies are satisfied.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Treat this repository as the skills-pack source: changes are primarily to skills, docs, examples, specs, templates, and reference tests unless a task explicitly targets executable starter/reference code.
- Update this file before finishing the harness response: set completed tasks to `done`, add a completion note, and add any discovered follow-up task rather than expanding the current task.
- Every completed task must end with a git commit.
- Review tasks must add new self-sufficient tasks for discovered gaps rather than silently accepting partial readiness.
- Commit message format: `user-admin-workstream: <short task title>`.

## Goal

Make the User Admin workstream specification implementation-ready and eventually fullstack functional. "Functional" means the User Admin functional agent works through the authenticated workstream shell and at least these three scope-aware surfaces are backed by real backend APIs/components, not static fixtures:

1. User Admin dashboard.
2. User list/search.
3. User account/detail.

The workstream must be one shared User Admin functional-agent family with scope-aware variants for SaaS Owner Admin, Tenant Admin, and Customer Admin. Reuse common frontend/backend components, but define explicit authority, visible actions, redaction, and denial behavior for each selected `AuthContext`.

## Tasks

### TASK-UA-001: Inventory current User Admin spec and fixture gaps

- status: done
- completion note: Created `specs/user-admin-workstream-functionalization/gap-inventory.md`; confirmed the current aggregate command-center prose, frontend API gaps, backend reference slice strengths, and fixture/test-only dashboard/list/detail coverage; no additional follow-up tasks were needed beyond the existing queue.
- source: user request to make User Admin fully functional with dashboard, user list, and user account surfaces
- task brief: none
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-command-center.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - frontend/src/workstream/fixtures/surfaces.ts
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
- expected outputs:
  - Create `specs/user-admin-workstream-functionalization/gap-inventory.md`.
  - Compare current app-description surfaces, API contracts, backend reference slice, frontend fixtures, and tests.
  - Identify exactly where dashboard/list/detail concepts exist only as fixtures or broad prose.
  - List missing contracts needed for fullstack readiness: payload schemas, APIs, capabilities, backend state/views, agent behavior, UI states, tests, and readiness gates.
  - Recommend the canonical target file set to edit in later tasks.
- required checks:
  - `test -f specs/user-admin-workstream-functionalization/gap-inventory.md`
  - `rg -n "dashboard|user list|user account|detail|SaaS Owner|Tenant Admin|Customer Admin|fullstack" specs/user-admin-workstream-functionalization/gap-inventory.md`
  - `git diff --check`
- done criteria:
  - Gap inventory clearly distinguishes existing coverage from missing implementation-ready contracts.
  - Follow-up tasks below still make sense or are adjusted based on the inventory.
  - A git commit exists for the changes.

### TASK-UA-002: Split User Admin command center into three canonical surface contracts

- status: pending
- source: TASK-UA-001
- task brief: none
- depends on: [TASK-UA-001]
- required reads:
  - specs/user-admin-workstream-functionalization/gap-inventory.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-command-center.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
- expected outputs:
  - Replace or decompose the broad `user-admin-command-center` contract into three required contracts under `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/`:
    - `02-user-admin-dashboard.md`
    - `03-user-admin-user-list.md`
    - `04-user-admin-user-account.md`
  - Update `surfaces-index.md` to list all three surfaces explicitly.
  - Preserve links to reusable decision-card and audit-trace surfaces rather than duplicating them.
  - Each surface contract must define type/version, owner functional agent, payload summary, allowed actions, states, auth/security, rendering tests, trace ids, and scope-aware variant notes for SaaS Owner Admin, Tenant Admin, and Customer Admin.
- required checks:
  - `test -f docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md`
  - `test -f docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md`
  - `test -f docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md`
  - `rg -n "user-admin-dashboard|user-admin-user-list|user-admin-user-account" docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md`
  - `rg -n "SaaS Owner Admin|Tenant Admin|Customer Admin|loading|empty|error|forbidden|stale" docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/0*-user-admin-*.md`
  - `git diff --check`
- done criteria:
  - User Admin has three explicit canonical surface contracts.
  - The old aggregate command-center meaning is either removed or marked as an umbrella/composition, not the only contract.
  - A git commit exists for the changes.

### TASK-UA-003: Define typed fullstack payload and frontend API contracts for the three surfaces

- status: pending
- source: TASK-UA-002
- task brief: none
- depends on: [TASK-UA-002]
- required reads:
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
- expected outputs:
  - Update `55-ui/frontend-api-contracts.md` with complete TypeScript DTOs for:
    - `UserAdminDashboardPayload`
    - `UserAdminUserListPayload`
    - `UserAdminUserAccountPayload`
    - shared scope/action/redaction/trace DTOs
  - Define API routes or route groups that load each surface and perform the dashboard-to-list-to-detail flow.
  - Include pagination, filters, row actions, detail action availability, correlation ids, trace ids, redaction markers, loading/error/forbidden semantics, and idempotency keys for mutations.
  - Ensure contracts are scope-aware without creating three separate unrelated frontend APIs.
- required checks:
  - `rg -n "UserAdminDashboardPayload|UserAdminUserListPayload|UserAdminUserAccountPayload" docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md`
  - `rg -n "/api/admin/users|/api/admin/users/\{.*\}|dashboard|pageToken|correlationId|trace" docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md`
  - `git diff --check`
- done criteria:
  - A frontend implementer can build the three surfaces from typed contracts without relying on fixtures.
  - A backend implementer can see what browser-safe DTOs each endpoint must return.
  - A git commit exists for the changes.

### TASK-UA-004: Add surface action to capability and authorization matrix

- status: pending
- source: TASK-UA-002
- task brief: none
- depends on: [TASK-UA-002]
- required reads:
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/surface-to-capability-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/functional-agent-to-capability-map.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
- expected outputs:
  - Update capability and traceability docs so every action on the three User Admin surfaces maps to a named backend capability.
  - Include at least read/search/detail, invite/resend/revoke, membership add/suspend/reactivate/remove, role replace/remove, account disable/reactivate, support-access grant/revoke/extend, access-review read/resolve, and admin-audit read.
  - Define how SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, and support-access actors differ for each capability.
  - Explicitly state denial behavior for cross-tenant, Customer Admin attempting Tenant action, SaaS Owner without support access, disabled actor, missing role/capability, role escalation, and last-admin loss.
- required checks:
  - `rg -n "admin\.users|admin\.memberships|admin\.roles|admin\.invitations|admin\.audit|admin\.access_review|support" docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/surface-to-capability-map.md`
  - `rg -n "SaaS Owner Admin|Tenant Admin|Customer Admin|last-admin|role escalation|support access" docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md`
  - `git diff --check`
- done criteria:
  - Surface actions are not UI-only affordances; every action has a governed backend capability and denial semantics.
  - A git commit exists for the changes.

### TASK-UA-005: Specify UserAdminAgent behavior, tools, prompts, and traces for the functional vertical

- status: pending
- source: TASK-UA-002
- task brief: none
- depends on: [TASK-UA-002, TASK-UA-004]
- required reads:
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/internal-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/20-behavior/flows/01-onboarding-and-access-flow.md
  - skills/ai-first-saas-admin-agents/SKILL.md
  - skills/akka-agent-seed-documents/SKILL.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
- expected outputs:
  - Update User Admin functional-agent docs and behavior flow so `user-admin-agent` can operate the three surfaces end to end.
  - Define supported agent intents: open dashboard, search/list users, open user account, explain allowed/denied actions, draft invitation rationale, summarize audit evidence, recommend least-privilege roles, and route risky actions to decision cards.
  - Define required governed runtime documents: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace.
  - Define default tool boundaries: read/list/detail/summarize allowed within AuthContext; consequential mutations default to human-confirmed capability calls or decision-card flows.
  - Define trace/test obligations for prompt assembly, skill load, tool allow/deny, and surface/action outcomes.
- required checks:
  - `rg -n "open dashboard|search.*users|open.*user account|allowed|denied|InvitationDraft|RoleRecommendation|AdminAuditSummary|AgentDefinition|ToolPermissionBoundary|AgentWorkTrace" docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md docs/examples/ai-first-saas-seed-app-description/app-description/20-behavior/flows/01-onboarding-and-access-flow.md`
  - `git diff --check`
- done criteria:
  - The User Admin agent is specified as functional within the three-surface vertical, not just named as a left-rail item.
  - A git commit exists for the changes.

### TASK-UA-006: Align backend Akka realization contract with the three-surface User Admin vertical

- status: pending
- source: TASK-UA-003
- task brief: none
- depends on: [TASK-UA-003, TASK-UA-004]
- required reads:
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/60-generation/horizontal-implementation-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/capability-to-horizontal-map.md
- expected outputs:
  - Update realization/horizontal maps so the three User Admin surfaces identify their required Akka substrates.
  - Minimum functional backing must include Account/Profile/Settings, Membership/Role, Invitation integration, AdminAuditEvent, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and selected AccessReviewQueueView behavior.
  - Define which APIs can be read-only first and which mutations are required for the first functional completion milestone.
  - Ensure the dashboard/list/detail flow is backed by view queries rather than requiring caller-supplied known user ids.
- required checks:
  - `rg -n "user-admin-dashboard|user-admin-user-list|user-admin-user-account|UserDirectoryView|MembershipView|InvitationView|AdminAuditView|AccessReviewQueueView|Account|Membership" specs/core-app-full-stack-readiness/full-core-realization-map.md docs/examples/ai-first-saas-seed-app-description/app-description/60-generation/horizontal-implementation-map.md docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/capability-to-horizontal-map.md`
  - `git diff --check`
- done criteria:
  - Backend implementation tasks can be derived from the three surfaces without re-deciding Akka components.
  - A git commit exists for the changes.

### TASK-UA-007: Update frontend reference fixtures/tests to match the canonical surface ids and contracts

- status: pending
- source: TASK-UA-003
- task brief: none
- depends on: [TASK-UA-003, TASK-UA-004]
- required reads:
  - frontend/src/workstream/fixtures/surfaces.ts
  - frontend/src/workstream/fixtures/agents.ts
  - frontend/src/workstream/fixtures/workstream.ts
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/structured-surface-rendering.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/ui-index.md
- expected outputs:
  - Align frontend reference fixtures with canonical User Admin surface ids from the docs.
  - Keep fixtures as reference/demo data, but ensure they model dashboard → user list → user account with capability-backed action metadata and trace ids.
  - Update contract tests to check scope-aware variants, action-to-capability ids, forbidden/empty/error state fixtures, and no page-first route dependency.
  - Update UI docs to cite the three-surface User Admin vertical as the canonical reference.
- required checks:
  - `rg -n "user-admin-dashboard|user-admin-user-list|user-admin-user-account|SaaS Owner|Tenant Admin|Customer Admin|forbidden|empty|error" frontend/src/workstream/fixtures frontend/src/workstream-user-admin-vertical.contract.test.mjs docs/examples/ai-first-saas-seed-app-description/app-description/55-ui`
  - `npm --prefix frontend test -- --runInBand` if supported by the frontend package; otherwise run the existing frontend test command documented in `frontend/README.md` and record the actual command in the completion note.
  - `git diff --check`
- done criteria:
  - Frontend reference assets and tests reinforce the new canonical User Admin vertical instead of preserving divergent ids or fixture-only semantics.
  - A git commit exists for the changes.

### TASK-UA-008: Add fullstack acceptance/readiness gates for User Admin functional completion

- status: pending
- source: TASK-UA-006
- task brief: none
- depends on: [TASK-UA-005, TASK-UA-006, TASK-UA-007]
- required reads:
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-generate-app/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/acceptance/01-seed-app-acceptance.md
- expected outputs:
  - Update readiness/generation/test guidance so User Admin is not considered functional unless the three-surface vertical is fullstack working.
  - Define acceptance checks for selecting User Admin, loading dashboard, opening user list, searching/filtering, opening user account, invoking at least one safe mutation or decision-card-producing action, and observing audit/trace output.
  - Define negative checks for disabled actor, cross-tenant access, Customer Admin Tenant-level denial, SaaS Owner no-support-access denial, role escalation, and last-admin loss.
  - Ensure full-core readiness blocks User Admin if it is fixture-only, API-only, or UI-only.
- required checks:
  - `rg -n "user-admin-dashboard|user-admin-user-list|user-admin-user-account|fixture-only|UI-only|API-only|fullstack|last-admin|cross-tenant" skills/app-description-readiness-assessment/SKILL.md skills/app-generate-app/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md docs/examples/ai-first-saas-seed-app-description/app-description/30-tests`
  - `git diff --check`
- done criteria:
  - Generation/readiness cannot claim User Admin functional completion without fullstack dashboard/list/detail behavior and tests.
  - A git commit exists for the changes.

### TASK-UA-009: Review User Admin functionalization readiness and add gap-closing tasks

- status: pending
- source: user request for readiness review and follow-up task creation
- task brief: none
- depends on: [TASK-UA-008]
- required reads:
  - specs/user-admin-workstream-functionalization/pending-tasks.md
  - specs/user-admin-workstream-functionalization/gap-inventory.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-dashboard.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/03-user-admin-user-list.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/04-user-admin-user-account.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/frontend-api-contracts.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/surface-to-capability-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/capability-to-horizontal-map.md
  - specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
- expected outputs:
  - Create `specs/user-admin-workstream-functionalization/readiness-review.md`.
  - Review whether the docs/specs/tests now define a functional fullstack User Admin vertical with dashboard, user list, and user account surfaces.
  - Assess these dimensions: surface contracts, typed API payloads, capability/action mapping, SaaS Owner/Tenant/Customer Admin variants, backend Akka realization map, UserAdminAgent behavior, frontend reference fixtures/tests, readiness gates, and downstream implementation handoff.
  - Classify each dimension as `ready`, `partial`, or `gap` with evidence links.
  - Add new self-sufficient pending tasks to this file for every material `partial` or `gap` finding. New tasks must have ids after the current last task, dependencies, required reads, expected outputs, required checks, done criteria, and commit expectations.
  - If no material gaps remain, state that explicitly and leave TASK-UA-010 as the final handoff task.
- required checks:
  - `test -f specs/user-admin-workstream-functionalization/readiness-review.md`
  - `rg -n "ready|partial|gap|user-admin-dashboard|user-admin-user-list|user-admin-user-account|SaaS Owner|Tenant Admin|Customer Admin|fullstack" specs/user-admin-workstream-functionalization/readiness-review.md`
  - `rg -n "TASK-UA-0(1[1-9]|[2-9][0-9])|gap|partial" specs/user-admin-workstream-functionalization/pending-tasks.md specs/user-admin-workstream-functionalization/readiness-review.md`
  - `git diff --check`
- done criteria:
  - Readiness review exists and gives an evidence-backed functionalization assessment.
  - Every material gap has a new self-sufficient follow-up task, or the review explicitly states that no material gaps remain.
  - A git commit exists for the changes.

### TASK-UA-010: Create implementation handoff tasks for generated/starter code realization

- status: pending
- source: TASK-UA-009
- task brief: none
- depends on: [TASK-UA-009]
- required reads:
  - specs/user-admin-workstream-functionalization/gap-inventory.md
  - specs/core-app-full-stack-readiness/user-admin-reference-slice.md
  - specs/core-app-full-stack-readiness/full-core-realization-map.md
  - specs/core-app-full-stack-readiness/pending-tasks.md
  - specs/ai-first-saas-starter-app-template/pending-tasks.md
- expected outputs:
  - Add or update downstream implementation tasks in the appropriate existing pending-task queue for generated/starter code realization.
  - Tasks must be self-sufficient and ordered so future sessions can implement backend components/views/APIs, frontend surface integration, agent runtime behavior, and fullstack tests incrementally.
  - Do not implement starter code in this task unless the downstream queue explicitly says to; this task is a handoff/planning task.
  - Ensure each downstream task carries the canonical surface ids, API contracts, capabilities, Akka substrates, auth/audit requirements, and required tests.
- required checks:
  - `rg -n "user-admin-dashboard|user-admin-user-list|user-admin-user-account|UserDirectoryView|fullstack" specs/core-app-full-stack-readiness/pending-tasks.md specs/ai-first-saas-starter-app-template/pending-tasks.md specs/user-admin-workstream-functionalization/pending-tasks.md`
  - `git diff --check`
- done criteria:
  - Future implementation sessions have concrete, ordered, self-sufficient code-realization tasks.
  - A git commit exists for the changes.
