# Realization: API contracts for Governance/Policy

Capability: `governance-policy-lifecycle`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `list-policy-proposals` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, governance repository/service | Scoped proposal/status list with trace links and affected-capability references. |
| `draft-policy-proposal` | `browser-tool`, `agent-tool` proposal | `AdminEndpoint.java`, `GovernancePolicyService.java`, `WorkstreamEndpoint.java` | Drafts carry rationale, impact, risk, tests, and no authority expansion by prompt. |
| `simulate-policy-change` | `browser-tool`, `agent-tool`, `internal-tool` | `GovernancePolicyImpactService.java`, `GovernancePolicyImpactAutonomousAgent.java` | Simulation produces evidence only; no activation side effect. |
| `approve-activate-or-rollback-policy` | `browser-tool` approval | governance repository/entity/service, admin/workstream endpoints | Human/backend-policy governed commit/rollback with audit and policy-decision trace. |
| `record-policy-outcome-note` | `browser-tool` | governance service/repository | Links observed outcomes and feedback to policy decision trace. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts` | Typed decision/gov diff/outcome surfaces with correlation ids and denials. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- `src/test/java/ai/first/application/coreapp/governance/GovernancePolicyImpactServiceTest.java`
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Future endpoint additions must keep browser, agent, internal, and workflow/timer/consumer exposures aligned to the same policy authority and trace obligations.
