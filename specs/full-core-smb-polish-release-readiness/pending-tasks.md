# Pending Tasks: Full-Core SMB Polish and Release Readiness

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

### TASK-FCSMB-REL-00-001: Create polish release-readiness queue

- status: done
- source: user approved cross-workstream polish and release-readiness after core full-core queues completed
- task brief: specs/full-core-smb-polish-release-readiness/tasks/00-planning/00-create-polish-release-readiness-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-polish-release-readiness/README.md
  - specs/full-core-smb-polish-release-readiness/conversation-capture.md
  - specs/full-core-smb-polish-release-readiness/pending-tasks.md
  - specs/full-core-smb-polish-release-readiness/sprints/*.md
  - specs/full-core-smb-polish-release-readiness/backlog/*.md
  - specs/full-core-smb-polish-release-readiness/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add polish release readiness queue`

### TASK-FCSMB-REL-01-001: Define full-core SMB integrated release-readiness map

- status: pending
- source: specs/full-core-smb-polish-release-readiness/backlog/01-polish-release-readiness-backlog.md
- task brief: specs/full-core-smb-polish-release-readiness/tasks/01-release-readiness/01-define-integrated-release-readiness-map.md
- depends on: [TASK-FCSMB-REL-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-polish-release-readiness/README.md
  - specs/full-core-smb-polish-release-readiness/conversation-capture.md
  - specs/full-core-smb-polish-release-readiness/sprints/01-polish-release-readiness-sprint.md
  - specs/full-core-smb-polish-release-readiness/backlog/01-polish-release-readiness-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
  - specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
  - specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
  - specs/full-core-smb-my-account/my-account-implementation-map.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository release-readiness planning task
- expected outputs:
  - specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md
  - updated specs/full-core-smb-polish-release-readiness/pending-tasks.md with bounded validation/polish/doc tasks
  - task briefs for next validation/polish/doc tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered validation scripts, workstream source/test/docs boundaries, and release-handoff candidates
  - `rg -n "fullstack|visual|provider|fail-closed|system_message|trace|secret|hidden prompt|User Admin|Agent Admin|Audit/Trace|Governance/Policy|My Account|release" specs/full-core-smb-polish-release-readiness`
- done criteria:
  - integrated release-readiness tasks can run without guessing source paths or validation commands
  - map distinguishes release blockers from intentional deferrals/post-release recommendations
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map polish release readiness`

### TASK-FCSMB-REL-99-001: Verify full-core SMB release readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-polish-release-readiness/tasks/99-verification/01-verify-full-core-smb-release-readiness.md
- depends on:
  - TASK-FCSMB-REL-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-polish-release-readiness/README.md
  - specs/full-core-smb-polish-release-readiness/conversation-capture.md
  - specs/full-core-smb-polish-release-readiness/pending-tasks.md
  - specs/full-core-smb-polish-release-readiness/sprints/*.md
  - specs/full-core-smb-polish-release-readiness/backlog/*.md
  - specs/full-core-smb-polish-release-readiness/tasks/**/*.md
  - specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-polish-release-readiness/pending-tasks.md
  - release-readiness notes or appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate release-readiness done state
- done criteria:
  - mini-project goals have been compared against completed work
  - release recommendation is explicit
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify release readiness`
