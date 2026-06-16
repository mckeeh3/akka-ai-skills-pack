# Surfaces: Governance/Policy

## Canonical action/tool/capability matrix

All Governance/Policy browser actions use the canonical `action-governance-policy-*` ids below. Legacy fixture aliases such as `action-govpol-*` may remain in tests only and must route to the same backend capability.

| Surface action | Governed tool | Capability | Result surface | Notes |
|---|---|---|---|---|
| `action-governance-policy-dashboard` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-dashboard` | Read-only scoped dashboard. |
| `action-governance-policy-list` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-inventory` | Scoped inventory and proposal queue. |
| `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-detail` | Browser-safe policy/proposal evidence. |
| `action-governance-policy-draft-proposal` | `draft-policy-proposal` | `governance.policy.propose` | `surface-governance-policy-proposal` | Inert draft; requires idempotency. |
| `action-governance-policy-submit-proposal` | `draft-policy-proposal` | `governance.policy.propose` | `surface-governance-policy-proposal` | Moves draft into review; no authority changes. |
| `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | `surface-governance-policy-simulation` | Advisory evidence only. |
| `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | `surface-governance-policy-decision` | Human approve/reject/request-changes. |
| `action-governance-policy-activate` | `approve-activate-or-rollback-policy` | `governance.policy.activate` | `surface-governance-policy-decision` or `system-message` | Separate command; requires approved proposal, simulation evidence, rollback metadata, idempotency, and backend authority. |
| `action-governance-policy-rollback` | `approve-activate-or-rollback-policy` | `governance.policy.rollback` | `surface-governance-policy-decision` or `system-message` | Separate command; requires activated proposal and rollback metadata. |
| `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | `surface-governance-policy-outcome` | Outcome panel with feedback/metrics/evidence. |
| `action-governance-policy-start-impact-analysis` | `governance.policy.impact_analysis.start` | `governance.policy.impact_analysis.start` | `surface-governance-policy-impact-analysis-task` | Durable autonomous-agent task path; fail closed when provider/runtime is unavailable. |
| `action-governance-policy-read-impact-analysis` | `governance.policy.impact_analysis.read` | `governance.policy.impact_analysis.read` | `surface-governance-policy-impact-analysis-task` | Reads backend task projection. |
| `action-governance-policy-cancel-impact-analysis` | `governance.policy.impact_analysis.cancel` | `governance.policy.impact_analysis.cancel` | `surface-governance-policy-impact-analysis-task` | Cancels task; policy proposal unchanged. |
| `action-governance-policy-accept-impact-result` | `governance.policy.impact_analysis.accept_result` | same | `surface-governance-policy-impact-analysis-result` | Records advisory evidence disposition only. |
| `action-governance-policy-reject-impact-result` | `governance.policy.impact_analysis.reject_result` | same | `surface-governance-policy-impact-analysis-result` | Requires reason; no policy activation. |
| `action-governance-policy-request-impact-changes` | `governance.policy.impact_analysis.request_changes` | same | `surface-governance-policy-impact-analysis-result` | Requires reason; no policy activation. |

## Surface contracts

### `surface-governance-policy-dashboard` (`governance.policy.dashboard.v1`)

Pattern: `workstream-dashboard`.

Required payload: `cards`, `attentionQueues`, `authorizedActions`, `recentActivity`, `attentionItems`, `proposalLifecycle`, `capabilityIds`, `traceLinks`, `redaction`, `readiness`.

Required states: loading, empty, ready, forbidden/system-message, stale/reconnect, partial-data, blocked-provider-or-runtime.

### `surface-governance-policy-inventory` (`governance.policy.inventory.v1`)

Pattern: `list-search`.

Rows include policy/proposal id, name/title, type, status/lifecycle state, affected capability ids, source artifact, last trace id, and safe redaction metadata. Rows must never include raw prompts, provider secrets, hidden authority state, raw tool payloads, JWTs, or cross-tenant evidence.

### `surface-governance-policy-proposal` (`governance.policy.proposal.v1`)

Pattern: `governance-diff`.

Required payload: proposal id, lifecycle state, source, risk classification, required approval, before/after summaries, change rows, affected capabilities/artifacts, idempotency metadata, decision metadata, outcome notes, trace links, redaction, and `noDirectMutation=true`.

### `surface-governance-policy-simulation` (`governance.policy.simulation.v1`)

Pattern: `governance-diff`.

Simulation is advisory and cannot grant authority. Required payload includes simulation id, proposal id, scenario summary, expected allows, expected denials, warnings, confidence, evidence trace refs, activation gate summary, and `noDirectMutation=true`.

### `surface-governance-policy-decision` (`governance.policy.decision.v1`)

Pattern: `decision-card`.

Required payload: recommendation, risk score/classification, confidence, impact, affected target, policy basis, idempotency key source, evidence, alternatives, allowed actions, disabled actions, trace links, outcome notes, and `noDirectMutation=true`.

Blocked activation/rollback may return `system-message` when prerequisites are missing, or a decision card with an explicit activation blocker. It must not render as a successful activation unless backend policy state changed.

### `surface-governance-policy-outcome` (`governance.policy.outcome.v1`)

Pattern: `outcome-panel`.

Required payload: outcome id, proposal id, decision state, summary, metrics, recommendations, evidence refs, trace refs, redaction, actor, and `noDirectMutation=true`. Outcome notes link observations back to the policy decision and simulation evidence without changing authority.

### `surface-governance-policy-impact-analysis-task` (`governance.policy.impact_analysis.task.v1`)

Pattern: `workflow-status`.

Required payload: task id, optional autonomous-agent task id, proposal id, status, progress, blockers, evidence refs, trace ids, provider/runtime failures, authorized actions, activation-blocked flag, redaction, and `noDirectMutation=true`.

Allowed statuses include queued, running, blocked_provider_or_runtime, completed-review-required, cancelled, failed, accepted, rejected_result, and request_changes. Missing provider/runtime returns blocked_provider_or_runtime with no fake success.

### `surface-governance-policy-impact-analysis-result` (`governance.policy.impact_analysis.result.v1`)

Pattern: `decision-card`.

Required payload: impact task id, proposal id, overall risk, review state, summary, findings, evidence refs, trace ids, required human decisions, allowed result-disposition actions, disabled activation/rollback actions, activation-blocked flag, redaction, and `noDirectMutation=true`.

### `surface-governance-policy-system-message` (`governance.policy.system_message.v1`)

Pattern: `system-message`.

Used for authorization denials, missing selected context, missing proposal/task ids, validation errors, conflict/stale states, provider/runtime blockers, and unsupported authority expansion. Payload must include status, severity, safe reason code, recovery-oriented message, trace refs, redaction, `noFakeSuccess=true`, and `noDirectMutation=true`.

## State and action rules

Every consequential browser action carries a stable action id, maps to capability `governance-policy-lifecycle`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, outcome panel, markdown response, or safe system message.

Agents may draft, summarize, recommend, and prepare evidence. Agents and frontend state may not approve, activate, roll back, weaken security, expand authority, or fabricate provider/model-backed analysis.

## Required tests and traces

- Frontend contract tests prove the shell renders Governance/Policy dashboard, inventory, diff, decision, workflow-status, outcome, and system-message states.
- Backend tests prove scoped reads, proposal lifecycle idempotency, simulation advisory behavior, activation/rollback prerequisites, outcome panel return, impact-task fail-closed lifecycle, and structured denial surfaces.
- Required trace evidence: policy-decision-trace, admin-audit-event, workstream-log-trace, agent-work-trace, impact-analysis worker/task events, and denial/failure traces.
