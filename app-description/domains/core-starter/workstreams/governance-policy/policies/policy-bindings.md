# Policies: Governance/Policy

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Binding

Applies backend-authorization-default-deny, tenant-customer-isolation, redaction-and-export-governance, governed-agent-authority, human-governance gates, audit/history retention, idempotent-write safety, decision-card evidence requirements, and non-overridable platform security.

## Foundation policy categories

Governance/Policy currently governs these foundation categories:

- `agent_tool_authority` for which functional/internal agents may call governed tools through `agent_tool_call`, which tools require human confirmation, and which scopes are allowed.
- `approval_gate` for risk, confidence, impact, authority-expansion, and human-review thresholds.
- `exception_policy` for bounded temporary deviations with owner, scope, expiry, evidence, reviewer, and trace links.
- `runtime_enforcement_policy` for simple boolean/counter/limit settings used by protected runtime actions.
- `model_and_governed_document_policy` for activation rules when prompts, skills, references, rubrics, or model policy changes affect runtime behavior or authority.
- `trace_retention_and_visibility` for audit/work trace visibility and retention, subordinate to hard platform controls and Audit/Trace permissions.

Domain-specific business policies remain extension-owned until an extension adds them to its domain app-description.

## Human approval policy

Human approval is required before any policy version is activated when the proposal:

- expands human, agent, workflow, API, or internal-call authority;
- changes approval gates, risk/confidence/impact thresholds, or exception eligibility;
- grants, revokes, or extends an exception that can alter runtime enforcement;
- activates or rolls back a behavior-shaping model/prompt/skill/reference/rubric policy;
- changes trace visibility, retention, redaction, or downstream enforcement evidence;
- rolls back an active policy version or reverses a prior decision;
- affects tenant/customer/account isolation-sensitive behavior.

Approval must be recorded on a decision card with reviewer authority, selected `AuthContext`, rationale, evidence refs, simulation refs or explicit evidence-gap acknowledgement, risk/impact/confidence, alternatives when applicable, and trace links. Approval state is checked mechanically before activation, rollback, or exception commit.

## Exception policy

Exceptions are scoped, time-bounded, evidence-backed, and reviewable. An exception requires owner, affected policy/version/scope, allowed deviation, expiry, reason, reviewer, and trace refs. Expired, revoked, hidden, cross-tenant/customer, unsupported, or hard-platform-control exceptions do not authorize runtime behavior.

## Simulation policy

Simulation/replay is required before activation when a proposal changes authority, approval gates, exception eligibility, managed-agent behavior policy, trace visibility, or runtime enforcement for protected actions. Simulation may produce partial-failure findings; partial evidence must block automatic activation and appear on the decision card.

## Idempotency and transaction policy

Draft, approval request, decision, activation, rollback, and exception commands require idempotency keys. Replays return the existing result without duplicate drafts, decisions, commits, history, or traces.

Activation and rollback are policy-version transaction boundaries. Result surfaces must distinguish committed, not-committed, partial-publication, stale/conflict, denied, and failed states.

## Non-overridable controls

The following controls are hard platform controls and are not overrideable through Governance/Policy: tenant isolation, backend authorization, secret/JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, frontend secret boundaries, audit trace integrity, required human-governance gates for authority expansion, and platform integrity checks.

Policy evaluation is backend-enforced for protected reads, writes, agent/tool calls, effective-policy calculations, runtime policy decisions, redaction, and frontend-visible payloads.
