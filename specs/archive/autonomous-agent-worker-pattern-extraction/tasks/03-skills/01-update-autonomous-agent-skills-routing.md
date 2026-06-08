# TASK-AAWPE-03-001: Update AutonomousAgent skills routing

## Objective

Update relevant skills/routing docs so future worker tasks load and apply the reusable AutonomousAgent worker pattern.

## Required reads

- pattern doc from TASK-AAWPE-02-001
- `skills/README.md`
- relevant `skills/akka-autonomous-*` and agent-workstream/capability skills

## Expected outputs

- focused skill/routing updates
- updated pending queue

## Required checks

- `git diff --check`
- focused `rg` proving skills reference the worker pattern and preserve runtime completion guardrails

## Commit message

`autonomous-agent-pattern: update skill routing`
