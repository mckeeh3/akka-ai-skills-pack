# Pending Tasks: Full-Core SMB Baseline and UX

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `full-core-smb: <short task title>`.

## Tasks

### TASK-FCSMB-BASEUX-00-001: Create baseline and UX child queue

- status: done
- source: specs/full-core-smb-saas-hardening/wave-plan.md
- task brief: specs/full-core-smb-baseline-and-ux/tasks/00-planning/00-create-baseline-ux-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/full-core-smb-saas-hardening/pending-tasks.md
  - specs/full-core-smb-saas-hardening/wave-plan.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-baseline-and-ux/README.md
  - specs/full-core-smb-baseline-and-ux/conversation-capture.md
  - specs/full-core-smb-baseline-and-ux/pending-tasks.md
  - specs/full-core-smb-baseline-and-ux/sprints/*.md
  - specs/full-core-smb-baseline-and-ux/backlog/*.md
  - specs/full-core-smb-baseline-and-ux/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - child mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: create first wave queues`

### TASK-FCSMB-BASEUX-01-001: Define executable shared baseline contracts and validation map

- status: pending
- source: specs/full-core-smb-baseline-and-ux/backlog/01-shared-baseline-ux-backlog.md
- task brief: specs/full-core-smb-baseline-and-ux/tasks/01-shared-baseline/01-define-shared-contracts.md
- depends on: [TASK-FCSMB-BASEUX-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-baseline-and-ux/README.md
  - specs/full-core-smb-baseline-and-ux/conversation-capture.md
  - specs/full-core-smb-baseline-and-ux/sprints/01-shared-baseline-ux-sprint.md
  - specs/full-core-smb-baseline-and-ux/backlog/01-shared-baseline-ux-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- required checks:
  - `git diff --check`
  - `rg -n "workstream shell|structured surface|system_message|provider|trace|visual|runtime validation|secret-boundary" specs/full-core-smb-baseline-and-ux`
- done criteria:
  - contracts are specific enough for workstream children to inherit
  - validation map refuses deterministic/demo/model-less normal runtime completion for model-backed paths
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: define shared baseline contracts`

### TASK-FCSMB-BASEUX-99-001: Verify baseline and UX readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-baseline-and-ux/tasks/99-verification/01-verify-baseline-ux-readiness.md
- depends on:
  - TASK-FCSMB-BASEUX-01-001
- required reads:
  - AGENTS.md
  - specs/full-core-smb-baseline-and-ux/README.md
  - specs/full-core-smb-baseline-and-ux/conversation-capture.md
  - specs/full-core-smb-baseline-and-ux/pending-tasks.md
  - specs/full-core-smb-baseline-and-ux/sprints/*.md
  - specs/full-core-smb-baseline-and-ux/backlog/*.md
  - specs/full-core-smb-baseline-and-ux/tasks/**/*.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-baseline-and-ux/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate baseline/UX readiness
- done criteria:
  - child goals have been compared against completed work
  - if complete, completion is recorded with no new required work
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify baseline ux readiness`
