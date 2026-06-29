# Governance Policy lifecycle

Workstream id: `governance-policy`
Owning domain: `core-starter`
Current readiness: `compile-ready`
Prior ready-to-build assessment: 2026-06-26 — see `../ready-to-build-status.md`; mapped to current canonical `compile-ready` lifecycle value.
Implementation alignment: `partially-aligned` at source-evidence level; runtime-ready is not claimed.
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — refreshed Governance/Policy to current policy lifecycle, decision-card, governed-tool, adapter, trace, test, and runtime-validation intent.
Last alignment review: 2026-06-29 — `TASK-ADIA-02-004` reviewed backend/frontend/test/runtime-validation source evidence and split aligned slices from gaps.
Last compile: unknown
Last manual runtime test: unknown

## Current alignment posture

This workstream is partially aligned at source-evidence level. Backend source and tests now show a deterministic Governance/Policy proposal lifecycle for dashboard/inventory/detail reads, inert draft/submit, advisory simulation, human decision recording, approved activation with simulation and rollback metadata gates, rollback recording, outcome notes, scoped denials, idempotent replays, browser-safe redaction, and provider/runtime fail-closed impact-analysis tasks. Frontend contract/source evidence renders the dashboard, inventory, proposal, simulation, decision, impact-analysis task/result, outcome, and system-message surfaces through the workstream shell.

This posture is not runtime-ready. The refreshed current intent still exceeds proven implementation for canonical governed-tool ids, exception review, runtime effective-policy enforcement across downstream actions, separation-of-duty, partial-publication transaction evidence, durable policy-decision traces through the real local Akka/API/UI path, and executed runtime-validation records.

## Blockers and assumptions

- Compile-ready means the description is sufficient to derive focused build/compile tasks; it does not claim runtime readiness.
- Source alignment is `partially-aligned` only for documented source/test/frontend slices; no manual-ready or runtime-ready claim is made.
- `specs/runtime-validation/scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md` is authored but not run, so there are no run records for the decision-card path.
- Existing implementation uses legacy action/governed-tool naming such as `action-governance-policy-draft-proposal`, `governance.policy.propose`, `list-policy-proposals`, `draft-policy-proposal`, and `approve-activate-or-rollback-policy` alongside newer intent terms such as `governance.policy.draft`, `submit_for_approval`, `approve`, `activate`, `rollback`, and `review_exception`; this is a source-alignment gap until aliases or canonical ids are reconciled.
- Exception review/grant/revoke/expire and runtime effective-policy checks are current intent but are not proven as dedicated service/API/UI lifecycle paths in the reviewed source.
- Model-backed Governance/Policy impact analysis is provider/runtime dependent and intentionally fails closed when the Akka AutonomousAgent/provider path is unavailable; missing provider configuration is not a successful normal runtime path.
- Human approval and decision-card requirements are current intent for authority expansion, approval-gate changes, exception grants, activation, rollback, trace visibility/retention changes, and behavior-shaping managed-agent policy changes.
- Mandatory foundation policy types are limited to agent/tool authority, approval gates, exception policy, runtime enforcement simple values, model/governed-document activation policy, and trace visibility/retention policy. Business-domain policy types remain extension-owned.
- Hard platform security controls remain non-overridable.
- Future runtime-validation must exercise the real Akka/API/UI path for policy draft, simulation, approval, activation, rollback, exception, denial, partial-failure, traces, and tenant isolation before readiness moves beyond source-evidence claims.

## Runtime-validation references

Required future validation scenarios are specified in `tests/coverage.md` and mapped through `realization/api-contracts.md`, `realization/frontend-routes.md`, and `realization/akka-components.md`. Runtime-validation scaffold now includes `specs/runtime-validation/scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md` with `executionStatus: authored-not-run` and `readinessClaim: not-run`. `git diff --check` plus mapped source/test/frontend proof commands were the only validation for this alignment task.

## Next recommended action

Execute or convert the Governance/Policy follow-up queue entries for the authored decision-card runtime-validation scenario, canonical governed-tool/action id reconciliation, exception lifecycle, runtime policy-decision trace/effective-policy enforcement, activation/rollback partial-failure and separation-of-duty coverage, and provider-backed impact-analysis validation before claiming manual-ready or runtime-ready.
