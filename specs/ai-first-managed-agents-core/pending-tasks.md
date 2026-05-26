# Pending Tasks: AI-first Managed Agents Core

## Queue rules

- Execute one task per fresh harness session.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Treat this repository as the skills-pack source. Most implementation changes target `templates/ai-first-saas-starter/` plus supporting docs/skills/tests.
- Normal runtime behavior for any generated AI-first SaaS workstream agent must use the configuration-driven managed-agent path and a concrete Akka `Agent` component.
- Tool availability must be resolved from active managed configuration and passed to the Akka Agent with `effects().tools(runtimeTools)`; ad hoc static tool registration is allowed only in explicitly non-managed examples/tests.
- Stable tool ids and capability ids are stored in governed records. Do not store or execute arbitrary Java class names from tenant-managed configuration.
- Prompt, skill, and reference text are behavior guidance only; they never grant tools, data access, tenant/customer scope, roles, approval bypass, or autonomous side effects.
- Update this file before finishing the harness response: set completed tasks to `done`, add a completion note, and add discovered follow-up tasks rather than expanding the current task.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes.
- Commit message format: `managed-agents-core: <short task title>`.

## Tasks

### TASK-MAGENT-00-001: Create managed agents core migration queue

- status: done
- completion note: Created the migration scaffold and self-sufficient task queue to make configuration-driven agents a core generated-app runtime feature.
- source: user request to implement configuration-driven agents as part of the core app rather than deferring runtime substrate to Agent Admin
- task brief: specs/ai-first-managed-agents-core/tasks/00-planning/00-create-managed-agents-core-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-runtime-invocation-pattern.md
  - specs/governed-runtime-agent-foundation/pending-tasks.md
  - specs/workstream-akka-agent-runtime/pending-tasks.md
  - specs/core-app-full-stack-readiness/pending-tasks.md
- expected outputs:
  - specs/ai-first-managed-agents-core/README.md
  - specs/ai-first-managed-agents-core/conversation-capture.md
  - specs/ai-first-managed-agents-core/pending-tasks.md
  - specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
  - specs/ai-first-managed-agents-core/sprints/*.md
  - specs/ai-first-managed-agents-core/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `rg -n "TASK-MAGENT|configuration-driven|managed agents|effects\(\)\.tools|ToolPermissionBoundary|readSkill|readReferenceDoc" specs/ai-first-managed-agents-core`
- done criteria:
  - Queue exists with self-sufficient tasks for fresh harness sessions.
  - Each task requires a focused git commit before being marked done.
  - This planning task is committed with message `managed-agents-core: add migration queue`.
- notes:
  - commit message: `managed-agents-core: add migration queue`

### TASK-MAGENT-01-001: Add starter tool catalog and runtime resolver

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/01-runtime-tools/01-add-tool-catalog-and-runtime-resolver.md
- depends on: [TASK-MAGENT-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-runtime-invocation-pattern.md
  - docs/capability-first-backend-architecture.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/ToolPermissionBoundary.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
- expected outputs:
  - Add starter domain/application types for a backend-owned tool catalog/registry with stable tool ids, categories, capability ids, side-effect level, and implementation binding metadata.
  - Add `AgentRuntimeToolResolver` or equivalent that reads an active `ToolPermissionBoundary` and resolves only approved registry bindings into `List<Object>` for Akka `effects().tools(...)`.
  - Include initial registry entries for `readSkill` and `readReferenceDoc`; optional placeholder entries for safe core read tools may be added only if tests cover denial/default behavior.
  - Do not store arbitrary Java class names from tenant-managed records.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "ToolCatalog|ToolRegistry|AgentRuntimeToolResolver|List<Object>|stable tool|toolId|readSkill|readReferenceDoc" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Runtime tool resolution is deterministic, deny-by-default, and maps governed stable ids to hardcoded/backend-owned Java bindings.
  - A focused git commit exists with message `managed-agents-core: add runtime tool resolver`.
- notes: []

### TASK-MAGENT-01-002: Add governed Akka function tools for skill and reference loading

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/01-runtime-tools/02-add-governed-loader-function-tools.md
- depends on: [TASK-MAGENT-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-reference-governance/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - akka-context/sdk/agents/extending.html.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java
- expected outputs:
  - Add request-scoped starter tool class, for example `AgentRuntimeLoaderTools`, with public `@FunctionTool` methods `readSkill(skillId)` and `readReferenceDoc(referenceId)`.
  - The tool methods must delegate to existing governed authorization paths in `AgentRuntimeService` or a factored authorizer, preserving manifest checks, boundary checks, token/secret checks, and trace creation.
  - Tool results must be safe model-readable strings or DTOs with authority notes; denied loads must not enumerate hidden cross-tenant resources.
  - Add unit tests that reflectively verify `@FunctionTool` annotations and exercise allowed/denied loader calls through the tool class.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "@FunctionTool|readSkill|readReferenceDoc|AgentRuntimeLoaderTools|SkillLoadTrace|ReferenceLoadTrace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - `readSkill` and `readReferenceDoc` are real Akka function tools available to the runtime tool resolver.
  - A focused git commit exists with message `managed-agents-core: add governed loader tools`.
- notes: []

### TASK-MAGENT-01-003: Wire WorkstreamRuntimeAgent to effects().tools(runtimeTools)

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/01-runtime-tools/03-wire-workstream-agent-runtime-tools.md
- depends on: [TASK-MAGENT-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-component/SKILL.md
  - skills/akka-agent-tools/SKILL.md
  - akka-context/sdk/agents/extending.html.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgent.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DefaultWorkstreamAgentRuntimeInvoker.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- expected outputs:
  - Update the starter workstream Akka Agent so normal runtime invocation resolves a runtime tool list from the governed request context and calls `.tools(runtimeTools)` before `.userMessage(...)`.
  - Keep serialized `ComponentClient` request DTOs free of Java tool instances; pass safe ids/context needed for the agent-side resolver to construct request-scoped tool objects.
  - Validate missing or inconsistent runtime tool context fail closed before model invocation.
  - Preserve model provider alias secret-boundary checks and structured markdown response behavior.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "\.tools\(|runtimeTools|AgentRuntimeToolResolver|GovernedWorkstreamRequest|modelProviderAlias|WorkstreamRuntimeAgent" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Workstream runtime agents use active managed configuration for both prompt assembly and Akka tool registration.
  - A focused git commit exists with message `managed-agents-core: wire runtime tools into agent`.
- notes: []

### TASK-MAGENT-01-004: Prove model-invoked governed tool calls through Akka Agent path

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/01-runtime-tools/04-test-model-invoked-governed-tools.md
- depends on: [TASK-MAGENT-01-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-testing/SKILL.md
  - akka-context/sdk/agents/testing.html.md
  - akka-context/sdk/agents/extending.html.md
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgentTest.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java
- expected outputs:
  - Add TestKit/TestModelProvider tests where the model requests `readSkill` and/or `readReferenceDoc` during `WorkstreamRuntimeAgent` execution.
  - Assert assigned active loader calls return governed content and emit load traces.
  - Assert unassigned or boundary-denied loader calls return safe denials and emit denial traces.
  - Assert initial prompt contains compact manifest entries but not full skill/reference bodies.
  - Add a regression guard that fails if `WorkstreamRuntimeAgent` no longer registers runtime tools.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "readSkill|readReferenceDoc|tool call|FunctionTool|runtime tools|compact manifest|SkillLoadTrace|ReferenceLoadTrace" templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/backend/src/main/java`
- done criteria:
  - Tests prove the real Akka Agent tool loop, not only direct service method calls.
  - A focused git commit exists with message `managed-agents-core: test governed tool calls`.
- notes: []

### TASK-MAGENT-02-001: Add first-class AgentDefinition component and views

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/02-durable-state/01-add-agent-definition-component-and-views.md
- depends on: [TASK-MAGENT-01-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-behavior-profiles/SKILL.md
  - skills/akka-event-sourced-entities/SKILL.md
  - skills/akka-views/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentDefinition.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DurableAgentBehaviorRepositoryEntity.java
- expected outputs:
  - Add `AgentDefinitionEntity` or equivalent first-class Akka component for tenant-scoped agent lifecycle/profile state.
  - Add views for runtime lookup, Agent Admin catalog/detail, lifecycle filters, and workstream placement.
  - Keep `AgentBehaviorRepository` compatibility or add an adapter so current runtime services can read from the new component path.
  - Tests must cover active lookup, disabled/archived denial data, tenant isolation, and view projection shape.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "AgentDefinitionEntity|AgentDefinition.*View|agent catalog|lifecycleStatus|functionalAgent|tenant" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Agent definitions are no longer represented only as fields in a single catch-all repository seam.
  - A focused git commit exists with message `managed-agents-core: add agent definition component`.
- notes: []

### TASK-MAGENT-02-002: Add prompt, skill, and reference document/version components

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/02-durable-state/02-add-governed-document-components.md
- depends on: [TASK-MAGENT-02-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-governed-documents/SKILL.md
  - skills/akka-agent-prompt-governance/SKILL.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-reference-governance/SKILL.md
  - skills/akka-event-sourced-entities/SKILL.md
  - skills/akka-key-value-entities/SKILL.md
  - skills/akka-views/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/PromptDocument.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/SkillDocument.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/ReferenceDocument.java
- expected outputs:
  - Add first-class components for governed prompt, skill, and reference document lifecycle and active version snapshots.
  - Add views for document catalog, active runtime lookup, version history, and assigned-agent usage where practical for starter scope.
  - Preserve seed import idempotency and tenant customization behavior.
  - Tests cover activation, active lookup, cross-tenant denial, secret-like content rejection, and version snapshot/read behavior.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "PromptDocumentEntity|SkillDocumentEntity|ReferenceDocumentEntity|PromptVersion|SkillVersion|ReferenceVersion|activeVersion|Document.*View" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Prompt/skill/reference runtime records have first-class Akka-owned carriers suitable for Agent Admin governance.
  - A focused git commit exists with message `managed-agents-core: add governed document components`.
- notes: []

### TASK-MAGENT-02-003: Add manifest and tool-boundary components and views

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/02-durable-state/03-add-manifest-tool-boundary-components.md
- depends on: [TASK-MAGENT-02-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-skill-governance/SKILL.md
  - skills/akka-agent-reference-governance/SKILL.md
  - skills/akka-agent-tool-boundaries/SKILL.md
  - skills/akka-event-sourced-entities/SKILL.md
  - skills/akka-views/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentSkillManifest.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentReferenceManifest.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/ToolPermissionBoundary.java
- expected outputs:
  - Add components for `AgentSkillManifest`, `AgentReferenceManifest`, and `ToolPermissionBoundary` lifecycle/current state.
  - Add views for per-agent compact manifest lookup, manifest detail, tool-boundary detail, grant search, and Agent Admin inspection.
  - Ensure authority expansion or new side-effecting grants are represented as approval-required/proposal-only where current starter scope supports it.
  - Tests cover assigned/unassigned lookup, missing grant denial, distinct read_skill/read_reference grants, tenant isolation, and compact rendering inputs.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "AgentSkillManifestEntity|AgentReferenceManifestEntity|ToolPermissionBoundaryEntity|ToolBoundary.*View|read_skill|read_reference|authority expansion" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Manifest and tool-boundary records are first-class core runtime configuration, not Agent Admin-only concepts.
  - A focused git commit exists with message `managed-agents-core: add manifest and boundary components`.
- notes: []

### TASK-MAGENT-02-004: Add durable agent runtime trace storage and views

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/02-durable-state/04-add-runtime-trace-storage-and-views.md
- depends on: [TASK-MAGENT-02-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agent-work-trace/SKILL.md
  - skills/ai-first-saas-audit-trace/SKILL.md
  - skills/akka-event-sourced-entities/SKILL.md
  - skills/akka-views/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentRuntimeTrace.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- expected outputs:
  - Add durable storage path for prompt assembly, skill load, reference load, tool invocation, model invocation, and agent work traces.
  - Add views for trace search/detail by tenant, agent, correlation/work trace id, trace type, decision, and timestamp.
  - Update `AgentRuntimeService` or trace sink to persist traces instead of keeping only process-local lists where the starter runtime path needs durable behavior.
  - Tests cover allowed and denied trace persistence, redaction/no-secret assertions, and query filtering.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "AgentRuntimeTraceEntity|AgentRuntimeTrace.*View|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|ToolInvocationTrace|AgentWorkTrace|trace search" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Agent runtime traces are durable/queryable core app facts for Audit/Trace and Agent Admin surfaces.
  - A focused git commit exists with message `managed-agents-core: add runtime trace storage`.
- notes: []

### TASK-MAGENT-03-001: Seed configuration-driven profiles for all five core agents

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/03-core-agents/01-seed-five-core-agent-profiles.md
- depends on: [TASK-MAGENT-01-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/minimum-ai-first-saas-app.md
  - docs/examples/core-ai-first-saas-input/03a-module-agent-workstream-runtime-bootstrap-prd.md
  - docs/examples/core-ai-first-saas-input/04-module-user-admin-prd.md
  - docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md
  - docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md
  - docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
  - templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/**
- expected outputs:
  - Seed distinct active managed runtime profiles for My Account, User Admin, Audit/Trace, Governance/Policy, and Agent Admin.
  - Each profile includes AgentDefinition, prompt document/version, model config ref, tool boundary, and agent-specific skill/reference manifests as applicable.
  - Ensure Agent Admin itself is configuration-driven before it provides management/editing surfaces.
  - Tests verify all five functional agent ids resolve through the same managed runtime path and do not share a generic catch-all manifest by accident.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "my.account|user.admin|audit|governance|policy|agent.admin|AgentDefinition|AgentSkillManifest|ToolPermissionBoundary" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/main/resources templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - The five core workstream agents are all seeded as configuration-driven managed agents.
  - A focused git commit exists with message `managed-agents-core: seed five core agents`.
- notes: []

### TASK-MAGENT-04-001: Update docs, skills, and validation gates for AI-first managed agents

- status: pending
- source: specs/ai-first-managed-agents-core/backlog/01-managed-agents-core-backlog.md
- task brief: specs/ai-first-managed-agents-core/tasks/04-docs-validation/01-update-managed-agents-docs-and-gates.md
- depends on: [TASK-MAGENT-01-004, TASK-MAGENT-03-001]
- required reads:
  - AGENTS.md
  - README.md
  - pack/AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/agent-runtime-invocation-pattern.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-coverage-matrix.md
  - templates/ai-first-saas-starter/README.md
  - tools/validate-ai-first-saas-starter-fullstack.sh
- expected outputs:
  - Update doctrine to name AI-first managed agents as a primary core app architecture feature alongside workstreams and structured surfaces.
  - Update starter README and validation gates so completion requires runtime config resolution, `.tools(runtimeTools)`, governed loader tools, and traces.
  - Update routing/skill guidance to make the managed runtime path mandatory for generated SaaS agents, while preserving static agent examples as reference-only or non-managed examples.
  - Add or update validation checks that detect missing `.tools(...)` registration in the starter workstream runtime agent.
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `git diff --check`
  - `rg -n "AI-first managed agents|configuration-driven|effects\(\)\.tools|runtimeTools|readSkill|readReferenceDoc|ToolPermissionBoundary|workstreams|surfaces" AGENTS.md pack/AGENTS.md README.md skills docs templates/ai-first-saas-starter tools`
- done criteria:
  - Pack guidance and starter validation enforce configuration-driven managed agents as a core generated-app feature.
  - A focused git commit exists with message `managed-agents-core: update docs and gates`.
- notes: []
