# TASK-WEB3-03-001: Add workflow/provider lifecycle events

## Objective

Represent bounded workflow/process/provider lifecycle state changes as workstream events and map them to attention/dashboard state honestly.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- v3 README, conversation capture, sprint, backlog, queue entry, and this task brief
- v3 contract from TASK-WEB3-01-001
- implementation from TASK-WEB3-02-001
- starter provider-blocked, access-review, invitation, governance, or workflow/process-like files

## Skills

- workflow/timed/consumer skills only if local implementation shape requires them

## In scope

- Add lifecycle events for bounded starter cases such as provider blocked/readiness, access-review worker state, invitation expiry, or governance decision lifecycle.
- Map lifecycle events to attention/dashboard source refs.
- Preserve fail-closed provider/model behavior; do not fake successful AutonomousAgent work.
- Add tests for lifecycle event emission, attention mapping, trace refs, and blocked/provider states.

## Out of scope

- Full AutonomousAgent task runtime integration.
- Full workflow engine redesign.

## Required checks

- `git diff --check`
- scaffolded backend tests for lifecycle event behavior
- focused `rg` proving provider/worker states are represented honestly

## Done criteria

- Workflow/process/provider lifecycle events exist for bounded starter cases and feed attention/projection state.
- No fake model-backed success path is introduced.
- Task changes and queue update are committed.

## Commit message

`event-backbone: add lifecycle events`
