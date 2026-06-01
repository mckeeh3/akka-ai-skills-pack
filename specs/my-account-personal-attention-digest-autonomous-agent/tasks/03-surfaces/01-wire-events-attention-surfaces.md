# TASK-MAPAD-03-001: Wire My Account digest events, attention, and surfaces

## Objective

Wire personal attention digest task lifecycle/results into v3 events, attention, and My Account structured surfaces.

## In scope

- `workflow.my_account.*` and `worker.task.*` events.
- Attention mappings for blocked/failed/digest-ready/acknowledged states.
- Digest result/progress surfaces with authorized item summaries, redaction notes, next actions, trace refs, and no direct mutation.
- Backend/frontend tests.

## Required checks

- `git diff --check`
- scaffolded backend tests
- frontend tests/typecheck/build if frontend changes

## Commit message

`my-account-digest-agent: wire events attention surfaces`
