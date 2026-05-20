# TASK-AWSR-05-002: Align core SaaS foundation with workstream-first foundation verticals

## Goal

Update `core-saas-foundation` so the mandatory secure foundation is framed through foundation functional agents, structured surfaces, governed capabilities, and then Akka components.

## Required reads

- `skills/core-saas-foundation/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `docs/core-ai-first-saas-foundation.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/agent-workstream-design-review-checklist.md`

## Work

1. Add required reads/routing to `agent-workstream-apps` and structured-surface docs.
2. Add a compact foundation functional-agent/surface section covering at least:
   - Access/Profile;
   - User Admin;
   - Agent Admin;
   - Audit/Trace;
   - Governance/Policy;
   - Support Access and Billing where relevant.
3. Ensure first-slice implementation order says user-facing foundation work must be modeled as surfaces/actions mapped to capabilities before component selection.
4. Update output checklist so missing foundation functional-agent/surface semantics block full-core readiness.
5. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- `rg -n "agent-workstream-apps|functional agent|structured surface|Access/Profile|User Admin|Agent Admin|Audit/Trace|Governance/Policy" skills/core-saas-foundation/SKILL.md`

## Done criteria

- Core foundation can no longer plausibly route through objects/components without foundation workstream verticals.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align core foundation workstream verticals`
