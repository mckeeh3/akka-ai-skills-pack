# Pending Tasks: AutonomousAgent Worker Pattern Extraction

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `autonomous-agent-pattern: <short task title>`.

## Tasks

### TASK-AAWPE-00-001: Create worker pattern extraction queue

- status: done
- source: current conversation after two AutonomousAgent verticals completed; user accepted recommended pattern extraction mini-project
- task brief: specs/autonomous-agent-worker-pattern-extraction/tasks/00-planning/00-create-worker-pattern-extraction-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/autonomous-agent-worker-pattern-extraction/README.md
  - specs/autonomous-agent-worker-pattern-extraction/conversation-capture.md
  - specs/autonomous-agent-worker-pattern-extraction/pending-tasks.md
  - specs/autonomous-agent-worker-pattern-extraction/sprints/01-pattern-extraction-sprint.md
  - specs/autonomous-agent-worker-pattern-extraction/backlog/01-pattern-extraction-backlog.md
  - specs/autonomous-agent-worker-pattern-extraction/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-pattern: add extraction queue`

### TASK-AAWPE-01-001: Inventory completed worker patterns

- status: pending
- source: specs/autonomous-agent-worker-pattern-extraction/backlog/01-pattern-extraction-backlog.md
- task brief: specs/autonomous-agent-worker-pattern-extraction/tasks/01-inventory/01-inventory-worker-patterns.md
- depends on:
  - TASK-AAWPE-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-worker-pattern-extraction/README.md
  - specs/autonomous-agent-worker-pattern-extraction/conversation-capture.md
  - specs/autonomous-agent-worker-pattern-extraction/tasks/01-inventory/01-inventory-worker-patterns.md
  - specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md
  - specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md
  - specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md
  - specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-verification.md
- skills:
  - none; inventory task
- expected outputs:
  - specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - inventory extracts common pattern elements and differences
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-pattern: inventory workers`

### TASK-AAWPE-02-001: Create AutonomousAgent worker pattern doc

- status: pending
- source: specs/autonomous-agent-worker-pattern-extraction/backlog/01-pattern-extraction-backlog.md
- task brief: specs/autonomous-agent-worker-pattern-extraction/tasks/02-docs/01-create-worker-pattern-doc.md
- depends on:
  - TASK-AAWPE-01-001
- required reads:
  - inventory artifact from TASK-AAWPE-01-001
  - existing docs/skills found with focused `rg`
- skills:
  - none; docs task
- expected outputs:
  - docs/autonomous-agent-worker-runtime-pattern.md or equivalent
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving the doc covers task contract, governed capabilities, v3 events, attention, surfaces, provider fail-closed, and no fake success
- done criteria:
  - reusable worker pattern doc exists and is accurate
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-pattern: add worker runtime doc`

### TASK-AAWPE-03-001: Update AutonomousAgent skills routing

- status: pending
- source: specs/autonomous-agent-worker-pattern-extraction/backlog/01-pattern-extraction-backlog.md
- task brief: specs/autonomous-agent-worker-pattern-extraction/tasks/03-skills/01-update-autonomous-agent-skills-routing.md
- depends on:
  - TASK-AAWPE-02-001
- required reads:
  - pattern doc from TASK-AAWPE-02-001
  - skills/README.md
  - relevant skills/akka-autonomous-* and workstream/capability skills
- skills:
  - none; skill-maintenance task
- expected outputs:
  - focused skill/routing updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving skills reference the worker pattern and preserve runtime completion guardrails
- done criteria:
  - future worker tasks route to the reusable pattern
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-pattern: update skill routing`

### TASK-AAWPE-04-001: Add example index and next-worker recommendations

- status: pending
- source: specs/autonomous-agent-worker-pattern-extraction/backlog/01-pattern-extraction-backlog.md
- task brief: specs/autonomous-agent-worker-pattern-extraction/tasks/04-examples/01-add-example-index-and-next-workers.md
- depends on:
  - TASK-AAWPE-03-001
- required reads:
  - pattern doc from TASK-AAWPE-02-001
  - updated skills from TASK-AAWPE-03-001
  - handoff/verification artifacts for both completed worker verticals
- skills:
  - none; docs/handoff task
- expected outputs:
  - example index or handoff artifact
  - next-worker recommendation list
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` showing both examples and next-worker candidates are discoverable
- done criteria:
  - examples and next-worker candidates are documented without creating implementation queues
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-pattern: add examples handoff`

### TASK-AAWPE-99-001: Verify worker pattern extraction

- status: pending
- source: mini-project verification loop
- task brief: specs/autonomous-agent-worker-pattern-extraction/tasks/99-verification/01-verify-worker-pattern-extraction.md
- depends on:
  - TASK-AAWPE-01-001
  - TASK-AAWPE-02-001
  - TASK-AAWPE-03-001
  - TASK-AAWPE-04-001
- required reads:
  - all mini-project artifacts
  - edited docs/skills
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - focused `rg` for AutonomousAgent worker pattern, examples, provider fail-closed, no fake success, v3 events, attention, surfaces, and next-worker candidates
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-pattern: verify extraction`
