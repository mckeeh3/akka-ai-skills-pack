# TASK-AAPR-01-001: Define prompt-risk AutonomousAgent contract

## Objective

Define the Agent Admin Prompt-Risk AutonomousAgent task/result/capability/event/surface contract.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`
- `specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md`
- `specs/workstream-event-backbone-v3/workstream-event-backbone-v3-contract.md`
- starter Agent Admin governance/runtime files

## Skills

- `akka-autonomous-agents`
- `akka-agent-behavior-profiles` / prompt/skill governance skills if relevant

## Expected outputs

- `specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md`
- updated pending queue

## Required checks

- `git diff --check`
- focused `rg` proving contract covers AutonomousAgent, prompt/skill/reference/model/tool-boundary risk, provider fail-closed, v3 events, attention, surfaces, and no fake success

## Done criteria

- Runtime and surface tasks can proceed without guessing.
- Task changes and queue update are committed.

## Commit message

`agent-admin-risk: define prompt risk contract`
