# Sprint 3: Frontend Reference Migration

## Goal

Migrate `frontend/` from stale seed/page-oriented routes to the canonical agent workstream shell.

## Scope

- Replace generic route navigation with role-authorized functional-agent rail semantics.
- Preserve routes only as deep links into selected functional agents, stream items, and surfaces.
- Replace page shell with workstream shell, context/authority indicators, workstream panel, persistent composer, and surface renderer.
- Update fixture clients to bootstrap `/api/me`, functional agents, surfaces, workstream history, and capability action responses.
- Retire or rewrite stale screens that contradict the workstream model.

## Out of scope

- Full backend integration.
- Implementing every possible surface type.

## Done criteria

- Running/building the frontend demonstrates the workstream shell as the primary UI architecture.
