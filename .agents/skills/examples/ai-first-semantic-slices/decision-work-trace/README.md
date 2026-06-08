# Decision/work-trace semantic slice

This compact example is a pack-maintainer reference for the AI-first semantics that ordinary Akka mechanics examples do not prove by themselves. It is intentionally domain-neutral and does not introduce a product vertical.

Use it when a backlog, app-description, or implementation task needs an executable acceptance shape for evidence-backed recommendations, policy gates, human authority, audit/work traces, idempotency, and outcome linkage.

## Capability contract

Capability id: `decision.review`

Actors:

- proposer: may request a decision recommendation inside an authorized tenant/customer scope;
- approver: may approve or reject high-risk or policy-gated decisions inside the same scope;
- auditor: may inspect trace rows subject to redaction and scope limits.

Required state:

- `decisionId`, `tenantId`, optional `customerId`;
- `capabilityId`, `policyVersion`, `risk`, `confidence`;
- evidence refs and redaction summary;
- status: `proposed`, `approval_required`, `approved`, `rejected`, `outcome_recorded`;
- idempotency keys for propose/approve/reject/outcome commands;
- trace ids for model/tool/policy/evidence/approval/outcome facts.

## Minimal Akka substrate

Prefer this implementation shape for a target project:

1. `DecisionRecordEntity` as an Event Sourced Entity for immutable decision history.
2. `DecisionApprovalWorkflow` when the recommendation must pass through deterministic policy gates or pause for human approval.
3. `DecisionQueueView` for pending/completed scoped decision lookup.
4. `DecisionTraceView` or existing `AgentWorkTrace`/audit projections for the decision timeline.
5. HTTP/API facade with backend authorization, browser-safe DTOs, idempotency key handling, and correlation id propagation.

## Executable acceptance path

A target implementation is not complete until a local Akka/API test or smoke path proves:

1. Authorized proposer creates a decision with evidence refs, policy version, risk/confidence, and correlation id.
2. Duplicate propose request returns or preserves the existing decision without duplicate events or side effects.
3. High-risk or policy-matched decision enters `approval_required` and exposes a pending row only to authorized approvers.
4. Cross-tenant or unauthorized approve/reject is denied and records an audit/work-trace denial.
5. Authorized approval persists approver, basis, timestamp, policy/evidence refs, and trace ids.
6. Authorized rejection persists equivalent facts and does not perform the protected side effect.
7. Outcome recording links back to the approved/rejected decision and preserves historical policy/evidence facts.
8. Views/API queries filter by tenant/customer, paginate, redact, and return forbidden/not-found semantics safely.
9. Tests exercise the entity/workflow/view/API route, not just pure fixtures.

## Test-only model seam

Tests may use `TestModelProvider`, a deterministic recommendation provider, or a stubbed agent result. Normal runtime must still be wired for governed agent invocation or fail closed with actionable provider/configuration errors. Do not expose a model-less success path as normal product behavior.

## Non-goals

- No finance/procurement/fleet/supplies domain terminology.
- No autonomous external side effects without approval gates.
- No copied root-app Java files.
- No fixture-only completion claim.
