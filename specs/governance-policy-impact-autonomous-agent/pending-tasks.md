# Pending Tasks: Governance/Policy Impact AutonomousAgent

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `governance-impact-agent: <short task title>`.

## Tasks

### TASK-GPIA-00-001: Create Governance/Policy impact AutonomousAgent queue

- status: done
- source: current conversation after Audit/Trace Summary AutonomousAgent completion; user requested Audit/Trace README cleanup plus Governance/Policy Impact AutonomousAgent mini-project
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/00-planning/00-create-governance-impact-agent-queue.md
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
  - specs/governance-policy-impact-autonomous-agent/README.md
  - specs/governance-policy-impact-autonomous-agent/conversation-capture.md
  - specs/governance-policy-impact-autonomous-agent/pending-tasks.md
  - specs/governance-policy-impact-autonomous-agent/sprints/01-governance-impact-agent-sprint.md
  - specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
  - specs/governance-policy-impact-autonomous-agent/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `governance-impact-agent: add autonomous agent queue`

### TASK-GPIA-01-001: Define Governance/Policy impact AutonomousAgent contract

- status: done
- source: specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/01-contracts/01-define-governance-impact-contract.md
- depends on:
  - TASK-GPIA-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/governance-policy-impact-autonomous-agent/README.md
  - specs/governance-policy-impact-autonomous-agent/conversation-capture.md
  - specs/governance-policy-impact-autonomous-agent/sprints/01-governance-impact-agent-sprint.md
  - specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
  - specs/governance-policy-impact-autonomous-agent/tasks/01-contracts/01-define-governance-impact-contract.md
  - docs/autonomous-agent-worker-runtime-pattern.md
  - specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md
  - specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md
- skills:
  - akka-autonomous-agents
- expected outputs:
  - specs/governance-policy-impact-autonomous-agent/governance-policy-impact-autonomous-agent-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract covers AutonomousAgent, policy impact evidence/redaction, provider fail-closed, v3 events, attention, surfaces, human approval, and no fake success
- done criteria:
  - runtime and surface tasks can proceed without guessing
  - task changes and queue update are committed
- notes:
  - commit message: `governance-impact-agent: define contract`

### TASK-GPIA-02-001: Implement Governance/Policy impact runtime

- status: pending
- source: specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/02-runtime/01-implement-governance-impact-runtime.md
- depends on:
  - TASK-GPIA-01-001
- required reads:
  - contract from TASK-GPIA-01-001
  - existing AutonomousAgent worker pattern examples
  - starter Governance/Policy, event backbone, attention, and agent runtime files
- skills:
  - akka-autonomous-agents
- expected outputs:
  - backend Governance/Policy impact AutonomousAgent runtime or explicit blocker
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
  - commit message: `governance-impact-agent: implement runtime`

### TASK-GPIA-03-001: Wire Governance/Policy events, attention, and surfaces

- status: pending
- source: specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/03-surfaces/01-wire-events-attention-surfaces.md
- depends on:
  - TASK-GPIA-02-001
- required reads:
  - contract and runtime implementation from prior tasks
  - v3 event backbone and attention implementation
  - Governance/Policy frontend/backend surfaces
- skills:
  - web UI/frontend skills as needed
- expected outputs:
  - v3 events, attention mappings, Governance/Policy impact surfaces, tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend tests
  - frontend tests/typecheck/build if frontend changes
- done criteria:
  - governance impact states are visible through backend-derived events, attention, and surfaces
  - task changes and queue update are committed
- notes:
  - commit message: `governance-impact-agent: wire events attention surfaces`

### TASK-GPIA-04-001: Run Governance/Policy impact validation

- status: pending
- source: specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/04-validation/01-run-governance-impact-validation.md
- depends on:
  - TASK-GPIA-03-001
- required reads:
  - mini-project README/conversation/sprint/backlog/queue entry and task brief
  - contract and implementation notes from prior tasks
- skills:
  - none; validation task
- expected outputs:
  - validation artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - frontend tests/typecheck/build
  - manual/local smoke notes or clear blocked reason
- done criteria:
  - validation evidence is captured and blockers are recorded or converted to tasks
  - task changes and queue update are committed
- notes:
  - commit message: `governance-impact-agent: validate runtime path`

### TASK-GPIA-05-001: Update Governance/Policy impact AutonomousAgent docs

- status: pending
- source: specs/governance-policy-impact-autonomous-agent/backlog/01-governance-impact-agent-build-backlog.md
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/05-docs/01-update-governance-impact-docs.md
- depends on:
  - TASK-GPIA-04-001
- required reads:
  - mini-project docs and validation artifacts
- skills:
  - none; docs task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish implemented vertical, future policy simulation platform, provider fail-closed, redaction, and no fake success
- done criteria:
  - future agents understand governance impact worker status and pattern
  - task changes and queue update are committed
- notes:
  - commit message: `governance-impact-agent: update docs`

### TASK-GPIA-99-001: Verify Governance/Policy impact AutonomousAgent

- status: pending
- source: mini-project verification loop
- task brief: specs/governance-policy-impact-autonomous-agent/tasks/99-verification/01-verify-governance-impact-agent.md
- depends on:
  - TASK-GPIA-01-001
  - TASK-GPIA-02-001
  - TASK-GPIA-03-001
  - TASK-GPIA-04-001
  - TASK-GPIA-05-001
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
  - focused `rg` for AutonomousAgent, governance impact, fail-closed, redaction, no fake success, events, attention, and surfaces
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `governance-impact-agent: verify completion`
