# TASK-STARTER-07-008: Add durable Akka governed-agent behavior slices

## Goal

Start replacing Akka component-backed governed-agent records with durable Akka component-backed state while preserving prompt assembly, readSkill, behavior proposal, and trace semantics.

## Required reads

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/SubstituteAgentBehaviorRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/**`
- `skills/akka-agent-seed-documents/SKILL.md`
- `skills/akka-agent-runtime-state/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`

## Work

1. Select the smallest governed-agent durable slice that can be completed safely in one session.
2. Introduce component-backed storage or explicit component seams for AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, ToolPermissionBoundary, proposal, or trace records.
3. Preserve seed idempotency and customization-preserving upgrade behavior.
4. Add tests for tenant scope, active/approved version resolution, readSkill allow/deny trace, and no provider-secret leakage.
5. Update docs to describe current durability coverage and remaining governed-agent slices.
6. Update the pending queue entry.

## Required checks

- `git diff --check`
- rendered-template Maven tests for agent governance slice
- direct scaffold + `mvn test` if feasible

## Done criteria

- At least one governed-agent repository path is backed by durable Akka components or a clearly introduced component seam.
- Prompt assembly/readSkill/proposal behavior remains deterministic and authorized.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Add durable starter agent governance slice`
