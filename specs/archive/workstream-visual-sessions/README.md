# Workstream Visual Sessions

## Scope

This spec tracks the phased implementation of workstream visual sessions for the agent workstream shell.

The goal is to preserve the familiar modern chat timeline experience while supporting typed workstream surfaces, governed capabilities, and per-workstream continuity:

- older turn groups appear above newer turn groups;
- new turn groups append at the end of the stream;
- after a new request is submitted, its request surface scrolls to the top of the visible workstream panel;
- response surfaces created for that request render below the request surface;
- the request surface remains anchored while response surfaces append unless the user manually scrolls;
- each workstream has independent visual session state that is restored when users switch workstreams.

## Canonical reference

- `docs/workstream-visual-sessions.md`
- `docs/workstream-ui-reference-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`

## Phase plan

### Phase 1: Akka component-backed visual sessions

Status: complete for the source frontend reference and starter template.

Implemented the basic frontend behavior without backend or browser persistence:

1. turn-group/session state helpers;
2. request-surface anchoring and manual-scroll pause;
3. per-workstream component-state restore when switching workstreams;
4. template sync and contract coverage;
5. phase 1 readiness documentation.

### Phase 2: browser-local persistence

Future work. Persist semantic visual-session snapshots in the browser for refresh/day-to-day same-device continuity.

### Phase 3: backend-persisted visual sessions

Future work. Persist semantic resume state server-side for cross-device continuity and observability-friendly SaaS behavior.

## Execution

Use `pending-tasks.md`. Execute one task per fresh harness session. Each implementation task must commit only its intended changes plus its queue-status update.
