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

2. **Apply AI-first interpretation when applicable**
   - use `../skills/ai-first-saas/SKILL.md` before CRUD or component decomposition when the input involves delegated operational work, agents, approvals, exceptions, policy-controlled automation, supervision, audit, or outcome accountability
   - identify delegated work, retained human authority, durable goals/plans, policies, decisions, traces, UI surfaces, and outcome loops only where the product intent justifies them

3. **Decompose before coding**
   - start with `../skills/akka-solution-decomposition/SKILL.md`
   - identify the Akka component set, boundaries, and delivery model
   - preserve any AI-first operating-model context in the solution plan before mapping to entities, workflows, views, agents, endpoints, or UI

4. **Resolve focused structural decisions**
   - use `../skills/akka-entity-type-selection/SKILL.md` when the remaining question is Event Sourced Entity vs Key Value Entity
   - use other focused routing in `../skills/README.md` when the solution shape is partly known but one design choice is still open

5. **Queue unresolved decisions when needed**
   - use `../skills/akka-pending-question-generation/SKILL.md` when open decisions should be answered before safe task generation or implementation
   - include AI-first blockers when the harness would otherwise guess authority, approval gates, policy/risk thresholds, evidence, trace obligations, supervision UI, evaluation, or outcome metrics
   - use `../skills/akka-do-next-pending-question/SKILL.md` to ask or reconcile one question at a time from `specs/pending-questions.md`
   - use `../skills/akka-pending-question-queue-maintenance/SKILL.md` to audit stale, duplicate, blocked, or unreconciled questions

6. **Load only the focused implementation skills**
   - use `../skills/README.md`
   - read only the Stage 3 skills that match the chosen components

7. **Generate code and tests last**
   - treat the accepted solution plan as the implementation contract for downstream work
   - for durable multi-session work, materialize unresolved decisions as `specs/pending-questions.md` before blocked implementation tasks
   - materialize follow-on tasks as `specs/pending-tasks.md` once blocking questions are resolved or explicitly deferred
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
Do not start writing Akka components until AI-first applicability has been classified, decomposition is complete, and any key structural decisions are resolved.
Use the accepted plan to drive the coding work queue.
For reliable follow-on work across sessions, use `specs/pending-questions.md` for design blockers and `specs/pending-tasks.md` for implementation work. Answer one question at a time with `akka-do-next-pending-question`, then run one task at a time with `akka-do-next-pending-task`.
When requirements change after the queue exists, update the maintained specs before coding: use `akka-change-request-to-spec-update` for bounded changes, `akka-revised-prd-reconciliation` for revised PRDs, `akka-pending-question-queue-maintenance` for clarification hygiene, and `akka-pending-task-queue-maintenance` for task queue hygiene.

For queue templates, see:
- `solution-plan-to-implementation-queue.md`
- `pending-question-queue.md`
- `pending-task-queue.md`
