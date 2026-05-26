# TASK-MAGENT-00-001: Create managed agents core migration queue

## Objective

Create a self-sufficient pending-task queue for making configuration-driven AI-first managed agents a mandatory core generated-app runtime feature.

## Required context

Read:

- `AGENTS.md`
- `skills/README.md`
- `docs/agent-runtime-invocation-pattern.md`
- `specs/governed-runtime-agent-foundation/pending-tasks.md`
- `specs/workstream-akka-agent-runtime/pending-tasks.md`
- `specs/core-app-full-stack-readiness/pending-tasks.md`

## Work

1. Create `specs/ai-first-managed-agents-core/`.
2. Capture the architectural decision: runtime substrate belongs to core app; Agent Admin manages it later.
3. Create task queue, backlog, sprint notes, and task briefs.
4. Mark this planning task done in the queue.
5. Commit only the planning scaffold.

## Checks

- `git diff --check`
- `rg -n "TASK-MAGENT|configuration-driven|managed agents|effects\(\)\.tools|ToolPermissionBoundary|readSkill|readReferenceDoc" specs/ai-first-managed-agents-core`

## Commit

`managed-agents-core: add migration queue`
