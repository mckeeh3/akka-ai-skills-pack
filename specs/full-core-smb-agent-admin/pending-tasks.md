# Pending Tasks: Full-Core SMB Agent Admin

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

### TASK-FCSMB-AA-00-001: Create Agent Admin full-core queue

- status: done
- source: user approved next full-core SMB workstream after User Admin completion
- task brief: specs/full-core-smb-agent-admin/tasks/00-planning/00-create-agent-admin-queue.md
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
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-agent-admin/README.md
  - specs/full-core-smb-agent-admin/conversation-capture.md
  - specs/full-core-smb-agent-admin/pending-tasks.md
  - specs/full-core-smb-agent-admin/sprints/*.md
  - specs/full-core-smb-agent-admin/backlog/*.md
  - specs/full-core-smb-agent-admin/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add agent admin queue`

### TASK-FCSMB-AA-01-001: Define Agent Admin vertical slice contracts and implementation map

- status: pending
- source: specs/full-core-smb-agent-admin/backlog/01-agent-admin-full-core-backlog.md
- task brief: specs/full-core-smb-agent-admin/tasks/01-agent-admin/01-define-agent-admin-implementation-map.md
- depends on: [TASK-FCSMB-AA-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-agent-admin/README.md
  - specs/full-core-smb-agent-admin/conversation-capture.md
  - specs/full-core-smb-agent-admin/sprints/01-agent-admin-full-core-sprint.md
  - specs/full-core-smb-agent-admin/backlog/01-agent-admin-full-core-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
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
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - updated specs/full-core-smb-agent-admin/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend/runtime validation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered Agent Admin, managed-agent, seed, tool-boundary, workstream, frontend surface, and test boundaries
  - `rg -n "Agent Admin|AgentAdminAgent|AgentDefinition|ToolPermissionBoundary|AgentSkillManifest|AgentReferenceManifest|model ref|behavior change|proposal|activate|rollback|seed|provider|system_message|AgentWorkTrace|PromptAssemblyTrace|no direct mutation" specs/full-core-smb-agent-admin`
- done criteria:
  - backend/frontend source-edit tasks can run without guessing source paths or validation commands
  - implementation map distinguishes deterministic lifecycle/authorization/redaction responsibilities from model-backed guidance/worker responsibilities
  - scope remains SMB Agent Admin, not enterprise marketplace/model-procurement/plugin management
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map agent admin full core`

### TASK-FCSMB-AA-99-001: Verify Agent Admin full-core readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-agent-admin/tasks/99-verification/01-verify-agent-admin-readiness.md
- depends on:
  - TASK-FCSMB-AA-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-agent-admin/README.md
  - specs/full-core-smb-agent-admin/conversation-capture.md
  - specs/full-core-smb-agent-admin/pending-tasks.md
  - specs/full-core-smb-agent-admin/sprints/*.md
  - specs/full-core-smb-agent-admin/backlog/*.md
  - specs/full-core-smb-agent-admin/tasks/**/*.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-agent-admin/pending-tasks.md
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
  - commit message: `full-core-smb: verify agent admin readiness`
