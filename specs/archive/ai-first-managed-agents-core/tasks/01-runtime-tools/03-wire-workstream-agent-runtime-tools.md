# TASK-MAGENT-01-003: Wire WorkstreamRuntimeAgent to effects().tools(runtimeTools)

## Objective

Make normal starter workstream agent execution register tools from active managed configuration through Akka `effects().tools(runtimeTools)`.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-component/SKILL.md`
- `skills/akka-agent-tools/SKILL.md`
- `akka-context/sdk/agents/extending.html.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DefaultWorkstreamAgentRuntimeInvoker.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`

## Implementation notes

- Do not pass Java object instances through serialized ComponentClient DTOs.
- Pass safe runtime context/ids to the agent, then resolve tool instances/classes inside the agent runtime boundary.
- Fail closed if runtime tool context is missing or inconsistent.
- Preserve model alias secret-boundary checks.

## Expected outputs

- `WorkstreamRuntimeAgent` calls `.tools(runtimeTools)`.
- Governed request DTO includes safe fields needed to resolve runtime tools.
- Tests updated for success and fail-closed cases.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "\.tools\(|runtimeTools|AgentRuntimeToolResolver|GovernedWorkstreamRequest|modelProviderAlias|WorkstreamRuntimeAgent" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: wire runtime tools into agent`
