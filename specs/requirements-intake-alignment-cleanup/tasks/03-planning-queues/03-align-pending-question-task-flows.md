# TASK-RIAC-03-003: Align pending question and pending task execution flows

## Objective

Update pending-question, pending-task, and do-next skills so future task sessions preserve workstream expertise, reference governance, surface/action/capability context, and runtime validation.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- docs/pending-question-queue.md
- docs/pending-task-queue.md
- docs/workstream-expertise-model.md
- skills/akka-pending-question-generation/SKILL.md
- skills/akka-pending-question-queue-maintenance/SKILL.md
- skills/akka-pending-task-queue-maintenance/SKILL.md
- skills/akka-do-next-pending-question/SKILL.md
- skills/akka-do-next-pending-task/SKILL.md

## In scope

- Add blockers for missing workstream/surface/capability/agent expertise details.
- Add readReferenceDoc/reference governance blockers where relevant.
- Ensure runtime-completion doctrine is preserved in task execution.

## Checks

- `git diff --check`
- Focused stale-term search over edited skills and queue docs if touched.

## Done criteria

- Pending flows cannot silently execute under-specified page/component tasks.
- Queue updated and committed.
