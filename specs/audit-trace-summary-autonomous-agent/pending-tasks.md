# Pending Tasks: Audit/Trace Summary AutonomousAgent

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `audit-summary-agent: <short task title>`.

## Tasks

### TASK-ATSA-00-001: Create Audit/Trace summary AutonomousAgent queue

- status: done
- source: current conversation after AutonomousAgent worker pattern extraction; user accepted recommended Audit/Trace summary AutonomousAgent mini-project
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/00-planning/00-create-audit-summary-agent-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - docs/autonomous-agent-worker-runtime-pattern.md
  - specs/autonomous-agent-worker-pattern-extraction/examples-and-next-workers.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/audit-trace-summary-autonomous-agent/README.md
  - specs/audit-trace-summary-autonomous-agent/conversation-capture.md
  - specs/audit-trace-summary-autonomous-agent/pending-tasks.md
  - specs/audit-trace-summary-autonomous-agent/sprints/01-audit-summary-agent-sprint.md
  - specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
  - specs/audit-trace-summary-autonomous-agent/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `audit-summary-agent: add autonomous agent queue`

### TASK-ATSA-01-001: Define Audit/Trace summary AutonomousAgent contract

- status: done
- source: specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/01-contracts/01-define-audit-summary-contract.md
- depends on:
  - TASK-ATSA-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/audit-trace-summary-autonomous-agent/README.md
  - specs/audit-trace-summary-autonomous-agent/conversation-capture.md
  - specs/audit-trace-summary-autonomous-agent/sprints/01-audit-summary-agent-sprint.md
  - specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
  - specs/audit-trace-summary-autonomous-agent/tasks/01-contracts/01-define-audit-summary-contract.md
  - docs/autonomous-agent-worker-runtime-pattern.md
  - specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md
  - specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md
- skills:
  - akka-autonomous-agents
- expected outputs:
  - specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract covers AutonomousAgent, audit trace evidence/redaction, provider fail-closed, v3 events, attention, surfaces, and no fake success
- done criteria:
  - runtime and surface tasks can proceed without guessing
  - task changes and queue update are committed
- notes:
  - commit message: `audit-summary-agent: define contract`

### TASK-ATSA-02-001: Implement Audit/Trace summary runtime

- status: done
- source: specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/02-runtime/01-implement-audit-summary-runtime.md
- depends on:
  - TASK-ATSA-01-001
- required reads:
  - contract from TASK-ATSA-01-001
  - existing AutonomousAgent worker pattern examples
  - starter Audit/Trace, event backbone, attention, and agent runtime files
- skills:
  - akka-autonomous-agents
- expected outputs:
  - backend Audit/Trace summary AutonomousAgent runtime or explicit blocker
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - focused `rg` for fail-closed/no fake success/redaction guardrails
- done criteria:
  - backend runtime path exists or blocker is recorded
  - task changes and queue update are committed
- notes:
  - commit message: `audit-summary-agent: implement runtime`

### TASK-ATSA-03-001: Wire Audit/Trace events, attention, and surfaces

- status: done
- source: specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/03-surfaces/01-wire-events-attention-surfaces.md
- depends on:
  - TASK-ATSA-02-001
- required reads:
  - contract and runtime implementation from prior tasks
  - v3 event backbone and attention implementation
  - Audit/Trace frontend/backend surfaces
- skills:
  - web UI/frontend skills as needed
- expected outputs:
  - v3 events, attention mappings, Audit/Trace summary surfaces, tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend tests
  - frontend tests/typecheck/build if frontend changes
- done criteria:
  - audit summary states are visible through backend-derived events, attention, and surfaces
  - task changes and queue update are committed
- notes:
  - commit message: `audit-summary-agent: wire events attention surfaces`

### TASK-ATSA-04-001: Run Audit/Trace summary validation

- status: done
- source: specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/04-validation/01-run-audit-summary-validation.md
- depends on:
  - TASK-ATSA-03-001
- required reads:
  - mini-project README/conversation/sprint/backlog/queue entry and task brief
  - contract and implementation notes from prior tasks
- skills:
  - none; validation task
- expected outputs:
  - specs/audit-trace-summary-autonomous-agent/validation/01-runtime-path-validation.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - frontend tests/typecheck/build
  - manual/local smoke notes or clear blocked reason
- done criteria:
  - validation evidence is captured and blockers are recorded or converted to tasks
  - task changes and queue update are committed
- validation notes:
  - backend Maven tests passed; frontend typecheck/build passed; targeted Audit/Trace frontend contract test passed
  - full frontend suite is blocked by unrelated User Admin expertise contract failure
  - current checkout has Audit/Trace summary frontend blocked/review surface fixtures but no backend `AuditTraceSummaryAutonomousAgent` runtime class
- notes:
  - commit message: `audit-summary-agent: validate runtime path`

### TASK-ATSA-05-001: Update Audit/Trace summary AutonomousAgent docs

- status: done
- source: specs/audit-trace-summary-autonomous-agent/backlog/01-audit-summary-agent-build-backlog.md
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/05-docs/01-update-audit-summary-docs.md
- depends on:
  - TASK-ATSA-04-001
- required reads:
  - mini-project docs and validation artifacts
- skills:
  - none; docs task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish implemented vertical, future digest platform, provider fail-closed, redaction, and no fake success
- done criteria:
  - future agents understand audit summary worker status and pattern
  - task changes and queue update are committed
- notes:
  - added `audit-trace-summary-handoff.md` and updated README/example handoff to avoid overclaiming backend runtime completion
  - checks: `git diff --check`; focused `rg` for implemented vertical, future digest platform, provider fail-closed, redaction, and no fake success
  - commit message: `audit-summary-agent: update docs`

### TASK-ATSA-99-001: Verify Audit/Trace summary AutonomousAgent

- status: pending
- source: mini-project verification loop
- task brief: specs/audit-trace-summary-autonomous-agent/tasks/99-verification/01-verify-audit-summary-agent.md
- depends on:
  - TASK-ATSA-01-001
  - TASK-ATSA-02-001
  - TASK-ATSA-03-001
  - TASK-ATSA-04-001
  - TASK-ATSA-05-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for runtime/fail-closed/events/attention/redaction
  - frontend tests/typecheck/build if surfaces changed
  - focused `rg` for AutonomousAgent, audit summary, fail-closed, redaction, no fake success, events, attention, and surfaces
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `audit-summary-agent: verify completion`
