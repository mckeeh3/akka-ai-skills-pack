# Sprint 01: Durable Core History and Projections

## Objective

Implement richer durable history and projection coverage for the starter core app without regressing current release-ready runtime paths.

## Scope

- Invitation lifecycle event history.
- Governed prompt/skill/reference/manifest/tool-boundary lifecycle history.
- Core projections/views for invitations, user directory, admin audit, and governed-agent catalog/runtime state.

## Acceptance criteria

- Rendered scaffold backend tests prove event history append/replay/idempotency and projection queries.
- Workstream/API surfaces continue to use backend-authorized scoped data.
- Current fullstack validation still passes.
- README/app-description docs are updated if scope claims change.

## Handoff notes

Run focused backend tests in a rendered scaffold when template placeholders prevent direct Maven execution from template source.
