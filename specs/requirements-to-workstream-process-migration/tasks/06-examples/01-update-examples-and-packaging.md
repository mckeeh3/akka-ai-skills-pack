# TASK-REQWS-06-001: Update examples, seed, and packaging alignment

## Objective

Update examples, seed references, starter guidance, and packaging so installed-pack users encounter the requirements-to-workstream process as the normal development path.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc from `TASK-REQWS-02-001`
- `docs/examples/ai-first-saas-seed-app-description/README.md`
- `docs/prd-to-akka-flow.md`
- `pack/AGENTS.md`
- `pack/README.md`
- `pack/manifest.yaml`
- `specs/requirements-to-workstream-process-migration/sprints/06-examples-packaging-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/06-examples-packaging-backlog.md`

## In scope

- Add or update a compact example of input/PRD → workstreams → attention → dashboards → surfaces → capabilities → autonomous tasks/events/traces.
- Update seed/starter references if needed.
- Ensure new canonical docs are packaged.
- Mark legacy examples as mechanics references where necessary.

## Out of scope

- Do not implement generated app runtime code in this task.

## Expected outputs

- updated examples/docs/pack references
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "requirements-to-workstream|attention|dashboard|AutonomousAgent|mechanics reference|target architecture" docs/examples docs/prd-to-akka-flow.md pack/manifest.yaml pack/README.md pack/AGENTS.md`

## Done criteria

- Installed-pack examples and exports reinforce the new process.
- One focused commit is made.
