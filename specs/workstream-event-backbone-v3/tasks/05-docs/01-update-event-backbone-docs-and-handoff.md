# TASK-WEB3-05-001: Update event backbone docs and handoff

## Objective

Update starter/reference docs and local guidance to reflect v3 event backbone status and the planned next AutonomousAgent runtime integration initiative.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- v3 README, conversation capture, sprint, backlog, queue entry, and this task brief
- v3 contract and completed implementation notes
- relevant docs found with `rg -n "event backbone|attention backbone|AutonomousAgent|notification|digest|SSE|needs.*attention" docs templates specs`

## In scope

- Update docs/handoff to distinguish:
  - v1 attention backbone;
  - v2 attention producers/update delivery;
  - v3 governed event backbone;
  - future AutonomousAgent runtime integration.
- Preserve runtime completion and backend-authoritative state guardrails.

## Out of scope

- Creating the future AutonomousAgent mini-project unless explicitly requested later.
- Broad doctrine rewrite unrelated to events/attention/workstreams.

## Required checks

- `git diff --check`
- focused `rg` proving docs distinguish v1/v2/v3/future AutonomousAgent work without stale missing-backbone claims

## Done criteria

- Future agents can understand the event backbone state and next recommended AutonomousAgent work.
- Task changes and queue update are committed.

## Commit message

`event-backbone: update docs handoff`
