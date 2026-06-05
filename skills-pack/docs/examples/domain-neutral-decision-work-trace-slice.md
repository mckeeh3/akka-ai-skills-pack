# Domain-neutral decision/work-trace slice example plan

This is the compact P0 executable example shape for future implementation. It avoids app-specific verticals while demonstrating the AI-first semantics that ordinary Akka mechanics examples do not cover.

## Goal

Prove an evidence-backed decision can be recommended, gated by policy/human authority, approved or rejected idempotently, traced completely, queried by supervisors, and linked to an outcome without unauthorized side effects.

## Minimal substrate

- `DecisionRecordEntity` as an Event Sourced Entity.
  - Commands: `ProposeDecision`, `RequireApproval`, `ApproveDecision`, `RejectDecision`, `RecordOutcome`, `SuppressDuplicate`.
  - Events: `DecisionProposed`, `ApprovalRequired`, `DecisionApproved`, `DecisionRejected`, `OutcomeRecorded`, `DuplicateSuppressed`.
  - State carries decision id, tenant/customer scope, capability id, actor ids, evidence refs, policy version, risk/confidence, approval status, outcome refs, trace ids, and idempotency keys.
- `DecisionApprovalWorkflow` for recommendation → policy gate → pause for approval when required → record outcome link.
- `DecisionQueueView` for pending approvals, completed decisions by capability/status, and trace lookup.
- `DecisionTraceView` or trace projection for policy, evidence, approval/rejection, tool/model, and outcome timeline facts.
- HTTP endpoint or service facade with backend authorization and browser-safe DTOs.

## Optional model seam

Use a deterministic test-only recommendation provider or `TestModelProvider` in tests, but keep the normal runtime shape compatible with governed agent invocation. Do not present a model-less normal path as feature completion.

## Acceptance tests

- authorized proposer can create a decision with evidence refs, policy version, risk/confidence, and trace id;
- duplicate propose/approve/reject commands are idempotent and do not duplicate side effects;
- high-risk or policy-matched decisions pause for human approval;
- unauthorized or cross-tenant approval/rejection is denied and traceable;
- approval and rejection persist actor, basis, timestamp, policy/evidence refs, and correlation id;
- outcome recording links to the decision and preserves historical policy/evidence facts;
- views project pending/completed/trace rows with tenant/customer filtering, pagination, redaction, and forbidden access behavior;
- local Akka/API test path exercises the entity/workflow/view/endpoint route, not only pure unit fixtures.

## Out of scope

Do not add a product domain such as finance, procurement, fleet, or supplies. Do not add broad autonomous external-agent control. Keep the example small enough to remain a reusable semantic reference.
