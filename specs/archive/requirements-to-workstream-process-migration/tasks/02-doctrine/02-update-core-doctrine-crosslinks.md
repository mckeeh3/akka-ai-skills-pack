# TASK-REQWS-02-002: Update core doctrine crosslinks

## Objective

Update existing canonical docs with concise references to the new process doctrine and the dashboard/attention/autonomous-task implications.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc created by `TASK-REQWS-02-001`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-component-selection-guide.md`

## In scope

- Add concise normative crosslinks/sections to core docs.
- Include dashboard scoping, My Account aggregate exception, attention summaries, task progress/result surfaces, and Autonomous Agent worker semantics where appropriate.

## Out of scope

- Do not rewrite focused implementation skills in this task.

## Expected outputs

- targeted updates in core docs
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "requirements-to-workstream|attention|dashboard|AutonomousAgent|task progress|My Account" docs/ai-first-saas-application-architecture.md docs/agent-workstream-application-architecture.md docs/structured-surface-contracts.md docs/capability-first-backend-architecture.md docs/agent-component-selection-guide.md`

## Done criteria

- Core doctrine consistently points to the canonical process without duplicating excessive prose.
- One focused commit is made.
