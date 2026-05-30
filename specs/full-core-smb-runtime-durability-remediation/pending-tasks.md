# Pending Tasks: Full-Core SMB Runtime Durability Remediation

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

### TASK-FCSMB-DUR-00-001: Create runtime durability remediation queue

- status: done
- source: user confirmed no-in-memory-normal-runtime release bar after source scan found in-memory runtime defaults
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/00-planning/00-create-runtime-durability-remediation-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - specs/full-core-smb-polish-release-readiness/release-handoff.md
  - specs/full-core-smb-polish-release-readiness/release-readiness-verification.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - specs/full-core-smb-runtime-durability-remediation/sprints/*.md
  - specs/full-core-smb-runtime-durability-remediation/backlog/*.md
  - specs/full-core-smb-runtime-durability-remediation/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add runtime durability remediation queue`

### TASK-FCSMB-DUR-01-001: Inspect in-memory and fixture runtime paths and define remediation map

- status: done
- source: specs/full-core-smb-runtime-durability-remediation/backlog/01-runtime-durability-remediation-backlog.md
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/01-inspect-runtime-durability-boundaries.md
- depends on: [TASK-FCSMB-DUR-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/sprints/01-runtime-durability-remediation-sprint.md
  - specs/full-core-smb-runtime-durability-remediation/backlog/01-runtime-durability-remediation-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-polish-release-readiness/release-handoff.md
  - specs/full-core-smb-polish-release-readiness/release-readiness-verification.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository source-boundary inspection task
- expected outputs:
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md with bounded remediation tasks
  - task briefs for next backend/frontend/docs remediation tasks
- required checks:
  - `git diff --check`
  - `rg -n "InMemory|in-memory|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'`
  - targeted `find` commands listing discovered in-memory classes, fixture clients, static generated assets, and docs claims
  - `rg -n "InMemory|fixture|demo|release|blocker|durable|fail closed|normal runtime|test-only" specs/full-core-smb-runtime-durability-remediation`
- done criteria:
  - all discovered in-memory/fixture/demo paths are classified by release impact
  - remediation tasks can run without guessing source paths or validation commands
  - release-readiness ship recommendation is superseded or explicitly blocked if needed
  - task changes and queue update are committed
- notes:
  - release-readiness handoff, verification, and starter README now mark the stronger no-in-memory-normal-runtime bar blocked until remediation completes
  - commit message: `full-core-smb: map runtime durability remediation`

### TASK-FCSMB-DUR-01-002: Remediate backend foundation runtime durability

- status: pending
- source: specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/02-remediate-backend-foundation-durability.md
- depends on: [TASK-FCSMB-DUR-01-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- skills:
  - none; focused repository source remediation task
- expected outputs:
  - backend source/tests for durable or fail-closed foundation runtime behavior
  - updated README/docs if runtime behavior changes
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
- required checks:
  - `git diff --check`
  - `rg -n "new InMemory(Identity|WorkstreamLog|AuditTrace|GovernancePolicy|AccessReviewTask)|InMemory(Identity|WorkstreamLog|AuditTrace|GovernancePolicy|AccessReviewTask)Repository" templates/ai-first-saas-starter/backend/src/main/java`
  - targeted rendered backend tests appropriate to changed paths
- done criteria:
  - normal generated backend runtime no longer silently relies on listed in-memory foundation repositories, or fails closed with actionable copy/traces where durable state is not yet implemented
  - test/local-demo use is explicitly named and gated
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: remediate backend foundation durability`

### TASK-FCSMB-DUR-01-003: Bind invitation, agent behavior, and runtime trace durable seams

- status: pending
- source: specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/03-bind-agent-invitation-durable-seams.md
- depends on: [TASK-FCSMB-DUR-01-002]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AkkaInvitationRepository.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/DurableInvitationRepositoryEntity.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AkkaAgentBehaviorRepository.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DurableAgentBehaviorRepositoryEntity.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- skills:
  - none; focused repository source remediation task
- expected outputs:
  - backend source/tests for durable invitation, agent behavior, and trace sink runtime wiring
  - updated README/docs if runtime behavior changes
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
- required checks:
  - `git diff --check`
  - `rg -n "new InMemory(Invitation|AgentBehavior|AgentRuntimeTrace)|InMemory(Invitation|AgentBehavior)Repository|InMemoryAgentRuntimeTraceSink" templates/ai-first-saas-starter/backend/src/main/java`
  - targeted rendered backend tests for invitation, agent behavior, prompt assembly, runtime trace sink, and provider fail-closed/smoke guards as applicable
- done criteria:
  - normal generated runtime does not wire in-memory invitation/agent behavior stores or in-memory trace sinks as completed defaults
  - governed Akka Agent runtime path and provider fail-closed behavior are preserved
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: bind durable agent and invitation seams`

### TASK-FCSMB-DUR-01-004: Gate frontend fixtures and refresh static assets

- status: pending
- source: specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/04-gate-frontend-fixtures-and-static-assets.md
- depends on: [TASK-FCSMB-DUR-01-003]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/frontend/src/main.tsx
  - frontend/src/main.tsx
  - templates/ai-first-saas-starter/frontend/src/frontend.contract.test.mjs
  - frontend/src/frontend.contract.test.mjs
- skills:
  - none; focused repository source remediation task
- expected outputs:
  - updated template frontend source/tests
  - updated root frontend mirror source/tests or documented no-sync rationale
  - refreshed or cleaned static resources
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
- required checks:
  - `git diff --check`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`
  - root frontend checks if root mirror changes: `cd frontend && npm test -- --run && npm run typecheck && npm run build`
  - `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|InMemory|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'`
- done criteria:
  - normal frontend runtime cannot render fixture bootstrap data or fixture clients unless explicit local/dev fixture mode is enabled
  - static resources match updated source or are intentionally cleaned
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: gate frontend fixtures`

### TASK-FCSMB-DUR-01-005: Validate durability remediation and update release handoff

- status: pending
- source: specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/05-validate-durability-and-update-release-handoff.md
- depends on:
  - TASK-FCSMB-DUR-01-002
  - TASK-FCSMB-DUR-01-003
  - TASK-FCSMB-DUR-01-004
- required reads:
  - AGENTS.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
  - specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - templates/ai-first-saas-starter/README.md
  - specs/full-core-smb-polish-release-readiness/release-handoff.md
  - specs/full-core-smb-polish-release-readiness/release-readiness-verification.md
- skills:
  - none; repository validation and release-doc task
- expected outputs:
  - updated validation/release docs
  - updated pending queue if remaining blockers exist
- required checks:
  - `git diff --check`
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `rg -n "InMemory|in-memory|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'`
  - `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|InMemory|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'`
- done criteria:
  - broad starter validation passes or remaining blockers are appended as bounded tasks before verification
  - release docs no longer recommend shipping under the stronger durability bar unless the bar is met
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: validate runtime durability remediation`

### TASK-FCSMB-DUR-99-001: Verify runtime durability remediation readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/99-verification/01-verify-runtime-durability-remediation.md
- depends on:
  - TASK-FCSMB-DUR-01-001
  - TASK-FCSMB-DUR-01-002
  - TASK-FCSMB-DUR-01-003
  - TASK-FCSMB-DUR-01-004
  - TASK-FCSMB-DUR-01-005
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - specs/full-core-smb-runtime-durability-remediation/sprints/*.md
  - specs/full-core-smb-runtime-durability-remediation/backlog/*.md
  - specs/full-core-smb-runtime-durability-remediation/tasks/**/*.md
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate remediation done state
- done criteria:
  - mini-project goals have been compared against completed work
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - if complete, release-readiness status is corrected and explicit
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify runtime durability remediation`
