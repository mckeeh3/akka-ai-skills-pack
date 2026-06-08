# TASK-MAGENT-01-001: Add starter tool catalog and runtime resolver

## Objective

Add the backend-owned catalog/resolver layer that converts active governed tool grants into Java tool bindings for Akka `effects().tools(List<Object>)`.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/agent-runtime-invocation-pattern.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-agent-tools/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/ToolPermissionBoundary.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`

## Implementation notes

- Tool ids in governed records are stable ids, not Java class names.
- The resolver may map stable ids to hardcoded/backend-owned factories.
- Include registry entries for `readSkill` and `readReferenceDoc`.
- Deny by default when a grant or registry binding is missing.

## Expected outputs

- Tool catalog/registry domain/application types.
- `AgentRuntimeToolResolver` or equivalent returning `List<Object>`.
- Tests for allowed, missing, and denied tool binding resolution.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "ToolCatalog|ToolRegistry|AgentRuntimeToolResolver|List<Object>|stable tool|toolId|readSkill|readReferenceDoc" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: add runtime tool resolver`
