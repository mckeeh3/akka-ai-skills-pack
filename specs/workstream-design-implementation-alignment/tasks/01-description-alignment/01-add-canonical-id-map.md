# TASK-WDA-01-001: Add canonical workstream id map

## Objective

Add a canonical traceability map that connects app-description ids, implementation ids, frontend ids, route/deep-link ids, surface ids, action ids, and test markers for the five core workstreams.

## Required reads

- `AGENTS.md`
- mini-project README, conversation capture, sprint, backlog, queue entry, and this task brief
- `app-description/12-workstreams/functional-agents.md`
- `app-description/12-workstreams/surfaces-index.md`
- `app-description/70-traceability/functional-agent-to-capability-map.md`
- `app-description/70-traceability/surface-to-capability-map.md`
- `src/main/java/ai/first/application/foundation/identity/MeResponse.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/types/agents.ts`
- `frontend/src/main.tsx`

## Skills

- `agent-workstream-apps`
- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`

## In scope

- Add or update an app-description traceability file, preferably `app-description/70-traceability/workstream-id-map.md`.
- Document canonical vs implementation aliases for all five core workstreams and primary surfaces.
- Link the map from relevant traceability/index files.
- Do not rename runtime ids unless every affected code/test path is updated and checked in this same task.

## Out of scope

- Runtime behavior changes.
- Domain-specific workstream additions.

## Expected outputs

- New or updated workstream id map.
- Updated links from traceability or workstream index docs.

## Required checks

- `git diff --check`
- focused `rg` proving the map includes `agent-my-account`, `agent-user-admin`, `agent-agent-admin`, `agent-audit-trace`, `agent-governance-policy`, and their primary surface ids

## Done criteria

- Future tasks can identify canonical and implementation ids without guessing.
- No full-core readiness claims are added.
- Changes and queue update are committed.

## Commit message

`workstream-align: add canonical id map`
