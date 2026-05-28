# TASK-REQWS-00-001: Create requirements-to-workstream process migration queue

## Objective

Create the durable mini-project scaffold and pending task queue for realigning requirements/input processing around the workstream-attention-dashboard-surface-capability-autonomous-task process.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`
- `docs/pending-task-queue.md`
- `docs/pending-question-queue.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`

## In scope

- Create `specs/requirements-to-workstream-process-migration/**` planning files.
- Capture source discussion, scope, sprint sequence, backlog, task briefs, and queue.

## Out of scope

- Do not edit installable skills/docs beyond this mini-project in this task.
- Do not implement migration tasks in the planning run.

## Expected outputs

- `README.md`
- `conversation-capture.md`
- `pending-tasks.md`
- sprint files
- backlog files
- task briefs

## Required checks

- `git diff --check`
- `rg -n "REQWS|requirements-to-workstream|attention|dashboard|AutonomousAgent" specs/requirements-to-workstream-process-migration`

## Done criteria

- Planning scaffold exists and future tasks are executable one per fresh harness session.
- Queue status is updated appropriately.
