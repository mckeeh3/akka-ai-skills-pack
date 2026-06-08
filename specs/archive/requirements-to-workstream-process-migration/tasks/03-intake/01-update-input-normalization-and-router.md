# TASK-REQWS-03-001: Update input normalization and intake router

## Objective

Update app-description input normalization and intake routing so broad app input is processed into workstreams, attention needs, dashboards, surfaces, capabilities, and autonomous task candidates before focused layer routing.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc from `TASK-REQWS-02-001`
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `specs/requirements-to-workstream-process-migration/sprints/03-intake-description-realignment-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/03-intake-description-realignment-backlog.md`

## In scope

- Extend normalized envelope with workstream, attention/dashboard, surface/action, autonomous-task, event/notification/trace candidate sections.
- Update routing order to prevent skipping workstream-attention-dashboard preprocessing.
- Keep confirmed vs inferred separation.

## Out of scope

- Do not update all app-description companion skills in this task.

## Expected outputs

- updates to `skills/app-description-input-normalization/SKILL.md`
- updates to `skills/app-description-intake-router/SKILL.md`
- possible small updates to `skills/ai-first-saas/SKILL.md` / `skills/agent-workstream-apps/SKILL.md`
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|autonomous task|AutonomousAgent|workstream|surface action" skills/app-description-input-normalization/SKILL.md skills/app-description-intake-router/SKILL.md skills/ai-first-saas/SKILL.md skills/agent-workstream-apps/SKILL.md`

## Done criteria

- Broad or mixed input normalization can represent the new process contract.
- Router selects workstream/surface/capability paths in the correct order.
- One focused commit is made.
