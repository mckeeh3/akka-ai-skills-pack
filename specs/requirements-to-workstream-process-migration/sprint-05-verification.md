# Sprint 05 Verification: Queue and Task Contract Realignment

## Scope verified

Verified Sprint 05 against:

- `docs/pending-task-queue.md`
- `docs/pending-question-queue.md`
- `skills/akka-do-next-pending-task/SKILL.md`
- `skills/akka-pending-task-queue-maintenance/SKILL.md`
- `skills/akka-do-next-pending-question/SKILL.md`
- `skills/akka-pending-question-queue-maintenance/SKILL.md`
- supporting backlog/task materialization skills already updated by Sprint 04 (`akka-backlog-to-pending-tasks`, `akka-backlog-item-to-task-brief`, `akka-pending-question-generation`)

## Findings

- Queue docs now require generated full-stack SaaS tasks to carry or inherit the vertical contract: workstream or internal/foundation scope, attention/dashboard or surface action, capability id/class, API/exposure channel, selected Akka substrate, events/notifications/projections, audit/work trace, tests, and local validation when feature-bearing.
- Pending task execution now blocks component-only, CRUD-only, page-only, or dashboard-only generated-SaaS tasks that lack the vertical contract.
- AutonomousAgent/autonomous-task work is guarded by explicit start/query/result/lifecycle capabilities, progress/result surfaces, notifications, failure/cancellation attention behavior, and lifecycle tests.
- Pending question docs and skills now include categories and reconciliation rules for attention semantics, dashboard scope, surface action authority, capability/API exposure, AutonomousAgent lifecycle/result behavior, notification visibility, task result/progress surfaces, and human-vs-agent worker assignment.
- Queue maintenance guidance now repairs or blocks stale generated-SaaS tasks rather than leaving them runnable with missing workstream/attention/surface/capability/substrate context.
- Supporting backlog/task-brief materialization skills preserve the same requirements-to-workstream chain before tasks enter the queue.

## Result

Sprint 05 is complete for queue and task contract realignment. No bounded Sprint 05 follow-up tasks are required before Sprint 06.

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|autonomous task|AutonomousAgent|notification|workstream|surface action|capability id|prescriptive" docs/pending-task-queue.md docs/pending-question-queue.md skills/akka-do-next-pending-task/SKILL.md skills/akka-pending-task-queue-maintenance/SKILL.md skills/akka-do-next-pending-question/SKILL.md skills/akka-pending-question-queue-maintenance/SKILL.md`
