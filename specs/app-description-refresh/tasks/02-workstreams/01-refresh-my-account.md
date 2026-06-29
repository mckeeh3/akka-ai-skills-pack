# TASK-ADR-02-001: Refresh My Account workstream app-description

## Summary

Revise the My Account workstream current-intent graph using `workstreams/my-account-migration-plan.md` and refreshed shared foundation artifacts.

## Scope

Primary edit scope: `app-description/domains/core-starter/workstreams/my-account/**` plus narrow runtime-validation/spec notes when needed.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/workstreams/my-account-migration-plan.md`
- `specs/app-description-refresh/shared-foundation-audit.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- current My Account app-description files

## Skills

- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-auth-security`
- `app-description-test-specification`
- `app-description-observability`
- `app-description-ui`

## Expected outputs

- Refreshed My Account workstream files.
- Lifecycle/source-alignment state updated, normally `stale-description-changed` unless proven no-code-impact.
- Runtime-validation expectations/scenario references updated or follow-up queued.

## Required checks

- `git diff --check`
- `rg -n "worker|actor adapter|governed tool|capability|AuthContext|tenant|Organization|trace|runtime-validation|source-alignment" app-description/domains/core-starter/workstreams/my-account`

## Done criteria

- My Account has coherent worker, surface, agent, tool, policy, trace, test, realization, lifecycle, and source-alignment coverage.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: description-ready for My Account; implementation alignment updated honestly.
- Workstream / functional agent: `my-account` / My Account functional agent.
- Governed-tool id and exposure: describe only; include surface/API/chat/agent exposure where intended.
- Capability id: account context/profile and membership context capabilities.
- AuthContext / roles / tenant scope: signed-in member, tenant/organization scope, denial behavior.
- Akka substrate: app-description only; map existing substrates in realization files.
- Audit/work trace requirements: profile/context read/update/denial/agent-assistance traces.
- Local validation path: `git diff --check`; runtime-validation scenario references only.
