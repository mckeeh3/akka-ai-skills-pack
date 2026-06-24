# Sprint 03: Workstream/API wiring

## Goal

Expose the backend doc-administration flows through protected Agent Admin workstream/API actions and align intent routing with the new surface catalog.

## Tasks

- `AADE-03-001`: Agent Admin workstream/API action contract wiring.

## Acceptance

- Workstream actions return typed surfaces for blank state, dashboard, agent list, agent detail, doc views, edit session, history/diff, create/delete confirmations, restore, and runtime traces.
- Non-SaaS-admin access is denied server-side.
- Old prompt-risk/seed/tool-boundary/activation Agent Admin routes are not surfaced as current intent.
