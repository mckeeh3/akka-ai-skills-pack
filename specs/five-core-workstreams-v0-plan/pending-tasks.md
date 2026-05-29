# Pending Tasks: Five Core Workstreams v0 Scope Plan

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `five-core-plan: <short task title>`.

## Tasks

### TASK-FCPLAN-00-001: Create five-core workstream mini-project series

- status: done
- source: user request to create a series of mini-projects: one shared plan and one per five-core workstream
- task brief: specs/five-core-workstreams-v0-plan/tasks/00-planning/00-create-five-core-series.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/five-core-workstreams-v0-plan/**
  - specs/my-account-workstream-v0/**
  - specs/user-admin-workstream-v0/**
  - specs/agent-admin-workstream-v0/**
  - specs/audit-trace-workstream-v0/**
  - specs/governance-policy-workstream-v0/**
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project series exists with README, conversation capture, sprint, backlog, task brief, pending queue, and terminal verification per project
  - planning scaffold is committed
- notes:
  - commit message: `five-core-plan: add workstream v0 planning series`

### TASK-FCPLAN-01-001: Define shared five-core v0 contract and dependency map

- status: done
- source: specs/five-core-workstreams-v0-plan/backlog/01-five-core-v0-scope-backlog.md
- task brief: specs/five-core-workstreams-v0-plan/tasks/01-scope/01-define-shared-contract.md
- depends on: [TASK-FCPLAN-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/README.md
  - specs/five-core-workstreams-v0-plan/conversation-capture.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-component-selection-guide.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
- required checks:
  - `git diff --check`
  - `rg -n "request/response|AutonomousAgent|deterministic|runtime validation|AuthContext|ToolPermissionBoundary" specs/five-core-workstreams-v0-plan`
- done criteria:
  - shared acceptance contract covers runtime, auth, capability, trace, UI, model-provider, and agent-type selection rules
  - dependency map identifies workstream ordering and cross-workstream prerequisites
  - task changes and queue update are committed
- notes:
  - commit message: `five-core-plan: define shared v0 contract`
  - completed checks: `git diff --check`; `rg -n "request/response|AutonomousAgent|deterministic|runtime validation|AuthContext|ToolPermissionBoundary" specs/five-core-workstreams-v0-plan`

### TASK-FCPLAN-01-002: Align sibling workstream queues with shared contract

- status: pending
- source: specs/five-core-workstreams-v0-plan/backlog/01-five-core-v0-scope-backlog.md
- task brief: specs/five-core-workstreams-v0-plan/tasks/01-scope/02-align-workstream-queues.md
- depends on: [TASK-FCPLAN-01-001]
- required reads:
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/my-account-workstream-v0/pending-tasks.md
  - specs/user-admin-workstream-v0/pending-tasks.md
  - specs/agent-admin-workstream-v0/pending-tasks.md
  - specs/audit-trace-workstream-v0/pending-tasks.md
  - specs/governance-policy-workstream-v0/pending-tasks.md
- skills:
  - none; repository planning task
- expected outputs:
  - updated sibling workstream README/backlog/task briefs/pending-tasks where needed
- required checks:
  - `git diff --check`
  - `rg -n "shared-five-core-v0-contract|TASK-FCPLAN|runtime validation|terminal verification" specs/*workstream-v0 specs/five-core-workstreams-v0-plan`
- done criteria:
  - all five sibling queues inherit or reference the shared contract
  - each first implementation task is bounded and not blocked by missing shared decisions
  - task changes and queue update are committed
- notes:
  - commit message: `five-core-plan: align workstream queues`

### TASK-FCPLAN-99-001: Verify five-core v0 planning series readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/five-core-workstreams-v0-plan/tasks/99-verification/01-verify-five-core-plan.md
- depends on:
  - TASK-FCPLAN-01-001
  - TASK-FCPLAN-01-002
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/README.md
  - specs/five-core-workstreams-v0-plan/conversation-capture.md
  - specs/five-core-workstreams-v0-plan/pending-tasks.md
  - specs/five-core-workstreams-v0-plan/sprints/*.md
  - specs/five-core-workstreams-v0-plan/backlog/*.md
  - specs/five-core-workstreams-v0-plan/tasks/**/*.md
  - specs/my-account-workstream-v0/pending-tasks.md
  - specs/user-admin-workstream-v0/pending-tasks.md
  - specs/agent-admin-workstream-v0/pending-tasks.md
  - specs/audit-trace-workstream-v0/pending-tasks.md
  - specs/governance-policy-workstream-v0/pending-tasks.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/five-core-workstreams-v0-plan/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - `rg -n "status: pending|TASK-FCPLAN-99|Verify" specs/five-core-workstreams-v0-plan/pending-tasks.md`
- done criteria:
  - planning goals have been compared against completed work
  - sibling queues have been reviewed for first-runnable-task readiness
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - commit message: `five-core-plan: verify planning readiness`
