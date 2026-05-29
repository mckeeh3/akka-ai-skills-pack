# Pending Tasks: Agent Admin Workstream v0

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `agent-admin-v0: <short task title>`.

## Tasks

### TASK-AGENTADMIN-00-001: Create Agent Admin Workstream v0 planning scaffold

- status: done
- source: user request for one mini-project per five-core v0 workstream
- task brief: specs/agent-admin-workstream-v0/tasks/00-planning/00-create-agent-admin-workstream-v0-queue.md
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
  - specs/agent-admin-workstream-v0/README.md
  - specs/agent-admin-workstream-v0/conversation-capture.md
  - specs/agent-admin-workstream-v0/pending-tasks.md
  - specs/agent-admin-workstream-v0/sprints/*.md
  - specs/agent-admin-workstream-v0/backlog/*.md
  - specs/agent-admin-workstream-v0/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - planning scaffold is committed
- notes:
  - commit message: `agent-admin-v0: add planning queue`

### TASK-AGENTADMIN-01-001: Define Agent Admin Workstream v0 contract and capability inventory

- status: done
- source: specs/agent-admin-workstream-v0/backlog/01-agent-admin-workstream-v0-build-backlog.md
- task brief: specs/agent-admin-workstream-v0/tasks/01-contract/01-define-workstream-contract.md
- depends on:
  - TASK-AGENTADMIN-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/agent-admin-workstream-v0/README.md
  - specs/agent-admin-workstream-v0/conversation-capture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-component-selection-guide.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
- required checks:
  - `git diff --check`
  - `rg -n "capability|AuthContext|request/response|AutonomousAgent|deterministic|trace|validation" specs/agent-admin-workstream-v0`
- done criteria:
  - workstream contract defines functional agent responsibility, structured surfaces/actions, capabilities, authority, traces, agent-type choices, and validation path
  - task changes and queue update are committed
- notes:
  - unblocked by completed TASK-FCPLAN-01-001; inherit shared contract and dependency map before defining workstream-specific scope.
  - checks: `git diff --check`; `rg -n "capability|AuthContext|request/response|AutonomousAgent|deterministic|trace|validation" specs/agent-admin-workstream-v0`
  - commit message: `agent-admin-v0: define workstream contract`

### TASK-AGENTADMIN-02-001: Implement Agent Admin Workstream v0 backend/runtime vertical

- status: done
- source: specs/agent-admin-workstream-v0/backlog/01-agent-admin-workstream-v0-build-backlog.md
- task brief: specs/agent-admin-workstream-v0/tasks/02-runtime/01-implement-backend-runtime.md
- depends on:
  - TASK-AGENTADMIN-01-001
- required reads:
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
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
  - vertical contract: `Agent Admin Workstream v0; capability ids from capability-inventory; selected Akka substrate per capability; audit/work trace required`
  - completed backend/runtime focus: Agent Admin v0 capability ids are exposed through `/api/me`, role grants, structured actions, behavior-change/test-console flows, and request/response runtime traces; Agent Admin prompt/test/proposal paths now target the Agent Admin managed definition.
  - checks: `tmp=$(mktemp -d /tmp/agent-admin-v0-backend.XXXXXX); tools/scaffold-ai-first-saas-starter.sh --target "$tmp" --template-dir templates/ai-first-saas-starter --app-name "Agent Admin Backend Check" --app-slug agent-admin-backend-check --base-package ai.first --maven-group-id ai.first >/tmp/scaffold-agent-admin.log && cd "$tmp" && mvn -q -Dtest=AgentRuntimeServiceTest,WorkstreamServiceTest test`; `git diff --check`
  - commit message: `agent-admin-v0: implement backend runtime`

### TASK-AGENTADMIN-03-001: Implement Agent Admin Workstream v0 frontend surfaces and workstream UX

- status: done
- source: specs/agent-admin-workstream-v0/backlog/01-agent-admin-workstream-v0-build-backlog.md
- task brief: specs/agent-admin-workstream-v0/tasks/03-frontend/01-implement-frontend-surfaces.md
- depends on:
  - TASK-AGENTADMIN-02-001
- required reads:
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
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
  - completed Agent Admin frontend fixture/workstream surfaces now align with backend `agent_admin.*` capability ids, list-search catalog shape, action result routing, trace-linked states, and browser-safe role grants; root frontend mirror updated for source-reference parity.
  - checks: `cd templates/ai-first-saas-starter/frontend && npm test -- --run`; `cd templates/ai-first-saas-starter/frontend && npm run typecheck`; `git diff --check`
  - extra non-required root mirror check attempted: `cd frontend && npm test -- --run && npm run typecheck` (failed on pre-existing root mirror drift/missing `backend/src/main/resources/agent-behavior-seeds/starter-v1/manifest.properties` and unrelated stale composer contract markers; template-required checks passed).
  - commit message: `agent-admin-v0: implement frontend surfaces`

### TASK-AGENTADMIN-99-001: Verify Agent Admin Workstream v0 completion

- status: done
- source: mini-project verification loop
- task brief: specs/agent-admin-workstream-v0/tasks/99-verification/01-verify-agent-admin-workstream-v0.md
- depends on:
  - TASK-AGENTADMIN-01-001
  - TASK-AGENTADMIN-02-001
  - TASK-AGENTADMIN-03-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/agent-admin-workstream-v0/README.md
  - specs/agent-admin-workstream-v0/conversation-capture.md
  - specs/agent-admin-workstream-v0/pending-tasks.md
  - specs/agent-admin-workstream-v0/sprints/*.md
  - specs/agent-admin-workstream-v0/backlog/*.md
  - specs/agent-admin-workstream-v0/tasks/**/*.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/agent-admin-workstream-v0/pending-tasks.md
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
  - verification result: incomplete; appended TASK-AGENTADMIN-04-001 and TASK-AGENTADMIN-99-002 because fullstack starter validation failed in `AgentBehaviorSeedLoaderTest.allFiveCoreAgentsResolveThroughSameManagedRuntimePathWithDistinctProfiles` for `agent-agent-admin` (`expected ALLOWED but was DENIED`).
  - checks: `tools/validate-ai-first-saas-starter-fullstack.sh` failed as expected for the discovered validation gap; `git diff --check` passed.
  - commit message: `agent-admin-v0: verify workstream completion`

### TASK-AGENTADMIN-04-001: Fix Agent Admin managed runtime validation gap

- status: done
- source: TASK-AGENTADMIN-99-001 verification finding
- task brief: specs/agent-admin-workstream-v0/tasks/04-validation/01-fix-agent-admin-managed-runtime-validation.md
- depends on:
  - TASK-AGENTADMIN-99-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
  - specs/agent-admin-workstream-v0/tasks/04-validation/01-fix-agent-admin-managed-runtime-validation.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoaderTest.java
- skills:
  - akka-agents
  - akka-agent-tool-boundaries
- expected outputs:
  - focused starter template/backend test or runtime authorization mapping fix
  - updated specs/agent-admin-workstream-v0/pending-tasks.md
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `git diff --check`
- done criteria:
  - fullstack starter validation passes
  - Agent Admin managed runtime preparation remains authorized only when the AuthContext has `agent_admin.submit_turn`
  - no ToolPermissionBoundary, provider fail-closed, AuthContext, tenant-isolation, or trace requirement is weakened
  - task changes and queue update are committed
- notes:
  - verification evidence: rendered-starter validation failed on `agent-agent-admin` runtime preparation because the shared test AuthContext did not include the per-agent Agent Admin invocation capability expected by `AgentRuntimeService.invocationCapability(...)`.
  - fix: updated the managed-runtime seed test to use per-agent invocation capabilities and added explicit denial/allowance coverage proving Agent Admin requires `agent_admin.submit_turn` rather than the generic invoke capability.
  - checks: `tools/validate-ai-first-saas-starter-fullstack.sh`; `git diff --check`
  - commit message: `agent-admin-v0: fix managed runtime validation`

### TASK-AGENTADMIN-99-002: Verify Agent Admin Workstream v0 completion after validation fix

- status: done
- source: mini-project verification loop after TASK-AGENTADMIN-04-001
- task brief: specs/agent-admin-workstream-v0/tasks/99-verification/01-verify-agent-admin-workstream-v0.md
- depends on:
  - TASK-AGENTADMIN-04-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
  - specs/five-core-workstreams-v0-plan/workstream-dependency-map.md
  - specs/agent-admin-workstream-v0/README.md
  - specs/agent-admin-workstream-v0/conversation-capture.md
  - specs/agent-admin-workstream-v0/pending-tasks.md
  - specs/agent-admin-workstream-v0/sprints/*.md
  - specs/agent-admin-workstream-v0/backlog/*.md
  - specs/agent-admin-workstream-v0/tasks/**/*.md
  - specs/agent-admin-workstream-v0/workstream-contract.md
  - specs/agent-admin-workstream-v0/capability-inventory.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/agent-admin-workstream-v0/pending-tasks.md
  - completion summary, verification notes, or newly appended follow-up tasks
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `git diff --check`
- done criteria:
  - task group goals have been compared against completed work
  - mini-project done state has been compared against completed work
  - runtime/API/UI validation evidence or blockers are recorded
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - verification result: complete; fullstack starter validation passed after the managed-runtime validation fix, with backend tests, frontend tests/typecheck/build, static secret scan, and real model provider smoke passing through the rendered starter validation path.
  - no new required follow-up tasks were appended; the Agent Admin Workstream v0 mini-project is complete at the stated scope.
  - checks: `tools/validate-ai-first-saas-starter-fullstack.sh`; `git diff --check`
  - commit message: `agent-admin-v0: verify workstream completion`
