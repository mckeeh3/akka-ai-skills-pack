# Intent-driven usage flow

Use the repository in this sequence:

1. **Read the input artifact first**
   - PRD
   - requirements doc
   - user story
   - process description
   - API sketch
   - UI brief
   - other spec file

2. **Decompose before coding**
   - start with `../skills/akka-solution-decomposition/SKILL.md`
   - identify the Akka component set, boundaries, and delivery model

3. **Resolve focused structural decisions**
   - use `../skills/akka-entity-type-selection/SKILL.md` when the remaining question is Event Sourced Entity vs Key Value Entity
   - use other focused routing in `../skills/README.md` when the solution shape is partly known but one design choice is still open

4. **Load only the focused implementation skills**
   - use `../skills/README.md`
   - read only the Stage 3 skills that match the chosen components

5. **Generate code and tests last**
   - treat the accepted solution plan as the implementation contract for downstream work
   - for durable multi-session work, materialize follow-on tasks as `specs/pending-tasks.md`
   - execute one pending task per fresh context with `../skills/akka-do-next-pending-task/SKILL.md`
   - implement component by component
   - use `../src/` examples and focused `../docs/` references as pattern support

## Concrete example

For a small canonical requirements-to-plan example, see:
- `prd-to-akka-flow.md`
- `examples/purchase-request-prd.md`
- `examples/purchase-request-solution-plan.md`

## Rule of thumb

Code generation is a downstream phase.
Do not start writing Akka components until decomposition is complete and any key structural decisions are resolved.
Use the accepted plan to drive the coding work queue.
For reliable follow-on work across sessions, use `specs/pending-tasks.md` and run one task at a time with `akka-do-next-pending-task`.
When requirements change after the queue exists, update the maintained specs before coding: use `akka-change-request-to-spec-update` for bounded changes, `akka-revised-prd-reconciliation` for revised PRDs, and `akka-pending-task-queue-maintenance` for queue hygiene.

For queue templates, see:
- `solution-plan-to-implementation-queue.md`
- `pending-task-queue.md`
