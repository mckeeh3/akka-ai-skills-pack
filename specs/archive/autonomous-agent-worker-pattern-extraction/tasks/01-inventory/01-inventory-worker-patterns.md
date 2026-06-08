# TASK-AAWPE-01-001: Inventory completed worker patterns

## Objective

Inventory the User Admin Access Review and Agent Admin Prompt-Risk AutonomousAgent verticals and extract a common pattern map.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- mini-project README/conversation/sprint/backlog/queue entry and this task brief
- `specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md`
- `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`
- `specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md`
- `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-verification.md`
- starter worker implementation files found with `rg -n "AutonomousAgent|AccessReviewAutonomousAgent|PromptRisk|worker.task|autonomous_task" templates/ai-first-saas-starter`

## Expected outputs

- `specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md`
- updated pending queue

## Required checks

- `git diff --check`

## Commit message

`autonomous-agent-pattern: inventory workers`
