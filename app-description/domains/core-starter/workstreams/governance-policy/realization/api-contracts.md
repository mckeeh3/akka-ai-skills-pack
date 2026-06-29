# Realization: API contracts for Governance/Policy

Capability: `governance-policy-lifecycle`.

This map is docs-only candidate evidence. It does not claim runtime alignment until source review and runtime-validation pass.

## Browser/API evidence

| Tool / action | Exposure | Candidate API evidence | Contract obligations |
|---|---|---|---|
| `governance.policy.search` | `surface_action`, `api_call`, bounded `agent_tool_call`, `internal_call` | `WorkstreamEndpoint.java`, `AdminEndpoint.java`, governance policy service/repository | Scoped catalog with active/draft versions, pending approvals, simulation status, exception markers, rollback availability, action availability, and trace links. |
| `governance.policy.read` | `surface_action`, `api_call`, bounded `agent_tool_call`, `internal_call` | governance policy service/repository and runtime policy evaluator | Returns active version, drafts, clauses/values, effective decision, exceptions, rollback targets, decision evidence, and redacted policy-decision trace refs. |
| `governance.policy.draft` | `surface_action`, `api_call`, bounded `agent_tool_call` assist, confirmed `human_chat_tool_plan`, `workflow_step` | workstream/admin endpoint plus governance policy service/repository | Creates/updates draft proposal with reason, idempotency, base version, affected workstreams/tools/roles, trace, and no runtime mutation. |
| `governance.policy.simulate` | `surface_action`, `api_call`, bounded `agent_tool_call` assist, confirmed `human_chat_tool_plan`, `workflow_step`, `internal_call` | governance policy simulation/replay service and trace services | Produces simulation findings, changed decisions, risk/impact/confidence, evidence gaps, partial-failure markers, and approval gate requirements without activation. |
| `governance.policy.submit_for_approval` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step` | approval workflow and workstream endpoint | Creates decision-card review item with evidence refs, reviewer eligibility, attention item, idempotency, and trace. |
| `governance.policy.approve` | `surface_action`, `api_call`, `workflow_step` | workflow endpoint/service | Records approve/reject/request-evidence/modify/defer/escalate decision with reviewer authority, rationale, evidence refs, separation-of-duty checks, idempotency, and trace. |
| `governance.policy.activate` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step`, `internal_call` | governance policy service/repository, workflow, projection/publication service | Activates approved version with decision ref, transaction boundary, publication status, result/partial-failure surfaces, history, and runtime policy-decision trace readiness. |
| `governance.policy.rollback` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step`, `internal_call` | governance policy service/repository and workflow | Restores prior approved version or revokes problematic exception with rollback decision ref, transaction boundary, publication status, history, and traces. |
| `governance.policy.review_exception` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step` | exception workflow/service and policy evaluator | Grants/denies/revokes/expires scoped exception with owner, expiry, reason, evidence refs, runtime enforcement effect, and trace. |
| `governance.policy.read_history` | `surface_action`, `api_call`, bounded `agent_tool_call`, `internal_call` | governance service/repository, audit/workstream trace services | Scoped lifecycle timeline for drafts, simulations, decisions, activations, exceptions, rollback, denials, and runtime outcome links. |
| Workstream messages/actions/events | `surface_action`, `api_call`, realtime/projection candidate | `WorkstreamEndpoint.java`, `frontend/src/api/HttpWorkstreamApiClient.ts` | Typed dashboard, catalog, detail, draft, simulation, decision-card, exception, result, partial-failure, history, and `system_message` surfaces with correlation ids, idempotency keys, validation blockers, denials, and trace refs. |

## Validation evidence

Future source-alignment/runtime-validation should map or create tests for:

- `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java`
- `src/test/java/ai/first/application/foundation/governance/DurableGovernancePolicyRepositoryEntityTest.java`
- workflow/API tests for decision cards, activation, rollback, exceptions, tenant isolation, and denial traces
- `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `frontend/src/workstream-actions.contract.test.mjs`

## Gaps / caveats

- Existing implementation may still reflect older simple-settings or proposal/simulation/approval semantics and must be reviewed before claiming alignment.
- Request/response DTOs must preserve the refreshed app-description distinction among catalog/detail, draft, simulate, submit approval, approve/deny/request evidence, activate, rollback, exception review, history, result, partial-failure, system-message, runtime-decision traces, and hard-platform-security denials.
- Runtime-validation must exercise real Akka/API/UI paths; fixtures or demo-only paths do not satisfy completion.
