# Surfaces: Audit/Trace

Audit/Trace is the cross-workstream investigation workstream for scoped audit events, work traces, authorization denials, provider/model/tool failures, policy decisions, governed agent evidence, support-access evidence, export decisions, and correlation timelines. It owns `audit-trace-agent`; other workstreams may link to Audit/Trace evidence, but trace links are only references until reauthorized by Audit/Trace capabilities.

## Surface bindings

| Surface id | Type | Surface contract | Purpose |
|---|---|---|---|
| `surface-audit-trace-dashboard` | `dashboard` | `audit.trace.dashboard.v1` | Investigation command center with scoped counters, failure/denial attention, redaction summary, and authorized entry points. |
| `surface-audit-trace-search` | `list-search` | `audit.trace.search.v1` | Scoped trace search across audit events, workstream logs, agent traces, denials, provider failures, policy decisions, and evidence categories. |
| `surface-audit-trace-detail` | `detail-edit` as read-only evidence | `audit.trace.detail.v1` | Browser-safe trace/event evidence detail with authorization basis, redacted summary, omitted fields, and trace links. |
| `surface-audit-trace-timeline` | `audit-timeline` | `audit.trace.timeline.v1` | Correlation timeline linking auth context, capability checks, agent/tool/model/policy events, decisions, failures, and source surfaces. |
| `surface-audit-trace-failure-evidence` | `detail-edit` as read-only evidence | `audit.trace.failureEvidence.v1` | Denial/provider/tool/model/runtime failure evidence with safe reason, recovery, policy refs, and redacted related events. |
| `surface-audit-trace-investigation-guide` | `decision-card` | `audit.trace.investigationGuide.v1` | Human investigation guidance with allowed/disabled actions, risk, next steps, and no authority expansion. |
| `surface-audit-trace-export-request` | `decision-card` | `audit.trace.exportRequest.v1` | Policy-gated scoped redacted export request; unredacted browser export is forbidden by default. |
| `surface-audit-trace-investigation-note` | `system-message` | `audit.trace.investigationNote.v1` | Result of a human investigation note append; notes annotate traces only and never mutate source traces, policy, authorization, or retained evidence. |
| `surface-audit-trace-summary-progress` | `workflow-status` | `audit.trace.summaryProgress.v1` | Audit summary AutonomousAgent worker progress or fail-closed provider/runtime/tool-boundary blocker. |
| `surface-audit-trace-summary-review` | `decision-card` | `audit.trace.summaryReview.v1` | Human review of a real model-backed redacted advisory summary; accept/reject records review evidence only. |

Legacy `surface-audit-timeline` links are compatibility aliases for Audit/Trace timeline/detail routing. New work should target `surface-audit-trace-*` ids.

## Frontend-safe payloads

Common fields on every surface: `surfaceContract`, selected `AuthContext` summary, tenant/customer scope, capability ids, correlation id, trace ids/refs, redaction metadata, omitted field keys, recovery text for blocked/denied states, and no raw secrets.

- Dashboard: `cards[]`, `attentionItems[]`, `readiness`, `capabilityIds[]`, `redaction`, `sections[]`, authorized actions for search, timeline, failure evidence, investigation guide, redacted export, and summary worker start.
- Search: `query`, `rows[]`, `pageInfo`, `partial`, `redaction`. Rows include only safe `traceId`, `correlationId`, `eventKind`, `actor`, `workstream`, `severity`, `status`, and redacted summary; row activation opens detail/timeline through backend actions.
- Detail/failure evidence: `traceId`, `eventKind`, `timestamp`, `actor`, `source`, `authorizationBasis`, `decision`, `safeReason`, `redactedEvidence`, `relatedEvents[]`, `policyRefs[]`, `userActionableNextSteps[]`, and `redactionMetadata`.
- Timeline: `correlationId`, `nodes[]` or `events[]`, `partial`, `omittedCategories[]`, and `redactionSummary`.
- Export request: `exportId`, `status=approval_required`, `requestedFormat`, `reasonSummary`, `policyDecision`, `bundleMetadata`, allowed/disabled actions, trace links, and redaction summary.
- Investigation note: note summary, target trace id, idempotency key, retained-authority statement, and redaction metadata.
- Summary progress/review: task id, autonomous-agent task id where present, lifecycle status, progress, blockers/provider failures, evidence categories, trace refs, no-direct-mutation safety, and human decision state.

Forbidden payload/content: raw JWT/session data, WorkOS/provider identifiers unless policy-safe, provider/model credentials, raw prompts, hidden prompt text, raw skills/reference bodies, raw tool payloads, invitation tokens, full email bodies, hidden tenant/customer identities/counts, cross-scope evidence, and unredacted export bodies.

## Actions

| Action id | Governed capability/tool | Result behavior |
|---|---|---|
| `action-audit-trace-dashboard` | `audit.trace.dashboard.read` | Refresh dashboard with scoped counters and trace refs. |
| `action-audit-trace-search` | `audit.trace.search` / `search-audit-traces` | Return `surface-audit-trace-search` with redacted scoped rows or validation/denial. |
| `action-audit-trace-detail` | `audit.trace.detail.read` / `read-trace-detail` | Return detail or `not_found_or_redacted` without enumerating hidden traces. |
| `action-audit-trace-timeline` | `audit.trace.timeline.read` | Return correlation timeline with hidden categories omitted. |
| `action-audit-trace-failure-evidence` | `audit.trace.failureEvidence.read` | Return redacted denial/provider/tool/model/runtime evidence. |
| `action-audit-trace-investigation-guide` | `audit.trace.investigationGuide.read` | Return next-step decision guidance and disabled unsafe actions. |
| `action-audit-trace-request-redacted-export` | `audit.trace.export.request` / `request-redacted-export` | Return policy-gated export decision surface; no browser-side unredacted export. |
| `action-audit-trace-append-investigation-note` | `audit.trace.investigation_note.append` / `draft-investigation-note` | Idempotently append human note annotation and return system message. |
| `action-audit-trace-summary-task-start` | `audit.trace.summary_task.start` | Start or fail-closed a governed summary worker; no model-less success. |
| summary read/accept/reject/open-evidence actions | `audit.trace.summary_task.*` | Read/review advisory summaries; accept/reject records review evidence only. |

Every consequential browser action has a stable action id, maps to capability `audit-and-trace-investigation`, carries correlation/idempotency where needed, recomputes authorization server-side, emits audit/work traces for allow/deny/no-op/failure, and returns a typed surface or safe system message. Frontend visibility is advisory only.

## Cross-workstream reusable placements

- My Account may link to a user's own trace refs through `my_account.view_own_trace_refs`.
- User Admin links to Audit/Trace for access, invitation, role, support-access, identity exception, and access-review evidence.
- Agent Admin links to Audit/Trace for PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolPermissionBoundary denials, model/provider readiness, behavior proposals, and AgentWorkTrace.
- Governance/Policy links to Audit/Trace for policy proposal, simulation, approval, activation, rollback, impact-analysis, and outcome-note evidence.

Opening any trace link reauthorizes through Audit/Trace capabilities. Hidden/cross-scope traces return `not_found_or_redacted` or a safe system message and do not reveal hidden ids or counts.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, hidden/not-found (`not_found_or_redacted`), conflict, stale/reconnect, partial-data, provider-fail-closed, model-fail-closed, tool-boundary-denied, approval-required, no-op/idempotent replay, recorded, blocked-provider-or-runtime, and failure states where applicable. All states preserve selected tenant/customer scope, browser-safe redaction, trace/correlation links, and recovery guidance.

## Sufficiency review

This surface graph is sufficient for implementation cleanup: concrete surface ids, payload expectations, action ids, governed capabilities, redaction/export rules, cross-workstream reuse, backend authorization behavior, and test obligations are explicit enough that generators and developers should not invent alternate Audit/Trace dashboards, export paths, trace detail semantics, or frontend-only authorization.
