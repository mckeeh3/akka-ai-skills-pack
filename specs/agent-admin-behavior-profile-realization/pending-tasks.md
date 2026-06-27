# Pending Tasks: Agent Admin Behavior Profile Realization

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

### AABP-00-001: Create Agent Admin behavior-profile realization mini-project

- status: done
- source: user request to use a mini-project for completing stale Agent Admin code changes after app-description update
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/00-planning/00-create-mini-project.md`
- depends on: []
- required reads:
  - `AGENTS.md`
  - `specs/AGENTS.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
- skills:
  - `project-discussed-idea-to-pending-project`
- expected outputs:
  - planning scaffold under `specs/agent-admin-behavior-profile-realization/**`
- required checks:
  - `git diff --check -- specs/agent-admin-behavior-profile-realization`
- done criteria:
  - mini-project captures current intent, backlog, task briefs, pending queue, and terminal verification loop
  - planning scaffold is committed
- notes:
  - completed in this commit with message: `Add Agent Admin behavior profile realization plan`
  - next runnable task after completion: `AABP-01-001` (`pending`, depends on `AABP-00-001`)

### AABP-01-001: Map current Agent Admin implementation drift

- status: done
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/00-planning/01-map-current-agent-admin-drift.md`
- depends on: [AABP-00-001]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/README.md`
  - `specs/agent-admin-behavior-profile-realization/conversation-capture.md`
  - `specs/agent-admin-behavior-profile-realization/backlog/01-agent-admin-behavior-profile-build-backlog.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/**`
  - `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- skills:
  - `app-generate-app`
  - `capability-first-backend`
  - `akka-agent-behavior-profiles`
  - `akka-agent-behavior-editing`
- expected outputs:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- required checks:
  - `git diff --check -- specs/agent-admin-behavior-profile-realization`
- done criteria:
  - implementation map classifies current source/test drift and narrows next implementation boundaries
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Map Agent Admin behavior profile drift`
  - next runnable task after completion: `AABP-01-002` (`pending`, depends on `AABP-01-001`)

### AABP-01-002: Implement proposal lifecycle foundation

- status: done
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/01-backend/01-proposal-lifecycle-foundation.md`
- depends on: [AABP-01-001]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- skills:
  - `akka-agent-behavior-editing`
  - `akka-agent-prompt-governance`
  - `akka-agent-skill-governance`
  - `akka-agent-reference-governance`
- expected outputs:
  - backend proposal lifecycle contracts/service behavior/tests
- required checks:
  - `mvn -Dtest='*AgentAdmin*Service*Test,*AgentAdmin*Doc*Test,*AgentAdmin*Proposal*Test' test`
  - `git diff --check`
- done criteria:
  - Save Draft is non-active, activation is separate, stale/high-risk denials are tested
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Implement Agent Admin proposal lifecycle foundation`
  - next runnable task after completion: `AABP-01-003` (`pending`, depends on `AABP-01-002`)

### AABP-01-003: Align doc restore and skill/reference lifecycle

- status: done
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/01-backend/02-doc-restore-skill-reference-lifecycle.md`
- depends on: [AABP-01-002]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- skills:
  - `akka-agent-prompt-governance`
  - `akka-agent-skill-governance`
  - `akka-agent-reference-governance`
  - `akka-agent-behavior-editing`
- expected outputs:
  - restore-as-proposal and skill/reference proposal/deprecation semantics
- required checks:
  - `mvn -Dtest='*AgentAdmin*Service*Test,*AgentAdmin*Doc*Test,*AgentRuntimeToolResolver*Test' test`
  - `git diff --check`
- done criteria:
  - direct restore/create/delete active mutations are reconciled to current intent
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Align Agent Admin document lifecycle semantics`
  - next runnable task after completion: `AABP-01-004` (`pending`, depends on `AABP-01-003`)

### AABP-01-004: Implement behavior-profile version and assignment seams

- status: done
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/01-backend/03-behavior-profile-version-assignments.md`
- depends on: [AABP-01-003]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/data-state/managed-agent-behavior-state.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- skills:
  - `akka-agent-behavior-profiles`
  - `akka-agent-model-governance`
  - `akka-agent-skill-governance`
  - `akka-agent-tool-boundaries`
- expected outputs:
  - tenant-scoped behavior-profile versions and assignment/model seams
- required checks:
  - `mvn -Dtest='*AgentAdmin*Service*Test,*AgentRuntimeService*Test,*Agent*Profile*Test,*ToolBoundary*Test' test`
  - `git diff --check`
- done criteria:
  - profile version seams support runtime loader and safe catalog/detail summaries
  - changes and queue update are committed
- notes:
  - completed in this commit with message: `Implement Agent Admin behavior profile versions`
  - next runnable task after completion: `AABP-02-001` (`pending`, depends on `AABP-01-004`)

### AABP-02-001: Align runtime loader and traces

- status: pending
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/02-runtime/01-runtime-loader-trace-alignment.md`
- depends on: [AABP-01-004]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
  - runtime loader/tool resolver/trace sink tests
- skills:
  - `akka-agent-component-tools`
  - `akka-agent-tool-boundaries`
  - `akka-agent-work-trace`
  - `akka-agent-testing`
- expected outputs:
  - active-profile runtime loading, assigned-doc/generated-tool boundary enforcement, trace metadata
- required checks:
  - `mvn -Dtest='*AgentRuntimeToolResolver*Test,*AgentRuntimeTrace*Test,*AgentRuntimeService*Test,*WorkstreamRuntimeAgent*Test,*AgentAdmin*Test' test`
  - `git diff --check`
- done criteria:
  - runtime uses active behavior only and emits current trace metadata
  - changes and queue update are committed
- notes: []

### AABP-03-001: Wire current Agent Admin workstream/API surfaces

- status: pending
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/03-api/01-workstream-api-current-surfaces.md`
- depends on: [AABP-02-001]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- skills:
  - `akka-http-endpoints`
  - `akka-http-endpoint-component-client`
  - `akka-http-endpoint-testing`
  - `akka-runtime-feature-verification`
- expected outputs:
  - protected workstream/API current surfaces and actions
- required checks:
  - `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest,WorkstreamServiceTest,*AgentAdmin*Service*Test,*AgentRuntime*Test' test`
  - `git diff --check`
- done criteria:
  - current surfaces/actions are API-smoked and stale product actions are not exposed
  - changes and queue update are committed
- notes: []

### AABP-04-001: Align Agent Admin frontend surfaces and contracts

- status: pending
- source: `backlog/01-agent-admin-behavior-profile-build-backlog.md`
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/04-frontend/01-frontend-current-surface-alignment.md`
- depends on: [AABP-03-001]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
  - `frontend/AGENTS.md`
- skills:
  - `akka-web-ui-apps`
  - `akka-web-ui-api-client`
  - `akka-web-ui-state-rendering`
  - `akka-web-ui-accessibility-responsive`
  - `akka-web-ui-testing`
- expected outputs:
  - frontend types/fixtures/renderers/contracts aligned to current Agent Admin inventory
- required checks:
  - `npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs frontend/src/workstream-actions.contract.test.mjs`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - frontend renders current surfaces without stale governance-console/direct-mutation assumptions
  - changes and queue update are committed
- notes: []

### AABP-05-001: Verify Agent Admin behavior-profile realization closure

- status: pending
- source: terminal verification loop for this mini-project
- task brief: `specs/agent-admin-behavior-profile-realization/tasks/05-validation/01-fullstack-closure-verification.md`
- depends on: [AABP-04-001]
- required reads:
  - `specs/agent-admin-behavior-profile-realization/README.md`
  - `specs/agent-admin-behavior-profile-realization/conversation-capture.md`
  - `specs/agent-admin-behavior-profile-realization/pending-tasks.md`
  - `specs/agent-admin-behavior-profile-realization/implementation-map.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/**`
- skills:
  - `akka-runtime-feature-verification`
  - `akka-web-ui-testing`
  - `akka-agent-testing`
  - `akka-agent-work-trace`
- expected outputs:
  - `specs/agent-admin-behavior-profile-realization/verification-notes.md`
  - queue closure or appended follow-up tasks plus new terminal verification task
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - mini-project done state is either achieved and recorded closed, or remaining material gaps are converted into a further bounded follow-up loop
  - changes are committed
- notes: []
