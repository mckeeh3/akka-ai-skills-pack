# Sprint 05: Queue and Task Contract Realignment

## Objective

Ensure generated pending questions, pending tasks, task briefs, and execution rules preserve the new vertical workstream process and prevent future implementation sessions from regressing into component-only or CRUD/page-first work.

## Scope

Likely affected docs/skills:
- `docs/pending-task-queue.md`
- `docs/pending-question-queue.md`
- `skills/akka-backlog-to-pending-tasks/SKILL.md`
- `skills/akka-backlog-item-to-task-brief/SKILL.md`
- `skills/akka-do-next-pending-task/SKILL.md`
- `skills/akka-pending-task-queue-maintenance/SKILL.md`
- `skills/akka-do-next-pending-question/SKILL.md`
- `skills/akka-pending-question-queue-maintenance/SKILL.md`

## Work areas

1. Add queue/task fields for workstream, attention category, dashboard/surface contract, surface action, API/capability, Akka substrate, autonomous task definition/result/notification mapping, and tests.
2. Add question categories for attention semantics, dashboard scope, autonomous task lifecycle, notification visibility, task result surfaces, and human/agent worker assignment.
3. Update do-next-pending-task guidance to block tasks that lack the vertical contract unless explicitly cross-cutting/internal.
4. Update maintenance guidance to repair stale component-only tasks.

## Acceptance criteria

- Generated task queues can drive one fresh harness session per vertical increment.
- Tasks cannot be marked runnable when they omit workstream/surface/capability/authority/substrate context needed for safe implementation.
- Autonomous Agent tasks include start/query/result/notification/lifecycle capability and test requirements when applicable.
