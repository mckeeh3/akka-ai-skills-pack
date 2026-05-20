# TASK-AWSR-02-003: Align PRD/spec/backlog generation with vertical workstreams

## Goal

Update PRD/spec/backlog generation so implementation tasks are vertical workstream/surface/capability increments, not vague modules or component slices.

## Required reads

- `specs/agent-workstream-skills-realignment/planning-description-gap-matrix.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `docs/module-sprint-planning.md`
- `docs/pending-task-queue.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`

## Work

1. Update PRD/backlog guidance to require each generated implementation task to carry:
   - functional agent;
   - surface/action or workstream event;
   - capability id/class;
   - AuthContext and role/capability rules;
   - selected Akka substrate;
   - frontend/API/realtime work;
   - required tests.
2. Update examples or output contracts only as needed.
3. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- PRD-generated queues are constrained to implementation-ready vertical work.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align PRD backlog workstream tasks`
