# TASK-AWSR-05-005: Align AI-first companion skills with surface/action handoffs

## Goal

Update AI-first companion skills so policy, decision, audit, outcome, and admin-agent guidance produces workstream surface/action/capability handoffs instead of abstract doctrine only.

## Required reads

- `skills/ai-first-saas-policy-governance/SKILL.md`
- `skills/ai-first-saas-decision-cards/SKILL.md`
- `skills/ai-first-saas-audit-trace/SKILL.md`
- `skills/ai-first-saas-admin-agents/SKILL.md`
- `skills/ai-first-saas-outcomes-metrics/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`

## Work

1. Add compact handoff rules to the companion skills so their outputs include:
   - owning or reusable functional agent;
   - structured surface id/type where user-facing;
   - surface actions;
   - capability ids/classes;
   - AuthContext/approval/audit/trace expectations;
   - downstream Akka/frontend/test skills.
2. Focus on skills with little or no `workstream` mention first.
3. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- `rg -n "workstream|structured surface|surface action|capability"` over touched AI-first companion skills

## Done criteria

- AI-first companion outputs are implementation-ready for workstream/surface/capability planning.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align AI-first companion surface handoffs`
