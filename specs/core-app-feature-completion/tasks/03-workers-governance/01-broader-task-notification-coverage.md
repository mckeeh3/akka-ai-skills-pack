# Task Brief: Broader Task and AutonomousAgent Notification Coverage

## Objective

Extend task/AutonomousAgent notification coverage beyond currently implemented worker states and verticals.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/03-workers-governance-sprint.md`
- `templates/ai-first-saas-starter/README.md`
- `docs/autonomous-agent-worker-runtime-pattern.md`
- `docs/agent-component-selection-guide.md`
- `skills/akka-autonomous-agents/SKILL.md` if present, otherwise use local docs and Akka context
- `skills/akka-consumers/SKILL.md`

## In scope

- Generalized worker-task notification mapping for queued/running/blocked/failed/completed/review/cancelled/accepted/rejected states across implemented workers.
- Backend-derived attention/notification/projection behavior for additional task categories.
- Progress/result surface contracts and tests.

## Out of scope

- Adding new model-driven workers unless their capabilities and runtime paths are explicitly in this task.

## Checks

- `git diff --check`
- focused backend worker notification tests
- frontend tests/typecheck/build for task surfaces
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Worker-task notifications are derived from governed task/runtime state and cannot be simulated by frontend-only badges.
