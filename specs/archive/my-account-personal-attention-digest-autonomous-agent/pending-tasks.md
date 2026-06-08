# Pending Tasks: My Account Personal Attention Digest AutonomousAgent

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `my-account-digest-agent: <short task title>`.

## Tasks

### TASK-MAPAD-00-001: Create My Account personal attention digest queue

- status: done
- source: current conversation after AutonomousAgent fullstack regression readiness; user accepted recommended My Account Personal Attention Digest AutonomousAgent mini-project
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/00-planning/00-create-personal-attention-digest-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/autonomous-agent-fullstack-regression-readiness/integrated-readiness-handoff.md
  - docs/autonomous-agent-worker-runtime-pattern.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/my-account-personal-attention-digest-autonomous-agent/README.md
  - specs/my-account-personal-attention-digest-autonomous-agent/conversation-capture.md
  - specs/my-account-personal-attention-digest-autonomous-agent/pending-tasks.md
  - specs/my-account-personal-attention-digest-autonomous-agent/sprints/01-personal-attention-digest-sprint.md
  - specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
  - specs/my-account-personal-attention-digest-autonomous-agent/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `my-account-digest-agent: add autonomous agent queue`

### TASK-MAPAD-01-001: Define personal attention digest AutonomousAgent contract

- status: done
- source: specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/01-contracts/01-define-personal-attention-digest-contract.md
- depends on:
  - TASK-MAPAD-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/my-account-personal-attention-digest-autonomous-agent/README.md
  - specs/my-account-personal-attention-digest-autonomous-agent/conversation-capture.md
  - specs/my-account-personal-attention-digest-autonomous-agent/sprints/01-personal-attention-digest-sprint.md
  - specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
  - specs/my-account-personal-attention-digest-autonomous-agent/tasks/01-contracts/01-define-personal-attention-digest-contract.md
  - docs/autonomous-agent-worker-runtime-pattern.md
  - specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md
  - specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md
- skills:
  - akka-autonomous-agents
- expected outputs:
  - specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-autonomous-agent-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract covers AutonomousAgent, personal attention evidence/redaction, provider fail-closed, v3 events, attention, My Account surfaces, and no fake success
- done criteria:
  - runtime and surface tasks can proceed without guessing
  - task changes and queue update are committed
- notes:
  - commit message: `my-account-digest-agent: define contract`

### TASK-MAPAD-02-001: Implement personal attention digest runtime

- status: done
- source: specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/02-runtime/01-implement-personal-attention-digest-runtime.md
- depends on:
  - TASK-MAPAD-01-001
- required reads:
  - contract from TASK-MAPAD-01-001
  - existing AutonomousAgent worker pattern examples
  - starter My Account, attention, event backbone, and agent runtime files
- skills:
  - akka-autonomous-agents
- expected outputs:
  - backend My Account digest AutonomousAgent runtime or explicit blocker
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
  - commit message: `my-account-digest-agent: implement runtime`

### TASK-MAPAD-03-001: Wire My Account digest events, attention, and surfaces

- status: done
- source: specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/03-surfaces/01-wire-events-attention-surfaces.md
- depends on:
  - TASK-MAPAD-02-001
- required reads:
  - contract and runtime implementation from prior tasks
  - v3 event backbone and attention implementation
  - My Account frontend/backend surfaces
- skills:
  - web UI/frontend skills as needed
- expected outputs:
  - v3 events, attention mappings, My Account digest surfaces, tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend tests
  - frontend tests/typecheck/build if frontend changes
- done criteria:
  - digest states are visible through backend-derived events, attention, and surfaces
  - task changes and queue update are committed
- notes:
  - commit message: `my-account-digest-agent: wire events attention surfaces`

### TASK-MAPAD-04-001: Run personal attention digest validation

- status: done
- source: specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/04-validation/01-run-personal-attention-digest-validation.md
- depends on:
  - TASK-MAPAD-03-001
- required reads:
  - mini-project README/conversation/sprint/backlog/queue entry and task brief
  - contract and implementation notes from prior tasks
- skills:
  - none; validation task
- expected outputs:
  - specs/my-account-personal-attention-digest-autonomous-agent/validation/01-personal-attention-digest-validation.md
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
  - commit message: `my-account-digest-agent: validate runtime path`

### TASK-MAPAD-05-001: Update personal attention digest AutonomousAgent docs

- status: done
- source: specs/my-account-personal-attention-digest-autonomous-agent/backlog/01-personal-attention-digest-build-backlog.md
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/05-docs/01-update-personal-attention-digest-docs.md
- depends on:
  - TASK-MAPAD-04-001
- required reads:
  - mini-project docs and validation artifacts
- skills:
  - none; docs task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish implemented vertical, future notification platform, provider fail-closed, redaction, and no fake success
- done criteria:
  - future agents understand personal attention digest worker status and pattern
  - task changes and queue update are committed
- notes:
  - commit message: `my-account-digest-agent: update docs`

### TASK-MAPAD-99-001: Verify My Account personal attention digest AutonomousAgent

- status: done
- source: mini-project verification loop
- task brief: specs/my-account-personal-attention-digest-autonomous-agent/tasks/99-verification/01-verify-personal-attention-digest-agent.md
- depends on:
  - TASK-MAPAD-01-001
  - TASK-MAPAD-02-001
  - TASK-MAPAD-03-001
  - TASK-MAPAD-04-001
  - TASK-MAPAD-05-001
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
  - focused `rg` for AutonomousAgent, personal attention digest, fail-closed, redaction, no fake success, events, attention, and surfaces
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `my-account-digest-agent: verify completion`
