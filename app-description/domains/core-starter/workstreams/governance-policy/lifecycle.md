# Governance Policy lifecycle

Workstream id: `governance-policy`
Owning domain: `core-starter`
Current readiness: `compile-ready`
Prior ready-to-build assessment: 2026-06-26 — see `../ready-to-build-status.md`; mapped to current canonical `compile-ready` lifecycle value.
Implementation alignment: `stale-description-changed`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — refreshed Governance/Policy to current policy lifecycle, decision-card, governed-tool, adapter, trace, test, and runtime-validation intent.
Last alignment review: 2026-06-29 — source-alignment updated as description-changed/stale pending runtime/API/UI validation.
Last compile: unknown
Last manual runtime test: unknown

## Current alignment posture

This workstream is not currently aligned. The current app-description now describes the Governance/Policy functional-agent workstream as a policy lifecycle covering catalog/detail, draft, simulate, decision-card approval/denial, activation, rollback, exceptions, runtime enforcement traces, result/partial-failure/system-message surfaces, and backend-denial behavior. Existing implementation evidence may partially realize older simple-settings or proposal/simulation intent, but no entry is marked aligned until a focused source-alignment review compares this refreshed graph against source, frontend, API, tests, and real local runtime evidence.

## Blockers and assumptions

- Compile-ready means the description is sufficient to derive focused build/compile tasks; it does not claim runtime readiness.
- Source remains `stale-description-changed` because this docs-only refresh changed policy lifecycle semantics without code, API, UI, or runtime-validation execution.
- Human approval and decision-card requirements are current intent for authority expansion, approval-gate changes, exception grants, activation, rollback, trace visibility/retention changes, and behavior-shaping managed-agent policy changes.
- Mandatory foundation policy types are limited to agent/tool authority, approval gates, exception policy, runtime enforcement simple values, model/governed-document activation policy, and trace visibility/retention policy. Business-domain policy types remain extension-owned.
- Hard platform security controls remain non-overridable.
- Future runtime-validation must exercise the real Akka/API/UI path for policy draft, simulation, approval, activation, rollback, exception, denial, partial-failure, traces, and tenant isolation before implementation alignment changes.

## Runtime-validation references

Required future validation scenarios are specified in `tests/coverage.md` and mapped through `realization/api-contracts.md`, `realization/frontend-routes.md`, and `realization/akka-components.md`. `git diff --check` is the only validation run for this docs-only refresh task.

## Next recommended action

Create focused build/compile tasks from the compile-ready description. A suitable first implementation slice is Governance/Policy catalog/detail and policy draft/simulate/decision/activate flow with decision-card traces, followed by rollback/exception handling and then a source-alignment/runtime-validation review before claiming implementation alignment or runtime readiness.
