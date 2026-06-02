# Pending Tasks: Notification Platform Foundation

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `notification-foundation: <short task title>`.

## Tasks

### TASK-NPF-00-001: Create notification foundation queue

- status: done
- source: current conversation after AutonomousAgent real-provider smoke readiness; user accepted recommended notification platform foundation mini-project
- task brief: specs/notification-platform-foundation/tasks/00-planning/00-create-notification-foundation-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/autonomous-agent-real-provider-smoke-readiness/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/notification-platform-foundation/README.md
  - specs/notification-platform-foundation/conversation-capture.md
  - specs/notification-platform-foundation/pending-tasks.md
  - specs/notification-platform-foundation/sprints/01-notification-foundation-sprint.md
  - specs/notification-platform-foundation/backlog/01-notification-foundation-build-backlog.md
  - specs/notification-platform-foundation/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: add queue`

### TASK-NPF-01-001: Define notification foundation contract

- status: done
- source: specs/notification-platform-foundation/backlog/01-notification-foundation-build-backlog.md
- task brief: specs/notification-platform-foundation/tasks/01-contracts/01-define-notification-contract.md
- depends on:
  - TASK-NPF-00-001
- required reads:
  - specs/notification-platform-foundation/README.md
  - specs/notification-platform-foundation/tasks/01-contracts/01-define-notification-contract.md
  - attention, event backbone, My Account digest, and My Account surface docs/files
- skills:
  - none; contract task
- expected outputs:
  - specs/notification-platform-foundation/notification-foundation-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract covers in-app notifications, preferences, projection inputs, AuthContext redaction, lifecycle, My Account surface, and future email/push boundary
- done criteria:
  - backend/frontend tasks can proceed without guessing
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: define contract`

### TASK-NPF-02-001: Implement notification backend foundation

- status: pending
- source: specs/notification-platform-foundation/backlog/01-notification-foundation-build-backlog.md
- task brief: specs/notification-platform-foundation/tasks/02-backend/01-implement-notification-backend.md
- depends on:
  - TASK-NPF-01-001
- required reads:
  - contract from TASK-NPF-01-001
  - starter attention/event/My Account backend files
- skills:
  - none unless focused Akka component skill is needed
- expected outputs:
  - backend notification projection/lifecycle/preferences implementation
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - focused scans for notification projection, lifecycle, preferences, and redaction
- done criteria:
  - backend notification foundation exists and is governed/scoped
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: implement backend`

### TASK-NPF-03-001: Wire My Account notification center surfaces

- status: pending
- source: specs/notification-platform-foundation/backlog/01-notification-foundation-build-backlog.md
- task brief: specs/notification-platform-foundation/tasks/03-surfaces/01-wire-notification-center-surfaces.md
- depends on:
  - TASK-NPF-02-001
- required reads:
  - contract and backend implementation from prior tasks
  - My Account frontend/backend surface files
- skills:
  - web UI/frontend skills as needed
- expected outputs:
  - My Account notification center surfaces and tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - frontend tests/typecheck/build
  - scaffolded backend tests if API/action contracts change
- done criteria:
  - notification center renders backend-derived data and actions
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: wire surfaces`

### TASK-NPF-04-001: Run notification foundation validation

- status: pending
- source: specs/notification-platform-foundation/backlog/01-notification-foundation-build-backlog.md
- task brief: specs/notification-platform-foundation/tasks/04-validation/01-run-notification-foundation-validation.md
- depends on:
  - TASK-NPF-03-001
- required reads:
  - mini-project artifacts and task brief
- skills:
  - none; validation task
- expected outputs:
  - validation artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - frontend tests/typecheck/build
  - focused scans for backend-owned notifications and no hidden-workstream leakage
- done criteria:
  - validation evidence is captured and blockers are recorded or converted to tasks
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: validate`

### TASK-NPF-05-001: Update notification foundation docs

- status: pending
- source: specs/notification-platform-foundation/backlog/01-notification-foundation-build-backlog.md
- task brief: specs/notification-platform-foundation/tasks/05-docs/01-update-notification-foundation-docs.md
- depends on:
  - TASK-NPF-04-001
- required reads:
  - mini-project docs and validation artifacts
- skills:
  - none; docs task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish implemented in-app notification foundation from future email/push platform
- done criteria:
  - future agents understand notification foundation status and boundaries
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: update docs`

### TASK-NPF-99-001: Verify notification foundation

- status: pending
- source: mini-project verification loop
- task brief: specs/notification-platform-foundation/tasks/99-verification/01-verify-notification-foundation.md
- depends on:
  - TASK-NPF-01-001
  - TASK-NPF-02-001
  - TASK-NPF-03-001
  - TASK-NPF-04-001
  - TASK-NPF-05-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for projection/lifecycle/preferences/redaction
  - frontend tests/typecheck/build
  - focused scans for notification contract, backend projection, My Account surface, future email/push boundary
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `notification-foundation: verify completion`
