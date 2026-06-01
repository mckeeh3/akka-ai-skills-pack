# Pending Tasks: Workstream Attention Backbone v1

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `attention-backbone: <short task title>`.

## Tasks

### TASK-WAB-00-001: Create workstream attention backbone planning scaffold

- status: done
- source: current conversation: user asked to create a mini-project for the shared workstream attention bus/queue after confirming it is not yet implemented
- task brief: specs/workstream-attention-backbone-v1/tasks/00-planning/00-create-workstream-attention-backbone-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/conversation-capture.md
  - specs/workstream-attention-backbone-v1/pending-tasks.md
  - specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md
  - specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
  - specs/workstream-attention-backbone-v1/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - terminal verification task exists
  - task changes and queue update are committed
- notes:
  - commit message: `attention-backbone: add v1 queue`

### TASK-WAB-01-001: Define starter attention backbone v1 contract

- status: done
- source: specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
- task brief: specs/workstream-attention-backbone-v1/tasks/01-contracts/01-define-attention-contract.md
- depends on:
  - TASK-WAB-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/conversation-capture.md
  - specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md
  - specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
  - specs/workstream-attention-backbone-v1/tasks/01-contracts/01-define-attention-contract.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/workstream-dashboard-attention-event-backbone-wip.md
- skills:
  - none; starter/reference contract task
- expected outputs:
  - focused v1 attention backbone contract artifact
  - updated specs/workstream-attention-backbone-v1/pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving the contract names the shared attention backbone, lifecycle, scoped projections, My Account aggregate, left-rail summary, and frontend-only badge guardrail
- done criteria:
  - backend/frontend tasks can implement without guessing attention fields, lifecycle, projections, capabilities, or redaction rules
  - shared backbone with workstream-scoped ownership/projections is preserved
  - task changes and queue update are committed
- notes:
  - vertical contract: foundation/cross-cutting attention backbone; attention category contract for all workstreams; dashboard purpose is role-specific `what needs attention`; governed-tool ids to be defined by task; exposure channels include browser-tool/API/view/internal-tool; selected substrate to be proposed; audit/work trace required
  - contract artifact: `specs/workstream-attention-backbone-v1/attention-backbone-v1-contract.md`
  - commit message: `attention-backbone: define v1 contract`

### TASK-WAB-02-001: Implement backend attention foundation

- status: pending
- source: specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
- task brief: specs/workstream-attention-backbone-v1/tasks/02-backend/01-implement-backend-attention-foundation.md
- depends on:
  - TASK-WAB-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/conversation-capture.md
  - specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md
  - specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
  - specs/workstream-attention-backbone-v1/tasks/02-backend/01-implement-backend-attention-foundation.md
  - contract artifact from TASK-WAB-01-001
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuthContextResolver.java
- skills:
  - akka-entity-type-selection if durable state choice is unclear
- expected outputs:
  - starter backend attention model/service/repository files
  - starter backend attention tests
  - updated specs/workstream-attention-backbone-v1/pending-tasks.md
- required checks:
  - `git diff --check`
  - targeted backend Maven tests covering new attention foundation behavior
- done criteria:
  - one shared backend attention foundation exists
  - scoped reads and lifecycle operations enforce AuthContext, tenant/customer, visible workstream/capability, and safe redaction
  - audit/protected-read/denial traces are emitted
  - task changes and queue update are committed
- notes:
  - vertical contract: foundation/cross-cutting attention backbone; non-UI/internal plus API-ready read/lifecycle operations; capability ids from TASK-WAB-01-001; selected substrate must be justified; tests include success, forbidden, tenant isolation, idempotency/no-op, audit/trace
  - commit message: `attention-backbone: implement backend foundation`

### TASK-WAB-03-001: Wire core workstreams to shared attention

- status: pending
- source: specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
- task brief: specs/workstream-attention-backbone-v1/tasks/03-integration/01-wire-core-workstreams-to-attention.md
- depends on:
  - TASK-WAB-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/conversation-capture.md
  - specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md
  - specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
  - specs/workstream-attention-backbone-v1/tasks/03-integration/01-wire-core-workstreams-to-attention.md
  - contract artifact from TASK-WAB-01-001
  - backend attention files/tests from TASK-WAB-02-001
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- skills:
  - none unless a focused implementation gap is discovered
- expected outputs:
  - My Account and core workstream backend services wired to shared attention
  - backend tests for aggregation, dashboard attention, redaction, and `open_attention_item`
  - updated specs/workstream-attention-backbone-v1/pending-tasks.md
- required checks:
  - `git diff --check`
  - targeted backend tests for My Account, WorkstreamService, and attention service
  - focused `rg` proving hard-coded personal attention has been removed or replaced by shared service calls
- done criteria:
  - My Account aggregate attention reads from shared attention state
  - User Admin, Agent Admin, Audit/Trace, and Governance/Policy v1 attention items derive from shared attention producer/derivation paths
  - hidden items/workstreams are safely redacted and traced
  - task changes and queue update are committed
- notes:
  - vertical contract: five core workstreams; attention categories include invitation delivery, provider blocked/readiness, governance approval, audit failure evidence; surfaces include My Account dashboard and workstream dashboards; exposure channels include browser API/surface action/internal service; audit/work trace required
  - commit message: `attention-backbone: wire core workstreams`

### TASK-WAB-04-001: Wire frontend attention surfaces to backend-derived data

- status: pending
- source: specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
- task brief: specs/workstream-attention-backbone-v1/tasks/04-frontend/01-wire-frontend-attention-surfaces.md
- depends on:
  - TASK-WAB-03-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/conversation-capture.md
  - specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md
  - specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
  - specs/workstream-attention-backbone-v1/tasks/04-frontend/01-wire-frontend-attention-surfaces.md
  - contract artifact from TASK-WAB-01-001
  - backend contracts from TASK-WAB-02-001 and TASK-WAB-03-001
  - templates/ai-first-saas-starter/frontend/src/workstream/**
- skills:
  - web UI/frontend skills only if needed by local patterns
- expected outputs:
  - starter frontend attention types/components/fixtures/tests updated for backend-derived attention
  - updated specs/workstream-attention-backbone-v1/pending-tasks.md
- required checks:
  - `git diff --check`
  - targeted frontend contract/type/build tests from templates/ai-first-saas-starter/frontend
  - focused `rg` proving actionable attention counts are not sourced only from railAttentionState
- done criteria:
  - left rail and My Account attention UI consume backend-derived data at v1 scope
  - transient unseen-response badges remain separate from actionable attention
  - rendering tests cover item metadata, empty/denied states, actions, and trace refs
  - task changes and queue update are committed
- notes:
  - vertical contract: frontend surfaces for left rail, My Account dashboard/personal queue, and workstream dashboard attention sections; browser-tool/API exposure; backend authorization remains authoritative; tests include rendering and frontend secret/authority boundary where touched
  - commit message: `attention-backbone: wire frontend surfaces`

### TASK-WAB-99-001: Verify attention backbone v1 completion

- status: pending
- source: mini-project verification loop
- task brief: specs/workstream-attention-backbone-v1/tasks/99-verification/01-verify-attention-backbone-v1.md
- depends on:
  - TASK-WAB-01-001
  - TASK-WAB-02-001
  - TASK-WAB-03-001
  - TASK-WAB-04-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-attention-backbone-v1/README.md
  - specs/workstream-attention-backbone-v1/conversation-capture.md
  - specs/workstream-attention-backbone-v1/pending-tasks.md
  - specs/workstream-attention-backbone-v1/sprints/01-attention-backbone-v1-sprint.md
  - specs/workstream-attention-backbone-v1/backlog/01-attention-backbone-v1-build-backlog.md
  - specs/workstream-attention-backbone-v1/tasks/**/*.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/workstream-attention-backbone-v1/pending-tasks.md
  - optional completion summary or appended follow-up tasks/task briefs if gaps remain
- required checks:
  - `git diff --check`
  - targeted backend tests for attention/My Account/workstream services
  - targeted frontend tests for workstream/rail/My Account attention rendering
  - `rg -n "AttentionItem|attention summary|list_personal_attention|open_attention_item|railAttentionState|personalAttention" templates/ai-first-saas-starter specs/workstream-attention-backbone-v1`
- done criteria:
  - sprint goals and mini-project done state are assessed against completed work
  - unresolved blockers/gaps are reviewed
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `attention-backbone: verify v1 completion`
