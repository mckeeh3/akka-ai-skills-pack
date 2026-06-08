# TASK-MAGENT-01-002: Add governed Akka function tools for skill and reference loading

## Objective

Expose governed skill/reference loading as real Akka `@FunctionTool` methods that can be registered in the runtime tool list.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-reference-governance/SKILL.md`
- `skills/akka-agent-tools/SKILL.md`
- `akka-context/sdk/agents/extending.html.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java`

## Implementation notes

- Add a request-scoped tool class such as `AgentRuntimeLoaderTools`.
- `readSkill(skillId)` and `readReferenceDoc(referenceId)` must delegate to existing governed checks.
- Preserve safe denial strings and trace emission.
- Returned text must state guidance/evidence does not grant authority.

## Expected outputs

- Public `@FunctionTool` methods for skill/reference loading.
- Reflection tests proving tool annotations exist.
- Allowed and denied loader-tool tests.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "@FunctionTool|readSkill|readReferenceDoc|AgentRuntimeLoaderTools|SkillLoadTrace|ReferenceLoadTrace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: add governed loader tools`
