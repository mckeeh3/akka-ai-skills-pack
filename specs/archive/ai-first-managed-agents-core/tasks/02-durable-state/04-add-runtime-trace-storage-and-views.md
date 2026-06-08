# TASK-MAGENT-02-004: Add durable agent runtime trace storage and views

## Objective

Persist and query agent runtime traces as core app facts for Audit/Trace and Agent Admin surfaces.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-work-trace/SKILL.md`
- `skills/ai-first-saas-audit-trace/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-views/SKILL.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentRuntimeTrace.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`

## Expected outputs

- Durable storage for prompt assembly, skill load, reference load, tool invocation, model invocation, and agent work traces.
- Views by tenant, agent, correlation/work trace id, trace type, decision, and timestamp.
- Runtime trace sink updated to persist traces where required.
- Tests for allowed/denied trace persistence, redaction/no-secret checks, and query filtering.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "AgentRuntimeTraceEntity|AgentRuntimeTrace.*View|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|ToolInvocationTrace|AgentWorkTrace|trace search" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: add runtime trace storage`
