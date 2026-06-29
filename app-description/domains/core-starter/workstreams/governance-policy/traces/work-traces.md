# Traces: Governance/Policy

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

Governance/Policy emits admin-audit events, policy-decision traces, policy-lifecycle traces, workflow traces, workstream-log traces, and agent-work traces.

Trace records include actor, reviewer when present, selected `AuthContext`, tenant/customer/account scope where applicable, role/capability basis, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `workflow_step`, `internal_call`), correlation id, idempotency key reference, policy id, policy category, policy version, scope selector, active/draft/rolled-back/exception state, simulation refs, decision-card refs, old/new/effective values or clause summaries, reason/rationale, approval/denial result, exception expiry, rollback target, denial/failure status, redaction decisions, result surface id, and linked workstream item.

Runtime policy-decision traces explain why an action was allowed, denied, governed, exception-authorized, approval-required, or blocked. They include active policy version, matching clause/value, winning scope, exception status, approval gate status, affected agent/workstream/tool/action/role/customer/account context, and protected action source.

Policy history links runtime outcomes influenced by a policy when practical, such as aggregated counts or trace references, without exposing hidden tenant/customer facts or raw sensitive payloads.

## Policy lifecycle trace events

Required event families:

- `governance_policy.catalog_read`
- `governance_policy.policy_read`
- `governance_policy.draft_created`
- `governance_policy.draft_updated`
- `governance_policy.simulation_started`
- `governance_policy.simulation_completed`
- `governance_policy.simulation_partial_failure`
- `governance_policy.approval_requested`
- `governance_policy.decision_recorded`
- `governance_policy.activation_started`
- `governance_policy.activation_completed`
- `governance_policy.activation_partial_failure`
- `governance_policy.exception_requested`
- `governance_policy.exception_decided`
- `governance_policy.exception_revoked_or_expired`
- `governance_policy.rollback_started`
- `governance_policy.rollback_completed`
- `governance_policy.rollback_partial_failure`
- `governance_policy.denied`
- `governance_policy.runtime_decision`

## Decision-card evidence

Decision traces must preserve the evidence available at decision time: recommended action, reviewer action, reviewer authority, evidence refs, simulation results or evidence-gap acknowledgement, policy clauses/guardrails triggered, confidence, risk/impact, alternatives, known gaps, rationale, deadline/SLA when present, and outcome follow-up. Recommendation and decision facts are separate durable records.

## `human_chat_tool_plan` trace evidence

Governance/Policy must emit durable work/audit trace facts for the `human_chat_tool_plan` adapter in addition to direct surface-action traces. Required event types are `human_chat_tool_plan.proposed`, `human_chat_tool_plan.confirmed`, `human_chat_tool_plan.step_started`, `human_chat_tool_plan.step_completed`, `human_chat_tool_plan.step_failed`, `human_chat_tool_plan.step_skipped`, and `human_chat_tool_plan.denied`.

Minimum fields: trace/work trace id, correlation id, selected `AuthContext`, functional agent `governance-policy-agent`, requestedBy, confirmedBy for execution, reviewer/decision refs when applicable, action id, governed tool id, capability id, input schema ref, plan id, plan snapshot id, idempotency key or redacted hash, authorization decision and basis summary, policy refs, result surface id, status, safe error code, redaction classification, and browser-safe input/output summaries.

Trace summaries must distinguish direct surface actions from `human_chat_tool_plan`, preserve no-mutation proposal evidence, record confirmation and per-step transaction outcomes, and omit raw provider secrets, JWTs, invitation tokens, raw email bodies, raw prompts/model payloads, hidden tenant/customer ids, raw tool payloads, and unredacted evidence from browser-visible views.

## Partial failure and rollback evidence

Simulation, activation, rollback, and exception operations must record committed, not-committed, and partial-publication states. Partial-failure traces identify safe failed scope summaries, redacted hidden target markers, retry/recovery guidance, idempotency state, and whether downstream enforcement projection was updated. Rollback traces preserve both the problematic active version and restored prior version; history is append-only.

## Denial and redaction evidence

Denials must record safe denial category, adapter/source, selected context summary, governed tool/capability, policy id/scope if visible, authorization basis, hard-platform-security reason when applicable, and result/system-message surface. Browser-visible trace summaries must not reveal hidden target existence, cross-tenant/customer facts, raw prompt/model/provider/tool payloads, raw secrets, raw correlation/idempotency internals, or protected evidence.

## Runtime-validation expectations

Future runtime-validation must prove traces for policy draft, simulation, approval/denial, activation, rollback, exception grant/deny/revoke/expire, runtime enforcement, denials, partial failures, and confirmed chat plans through the real Akka/API/UI path before the lifecycle alignment state can move beyond `stale-description-changed`.
