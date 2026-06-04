# Sprint 01: Alignment Contracts and Traceability

## Objective

Make the authoritative app-description and traceability layers accurately describe the current runtime workstream model and provide stable contracts for follow-up implementation.

## Scope

- Canonical id and alias map for functional agents, workstreams, surfaces, dashboards, and implementation ids.
- Exact surface/action to capability/governed-tool mappings for core workstream actions.
- Readiness/review documentation update for current implemented state and remaining full-core gaps.

## Source context

- Audit findings in `conversation-capture.md`.
- Current app-description workstream/capability/UI/traceability files.
- Current backend/frontend workstream ids and action ids.

## Acceptance criteria

- Future implementation tasks can identify the canonical app-description id, implementation id, surface id, action id, governed-tool id, capability id, and exposure channel without guessing.
- No design doc claims full-core readiness beyond what the current local runtime path proves.
- `git diff --check` passes and focused grep evidence shows the new maps/docs reference the relevant ids.

## Handoff notes

Do not rename implementation ids in this sprint unless the task explicitly proves all backend/frontend/tests are updated. Prefer stable alias mapping first.
