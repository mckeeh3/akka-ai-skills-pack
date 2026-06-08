# TASK-AAI-05-001: Update AutonomousAgent runtime docs and handoff

## Objective

Update starter/reference docs and handoff to describe the first AutonomousAgent runtime vertical and future broader runtime/team/delegation work.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- contract and validation artifacts from prior tasks
- relevant docs found with `rg -n "AutonomousAgent|access review|worker|task lifecycle|event backbone|attention" docs templates specs`

## In scope

- Document what is implemented, blocked, or future.
- Distinguish request-based workstream Agents from durable internal/background AutonomousAgents.
- Preserve no fake success and provider fail-closed guidance.
- Identify next candidate workers after User Admin Access Review.

## Required checks

- `git diff --check`
- focused `rg` proving docs distinguish implemented vertical, future workers, provider fail-closed, and no fake success

## Done criteria

- Future agents can understand current AutonomousAgent runtime integration status and next steps.
- Task changes and queue update are committed.

## Commit message

`autonomous-agent: update runtime docs`
