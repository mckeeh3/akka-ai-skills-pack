# Pending Tasks: Full-Core SMB User Admin Access-Review Worker

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

### TASK-FCSMB-UARW-00-001: Create User Admin access-review worker queue

- status: done
- source: user approved next full-core SMB User Admin slice after UserAdminAgent guidance completion
- task brief: specs/full-core-smb-user-admin-access-review-worker/tasks/00-planning/00-create-user-admin-access-review-worker-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-component-selection-guide.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-user-admin-access-review-worker/README.md
  - specs/full-core-smb-user-admin-access-review-worker/conversation-capture.md
  - specs/full-core-smb-user-admin-access-review-worker/pending-tasks.md
  - specs/full-core-smb-user-admin-access-review-worker/sprints/*.md
  - specs/full-core-smb-user-admin-access-review-worker/backlog/*.md
  - specs/full-core-smb-user-admin-access-review-worker/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add user admin access review worker queue`

### TASK-FCSMB-UARW-01-001: Inspect access-review worker source boundaries and define implementation map

- status: pending
- source: specs/full-core-smb-user-admin-access-review-worker/backlog/01-access-review-worker-backlog.md
- task brief: specs/full-core-smb-user-admin-access-review-worker/tasks/01-access-review-worker/01-inspect-access-review-worker-boundaries.md
- depends on: [TASK-FCSMB-UARW-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-access-review-worker/README.md
  - specs/full-core-smb-user-admin-access-review-worker/conversation-capture.md
  - specs/full-core-smb-user-admin-access-review-worker/sprints/01-access-review-worker-sprint.md
  - specs/full-core-smb-user-admin-access-review-worker/backlog/01-access-review-worker-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - docs/agent-component-selection-guide.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-agent-seed-documents/SKILL.md
- skills:
  - akka-agents
  - akka-agent-tools
  - akka-agent-tool-boundaries
  - akka-agent-seed-documents
- expected outputs:
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - updated specs/full-core-smb-user-admin-access-review-worker/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend/runtime validation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered access-review, User Admin, workstream, agent/worker, seed, frontend surface, and test boundaries
  - `rg -n "access_review|access-review|AutonomousAgent|UserAdminAgent|userAdminEvidence|user_admin\.access_review|access_review_task|ToolPermissionBoundary|AgentWorkTrace|system_message|provider|no direct mutation" specs/full-core-smb-user-admin-access-review-worker`
- done criteria:
  - backend/frontend source-edit tasks can run without guessing source paths or validation commands
  - implementation map distinguishes deterministic lifecycle responsibilities from governed worker/model responsibilities
  - scope remains SMB access review, not enterprise certification or direct mutation automation
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map user admin access review worker`

### TASK-FCSMB-UARW-99-001: Verify access-review worker readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-user-admin-access-review-worker/tasks/99-verification/01-verify-access-review-worker-readiness.md
- depends on:
  - TASK-FCSMB-UARW-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-user-admin-access-review-worker/README.md
  - specs/full-core-smb-user-admin-access-review-worker/conversation-capture.md
  - specs/full-core-smb-user-admin-access-review-worker/pending-tasks.md
  - specs/full-core-smb-user-admin-access-review-worker/sprints/*.md
  - specs/full-core-smb-user-admin-access-review-worker/backlog/*.md
  - specs/full-core-smb-user-admin-access-review-worker/tasks/**/*.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-user-admin-access-review-worker/pending-tasks.md
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
  - commit message: `full-core-smb: verify access review worker readiness`
