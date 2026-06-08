# Backlog: Attention Event Producers v2

## Goal

Create bounded tasks that extend the v1 shared attention backbone with real producer/update behavior for the starter template.

## Suggested task breakdown

1. **Producer contract and gap map**
   - Document source event families, producer ids, idempotency keys, upsert/resolve semantics, source refs, and tests.
   - Inventory v1 attention derivations and identify which should become producers.

2. **Domain/service producers**
   - Wire existing service outcomes to attention lifecycle updates.
   - Prioritize invitation delivery failure/resolution and governance proposal/decision state because they have concrete starter flows.

3. **Timed checks and worker/task attention**
   - Add a bounded timed/stale check for expiring invitations or stale blocked provider/task states.
   - Represent internal worker/AutonomousAgent-adjacent states honestly, especially blocked/provider-fail-closed readiness.

4. **Frontend/realtime update path**
   - Add backend-derived refresh/poll/stream behavior for rail/My Account/workstream attention summaries.
   - Preserve frontend transient unseen-response state as separate.

5. **Docs/guidance update**
   - Update starter docs and relevant architecture/WIP docs to say v1 backbone exists and v2 handles producers.
   - Avoid adding project-only planning details to installable pack manifests unless a task explicitly calls for packaged docs.

6. **Verification**
   - Run targeted checks and append follow-up tasks if material v2 gaps remain.

## Dependencies

- Producer contract before implementation.
- Domain/service producers before frontend update delivery.
- Docs update after implementation shape is known.
- Verification after all planned tasks.

## Required checks

Per task, use targeted subsets of:

- `git diff --check`
- scaffolded starter backend Maven tests for attention producers
- frontend tests/typecheck/build for shell update changes
- focused `rg` checks for producer ids, idempotency, and avoidance of frontend-only authoritative attention

## Acceptance criteria

The queue succeeds when attention records are created/updated/resolved from real starter backend events or state transitions at v2 scope, surfaced to users through backend-derived update paths, documented accurately, and verified by tests.
