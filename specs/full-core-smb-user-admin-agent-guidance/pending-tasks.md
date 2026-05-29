# Pending Tasks: Full-Core SMB UserAdminAgent Guidance

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `full-core-smb: <short task title>`.

## Tasks

### TASK-FCSMB-UAG-00-001: Create UserAdminAgent guidance queue

- status: done
- source: user approved next User Admin full-core slice after deterministic access-management foundation
- task brief: specs/full-core-smb-user-admin-agent-guidance/tasks/00-planning/00-create-user-admin-agent-guidance-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - specs/full-core-smb-user-admin-access-management/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-user-admin-agent-guidance/README.md
  - specs/full-core-smb-user-admin-agent-guidance/conversation-capture.md
  - specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md
  - specs/full-core-smb-user-admin-agent-guidance/sprints/*.md
  - specs/full-core-smb-user-admin-agent-guidance/backlog/*.md
  - specs/full-core-smb-user-admin-agent-guidance/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add user admin agent guidance queue`

### TASK-FCSMB-UAG-01-001: Inspect UserAdminAgent runtime boundaries and define guidance implementation map

- status: done
- source: specs/full-core-smb-user-admin-agent-guidance/backlog/01-user-admin-agent-guidance-backlog.md
- task brief: specs/full-core-smb-user-admin-agent-guidance/tasks/01-agent-guidance/01-inspect-agent-guidance-boundaries.md
- depends on: [TASK-FCSMB-UAG-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-agent-guidance/README.md
  - specs/full-core-smb-user-admin-agent-guidance/conversation-capture.md
  - specs/full-core-smb-user-admin-agent-guidance/sprints/01-user-admin-agent-guidance-sprint.md
  - specs/full-core-smb-user-admin-agent-guidance/backlog/01-user-admin-agent-guidance-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-agent-seed-documents/SKILL.md
- skills:
  - akka-agents
  - akka-agent-tools
  - akka-agent-tool-boundaries
  - akka-agent-seed-documents
- expected outputs:
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - updated specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md with bounded source-edit tasks
  - task briefs for the next backend/frontend implementation tasks
- required checks:
  - `git diff --check`
  - targeted `find`/`rg` commands proving discovered UserAdminAgent seed/runtime/tool/evidence source and test boundaries
  - `rg -n "UserAdminAgent|user-admin-system|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|system_message|no direct mutation" specs/full-core-smb-user-admin-agent-guidance`
- done criteria:
  - backend/frontend source-edit tasks can run without guessing source paths or validation commands
  - guidance scope is bounded to request/response evidence and safe next steps, not direct mutations
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map user admin agent guidance`
  - discovery: `userAdminEvidence.read` is seeded but denied by the current registry because no binding exists; backend task must add a read-only facade before UserAdminAgent can use deterministic evidence.
  - implementation map: specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md

### TASK-FCSMB-UAG-01-002: Implement backend UserAdminAgent guidance runtime and evidence tool

- status: pending
- source: specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- task brief: specs/full-core-smb-user-admin-agent-guidance/tasks/01-agent-guidance/02-implement-backend-user-admin-agent-guidance.md
- depends on: [TASK-FCSMB-UAG-01-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-agent-guidance/README.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
  - specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-agent-seed-documents/SKILL.md
- skills:
  - akka-agents
  - akka-agent-tools
  - akka-agent-tool-boundaries
  - akka-agent-seed-documents
- expected outputs:
  - updated backend agentfoundation/security source under templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/
  - updated User Admin seed resources under templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/ when needed
  - updated backend tests under templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ and/or application/security/
- required checks:
  - `cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest`
  - `rg -n "userAdminEvidence\\.read|UserAdminAgent|user-admin-system|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|system_message|no direct mutation" templates/ai-first-saas-starter/backend --glob '!**/target/**'`
  - `git diff --check`
- done criteria:
  - UserAdminAgent has a registered read-only evidence tool available through `effects().tools(runtimeTools)` when boundary-granted
  - evidence tool returns scoped, redacted deterministic evidence and cannot mutate access state
  - backend tests prove allowed, denied, no-mutation, trace, and provider-fail-closed behavior
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: implement user admin agent backend guidance`

### TASK-FCSMB-UAG-01-003: Implement frontend UserAdminAgent guidance and blocked-state rendering

- status: pending
- source: specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- task brief: specs/full-core-smb-user-admin-agent-guidance/tasks/01-agent-guidance/03-implement-frontend-user-admin-agent-guidance.md
- depends on: [TASK-FCSMB-UAG-01-002]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-agent-guidance/README.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; focused frontend source task using the implementation map
- expected outputs:
  - updated frontend workstream surface/stream rendering files under templates/ai-first-saas-starter/frontend/src/
  - updated frontend contract tests for UserAdminAgent guidance, provider-blocked/system-message states, no secret leakage, and trace links
- required checks:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-expertise.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/workstream-surfaces.contract.test.mjs`
  - `rg -n "UserAdminAgent|user-admin|markdown_response|system_message|blocked_provider_or_runtime|provider|trace|no direct mutation|readSkill|readReferenceDoc|userAdminEvidence" templates/ai-first-saas-starter/frontend/src`
  - `git diff --check`
- done criteria:
  - UserAdminAgent normal guidance and provider/runtime-blocked responses render with safe copy, trace links, and recovery steps
  - frontend tests cover blocked, trace-linked, no-secret, and no-direct-mutation expectations
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: implement user admin agent frontend guidance`

### TASK-FCSMB-UAG-01-004: Validate UserAdminAgent guidance runtime

- status: pending
- source: specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- task brief: specs/full-core-smb-user-admin-agent-guidance/tasks/01-agent-guidance/04-validate-user-admin-agent-guidance-runtime.md
- depends on: [TASK-FCSMB-UAG-01-003]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-user-admin-agent-guidance/README.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- skills:
  - none; repository validation task
- expected outputs:
  - updated specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md with validation notes, blocked status, or appended follow-up tasks if needed
  - optional validation notes in the mini-project if failures require explanation
- required checks:
  - `cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-expertise.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/workstream-surfaces.contract.test.mjs`
  - `rg -n "UserAdminAgent|user-admin-system|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|system_message|no direct mutation|userAdminEvidence\\.read" templates/ai-first-saas-starter --glob '!**/node_modules/**' --glob '!**/target/**'`
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `git diff --check`
- done criteria:
  - targeted checks pass and broad validation passes or a justified blocker is appended
  - provider-missing fail-closed behavior is validated; provider-configured smoke is run when environment exists
  - no direct mutation, unauthorized evidence, or secret leakage path is found
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: validate user admin agent guidance runtime`

### TASK-FCSMB-UAG-99-001: Verify UserAdminAgent guidance readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-user-admin-agent-guidance/tasks/99-verification/01-verify-user-admin-agent-guidance.md
- depends on:
  - TASK-FCSMB-UAG-01-004
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-user-admin-agent-guidance/README.md
  - specs/full-core-smb-user-admin-agent-guidance/conversation-capture.md
  - specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md
  - specs/full-core-smb-user-admin-agent-guidance/sprints/*.md
  - specs/full-core-smb-user-admin-agent-guidance/backlog/*.md
  - specs/full-core-smb-user-admin-agent-guidance/tasks/**/*.md
  - specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
  - specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate implementation-map/source-edit task readiness
- done criteria:
  - mini-project goals have been compared against completed work
  - if ready, next implementation task is runnable without guessing
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify user admin agent guidance readiness`
