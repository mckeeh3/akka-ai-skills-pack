# Pending Tasks: Agent Admin Doc Editing Realization

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Mark the selected task `in-progress` before implementation edits.
- Mark a task `done` only after done criteria and required checks pass.
- Each completed task must make one focused git commit containing implementation/planning changes and the queue-status update.
- Record the commit message or hash in task notes.
- If blocked, mark `blocked`, record the blocker and recommended unblock path, and commit the queue update if useful.
- Do not run implementation tasks in parallel.

## Tasks

### AADE-00-001: Create Agent Admin doc-editing realization mini-project

- status: done
- source: user request to create mini-project(s) to fully revise frontend and backend code after Agent Admin app-description reframing
- task brief: `specs/agent-admin-doc-editing-realization/tasks/00-planning/00-create-mini-project.md`
- depends on: []
- required reads:
  - `AGENTS.md`
  - `specs/AGENTS.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- skills:
  - `project-discussed-idea-to-pending-project`
- expected outputs:
  - planning scaffold under `specs/agent-admin-doc-editing-realization/**`
- required checks:
  - `git diff --check -- specs/agent-admin-doc-editing-realization`
- done criteria:
  - mini-project captures current intent, backlog, task briefs, pending queue, and terminal verification loop
  - planning scaffold is committed
- notes:
  - commit message: `Add Agent Admin doc editing realization plan`

### AADE-01-001: Backend contract and service slice

- status: done
- source: `backlog/01-backend-doc-admin-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/01-backend-core/01-backend-contract-service-slice.md`
- depends on: [AADE-00-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/01-backend-doc-admin-sprint.md`
  - `specs/agent-admin-doc-editing-realization/backlog/01-backend-doc-admin-build-backlog.md`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- skills:
  - `capability-first-backend`
  - `akka-solution-decomposition`
- expected outputs:
  - backend service/API contract for Agent Admin doc administration
  - focused unit tests
- required checks:
  - `mvn -Dtest='*AgentAdmin*Service*Test,*Agent*Doc*Test' test` or updated targeted equivalent
  - `git diff --check`
- done criteria:
  - service boundary supports list/detail/doc-read/edit-session/version/diff/restore contracts
  - SaaS-admin-only access decisions are tested
  - changes and queue update are committed
- notes:
  - completed in commit message: `Implement Agent Admin doc service contract slice`
  - next runnable task after completion: `AADE-01-002` (`pending`, depends on `AADE-01-001`)

### AADE-01-002: Durable document version and lifecycle behavior

- status: done
- source: `backlog/01-backend-doc-admin-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/01-backend-core/02-durable-doc-version-lifecycle.md`
- depends on: [AADE-01-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/01-backend-doc-admin-sprint.md`
  - `specs/agent-admin-doc-editing-realization/backlog/01-backend-doc-admin-build-backlog.md`
  - `app-description/domains/core-starter/data-state/managed-agent-behavior-state.md`
- skills:
  - `akka-key-value-entities` or `akka-event-sourced-entities`
  - `akka-kve-unit-testing` or `akka-ese-unit-testing`
- expected outputs:
  - durable prompt/skill/reference version state and lifecycle behavior
  - focused tests
- required checks:
  - `mvn -Dtest='*GovernedDocument*Test,*Agent*Doc*Test,*AgentAdmin*Service*Test' test` or updated targeted equivalent
  - `git diff --check`
- done criteria:
  - versions, adjacent diff, restore, current-version-only save, skill/reference deletion semantics are tested
  - changes and queue update are committed
- notes:
  - completed in commit message: `Implement durable agent document lifecycle`
  - next runnable task after completion: `AADE-02-001` (`pending`, depends on `AADE-01-002`)

### AADE-02-001: Editing-agent draft/revise/save/cancel flow

- status: done
- source: `backlog/02-agent-runtime-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/02-runtime/01-editing-agent-flow.md`
- depends on: [AADE-01-002]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/02-agent-runtime-sprint.md`
  - `specs/agent-admin-doc-editing-realization/backlog/02-agent-runtime-build-backlog.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/agents/functional-agent.md`
- skills:
  - `akka-agent-component`
  - `akka-agent-structured-responses`
  - `akka-agent-testing`
- expected outputs:
  - model-backed/fail-closed editing-agent flow
  - audit/trace capture for edit sessions
  - focused tests
- required checks:
  - `mvn -Dtest='*AgentAdmin*Edit*,*WorkstreamRuntimeAgent*Test,*Agent*Runtime*Test' test` or updated targeted equivalent
  - `git diff --check`
- done criteria:
  - draft/revise/save/cancel behavior works through backend doc state
  - provider/runtime fail-closed behavior is tested
  - changes and queue update are committed
- notes:
  - completed in commit message: `Implement Agent Admin doc editing agent flow`
  - next runnable task after completion: `AADE-02-002` (`pending`, depends on `AADE-02-001`)

### AADE-02-002: Runtime doc loading and read traces

- status: done
- source: `backlog/02-agent-runtime-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/02-runtime/02-runtime-doc-loading-and-traces.md`
- depends on: [AADE-02-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/02-agent-runtime-sprint.md`
  - `specs/agent-admin-doc-editing-realization/backlog/02-agent-runtime-build-backlog.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
- skills:
  - `akka-agent-component-tools`
  - `akka-agent-work-trace`
  - `akka-agent-testing`
- expected outputs:
  - runtime prompt + skill descriptor loading
  - `readSkill` / `readReferenceDoc` behavior
  - Agent Admin-visible read trace metadata
- required checks:
  - `mvn -Dtest='*AgentRuntimeToolResolver*Test,*AgentRuntimeTrace*Test,*AgentRuntimeService*Test,*WorkstreamRuntimeAgent*Test' test` or updated targeted equivalent
  - `git diff --check`
- done criteria:
  - current docs are used at runtime
  - no cross-agent skill discovery path exists
  - read traces are tested
  - changes and queue update are committed
- notes:
  - completed in commit message: `Implement runtime doc loading and read traces`
  - next runnable task after completion: `AADE-03-001` (`pending`, depends on `AADE-02-002`)

### AADE-03-001: Workstream/API action wiring

- status: done
- source: `backlog/03-api-frontend-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/03-api/01-workstream-api-action-wiring.md`
- depends on: [AADE-02-002]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/03-api-workstream-sprint.md`
  - `specs/agent-admin-doc-editing-realization/backlog/03-api-frontend-build-backlog.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- skills:
  - `akka-http-endpoints`
  - `akka-http-endpoint-component-client`
  - `akka-http-endpoint-testing`
- expected outputs:
  - protected Agent Admin workstream/API action wiring
  - typed surface/action DTOs
  - endpoint/workstream tests
- required checks:
  - `mvn -Dtest='*AdminEndpoint*Test,*Workstream*Endpoint*Test,*AgentAdmin*Test' test` or updated targeted equivalent
  - `git diff --check`
- done criteria:
  - backend actions return current Agent Admin surfaces
  - non-SaaS-admin access is denied
  - stale governance actions are not exposed as current Agent Admin UI actions
  - changes and queue update are committed
- notes:
  - completed in commit message: `Implement Agent Admin workstream action wiring`
  - next runnable task after completion: `AADE-04-001` (`pending`, depends on `AADE-03-001`)

### AADE-04-001: Frontend contracts, API types, and fixtures

- status: pending
- source: `backlog/03-api-frontend-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/04-frontend/01-frontend-contracts-api-fixtures.md`
- depends on: [AADE-03-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/04-frontend-sprint.md`
  - `specs/agent-admin-doc-editing-realization/backlog/03-api-frontend-build-backlog.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- skills:
  - `akka-web-ui-api-client`
  - `akka-web-ui-testing`
- expected outputs:
  - frontend types/API contract/fixtures for current Agent Admin surfaces
  - updated contract tests
- required checks:
  - `npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs` or updated targeted equivalent
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - frontend contracts compile with current surface inventory
  - stale governance fixture assumptions are superseded
  - changes and queue update are committed
- notes: []

### AADE-04-002: Frontend browsing, doc, and version surfaces

- status: pending
- source: `backlog/03-api-frontend-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/04-frontend/02-frontend-browsing-doc-version-surfaces.md`
- depends on: [AADE-04-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/04-frontend-sprint.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- skills:
  - `akka-web-ui-apps`
  - `akka-web-ui-state-rendering`
  - `akka-web-ui-accessibility-responsive`
  - `akka-web-ui-testing`
- expected outputs:
  - frontend blank/dashboard/list/detail/doc/version/diff surfaces
  - updated tests
- required checks:
  - `npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs` or updated targeted equivalent
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - read/browse/version surfaces render correctly
  - historical versions are read-only and current-version edit input is enforced in UI state
  - changes and queue update are committed
- notes: []

### AADE-04-003: Frontend edit, create/delete, trace flows, and stale governance cleanup

- status: pending
- source: `backlog/03-api-frontend-build-backlog.md`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/04-frontend/03-frontend-edit-create-delete-trace-flows.md`
- depends on: [AADE-04-002]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/sprints/04-frontend-sprint.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- skills:
  - `akka-web-ui-forms-validation`
  - `akka-web-ui-state-rendering`
  - `akka-web-ui-accessibility-responsive`
  - `akka-web-ui-testing`
- expected outputs:
  - frontend edit-session/create/delete/trace flows
  - stale governance UI/test cleanup
- required checks:
  - `npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-actions.contract.test.mjs frontend/src/workstream-surface-intent-routing.contract.test.mjs` or updated targeted equivalent
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - interactive frontend flows match app-description
  - current tests no longer assert stale governance-console Agent Admin behavior
  - changes and queue update are committed
- notes: []

### AADE-05-001: Full-stack validation and terminal verification

- status: pending
- source: verification loop for this mini-project
- task brief: `specs/agent-admin-doc-editing-realization/tasks/05-validation/01-fullstack-verification.md`
- depends on: [AADE-04-003]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/pending-tasks.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/**`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- skills:
  - `akka-runtime-feature-verification`
  - `akka-web-ui-testing`
  - `akka-agent-testing`
- expected outputs:
  - `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - queue completion or appended follow-up tasks plus new terminal verification task
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - verification determines whether the mini-project done state is achieved
  - material gaps are either absent or converted into follow-up queue tasks with a new terminal verification task
  - changes and queue update are committed
- notes: []
