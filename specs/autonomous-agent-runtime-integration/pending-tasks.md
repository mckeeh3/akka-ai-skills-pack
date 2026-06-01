# Pending Tasks: AutonomousAgent Runtime Integration

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `autonomous-agent: <short task title>`.

## Tasks

### TASK-AAI-00-001: Create AutonomousAgent runtime integration queue

- status: done
- source: current conversation after Workstream Event Backbone v3 completion; user requested suggested AutonomousAgent runtime integration mini-project
- task brief: specs/autonomous-agent-runtime-integration/tasks/00-planning/00-create-autonomous-agent-runtime-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/workstream-event-backbone-v3/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/pending-tasks.md
  - specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md
  - specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
  - specs/autonomous-agent-runtime-integration/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project captures rationale, sprint sequence, backlog, task briefs, pending queue, and User Admin Access Review first vertical
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent: add runtime integration queue`

### TASK-AAI-01-001: Define runtime contract and SDK gap check

- status: done
- source: specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
- task brief: specs/autonomous-agent-runtime-integration/tasks/01-contracts/01-define-runtime-contract-and-sdk-gap.md
- depends on:
  - TASK-AAI-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md
  - specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
  - specs/autonomous-agent-runtime-integration/tasks/01-contracts/01-define-runtime-contract-and-sdk-gap.md
  - specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md
  - official Akka AutonomousAgent docs/examples under akka-context/sdk/ and/or local autonomous-agent skills
  - current starter User Admin access-review, attention, event backbone, and agent runtime files
- skills:
  - akka-autonomous-agents
  - akka-autonomous-agent-tasks if available/relevant
- expected outputs:
  - specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving contract names AutonomousAgent, task lifecycle, provider fail-closed, v3 events, attention, surfaces, and no fake success guardrail
- done criteria:
  - implementation tasks can proceed without guessing SDK shape, task contract, auth, events, attention, or surfaces
  - any SDK blocker is recorded with affected tasks updated
  - task changes and queue update are committed
- notes:
  - vertical contract: User Admin Access Review; durable internal/background AutonomousAgent; governed capabilities for task start/query/lifecycle/result review; v3 events; attention; User Admin/My Account surfaces; provider fail-closed; no deterministic fake success
  - outcome: contract created at `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`; no SDK blocker found for single-task AutonomousAgent path; next task should block only if local dependency version lacks documented APIs
  - commit message: `autonomous-agent: define access review contract`

### TASK-AAI-02-001: Implement access review AutonomousAgent runtime

- status: done
- source: specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
- task brief: specs/autonomous-agent-runtime-integration/tasks/02-runtime/01-implement-access-review-runtime.md
- depends on:
  - TASK-AAI-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md
  - specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
  - specs/autonomous-agent-runtime-integration/tasks/02-runtime/01-implement-access-review-runtime.md
  - contract from TASK-AAI-01-001
  - relevant Akka AutonomousAgent docs/skills confirmed by TASK-AAI-01-001
  - starter User Admin access-review service, attention, event backbone, and agent runtime files
- skills:
  - akka-autonomous-agents
  - akka-autonomous-agent-tasks if relevant
- expected outputs:
  - backend AutonomousAgent runtime/task lifecycle implementation or explicit blocker
  - backend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests for access-review AutonomousAgent runtime/fail-closed behavior
  - focused `rg` proving no deterministic fake success path is wired as normal runtime
- done criteria:
  - backend runtime path exists or is explicitly blocked with queue updates
  - tests prove governed lifecycle and fail-closed behavior
  - task changes and queue update are committed
- notes:
  - vertical contract: User Admin access-review task start/query/lifecycle/result review; AuthContext tenant/customer scope; event backbone; attention; audit/work trace; provider fail-closed
  - commit message: `autonomous-agent: implement access review runtime`
  - completed: added `UserAdminAccessReviewAutonomousAgent`, typed task/result/rule definitions, ComponentClient-backed runtime adapter, fail-closed runtime adapter, starter task projection with `autonomousAgentTaskId`, and backend tests for lifecycle/idempotency/fail-closed behavior including `TestModelProvider.AutonomousAgentTools.completeTask`/`failTask`
  - checks: `git diff --check`; rendered scaffold `mvn -q test`; focused `rg` for AutonomousAgent/task APIs, fail-closed/no fake success guardrails, and access-review event/attention/surface refs

### TASK-AAI-03-001: Wire AutonomousAgent events, attention, and surfaces

- status: done
- source: specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
- task brief: specs/autonomous-agent-runtime-integration/tasks/03-surfaces/01-wire-events-attention-surfaces.md
- depends on:
  - TASK-AAI-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md
  - specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
  - specs/autonomous-agent-runtime-integration/tasks/03-surfaces/01-wire-events-attention-surfaces.md
  - contract from TASK-AAI-01-001
  - backend runtime implementation from TASK-AAI-02-001
  - v3 event backbone contract/implementation
  - v1/v2 attention implementation
  - starter frontend workstream surface files
- skills:
  - web UI/frontend skills as needed
  - event/consumer skills if backend event mapping changes
- expected outputs:
  - task lifecycle/progress/result/failure event mappings
  - attention mappings
  - structured progress/result/decision/system-message surfaces
  - backend/frontend tests
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend tests for events/attention linkage
  - frontend tests/typecheck/build if frontend changes are made
  - focused `rg` for task lifecycle events, attention mappings, and surface ids
- done criteria:
  - task lifecycle states are visible through events, attention, and surfaces
  - frontend state is not authoritative
  - task changes and queue update are committed
- notes:
  - vertical contract: User Admin and My Account attention/surface paths; v3 events; backend-derived update delivery; no fake model-backed success
  - completed: access-review lifecycle publication now emits both `workflow.access_review.*` and `worker.task.*` v3 events with `autonomous_task` refs; event attention projection accepts workflow and task families; attention producer preserves event/idempotency evidence across duplicate projections; User Admin access-review surface exposes backend-derived result review states, result summary, trace/evidence/recommendation data, and no-direct-mutation safety copy; frontend workflow-status rendering supports backend string refs without becoming authoritative.
  - checks: `git diff --check`; rendered scaffold backend `mvn -q -Dtest=UserAdminAccessReviewServiceTest,WorkstreamEventBackboneServiceTest test`; frontend `npm test`; frontend `npm run typecheck`; frontend `npm run build`; focused `rg` for `workflow.access_review`, `worker.task`, `autonomous_task`, `attention:worker-task`, `surface-user-admin-access-review`, `blocked_provider_or_runtime`, fail-closed/no fake success guardrails
  - commit message: `autonomous-agent: wire events attention surfaces`

### TASK-AAI-04-001: Run AutonomousAgent runtime validation

- status: pending
- source: specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
- task brief: specs/autonomous-agent-runtime-integration/tasks/04-validation/01-run-runtime-validation.md
- depends on:
  - TASK-AAI-03-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md
  - specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
  - specs/autonomous-agent-runtime-integration/tasks/04-validation/01-run-runtime-validation.md
  - contract from TASK-AAI-01-001
  - implementation notes from TASK-AAI-02-001 and TASK-AAI-03-001
- skills:
  - none; validation task
- expected outputs:
  - runtime validation artifact
  - updated pending-tasks.md
  - optional appended blocker tasks if issues are found
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests
  - frontend tests/typecheck/build
  - manual/local smoke notes or clear blocked reason
- done criteria:
  - runtime validation evidence is captured
  - blockers are converted to bounded tasks or recorded as release blockers
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent: validate runtime path`

### TASK-AAI-05-001: Update AutonomousAgent runtime docs and handoff

- status: pending
- source: specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
- task brief: specs/autonomous-agent-runtime-integration/tasks/05-docs/01-update-autonomous-agent-runtime-docs.md
- depends on:
  - TASK-AAI-04-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/sprints/01-autonomous-agent-runtime-sprint.md
  - specs/autonomous-agent-runtime-integration/backlog/01-autonomous-agent-runtime-build-backlog.md
  - specs/autonomous-agent-runtime-integration/tasks/05-docs/01-update-autonomous-agent-runtime-docs.md
  - contract and validation artifacts from prior tasks
- skills:
  - none; docs/handoff task
- expected outputs:
  - docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs distinguish implemented vertical, future workers, provider fail-closed, and no fake success
- done criteria:
  - future agents can understand current AutonomousAgent runtime integration status and next steps
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent: update runtime docs`

### TASK-AAI-99-001: Verify AutonomousAgent runtime integration

- status: pending
- source: mini-project verification loop
- task brief: specs/autonomous-agent-runtime-integration/tasks/99-verification/01-verify-autonomous-agent-runtime-integration.md
- depends on:
  - TASK-AAI-01-001
  - TASK-AAI-02-001
  - TASK-AAI-03-001
  - TASK-AAI-04-001
  - TASK-AAI-05-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-runtime-integration/README.md
  - specs/autonomous-agent-runtime-integration/conversation-capture.md
  - specs/autonomous-agent-runtime-integration/pending-tasks.md
  - all artifacts and task briefs under specs/autonomous-agent-runtime-integration/
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional completion summary or appended follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for AutonomousAgent runtime/fail-closed/events/attention
  - frontend tests/typecheck/build if surfaces changed
  - focused `rg` for AutonomousAgent, fail-closed, no fake success, v3 events, attention, and surfaces
- done criteria:
  - mini-project done state is assessed
  - if complete, completion is recorded
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent: verify runtime integration`
