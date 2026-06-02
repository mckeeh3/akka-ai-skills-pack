# Pending Tasks: Notification Email Delivery Channel

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `notification-email: <short task title>`.

## Tasks

### TASK-NEDC-00-001: Create notification email delivery channel queue

- status: done
- source: current conversation after Notification Platform Foundation completion; user requested email delivery channel and explicitly required Resend service for emails
- task brief: specs/notification-email-delivery-channel/tasks/00-planning/00-create-email-delivery-channel-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/notification-platform-foundation/notification-foundation-handoff.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/notification-email-delivery-channel/README.md
  - specs/notification-email-delivery-channel/conversation-capture.md
  - specs/notification-email-delivery-channel/pending-tasks.md
  - specs/notification-email-delivery-channel/sprints/01-email-delivery-channel-sprint.md
  - specs/notification-email-delivery-channel/backlog/01-email-delivery-channel-build-backlog.md
  - specs/notification-email-delivery-channel/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - Resend service requirement is captured
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: add delivery channel queue`

### TASK-NEDC-01-001: Define email notification channel contract

- status: done
- source: specs/notification-email-delivery-channel/backlog/01-email-delivery-channel-build-backlog.md
- task brief: specs/notification-email-delivery-channel/tasks/01-contracts/01-define-email-channel-contract.md
- depends on:
  - TASK-NEDC-00-001
- required reads:
  - specs/notification-email-delivery-channel/README.md
  - specs/notification-email-delivery-channel/tasks/01-contracts/01-define-email-channel-contract.md
  - specs/notification-platform-foundation/notification-foundation-contract.md
  - specs/notification-platform-foundation/notification-foundation-handoff.md
  - starter Resend/email/outbox service files and docs
  - skills/README.md Resend/email foundation guidance
- skills:
  - none; contract task
- expected outputs:
  - specs/notification-email-delivery-channel/notification-email-channel-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract covers Resend, captured outbox, preferences, category allowlist, redaction, idempotency, audit, fail-closed, and no fake production email success
- done criteria:
  - backend/frontend tasks can proceed without guessing
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: define contract`

### TASK-NEDC-02-001: Implement Resend email notification channel

- status: done
- source: specs/notification-email-delivery-channel/backlog/01-email-delivery-channel-build-backlog.md
- task brief: specs/notification-email-delivery-channel/tasks/02-backend/01-implement-resend-email-channel.md
- depends on:
  - TASK-NEDC-01-001
- required reads:
  - contract from TASK-NEDC-01-001
  - starter notification, Resend email, invitation outbox, audit, and My Account backend files
- skills:
  - akka-resend-email-service if available/relevant
- expected outputs:
  - backend Resend email notification channel implementation
  - captured outbox/local-dev-test behavior
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - focused scans for Resend, captured outbox, provider fail-closed, redaction, preferences, and idempotency
- done criteria:
  - email notification channel uses Resend for production and captured outbox for local/dev/test
  - missing Resend config fails closed without fake success
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: implement resend channel`

### TASK-NEDC-03-001: Wire email notification preference surfaces

- status: done
- source: specs/notification-email-delivery-channel/backlog/01-email-delivery-channel-build-backlog.md
- task brief: specs/notification-email-delivery-channel/tasks/03-surfaces/01-wire-email_preferences_surfaces.md
- depends on:
  - TASK-NEDC-02-001
- required reads:
  - contract and backend implementation from prior tasks
  - My Account notification/preference frontend/backend surface files
- skills:
  - web UI/frontend skills as needed
- expected outputs:
  - My Account email notification preference surfaces and tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - frontend tests/typecheck/build
  - scaffolded backend tests if API/action contracts change
- done criteria:
  - email preference surfaces render backend-derived data and actions
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: wire preference surfaces`

### TASK-NEDC-04-001: Run email channel validation

- status: pending
- source: specs/notification-email-delivery-channel/backlog/01-email-delivery-channel-build-backlog.md
- task brief: specs/notification-email-delivery-channel/tasks/04-validation/01-run-email-channel-validation.md
- depends on:
  - TASK-NEDC-03-001
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
  - local/dev captured outbox checks
  - Resend-provider fail-closed check when configuration absent
  - real Resend smoke only if explicitly configured and safe
- done criteria:
  - validation evidence is captured and blockers are recorded or converted to tasks
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: validate channel`

### TASK-NEDC-05-001: Update email notification channel docs

- status: pending
- source: specs/notification-email-delivery-channel/backlog/01-email-delivery-channel-build-backlog.md
- task brief: specs/notification-email-delivery-channel/tasks/05-docs/01-update-email-channel-docs.md
- depends on:
  - TASK-NEDC-04-001
- required reads:
  - mini-project docs and validation artifacts
- skills:
  - none; docs task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs cover Resend, captured outbox, provider fail-closed, preferences, redaction, and future SMS/push/webhook boundary
- done criteria:
  - future agents understand email channel status and boundaries
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: update docs`

### TASK-NEDC-99-001: Verify notification email delivery channel

- status: pending
- source: mini-project verification loop
- task brief: specs/notification-email-delivery-channel/tasks/99-verification/01-verify-email-channel.md
- depends on:
  - TASK-NEDC-01-001
  - TASK-NEDC-02-001
  - TASK-NEDC-03-001
  - TASK-NEDC-04-001
  - TASK-NEDC-05-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for Resend/outbox/preferences/redaction/idempotency/fail-closed
  - frontend tests/typecheck/build if surfaces changed
  - focused scans for Resend, captured outbox, email preferences, redaction, audit, and future channel boundary
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `notification-email: verify completion`
