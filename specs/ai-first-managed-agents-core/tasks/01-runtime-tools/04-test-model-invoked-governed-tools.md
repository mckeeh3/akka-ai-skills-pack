# TASK-MAGENT-01-004: Prove model-invoked governed tool calls through Akka Agent path

## Objective

Add regression tests proving the real Akka Agent tool loop can call governed loader tools during workstream runtime execution.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-testing/SKILL.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/agents/extending.html.md`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgentTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeServiceTest.java`

## Test requirements

- Model requests `readSkill` and receives assigned active skill text.
- Model requests `readReferenceDoc` and receives assigned active reference text.
- Unassigned or boundary-denied loader calls return safe denials.
- `SkillLoadTrace` / `ReferenceLoadTrace` are emitted for allowed and denied loads.
- Initial prompt includes compact manifest entries only.
- Regression fails if runtime tools are no longer registered.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "readSkill|readReferenceDoc|tool call|FunctionTool|runtime tools|compact manifest|SkillLoadTrace|ReferenceLoadTrace" templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/backend/src/main/java`

## Commit

`managed-agents-core: test governed tool calls`
