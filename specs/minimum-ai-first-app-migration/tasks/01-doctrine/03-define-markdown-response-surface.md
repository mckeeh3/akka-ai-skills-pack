# Task: Define markdown_response structured surface contract

## Objective

Make `markdown_response` a first-class structured surface type rather than an informal chat message.

## Scope

- Update `docs/structured-surface-contracts.md`.
- Update `skills/app-description-surface-modeling/SKILL.md` only if needed to route/model the new base surface.

## Required details

Cover payload, rendering, sanitization, trace links, allowed actions, loading/error/forbidden states, accessibility, and rendering/security tests.

## Required reads

See `TASK-MINAPP-01-003` in `pending-tasks.md`.

## Acceptance

- `markdown_response` is usable as the first surface in User Admin workstream v0.
- Security says sanitized HTML only; no raw script execution.
- Surface remains connected to workstream trace/correlation ids.

## Commit

Make one commit for this task and queue update.
