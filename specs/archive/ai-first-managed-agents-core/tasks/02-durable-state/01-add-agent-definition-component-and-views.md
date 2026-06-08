# TASK-MAGENT-02-001: Add first-class AgentDefinition component and views

## Objective

Make `AgentDefinition` a first-class Akka-owned core app record with lifecycle/profile views, not only a field inside the starter repository seam.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-views/SKILL.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentDefinition.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DurableAgentBehaviorRepositoryEntity.java`

## Expected outputs

- `AgentDefinitionEntity` or equivalent lifecycle/profile component.
- Runtime lookup and Agent Admin catalog/detail views.
- Adapter compatibility for existing `AgentBehaviorRepository` reads.
- Tests for active lookup, disabled/archived state, tenant isolation, and view shape.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "AgentDefinitionEntity|AgentDefinition.*View|agent catalog|lifecycleStatus|functionalAgent|tenant" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: add agent definition component`
