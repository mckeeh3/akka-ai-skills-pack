# TASK-AWSR-05-004: Normalize structured-surface and exposure-channel terminology

## Goal

Reduce ambiguity between workstream structured surfaces and capability exposure paths/channels.

## Required reads

- `skills/akka-solution-decomposition/SKILL.md`
- `skills/capability-first-backend/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`

## Work

1. Audit occurrences where `surface` may ambiguously mean both:
   - a structured workstream UI artifact; and
   - an API/tool/workflow/timer/consumer exposure.
2. Update high-impact routing/planning docs/skills to prefer:
   - `structured surface` for workstream renderable artifacts;
   - `exposure channel`, `exposure path`, or `capability exposure` for HTTP/gRPC/MCP/tools/workflows/timers/consumers/internal calls.
3. Preserve established `surface action` terminology for actions on structured workstream surfaces.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- targeted `rg -n "exposure surfaces|selected surfaces|surface"` review over touched files

## Done criteria

- Top-level planning language clearly distinguishes structured surfaces from capability exposure channels.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Normalize workstream surface terminology`
