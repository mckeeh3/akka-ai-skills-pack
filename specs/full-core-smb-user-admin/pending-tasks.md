# Pending Tasks: Full-Core SMB User Admin

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `full-core-smb: <short task title>`.

## Tasks

### TASK-FCSMB-UA-00-001: Create User Admin child queue

- status: done
- source: specs/full-core-smb-saas-hardening/wave-plan.md
- task brief: specs/full-core-smb-user-admin/tasks/00-planning/00-create-user-admin-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/full-core-smb-saas-hardening/pending-tasks.md
  - specs/full-core-smb-saas-hardening/wave-plan.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-user-admin/README.md
  - specs/full-core-smb-user-admin/conversation-capture.md
  - specs/full-core-smb-user-admin/pending-tasks.md
  - specs/full-core-smb-user-admin/sprints/*.md
  - specs/full-core-smb-user-admin/backlog/*.md
  - specs/full-core-smb-user-admin/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - child mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: create first wave queues`

### TASK-FCSMB-UA-01-001: Define User Admin vertical slice contracts and implementation map

- status: pending
- source: specs/full-core-smb-user-admin/backlog/01-user-admin-vertical-contract-backlog.md
- task brief: specs/full-core-smb-user-admin/tasks/01-user-admin/01-define-user-admin-vertical-contracts.md
- depends on: [TASK-FCSMB-UA-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin/README.md
  - specs/full-core-smb-user-admin/conversation-capture.md
  - specs/full-core-smb-user-admin/sprints/01-user-admin-vertical-contract-sprint.md
  - specs/full-core-smb-user-admin/backlog/01-user-admin-vertical-contract-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - optionally appended bounded implementation tasks if enough detail is available
- required checks:
  - `git diff --check`
  - `rg -n "User Admin|invitation|member|role|disable|reactivate|access review|UserAdminAgent|AutonomousAgent|deterministic|audit|trace|runtime validation" specs/full-core-smb-user-admin`
- done criteria:
  - first User Admin implementation slice is bounded enough for a fresh harness task
  - agent, worker, deterministic-service, surface, capability, trace, and validation boundaries are explicit
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: define user admin vertical contracts`

### TASK-FCSMB-UA-99-001: Verify User Admin readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-user-admin/tasks/99-verification/01-verify-user-admin-readiness.md
- depends on:
  - TASK-FCSMB-UA-01-001
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin/README.md
  - specs/full-core-smb-user-admin/conversation-capture.md
  - specs/full-core-smb-user-admin/pending-tasks.md
  - specs/full-core-smb-user-admin/sprints/*.md
  - specs/full-core-smb-user-admin/backlog/*.md
  - specs/full-core-smb-user-admin/tasks/**/*.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-user-admin/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate User Admin readiness
- done criteria:
  - child goals have been compared against completed work
  - if complete, completion is recorded with no new required work
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify user admin readiness`
