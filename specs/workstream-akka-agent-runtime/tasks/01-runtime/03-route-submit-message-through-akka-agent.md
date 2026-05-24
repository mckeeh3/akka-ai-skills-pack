# TASK-WSAGENT-02-002: Route workstream message submission through Akka Agent runtime

## Objective

Make the normal frontend/API workstream message path invoke the new Akka Agent component after governed prompt/runtime resolution and before creating the returned `markdown_response` surface.

## Required reads

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

## Expected outputs

- Introduce a production runtime invoker that calls the Akka Agent component from the backend message path, using the appropriate Akka Java SDK mechanism such as `ComponentClient` when needed.
- Keep `AgentRuntimeService` responsible for governance: active `AgentDefinition`, prompt, skill/reference manifests, tool boundary, model policy, trace basis, and fail-closed denial.
- Remove or demote direct `ModelProviderClient` calls from the normal successful workstream response path if they bypass Akka Agent execution.
- Preserve durable user item, agent item, surface, idempotency, trace ids, and safe denial/error behavior.
- Update tests so successful `submitMessage` proves the Akka Agent runtime invoker was used. Isolated service unit tests may use a clearly named `TestWorkstreamAgentInvoker`; this must not be the production default.
- Ensure all five core functional agent ids route through the same governed Akka Agent path.

## Required checks

- `mvn test`
- `git diff --check`
- `rg -n "ComponentClient|Workstream.*AgentInvoker|invokeWorkstreamAgent|ModelProviderClient|TestWorkstream|markdown_response|AgentWorkTrace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Done criteria

- Browser/API message submission cannot produce a successful model-backed `markdown_response` without invoking the Akka Agent runtime path.
- Missing provider/configuration still fails closed with actionable surface/trace behavior.
- Task status is updated in `specs/workstream-akka-agent-runtime/pending-tasks.md`.
- A focused git commit exists with message `workstream-agent: route messages through Akka Agent`.
