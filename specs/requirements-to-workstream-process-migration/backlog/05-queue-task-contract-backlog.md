# Backlog 05: Queue and Task Contract Realignment

## Goal

Make generated pending questions, pending tasks, and do-next execution preserve the new vertical process contract.

## Suggested task breakdown

1. Update queue/question docs with attention/dashboard/autonomous-task fields.
2. Update backlog-to-task and task-brief generation skills.
3. Update do-next and queue maintenance skills to block/repair stale component-only tasks.

## Implementation notes

- Pending task entries should name or inherit functional agent/workstream, attention category, surface/action, capability id, API/exposure channel, Akka substrate, autonomous task definition/result/notification mapping if any, auth/security, traces, and tests.
- Pending questions should avoid broad interviews but ask one focused question when attention semantics, authority, approval, lifecycle, or notification visibility would otherwise be guessed.

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|AutonomousAgent|autonomous task|workstream|surface action|capability id" docs/pending-task-queue.md docs/pending-question-queue.md skills/akka-*pending* skills/akka-*backlog* skills/akka-do-next-pending-task skills/akka-do-next-pending-question`

## Acceptance criteria

- Future task queues are implementation-ready for one fresh harness session per vertical increment.
