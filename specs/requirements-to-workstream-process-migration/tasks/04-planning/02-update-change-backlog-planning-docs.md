# TASK-REQWS-04-002: Update change/backlog planning skills and docs

## Objective

Update revised PRD, change request, slice-to-backlog, backlog-to-task, and planning docs so iterative planning preserves the new vertical process contract.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc from `TASK-REQWS-02-001`
- `skills/akka-revised-prd-reconciliation/SKILL.md`
- `skills/akka-change-request-to-spec-update/SKILL.md`
- `skills/akka-slice-spec-to-backlog/SKILL.md`
- `skills/akka-backlog-to-pending-tasks/SKILL.md`
- `skills/akka-backlog-item-to-task-brief/SKILL.md`
- `skills/akka-pending-question-generation/SKILL.md`
- `docs/prd-to-akka-flow.md`
- `docs/module-sprint-planning.md`
- `docs/solution-plan-to-implementation-queue.md`

## In scope

- Update iterative planning language to preserve workstream, attention, dashboard, surface, capability, autonomous task, notification, trace, auth, and test context.
- Update docs to stop centering conventional CRUD/module/component decomposition.

## Out of scope

- Do not rewrite examples unless small doc changes require it.

## Expected outputs

- updates to relevant planning skills/docs
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|autonomous task|AutonomousAgent|notification|workstream|surface action" skills/akka-revised-prd-reconciliation/SKILL.md skills/akka-change-request-to-spec-update/SKILL.md skills/akka-slice-spec-to-backlog/SKILL.md skills/akka-backlog-to-pending-tasks/SKILL.md skills/akka-backlog-item-to-task-brief/SKILL.md skills/akka-pending-question-generation/SKILL.md docs/prd-to-akka-flow.md docs/module-sprint-planning.md docs/solution-plan-to-implementation-queue.md`

## Done criteria

- Iterative planning and backlog materialization preserve the vertical process chain.
- One focused commit is made.
