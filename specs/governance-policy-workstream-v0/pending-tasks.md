# Pending Tasks: Governance Policy Workstream v0

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `governance-policy-v0: <short task title>`.

## Tasks

### TASK-GOVPOL-00-001: Create Governance Policy Workstream v0 planning scaffold

- status: done
- source: user request for one mini-project per five-core v0 workstream
- task brief: specs/governance-policy-workstream-v0/tasks/00-planning/00-create-governance-policy-workstream-v0-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-component-selection-guide.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/governance-policy-workstream-v0/README.md
  - specs/governance-policy-workstream-v0/conversation-capture.md
  - specs/governance-policy-workstream-v0/pending-tasks.md
  - specs/governance-policy-workstream-v0/sprints/*.md
  - specs/governance-policy-workstream-v0/backlog/*.md
  - specs/governance-policy-workstream-v0/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - planning scaffold is committed
- notes:
  - commit message: `governance-policy-v0: add planning queue`

### TASK-GOVPOL-01-001: Define Governance Policy Workstream v0 contract and capability inventory

- status: done
- source: specs/governance-policy-workstream-v0/backlog/01-governance-policy-workstream-v0-build-backlog.md
- task brief: specs/governance-policy-workstream-v0/tasks/01-contract/01-define-workstream-contract.md
- depends on:
  - TASK-GOVPOL-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/governance-policy-workstream-v0/README.md
  - specs/governance-policy-workstream-v0/conversation-capture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-component-selection-guide.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/governance-policy-workstream-v0/capability-inventory.md
- required checks:
  - `git diff --check`
  - `rg -n "capability|AuthContext|request/response|AutonomousAgent|deterministic|trace|validation" specs/governance-policy-workstream-v0`
- done criteria:
  - workstream contract defines functional agent responsibility, structured surfaces/actions, capabilities, authority, traces, agent-type choices, and validation path
  - task changes and queue update are committed
- notes:
  - unblocked by completed TASK-FCPLAN-01-001; inherit shared contract and dependency map before defining workstream-specific scope.
  - checks: `git diff --check`; `rg -n "capability|AuthContext|request/response|AutonomousAgent|deterministic|trace|validation" specs/governance-policy-workstream-v0`
  - commit message: `governance-policy-v0: define workstream contract`

### TASK-GOVPOL-02-001: Implement Governance Policy Workstream v0 backend/runtime vertical

- status: done
- source: specs/governance-policy-workstream-v0/backlog/01-governance-policy-workstream-v0-build-backlog.md
- task brief: specs/governance-policy-workstream-v0/tasks/02-runtime/01-implement-backend-runtime.md
- depends on:
  - TASK-GOVPOL-01-001
- required reads:
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/governance-policy-workstream-v0/capability-inventory.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-autonomous-agents/SKILL.md
  - skills/akka-autonomous-agent-governance/SKILL.md
  - skills/akka-autonomous-agent-testing/SKILL.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - akka-agents
  - akka-agent-tools
  - akka-agent-tool-boundaries
  - akka-autonomous-agents when the contract includes durable internal/background tasks
- expected outputs:
  - focused backend/runtime/template changes for this workstream only
  - backend tests for success, validation, forbidden/tenant isolation, idempotency where relevant, audit/work trace, and model/provider fail-closed behavior where relevant
- required checks:
  - `mvn test` or targeted rendered-starter backend test command documented by the task
  - `git diff --check`
- done criteria:
  - backend/runtime behavior in the contract works through real local paths or the task records a blocker
  - no fixture/mock/model-less normal runtime substitute is used to mark runtime behavior done
  - task changes and queue update are committed
- notes:
  - vertical contract: `Governance Policy Workstream v0; capability ids from capability-inventory; selected Akka substrate per capability; audit/work trace required`
  - checks: `tmp=$(mktemp -d) && tools/scaffold-ai-first-saas-starter.sh --target "$tmp" --base-package ai.first --app-name TestApp >/tmp/scaffold.log && cd "$tmp" && mvn test -Dtest=WorkstreamServiceTest -q`; `git diff --check`
  - commit message: `governance-policy-v0: implement backend runtime`

### TASK-GOVPOL-03-001: Implement Governance Policy Workstream v0 frontend surfaces and workstream UX

- status: done
- source: specs/governance-policy-workstream-v0/backlog/01-governance-policy-workstream-v0-build-backlog.md
- task brief: specs/governance-policy-workstream-v0/tasks/03-frontend/01-implement-frontend-surfaces.md
- depends on:
  - TASK-GOVPOL-02-001
- required reads:
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/governance-policy-workstream-v0/capability-inventory.md
  - skills/akka-web-ui-apps/SKILL.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - akka-web-ui-apps
- expected outputs:
  - focused frontend/template changes for this workstream only
  - frontend tests/typecheck updates for surfaces, actions, denials, trace links, accessibility, and safe rendering
- required checks:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
- done criteria:
  - frontend surfaces and workstream behavior reflect backend capabilities and do not create frontend-only authorization
  - task changes and queue update are committed
- notes:
  - checks: `cd templates/ai-first-saas-starter/frontend && npm test -- --run`; `cd templates/ai-first-saas-starter/frontend && npm run typecheck`; `git diff --check`
  - commit message: `governance-policy-v0: implement frontend surfaces`

### TASK-GOVPOL-99-001: Verify Governance Policy Workstream v0 completion

- status: pending
- source: mini-project verification loop
- task brief: specs/governance-policy-workstream-v0/tasks/99-verification/01-verify-governance-policy-workstream-v0.md
- depends on:
  - TASK-GOVPOL-01-001
  - TASK-GOVPOL-02-001
  - TASK-GOVPOL-03-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/governance-policy-workstream-v0/README.md
  - specs/governance-policy-workstream-v0/conversation-capture.md
  - specs/governance-policy-workstream-v0/pending-tasks.md
  - specs/governance-policy-workstream-v0/sprints/*.md
  - specs/governance-policy-workstream-v0/backlog/*.md
  - specs/governance-policy-workstream-v0/tasks/**/*.md
  - specs/governance-policy-workstream-v0/workstream-contract.md
  - specs/governance-policy-workstream-v0/capability-inventory.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/governance-policy-workstream-v0/pending-tasks.md
  - completion summary, verification notes, or newly appended follow-up tasks
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh` when runtime/template behavior changed
  - `git diff --check`
- done criteria:
  - task group goals have been compared against completed work
  - mini-project done state has been compared against completed work
  - runtime/API/UI validation evidence or blockers are recorded
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - commit message: `governance-policy-v0: verify workstream completion`
