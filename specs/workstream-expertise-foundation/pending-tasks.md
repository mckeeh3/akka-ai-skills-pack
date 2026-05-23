# Pending Tasks: Workstream Expertise Foundation

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- At the completion of every sprint, the sprint review task must identify remaining refinement areas and add more tasks/sprints when needed.

## Tasks

### TASK-WEF-01-001: Create workstream expertise doctrine

- status: done
- source: specs/workstream-expertise-foundation/backlog/01-doctrine-description-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/01-doctrine-description/01-create-workstream-expertise-doctrine.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/workstream-expertise-foundation/README.md
  - specs/workstream-expertise-foundation/conversation-capture.md
  - specs/workstream-expertise-foundation/sprints/01-doctrine-description-sprint.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
- skills:
  - app-description-functional-agent-modeling
  - akka-agent-skill-governance
- expected outputs:
  - docs/workstream-expertise-model.md
  - focused references from existing doctrine docs if needed
- required checks:
  - git diff --check
- done criteria:
  - doctrine defines a workstream expert bundle and distinguishes skills, references, capabilities, tools, boundaries, traces, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Add workstream expertise doctrine

### TASK-WEF-01-002: Add app-description ownership for workstream expertise

- status: done
- source: specs/workstream-expertise-foundation/backlog/01-doctrine-description-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/01-doctrine-description/02-app-description-expertise-ownership.md
- depends on: [TASK-WEF-01-001]
- required reads:
  - docs/workstream-expertise-model.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
  - skills/app-description-capability-modeling/SKILL.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
- skills:
  - app-descriptions
  - app-description-functional-agent-modeling
- expected outputs:
  - updated app-description architecture/guidance
  - optional example `12-workstreams/workstream-expertise/README.md` contract in seed app-description
- required checks:
  - git diff --check
- done criteria:
  - app-description layer has an explicit authoritative location and contract for per-workstream expertise
  - task changes and queue update are committed
- notes:
  - commit message: Add app description workstream expertise ownership

### TASK-WEF-01-003: Align readiness and traceability with expertise requirements

- status: done
- source: specs/workstream-expertise-foundation/backlog/01-doctrine-description-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/01-doctrine-description/03-readiness-traceability-expertise.md
- depends on: [TASK-WEF-01-002]
- required reads:
  - docs/workstream-expertise-model.md
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-description-change-impact/SKILL.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/functional-agent-to-capability-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/surface-to-capability-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md
- skills:
  - app-description-readiness-assessment
  - app-description-change-impact
- expected outputs:
  - updated readiness/change-impact guidance
  - seed traceability/test notes if needed
- required checks:
  - git diff --check
- done criteria:
  - functional-agent readiness blocks or explicitly defers missing expertise artifacts, manifests, boundaries, traces, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Require expertise in readiness checks

### TASK-WEF-01-004: Review doctrine and description sprint

- status: done
- source: specs/workstream-expertise-foundation/backlog/01-doctrine-description-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/01-doctrine-description/04-review-doctrine-description-sprint.md
- depends on: [TASK-WEF-01-003]
- required reads:
  - specs/workstream-expertise-foundation/README.md
  - specs/workstream-expertise-foundation/sprints/01-doctrine-description-sprint.md
  - specs/workstream-expertise-foundation/backlog/01-doctrine-description-build-backlog.md
  - docs/workstream-expertise-model.md
  - docs/agent-workstream-application-architecture.md
  - skills/app-description-functional-agent-modeling/SKILL.md
- skills:
  - app-descriptions
  - agent-workstream-apps
- expected outputs:
  - specs/workstream-expertise-foundation/sprint-01-review.md
  - pending task adjustments if needed
- required checks:
  - git diff --check
- done criteria:
  - sprint review records whether Sprint 02 can proceed and captures the reference-document governance decision or open concern
  - task changes and queue update are committed
- notes:
  - commit message: Review workstream expertise doctrine sprint
  - checks: `git diff --check` passed; text-search proof recorded in sprint review

### TASK-WEF-02-001: Audit governed runtime expertise gaps

- status: done
- source: specs/workstream-expertise-foundation/backlog/02-governed-runtime-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/02-governed-runtime/01-audit-runtime-expertise-gaps.md
- depends on: [TASK-WEF-01-004]
- required reads:
  - specs/workstream-expertise-foundation/sprint-01-review.md
  - docs/workstream-expertise-model.md
  - docs/agent-runtime-invocation-pattern.md
  - docs/agent-coverage-matrix.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-harness-skills/SKILL.md
  - skills/akka-agent-seed-documents/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
- skills:
  - akka-agent-skill-governance
  - akka-agent-tool-boundaries
- expected outputs:
  - specs/workstream-expertise-foundation/runtime-expertise-gap-matrix.md
- required checks:
  - git diff --check
- done criteria:
  - gap matrix identifies runtime changes needed for skill/reference manifests, loaders, boundaries, traces, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Audit runtime expertise gaps
  - checks: `git diff --check` passed

### TASK-WEF-02-002: Define governed reference-document loading pattern

- status: done
- source: specs/workstream-expertise-foundation/backlog/02-governed-runtime-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/02-governed-runtime/02-reference-document-loading-pattern.md
- depends on: [TASK-WEF-02-001]
- required reads:
  - specs/workstream-expertise-foundation/runtime-expertise-gap-matrix.md
  - docs/workstream-expertise-model.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-governed-documents/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-work-trace/SKILL.md
- skills:
  - akka-agent-governed-documents
  - akka-agent-skill-governance
  - akka-agent-tools
- expected outputs:
  - updated skill/governed-document guidance or a new focused reference-governance skill
  - updated doctrine if model terms change
- required checks:
  - git diff --check
- done criteria:
  - reference-document ids, manifests, loader authorization, denied loads, trace records, and relationship to SkillDocument are explicit
  - task changes and queue update are committed
- notes:
  - commit message: Define workstream reference loading pattern
  - checks: `git diff --check` passed; text-search proof covered ReferenceDocument, AgentReferenceManifest, readReferenceDoc, denied reference loads, read_reference, ReferenceLoadTrace, compact expertise manifest, and SkillDocument relationship

### TASK-WEF-02-003: Align runtime invocation, seed, and tool-boundary guidance

- status: done
- source: specs/workstream-expertise-foundation/backlog/02-governed-runtime-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/02-governed-runtime/03-align-runtime-seed-boundaries.md
- depends on: [TASK-WEF-02-002]
- required reads:
  - docs/workstream-expertise-model.md
  - docs/agent-runtime-invocation-pattern.md
  - skills/akka-agent-seed-documents/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
  - docs/ai-first-saas-application-architecture.md
- skills:
  - akka-agent-seed-documents
  - akka-agent-tool-boundaries
  - akka-agent-skill-governance
- expected outputs:
  - updated runtime invocation/seed/tool-boundary docs and skills
- required checks:
  - git diff --check
- done criteria:
  - runtime guidance includes compact expertise manifest assembly, authorized skill/reference loading, seed import, customization-preserving upgrades, and boundary enforcement
  - task changes and queue update are committed
- notes:
  - commit message: Align runtime expertise loading guidance
  - checks: `git diff --check` passed; text-search proof covered compact expertise manifest, authorized skill/reference loading, seed import/upgrades, `read_reference`, `ReferenceLoadTrace`, and boundary enforcement

### TASK-WEF-02-004: Update agent coverage and testing guidance

- status: done
- source: specs/workstream-expertise-foundation/backlog/02-governed-runtime-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/02-governed-runtime/04-update-coverage-testing-guidance.md
- depends on: [TASK-WEF-02-003]
- required reads:
  - docs/agent-coverage-matrix.md
  - skills/akka-agent-testing/SKILL.md
  - skills/akka-agent-work-trace/SKILL.md
  - docs/workstream-expertise-model.md
- skills:
  - akka-agent-testing
  - akka-agent-work-trace
- expected outputs:
  - updated coverage matrix and testing guidance
- required checks:
  - git diff --check
- done criteria:
  - tests cover assigned/unassigned skill and reference loads, trace emission, tool-boundary denial, and no authority expansion from text
  - task changes and queue update are committed
- notes:
  - commit message: Add expertise runtime test guidance
  - checks: `git diff --check` passed; text-search proof covered assigned/unassigned skill/reference loads, `ReferenceLoadTrace`, `read_reference` boundary denial, tool-boundary denial, and text-cannot-grant-authority

### TASK-WEF-02-005: Review governed runtime expertise sprint

- status: done
- source: specs/workstream-expertise-foundation/backlog/02-governed-runtime-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/02-governed-runtime/05-review-runtime-sprint.md
- depends on: [TASK-WEF-02-004]
- required reads:
  - specs/workstream-expertise-foundation/sprints/02-governed-runtime-sprint.md
  - specs/workstream-expertise-foundation/runtime-expertise-gap-matrix.md
  - docs/workstream-expertise-model.md
  - docs/agent-runtime-invocation-pattern.md
  - docs/agent-coverage-matrix.md
- skills:
  - akka-agent-skill-governance
  - akka-agent-testing
- expected outputs:
  - specs/workstream-expertise-foundation/sprint-02-review.md
  - pending task adjustments if needed
- required checks:
  - git diff --check
- done criteria:
  - sprint review confirms runtime guidance is ready for seed/example work or records blocking gaps
  - task changes and queue update are committed
- notes:
  - commit message: Review runtime expertise sprint
  - checks: `git diff --check` passed; text-search proof covered compact expertise manifest, `readReferenceDoc`, denied reference loads, `ReferenceLoadTrace`, separate `read_reference` grants, seed import, and no authority expansion from expertise text

### TASK-WEF-03-001: Model User Admin workstream expertise in seed app-description

- status: done
- source: specs/workstream-expertise-foundation/backlog/03-seed-example-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/03-seed-example/01-user-admin-expertise-description.md
- depends on: [TASK-WEF-02-005]
- required reads:
  - specs/workstream-expertise-foundation/sprint-02-review.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/05-managed-agent-foundation.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/functional-agent-to-capability-map.md
- skills:
  - app-description-functional-agent-modeling
  - app-description-capability-modeling
- expected outputs:
  - User Admin workstream expertise artifact under seed app-description
  - updated links from functional agent/capability/traceability files
- required checks:
  - git diff --check
- done criteria:
  - seed app-description makes User Admin expertise concrete with skills, references, capabilities, denials, traces, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Model User Admin workstream expertise
  - checks: `git diff --check` passed; text-search proof covered `user-admin-agent.expertise`, `SkillDocument`, `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, required denials, `ToolPermissionBoundary`, `ReferenceLoadTrace`, and User Admin test obligations

### TASK-WEF-03-002: Add User Admin expertise seed resources or fixtures

- status: done
- source: specs/workstream-expertise-foundation/backlog/03-seed-example-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/03-seed-example/02-user-admin-expertise-seeds.md
- depends on: [TASK-WEF-03-001]
- required reads:
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/user-admin-agent.md if present
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentBehaviorRepositoryState.java
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts
  - src/main/resources/agent-behavior-seeds/reference-v1/ if present
- skills:
  - akka-agent-seed-documents
  - akka-agent-skill-governance
- expected outputs:
  - starter/example seed resources or fixtures for User Admin expertise
  - manifest entries and checksum/provenance expectations where applicable
- required checks:
  - git diff --check
  - relevant starter/example tests if code or fixtures are touched
- done criteria:
  - canonical seed location includes default User Admin expertise content and manifest references without bypassing governed storage
  - task changes and queue update are committed
- notes:
  - commit message: Add User Admin expertise seed content
  - checks: `git diff --check` passed; `tools/validate-ai-first-saas-starter-fullstack.sh` passed

### TASK-WEF-03-003: Add User Admin expertise tests or contract checks

- status: done
- source: specs/workstream-expertise-foundation/backlog/03-seed-example-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/03-seed-example/03-user-admin-expertise-tests.md
- depends on: [TASK-WEF-03-002]
- required reads:
  - docs/workstream-expertise-model.md
  - docs/agent-coverage-matrix.md
  - templates/ai-first-saas-starter/frontend/src/workstream-agent-admin-vertical.contract.test.mjs
  - relevant backend/frontend tests for touched seed resources or fixtures
  - skills/akka-agent-testing/SKILL.md
- skills:
  - akka-agent-testing
  - akka-agent-work-trace
- expected outputs:
  - updated or new tests/contract checks for manifest display, authorized loads, denied loads, tool-boundary denial, and trace visibility
- required checks:
  - git diff --check
  - run targeted tests for changed code/fixtures or document docs-only limitation
- done criteria:
  - User Admin expert bundle has test or contract coverage appropriate to touched assets
  - task changes and queue update are committed
- notes:
  - commit message: Test User Admin expertise bundle
  - checks: `git diff --check` passed; `cd templates/ai-first-saas-starter/frontend && npm test -- --test-name-pattern='User Admin expertise|User Admin'` passed

### TASK-WEF-03-004: Review seed/example expertise sprint

- status: done
- source: specs/workstream-expertise-foundation/backlog/03-seed-example-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/03-seed-example/04-review-seed-example-sprint.md
- depends on: [TASK-WEF-03-003]
- required reads:
  - specs/workstream-expertise-foundation/sprints/03-seed-example-sprint.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/user-admin-agent.md if present
  - docs/agent-coverage-matrix.md
- skills:
  - app-description-readiness-assessment
  - akka-agent-testing
- expected outputs:
  - specs/workstream-expertise-foundation/sprint-03-review.md
  - pending task adjustments if needed
- required checks:
  - git diff --check
- done criteria:
  - review confirms User Admin is a sufficient canonical workstream expertise example or records remaining gaps
  - task changes and queue update are committed
- notes:
  - commit message: Review User Admin expertise example
  - checks: `git diff --check` passed
  - review outcome: Sprint 04 may proceed; no queue adjustments required before planning integration

### TASK-WEF-04-001: Align PRD/spec/backlog generation with expertise tasks

- status: done
- source: specs/workstream-expertise-foundation/backlog/04-planning-integration-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/04-planning-integration/01-align-prd-backlog-expertise.md
- depends on: [TASK-WEF-03-004]
- required reads:
  - specs/workstream-expertise-foundation/sprint-03-review.md
  - docs/workstream-expertise-model.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/module-sprint-planning.md
  - docs/pending-task-queue.md
  - docs/solution-plan-to-implementation-queue.md
- skills:
  - akka-prd-to-specs-backlog
- expected outputs:
  - updated planning/backlog/queue guidance
- required checks:
  - git diff --check
- done criteria:
  - generated plans create explicit tasks for expert bundle, prompts, skills, references, manifests, boundaries, loaders, UI/governance, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Add workstream expertise planning tasks
  - checks: `git diff --check` passed; text-search proof covered workstream expertise tasks, expert bundle, `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, `ReferenceLoadTrace`, self-contained fresh-session execution, and vague `make the agent expert`/`agent governance` anti-patterns

### TASK-WEF-04-002: Align change-impact and iterative update guidance

- status: done
- source: specs/workstream-expertise-foundation/backlog/04-planning-integration-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/04-planning-integration/02-align-change-impact-expertise.md
- depends on: [TASK-WEF-04-001]
- required reads:
  - docs/workstream-expertise-model.md
  - skills/app-description-change-impact/SKILL.md
  - skills/akka-change-request-to-spec-update/SKILL.md
  - skills/akka-revised-prd-reconciliation/SKILL.md
  - docs/app-description-maintenance-flow.md
- skills:
  - app-description-change-impact
  - akka-change-request-to-spec-update
- expected outputs:
  - updated change-impact and iterative planning guidance
- required checks:
  - git diff --check
- done criteria:
  - changes to workstream expertise propagate to behavior, governance, capabilities, auth/security, observability, UI, generation, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Align expertise change impact guidance
  - checks: `git diff --check` passed; text-search proof covered workstream expertise propagation across behavior, governance, capabilities, auth/security, observability, UI/governance, generation/seed assets, tests, queue blocking/decomposition, `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, `ReferenceLoadTrace`, and `ToolPermissionBoundary`

### TASK-WEF-04-003: Review planning integration with sample domain workstream

- status: done
- source: specs/workstream-expertise-foundation/backlog/04-planning-integration-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/04-planning-integration/03-review-planning-sample.md
- depends on: [TASK-WEF-04-002]
- required reads:
  - specs/workstream-expertise-foundation/sprints/04-planning-integration-sprint.md
  - docs/workstream-expertise-model.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/pending-task-queue.md
- skills:
  - akka-prd-to-specs-backlog
- expected outputs:
  - specs/workstream-expertise-foundation/sprint-04-review.md
  - optional sample generated task outline for a hypothetical domain workstream
  - pending task adjustments if needed
- required checks:
  - git diff --check
- done criteria:
  - review proves planning guidance can create self-contained expertise tasks for a new domain workstream
  - task changes and queue update are committed
- notes:
  - commit message: Review expertise planning integration
  - checks: `git diff --check` passed; text-search proof covered workstream expertise tasks, self-contained fresh-session execution, `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, `ReferenceLoadTrace`, and vague `make the agent expert` / `agent governance` rejection
  - review outcome: Sprint 05 may proceed; no queue adjustments required before final consistency review

### TASK-WEF-05-001: Run final workstream expertise consistency review

- status: done
- source: specs/workstream-expertise-foundation/backlog/05-review-hardening-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/05-review-hardening/01-final-consistency-review.md
- depends on: [TASK-WEF-04-003]
- required reads:
  - specs/workstream-expertise-foundation/README.md
  - docs/workstream-expertise-model.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/agent-coverage-matrix.md
  - skills/akka-agents/SKILL.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/app-description-readiness-assessment/SKILL.md
- skills:
  - agent-workstream-apps
  - akka-agents
  - app-description-readiness-assessment
- expected outputs:
  - specs/workstream-expertise-foundation/final-consistency-review.md
- required checks:
  - git diff --check
  - repository text search for stale prompt-only or generic-chatbot workstream-agent readiness paths
- done criteria:
  - final review identifies remaining gaps and recommends final cleanup or next sprint
  - task changes and queue update are committed
- notes:
  - commit message: Review workstream expertise consistency
  - checks: `git diff --check` passed; repository text search found no prompt-only, generic-chatbot, page-first, or CRUD-first readiness path that bypasses workstream expertise
  - review outcome: proceed to TASK-WEF-05-002 for small cleanup of stale skill-only governed-runtime summaries; no new sprint recommended yet

### TASK-WEF-05-002: Apply final cleanup or add next sprint

- status: done
- source: specs/workstream-expertise-foundation/backlog/05-review-hardening-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/05-review-hardening/02-final-cleanup-or-next-sprint.md
- depends on: [TASK-WEF-05-001]
- required reads:
  - specs/workstream-expertise-foundation/final-consistency-review.md
  - specs/workstream-expertise-foundation/README.md
  - docs/workstream-expertise-model.md
  - files identified by the final consistency review
- skills:
  - agent-workstream-apps
  - akka-agents
- expected outputs:
  - smallest final cleanup edits or new sprint/backlog/task files if material gaps remain
  - updated pending queue
- required checks:
  - git diff --check
- done criteria:
  - plan is either complete or has a justified next sprint with runnable tasks
  - task changes and queue update are committed
- notes:
  - commit message: Finalize workstream expertise plan
  - checks: `git diff --check` passed; cleanup aligned stale full-core/foundation summaries with `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, and `ReferenceLoadTrace`
  - outcome: workstream expertise foundation plan complete; no next sprint added

### TASK-WEF-06-001: Add reference domain state and seed import

- status: done
- source: specs/workstream-expertise-foundation/backlog/06-executable-reference-coverage-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/06-executable-reference-coverage/01-reference-domain-seed-state.md
- depends on: [TASK-WEF-05-002]
- required reads:
  - specs/workstream-expertise-foundation/post-completion-expertise-review.md
  - specs/workstream-expertise-foundation/sprints/06-executable-reference-coverage-sprint.md
  - docs/workstream-expertise-model.md
  - skills/akka-agent-reference-governance/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentDefinition.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentBehaviorRepositoryState.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
  - templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/user-admin-agent-expertise.yaml
- skills:
  - akka-agent-reference-governance
  - akka-agent-seed-documents
- expected outputs:
  - starter backend `ReferenceDocument` and `AgentReferenceManifest` domain state or explicitly named interim equivalents
  - seed loader imports User Admin references as governed records
- required checks:
  - git diff --check
  - targeted starter backend seed-loader tests
- done criteria:
  - reference seed resources become governed tenant-scoped state with provenance, checksums, idempotency, and manifest assignment
  - task changes and queue update are committed
- notes:
  - commit message: Add starter reference governance state
  - checks: `git diff --check` passed; scaffolded targeted backend `mvn -Dtest=AgentBehaviorSeedLoaderTest,DurableAgentBehaviorRepositoryStateTest test` passed
  - validation note: full `tools/validate-ai-first-saas-starter-fullstack.sh` backend phase passed, but frontend fixture phase failed on pre-existing missing scaffolded `backend/src/main/resources/agent-behavior-seeds/starter-v1/manifest.properties` path assumption

### TASK-WEF-06-002: Implement runtime reference manifest and loader

- status: pending
- source: specs/workstream-expertise-foundation/backlog/06-executable-reference-coverage-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/06-executable-reference-coverage/02-runtime-reference-loader.md
- depends on: [TASK-WEF-06-001]
- required reads:
  - specs/workstream-expertise-foundation/post-completion-expertise-review.md
  - docs/agent-runtime-invocation-pattern.md
  - skills/akka-agent-reference-governance/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/ToolPermissionBoundary.java
- skills:
  - akka-agent-reference-governance
  - akka-agent-tool-boundaries
- expected outputs:
  - runtime compact reference manifest assembly
  - `readReferenceDoc(referenceId)` request/result path with safe denials and trace emission
- required checks:
  - git diff --check
  - targeted starter backend runtime tests
- done criteria:
  - runtime enforces active agent, active reference manifest assignment, active reference document/version, token/secret/redaction checks, mode, and separate `READ_REFERENCE` boundary grant
  - task changes and queue update are committed
- notes:
  - commit message: Implement starter readReferenceDoc runtime

### TASK-WEF-06-003: Add backend reference governance tests

- status: pending
- source: specs/workstream-expertise-foundation/backlog/06-executable-reference-coverage-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/06-executable-reference-coverage/03-reference-runtime-tests.md
- depends on: [TASK-WEF-06-002]
- required reads:
  - docs/workstream-expertise-model.md
  - docs/agent-coverage-matrix.md
  - skills/akka-agent-testing/SKILL.md
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoaderTest.java
- skills:
  - akka-agent-testing
  - akka-agent-reference-governance
- expected outputs:
  - backend tests for compact reference manifest, assigned reference success, unassigned denial, missing `read_reference` boundary denial, disabled-agent denial, and trace emission
- required checks:
  - git diff --check
  - targeted starter backend tests for agentfoundation package
- done criteria:
  - backend tests prove the reference path without relying only on frontend fixture string checks
  - task changes and queue update are committed
- notes:
  - commit message: Test starter reference governance runtime

### TASK-WEF-06-004: Update reference coverage docs and validation

- status: pending
- source: specs/workstream-expertise-foundation/backlog/06-executable-reference-coverage-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/06-executable-reference-coverage/04-update-reference-coverage-docs.md
- depends on: [TASK-WEF-06-003]
- required reads:
  - docs/agent-coverage-matrix.md
  - specs/workstream-expertise-foundation/post-completion-expertise-review.md
  - templates/ai-first-saas-starter/README.md
  - tools/validate-ai-first-saas-starter-fullstack.sh
- skills:
  - akka-agent-reference-governance
  - akka-agent-testing
- expected outputs:
  - coverage matrix updated to reflect executable first-class reference coverage
  - starter validation/docs updated if needed
- required checks:
  - git diff --check
  - relevant validation command if script/docs changed
- done criteria:
  - coverage docs no longer describe first-class starter reference coverage as missing after tests exist
  - task changes and queue update are committed
- notes:
  - commit message: Update reference governance coverage docs

### TASK-WEF-06-005: Review executable reference coverage sprint

- status: pending
- source: specs/workstream-expertise-foundation/backlog/06-executable-reference-coverage-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/06-executable-reference-coverage/05-review-reference-coverage-sprint.md
- depends on: [TASK-WEF-06-004]
- required reads:
  - specs/workstream-expertise-foundation/sprints/06-executable-reference-coverage-sprint.md
  - specs/workstream-expertise-foundation/backlog/06-executable-reference-coverage-build-backlog.md
  - specs/workstream-expertise-foundation/post-completion-expertise-review.md
  - docs/agent-coverage-matrix.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java
- skills:
  - akka-agent-reference-governance
  - akka-agent-testing
- expected outputs:
  - specs/workstream-expertise-foundation/sprint-06-review.md
  - pending task adjustments if needed
- required checks:
  - git diff --check
- done criteria:
  - review confirms executable reference-governance coverage is complete or records remaining gaps
  - task changes and queue update are committed
- notes:
  - commit message: Review executable reference coverage sprint

### TASK-WEF-07-001: Audit foundation functional-agent expertise coverage

- status: pending
- source: specs/workstream-expertise-foundation/backlog/07-foundation-expertise-expansion-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/07-foundation-expertise-expansion/01-audit-foundation-expertise-coverage.md
- depends on: [TASK-WEF-06-005]
- required reads:
  - specs/workstream-expertise-foundation/post-completion-expertise-review.md
  - specs/workstream-expertise-foundation/sprints/07-foundation-expertise-expansion-sprint.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/README.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/user-admin-agent.md
- skills:
  - app-description-functional-agent-modeling
  - app-description-readiness-assessment
- expected outputs:
  - specs/workstream-expertise-foundation/foundation-expertise-coverage-audit.md
- required checks:
  - git diff --check
- done criteria:
  - audit lists every foundation functional agent and whether it has detailed bundle, needs bundle, or has explicit deferral/non-LLM status
  - task changes and queue update are committed
- notes:
  - commit message: Audit foundation expertise coverage

### TASK-WEF-07-002: Add Agent Admin expert bundle

- status: pending
- source: specs/workstream-expertise-foundation/backlog/07-foundation-expertise-expansion-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/07-foundation-expertise-expansion/02-agent-admin-expert-bundle.md
- depends on: [TASK-WEF-07-001]
- required reads:
  - specs/workstream-expertise-foundation/foundation-expertise-coverage-audit.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/05-managed-agent-foundation.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/07-agent-governance-center.md
  - skills/akka-agent-reference-governance/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
- skills:
  - app-description-functional-agent-modeling
  - akka-agent-skill-governance
  - akka-agent-reference-governance
- expected outputs:
  - `agent-admin-agent` workstream expertise artifact
  - updated functional-agent/traceability/test links as needed
- required checks:
  - git diff --check
- done criteria:
  - Agent Admin has a concrete expert bundle covering governed behavior artifacts, proposals, approvals, denials, traces, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Add Agent Admin expert bundle

### TASK-WEF-07-003: Add Audit/Trace expert bundle

- status: pending
- source: specs/workstream-expertise-foundation/backlog/07-foundation-expertise-expansion-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/07-foundation-expertise-expansion/03-audit-trace-expert-bundle.md
- depends on: [TASK-WEF-07-002]
- required reads:
  - specs/workstream-expertise-foundation/foundation-expertise-coverage-audit.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/03-governance-decisions-and-audit.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/06-audit-trace-explorer.md
  - skills/ai-first-saas-audit-trace/SKILL.md
  - skills/akka-agent-reference-governance/SKILL.md
- skills:
  - app-description-functional-agent-modeling
  - ai-first-saas-audit-trace
  - akka-agent-reference-governance
- expected outputs:
  - `audit-trace-agent` workstream expertise artifact
  - updated functional-agent/traceability/test links as needed
- required checks:
  - git diff --check
- done criteria:
  - Audit/Trace has a concrete expert bundle covering trace search/explanation, redaction, evidence, export limits, denials, and tests
  - task changes and queue update are committed
- notes:
  - commit message: Add Audit Trace expert bundle

### TASK-WEF-07-004: Add remaining foundation bundles or explicit deferrals

- status: pending
- source: specs/workstream-expertise-foundation/backlog/07-foundation-expertise-expansion-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/07-foundation-expertise-expansion/04-remaining-foundation-bundles-or-deferrals.md
- depends on: [TASK-WEF-07-003]
- required reads:
  - specs/workstream-expertise-foundation/foundation-expertise-coverage-audit.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/15-operating-model/policies-and-approval-gates.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/15-operating-model/goals-and-objectives.md
- skills:
  - app-description-functional-agent-modeling
  - app-description-readiness-assessment
- expected outputs:
  - initial bundles or explicit deferrals/non-LLM status for Governance/Policy, Mission Control, and My Account
- required checks:
  - git diff --check
- done criteria:
  - every seed foundation functional agent has a matching expertise artifact or explicit readiness-impacting deferral/non-LLM note
  - task changes and queue update are committed
- notes:
  - commit message: Add remaining foundation expertise statuses

### TASK-WEF-07-005: Update traceability/readiness and review foundation expertise

- status: pending
- source: specs/workstream-expertise-foundation/backlog/07-foundation-expertise-expansion-build-backlog.md
- task brief: specs/workstream-expertise-foundation/tasks/07-foundation-expertise-expansion/05-traceability-readiness-review.md
- depends on: [TASK-WEF-07-004]
- required reads:
  - specs/workstream-expertise-foundation/foundation-expertise-coverage-audit.md
  - docs/workstream-expertise-model.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/functional-agent-to-capability-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/surface-to-capability-map.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md
  - skills/app-description-readiness-assessment/SKILL.md
- skills:
  - app-description-readiness-assessment
  - app-description-change-impact
- expected outputs:
  - updated traceability/readiness/test links
  - specs/workstream-expertise-foundation/sprint-07-review.md
  - pending task adjustments if needed
- required checks:
  - git diff --check
  - text search proving each foundation functional agent has bundle or explicit deferral status
- done criteria:
  - review confirms foundation workstream expertise coverage is complete enough or creates further tasks
  - task changes and queue update are committed
- notes:
  - commit message: Review foundation expertise expansion
