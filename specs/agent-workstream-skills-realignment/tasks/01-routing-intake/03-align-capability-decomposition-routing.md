# TASK-AWSR-01-003: Align capability-first and Akka decomposition routing

## Goal

Update capability-first and Akka solution decomposition guidance so capabilities are derived from functional-agent workstreams and structured surface actions before Akka component selection.

## Required reads

- `specs/agent-workstream-skills-realignment/routing-gap-matrix.md`
- `skills/capability-first-backend/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `docs/capability-first-backend-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`

## Work

1. Update `capability-first-backend` to clarify that, for generated SaaS apps, workstreams/surfaces are upstream application modeling while capabilities are the backend design substrate below them.
2. Update `akka-solution-decomposition` required output so generated SaaS plans include:
   - functional-agent model;
   - workstreams and surfaces;
   - surface/action-to-capability mapping;
   - capability-to-component mapping;
   - implementation order by vertical workstream/surface/capability increments.
3. Update docs only where needed to avoid contradictions.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- text search confirming decomposition output sections include functional agents, surfaces, capabilities, and components

## Done criteria

- Decomposition cannot plausibly skip from product intent directly to Akka components for generated SaaS apps.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align capability and decomposition routing`
