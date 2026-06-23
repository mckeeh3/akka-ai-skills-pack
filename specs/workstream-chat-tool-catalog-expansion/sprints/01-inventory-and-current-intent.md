# Sprint 01: Inventory and Current Intent

## Goal

Classify the existing foundation workstream surface/action catalog before exposing more actions through confirmed chat tool plans.

## Scope

- Inventory existing surface actions, backend action dispatch paths, governed tool ids, capability ids, schemas, confirmation/approval/idempotency rules, traces, and tests.
- Classify each action as `chat-executable-now`, `chat-proposal-only`, `approval-gated`, `surface-only`, `router-only`, `internal-only`, `blocked-pending-design`, or `out-of-scope`.
- Update app-description current intent for all five workstreams with the expanded/blocked catalog.

## Completion signal

Sprint 01 is complete when implementation tasks can safely choose expanded catalog entries without guessing whether an action is suitable for chat execution.
