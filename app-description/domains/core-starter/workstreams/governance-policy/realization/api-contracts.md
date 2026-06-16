# Realization: API contracts for Governance/Policy

Capability: `governance-policy-lifecycle`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `list-policy-proposals` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, governance repository/service | Scoped proposal/status list with trace links and affected-capability references. |
| `draft-policy-proposal` | `browser-tool`, `agent-tool` proposal | `AdminEndpoint.java`, `GovernancePolicyService.java`, `WorkstreamEndpoint.java` | Drafts carry rationale, impact, risk, tests, and no authority expansion by prompt. |
| `simulate-policy-change` | `browser-tool`, `agent-tool`, `internal-tool` | `GovernancePolicyImpactService.java`, `GovernancePolicyImpactAutonomousAgent.java` | Synchronous or service-backed simulation produces advisory evidence only; no approval or activation side effect. |
| `start-policy-impact-analysis` / `read-policy-impact-analysis` / `cancel-policy-impact-analysis` | `browser-tool`, selected `agent-tool` for start/read | `GovernancePolicyImpactService.java`, `GovernancePolicyImpactAutonomousAgent.java`, impact task repository | Durable advisory task lifecycle with provider/runtime fail-closed states; proposal authority unchanged. |
| `accept-policy-impact-result` / `reject-policy-impact-result` / `request-policy-impact-changes` | `browser-tool` decision | governance impact service/repository | Human disposition of advisory evidence; requires reason for reject/request-changes and never approves or activates policy. |
| `approve-activate-or-rollback-policy` | `browser-tool` approval/commit | governance repository/entity/service, admin/workstream endpoints | Explicit command mode `decide`, `activate`, or `rollback`; human/backend-policy governed commit/rollback with audit and policy-decision trace. |
| `record-policy-outcome-note` | `browser-tool` | governance service/repository | Links observed outcomes and feedback to policy decision trace without changing authority. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts` | Typed proposal, simulation, task, decision, gov diff, outcome, and system-message surfaces with correlation ids, idempotency keys, lifecycle blockers, and denials. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- `src/test/java/ai/first/application/coreapp/governance/GovernancePolicyImpactServiceTest.java`
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Future endpoint additions must keep browser, agent, internal, and workflow/timer/consumer exposures aligned to the same policy authority and trace obligations.
- Request DTOs must preserve the app-description distinction between proposal lifecycle commands, advisory simulation/impact-analysis commands, impact-result disposition, and explicit decision/activation/rollback modes.
