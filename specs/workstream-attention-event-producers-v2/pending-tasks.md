# Pending Tasks: Workstream Attention Event Producers v2

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `attention-producers: <short task title>`.

## Tasks

### TASK-WAEP-00-001: Create attention event producers v2 planning scaffold

- status: done
- source: current conversation after completion of specs/workstream-attention-backbone-v1; user accepted creating the next v2 mini-project
- task brief: specs/workstream-attention-event-producers-v2/tasks/00-planning/00-create-attention-event-producers-v2-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/pending-tasks.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - terminal verification task exists
  - task changes and queue update are committed
- notes:
  - commit message: `attention-producers: add v2 queue`

### TASK-WAEP-01-001: Define producer contract and v1 gap map

- status: done
- source: specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
- task brief: specs/workstream-attention-event-producers-v2/tasks/01-contracts/01-define-producer-contract-and-gap-map.md
- depends on:
  - TASK-WAEP-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/01-contracts/01-define-producer-contract-and-gap-map.md
  - specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md
  - specs/workstream-attention-backbone-v1/pending-tasks.md
- skills:
  - none; starter/reference contract task
- expected outputs:
  - specs/workstream-attention-event-producers-v2/attention-event-producers-v2-contract.md or equivalent
  - updated specs/workstream-attention-event-producers-v2/pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving the contract names producer ids, idempotency, upsert/resolve, timed checks, task-state attention, and update delivery
- done criteria:
  - implementation tasks can proceed without guessing producer identity, lifecycle behavior, or update delivery expectations
  - backend attention state remains authoritative
  - task changes and queue update are committed
- notes:
  - vertical contract: foundation/cross-cutting attention producer layer over v1 backbone; source events/states feed workstream dashboards, My Account, and rail summaries; governed-tool/API exposure remains v1 attention lifecycle/read boundary; audit/work trace required
  - contract artifact: `specs/workstream-attention-event-producers-v2/attention-event-producers-v2-contract.md`
  - validation: `git diff --check`; `rg -n "producerId|attention\.producer\.|idempotencyKey|upsertAttention|resolveAttention|timed checks|Task-state attention|update delivery|attention\.list_rail_summaries|attention\.list_workstream_items|attention\.list_my_account_items" specs/workstream-attention-event-producers-v2/attention-event-producers-v2-contract.md`
  - commit message: `attention-producers: define v2 contract`

### TASK-WAEP-02-001: Wire domain service attention producers

- status: done
- source: specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
- task brief: specs/workstream-attention-event-producers-v2/tasks/02-producers/01-wire-domain-service-producers.md
- depends on:
  - TASK-WAEP-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/02-producers/01-wire-domain-service-producers.md
  - v2 producer contract from TASK-WAEP-01-001
  - specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md
  - relevant starter backend attention, invitation, governance, audit/trace, agent admin, and workstream files
- skills:
  - none unless a focused Akka component skill is needed
- expected outputs:
  - starter backend domain/service producers wired to attention lifecycle
  - backend tests for producer creation/update/resolution/idempotency/redaction
  - updated specs/workstream-attention-event-producers-v2/pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded starter backend Maven tests covering producer behavior
  - focused `rg` for producer ids and upsert/resolve paths
- done criteria:
  - at least two concrete starter flows produce/update/resolve attention through the shared backbone
  - producer behavior is idempotent, tenant-safe, and traced
  - task changes and queue update are committed
- notes:
  - vertical contract: User Admin invitation delivery, Governance proposal/approval, Audit/Trace provider evidence, and/or Agent Admin provider readiness; attention categories include failed_action, approval, blocked_work, audit_anomaly; exposure through backend services and existing workstream surfaces
  - implemented `AttentionProducerService` for `attention.producer.user_admin.invitation_delivery` and `attention.producer.governance.policy_approval`; wired invitation delivery failure/success/revoke/expire/accept plus governance submit/decision/activation/rollback to backend attention upsert/resolve lifecycle.
  - validation: `git diff --check`; scaffolded starter Maven `mvn -q -Dtest=AttentionProducerServiceTest,AttentionServiceTest,InvitationAndUserAdminServiceTest,GovernancePolicyServiceTest test`; focused producer-id/upsert/resolve `rg`.
  - commit message: `attention-producers: wire domain producers`

### TASK-WAEP-03-001: Add timed and worker/task attention producers

- status: done
- source: specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
- task brief: specs/workstream-attention-event-producers-v2/tasks/03-workers/01-add-timed-and-worker-attention.md
- depends on:
  - TASK-WAEP-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/03-workers/01-add-timed-and-worker-attention.md
  - v2 producer contract from TASK-WAEP-01-001
  - v1 attention implementation files
  - relevant starter timed action, invitation, audit summary worker, access-review worker, or provider-blocked state files
- skills:
  - akka-timed-actions if implementing/changing an Akka Timed Action
  - akka-autonomous-agents only if actual AutonomousAgent APIs are touched
- expected outputs:
  - backend timed/stale and worker/task attention producer implementation
  - backend tests
  - updated specs/workstream-attention-event-producers-v2/pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded starter backend Maven tests covering timed/worker attention behavior
  - focused `rg` proving blocked/provider-fail-closed states are represented honestly and no fake worker success path is introduced
- done criteria:
  - at least one timed/stale condition updates attention through the shared backbone
  - worker/task states create/update/resolve attention without fake model-backed success
  - task changes and queue update are committed
- notes:
  - vertical contract: timed/stale attention plus internal worker/task attention; non-UI trigger with dashboard/system-message/result surface refs; audit/work trace required; frontend rendering remains existing attention surfaces unless update delivery task changes it
  - implemented invitation delivery timed-check producer (`runInvitationDeliveryTimedCheck`) and worker/task-state producer (`attention.producer.worker.task_state`) for User Admin access-review `blocked_provider_or_runtime`, completed-review-needed, rejected, queued/running stale, cancel/accept resolution paths.
  - wired `UserAdminAccessReviewService` to upsert/resolve worker task attention while preserving fail-closed behavior and no fake model-backed success path.
  - validation: direct template Maven command is blocked by placeholder `{{MAVEN_GROUP_ID}}`/`{{APP_SLUG}}`; scaffolded equivalent passed with `tools/scaffold-ai-first-saas-starter.sh --target <tmp> --template-dir templates/ai-first-saas-starter --app-name "WAEP Backend Check" --app-slug waep-backend-check --base-package ai.first --maven-group-id ai.first --yes` then `mvn -q -Dtest=AttentionProducerServiceTest,UserAdminAccessReviewServiceTest,WorkstreamServiceTest test`; `git diff --check`; focused blocked/provider/fake-success `rg`.
  - commit message: `attention-producers: add timed worker attention`

### TASK-WAEP-04-001: Wire backend-derived attention update delivery

- status: pending
- source: specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
- task brief: specs/workstream-attention-event-producers-v2/tasks/04-realtime/01-wire-attention-update-delivery.md
- depends on:
  - TASK-WAEP-03-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/04-realtime/01-wire-attention-update-delivery.md
  - v2 producer contract from TASK-WAEP-01-001
  - frontend/backend attention files from v1 and producer tasks
- skills:
  - web UI/frontend skills as needed
  - Akka endpoint/SSE skills only if adding backend streaming endpoints
- expected outputs:
  - backend endpoint/action updates or frontend refresh/poll/stream behavior
  - tests for backend-derived attention update delivery
  - updated specs/workstream-attention-event-producers-v2/pending-tasks.md
- required checks:
  - `git diff --check`
  - targeted frontend tests/typecheck/build
  - targeted scaffolded backend tests if backend endpoints/actions change
  - focused `rg` proving actionable attention update delivery uses backend summaries rather than frontend-only state
- done criteria:
  - users can see updated backend-derived attention summaries/items after producer-affecting changes through selected update path
  - frontend-only state is not authoritative
  - task changes and queue update are committed
- notes:
  - vertical contract: shell/rail, My Account, workstream dashboards; browser API/action or stream exposure; backend attention service remains source of truth; tests cover rendering/update and frontend secret/authority boundary where touched
  - commit message: `attention-producers: wire update delivery`

### TASK-WAEP-05-001: Update attention docs and guidance

- status: pending
- source: specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
- task brief: specs/workstream-attention-event-producers-v2/tasks/05-docs/01-update-attention-docs-and-guidance.md
- depends on:
  - TASK-WAEP-04-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/05-docs/01-update-attention-docs-and-guidance.md
  - specs/workstream-attention-backbone-v1/README.md
  - v2 contract and completed implementation notes
- skills:
  - none; docs/guidance task
- expected outputs:
  - focused docs/guidance updates
  - updated specs/workstream-attention-event-producers-v2/pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` showing docs distinguish implemented v1, v2 producers, and future work without stale “not implemented” claims
- done criteria:
  - future agents will not incorrectly report the attention backbone as missing after v1/v2 work
  - docs preserve backend-authoritative attention and runtime completion standards
  - task changes and queue update are committed
- notes:
  - scope: docs/starter guidance only; do not register project-only docs in pack manifest unless already appropriate
  - commit message: `attention-producers: update docs guidance`

### TASK-WAEP-99-001: Verify attention event producers v2 completion

- status: pending
- source: mini-project verification loop
- task brief: specs/workstream-attention-event-producers-v2/tasks/99-verification/01-verify-attention-event-producers-v2.md
- depends on:
  - TASK-WAEP-01-001
  - TASK-WAEP-02-001
  - TASK-WAEP-03-001
  - TASK-WAEP-04-001
  - TASK-WAEP-05-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-event-producers-v2/README.md
  - specs/workstream-attention-event-producers-v2/conversation-capture.md
  - specs/workstream-attention-event-producers-v2/pending-tasks.md
  - specs/workstream-attention-event-producers-v2/sprints/01-event-producers-v2-sprint.md
  - specs/workstream-attention-event-producers-v2/backlog/01-event-producers-v2-build-backlog.md
  - specs/workstream-attention-event-producers-v2/tasks/**/*.md
  - specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/workstream-attention-event-producers-v2/pending-tasks.md
  - optional completion summary or appended follow-up tasks/task briefs if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for attention producers/timed/worker behavior
  - targeted frontend tests/typecheck/build for attention update delivery
  - `rg -n "AttentionProducer|attention producer|upsertAttention|resolveAttention|timed attention|blocked_provider_or_runtime|railAttentionState|attention backbone" templates/ai-first-saas-starter docs specs/workstream-attention-event-producers-v2`
- done criteria:
  - sprint goals and mini-project done state are assessed against completed work
  - unresolved blockers/gaps are reviewed
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `attention-producers: verify v2 completion`
