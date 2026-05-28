# Governed Autonomous Agent Tool-Boundary Example Plan

## Purpose

Define the next executable reference slice for generated-app-style governance around an Akka `AutonomousAgent` that uses backend-owned tools. The slice should prove tool permission enforcement, tenant/customer scope, approval-before-side-effect, trace recording, and fail-closed configuration behavior without building a full generated SaaS runtime or using deterministic/model-less normal runtime substitutes.

## Implementation target

Implement as a focused `src/` reference example, not starter-template work.

Rationale:
- `src/` already contains the executable Autonomous Agent examples and integration tests for this initiative.
- A starter-template implementation would require wiring the broader generated-app governed runtime, UI, and seed data surface; that is too large for a single reference slice.
- The `src/` slice can still model generated-app contracts explicitly with tenant-scoped DTOs, tool registry/boundary records, approval status, trace records, and fail-closed checks.

## Scenario

Use a small internal/background customer-risk investigation agent that may read scoped evidence and propose a follow-up action.

Suggested names:
- `GovernedRiskReviewAutonomousAgent`
- `GovernedRiskReviewTasks`
- `GovernedRiskReviewTools`
- `GovernedToolBoundaryService`
- `GovernedToolBoundaryTraceStore`
- `GovernedRiskReviewEndpoint` or component-client-only test helper if an endpoint adds noise

## Capability contracts

Use stable capability ids in code/tests:

| Capability id | Purpose | Scope | Approval/trace |
|---|---|---|---|
| `risk_review.start_autonomous_task` | start a governed Autonomous Agent task | same tenant, selected customer | trace task start or denial |
| `risk_review.query_task` | query task snapshot/result | same tenant, selected customer | trace protected result access when surfaced |
| `risk_review.read_customer_evidence` | read scoped evidence through an agent tool | same tenant, selected customer only | allowed when boundary grants read; trace allowed/denied |
| `risk_review.propose_customer_followup` | create/propose follow-up action | same tenant, selected customer only | approval required; trace `approval_required`; no side effect before approval |

## Minimum domain model

Keep the reference small and in-memory/test-oriented where possible, while preserving runtime path semantics:

- `GovernedRiskReviewRequest(tenantId, customerId, reviewId, question)`
- `GovernedRiskReviewResult(tenantId, customerId, recommendation, evidenceIds, proposedActionId)`
- `GovernedToolInvocationResult(status, message, evidence?, proposalId?)` with statuses `allowed`, `denied`, `approval_required`
- `GovernedToolPermissionBoundary(tenantId, agentId, grants, status)`
- `GovernedToolGrant(toolId, capabilityId, operation, tenantScope, approvalPolicy)`
- `GovernedToolTrace(tenantId, customerId, toolId, capabilityId, decision, reason, correlationId)`

## Tool grants and denials to prove

The agent should register only a small `@FunctionTool` facade and let the facade enforce the boundary before protected work:

1. `risk.read_customer_evidence`
   - capability: `risk_review.read_customer_evidence`
   - operation: `read`
   - allowed only for the task tenant/customer in the active boundary
   - returns redacted evidence summaries
   - emits allowed and denied traces

2. `risk.propose_customer_followup`
   - capability: `risk_review.propose_customer_followup`
   - operation: `request_approval`
   - returns `approval_required` and records a proposal/trace
   - must not commit/send/execute the follow-up action in this example

Denied paths:
- missing active boundary: fail closed before task start or before tool registration, with actionable error/trace;
- ungranted read tool: safe denied tool result and denial trace;
- cross-tenant or wrong-customer evidence request: safe denied tool result and denial trace;
- side-effecting follow-up requested when only read is granted: safe `approval_required` or denied result and no side effect.

## Test expectations

Use `TestModelProvider.AutonomousAgentTools` only as test infrastructure. Trigger the real Akka `AutonomousAgent` task path through `ComponentClient` or a narrow endpoint.

Required tests for the implementation task:

1. allowed read-only tool invocation records an allowed trace and completes a typed task result;
2. ungranted evidence tool returns safe denial, records denial trace, and does not leak evidence;
3. cross-tenant/wrong-customer evidence request is denied and traced;
4. side-effecting follow-up returns `approval_required`, records an approval trace/proposal, and does not execute the side effect;
5. missing provider/security/boundary configuration fails closed with an actionable error or failed task, not a canned model-less result.

## Non-goals

- Do not build a full generated-app governed runtime, UI, persistent repository, or starter-template implementation.
- Do not model every tool category; local function-tool facade coverage is enough for this reference.
- Do not grant authority from prompt text, task instructions, skill/reference text, or model-supplied tenant/customer ids.
- Do not treat notifications as source of truth; task snapshots/results and trace assertions remain the correctness basis.

## Queue decision

Append one implementation task, `TASK-AUTO-06-007`, before final verification. The task should implement this `src/` reference slice and update `docs/agent-coverage-matrix.md` from partial to covered only for the governed non-component facade/tool-boundary pattern proven by tests.
