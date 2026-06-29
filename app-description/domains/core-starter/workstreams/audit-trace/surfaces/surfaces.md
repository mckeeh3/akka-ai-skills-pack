# Surfaces: Audit/Trace

Audit/Trace owns role-specific dashboard, search, detail, timeline/correlation, denial investigation, support-access review, investigation summary, export request/result, and system-message surfaces. These surfaces are human execution harnesses for `tenant-admin-human` and `saas-support-human`; `audit-trace-agent` may assist through confirmed read-only chat plans and bounded model-safe agent-tool calls described in `../agents/functional-agent.md`.

## Current graph binding rules

Every consequential surface action is a `surface_action` adapter to one governed tool in capability `audit-and-trace-investigation`, normally exposed through a protected browser `api_call` handled by the Audit/Trace system worker. Matching `human_chat_tool_plan`, `agent_tool_call`, `consumer_reaction`, `projection_update`, `internal_call`, and `timer_invocation` adapters are modeled separately in `../tools/governed-tools.md` and must not bypass backend authorization, support-access scope, redaction, or trace emission.

## Surface bindings

| Surface id | Type | Surface contract | Purpose |
|---|---|---|---|
| `surface-audit-trace-dashboard` | `role-dashboard` | `audit.trace.dashboard.v2` | Attention router for investigations, denials, trace gaps, support-access reviews, runtime-validation evidence, and exports. |
| `surface-audit-trace-search` | `list-search` | `audit.trace.search.v2` | Search tenant/support-scoped audit and work traces. |
| `surface-audit-trace-detail` | `detail` | `audit.trace.detail.v2` | Authorized trace/work-trace detail with redaction and sensitive-detail states. |
| `surface-audit-trace-timeline` | `timeline-correlation` | `audit.trace.timeline.v2` | Correlation timeline across workstreams, workers, adapters, tools, policies, decisions, and runtime-validation evidence. |
| `surface-audit-trace-denial-investigation` | `investigation` | `audit.trace.denialInvestigation.v2` | Explain authorized denial evidence and safe remediation paths. |
| `surface-audit-trace-support-access-review` | `review` | `audit.trace.supportAccessReview.v2` | Review support-access grant/use/expiry/denial evidence. |
| `surface-audit-trace-investigation-summary` | `result-summary` | `audit.trace.investigationSummary.v2` | Evidence-cited summary of selected authorized traces and unknowns. |
| `surface-audit-trace-export-request` | `approval-result` | `audit.trace.exportRequest.v2` | Redacted export request, approval-required, denied, preparing, ready, expired, or failed result. |
| `surface-audit-trace-system-message` | `system-message` | `audit.trace.systemMessage.v2` | Safe validation, forbidden, redacted, approval-required, no-op, trace-gap, or failure feedback. |

## Dashboard contract

- Surface role: role-specific dashboard and attention router.
- Actors/workers: tenant admins for tenant scope; SaaS support for support-access/platform scope.
- Payload fields: `attentionItems`, `activeInvestigations`, `denialQueue`, `traceGapQueue`, `supportAccessReviewQueue`, `runtimeValidationEvidence`, `exportRequests`, `availableActions`, `authContextSummary`, `redaction`, and `correlationId`.
- Attention categories: `audit-trace.investigation`, `audit-trace.denial`, `audit-trace.trace-gap`, `audit-trace.support-access-review`, `audit-trace.runtime-validation-evidence`.

| Interaction | Action id | Governed tool | Result surface/outcome |
|---|---|---|---|
| Open trace search | `action-audit-trace-search-open` | `search-audit-traces` | `surface-audit-trace-search` |
| Open denial queue item | `action-audit-trace-denial-open` | `investigate-denied-trace-access` | `surface-audit-trace-denial-investigation` |
| Open trace-gap item | `action-audit-trace-correlation-open` | `lookup-trace-correlation` | `surface-audit-trace-timeline` or trace-gap system message |
| Open support-access review | `action-audit-trace-support-access-review-open` | `review-support-access-traces` | `surface-audit-trace-support-access-review` |
| Open runtime-validation evidence | `action-audit-trace-runtime-validation-open` | `lookup-trace-correlation` | `surface-audit-trace-timeline` or detail |

States: loading, ready, empty actionable queues, forbidden, partial-data/redacted, stale/reconnect, trace-gap, and failure.

## Search contract

- Surface role: list/search for audit/work trace records.
- User goal: find relevant trace evidence without full-payload keyword search.
- Payload fields: `query`, `filters`, `rows`, `pageInfo`, `availableActions`, `emptyState`, `validationErrors`, `redaction`, `authContextSummary`, and `correlationId`.
- Filters: date/time range, tenant/customer within scope, actor, worker/workstream, event category, action type, governed tool/capability, policy/agent/prompt/skill/reference/model refs where visible, actor adapter/source, status, and safe correlation/work-trace id.
- Rows: safe display handle, time, actor label, worker/workstream, action, adapter/source, status, deterministic summary, redaction class, customer/account label or `system/no-customer`, correlation/work-trace id, and action ids.

| Interaction | Action id | Governed tool | Result surface/outcome |
|---|---|---|---|
| Run/refresh/filter/sort/page audit traces | `action-audit-trace-search` | `search-audit-traces` | `surface-audit-trace-search`, validation-error, forbidden |
| Search work traces | `action-audit-work-trace-search` | `search-work-traces` | `surface-audit-trace-search` |
| Open selected trace detail | `action-audit-trace-detail` | `read-audit-trace-detail` | `surface-audit-trace-detail` or `not_found_or_redacted` |
| Open selected work trace | `action-audit-work-trace-detail` | `read-work-trace-detail` | `surface-audit-trace-detail` |
| Open correlation timeline | `action-audit-trace-correlation-open` | `lookup-trace-correlation` | `surface-audit-trace-timeline` |
| Request summary from selected traces | `action-audit-trace-summary-request` | `summarize-investigation-evidence` | `surface-audit-trace-investigation-summary` |
| Request redacted export | `action-audit-trace-export-request` | `request-redacted-trace-export` | `surface-audit-trace-export-request` |

Search states: loading, empty authorized result set, ready, submitting, validation-error, forbidden, partial-data/redacted, stale/reconnect, trace-gap, and failure.

## Detail contract

- Surface role: detail view for one authorized trace or work trace.
- Payload fields: `traceDisplayHandle`, `workTraceId`, `time`, `tenant`, `customerAccount`, `actor`, `worker`, `workstream`, `eventCategory`, `action`, `actorAdapterSource`, `governedToolId`, `capabilityId`, `authorizationDecision`, `authorizationBasisSummary`, `policyRefs`, `approvalRefs`, `promptSkillReferenceModelRefs`, `inputSummary`, `outputSummary`, `safePayload`, `sensitivePayloadState`, `denial`, `supportAccess`, `runtimeValidationEvidence`, `relatedTraces`, `redaction`, `availableActions`, and `correlationId`.
- Default view is safe summary and redaction status. Sensitive payload sections require explicit `trace.sensitive.read` and still omit secret-never-store material.

| Interaction | Action id | Governed tool | Result surface/outcome |
|---|---|---|---|
| Open or refresh audit trace detail | `action-audit-trace-detail` | `read-audit-trace-detail` | `surface-audit-trace-detail`, forbidden, `not_found_or_redacted` |
| Open or refresh work trace detail | `action-audit-work-trace-detail` | `read-work-trace-detail` | `surface-audit-trace-detail` |
| Follow related trace/work-trace link | `action-audit-trace-correlation-open` | `lookup-trace-correlation` | `surface-audit-trace-timeline` |
| Open denial explanation | `action-audit-trace-denial-open` | `investigate-denied-trace-access` | `surface-audit-trace-denial-investigation` |
| Summarize this trace/correlation | `action-audit-trace-summary-request` | `summarize-investigation-evidence` | `surface-audit-trace-investigation-summary` |

Detail states: loading, ready, forbidden, `not_found_or_redacted`, retention-expired, partial-data/redacted, sensitive-grant-required, stale/reconnect, and failure.

## Timeline/correlation contract

Timeline surfaces show ordered causation links across source workstreams and adapters. Payload includes `correlationId`, `workTraceId`, `timelineEvents`, `causationLinks`, `sourceWorkstreams`, `actorAdapters`, `traceGaps`, `runtimeValidationEvidence`, `redaction`, and `availableActions`.

| Interaction | Action id | Governed tool | Result surface/outcome |
|---|---|---|---|
| Build/refresh correlation timeline | `action-audit-trace-correlation-open` | `lookup-trace-correlation` | `surface-audit-trace-timeline` |
| Open event detail from timeline | `action-audit-trace-detail` | `read-audit-trace-detail` or `read-work-trace-detail` | `surface-audit-trace-detail` |
| Open trace gap diagnostics | `action-audit-trace-gap-open` | `lookup-trace-correlation` | `surface-audit-trace-system-message` or timeline with gaps |

## Denial/support-access/summary/export contracts

- Denial investigation shows authorized denial reason, policy reference, actor adapter, governed tool/capability, selected AuthContext, redaction state, safe remediation, and related trace refs.
- Support-access review shows grant id/scope/expiry, requester/approver/support actor, use/denial/export/read events, tenant-visible review status, and redaction state.
- Investigation summary shows selected evidence refs, correlation chain, denials, trace gaps, support-access/export refs, redaction disclaimer, confidence/unknowns, and recommended next human action. It is a result surface, not the source of truth.
- Export request shows requested scope, redaction level, approval state, prepared artifact safe handle if allowed, expiry, denied reason, and audit trace refs. Raw/sensitive export returns approval-required or denied unless explicit policy grants it.

## Common action, trace, and UI rules

Every action map row has a human `surface_action` adapter, governed tool id, capability id `audit-and-trace-investigation`, protected `api_call` realization path, and trace source. Matching chat/agent/system adapters are separately authorized.

Rows, filters, summaries, route params, related links, and agent text are never authorization controls. Hidden or expired targets return `not_found_or_redacted`, forbidden, approval-required, or redacted result surfaces without enumeration.

## Accessibility, responsive, and sufficiency review

- Dashboard cards, rows, filters, timeline events, detail links, support-access review controls, summary/export actions, and confirmations are keyboard-operable with visible focus.
- Status and redaction are not communicated by color alone.
- Browser assets/API payloads do not expose secrets, provider credentials, bearer/session tokens, hidden cross-tenant identifiers, or frontend-secret material.
- Surface-description sufficiency review: sufficient for current Audit/Trace implementation planning without inventing payload fields, actions, states, auth/tenant/support behavior, trace links, tests, or component semantics.
