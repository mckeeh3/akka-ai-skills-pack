# Pending Tasks: Full-Core SMB User Admin Access Management

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `full-core-smb: <short task title>`.

## Tasks

### TASK-FCSMB-UAM-00-001: Create User Admin access management queue

- status: done
- source: user approved next User Admin full-core slice after dashboard/invitation foundation
- task brief: specs/full-core-smb-user-admin-access-management/tasks/00-planning/00-create-user-admin-access-management-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/pending-tasks.md
  - specs/full-core-smb-user-admin-access-management/sprints/*.md
  - specs/full-core-smb-user-admin-access-management/backlog/*.md
  - specs/full-core-smb-user-admin-access-management/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add user admin access management queue`

### TASK-FCSMB-UAM-01-001: Inspect access-management source boundaries and define implementation map

- status: pending
- source: specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
- task brief: specs/full-core-smb-user-admin-access-management/tasks/01-access-management/01-inspect-access-management-boundaries.md
- depends on: [TASK-FCSMB-UAM-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/sprints/01-user-admin-access-management-sprint.md
  - specs/full-core-smb-user-admin-access-management/backlog/01-user-admin-access-management-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin/source-boundary-notes.md
- skills:
  - none; repository source-discovery/planning task
- expected outputs:
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend implementation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered member/status/role/capability source and test boundaries
  - `rg -n "disable|reactivate|role|capability|last-admin|self-disable|idempotency|trace|system_message|runtime validation" specs/full-core-smb-user-admin-access-management`
- done criteria:
  - backend and frontend source-edit tasks can run without guessing source paths or validation commands
  - member status and role/capability scope is bounded to SMB and preserves deterministic authority
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map user admin access management`

### TASK-FCSMB-UAM-99-001: Verify User Admin access management readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-user-admin-access-management/tasks/99-verification/01-verify-user-admin-access-management.md
- depends on:
  - TASK-FCSMB-UAM-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-user-admin-access-management/README.md
  - specs/full-core-smb-user-admin-access-management/conversation-capture.md
  - specs/full-core-smb-user-admin-access-management/pending-tasks.md
  - specs/full-core-smb-user-admin-access-management/sprints/*.md
  - specs/full-core-smb-user-admin-access-management/backlog/*.md
  - specs/full-core-smb-user-admin-access-management/tasks/**/*.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-user-admin-access-management/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate implementation-map/source-edit task readiness
- done criteria:
  - mini-project goals have been compared against completed work
  - if ready, next implementation task is runnable without guessing
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify access management readiness`
