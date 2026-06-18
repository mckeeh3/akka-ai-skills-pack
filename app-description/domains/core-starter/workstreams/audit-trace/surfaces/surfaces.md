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

## `surface-audit-trace-dashboard` contract details

- Surface role: workstream dashboard and action router for Audit/Trace investigations. The dashboard does not render passive metrics; every card, counter, row, badge, shortcut, and queue either opens a typed Audit/Trace surface through a governed backend action or is omitted when the actor is not authorized.
- Owner: Audit/Trace workstream, functional agent `audit-trace-agent`; reusable placement from other workstreams is by trace reference only and must reauthorize through Audit/Trace capabilities before dashboard data or drilldowns are returned.
- Actors and scope: tenant admins, tenant auditors/compliance reviewers, authorized support operators, and customer-scoped support actors with selected `AuthContext`, tenant id, optional customer scope, role/capability summary, and active support-access basis where required. Customer-scoped actors see only customer-authorized redacted counts and links. Unauthorized, disabled-user, expired-support-access, or cross-tenant/customer contexts receive `forbidden` or `not_found_or_redacted` outcomes without hidden ids, hidden counts, or policy internals.
- User goal: decide what investigation work needs attention now, open the least-privilege evidence surface, and request safe follow-up work without expanding authority or exposing raw evidence.
- Primary decision/action: choose an attention item or action shortcut. Success means the actor lands on an authorized search, detail, timeline, failure-evidence, investigation-guide, export-request, summary-progress, or safe system-message result with preserved tenant/customer scope and trace/correlation context.

### Dashboard payload schema

Top-level browser-safe fields for `audit.trace.dashboard.v1`:

- `surfaceContract`, `surfaceId`, `generatedAt`, `selectedScope`, `authContextSummary`, `capabilityIds`, `correlationId`, `traceRefs`, `redaction`, `readiness`, `sections`, `cards`, `attentionItems`, `actions`, `emptyState`, and `recovery`.
- `selectedScope`: tenant display label, optional customer display label, scope kind, and support-access status; never raw session data, hidden tenant/customer ids, or provider identifiers.
- `authContextSummary`: actor type, role labels, support/auditor/admin basis, and capability labels; implementation role ids and policy clause ids are diagnostic-only and never shown in the default view.
- `redaction`: summary counts and omitted field keys by category, plus safe explanation text; no raw evidence bodies, prompts, model/provider credentials, invitation tokens, or cross-scope details.
- `readiness`: `ready`, `partial`, `blocked-provider-or-runtime`, or `forbidden`, with safe recovery text and trace refs.
- `sections[]` order is fixed: first `needs-attention`, then `things-i-can-do`, then optional `recently-recorded`/`readiness-notices` if they contain clickable result targets. Non-actionable FYI metrics are excluded.
- `cards[]`: stable `cardId`, label, safe count/status/severity, short explanation, `actionId`, governed capability, result surface id, keyboard label, `emptyBehavior`, and optional redaction badge. Valid zero-count cards open an empty authorized queue or explanation surface.
- `attentionItems[]`: stable `itemId`, event category, severity, workstream/source label, safe actor label, age/status, redacted summary, `actionId`, result surface id, trace ref, and recovery/next-step text.
- `actions[]`: stable action id, browser label, capability id, approval requirement if any, idempotency/correlation expectation, result surface/system message, and hidden/denied reason category for audit logs. Hidden actions are normally omitted from the default dashboard.

Default visible content is limited to investigation summaries, counts, severity/status, user-actionable next steps, scope labels, and redaction explanations. On-demand role-gated drilldowns may show diagnostic trace ids, policy refs, provider/model/tool failure categories, and support-access basis. Internal-only metadata includes raw auth tokens, raw provider/model/tool payloads, raw prompts, hidden policy implementation ids, backend component names, full event bodies, and correlation/idempotency internals beyond the safe correlation label.

### Dashboard action map

| User-facing interaction | Action id | Governed capability/tool | Result surface or outcome | Notes |
|---|---|---|---|---|
| Refresh investigation command center | `action-audit-trace-dashboard` | `audit.trace.dashboard.read` | `surface-audit-trace-dashboard` | Recomputes authorization and scoped counters server-side; no frontend-only authorization. |
| Open scoped trace search from a card, queue, or shortcut | `action-audit-trace-search` | `audit.trace.search` / `search-audit-traces` | `surface-audit-trace-search` | Carries selected scope, filters, trace category, and correlation label; validation/denial returns typed state. |
| Open trace/event evidence | `action-audit-trace-detail` | `audit.trace.detail.read` / `read-trace-detail` | `surface-audit-trace-detail` or `not_found_or_redacted` | Hidden or cross-scope evidence is not enumerable. |
| Open a correlation timeline | `action-audit-trace-timeline` | `audit.trace.timeline.read` | `surface-audit-trace-timeline` | Omits categories the actor cannot inspect and explains redaction safely. |
| Inspect denial/provider/tool/model/runtime failures | `action-audit-trace-failure-evidence` | `audit.trace.failureEvidence.read` | `surface-audit-trace-failure-evidence` | Shows safe reason, recovery, policy refs, and related redacted events. |
| Open investigation guidance | `action-audit-trace-investigation-guide` | `audit.trace.investigationGuide.read` | `surface-audit-trace-investigation-guide` | Guidance is advisory and never expands authority. |
| Request a redacted export bundle | `action-audit-trace-request-redacted-export` | `audit.trace.export.request` / `request-redacted-export` | `surface-audit-trace-export-request` | Always policy-gated; unredacted browser export is forbidden by default. |
| Append an investigation note from a dashboard-selected trace | `action-audit-trace-append-investigation-note` | `audit.trace.investigation_note.append` / `draft-investigation-note` | `surface-audit-trace-investigation-note` system message | Idempotent annotation only; source traces, policies, authorization, and retained evidence are immutable from this action. |
| Start a redacted advisory summary worker | `action-audit-trace-summary-task-start` | `audit.trace.summary_task.start` | `surface-audit-trace-summary-progress` | Must invoke governed model-backed runtime or fail closed with provider/runtime/tool-boundary blocker; no model-less success state. |

### Dashboard states, traces, UI, and tests

- States: loading, empty, ready, submitting, validation-error, forbidden, `not_found_or_redacted`, stale/reconnect, partial-data, provider-fail-closed, model-fail-closed, tool-boundary-denied, approval-required, no-op/idempotent replay, recorded, blocked-provider-or-runtime, and failure. Every state preserves selected scope, redaction summary, recovery text, and trace/correlation refs when allowed.
- Audit/work traces: dashboard reads, denials, partial redaction, search/detail/timeline/failure/export/summary/note actions, idempotent replays, approval-required decisions, provider/model/tool-boundary failures, and no-op outcomes emit audit/work trace refs. Dashboard trace links are summaries until reopened and reauthorized through Audit/Trace capabilities.
- Accessibility/responsive semantics: implement with the selected web UI style guide `.agents/skills/docs/web-ui-style-guide.md`, component catalog anatomy `.agents/skills/docs/web-ui-component-catalog.md`, and named theme `core-saas-foundation`. Cards and rows are keyboard-operable controls with visible focus, ARIA labels based on safe labels, responsive single-column grouping on narrow viewports, and no color-only severity communication.
- Required app-description tests: acceptance coverage for authorized dashboard load and each visible action result; regression coverage for zero-count actionable cards and stale/reconnect recovery; security coverage for missing bearer, disabled user, role denial, support-access expiry, customer-scope restriction, cross-tenant/cross-customer redaction, hidden count non-enumeration, and frontend secret boundaries; negative coverage for invalid filters, hidden trace ids, unauthorized export, and model/provider/tool-boundary fail-closed states; idempotency coverage for note append/export/summary-start replays; observability coverage that allow/deny/no-op/failure paths record audit/work traces and preserve correlation refs; UX usefulness coverage that the default dashboard answers “what needs my attention” before “what can I do.”
- Surface-description sufficiency review: this dashboard definition is sufficiently unambiguous for implementation and review without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. No additional description pass is required for the `fully-specified` objective.

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
