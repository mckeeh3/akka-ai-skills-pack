# TASK-MAPAD-01-001: Define personal attention digest AutonomousAgent contract

## Objective

Define the My Account Personal Attention Digest AutonomousAgent task/result/evidence/capability/event/surface contract.

## Required reads

- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- `docs/autonomous-agent-worker-runtime-pattern.md`
- `specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md`
- `specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md`
- starter My Account and attention service/surface files

## Skills

- `akka-autonomous-agents`

## Expected outputs

- `specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-autonomous-agent-contract.md`
- updated pending queue

## Required checks

- `git diff --check`
- focused `rg` proving contract covers AutonomousAgent, personal attention evidence/redaction, provider fail-closed, v3 events, attention, My Account surfaces, and no fake success

## Commit message

`my-account-digest-agent: define contract`
