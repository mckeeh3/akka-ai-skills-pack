# TASK-FC5-99-001: Verify full-core five-workstreams completion

## Objective

Verify the current task group and overall mini-project done state. Append bounded follow-up tasks before a new terminal verification task if material gaps remain.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- all files under `specs/full-core-five-workstreams/`
- `docs/requirements-to-workstream-development-process.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/ai-first-saas-application-architecture.md`

## Expected outputs

- Updated `specs/full-core-five-workstreams/pending-tasks.md` with verification result and any appended follow-up tasks.
- Optional release/readiness handoff if complete.

## Checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `git diff --check`

## Done criteria

Verification compares completed work against the workstream/surface/agent/capability doctrine, task done criteria, and mini-project done state. If incomplete, the queue contains new bounded tasks and a new terminal verification task.
