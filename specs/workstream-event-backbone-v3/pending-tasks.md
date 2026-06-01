# Pending Tasks: Workstream Event Backbone v3

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `event-backbone: <short task title>`.

## Tasks

### TASK-WEB3-00-001: Create workstream event backbone v3 queue

- status: done
- source: current conversation; user chose Workstream Event Backbone v3 next and agreed broader AutonomousAgent runtime integration should follow after v3
- task brief: specs/workstream-event-backbone-v3/tasks/00-planning/00-create-workstream-event-backbone-v3-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/workstream-attention-backbone-v1/pending-tasks.md
  - specs/workstream-attention-event-producers-v2/pending-tasks.md
  - specs/attention-release-readiness-dogfood/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/pending-tasks.md
  - specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md
  - specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
  - specs/workstream-event-backbone-v3/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project captures rationale, sprint sequence, backlog, task briefs, pending queue, and AutonomousAgent-after-v3 sequencing
  - task changes and queue update are committed
- notes:
  - commit message: `event-backbone: add v3 queue`

### TASK-WEB3-01-001: Define event contract and gap map

- status: done
- source: specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
- task brief: specs/workstream-event-backbone-v3/tasks/01-contracts/01-define-event-contract-and-gap-map.md
- depends on:
  - TASK-WEB3-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md
  - specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
  - specs/workstream-event-backbone-v3/tasks/01-contracts/01-define-event-contract-and-gap-map.md
  - specs/workstream-attention-event-producers-v2/attention-event-producers-v2-contract.md
  - docs/workstream-dashboard-attention-event-backbone-wip.md
- skills:
  - none; starter/reference contract task
- expected outputs:
  - specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract names event envelope, source refs, idempotency, consumers/projections, workflow lifecycle, provider events, and AutonomousAgent follow-up sequencing
- done criteria:
  - implementation tasks can proceed without guessing event fields, boundaries, or consumer behavior
  - contract prevents bypassing governed capabilities/auth/audit
  - task changes and queue update are committed
- notes:
  - vertical contract: cross-cutting event backbone over attention/workstream shell; event source families feed attention/dashboard/trace projections; selected Akka substrate to be decided by contract; future AutonomousAgent runtime integration remains next-after-v3
  - commit message: `event-backbone: define v3 contract`

### TASK-WEB3-02-001: Implement domain events and consumer projection path

- status: done
- source: specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
- task brief: specs/workstream-event-backbone-v3/tasks/02-events-consumers/01-implement-domain-events-and-consumer.md
- depends on:
  - TASK-WEB3-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md
  - specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
  - specs/workstream-event-backbone-v3/tasks/02-events-consumers/01-implement-domain-events-and-consumer.md
  - v3 contract from TASK-WEB3-01-001
  - relevant starter backend attention/producer/domain service files
- skills:
  - akka-consumers if adding an Akka Consumer
- expected outputs:
  - starter event records/envelopes and publication path
  - governed consumer/projection reaction path into attention/projection state
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded starter backend Maven tests covering event/consumer/projection behavior
  - focused `rg` for event envelope, consumer, idempotency, and source refs
- done criteria:
  - at least one real event-backed path updates attention/projection state
  - duplicate events are safe
  - task changes and queue update are committed
- notes:
  - vertical contract: selected starter flow such as invitation or governance; events preserve tenant/customer/AuthContext/source/capability/trace refs; consumer/projection must not bypass governed capability rules
  - commit message: `event-backbone: implement domain consumer path`

### TASK-WEB3-03-001: Add workflow/provider lifecycle events

- status: pending
- source: specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
- task brief: specs/workstream-event-backbone-v3/tasks/03-workflows/01-add-workflow-provider-lifecycle-events.md
- depends on:
  - TASK-WEB3-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md
  - specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
  - specs/workstream-event-backbone-v3/tasks/03-workflows/01-add-workflow-provider-lifecycle-events.md
  - v3 contract from TASK-WEB3-01-001
  - implementation from TASK-WEB3-02-001
  - starter provider-blocked, access-review, invitation, governance, or workflow/process-like files
- skills:
  - workflow/timed/consumer skills only if local implementation shape requires them
- expected outputs:
  - lifecycle event implementation for bounded starter cases
  - attention/dashboard source-ref mapping
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend tests for lifecycle event behavior
  - focused `rg` proving provider/worker states are represented honestly
- done criteria:
  - workflow/process/provider lifecycle events feed attention/projection state
  - no fake model-backed success path is introduced
  - task changes and queue update are committed
- notes:
  - vertical contract: provider blocked/readiness, access-review worker/task state, invitation expiry, or governance lifecycle; audit/work trace required; future real AutonomousAgent runtime remains out of scope
  - commit message: `event-backbone: add lifecycle events`

### TASK-WEB3-04-001: Harden event-backed update delivery

- status: pending
- source: specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
- task brief: specs/workstream-event-backbone-v3/tasks/04-delivery/01-harden-event-backed-update-delivery.md
- depends on:
  - TASK-WEB3-03-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md
  - specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
  - specs/workstream-event-backbone-v3/tasks/04-delivery/01-harden-event-backed-update-delivery.md
  - v3 contract from TASK-WEB3-01-001
  - implementation from TASK-WEB3-02-001 and TASK-WEB3-03-001
  - existing frontend/backend attention update delivery files
- skills:
  - web UI/frontend skills as needed
  - endpoint/SSE skills only if adding a stream endpoint
- expected outputs:
  - update delivery/projection hardening for event-backed changes
  - frontend/backend tests as needed
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - frontend tests/typecheck/build if frontend changes are made
  - scaffolded backend tests if backend endpoints/actions change
  - focused `rg` proving backend-derived event-backed updates are used
- done criteria:
  - event-backed projection changes are visible through supported update paths
  - frontend state is not authoritative
  - task changes and queue update are committed
- notes:
  - vertical contract: shell/rail, My Account, workstream dashboards; backend event/projection state is source of truth; optional stream only if bounded
  - commit message: `event-backbone: harden update delivery`

### TASK-WEB3-05-001: Update event backbone docs and handoff

- status: pending
- source: specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
- task brief: specs/workstream-event-backbone-v3/tasks/05-docs/01-update-event-backbone-docs-and-handoff.md
- depends on:
  - TASK-WEB3-04-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/sprints/01-workstream-event-backbone-v3-sprint.md
  - specs/workstream-event-backbone-v3/backlog/01-workstream-event-backbone-v3-build-backlog.md
  - specs/workstream-event-backbone-v3/tasks/05-docs/01-update-event-backbone-docs-and-handoff.md
  - v3 contract and completed implementation notes
- skills:
  - none; docs/handoff task
- expected outputs:
  - docs/handoff updates distinguishing v1/v2/v3 and future AutonomousAgent runtime integration
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish v1/v2/v3/future AutonomousAgent work without stale missing-backbone claims
- done criteria:
  - future agents can understand event backbone state and next recommended AutonomousAgent work
  - task changes and queue update are committed
- notes:
  - commit message: `event-backbone: update docs handoff`

### TASK-WEB3-99-001: Verify workstream event backbone v3 completion

- status: pending
- source: mini-project verification loop
- task brief: specs/workstream-event-backbone-v3/tasks/99-verification/01-verify-workstream-event-backbone-v3.md
- depends on:
  - TASK-WEB3-01-001
  - TASK-WEB3-02-001
  - TASK-WEB3-03-001
  - TASK-WEB3-04-001
  - TASK-WEB3-05-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-event-backbone-v3/README.md
  - specs/workstream-event-backbone-v3/conversation-capture.md
  - specs/workstream-event-backbone-v3/pending-tasks.md
  - all artifacts and task briefs under specs/workstream-event-backbone-v3/
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional completion summary or appended follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for event publication/consumer/projection/lifecycle behavior
  - frontend tests/typecheck/build if update delivery changed frontend
  - focused `rg` for event envelope, consumers, idempotency, source refs, lifecycle events, and future AutonomousAgent handoff
- done criteria:
  - mini-project done state is assessed
  - if complete, v3 completion is recorded
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `event-backbone: verify v3 completion`
