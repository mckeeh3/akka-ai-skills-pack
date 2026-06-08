# Realization: Akka components for Governance/Policy

Capability: `governance-policy-lifecycle`.

This map is docs-only. It points to current implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Akka / Java evidence | Notes |
|---|---|---|
| Governance policy repository and service | `src/main/java/ai/first/application/foundation/governance/DurableGovernancePolicyRepositoryEntity.java`, `GovernancePolicyRepository.java`, `AkkaGovernancePolicyRepository.java`, `GovernancePolicyService.java` | Durable policy state, decisions, activation/rollback, and outcome notes are backend-governed. |
| Policy impact/simulation worker | `src/main/java/ai/first/application/coreapp/governance/GovernancePolicyImpactService.java`, `GovernancePolicyImpactAutonomousAgent.java`, `DurableGovernancePolicyImpactTaskRepositoryEntity.java` | Produces evidence and recommendations; cannot autonomously activate high-impact policy changes. |
| Evidence tools | `GovernancePolicyEvidenceTools.java` | Agent-visible policy evidence reads must be scoped and traceable. |
| Audit/policy decision traces | `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**` | Proposal, simulation, approval, activation, rollback, override, and outcome events require trace links. |
| Workstream/API orchestration | `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Browser actions and agent proposals share backend policy authority. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/governance/DurableGovernancePolicyRepositoryEntityTest.java`
- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- `src/test/java/ai/first/application/coreapp/governance/GovernancePolicyImpactServiceTest.java`
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/governance-audit-admin-profile.contract.test.mjs`

## Gaps / caveats

- Hidden threshold changes and autonomous policy commits are forbidden; feature work must validate approval/denial paths through backend runtime.
