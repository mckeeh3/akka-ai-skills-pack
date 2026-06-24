# TASK-002: Add canonical app worker and governed-tool model

## Scope

Create the canonical doctrine for app workers, harnesses, actor adapters, governed tools, capabilities, and Akka implementation separation.

## Required reads

- `skills-pack/docs/workforce-decomposition.md`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/docs/capability-first-backend-architecture.md`
- `skills-pack/docs/agent-workstream-application-architecture.md`
- `skills-pack/docs/intent-to-realization-flow.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/conversation-capture.md`
- `specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md`

## Expected outputs

- New `skills-pack/docs/app-worker-tool-model.md`.
- Focused updates to existing worker/surface/capability docs to point at the canonical model without duplicating it.

## Done criteria

- Defines worker types and human/software/system worker symmetry.
- Defines surfaces as human worker execution harnesses without treating surfaces as authorization.
- Defines AI agent runtimes as software worker execution harnesses.
- Defines governed tools as first-class semantic operations/evidence reads.
- Defines actor adapters including `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/timer/consumer/API/MCP/internal variants.
- Clearly separates governed tools, capabilities, and Akka components.
- States that human surface availability does not imply AI-agent tool availability.

## Required checks

- `git diff --check`
- Search proof for consistent terminology: worker, governed tool, actor adapter, capability, Akka implementation.
