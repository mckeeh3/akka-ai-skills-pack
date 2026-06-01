# Pending Tasks: Agent Admin Prompt-Risk AutonomousAgent

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `agent-admin-risk: <short task title>`.

## Tasks

### TASK-AAPR-00-001: Create Agent Admin prompt-risk AutonomousAgent queue

- status: done
- source: current conversation after User Admin Access Review AutonomousAgent completion; user requested suggested Agent Admin Prompt-Risk AutonomousAgent mini-project
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/00-planning/00-create-prompt-risk-agent-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/agent-admin-prompt-risk-autonomous-agent/README.md
  - specs/agent-admin-prompt-risk-autonomous-agent/conversation-capture.md
  - specs/agent-admin-prompt-risk-autonomous-agent/pending-tasks.md
  - specs/agent-admin-prompt-risk-autonomous-agent/sprints/01-prompt-risk-agent-sprint.md
  - specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
  - specs/agent-admin-prompt-risk-autonomous-agent/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `agent-admin-risk: add autonomous agent queue`

### TASK-AAPR-01-001: Define prompt-risk AutonomousAgent contract

- status: done
- source: specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/01-contracts/01-define-prompt-risk-contract.md
- depends on:
  - TASK-AAPR-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/agent-admin-prompt-risk-autonomous-agent/README.md
  - specs/agent-admin-prompt-risk-autonomous-agent/conversation-capture.md
  - specs/agent-admin-prompt-risk-autonomous-agent/sprints/01-prompt-risk-agent-sprint.md
  - specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
  - specs/agent-admin-prompt-risk-autonomous-agent/tasks/01-contracts/01-define-prompt-risk-contract.md
  - specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md
  - specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md
  - specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md
- skills:
  - akka-autonomous-agents
  - akka-agent-behavior-profiles
- expected outputs:
  - specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract covers AutonomousAgent, prompt/skill/reference/model/tool-boundary risk, provider fail-closed, v3 events, attention, surfaces, and no fake success
- done criteria:
  - runtime and surface tasks can proceed without guessing
  - task changes and queue update are committed
- notes:
  - commit message: `agent-admin-risk: define prompt risk contract`

### TASK-AAPR-02-001: Implement prompt-risk AutonomousAgent runtime

- status: done
- source: specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/02-runtime/01-implement-prompt-risk-runtime.md
- depends on:
  - TASK-AAPR-01-001
- required reads:
  - mini-project README/conversation/sprint/backlog/queue entry and task brief
  - contract from TASK-AAPR-01-001
  - User Admin AutonomousAgent implementation pattern
  - starter Agent Admin behavior proposal/governance files
- skills:
  - akka-autonomous-agents
- expected outputs:
  - backend prompt-risk AutonomousAgent runtime or explicit blocker
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - focused `rg` for fail-closed/no fake success guardrails
- done criteria:
  - backend runtime path exists or blocker is recorded
  - task changes and queue update are committed
- notes:
  - commit message: `agent-admin-risk: implement runtime`

### TASK-AAPR-03-001: Wire prompt-risk events, attention, and surfaces

- status: done
- source: specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/03-surfaces/01-wire-events-attention-surfaces.md
- depends on:
  - TASK-AAPR-02-001
- required reads:
  - contract and runtime implementation from prior tasks
  - v3 event backbone and attention implementation
  - Agent Admin frontend/backend surfaces
- skills:
  - web UI/frontend skills as needed
- expected outputs:
  - v3 events, attention mappings, Agent Admin risk review surfaces, tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend tests
  - frontend tests/typecheck/build if frontend changes
- done criteria:
  - prompt-risk states are visible through backend-derived events, attention, and surfaces
  - task changes and queue update are committed
- notes:
  - commit message: `agent-admin-risk: wire events attention surfaces`

### TASK-AAPR-04-001: Run prompt-risk AutonomousAgent validation

- status: done
- source: specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/04-validation/01-run-prompt-risk-validation.md
- depends on:
  - TASK-AAPR-03-001
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
  - commit message: `agent-admin-risk: validate runtime path`

### TASK-AAPR-05-001: Update prompt-risk AutonomousAgent docs

- status: pending
- source: specs/agent-admin-prompt-risk-autonomous-agent/backlog/01-prompt-risk-agent-build-backlog.md
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/05-docs/01-update-prompt-risk-docs.md
- depends on:
  - TASK-AAPR-04-001
- required reads:
  - mini-project docs and validation artifacts
- skills:
  - none; docs task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish implemented vertical, future workers, provider fail-closed, and no fake success
- done criteria:
  - future agents understand prompt-risk worker status and pattern
  - task changes and queue update are committed
- notes:
  - commit message: `agent-admin-risk: update docs`

### TASK-AAPR-99-001: Verify Agent Admin prompt-risk AutonomousAgent

- status: pending
- source: mini-project verification loop
- task brief: specs/agent-admin-prompt-risk-autonomous-agent/tasks/99-verification/01-verify-prompt-risk-agent.md
- depends on:
  - TASK-AAPR-01-001
  - TASK-AAPR-02-001
  - TASK-AAPR-03-001
  - TASK-AAPR-04-001
  - TASK-AAPR-05-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for runtime/fail-closed/events/attention
  - frontend tests/typecheck/build if surfaces changed
  - focused `rg` for AutonomousAgent, prompt-risk, fail-closed, no fake success, events, attention, and surfaces
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `agent-admin-risk: verify completion`
