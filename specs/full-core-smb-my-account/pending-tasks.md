# Pending Tasks: Full-Core SMB My Account

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

### TASK-FCSMB-MA-00-001: Create My Account full-core queue

- status: done
- source: user approved next full-core SMB workstream after Governance/Policy completion
- task brief: specs/full-core-smb-my-account/tasks/00-planning/00-create-my-account-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-component-selection-guide.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/my-account-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
  - specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-my-account/README.md
  - specs/full-core-smb-my-account/conversation-capture.md
  - specs/full-core-smb-my-account/pending-tasks.md
  - specs/full-core-smb-my-account/sprints/*.md
  - specs/full-core-smb-my-account/backlog/*.md
  - specs/full-core-smb-my-account/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add my account queue`

### TASK-FCSMB-MA-01-001: Define My Account vertical slice contracts and implementation map

- status: pending
- source: specs/full-core-smb-my-account/backlog/01-my-account-full-core-backlog.md
- task brief: specs/full-core-smb-my-account/tasks/01-my-account/01-define-my-account-implementation-map.md
- depends on: [TASK-FCSMB-MA-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-my-account/README.md
  - specs/full-core-smb-my-account/conversation-capture.md
  - specs/full-core-smb-my-account/sprints/01-my-account-full-core-sprint.md
  - specs/full-core-smb-my-account/backlog/01-my-account-full-core-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/my-account-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
  - specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
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
  - specs/full-core-smb-my-account/my-account-implementation-map.md
  - updated specs/full-core-smb-my-account/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend/runtime validation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered My Account, `/api/me`, context, profile/settings, attention, workstream navigation, agent/worker, frontend user-tile/surface, and test boundaries
  - `rg -n "My Account|MyAccountAgent|my_account|/api/me|selected context|authority|profile|settings|personal attention|user tile|trace refs|open_authorized_workstream|ToolPermissionBoundary|provider|system_message|tenant|no duplicate top-rail" specs/full-core-smb-my-account`
- done criteria:
  - backend/frontend source-edit tasks can run without guessing source paths or validation commands
  - implementation map distinguishes deterministic account/context/settings/attention/navigation responsibilities from model-backed guidance/worker responsibilities
  - scope remains SMB self-service My Account, not identity-provider administration or admin mutation scope
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map my account full core`

### TASK-FCSMB-MA-99-001: Verify My Account full-core readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-my-account/tasks/99-verification/01-verify-my-account-readiness.md
- depends on:
  - TASK-FCSMB-MA-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-my-account/README.md
  - specs/full-core-smb-my-account/conversation-capture.md
  - specs/full-core-smb-my-account/pending-tasks.md
  - specs/full-core-smb-my-account/sprints/*.md
  - specs/full-core-smb-my-account/backlog/*.md
  - specs/full-core-smb-my-account/tasks/**/*.md
  - specs/full-core-smb-my-account/my-account-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/my-account-workstream-v0/workstream-contract.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-my-account/pending-tasks.md
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
  - commit message: `full-core-smb: verify my account readiness`
