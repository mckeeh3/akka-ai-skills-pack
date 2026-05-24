# TASK-WSAGENT-02-001: Implement governed Akka workstream Agent component

## Objective

Add the concrete Akka Agent component that produces workstream `markdown_response` output for the five core v0 functional agents using the governed runtime context.

## Required reads

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

## Expected outputs

- Add a concrete starter backend class, for example `WorkstreamRuntimeAgent`, that extends `akka.javasdk.agent.Agent`.
- The agent accepts an already-governed request containing assembled system prompt/context, selected functional agent id, correlation id, and redacted user input.
- The agent invokes the Akka Agent model path with configured model/provider behavior; do not manually call a fake/default provider for normal runtime.
- The agent returns structured output suitable for a `markdown_response` surface, including safe markdown text and non-secret metadata.
- Missing provider/configuration must fail closed through the same safe error/trace path used by workstream runtime, not generate canned text.
- Add focused tests with Akka agent test support or a clearly named test model provider. Test doubles must not be wired as normal runtime fallback.

## Required checks

- `mvn test`
- `git diff --check`
- `rg -n "class .*Workstream.*Agent|extends Agent|akka.javasdk.agent.Agent|effects\(|systemMessage|userMessage|response" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Done criteria

- Starter template contains a real Akka Agent component for workstream responses.
- The component can be tested without leaking secrets and without creating a production fake path.
- Task status is updated in `specs/workstream-akka-agent-runtime/pending-tasks.md`.
- A focused git commit exists with message `workstream-agent: add governed Akka Agent`.
