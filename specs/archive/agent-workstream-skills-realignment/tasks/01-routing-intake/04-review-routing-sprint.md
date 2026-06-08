# TASK-AWSR-01-004: Review routing sprint and create next tasks

## Goal

Review Sprint 01 changes, identify remaining routing/intake gaps, and create or refine Sprint 02 tasks before proceeding.

## Required reads

- `specs/agent-workstream-skills-realignment/README.md`
- `specs/agent-workstream-skills-realignment/sprints/01-routing-intake-sprint.md`
- `specs/agent-workstream-skills-realignment/backlog/01-routing-intake-build-backlog.md`
- `specs/agent-workstream-skills-realignment/routing-gap-matrix.md`
- `docs/agent-workstream-design-review-checklist.md`
- touched skills/docs from TASK-AWSR-01-002 and TASK-AWSR-01-003

## Work

1. Run a compact review of top-level routing against the design review checklist.
2. Create `specs/agent-workstream-skills-realignment/sprint-01-review.md` with:
   - pass/fail summary;
   - remaining gaps;
   - whether Sprint 02 can proceed;
   - task additions or changes needed.
3. If more routing work is needed, append new pending tasks before Sprint 02.
4. If routing is aligned enough, ensure Sprint 02 task briefs/pending entries are ready and specific.
5. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Sprint 01 has an explicit review result.
- More tasks are added if needed, otherwise Sprint 02 is unblocked.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Review routing alignment sprint`
