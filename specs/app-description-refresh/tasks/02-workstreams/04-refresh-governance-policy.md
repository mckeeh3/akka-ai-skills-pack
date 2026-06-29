# TASK-ADR-02-004: Refresh Governance/Policy workstream app-description

## Summary

Revise the Governance/Policy workstream current-intent graph using `workstreams/governance-policy-migration-plan.md` and refreshed shared foundation artifacts.

## Scope

Primary edit scope: `app-description/domains/core-starter/workstreams/governance-policy/**` plus narrow runtime-validation/spec notes when needed.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/workstreams/governance-policy-migration-plan.md`
- `specs/app-description-refresh/shared-foundation-audit.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/ai-first-saas-policy-governance/SKILL.md`
- `.agents/skills/ai-first-saas-decision-cards/SKILL.md`
- current Governance/Policy app-description files

## Skills

- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-auth-security`
- `app-description-test-specification`
- `app-description-observability`
- `app-description-ui`
- `ai-first-saas-policy-governance`
- `ai-first-saas-decision-cards`

## Expected outputs

- Refreshed Governance/Policy workstream files.
- Policy lifecycle, approval, exception, simulation, decision evidence, and trace obligations are explicit.
- Lifecycle/source-alignment state updated, normally `stale-description-changed` unless proven no-code-impact.

## Required checks

- `git diff --check`
- `rg -n "policy|approval|decision|exception|simulation|worker|actor adapter|governed tool|capability|trace|runtime-validation|source-alignment" app-description/domains/core-starter/workstreams/governance-policy`

## Done criteria

- Governance/Policy has coherent worker, surface, agent, tool, policy, trace, test, realization, lifecycle, and source-alignment coverage.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: description-ready for Governance/Policy; implementation alignment updated honestly.
- Workstream / functional agent: `governance-policy` / Governance Policy functional agent.
- Governed-tool id and exposure: policy read/draft/simulate/approve/activate/rollback/exception tools described with adapters.
- Capability id: governance-policy lifecycle.
- AuthContext / roles / tenant scope: admin/policy operator scope, tenant isolation, approval denials.
- Akka substrate: app-description only; map existing substrates in realization files.
- Audit/work trace requirements: policy changes, decisions, simulations, exceptions, denials, rollback traces.
- Local validation path: `git diff --check`; runtime-validation scenario references only.
