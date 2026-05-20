# TASK-AWSR-01-001: Audit top-level routing alignment

## Goal

Audit the installed/source top-level routing layer for alignment with the agent workstream model and produce a gap matrix before editing routing skills.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `.agents/skills/README.md` if `.agents/` exists
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/agent-workstream-design-review-checklist.md`
- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/capability-first-backend/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`

## Work

1. Review whether top-level routing can bypass functional agents/workstreams/surfaces for generated SaaS apps.
2. Compare source skills with installed `.agents/skills/**` behavior when useful.
3. Create `specs/agent-workstream-skills-realignment/routing-gap-matrix.md` with:
   - file/skill;
   - current alignment status;
   - gap;
   - recommended change;
   - priority.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Gap matrix exists and identifies the first routing changes needed.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Audit agent workstream routing alignment`
