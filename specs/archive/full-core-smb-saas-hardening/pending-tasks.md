# Pending Tasks: Full-Core SMB SaaS Hardening

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

### TASK-FCSMB-00-001: Create full-core SMB hardening planning scaffold

- status: done
- source: user request to begin full-core SMB hardening after five-core v0 release-readiness
- task brief: specs/full-core-smb-saas-hardening/tasks/00-planning/00-create-full-core-smb-hardening-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-component-selection-guide.md
  - templates/ai-first-saas-starter/README.md
  - specs/release-readiness-after-five-core-v0/release-handoff.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-saas-hardening/README.md
  - specs/full-core-smb-saas-hardening/conversation-capture.md
  - specs/full-core-smb-saas-hardening/pending-tasks.md
  - specs/full-core-smb-saas-hardening/sprints/*.md
  - specs/full-core-smb-saas-hardening/backlog/*.md
  - specs/full-core-smb-saas-hardening/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - umbrella mini-project has captured rationale, scope, sprint, backlog, task briefs, pending queue, and terminal verification
  - planning scaffold is committed
- notes:
  - commit message: `full-core-smb: add hardening program queue`

### TASK-FCSMB-01-001: Define SMB full-core baseline and visual UX standard

- status: done
- source: specs/full-core-smb-saas-hardening/backlog/01-full-core-smb-hardening-backlog.md
- task brief: specs/full-core-smb-saas-hardening/tasks/01-baseline/01-define-smb-baseline-ux-standard.md
- depends on: [TASK-FCSMB-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-saas-hardening/README.md
  - specs/full-core-smb-saas-hardening/conversation-capture.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/web-ui-style-guide.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
- required checks:
  - `git diff --check`
  - `rg -n "SMB|workstream|surface|dashboard|request/response|AutonomousAgent|deterministic|visual|runtime validation" specs/full-core-smb-saas-hardening`
- done criteria:
  - baseline defines SMB functional completeness without enterprise scope creep
  - visual/UX standard defines attractive AI-first dashboards and surfaces, not page-first CRUD
  - runtime/model/provider/auth/trace validation expectations are explicit
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: define baseline ux standard`
  - checks: `git diff --check`; `rg -n "SMB|workstream|surface|dashboard|request/response|AutonomousAgent|deterministic|visual|runtime validation" specs/full-core-smb-saas-hardening`

### TASK-FCSMB-01-002: Outline full-core workstream capabilities, surfaces, and agents

- status: done
- source: specs/full-core-smb-saas-hardening/backlog/01-full-core-smb-hardening-backlog.md
- task brief: specs/full-core-smb-saas-hardening/tasks/01-baseline/02-outline-workstream-capabilities.md
- depends on: [TASK-FCSMB-01-001]
- required reads:
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/my-account-workstream-v0/workstream-contract.md
  - specs/user-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/audit-trace-workstream-v0/workstream-contract.md
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/core-prd-workstream-reconciliation/prd-to-workstream-traceability.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- required checks:
  - `git diff --check`
  - `rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|capability|surface|dashboard|worker|AutonomousAgent|deterministic" specs/full-core-smb-saas-hardening`
- done criteria:
  - each core workstream has a full-core SMB capability/surface/agent outline
  - internal worker opportunities are concrete and lifecycle-justified
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: outline workstream capabilities`
  - checks: `git diff --check`; `rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|capability|surface|dashboard|worker|AutonomousAgent|deterministic" specs/full-core-smb-saas-hardening`

### TASK-FCSMB-01-003: Create first-wave child mini-project queues

- status: done
- source: specs/full-core-smb-saas-hardening/backlog/01-full-core-smb-hardening-backlog.md
- task brief: specs/full-core-smb-saas-hardening/tasks/02-child-projects/01-create-first-wave-child-queues.md
- depends on: [TASK-FCSMB-01-002]
- required reads:
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - docs/pending-task-queue.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - first-wave child mini-project directories under `specs/`
  - each child mini-project has README, conversation capture, sprint/backlog/task briefs, pending queue, and terminal verification
  - specs/full-core-smb-saas-hardening/wave-plan.md
- required checks:
  - `git diff --check`
  - targeted `rg` proving child queues exist and inherit the SMB baseline/UX/workstream outline
- done criteria:
  - first implementation wave is ready for one-child-project-at-a-time execution
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: create first wave queues`
  - checks: `git diff --check`; `rg -n "Full-Core SMB|SMB|workstream|surface|dashboard|request/response|AutonomousAgent|deterministic|visual|runtime validation|provider|trace" specs/full-core-smb-baseline-and-ux specs/full-core-smb-user-admin specs/full-core-smb-saas-hardening/wave-plan.md`

### TASK-FCSMB-99-001: Verify full-core SMB umbrella readiness

- status: done
- source: mini-project verification loop
- task brief: specs/full-core-smb-saas-hardening/tasks/99-verification/01-verify-full-core-smb-umbrella.md
- depends on:
  - TASK-FCSMB-01-001
  - TASK-FCSMB-01-002
  - TASK-FCSMB-01-003
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-saas-hardening/README.md
  - specs/full-core-smb-saas-hardening/conversation-capture.md
  - specs/full-core-smb-saas-hardening/pending-tasks.md
  - specs/full-core-smb-saas-hardening/sprints/*.md
  - specs/full-core-smb-saas-hardening/backlog/*.md
  - specs/full-core-smb-saas-hardening/tasks/**/*.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md
  - specs/full-core-smb-saas-hardening/workstream-full-core-outline.md
  - specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
  - specs/full-core-smb-saas-hardening/wave-plan.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-saas-hardening/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate child queues and baseline artifacts
- done criteria:
  - umbrella goals have been compared against completed work
  - child mini-project queues are reviewed for first-runnable-task readiness
  - if ready, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify umbrella readiness`
  - verification: umbrella done state satisfied; Wave 1 child queues exist for `specs/full-core-smb-baseline-and-ux/` and `specs/full-core-smb-user-admin/` with first runnable tasks, inherited SMB/UX/runtime standards, and terminal verification tasks.
  - checks: `git diff --check`; targeted `rg` over `specs/full-core-smb-baseline-and-ux`, `specs/full-core-smb-user-admin`, and `specs/full-core-smb-saas-hardening/wave-plan.md` returned 110 matches.
