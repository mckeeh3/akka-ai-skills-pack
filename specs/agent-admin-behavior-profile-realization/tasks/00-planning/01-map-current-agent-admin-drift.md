# Task AABP-01-001: Map current Agent Admin implementation drift

## Goal

Create a focused implementation map that translates current Agent Admin app-description nodes into source/test areas and classifies stale code/tests before broad behavior edits.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/agent-admin-behavior-profile-realization/README.md`
- `specs/agent-admin-behavior-profile-realization/conversation-capture.md`
- `specs/agent-admin-behavior-profile-realization/backlog/01-agent-admin-behavior-profile-build-backlog.md`
- `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
- Current Agent Admin backend/frontend/tests named by source-alignment.

## Skills

- `app-generate-app`
- `capability-first-backend`
- `akka-agent-behavior-profiles`
- `akka-agent-behavior-editing`

## Expected outputs

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- Optional queue refinements if a task must be split or blocked.

## Done criteria

- Map identifies backend services/entities/repositories, workstream/API action routes, frontend surfaces/fixtures/tests, and source-alignment entries to keep/change/remove.
- Stale direct-save, direct-restore, direct-create/delete, whole-agent lifecycle, generated agent identity editing, and governance-console assertions are classified.
- The next implementation task can proceed without rediscovering source boundaries.
- Queue status is updated and changes are committed.

## Required checks

```bash
git diff --check -- specs/agent-admin-behavior-profile-realization
```

## Commit message

`Map Agent Admin behavior profile drift`
