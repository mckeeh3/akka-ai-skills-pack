# TASK-WGGT-03-003: Update pending question/task queue flows

## Objective

Update question/task queue docs and do-next skills so executable tasks carry graph/governed-tool/expertise context.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- docs/pending-question-queue.md
- docs/pending-task-queue.md
- skills/akka-pending-question-generation/SKILL.md
- skills/akka-pending-question-queue-maintenance/SKILL.md
- skills/akka-pending-task-queue-maintenance/SKILL.md
- skills/akka-do-next-pending-question/SKILL.md
- skills/akka-do-next-pending-task/SKILL.md

## In scope

- Add question categories/blockers for role dashboards, surface graphs, governed-tools, internal agent graphs, workstream expertise.
- Add task vertical contract fields for graph nodes/edges and governed-tool exposure.
- Add execution blockers when tasks lack graph/governed-tool context.

## Checks

- `git diff --check`
- Focused term search over edited docs/skills.

## Done criteria

- Pending queues preserve graph/governed-tool context across fresh sessions.
- Queue updated and committed.
