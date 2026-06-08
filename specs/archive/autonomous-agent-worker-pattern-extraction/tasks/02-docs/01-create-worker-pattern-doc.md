# TASK-AAWPE-02-001: Create AutonomousAgent worker pattern doc

## Objective

Create or update a focused docs artifact that defines the reusable AutonomousAgent worker pattern for generated apps.

## Required reads

- inventory artifact from TASK-AAWPE-01-001
- existing docs/skills found with `rg -n "AutonomousAgent|worker|task lifecycle|backend agent|attention|event backbone" docs skills`

## Expected outputs

- new or updated docs pattern file, likely `docs/autonomous-agent-worker-runtime-pattern.md`
- updated pending queue

## Required checks

- `git diff --check`
- focused `rg` proving the doc covers task contract, governed capabilities, v3 events, attention, surfaces, provider fail-closed, and no fake success

## Commit message

`autonomous-agent-pattern: add worker runtime doc`
