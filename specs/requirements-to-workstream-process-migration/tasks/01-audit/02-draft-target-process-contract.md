# TASK-REQWS-01-002: Draft target process contract

## Objective

Create a concise target process contract that future docs and skills can reference when processing app input.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- `specs/requirements-to-workstream-process-migration/audit-input-processing-artifacts.md`
- `specs/requirements-to-workstream-process-migration/sprints/01-audit-and-target-model-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/01-audit-and-target-model-backlog.md`

## In scope

- Create a compact target process contract under this mini-project.
- Define mandatory processing order, output fields, and anti-drift rules.
- Include Autonomous Agent impact and request-based Agent vs AutonomousAgent vs Workflow selection handoff.

## Out of scope

- Do not promote the target contract to canonical docs yet.

## Expected outputs

- `specs/requirements-to-workstream-process-migration/target-process-contract.md`
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "workstream|attention|dashboard|surface|capability|AutonomousAgent|Workflow|request-based" specs/requirements-to-workstream-process-migration/target-process-contract.md`

## Done criteria

- Contract is concise and implementable by later skill/doc edit tasks.
- One focused commit is made.
