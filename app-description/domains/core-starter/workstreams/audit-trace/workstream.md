# Workstream: Audit/Trace

## Purpose

Give authorized tenant administrators and explicitly scoped SaaS support operators a governed investigation workstream for answering **"who did what, why was it allowed or denied, and how does this correlate across workstreams?"**

Audit/Trace is the foundation workstream for audit events, agent work traces, governed-tool invocations, authorization denials, support-access review, provider/config fail-closed evidence, runtime-validation evidence, and cross-workstream correlation. It is read/investigation first: trace records are immutable until retention expiry, surface results are redacted by default, and exports/support access require explicit policy gates.

## Worker roster

Audit/Trace binds explicit workers under `workers/` so implementation tasks preserve the current skills-pack chain:

```text
worker → execution harness → actor adapter → governed tool → capability → Akka/API/frontend realization
```

- `workers/tenant-admin-human.md` — organization/tenant administrator using structured browser surfaces and confirmed read-only chat plans for tenant-scoped trace search, detail, timeline/correlation, denial investigation, summaries, and redacted export requests where allowed.
- `workers/saas-support-human.md` — SaaS owner/support operator using Audit/Trace only under platform support authority or active support-access scope; all access is reviewed, redacted by default, and traced.
- `workers/audit-trace-functional-agent-worker.md` — the user-facing Audit Trace functional agent behind `audit-trace-agent`; it may assist read-only investigations through confirmed `human_chat_tool_plan` and bounded `agent_tool_call` adapters, but cannot expand tenant/support scope, bypass redaction, approve its own export, or reveal hidden targets.
- `workers/audit-trace-system-worker.md` — deterministic backend/API/projection/consumer/runtime-validation participants that ingest traces, build search/correlation projections, enforce AuthContext and redaction, execute authorized read/export workflows, detect trace gaps, and emit evidence.

## Functional agent

The workstream owns `audit-trace-agent` as its exactly-one user-facing functional-agent binding. The agent helps authorized users navigate Audit/Trace dashboards, interpret visible denials/redactions, prepare read-only investigation plans, correlate work traces, and draft investigation summaries from authorized evidence.

The agent does not grant authority. Its tool use is limited to the workstream tool catalog, selected `AuthContext`, support-access state, redaction policy, and confirmation/approval gates described in `agents/functional-agent.md`, `tools/governed-tools.md`, and `access.md`.

## Capability binding

Primary capability: `../../capabilities/audit-and-trace-investigation.md`.

Capability scope includes audit/work trace search/read, correlation lookup, denial investigation, support-access review, runtime-validation evidence inspection, investigation summary generation from authorized evidence, and redacted export request handling where allowed.

## Investigation attention categories

Audit/Trace produces and displays backend-owned attention categories:

- `audit-trace.investigation`: saved or active investigation needing review.
- `audit-trace.denial`: high-signal authorization denial or repeated denied access attempt.
- `audit-trace.trace-gap`: missing, delayed, malformed, or uncorrelated trace evidence from a source workstream/adapter.
- `audit-trace.support-access-review`: support-access grant/use/expiry needing tenant or SaaS owner review.
- `audit-trace.runtime-validation-evidence`: runtime-validation result or gap linked as evidence for refreshed app-description/source-alignment checks.

## Role and scope summary

- Tenant/Organization admins may inspect tenant-scoped audit/work traces, denials, policy/tool evidence, agent/tool traces, and runtime-validation evidence for their selected tenant. Sensitive platform/provider internals, secrets, hidden cross-tenant ids, and support-only operational notes are redacted unless an explicit capability grants visibility.
- SaaS support/support operators may inspect tenant traces only with active support-access scope or platform support authority recorded in `AuthContext`; support access itself is searchable/reviewable evidence and never silently widens visibility.
- Customer admins, ordinary members, disabled users, inactive memberships, agents without tool grants, and unsupported API/internal callers are default-denied.

## Surface inventory

- `surface-audit-trace-dashboard`: role-specific investigation dashboard and attention router.
- `surface-audit-trace-search`: audit/work trace search and filter surface.
- `surface-audit-trace-detail`: trace detail with redacted/default and sensitive/detail states.
- `surface-audit-trace-timeline`: correlation timeline across surface actions, chat plans, agent tool calls, workflows, consumers, API, and internal events.
- `surface-audit-trace-denial-investigation`: denial reason/evidence investigation surface.
- `surface-audit-trace-support-access-review`: support-access grant/use/expiry review surface.
- `surface-audit-trace-investigation-summary`: authorized evidence summary/result surface.
- `surface-audit-trace-export-request`: redacted export request/approval/result surface where policy allows.
- `surface-audit-trace-system-message`: validation, forbidden, redacted, approval-required, no-op, trace-gap, and failure feedback.

## Readiness posture

This node captures current intent only and is `description-ready` for focused build/alignment tasks, not runtime-ready. Runtime readiness still requires local Akka/API/UI validation of authorization, redaction, trace ingestion, search/detail/timeline/correlation, denial handling, support-access review, export approval, runtime-validation evidence links, trace-gap handling, and frontend secret boundaries. Implementation/source alignment remains `stale-description-changed` until a focused source-alignment review or compile validates the refreshed graph.
