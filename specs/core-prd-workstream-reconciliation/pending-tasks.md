# Pending Tasks: Core PRD Workstream Reconciliation

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `core-prd-reconcile: <short task title>`.

## Tasks

### TASK-CPR-00-001: Create core PRD reconciliation planning scaffold

- status: done
- source: user approved recommendation to reconcile `docs/examples/core-ai-first-saas-input/` PRDs with completed five-core workstreams
- task brief: specs/core-prd-workstream-reconciliation/tasks/00-planning/00-create-core-prd-reconciliation-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/examples/core-ai-first-saas-input/README.md
  - docs/skills-pack-user-guide.md
  - specs/five-core-workstreams-v0-plan/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/core-prd-workstream-reconciliation/README.md
  - specs/core-prd-workstream-reconciliation/conversation-capture.md
  - specs/core-prd-workstream-reconciliation/pending-tasks.md
  - specs/core-prd-workstream-reconciliation/sprints/*.md
  - specs/core-prd-workstream-reconciliation/backlog/*.md
  - specs/core-prd-workstream-reconciliation/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - planning scaffold is committed
- notes:
  - commit message: `core-prd-reconcile: add reconciliation queue`

### TASK-CPR-01-001: Create PRD-to-workstream traceability report

- status: pending
- source: specs/core-prd-workstream-reconciliation/backlog/01-core-prd-reconciliation-backlog.md
- task brief: specs/core-prd-workstream-reconciliation/tasks/01-reconciliation/01-create-prd-workstream-traceability.md
- depends on: [TASK-CPR-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-prd-workstream-reconciliation/README.md
  - specs/core-prd-workstream-reconciliation/conversation-capture.md
  - docs/examples/core-ai-first-saas-input/README.md
  - docs/examples/core-ai-first-saas-input/00-document-development-process-context.md
  - docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md
  - docs/examples/core-ai-first-saas-input/03-module-auth-app-access-prd.md
  - docs/examples/core-ai-first-saas-input/03a-module-agent-workstream-runtime-bootstrap-prd.md
  - docs/examples/core-ai-first-saas-input/04-module-user-admin-prd.md
  - docs/examples/core-ai-first-saas-input/05-module-agent-definition-prd.md
  - docs/examples/core-ai-first-saas-input/06-module-prompt-governance-prd.md
  - docs/examples/core-ai-first-saas-input/07-module-skill-governance-prd.md
  - docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md
  - docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/my-account-workstream-v0/workstream-contract.md
  - specs/my-account-workstream-v0/capability-inventory.md
  - specs/user-admin-workstream-v0/workstream-contract.md
  - specs/user-admin-workstream-v0/capability-inventory.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
  - specs/audit-trace-workstream-v0/workstream-contract.md
  - specs/audit-trace-workstream-v0/capability-inventory.md
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/governance-policy-workstream-v0/capability-inventory.md
- skills:
  - none; repository reconciliation task
- expected outputs:
  - specs/core-prd-workstream-reconciliation/prd-to-workstream-traceability.md
  - specs/core-prd-workstream-reconciliation/reconciliation-findings.md
- required checks:
  - `git diff --check`
  - `rg -n "10-canonical-core-app-prd|04-module-user-admin|05-module-agent-definition|covered|partial|deferred|superseded|gap" specs/core-prd-workstream-reconciliation`
- done criteria:
  - every core PRD input file is mapped to completed workstream artifacts or classified as deferred/superseded/gap
  - findings distinguish v0 coverage from full-core scope
  - task changes and queue update are committed
- notes:
  - commit message: `core-prd-reconcile: map prds to workstreams`

### TASK-CPR-01-002: Apply reconciliation follow-up updates

- status: pending
- source: specs/core-prd-workstream-reconciliation/reconciliation-findings.md
- task brief: specs/core-prd-workstream-reconciliation/tasks/01-reconciliation/02-apply-reconciliation-followups.md
- depends on: [TASK-CPR-01-001]
- required reads:
  - specs/core-prd-workstream-reconciliation/prd-to-workstream-traceability.md
  - specs/core-prd-workstream-reconciliation/reconciliation-findings.md
  - docs/examples/core-ai-first-saas-input/README.md
  - docs/examples/ai-first-saas-core-app-domain/README.md
  - docs/skills-pack-user-guide.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
- skills:
  - none; repository docs/planning task
- expected outputs:
  - documentation updates, follow-up task appendices, or explicit no-op reconciliation notes
- required checks:
  - `git diff --check`
  - targeted `rg` checks named by the findings
- done criteria:
  - source-of-truth ambiguity or actionable gaps from the findings are resolved or converted into bounded follow-up tasks
  - if no changes are needed, the task records why and updates the queue accordingly
  - task changes and queue update are committed
- notes:
  - commit message: `core-prd-reconcile: apply followups`

### TASK-CPR-99-001: Verify core PRD reconciliation completion

- status: pending
- source: mini-project verification loop
- task brief: specs/core-prd-workstream-reconciliation/tasks/99-verification/01-verify-core-prd-reconciliation.md
- depends on:
  - TASK-CPR-01-001
  - TASK-CPR-01-002
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-prd-workstream-reconciliation/README.md
  - specs/core-prd-workstream-reconciliation/conversation-capture.md
  - specs/core-prd-workstream-reconciliation/pending-tasks.md
  - specs/core-prd-workstream-reconciliation/sprints/*.md
  - specs/core-prd-workstream-reconciliation/backlog/*.md
  - specs/core-prd-workstream-reconciliation/tasks/**/*.md
  - specs/core-prd-workstream-reconciliation/prd-to-workstream-traceability.md
  - specs/core-prd-workstream-reconciliation/reconciliation-findings.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/core-prd-workstream-reconciliation/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - any checks needed to validate applied follow-ups
- done criteria:
  - traceability and findings are compared against mini-project done state
  - old PRD relationship to completed workstreams and newer input path is clear
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - commit message: `core-prd-reconcile: verify reconciliation completion`
