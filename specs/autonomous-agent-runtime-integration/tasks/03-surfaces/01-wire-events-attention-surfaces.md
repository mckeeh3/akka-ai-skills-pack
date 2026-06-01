# TASK-AAI-03-001: Wire AutonomousAgent events, attention, and surfaces

## Objective

Wire access-review AutonomousAgent task lifecycle/progress/result/failure states into v3 events, attention items, and User Admin/My Account surfaces.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- contract from TASK-AAI-01-001
- backend runtime implementation from TASK-AAI-02-001
- v3 event backbone contract/implementation
- v1/v2 attention implementation
- starter frontend workstream surface files

## Skills

- web UI/frontend skills as needed
- event/consumer skills if backend event mapping changes

## In scope

- Emit v3 task lifecycle/progress/result/failure events.
- Map task states to attention items using existing attention backbone.
- Render or update structured progress/result/decision/system-message surfaces.
- Ensure My Account/rail attention reflects task states through backend-derived paths.
- Add backend/frontend tests for event/attention/surface linkage.

## Out of scope

- New AutonomousAgent worker types.
- Broad UI redesign.

## Required checks

- `git diff --check`
- scaffolded backend tests for events/attention linkage
- frontend tests/typecheck/build if frontend changes are made
- focused `rg` for task lifecycle events, attention mappings, and surface ids

## Done criteria

- Task lifecycle states are visible through events, attention, and surfaces.
- Frontend state is not authoritative.
- Task changes and queue update are committed.

## Commit message

`autonomous-agent: wire events attention surfaces`
