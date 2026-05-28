# TASK-REQWS-03-002: Update app-description modeling and readiness skills

## Objective

Update app-description companion skills so workstreams own attention/dashboard semantics, surfaces preserve action/capability mappings, capabilities preserve autonomous task exposure where applicable, and readiness catches missing vertical process fields.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- canonical process doc from `TASK-REQWS-02-001`
- `skills/app-description-bootstrap/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `skills/app-description-capability-modeling/SKILL.md`
- `skills/app-description-ui/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`

## In scope

- Update bootstrap/modeling/readiness guidance for attention breakdowns, dashboard defaults, surface expansion, autonomous task definitions/result surfaces, and notification mappings.
- Ensure `12-workstreams/` remains owner of app meaning and `55-ui/` remains browser realization.

## Out of scope

- Do not generate example app-description changes unless needed as small references.

## Expected outputs

- updates to focused app-description skills
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|autonomous task|AutonomousAgent|notification|surface action" skills/app-description-bootstrap/SKILL.md skills/app-description-functional-agent-modeling/SKILL.md skills/app-description-surface-modeling/SKILL.md skills/app-description-capability-modeling/SKILL.md skills/app-description-ui/SKILL.md skills/app-description-readiness-assessment/SKILL.md`

## Done criteria

- App-description maintenance can store and assess the new process concepts.
- One focused commit is made.
