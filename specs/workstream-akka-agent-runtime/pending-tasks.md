# Pending Tasks: Workstream Akka Agent Runtime

## Queue rules

- Execute one task per fresh harness session.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Treat this repository as the skills-pack source; most implementation changes target `templates/ai-first-saas-starter/` and supporting tests/docs.
- Normal generated-app runtime behavior for workstream agents must use a real Akka Agent component and configured model/provider boundary. Deterministic, mocked, fake, simulated, or model-less behavior is allowed only in tests or explicitly named test adapters.
- Missing provider/security configuration must fail closed with actionable errors/surfaces/traces, not silently fall back to canned text.
- Update this file before finishing the harness response: set completed tasks to `done`, add a completion note, and add discovered follow-up tasks rather than expanding the current task.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes.
- Commit message format: `workstream-agent: <short task title>`.

## Tasks

### TASK-WSAGENT-00-001: Create workstream Akka Agent runtime queue

- status: done
- completion note: Created the focused migration queue for making v0 workstream responses execute through a real Akka Agent component and real configured model/provider boundary.
- source: user request after identifying that current starter workstream agents are service/provider-backed, not Akka Agent components
- task brief: specs/workstream-akka-agent-runtime/tasks/00-planning/00-create-workstream-akka-agent-runtime-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - specs/production-ready-five-core-v0/pending-tasks.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- expected outputs:
  - specs/workstream-akka-agent-runtime/README.md
  - specs/workstream-akka-agent-runtime/conversation-capture.md
  - specs/workstream-akka-agent-runtime/pending-tasks.md
  - specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
  - specs/workstream-akka-agent-runtime/sprints/01-runtime-correction-sprint.md
  - specs/workstream-akka-agent-runtime/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `rg -n "TASK-WSAGENT|Akka Agent|workstream|real model|fake|deterministic" specs/workstream-akka-agent-runtime`
- done criteria:
  - Queue exists with self-sufficient tasks for fresh harness sessions.
  - Each task requires a focused git commit before being marked done.
  - This planning task is committed with message `workstream-agent: add Akka Agent runtime queue`.
- notes:
  - commit message: `workstream-agent: add Akka Agent runtime queue`

### TASK-WSAGENT-01-001: Add regression guard for missing Akka workstream Agent

- status: done
- completion note: Added a concrete `WorkstreamRuntimeAgent` source guard, a `WorkstreamAgentRuntimeInvoker` seam, and service tests proving successful `submitMessage` markdown uses that seam with only explicitly named test adapters.
- source: specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
- task brief: specs/workstream-akka-agent-runtime/tasks/01-runtime/01-add-missing-akka-agent-regression-guard.md
- depends on: [TASK-WSAGENT-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/agent-coverage-matrix.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-component/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- expected outputs:
  - Add or update a starter/template regression test that asserts a concrete workstream runtime class imports `akka.javasdk.agent.Agent` and extends `Agent`.
  - Add or update a regression test that fails if `WorkstreamService.submitMessage(...)` or the message endpoint can produce successful normal runtime markdown without the Akka Agent runtime invoker seam.
  - Keep fake providers/test adapters clearly named and scoped to tests only.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "extends Agent|akka.javasdk.agent.Agent|Workstream.*Agent|fake|test adapter|submitMessage" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Tests fail on the pre-fix shape where the starter has no Akka Agent-backed workstream runtime.
  - Tests still allow isolated unit-test fakes only when explicitly named as test adapters.
  - A focused git commit exists with message `workstream-agent: guard Akka Agent runtime`.
- notes:
  - commit message: `workstream-agent: guard Akka Agent runtime`

### TASK-WSAGENT-02-001: Implement governed Akka workstream Agent component

- status: done
- completion note: Replaced the placeholder with a governed `WorkstreamRuntimeAgent` that uses `ModelProvider.fromConfig(...)`, validates non-secret governed request inputs, returns structured `markdown_response` payloads, added TestKit/TestModelProvider coverage, and configured the starter model alias.
- source: specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
- task brief: specs/workstream-akka-agent-runtime/tasks/01-runtime/02-implement-governed-workstream-akka-agent.md
- depends on: [TASK-WSAGENT-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-component/SKILL.md
  - skills/akka-agent-configuration/SKILL.md
  - skills/akka-agent-structured-responses/SKILL.md
  - skills/akka-agent-model-governance/SKILL.md
  - akka-context/sdk/agents.html.md
  - akka-context/sdk/agents/prompt.html.md
  - akka-context/sdk/agents/testing.html.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ModelProviderClient.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentDefinition.java
  - templates/ai-first-saas-starter/backend/src/main/resources/application.conf
- expected outputs:
  - Add a concrete starter backend class, for example `WorkstreamRuntimeAgent`, that extends `akka.javasdk.agent.Agent`.
  - The agent accepts an already-governed request containing assembled system prompt/context, selected functional agent id, correlation id, and redacted user input.
  - The agent invokes the Akka Agent model path with configured model/provider behavior; do not manually call a fake/default provider for normal runtime.
  - The agent returns structured output suitable for a `markdown_response` surface, including safe markdown text and non-secret metadata.
  - Missing provider/configuration must fail closed through safe error/trace behavior, not generate canned text.
  - Add focused tests with Akka agent test support or a clearly named test model provider.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "class .*Workstream.*Agent|extends Agent|akka.javasdk.agent.Agent|effects\(|systemMessage|userMessage|response" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Starter template contains a real Akka Agent component for workstream responses.
  - The component can be tested without leaking secrets and without creating a production fake path.
  - A focused git commit exists with message `workstream-agent: add governed Akka Agent`.
- notes:
  - commit message: `workstream-agent: add governed Akka Agent`

### TASK-WSAGENT-02-002: Route workstream message submission through Akka Agent runtime

- status: done
- completion note: Routed the browser/API workstream service construction through a ComponentClient-backed `DefaultWorkstreamAgentRuntimeInvoker`, split governance preparation/completion in `AgentRuntimeService`, added a fail-closed no-ComponentClient invoker, and strengthened regression guards for the Akka Agent component path.
- source: specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
- task brief: specs/workstream-akka-agent-runtime/tasks/01-runtime/03-route-submit-message-through-akka-agent.md
- depends on: [TASK-WSAGENT-02-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - skills/akka-http-endpoints/SKILL.md
  - skills/akka-agent-component/SKILL.md
  - skills/akka-agent-testing/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamLogRepository.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- expected outputs:
  - Introduce a production runtime invoker that calls the Akka Agent component from the backend message path, using the appropriate Akka Java SDK mechanism such as `ComponentClient` when needed.
  - Keep `AgentRuntimeService` responsible for governance: active `AgentDefinition`, prompt, skill/reference manifests, tool boundary, model policy, trace basis, and fail-closed denial.
  - Remove or demote direct `ModelProviderClient` calls from the normal successful workstream response path if they bypass Akka Agent execution.
  - Preserve durable user item, agent item, surface, idempotency, trace ids, and safe denial/error behavior.
  - Update tests so successful `submitMessage` proves the Akka Agent runtime invoker was used.
  - Ensure all five core functional agent ids route through the same governed Akka Agent path.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "ComponentClient|Workstream.*AgentInvoker|invokeWorkstreamAgent|ModelProviderClient|TestWorkstream|markdown_response|AgentWorkTrace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Browser/API message submission cannot produce a successful model-backed `markdown_response` without invoking the Akka Agent runtime path.
  - Missing provider/configuration still fails closed with actionable surface/trace behavior.
  - A focused git commit exists with message `workstream-agent: route messages through Akka Agent`.
- notes:
  - commit message: `workstream-agent: route messages through Akka Agent`

### TASK-WSAGENT-03-001: Add real provider smoke validation for Akka Agent path

- status: done
- completion note: Added a real-provider smoke that routes through WorkstreamService and the ComponentClient-backed WorkstreamRuntimeAgent, scans smoke logs/frontend env/static assets for provider-secret leaks, reports skip state clearly when `OPENAI_API_KEY` is absent, and validated both skip/fullstack and real-provider modes.
- source: specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
- task brief: specs/workstream-akka-agent-runtime/tasks/02-validation/01-add-real-provider-smoke-for-akka-agent-path.md
- depends on: [TASK-WSAGENT-02-002]
- required reads:
  - AGENTS.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/.env.example
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/*
  - tools/validate-ai-first-saas-starter-fullstack.sh
  - tools/scaffold-ai-first-saas-starter.sh
- expected outputs:
  - Add or update an optional real-provider smoke test/script that is skipped by default when provider env vars are absent.
  - The smoke must exercise the normal message path, not a direct provider call: selected context + functional agent + workstream message endpoint/service + Akka Agent component + provider-backed response + trace shape.
  - The smoke must assert that provider secrets are not present in `/api/me`, workstream items, surfaces, trace summaries, frontend env, or logs produced by the smoke script.
  - Update fullstack validation to report clearly whether the real-provider Akka Agent smoke ran or was skipped due to missing env.
  - Document exact local commands to run the smoke with a real provider.
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - provider smoke command in skip mode with provider env absent
  - `git diff --check`
  - `rg -n "Akka Agent smoke|provider smoke|OPENAI_API_KEY|skip|real provider|workstream message" tools templates/ai-first-saas-starter`
- done criteria:
  - Maintainers can distinguish CI-safe tests from real local provider validation.
  - There is a documented command that proves the actual Akka Agent-backed workstream path with a real model provider.
  - A focused git commit exists with message `workstream-agent: add real provider smoke`.
- notes:
  - checks: `env -u OPENAI_API_KEY tools/validate-ai-first-saas-starter-fullstack.sh`; `env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh`; `tools/smoke-ai-first-saas-starter-real-model.sh`; `git diff --check`; `rg -n "Akka Agent smoke|provider smoke|OPENAI_API_KEY|skip|real provider|workstream message" tools templates/ai-first-saas-starter specs/workstream-akka-agent-runtime/pending-tasks.md`
  - real-provider smoke result: passed with configured local `OPENAI_API_KEY`; script scanned smoke logs, frontend env files, and static assets for the exact provider secret.
  - commit message: `workstream-agent: add real provider smoke`

### TASK-WSAGENT-04-001: Update docs and completion gates for Akka Agent-backed v0

- status: done
- completion note: Updated core doctrine, installed-pack guidance, starter README validation/smoke gates, user guide prompts, agent coverage, historical core PRD wording, and migration retrospective so five-core v0 completion requires the governed Akka Agent component path instead of service-only provider or deterministic runtime substitutes.
- source: specs/workstream-akka-agent-runtime/backlog/01-workstream-akka-agent-runtime-backlog.md
- task brief: specs/workstream-akka-agent-runtime/tasks/02-validation/02-update-docs-and-completion-gates.md
- depends on: [TASK-WSAGENT-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/skills-pack-user-guide.md
  - docs/agent-coverage-matrix.md
  - README.md
  - pack/AGENTS.md
  - templates/ai-first-saas-starter/README.md
  - specs/production-ready-five-core-v0/pending-tasks.md
  - specs/workstream-akka-agent-runtime/README.md
- expected outputs:
  - Update docs/guidance that define v0 workstream completion to require an Akka Agent component in the normal runtime path.
  - Update starter README smoke checklist to include confirmation of Akka Agent-backed execution, real provider configuration, trace ids, and secret boundary.
  - Update agent coverage/routing references if they imply the starter workstream runtime is complete without an Akka Agent component.
  - Add a short retrospective note explaining this failure mode and the regression guard that now prevents it.
  - If any older pending task wording still allows deterministic/demo/model-less normal runtime for workstream responses, supersede it or point to this queue.
- required checks:
  - `git diff --check`
  - `rg -n "Akka Agent|real model|model-backed|deterministic|mock|fixture|provider smoke|workstream agent" AGENTS.md pack/AGENTS.md skills/README.md docs README.md templates/ai-first-saas-starter specs/production-ready-five-core-v0 specs/workstream-akka-agent-runtime`
- done criteria:
  - Documentation and completion gates align with the implemented Akka Agent-backed runtime path.
  - The retrospective makes the root cause explicit enough for future agents.
  - A focused git commit exists with message `workstream-agent: update completion gates`.
- notes:
  - checks: `git diff --check`; `rg -n "Akka Agent|real model|model-backed|deterministic|mock|fixture|provider smoke|workstream agent" AGENTS.md pack/AGENTS.md skills/README.md docs README.md templates/ai-first-saas-starter specs/production-ready-five-core-v0 specs/workstream-akka-agent-runtime`
  - commit message: `workstream-agent: update completion gates`
