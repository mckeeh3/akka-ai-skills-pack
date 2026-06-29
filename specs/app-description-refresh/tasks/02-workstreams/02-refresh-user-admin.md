# TASK-ADR-02-002: Refresh User Admin workstream app-description

## Summary

Revise the User Admin workstream current-intent graph using `workstreams/user-admin-migration-plan.md` and refreshed shared foundation artifacts.

## Scope

Primary edit scope: `app-description/domains/core-starter/workstreams/user-admin/**` plus narrow runtime-validation/spec notes when needed.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/README.md`
- `specs/app-description-refresh/workstreams/user-admin-migration-plan.md`
- `specs/app-description-refresh/shared-foundation-audit.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/app-description-component-graph.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/core-saas-foundation/SKILL.md`
- current User Admin app-description files

## Skills

- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-auth-security`
- `app-description-test-specification`
- `app-description-observability`
- `app-description-ui`
- `core-saas-foundation`

## Expected outputs

- Refreshed User Admin workstream files.
- Lifecycle/source-alignment state updated, normally `stale-description-changed` unless proven no-code-impact.
- Runtime-validation expectations/scenario references updated or follow-up queued.

## Required checks

- `git diff --check`
- `rg -n "invitation|membership|role|capability|worker|actor adapter|governed tool|AuthContext|tenant|trace|runtime-validation|source-alignment" app-description/domains/core-starter/workstreams/user-admin`

## Done criteria

- User Admin has coherent worker, surface, agent, tool, policy, trace, test, realization, lifecycle, and source-alignment coverage.
- Queue is updated and committed.

## Vertical workstream contract

- Lifecycle / readiness target: description-ready for User Admin; implementation alignment updated honestly.
- Workstream / functional agent: `user-admin` / User Admin functional agent.
- Governed-tool id and exposure: invitation, membership, role/capability, access-review, support-access, audit-read tools described with adapters.
- Capability id: user-and-access-administration and auth-context/membership state.
- AuthContext / roles / tenant scope: org/SaaS admins, tenant/organization scope, denials and last-admin guard.
- Akka substrate: app-description only; map existing substrates in realization files.
- Audit/work trace requirements: admin action, invitation, denial, confirmation, requestedBy/confirmedBy traces.
- Local validation path: `git diff --check`; runtime-validation scenario references only.
