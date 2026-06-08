# Pending Tasks: AI-First SaaS Starter Release Readiness

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `starter-release: <short task title>`.

## Tasks

### TASK-AFSSR-00-001: Create starter release-readiness queue

- status: done
- source: current conversation after notification delivery release readiness; user accepted starter-wide release/package readiness mini-project
- task brief: specs/ai-first-saas-starter-release-readiness/tasks/00-planning/00-create-starter-release-readiness-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/notification-delivery-release-readiness/notification-delivery-release-readiness-handoff.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/ai-first-saas-starter-release-readiness/README.md
  - specs/ai-first-saas-starter-release-readiness/conversation-capture.md
  - specs/ai-first-saas-starter-release-readiness/pending-tasks.md
  - specs/ai-first-saas-starter-release-readiness/sprints/01-starter-release-readiness-sprint.md
  - specs/ai-first-saas-starter-release-readiness/backlog/01-starter-release-readiness-backlog.md
  - specs/ai-first-saas-starter-release-readiness/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `starter-release: add readiness queue`

### TASK-AFSSR-01-001: Review pack and scaffold metadata

- status: done
- source: specs/ai-first-saas-starter-release-readiness/backlog/01-starter-release-readiness-backlog.md
- task brief: specs/ai-first-saas-starter-release-readiness/tasks/01-package/01-review-pack-scaffold-metadata.md
- depends on:
  - TASK-AFSSR-00-001
- required reads:
  - specs/ai-first-saas-starter-release-readiness/README.md
  - specs/ai-first-saas-starter-release-readiness/tasks/01-package/01-review-pack-scaffold-metadata.md
  - pack/install/scaffold metadata files
- skills:
  - none; package/scaffold review task
- expected outputs:
  - specs/ai-first-saas-starter-release-readiness/package-scaffold-release-checklist.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused scans over pack/install/scaffold files for starter references and project-only leakage
- done criteria:
  - checklist covers package/scaffold readiness and leakage guardrails
  - task changes and queue update are committed
- notes:
  - commit message: `starter-release: review scaffold metadata`

### TASK-AFSSR-02-001: Run fresh scaffold fullstack validation

- status: done
- source: specs/ai-first-saas-starter-release-readiness/backlog/01-starter-release-readiness-backlog.md
- task brief: specs/ai-first-saas-starter-release-readiness/tasks/02-validation/01-run-fresh-scaffold-fullstack-validation.md
- depends on:
  - TASK-AFSSR-01-001
- required reads:
  - release checklist from TASK-AFSSR-01-001
  - specs/ai-first-saas-starter-release-readiness/tasks/02-validation/01-run-fresh-scaffold-fullstack-validation.md
- skills:
  - none; validation task
- expected outputs:
  - validation artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffold script execution into a fresh target
  - full backend Maven tests
  - frontend install/tests/typecheck/build
  - provider-skip/fail-closed checks
  - focused scans for key implemented capabilities
- done criteria:
  - fresh scaffold fullstack validation evidence is captured
  - blockers are appended as bounded tasks if found
  - task changes and queue update are committed
- notes:
  - commit message: `starter-release: run fullstack validation`

### TASK-AFSSR-03-001: Audit docs and handoffs

- status: done
- source: specs/ai-first-saas-starter-release-readiness/backlog/01-starter-release-readiness-backlog.md
- task brief: specs/ai-first-saas-starter-release-readiness/tasks/03-docs/01-audit-docs-and-handoffs.md
- depends on:
  - TASK-AFSSR-02-001
- required reads:
  - validation artifact from TASK-AFSSR-02-001
  - starter docs/handoffs
- skills:
  - none; docs audit task
- expected outputs:
  - docs audit artifact and/or focused docs updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused scans for stale missing/future claims around attention, events, AutonomousAgent workers, notifications, Resend email, provider smoke, and scaffold readiness
- done criteria:
  - stale claims or overclaims are fixed or recorded as blockers
  - task changes and queue update are committed
- notes:
  - commit message: `starter-release: audit docs handoffs`

### TASK-AFSSR-03-002: Create starter release notes

- status: done
- source: specs/ai-first-saas-starter-release-readiness/backlog/01-starter-release-readiness-backlog.md
- task brief: specs/ai-first-saas-starter-release-readiness/tasks/03-docs/02-create-release-notes.md
- depends on:
  - TASK-AFSSR-03-001
- required reads:
  - docs audit artifact from TASK-AFSSR-03-001
  - validation artifact from TASK-AFSSR-02-001
- skills:
  - none; release notes task
- expected outputs:
  - starter release notes artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused scan proving release notes mention current core capabilities and future boundaries without overclaiming
- done criteria:
  - release notes are clear, bounded, and validated
  - task changes and queue update are committed
- notes:
  - commit message: `starter-release: add release notes`

### TASK-AFSSR-99-001: Verify starter release readiness

- status: done
- source: mini-project verification loop
- task brief: specs/ai-first-saas-starter-release-readiness/tasks/99-verification/01-verify-starter-release-readiness.md
- depends on:
  - TASK-AFSSR-01-001
  - TASK-AFSSR-02-001
  - TASK-AFSSR-03-001
  - TASK-AFSSR-03-002
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - review checklist, validation, docs audit, release notes, and queue evidence
  - focused scans for release blockers and stale overclaim language
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `starter-release: verify readiness`
  - verification result: release-ready for validated scaffold/package scope; no bounded follow-up tasks required
  - evidence: package/scaffold checklist, fresh scaffold validation, docs/handoff audit, release notes, queue state, `git diff --check`, and focused release-blocker/stale-overclaim scans reviewed
