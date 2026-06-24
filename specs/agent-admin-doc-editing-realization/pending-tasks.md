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

- status: done
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
- notes:
  - completed in commit message: `Implement Agent Admin frontend doc contracts`
  - next runnable task after completion: `AADE-04-002` (`pending`, depends on `AADE-04-001`)

### AADE-04-002: Frontend browsing, doc, and version surfaces

- status: done
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
- notes:
  - completed in commit message: `Implement Agent Admin frontend browse surfaces`
  - next runnable task after completion: `AADE-04-003` (`pending`, depends on `AADE-04-002`)

### AADE-04-003: Frontend edit, create/delete, trace flows, and stale governance cleanup

- status: done
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
- notes:
  - completed in this commit with message: `Implement Agent Admin frontend edit flows`
  - next runnable task after completion: `AADE-05-001` (`pending`, depends on `AADE-04-003`)

### AADE-05-001: Full-stack validation and terminal verification

- status: done
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
- notes:
  - completed terminal verification pass; mini-project not closed because `mvn test` failed on Agent Admin API/workstream smoke and collateral authorization smoke blockers
  - verification notes: `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - commit message: `Verify Agent Admin doc editing full stack`

### AADE-06-001: Repair Agent Admin workstream smoke and stale backend test drift

- status: done
- source: `AADE-05-001` terminal verification gaps
- task brief: `specs/agent-admin-doc-editing-realization/tasks/06-follow-up/01-repair-agent-admin-workstream-smoke-and-stale-tests.md`
- depends on: [AADE-05-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/pending-tasks.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/**`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- skills:
  - `akka-runtime-feature-verification`
  - `akka-agent-testing`
- expected outputs:
  - current Agent Admin Akka workstream/API smoke path repaired
  - stale governance-console backend tests reconciled with current intent
- required checks:
  - `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest,WorkstreamServiceTest,AgentAdminDocAdministrationServiceTest,AgentAdminDocEditingAgentTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,AgentRuntimeTraceSinkTest' test`
  - `npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs frontend/src/workstream-surface-intent-routing.contract.test.mjs`
  - `git diff --check`
- done criteria:
  - Agent Admin backend/API smoke proves current SaaS-admin doc-editing surfaces at the implemented scope
  - stale tenant-scoped/governance-console Agent Admin assertions no longer block current validation
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Repair Agent Admin workstream smoke and stale tests`
  - backend/API smoke now exercises SaaS Owner/Admin current doc-editing list/detail/prompt/history/trace surfaces; non-SaaS-admin denial remains `agent-admin-requires-saas-owner-admin`
  - stale tenant-scoped/governance-console assertions were reconciled as non-current/internal substrate or replaced with current Agent Admin doc-editing assertions
  - next runnable task after completion: `AADE-06-002` (`pending`, depends on `AADE-06-001`)

### AADE-06-002: Repair collateral full-suite authorization smoke blockers

- status: done
- source: `AADE-05-001` full-suite validation gaps outside direct Agent Admin product behavior
- task brief: `specs/agent-admin-doc-editing-realization/tasks/06-follow-up/02-repair-collateral-full-suite-authorization-smokes.md`
- depends on: [AADE-06-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - `specs/agent-admin-doc-editing-realization/pending-tasks.md`
  - affected smoke tests and workstream authorization/runtime paths named in the verification notes
- skills:
  - `akka-runtime-feature-verification`
  - `akka-web-ui-testing`
- expected outputs:
  - Audit/Trace SaaS Owner/Admin scope evidence repaired or reconciled
  - My Account hosted workstream smoke no longer blocked by Agent Admin SaaS-admin authorization changes
- required checks:
  - `mvn -Dtest='AuditTraceBrowserWorkstreamSmokeTest,MyAccountBrowserWorkstreamSmokeTest,WorkstreamServiceTest' test`
  - `git diff --check`
- done criteria:
  - named collateral full-suite failures from `AADE-05-001` pass
  - shared authorization behavior remains fail-closed and browser-safe
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Repair collateral authorization smokes`
  - Audit/Trace SaaS Owner dashboard now exposes browser-safe SaaS-owner scope evidence.
  - My Account open-workstream action now denies non-SaaS-owner Agent Admin opens with a browser-safe redacted system message while SaaS Owner/Admin opens still resolve the Agent Admin dashboard.
  - next runnable task after completion: `AADE-07-001` (`pending`, depends on `AADE-06-001` and `AADE-06-002`)

### AADE-07-001: Re-run full-stack closure verification

- status: done
- source: terminal verification loop after `AADE-06-*` follow-ups
- task brief: `specs/agent-admin-doc-editing-realization/tasks/07-validation/01-reverify-fullstack-closure.md`
- depends on: [AADE-06-001, AADE-06-002]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - `specs/agent-admin-doc-editing-realization/pending-tasks.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/**`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- skills:
  - `akka-runtime-feature-verification`
  - `akka-web-ui-testing`
  - `akka-agent-testing`
- expected outputs:
  - updated `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - queue closure or another bounded follow-up loop
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - mini-project done state is either achieved and recorded closed, or remaining material gaps are converted into a further bounded follow-up loop
  - changes and queue update are committed
- notes:
  - completed terminal verification pass; mini-project not closed because full `mvn test` still fails at `AgentAdminBrowserWorkstreamSmokeTest.protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions:100` when the `User Admin` filtered list returns empty rows.
  - targeted diagnostic `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test` passed, so the remaining gap appears full-suite/order-dependent rather than a simple isolated smoke failure.
  - frontend test, typecheck, build, and `git diff --check` passed.
  - appended `AADE-08-001` to repair Agent Admin full-suite smoke isolation and `AADE-09-001` as the next terminal verification task.
  - verification notes: `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - commit message: `Reverify Agent Admin doc editing closure`

### AADE-08-001: Repair Agent Admin full-suite smoke isolation

- status: done
- source: `AADE-07-001` terminal verification gap
- task brief: `specs/agent-admin-doc-editing-realization/tasks/08-follow-up/01-repair-agent-admin-full-suite-isolation.md`
- depends on: [AADE-07-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - `specs/agent-admin-doc-editing-realization/pending-tasks.md`
  - `src/test/java/ai/first/application/coreapp/workstream/AgentAdminBrowserWorkstreamSmokeTest.java`
  - affected Agent Admin doc-admin service/workstream action/runtime seeding paths used by `list-agent-doc-agents`
  - app-description Agent Admin files only as needed to confirm current SaaS-admin-only doc-editing intent
- skills:
  - `akka-runtime-feature-verification`
  - `akka-agent-testing`
- expected outputs:
  - deterministic Agent Admin protected workstream smoke under full-suite execution
  - implementation/test repair for the order-dependent empty `User Admin` filtered rows
- required checks:
  - `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test`
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - isolated and full-suite Agent Admin protected workstream smoke pass
  - SaaS-admin-only Agent Admin authorization and current doc-editing API coverage are preserved
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Repair Agent Admin full-suite smoke isolation`
  - Agent Admin catalog reads now merge starter core agent definitions from authoritative entities with the eventually consistent catalog view, making the `User Admin` filtered protected workstream smoke deterministic after full-suite/TestKit startup.
  - verification passed for isolated `AgentAdminBrowserWorkstreamSmokeTest`, full `mvn test`, frontend tests/typecheck/build, and `git diff --check`.
  - next runnable task after completion: `AADE-09-001` (`pending`, depends on `AADE-08-001`)

### AADE-09-001: Re-run full-stack closure verification

- status: done
- source: terminal verification loop after `AADE-08-001`
- task brief: `specs/agent-admin-doc-editing-realization/tasks/09-validation/01-reverify-fullstack-closure.md`
- depends on: [AADE-08-001]
- required reads:
  - `specs/agent-admin-doc-editing-realization/README.md`
  - `specs/agent-admin-doc-editing-realization/conversation-capture.md`
  - `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - `specs/agent-admin-doc-editing-realization/pending-tasks.md`
  - `specs/agent-admin-doc-editing-realization/tasks/08-follow-up/01-repair-agent-admin-full-suite-isolation.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/**`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- skills:
  - `akka-runtime-feature-verification`
  - `akka-web-ui-testing`
  - `akka-agent-testing`
- expected outputs:
  - updated `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - queue closure or another bounded follow-up loop
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - mini-project done state is either achieved and recorded closed, or remaining material gaps are converted into a further bounded follow-up loop
  - changes are committed
- notes:
  - completed terminal verification pass; mini-project closed because full `mvn test`, frontend test/typecheck/build, evidence validators, and `git diff --check` passed.
  - `AgentAdminBrowserWorkstreamSmokeTest` passed inside the full Maven suite, confirming the `AADE-08-001` full-suite/order-dependent empty filtered-row repair held.
  - no further follow-up tasks were appended.
  - verification notes: `specs/agent-admin-doc-editing-realization/verification-notes.md`
  - commit message: `Close Agent Admin doc editing realization`
