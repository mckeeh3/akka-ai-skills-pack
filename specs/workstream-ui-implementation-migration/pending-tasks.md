# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the workstream UI implementation migration, rooted at `specs/workstream-ui-implementation-migration/`.

## Tasks

### TASK-WUI-00-001: Create workstream UI implementation migration planning scaffold

- status: done
- source: user request to migrate stale frontend/seed code to canonical workstream UI reference
- task brief: specs/workstream-ui-implementation-migration/tasks/00-planning-scaffold/00-create-workstream-ui-implementation-migration-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - skills/akka-web-ui-apps/SKILL.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/workstream-ui-implementation-migration/README.md
  - specs/workstream-ui-implementation-migration/conversation-capture.md
  - specs/workstream-ui-implementation-migration/pending-tasks.md
  - specs/workstream-ui-implementation-migration/sprints/*.md
  - specs/workstream-ui-implementation-migration/backlog/*.md
- required checks:
  - verify git status contains only migration planning scaffold files before commit
- done criteria:
  - migration has captured rationale, sprint sequence, backlogs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `Add workstream UI implementation migration plan`
  - completed: created planning scaffold, conversation capture, sprint specs, build backlogs, task briefs, and queued implementation tasks.

### TASK-WUI-01-001: Inventory stale frontend and seed code

- status: done
- source: specs/workstream-ui-implementation-migration/backlog/01-inventory-and-target-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/01-inventory-and-target/01-inventory-stale-frontend-and-seed-code.md
- depends on: [TASK-WUI-00-001]
- required reads:
  - AGENTS.md
  - specs/workstream-ui-implementation-migration/README.md
  - specs/workstream-ui-implementation-migration/conversation-capture.md
  - specs/workstream-ui-implementation-migration/sprints/01-inventory-and-target-sprint.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - skills/akka-web-ui-apps/SKILL.md
  - frontend/**
  - src/main/resources/static-resources/**
- skills:
  - akka-web-ui-apps
  - agent-workstream-apps
- expected outputs:
  - specs/workstream-ui-implementation-migration/frontend-stale-code-inventory.md
- required checks:
  - identify canonical, stale, generated, and legacy frontend/static files
  - no production code rewrite in this inventory task
- done criteria:
  - inventory records retire/revise/quarantine recommendations
  - task changes and queue update are committed
- notes:
  - commit message: `Inventory stale frontend and seed code`
  - completed: created frontend/static-resource inventory with canonical, stale, generated, legacy/quarantine, revise, and retire recommendations; no production frontend code rewritten.

### TASK-WUI-01-002: Define workstream UI target architecture

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/01-inventory-and-target-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/01-inventory-and-target/02-define-workstream-ui-target-architecture.md
- depends on: [TASK-WUI-01-001]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/web-ui-frontend-decomposition.md
  - specs/workstream-ui-implementation-migration/frontend-stale-code-inventory.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-state-rendering
  - akka-web-ui-realtime
- expected outputs:
  - docs/workstream-ui-reference-architecture.md
- required checks:
  - target source layout distinguishes shell, rail, composer, stream, surfaces, actions, fixtures, and tests
  - routes are deep links, not primary decomposition
- done criteria:
  - implementation target is specific enough for component-library tasks
  - task changes and queue update are committed
- notes: []

### TASK-WUI-02-001: Add workstream UI types and fixtures

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/02-component-library-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/02-component-library/01-add-workstream-ui-types-and-fixtures.md
- depends on: [TASK-WUI-01-002]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/structured-surface-contracts.md
  - frontend/src/api/**
- skills:
  - akka-web-ui-api-client
  - akka-web-ui-testing
- expected outputs:
  - frontend/src/workstream/types/**
  - frontend/src/workstream/fixtures/**
  - focused contract tests
- required checks:
  - fixtures include `/api/me`, AuthContext, functional agents, workstream items, surface envelopes, surface actions, and events
- done criteria:
  - reusable workstream UI data contracts exist
  - task changes and queue update are committed
- notes: []

### TASK-WUI-02-002: Add shell, rail, context, and composer components

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/02-component-library-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/02-component-library/02-add-shell-rail-composer-components.md
- depends on: [TASK-WUI-02-001]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - skills/akka-web-ui-accessibility-responsive/SKILL.md
  - frontend/src/workstream/types/**
- skills:
  - akka-web-ui-apps
  - akka-web-ui-accessibility-responsive
  - akka-web-ui-testing
- expected outputs:
  - frontend/src/workstream/shell/**
  - frontend/src/workstream/rail/**
  - frontend/src/workstream/composer/**
  - focused contract tests
- required checks:
  - rail is collapsible and role/capability aware
  - composer is persistent and selected-agent aware
  - context/authority indicators are visible
- done criteria:
  - reusable shell components exist
  - task changes and queue update are committed
- notes: []

### TASK-WUI-02-003: Add stream and structured surface components

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/02-component-library-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/02-component-library/03-add-stream-and-surface-components.md
- depends on: [TASK-WUI-02-002]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/structured-surface-contracts.md
  - frontend/src/workstream/types/**
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-forms-validation
  - akka-web-ui-testing
- expected outputs:
  - frontend/src/workstream/stream/**
  - frontend/src/workstream/surfaces/**
  - focused contract tests
- required checks:
  - surface components include dashboard, list/search, detail/edit, decision, audit, workflow, governance diff, and outcome patterns
  - action feedback stream item is supported for non-chat UI navigation/actions
- done criteria:
  - reusable stream and surface components exist
  - task changes and queue update are committed
- notes: []

### TASK-WUI-02-004: Add capability action components

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/02-component-library-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/02-component-library/04-add-capability-action-components.md
- depends on: [TASK-WUI-02-003]
- required reads:
  - docs/structured-surface-contracts.md
  - docs/capability-first-backend-architecture.md
  - frontend/src/workstream/surfaces/**
- skills:
  - akka-web-ui-forms-validation
  - akka-web-ui-testing
- expected outputs:
  - frontend/src/workstream/actions/**
  - focused contract tests
- required checks:
  - capability action rendering preserves disabled/denied reasons, idempotency, confirmation, trace/audit, and result-surface behavior
- done criteria:
  - reusable capability action controls exist
  - task changes and queue update are committed
- notes: []

### TASK-WUI-03-001: Migrate app shell to workstream shell

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/03-frontend-migration-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/03-frontend-migration/01-migrate-app-shell-to-workstream-shell.md
- depends on: [TASK-WUI-02-004]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - specs/workstream-ui-implementation-migration/frontend-stale-code-inventory.md
  - frontend/src/main.tsx
  - frontend/src/workstream/**
- skills:
  - akka-web-ui-apps
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - revised frontend app shell using reusable workstream components
- required checks:
  - run frontend checks/build available in `frontend/package.json`
- done criteria:
  - primary frontend reference is workstream-first
  - task changes and queue update are committed
- notes: []

### TASK-WUI-03-002: Connect fixture clients and deep links

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/03-frontend-migration-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/03-frontend-migration/02-connect-fixture-clients-and-deep-links.md
- depends on: [TASK-WUI-03-001]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - frontend/src/api/**
  - frontend/src/workstream/**
- skills:
  - akka-web-ui-api-client
  - akka-web-ui-realtime
  - akka-web-ui-testing
- expected outputs:
  - workstream-first fixture API/realtime clients wired into the migrated shell
  - deep-link handling for functional agents, stream items, and surfaces
- required checks:
  - no stale page route is treated as primary app decomposition
  - frontend checks/build pass
- done criteria:
  - fixture clients and deep links support workstream reference use
  - task changes and queue update are committed
- notes: []

### TASK-WUI-04-001: Implement User Admin dashboard and list surfaces

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/04-reference-vertical-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/04-reference-vertical/01-implement-user-admin-dashboard-and-list-surfaces.md
- depends on: [TASK-WUI-03-002]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/structured-surface-contracts.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/02-user-admin-command-center.md
  - frontend/src/workstream/**
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-forms-validation
  - akka-web-ui-testing
- expected outputs:
  - User Admin functional-agent dashboard and list/search surfaces
  - action feedback items for surface navigation
  - tests for dashboard/list/composer paths
- required checks:
  - actions map to capability ids
  - list/search is a surface, not a page-first route
  - frontend checks/build pass
- done criteria:
  - first half of User Admin reference vertical works through surfaces
  - task changes and queue update are committed
- notes: []

### TASK-WUI-04-002: Implement User Detail/Edit surface

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/04-reference-vertical-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/04-reference-vertical/02-implement-user-detail-edit-surface.md
- depends on: [TASK-WUI-04-001]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/structured-surface-contracts.md
  - frontend/src/workstream/**
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-forms-validation
  - akka-web-ui-testing
- expected outputs:
  - User Detail/Edit surface
  - permission-aware edit actions and denial states
  - tests for list-to-detail, composer navigation, and trace/audit affordances
- required checks:
  - user detail/edit is a surface, not a standalone page-first screen
  - frontend checks/build pass
- done criteria:
  - User Admin vertical demonstrates dashboard → list/search → detail/edit workstream flow
  - task changes and queue update are committed
- notes: []

### TASK-WUI-05-001: Update frontend contract tests

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/05-tests-and-docs-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/05-tests-and-docs/01-update-frontend-contract-tests.md
- depends on: [TASK-WUI-04-002]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - docs/web-ui-quality-checklist.md
  - frontend/src/**/*.test.*
- skills:
  - akka-web-ui-testing
- expected outputs:
  - revised frontend contract tests
- required checks:
  - tests cover shell, rail, composer, surfaces, capability actions, deep links, forbidden states, and stale/realtime states where applicable
  - frontend checks/build pass
- done criteria:
  - tests encode the canonical workstream UI reference
  - task changes and queue update are committed
- notes: []

### TASK-WUI-05-002: Update docs and skills for reference components

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/05-tests-and-docs-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/05-tests-and-docs/02-update-docs-and-skills-for-reference-components.md
- depends on: [TASK-WUI-05-001]
- required reads:
  - docs/workstream-ui-reference-architecture.md
  - skills/akka-web-ui-apps/SKILL.md
  - docs/web-ui-frontend-decomposition.md
  - docs/web-ui-quality-checklist.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
- skills:
  - akka-web-ui-apps
  - app-description-ui
- expected outputs:
  - docs/skills updates pointing to reusable frontend reference
  - stale seed-app UI docs revised or marked legacy
- required checks:
  - guidance points to workstream components and User Admin vertical reference
  - no page-first frontend reference is promoted as canonical
- done criteria:
  - docs/skills are aligned with implementation reference
  - task changes and queue update are committed
- notes: []

### TASK-WUI-06-001: Final workstream UI implementation review

- status: pending
- source: specs/workstream-ui-implementation-migration/backlog/06-final-review-build-backlog.md
- task brief: specs/workstream-ui-implementation-migration/tasks/06-review/01-final-workstream-ui-implementation-review.md
- depends on: [TASK-WUI-05-002]
- required reads:
  - specs/workstream-ui-implementation-migration/README.md
  - specs/workstream-ui-implementation-migration/frontend-stale-code-inventory.md
  - docs/workstream-ui-reference-architecture.md
  - frontend/**
  - skills/akka-web-ui-apps/SKILL.md
  - docs/web-ui-frontend-decomposition.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-testing
- expected outputs:
  - specs/workstream-ui-implementation-migration/migration-completion-summary.md
  - small final fixes or follow-up task notes if needed
- required checks:
  - drift searches for stale page-first/frontend seed references
  - frontend checks/build pass
  - docs/skills/tests point to the canonical workstream reference
- done criteria:
  - migration completion summary exists
  - remaining gaps are tracked or explicitly accepted
  - task changes and queue update are committed
- notes: []
