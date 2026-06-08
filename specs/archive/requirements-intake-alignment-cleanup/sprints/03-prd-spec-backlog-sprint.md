# Sprint 03: PRD, Spec, Backlog, and Queue Alignment

## Objective

Rewrite PRD/spec/backlog and pending task/question flows so implementation plans and queues preserve workstream, surface/action, capability, authority, UI, audit/trace, and runtime validation context.

## Scope

Primary files include:

- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-revised-prd-reconciliation/SKILL.md`
- `skills/akka-change-request-to-spec-update/SKILL.md`
- `skills/akka-slice-spec-to-backlog/SKILL.md`
- `skills/akka-backlog-to-pending-tasks/SKILL.md`
- `skills/akka-backlog-item-to-task-brief/SKILL.md`
- `skills/akka-pending-question-generation/SKILL.md`
- `skills/akka-pending-question-queue-maintenance/SKILL.md`
- `skills/akka-pending-task-queue-maintenance/SKILL.md`
- `skills/akka-do-next-pending-question/SKILL.md`
- `skills/akka-do-next-pending-task/SKILL.md`
- related queue docs if required

## Ordered work areas

1. Fix minimum-starter drift in solution and PRD planning.
2. Replace preferred conventional examples with AI-first workstream examples.
3. Ensure revised PRD and change-request flows detect scope/readiness and five-core/full-core changes.
4. Ensure queue/task flows reject CRUD/page/component-only tasks unless explicitly mechanics-only/internal.
5. Ensure reference governance and `readReferenceDoc` coverage is consistently represented where managed-agent expertise is planned.

## Acceptance criteria

- Planning outputs cannot skip from PRD to components for generated SaaS.
- Pending tasks inherit vertical workstream/surface/capability contracts.
- Stale User-Admin-only starter text is removed.
- `git diff --check` passes.
