# Audit/Trace and Governance/Policy Validation

## Scope

Validation artifact for `TASK-FCSR-06-001` at the selected full-core scope: Audit/Trace and Governance/Policy workstream surfaces/actions are available through backend-authorized workstream paths with scoped redaction, investigation notes, policy proposal lifecycle, simulation evidence, approval, activation/rollback, outcome notes, and fail-closed model-backed worker boundaries.

## Evidence

- `AuditTraceService` provides dashboard, scoped search, detail, correlation timeline, failure evidence, investigation guidance, and human investigation-note append surfaces for the selected `AuthContext`.
- Audit/Trace search/detail/timeline validate tenant/customer scope, emit protected read or denial traces, and return browser-safe evidence that omits raw JWTs, provider credentials, hidden prompts, raw tool payloads, invitation tokens, and provider secrets.
- Investigation notes are recorded through `audit.trace.investigation_note.append` as workstream annotations only; they do not mutate source traces, policy, authorization, or retained evidence.
- Audit summary worker start is surfaced as backend-governed progress/review state and fails closed with `blocked_provider_or_runtime` until a real governed AutonomousAgent provider/runtime/tool-boundary path is configured; no deterministic or model-less successful summary is exposed.
- `GovernancePolicyService` provides scoped dashboard, inventory, detail, proposal draft/submit/read, simulation, decision, activation, rollback, and outcome-note surfaces.
- Governance/Policy proposals are tenant/customer scoped and idempotent. Draft and submit do not mutate active authority; simulation is advisory; activation requires approval, simulation evidence, rollback metadata, backend authority, and idempotency.
- Governance decisions and outcome notes remain human-governed evidence. Approval/rejection/request-changes states are separate from activation, and rollback requires an activated proposal with retained rollback metadata.
- Governance/Policy impact-analysis worker start is surfaced as backend-governed workflow/autonomous-agent state and fails closed with provider/runtime/tool-boundary blockers; no fake `impact_ready` result is returned.
- Workstream actions route Audit/Trace and Governance/Policy requests through `WorkstreamService.runAction`, backend capability checks, selected context validation, and result surfaces rather than frontend-only authorization.
- Frontend contract tests cover structured Audit/Trace and Governance/Policy surface rendering states, trace links, redaction markers, backend-authoritative actions, approval gates, and blocked provider/runtime worker states.

## Checks run

```bash
mvn test -Dtest=AdminAuditViewTest,GovernancePolicyServiceTest,DurableGovernancePolicyRepositoryEntityTest,WorkstreamServiceTest
npm --prefix frontend test -- --run workstream-audit-trace-vertical.contract.test.mjs workstream-governance-policy-vertical.contract.test.mjs
git diff --check
```

Result: passed locally.

## Remaining production/runtime prerequisites

- Live Audit/Trace summary and Governance/Policy impact-analysis worker completion still require real model/provider configuration, governed runtime tool binding, approved `ToolPermissionBoundary` grants, `readSkill`/`readReferenceDoc` access, and durable runtime traces. Current behavior intentionally fails closed instead of fabricating successful model-backed worker results.
- Full cross-feature local Akka/API/UI runtime smoke remains in `TASK-FCSR-07-001`.
- Billing implementation remains deferred by the full-core readiness gap contract.
