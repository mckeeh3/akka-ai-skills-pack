# Pending Tasks: User Admin Surface Conformance Cleanup

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, conversation capture, selected backlog, selected task entry, and task brief before editing.
- Mark exactly one selected task `in-progress` before implementation edits.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit including that task's intended changes and queue-status update.
- Commit message format: `user-admin-surface-conformance: <short task title>`.

## Tasks

### TASK-UASCC-00-001: Create User Admin surface conformance cleanup planning scaffold

- status: done
- source: user requested cleanup of all previously identified User Admin surface/workstream conformance findings
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/00-planning/00-create-conformance-cleanup-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/conversation-capture.md
  - specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
  - specs/user-admin-surface-conformance-cleanup/tasks/00-planning/00-create-conformance-cleanup-queue.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/conversation-capture.md
  - specs/user-admin-surface-conformance-cleanup/sprints/01-conformance-cleanup-sprint.md
  - specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
  - specs/user-admin-surface-conformance-cleanup/tasks/**/*.md
  - specs/user-admin-surface-conformance-cleanup/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, current intent, done state, backlog, task briefs, and pending queue
  - first non-done task is runnable without guessing
- notes:
  - vertical contract: docs/planning only for User Admin structured-surface conformance cleanup; no runtime mutation

### TASK-UASCC-01-001: Align User Admin surface conformance specs

- status: done
- source: specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/01-spec-alignment/01-align-user-admin-surface-conformance.md
- depends on:
  - TASK-UASCC-00-001
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/conversation-capture.md
  - specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
  - specs/user-admin-surface-conformance-cleanup/tasks/01-spec-alignment/01-align-user-admin-surface-conformance.md
  - app-description/domains/core-starter/workstreams/user-admin/workstream.md
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - app-description/domains/core-starter/realization/traceability.md
  - specs/user-admin-surface-navigation-tree/navigation-tree-verification.md
- skills:
  - app-description-surface-modeling
  - app-description-change-impact
  - app-description-ui
- expected outputs:
  - updated app-description and traceability docs if needed
  - optional specs/user-admin-surface-conformance-cleanup/conformance-specification.md
- required checks:
  - `git diff --check`
  - focused `rg` proof for canonical surface types, backend-authored routing, system-message requirements, and no-inline-mutation requirements
- done criteria:
  - app-description/spec decisions are explicit enough for backend/frontend implementation without guessing
- notes:
  - vertical contract: User Admin / `user-admin-agent` concept and `agent-user-admin` runtime alias; spec-only cleanup of dashboard/list/detail/task/decision/workflow/system-message surfaces; capabilities `user_admin.*`, `saas_owner.organization.*`, and `admin.audit.read`; selected AuthContext/role-scope semantics; audit/trace/redaction expectations; validation by focused search and diff check
  - completed checks: `git diff --check`; `rg "show-inspection|surface-user-admin-system-message|backend-authored|canonical" app-description/domains/core-starter/workstreams/user-admin specs/user-admin-surface-conformance-cleanup`
  - commit: `user-admin-surface-conformance: align surface conformance specs`

### TASK-UASCC-02-001: Repair backend canonical surface envelopes and authored payloads

- status: done
- source: specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/02-backend/01-backend-canonical-payloads.md
- depends on:
  - TASK-UASCC-01-001
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/conversation-capture.md
  - specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
  - specs/user-admin-surface-conformance-cleanup/tasks/02-backend/01-backend-canonical-payloads.md
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - src/main/java/ai/first/application/coreapp/useradmin/**
  - src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java
- skills:
  - akka-basic-user-admin
  - akka-http-endpoint-component-client
  - akka-http-endpoint-testing
- expected outputs:
  - backend/workstream surface envelope and payload changes
  - focused backend tests
- required checks:
  - `git diff --check`
  - `mvn -q -Dtest=WorkstreamServiceTest test`
- done criteria:
  - backend provides canonical surface semantics, authored dashboard attention/populations/actions, authored row routing metadata, backend-shaped form options, and default/diagnostic metadata separation
- notes:
  - vertical contract: User Admin / `agent-user-admin`; dashboard attention queues and User Directory routing; browser-tool/workstream action exposure; capabilities `user_admin.list_members`, invitation, role, support, access-review, identity, audit, and organization capabilities; selected AuthContext allow/deny; backend workstream/API substrate; audit/work trace and redaction validation
  - implemented backend-authored `attentionCounts`, `administeredPopulations`, canonical dashboard-variant metadata, branch/authorized action metadata, list row activation metadata, backend-shaped invitation/support/status options, and default/diagnostic metadata separation
  - completed checks: `git diff --check`; `mvn -q -Dtest=WorkstreamServiceTest test`
  - commit: `user-admin-surface-conformance: repair backend canonical payloads`

### TASK-UASCC-02-002: Repair backend task routing and typed system-message outcomes

- status: done
- source: specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/02-backend/02-backend-task-router-system-messages.md
- depends on:
  - TASK-UASCC-02-001
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/tasks/02-backend/02-backend-task-router-system-messages.md
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java
  - src/main/java/ai/first/application/coreapp/useradmin/**
  - src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java
- skills:
  - akka-basic-user-admin
  - akka-http-endpoint-component-client
  - akka-http-endpoint-testing
- expected outputs:
  - backend action/result surface changes
  - focused backend tests
- required checks:
  - `git diff --check`
  - `mvn -q -Dtest=WorkstreamServiceTest test`
- done criteria:
  - detail surfaces are inspection/task routers only; role/status/support/invitation/access-review/identity operations route through dedicated task/result/system-message surfaces; representative denials/no-ops/blocked states return typed safe system messages
- notes:
  - vertical contract: User Admin / `agent-user-admin`; task/action outcomes for role/status/support/invitation/access-review/identity; dedicated surface graph edges; browser-tool action path; exact `user_admin.*` capabilities; tenant/customer hidden-target and last-admin/self-action denials; workstream service/API substrate; audit/work trace and secret redaction
  - implemented inspection-only user/invitation detail surfaces, direct task-entry actions only on details, canonical `system-message` action failure surface, role preview decision-card typing, and self-action denial system-message tests
  - completed checks: `git diff --check`; `mvn -q -Dtest=WorkstreamServiceTest test`
  - commit: `user-admin-surface-conformance: repair task router system messages`

### TASK-UASCC-03-001: Repair frontend structured rendering and retire legacy admin page

- status: done
- source: specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/03-frontend/01-frontend-conformance-and-legacy-retirement.md
- depends on:
  - TASK-UASCC-02-002
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/tasks/03-frontend/01-frontend-conformance-and-legacy-retirement.md
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - frontend/src/workstream/surfaces/**
  - frontend/src/workstream/types/surfaces.ts
  - frontend/src/screens/admin/AdminUsersPage.tsx
  - frontend/src/main.tsx
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
  - frontend/src/workstream-surfaces.contract.test.mjs
- skills:
  - akka-web-ui-apps
  - akka-web-ui-api-client
  - akka-web-ui-state-rendering
  - akka-web-ui-forms-validation
  - akka-web-ui-accessibility-responsive
  - akka-web-ui-testing
- expected outputs:
  - frontend User Admin rendering cleanup
  - legacy page/route/test retirement or absorption
  - frontend contract tests
- required checks:
  - `git diff --check`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - canonical User Admin surface types render without generic fallback; no inline mutation controls remain in detail; dashboard/list use backend-authored metadata; task forms use backend options; raw diagnostics hidden by default; legacy Admin Users page is not a normal runtime path
- notes:
  - vertical contract: User Admin / `agent-user-admin`; frontend workstream shell surfaces and browser-tool submissions; backend metadata is authority, frontend visibility advisory only; no hidden counts/targets inferred; frontend React/Vite substrate; trace/audit drilldowns are browser-safe and role-gated; validation by frontend tests/typecheck/diff check
  - implemented canonical surface routing for `show-inspection`, form/confirmation aliases, `system-message`, decision-card/diff aliases; backend-authored User Admin dashboard attention/population rendering; inspection-only detail task entry points; backend-shaped role/expiry option handling; and quarantined legacy Admin Users page
  - completed checks: `git diff --check`; focused `rg "show-inspection|create-form|lifecycle-confirmation|destructive-lifecycle-confirmation|system-message|attentionCounts|administeredPopulations|backendAuthoredUserAdminQueues|Read-only inspection|Dedicated task surfaces|quarantined-legacy-screen|userAdminRoleOptions|userAdminExpiryOptions" frontend/src/workstream frontend/src/screens/admin frontend/src/workstream-user-admin-vertical.contract.test.mjs`; `npm --prefix frontend test -- --run`; `npm --prefix frontend run typecheck`
  - commit: `user-admin-surface-conformance: repair frontend structured rendering`

### TASK-UASCC-04-001: Add full-stack User Admin surface conformance tests

- status: done
- source: specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/04-tests/01-fullstack-user-admin-conformance-tests.md
- depends on:
  - TASK-UASCC-03-001
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/conversation-capture.md
  - specs/user-admin-surface-conformance-cleanup/tasks/04-tests/01-fullstack-user-admin-conformance-tests.md
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java
  - frontend/src/workstream-user-admin-vertical.contract.test.mjs
  - frontend/src/workstream-surfaces.contract.test.mjs
  - relevant backend/frontend files changed by prior tasks
- skills:
  - akka-http-endpoint-testing
  - akka-web-ui-testing
  - akka-basic-user-admin
- expected outputs:
  - backend/frontend conformance test coverage
- required checks:
  - `git diff --check`
  - `mvn -q -Dtest=WorkstreamServiceTest test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - tests prove canonical rendering, backend-authored routing, inspection-only detail, dedicated task/result/system-message surfaces, backend-shaped options, legacy page retirement, authorization/tenant isolation, audit/trace redaction, and frontend secret boundary
- notes:
  - vertical contract: User Admin / `agent-user-admin`; validation of dashboard -> list -> detail/task -> result/system-message; browser-tool/workstream action mappings; capabilities `user_admin.*`, `saas_owner.organization.*`, `admin.audit.read`; App/Tenant/Customer/Auditor allow/deny; backend workstream service and frontend renderer substrates; local Maven/npm validation
  - added backend conformance coverage for dashboard/list backend-authored routing, inspection-only detail, backend-shaped options, hidden invitation typed system-message denial, access-review provider-blocked workflow status, and browser-safe redaction
  - added frontend contract coverage for canonical renderer routing, no inline User Admin mutations, backend-authored row/action metadata, system-message recovery/redaction, backend option helpers, legacy Admin Users quarantine, and frontend secret boundary
  - completed checks: `git diff --check`; `mvn -q -Dtest=WorkstreamServiceTest test`; `npm --prefix frontend test -- --run`; `npm --prefix frontend run typecheck`
  - commit: `user-admin-surface-conformance: add fullstack conformance tests`

### TASK-UASCC-99-001: Verify User Admin surface conformance cleanup

- status: done
- source: specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
- task brief: specs/user-admin-surface-conformance-cleanup/tasks/99-verification/01-verify-user-admin-surface-conformance.md
- depends on:
  - TASK-UASCC-04-001
- required reads:
  - AGENTS.md
  - specs/user-admin-surface-conformance-cleanup/README.md
  - specs/user-admin-surface-conformance-cleanup/conversation-capture.md
  - specs/user-admin-surface-conformance-cleanup/pending-tasks.md
  - specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md
  - specs/user-admin-surface-conformance-cleanup/tasks/99-verification/01-verify-user-admin-surface-conformance.md
  - app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md
  - specs/user-admin-surface-navigation-tree/navigation-tree-verification.md
- skills:
  - app-description-readiness-assessment
  - app-description-change-impact
  - akka-http-endpoint-testing
  - akka-web-ui-testing
- expected outputs:
  - specs/user-admin-surface-conformance-cleanup/conformance-verification.md
  - updated pending-tasks.md with done status if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain
- required checks:
  - `git diff --check`
  - `mvn -q -Dtest=WorkstreamServiceTest test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
- done criteria:
  - verification compares completed work against README done state, backlog, app-description, prior navigation-tree verification, task criteria, and command evidence; mini-project is marked complete only if no material gaps remain
- notes:
  - vertical contract: User Admin / `user-admin-agent` concept and runtime normalization/alias result; terminal verification for User Admin structured-surface conformance; dashboard/list/detail/task/decision/workflow/system-message graph; governed browser-tool mappings; role/scope allow/deny; backend/frontend/API evidence; audit/work trace and redaction validation; append follow-ups if gaps remain
  - completed verification: `specs/user-admin-surface-conformance-cleanup/conformance-verification.md`; no material gaps or follow-up tasks appended
  - completed checks: `mvn -q -Dtest=WorkstreamServiceTest test`; `npm --prefix frontend test -- --run`; `npm --prefix frontend run typecheck`; `git diff --check`
  - commit: `user-admin-surface-conformance: verify cleanup completion`
