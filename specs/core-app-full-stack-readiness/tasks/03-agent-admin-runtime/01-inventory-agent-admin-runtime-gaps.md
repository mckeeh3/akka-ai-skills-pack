# TASK-CORE-03-001: Inventory Agent Admin and hybrid runtime gaps

## Purpose

Compare current governed-agent skills, docs, Java reference code, and frontend fixtures against the full Agent Admin/runtime target.

## Required reads

- `docs/agent-coverage-matrix.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-agent-model-governance/SKILL.md`
- `src/main/java/com/example/domain/agentfoundation/**`
- `src/main/java/com/example/application/agentfoundation/**`

## Expected outputs

- `specs/core-app-full-stack-readiness/agent-admin-runtime-gap-inventory.md`

## Required checks

- Inventory covers AgentDefinition, prompts, skills, manifests, tool boundaries, ModelConfigRef, seed import, behavior editing, test console, runtime resolver, tool enforcement, traces, APIs, UI, and tests.
- No production code rewrite in this inventory task.
- `git diff --check`

## Done criteria

- Gaps are concrete and ordered for follow-up tasks.
- Queue status and changes are committed.
