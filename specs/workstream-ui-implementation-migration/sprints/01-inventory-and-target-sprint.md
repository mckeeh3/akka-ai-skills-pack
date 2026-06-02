# Sprint 1: Inventory and Target Architecture

## Goal

Inventory stale frontend and starter-core-app implementation drift, then define the target reusable workstream UI component architecture before code migration begins.

## Scope

- Identify stale route/page-first seed code under `frontend/**`, `src/main/resources/static-resources/**`, docs, and tests.
- Decide which code should be retired, quarantined as legacy reference, or revised.
- Define target frontend source organization for reusable workstream UI components.
- Define fixture contracts for `/api/me`, functional agents, workstream items, surfaces, capability actions, and realtime events.

## Out of scope

- Large frontend rewrites.
- Implementing the reusable component set.
- Deleting code before the migration target is documented.

## Done criteria

- A migration inventory exists.
- A target component/source layout exists.
- Pending tasks can safely retire or revise stale code without ambiguity.
