# Pending Tasks: Full-Core SMB Governance/Policy

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

### TASK-FCSMB-GP-00-001: Create Governance/Policy full-core queue

- status: done
- source: user approved next full-core SMB workstream after Audit/Trace completion
- task brief: specs/full-core-smb-governance-policy/tasks/00-planning/00-create-governance-policy-queue.md
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
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-governance-policy/README.md
  - specs/full-core-smb-governance-policy/conversation-capture.md
  - specs/full-core-smb-governance-policy/pending-tasks.md
  - specs/full-core-smb-governance-policy/sprints/*.md
  - specs/full-core-smb-governance-policy/backlog/*.md
  - specs/full-core-smb-governance-policy/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add governance policy queue`

### TASK-FCSMB-GP-01-001: Define Governance/Policy vertical slice contracts and implementation map

- status: pending
- source: specs/full-core-smb-governance-policy/backlog/01-governance-policy-full-core-backlog.md
- task brief: specs/full-core-smb-governance-policy/tasks/01-governance-policy/01-define-governance-policy-implementation-map.md
- depends on: [TASK-FCSMB-GP-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-governance-policy/README.md
  - specs/full-core-smb-governance-policy/conversation-capture.md
  - specs/full-core-smb-governance-policy/sprints/01-governance-policy-full-core-sprint.md
  - specs/full-core-smb-governance-policy/backlog/01-governance-policy-full-core-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
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
  - specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
  - updated specs/full-core-smb-governance-policy/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend/runtime validation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered Governance/Policy, policy/proposal, simulation, decision, workstream, agent/worker, frontend surface, and test boundaries
  - `rg -n "Governance/Policy|GovernancePolicyAgent|governance\.policy|policy dashboard|proposal|simulate|approve|reject|activate|rollback|exception|decision|AgentWorkTrace|PromptAssemblyTrace|ToolPermissionBoundary|provider|system_message|tenant|no direct mutation" specs/full-core-smb-governance-policy`
- done criteria:
  - backend/frontend source-edit tasks can run without guessing source paths or validation commands
  - implementation map distinguishes deterministic policy/proposal/simulation/decision responsibilities from model-backed guidance/worker responsibilities
  - scope remains SMB governance, not enterprise compliance/policy-as-code/governance-office scope
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map governance policy full core`

### TASK-FCSMB-GP-99-001: Verify Governance/Policy full-core readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-governance-policy/tasks/99-verification/01-verify-governance-policy-readiness.md
- depends on:
  - TASK-FCSMB-GP-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-governance-policy/README.md
  - specs/full-core-smb-governance-policy/conversation-capture.md
  - specs/full-core-smb-governance-policy/pending-tasks.md
  - specs/full-core-smb-governance-policy/sprints/*.md
  - specs/full-core-smb-governance-policy/backlog/*.md
  - specs/full-core-smb-governance-policy/tasks/**/*.md
  - specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/governance-policy-workstream-v0/workstream-contract.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-governance-policy/pending-tasks.md
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
  - commit message: `full-core-smb: verify governance policy readiness`
