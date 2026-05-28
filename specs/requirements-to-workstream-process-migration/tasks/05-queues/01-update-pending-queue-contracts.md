# TASK-REQWS-05-001: Update pending queue contracts

## Objective

Update pending task/question docs and execution skills so generated queues preserve workstream-attention-dashboard-surface-capability-autonomous-task context and block stale component-only tasks.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc from `TASK-REQWS-02-001`
- `docs/pending-task-queue.md`
- `docs/pending-question-queue.md`
- `skills/akka-do-next-pending-task/SKILL.md`
- `skills/akka-pending-task-queue-maintenance/SKILL.md`
- `skills/akka-do-next-pending-question/SKILL.md`
- `skills/akka-pending-question-queue-maintenance/SKILL.md`
- `specs/requirements-to-workstream-process-migration/sprints/05-queue-task-contract-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/05-queue-task-contract-backlog.md`

## In scope

- Add required/inherited fields for workstream, attention category, dashboard/surface/action, capability/API, substrate, autonomous task/result/notification, auth, traces, and tests.
- Add question categories for attention/dashboard/autonomous task lifecycle gaps.
- Update do-next guidance to block/repair missing vertical contracts.

## Out of scope

- Do not update PRD/backlog generation skills here if already handled in sprint 04.

## Expected outputs

- updates to queue docs and do-next/maintenance skills
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|autonomous task|AutonomousAgent|notification|workstream|surface action|capability id" docs/pending-task-queue.md docs/pending-question-queue.md skills/akka-do-next-pending-task/SKILL.md skills/akka-pending-task-queue-maintenance/SKILL.md skills/akka-do-next-pending-question/SKILL.md skills/akka-pending-question-queue-maintenance/SKILL.md`

## Done criteria

- Queue execution rules reinforce the new process and prevent unsafe guessing.
- One focused commit is made.
