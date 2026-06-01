# TASK-ATSA-01-001: Define Audit/Trace summary AutonomousAgent contract

## Objective

Define the Audit/Trace Summary AutonomousAgent task/result/evidence/capability/event/surface contract.

## Required reads

- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- `docs/autonomous-agent-worker-runtime-pattern.md`
- `specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md`
- `specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md`
- starter Audit/Trace service/surface files

## Skills

- `akka-autonomous-agents`

## Expected outputs

- `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-autonomous-agent-contract.md`
- updated pending queue

## Required checks

- `git diff --check`
- focused `rg` proving contract covers AutonomousAgent, audit trace evidence/redaction, provider fail-closed, v3 events, attention, surfaces, and no fake success

## Commit message

`audit-summary-agent: define contract`
