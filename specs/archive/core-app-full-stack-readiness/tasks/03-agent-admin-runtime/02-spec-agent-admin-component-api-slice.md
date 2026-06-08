# TASK-CORE-03-002: Specify Agent Admin component and API slice

## Purpose

Create implementation-ready contracts for durable Agent Admin state, APIs, views, and tests.

## Required reads

- `specs/core-app-full-stack-readiness/agent-admin-runtime-gap-inventory.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-governed-documents/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-seed-documents/SKILL.md`
- `skills/akka-agent-model-governance/SKILL.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md`

## Required checks

- Slice covers AgentDefinition lifecycle, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, ModelConfigRef, seed import, behavior-edit proposals, test console, views, endpoints, and tests.
- `git diff --check`

## Done criteria

- Future implementation tasks can build Agent Admin components/APIs without guessing state boundaries.
- Queue status and changes are committed.
