# TASK-AWSR-04-003: Final realignment review and next-sprint decision

## Goal

Review the full skills realignment effort and decide whether the pack is ready for PRD-driven starter implementation or needs another refinement sprint.

## Required reads

- `specs/agent-workstream-skills-realignment/README.md`
- all sprint review files created so far
- all gap matrices created so far
- `docs/agent-workstream-design-review-checklist.md`
- `skills/README.md`
- top-level routing, app-description, PRD, web UI, agent, endpoint, and component skills touched during this effort
- updated starter queue files

## Work

1. Create `specs/agent-workstream-skills-realignment/final-realignment-review.md`.
2. Run the design review checklist against:
   - top-level routing;
   - app-description path;
   - PRD/spec/backlog path;
   - implementation skills;
   - starter queue.
3. If gaps remain, add the next sprint, backlog, task briefs, and pending task entries.
4. If aligned enough, explicitly state that PRD-driven starter implementation can begin.
5. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Final review either closes realignment or creates a concrete next sprint.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Review agent workstream skills realignment`
