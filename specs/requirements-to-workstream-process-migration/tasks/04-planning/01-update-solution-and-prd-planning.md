# TASK-REQWS-04-001: Update solution and PRD planning skills

## Objective

Update direct solution decomposition and PRD-to-specs backlog generation so requirements are processed through the vertical workstream-attention-dashboard-surface-capability-autonomous-task chain.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc from `TASK-REQWS-02-001`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `docs/agent-component-selection-guide.md`
- `specs/requirements-to-workstream-process-migration/sprints/04-prd-spec-backlog-realignment-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/04-prd-spec-backlog-realignment-backlog.md`

## In scope

- Update required output sections and workflows.
- Ensure large PRDs split by workstream and attention/dashboard vertical increments.
- Add autonomous task definitions/result/notification mapping to solution/backlog outputs when internal workers are needed.

## Out of scope

- Do not update every derived planning skill in this task.

## Expected outputs

- updates to `skills/akka-solution-decomposition/SKILL.md`
- updates to `skills/akka-prd-to-specs-backlog/SKILL.md`
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|autonomous task|AutonomousAgent|notification|workstream|surface action" skills/akka-solution-decomposition/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md`

## Done criteria

- Direct planning paths produce the new vertical process model before Akka component selection.
- One focused commit is made.
