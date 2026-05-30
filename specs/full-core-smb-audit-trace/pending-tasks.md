# Pending Tasks: Full-Core SMB Audit/Trace

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

### TASK-FCSMB-AT-00-001: Create Audit/Trace full-core queue

- status: done
- source: user approved next full-core SMB workstream after Agent Admin completion
- task brief: specs/full-core-smb-audit-trace/tasks/00-planning/00-create-audit-trace-queue.md
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
  - specs/audit-trace-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-audit-trace/README.md
  - specs/full-core-smb-audit-trace/conversation-capture.md
  - specs/full-core-smb-audit-trace/pending-tasks.md
  - specs/full-core-smb-audit-trace/sprints/*.md
  - specs/full-core-smb-audit-trace/backlog/*.md
  - specs/full-core-smb-audit-trace/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add audit trace queue`

### TASK-FCSMB-AT-01-001: Define Audit/Trace vertical slice contracts and implementation map

- status: pending
- source: specs/full-core-smb-audit-trace/backlog/01-audit-trace-full-core-backlog.md
- task brief: specs/full-core-smb-audit-trace/tasks/01-audit-trace/01-define-audit-trace-implementation-map.md
- depends on: [TASK-FCSMB-AT-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-audit-trace/README.md
  - specs/full-core-smb-audit-trace/conversation-capture.md
  - specs/full-core-smb-audit-trace/sprints/01-audit-trace-full-core-sprint.md
  - specs/full-core-smb-audit-trace/backlog/01-audit-trace-full-core-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/audit-trace-workstream-v0/workstream-contract.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
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
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
  - updated specs/full-core-smb-audit-trace/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend/runtime validation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered Audit/Trace, trace repository/projection, workstream, agent/worker, frontend surface, and test boundaries
  - `rg -n "Audit/Trace|AuditTraceAgent|audit\.trace|trace dashboard|trace search|timeline|correlation|evidence|redacted|provider|tool|model|worker|system_message|AgentWorkTrace|PromptAssemblyTrace|no secret|tenant" specs/full-core-smb-audit-trace`
- done criteria:
  - backend/frontend source-edit tasks can run without guessing source paths or validation commands
  - implementation map distinguishes deterministic trace authorization/redaction/correlation responsibilities from model-backed explanation/worker responsibilities
  - scope remains SMB investigation, not enterprise SIEM/e-discovery/compliance-suite scope
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map audit trace full core`

### TASK-FCSMB-AT-99-001: Verify Audit/Trace full-core readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-audit-trace/tasks/99-verification/01-verify-audit-trace-readiness.md
- depends on:
  - TASK-FCSMB-AT-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-audit-trace/README.md
  - specs/full-core-smb-audit-trace/conversation-capture.md
  - specs/full-core-smb-audit-trace/pending-tasks.md
  - specs/full-core-smb-audit-trace/sprints/*.md
  - specs/full-core-smb-audit-trace/backlog/*.md
  - specs/full-core-smb-audit-trace/tasks/**/*.md
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/audit-trace-workstream-v0/workstream-contract.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-audit-trace/pending-tasks.md
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
  - commit message: `full-core-smb: verify audit trace readiness`
