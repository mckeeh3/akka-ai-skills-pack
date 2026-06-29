# Workstream: Governance/Policy

## Purpose

Manage the foundation governance-policy lifecycle for AI-first SaaS behavior: policy catalog visibility, draft changes, simulation evidence, human approval/denial decisions, activation, exceptions, rollback, and policy-decision evidence across tenant-scoped workstreams.

Governance/Policy is the human-governed control plane for behavior-shaping policy. It is not a prompt-only guardrail, an enterprise rule-language engine, or a place to override platform security. Policy changes that affect runtime behavior are versioned, simulated where consequential, reviewed on decision cards, activated only through authorized human decisions, and traceable after rollback or exception handling.

## Worker roster

- Human worker binding: [`workers/governance-policy-human-operators.md`](workers/governance-policy-human-operators.md) covers SaaS owner admins, tenant admins, policy operators, auditors, and scoped support users with role-specific authority.
- Functional-agent worker binding: [`workers/governance-policy-functional-agent-worker.md`](workers/governance-policy-functional-agent-worker.md) is the model-backed Governance/Policy assistant behind `governance-policy-agent`.
- System worker binding: [`workers/governance-policy-system-worker.md`](workers/governance-policy-system-worker.md) deterministically validates policy lifecycle commands, runs simulation/effective-policy checks, enforces approval gates, activates/rolls back versions, and emits traces.

## Functional agent

Owns `governance-policy-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions. The agent may explain policy state, draft proposals, summarize simulation findings, prepare decision-card evidence, and propose exception/rollback plans, but it does not inherit human approval or activation authority.

## Capability binding

Primary capability: `../../capabilities/governance-policy-lifecycle.md`.

The workstream graph binds each surface action, confirmed chat plan, bounded agent tool call, workflow/internal invocation, API mapping, trace, test, and realization file back to `governance-policy-lifecycle`.

## Mandatory foundation policy types

Foundation current intent includes these policy object categories:

- `agent_tool_authority`: whether a functional/internal agent may use a governed tool through `agent_tool_call`, whether human confirmation is required, and which scopes are allowed.
- `approval_gate`: risk/impact/confidence thresholds that require a human decision card before activation or execution.
- `exception_policy`: bounded temporary exceptions with owner, scope, expiry, reason, evidence, and review state.
- `runtime_enforcement_policy`: simple boolean/counter/limit values used by protected runtime actions and effective-policy checks.
- `model_and_governed_document_policy`: behavior-shaping prompt/skill/reference/model-policy activation rules shared with Agent Admin but approved through Governance/Policy when they expand authority.
- `trace_retention_and_visibility`: audit/work-trace visibility, redaction, and retention settings that remain subordinate to non-overridable platform controls.

Historical domain-specific policy examples remain examples/placeholders until a business-domain extension adds them under that domain. Complex scripting languages, legal compliance workflow suites, unbounded autonomous policy commits, and platform-security overrides are out of scope for the foundation workstream.

## Policy lifecycle

1. **Catalog/read:** authorized actors inspect active policy versions, pending drafts, exceptions, simulation findings, rollback candidates, and decision history.
2. **Draft:** policy operators or the Governance/Policy agent draft a versioned `PolicyProposal` with scope, clauses/values, rationale, risk, affected workstreams/tools/roles, and idempotency key.
3. **Simulate:** simulation/replay evaluates the draft against representative current-policy, trace, and affected-action evidence. Simulation produces findings, expected allow/deny/governed outcomes, risk/impact/confidence, and partial-failure details when evidence is incomplete.
4. **Decide:** human reviewers act on a decision card to approve, reject, request evidence, modify, defer, escalate, or mark exception-required. The decision card records evidence, policy clauses, simulation findings, alternatives, reviewer, rationale, and deadline/SLA where applicable.
5. **Activate:** approved policy versions are activated by backend transaction boundary. Activation records version provenance, superseded version, approval reference, affected scopes, and runtime publication status. Unapproved drafts cannot activate.
6. **Exception:** authorized reviewers may grant, deny, expire, or revoke scoped exceptions when policy allows it. Exceptions are time-bounded and trace-linked.
7. **Rollback:** authorized reviewers may roll back to a prior approved version or revoke an exception through a separate rollback decision card. Rollback is a transaction boundary and records why the previous version is restored.
8. **Runtime enforcement/evidence:** downstream workstreams cite policy-decision traces when policy affects action availability, tool execution, approval routing, denials, or exception handling.

## Approval and confirmation behavior

Human approval is required before activating policy changes that expand authority, change approval thresholds, alter agent/tool permission boundaries, affect trace visibility/retention, grant exceptions, roll back active policy, or change behavior-shaping model/prompt/skill/reference policy. Lower-risk drafts may still require explicit human confirmation when submitted through a surface or confirmed chat plan. The functional agent cannot approve or activate.

Decision-card requirements follow the workstream surface contracts: recommended action, evidence considered, simulation findings, policy clauses/guardrails, confidence, risk/impact, alternatives, uncertainty, allowed reviewer actions, governed-tool/capability ids, decision deadline when present, and trace links.

## Actor adapters and result behavior

- `surface_action`: human browser actions for catalog, draft, simulation, decision, activation, rollback, exception, and history surfaces.
- `human_chat_tool_plan`: confirmed chat plan for drafting/simulation/rollback/exception preparation and for backend execution only after exact human confirmation plus reauthorization.
- `agent_tool_call`: bounded read, draft-assist, simulation-summary, and evidence-summary calls; no autonomous approval, activation, rollback, or exception commit.
- `api_call`: protected frontend/API path for the typed Governance/Policy surfaces.
- `workflow_step`: approval, exception, activation, rollback, and expiry workflows.
- `internal_call`: runtime policy evaluator and downstream enforcement checks.

Policy activation and rollback are single-policy-version transaction boundaries. Multi-scope activation publishes per-scope results and returns result or partial-failure surfaces; failed activation or rollback does not masquerade as success. Repeated commands with the same idempotency key return the existing result without duplicate policy commits, history, or traces.

## Attention model

Backend-owned attention items include pending policy approvals, rejected or stale drafts, simulation findings requiring review, expiring exceptions, failed/partial activations, rollback recommendations, denied policy actions, and policy-decision anomalies from downstream enforcement traces. Counts feed the left rail and My Account aggregation only for authorized actors and never expose hidden tenant/customer facts, raw secrets, raw provider/model data, raw prompts, raw tool payloads, or unredacted evidence.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation for catalog/detail, draft, simulate, decision-card approval/denial, activation, rollback, exception review, runtime policy-decision traces, tenant isolation, AuthContext denials, result/partial-failure/system-message surfaces, and source-alignment evidence.
