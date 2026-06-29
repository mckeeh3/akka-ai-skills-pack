# Realization: Akka components for Governance/Policy

Capability: `governance-policy-lifecycle`.

This map is docs-only. It points to candidate implementation evidence and does not change runtime behavior or claim alignment.

## Component and service evidence

| Intent binding | Candidate Akka / Java evidence | Notes |
|---|---|---|
| Policy catalog/version/proposal repository and service | `src/main/java/ai/first/application/foundation/governance/**`, `GovernancePolicyService.java` | Durable policy definitions, drafts/proposals, active versions, prior versions, exception state, rollback records, and history. |
| Policy lifecycle workflow | workflow services under governance/coreapp workstream packages | Approval requests, decision-card pauses, simulation jobs, activation, rollback, exception review/expiry, partial failures, and attention item updates. |
| Simulation/replay evaluator | governance policy simulation/replay service plus trace readers | Evaluates draft/rollback/exception candidates against representative policy state, trace evidence, affected actions/tools/workstreams, and evidence gaps. |
| Runtime effective-policy evaluator | governance policy service plus runtime policy-check adapters | Computes active version, matching clauses/values, exception state, approval requirement, winning scope, denials, and policy-decision traces. |
| Audit/policy lifecycle traces | `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**` | Drafts, simulations, decisions, activations, exceptions, rollback, denials, partial failures, and runtime policy decisions require trace links. |
| Workstream/API orchestration | `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Browser actions and confirmed chat plans share backend policy authority and selected `AuthContext`. |
| Agent evidence tools | `GovernancePolicyEvidenceTools.java` or successor | Agent-visible policy reads, draft assist, simulation summaries, and history must be scoped and traceable; agent has no approval/activation/rollback/exception commit tools. |
| Projection/realtime/update publication | workstream views/projections/consumers | Publishes dashboard attention, decision queues, activation/rollback result status, exception expiry, and stale/reconnect events. |

## Validation evidence

Future source-alignment/runtime-validation should map or create tests for:

- `src/test/java/ai/first/application/foundation/governance/DurableGovernancePolicyRepositoryEntityTest.java`
- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- workflow tests for approval pause/resume, decision-card outcomes, activation, rollback, exception expiry/revocation, idempotency, and partial failure
- API tests for selected AuthContext, tenant isolation, denials, hard-platform-control denial, and trace redaction
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Existing code may still implement a simpler policy settings model or older proposal/simulation/approval concepts. Treat that as candidate or stale evidence until a focused source-alignment review is completed.
- Hidden threshold changes, autonomous policy commits, unapproved activations, expired exceptions, and hard-platform-security overrides are forbidden; feature work must validate denial paths through backend runtime.
- Runtime completion requires the real Akka/API/UI path with governed agent/tool boundaries, not deterministic demos or fixture-only contract checks.
