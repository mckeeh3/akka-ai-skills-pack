# Realization: Akka components for Governance/Policy

Capability: `governance-policy-lifecycle`.

This map is docs-only. It points to candidate implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Candidate Akka / Java evidence | Notes |
|---|---|---|
| Policy catalog/default/override repository and service | `src/main/java/ai/first/application/foundation/governance/**`, `GovernancePolicyService.java` | Durable simple policy definitions, SaaS defaults, tenant overrides, effective-value calculation, reset-to-default, and history. |
| Effective-policy evaluator | governance policy service plus runtime policy-check adapters | Computes winning scope, enforces finer-grained precedence, denies hard-platform-security overrides, and emits policy-decision traces. |
| Audit/policy decision traces | `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**` | Default changes, tenant overrides, reset actions, denials, and runtime policy decisions require trace links. |
| Workstream/API orchestration | `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Browser actions and confirmed chat plans share backend policy authority. |
| Agent evidence tools | `GovernancePolicyEvidenceTools.java` or successor | Agent-visible policy reads/history must be scoped and traceable; agent writes require confirmed backend execution. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/governance/DurableGovernancePolicyRepositoryEntityTest.java`
- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Existing code may still implement proposal/simulation/approval/activation concepts from older intent. Treat that as candidate or stale evidence until a focused source-alignment review is completed.
- Hidden threshold changes, autonomous policy commits, and hard-platform-security overrides are forbidden; feature work must validate denial paths through backend runtime.
