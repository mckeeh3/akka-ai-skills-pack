# Pending Tasks: Notification Delivery Release Readiness

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `notification-readiness: <short task title>`.

## Tasks

### TASK-NDRR-00-001: Create notification delivery readiness queue

- status: done
- source: current conversation after Notification Email Delivery Channel completion; user accepted notification delivery release-readiness mini-project
- task brief: specs/notification-delivery-release-readiness/tasks/00-planning/00-create-notification-delivery-readiness-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/notification-platform-foundation/notification-foundation-handoff.md
  - specs/notification-email-delivery-channel/notification-email-channel-handoff.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/notification-delivery-release-readiness/README.md
  - specs/notification-delivery-release-readiness/conversation-capture.md
  - specs/notification-delivery-release-readiness/pending-tasks.md
  - specs/notification-delivery-release-readiness/sprints/01-notification-delivery-readiness-sprint.md
  - specs/notification-delivery-release-readiness/backlog/01-notification-delivery-readiness-backlog.md
  - specs/notification-delivery-release-readiness/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `notification-readiness: add delivery readiness queue`

### TASK-NDRR-01-001: Create notification delivery readiness checklist

- status: pending
- source: specs/notification-delivery-release-readiness/backlog/01-notification-delivery-readiness-backlog.md
- task brief: specs/notification-delivery-release-readiness/tasks/01-validation/01-create-readiness-checklist.md
- depends on:
  - TASK-NDRR-00-001
- required reads:
  - specs/notification-delivery-release-readiness/README.md
  - specs/notification-delivery-release-readiness/tasks/01-validation/01-create-readiness-checklist.md
  - specs/notification-platform-foundation/notification-foundation-handoff.md
  - specs/notification-email-delivery-channel/notification-email-channel-handoff.md
- skills:
  - none; checklist task
- expected outputs:
  - readiness checklist artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - checklist covers in-app, email, outbox, preferences, allowlist, redaction, idempotency, audit, docs/handoff
  - task changes and queue update are committed
- notes:
  - commit message: `notification-readiness: create checklist`

### TASK-NDRR-01-002: Run fullstack notification delivery validation

- status: pending
- source: specs/notification-delivery-release-readiness/backlog/01-notification-delivery-readiness-backlog.md
- task brief: specs/notification-delivery-release-readiness/tasks/01-validation/02-run-fullstack-notification-validation.md
- depends on:
  - TASK-NDRR-01-001
- required reads:
  - checklist from TASK-NDRR-01-001
  - specs/notification-delivery-release-readiness/tasks/01-validation/02-run-fullstack-notification-validation.md
- skills:
  - none; validation task
- expected outputs:
  - validation artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - fresh scaffold backend tests covering notification projection/email/outbox/preferences/redaction/audit
  - frontend tests/typecheck/build
  - optional real Resend smoke only if explicitly configured and safe
  - focused scans for Resend, captured outbox, backend-owned notification center, redaction, idempotency, audit, and future channel boundary
- done criteria:
  - validation evidence is captured and blockers are recorded or converted to tasks
  - task changes and queue update are committed
- notes:
  - commit message: `notification-readiness: run fullstack validation`

### TASK-NDRR-02-001: Update notification delivery handoff

- status: pending
- source: specs/notification-delivery-release-readiness/backlog/01-notification-delivery-readiness-backlog.md
- task brief: specs/notification-delivery-release-readiness/tasks/02-review/01-update-notification-delivery-handoff.md
- depends on:
  - TASK-NDRR-01-002
- required reads:
  - checklist and validation artifacts from prior tasks
- skills:
  - none; docs/handoff task
- expected outputs:
  - release-readiness handoff
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving handoff distinguishes implemented in-app/email channels from future SMS/push/webhook/analytics work and preserves Resend fail-closed guardrails
- done criteria:
  - handoff accurately describes readiness and boundaries
  - task changes and queue update are committed
- notes:
  - commit message: `notification-readiness: update handoff`

### TASK-NDRR-99-001: Verify notification delivery release readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/notification-delivery-release-readiness/tasks/99-verification/01-verify-notification-delivery-readiness.md
- depends on:
  - TASK-NDRR-01-001
  - TASK-NDRR-01-002
  - TASK-NDRR-02-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - review checklist, validation, and handoff evidence
  - focused scans for notification stack readiness and future channel boundaries
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `notification-readiness: verify completion`
