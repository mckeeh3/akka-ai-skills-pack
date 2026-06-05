# PRD 4: Audit Workstream PRD

## 1. PRD identity

- **PRD name:** Audit Functional Agent Workstream PRD
- **Scope:** Tenant/customer-scoped audit, work-trace, prompt/skill/tool trace search and detail investigation surfaces with role-based redaction, sensitive-read auditing, and optional approval-gated export.
- **Functional agent workstream:** `functional_agent.audit`.
- **Goals:** Let authorized auditors and admins answer who/what/when/why/how-authorized for identity, auth, user admin, agent admin, prompt/skill/tool, denials, sensitive reads, and consequential actions.
- **Non-goals:** Raw database dump; unrestricted export; editing audit records; replacing external SIEM integrations.
- **Dependencies on other PRDs:** Requires Foundation audit event writer and AuthContext. Consumes User Admin and Agent Admin events/traces.

## 2. Actors and authority

- **User roles:** `AUDITOR` for scoped audit read; `TENANT_ADMIN` for tenant admin audit; `SAAS_OWNER_ADMIN` for platform-safe audit and support-access records; specialized `SECURITY_REVIEWER` may approve exports.
- **System/internal actors:** `AuditEventWriter`, `TraceRedactionService`, `AuditExportWorkflow`, `AuditRetentionTimedAction`, `AuditProjectionConsumer`.
- **Functional agent:** `functional_agent.audit` helps summarize audit timelines, explain related traces, and build filter suggestions. It cannot delete/alter audit records or bypass redaction.
- **Internal agents:** optional `AuditSummaryAgent` generates summaries from redacted evidence and records AgentWorkTrace.
- **AuthContext:** selected tenant/customer or SaaS Owner support context required; export requires explicit authority and approval.
- **Tenant/customer scope:** all search/detail queries scoped by selected AuthContext; cross-tenant ids return safe not-found/denied without existence leak.
- **Capabilities:** `audit.dashboard.view`, `audit.events.search`, `audit.events.view_detail`, `audit.traces.view`, `audit.prompts.view_trace`, `audit.skills.view_trace`, `audit.tools.view_trace`, `audit.events.export.request`, `audit.events.export.approve`, `audit.sensitive_read.view`.
- **Approval/escalation:** audit export and broad date-range/high-volume queries may require approval. Viewing highly sensitive prompt/tool payloads may require elevated capability and logs a sensitive read.
- **Forbidden behavior:** no cross-tenant audit browsing; no mutation/deletion; no unredacted secrets; no export without approval; no agent access to unredacted trace payload unless capability permits.

## 3. Workstream model

- **Purpose:** continuous investigation and compliance workstream for audit evidence and traces.
- **Default entry:** `surface.audit.dashboard.v1` with event volume, denials, sensitive reads, high-risk admin actions, agent trace anomalies, export requests, and recent audit timeline.
- **Persistent composer:** accepts scoped investigation requests such as “show role changes for Alex last week” or “summarize denied skill loads”. Composer maps to search/detail/read capabilities and returns structured results; export requires explicit action and approval.
- **Items/events:** `AuditDashboardRead`, `AuditEventsSearched`, `AuditEventRead`, `RelatedWorkTraceRead`, `PromptAssemblyTraceRead`, `SkillLoadTraceRead`, `ToolTraceRead`, `SensitiveAuditRead`, `AuditExportRequested`, `AuditExportApproved`, `AuditExportGenerated`, `AuditExportDenied`, `CapabilityDenied`.
- **Trace links:** every audit detail links to related trace ids, source capability id, actor, target, policy/authorization basis, and originating workstream item.
- **Realtime/stale/reconnect:** dashboard/list indicate projection lag and latest indexed event time; detail can fetch canonical event by id; exports continue asynchronously with workflow status.

## 4. Structured surfaces

### `surface.audit.dashboard.v1`
- **Type:** audit dashboard/attention summary.
- **Purpose:** summarize audit posture and investigative entry points.
- **Placement:** default Audit workstream entry.
- **Payload:** `eventCountsByType`, `denialCounts`, `sensitiveReadCounts`, `adminActionCounts`, `agentTraceCounts`, `recentEvents[]`, `exportRequests[]`, `projectionLag`, `allowedActions`.
- **States:** loading; empty no audit events; ready; validation-error invalid filters; forbidden if missing `audit.dashboard.view`; stale/reconnect shows projection lag; success/failure via workstream items.
- **Trace/audit links:** cards open list filtered by event type/status/trace id.
- **A11y/responsive:** timeline and charts have table alternatives and text labels.

### `surface.audit.audit_list.v1`
- **Type:** searchable audit table/timeline.
- **Purpose:** filter and inspect audit events by actor, action, target, tenant, capability, trace id, status, date range.
- **Payload:** `filters {actorId, actorType, action, eventName, targetType, targetId, tenantId, customerId, capabilityId, traceId, status, from, to}`, `rows[] {auditEventId, occurredAt, actorSummary, action, targetSummary, capabilityId, status, traceId, redactionLevel}`, `page`, `projectionLag`, `allowedActions`.
- **States:** loading; empty no matches; ready; validation-error for invalid range/page/filter; forbidden; stale projection; export requested/success/failure.
- **Trace/audit:** search itself emits `AuditEventsSearched`; sensitive filters redacted in logs.
- **A11y/responsive:** keyboard filter chips; table supports screen readers; mobile timeline cards.

### `surface.audit.audit_detail.v1`
- **Type:** immutable detail/timeline/evidence surface.
- **Purpose:** inspect one audit event and related traces with role-based redaction.
- **Payload:** `auditEvent`, `actor`, `target`, `capability`, `authzDecision`, `metadata`, `redactions`, `relatedTraceIds`, `relatedEvents[]`, `workTraceSummary`, `promptTraceSummary`, `skillTraceSummary`, `toolTraceSummary`, `allowedActions`.
- **States:** loading; empty not found; ready; validation-error invalid id; forbidden/redacted; stale if related trace projection lag; success/failure for trace expansion.
- **Trace/audit:** opening detail emits `AuditEventRead`; expanding sensitive trace emits specific sensitive-read audit.
- **A11y/responsive:** structured definition lists; copy IDs buttons accessible; redactions explained.

## 5. Surface actions

| Action id | Label | Intent | Inputs | Capability id | Required authority | Idempotency | Side effects | Audit events | Success | Failure/denial | Approval |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `action.audit.refresh_dashboard` | Refresh audit summary | Load dashboard | filters | `audit.dashboard.summary` | `audit.dashboard.view` | read-only | sensitive read audit if needed | `AuditDashboardRead` | summary | denial | no |
| `action.audit.search_events` | Search audit | Find audit events | filters/page | `audit.events.search` | `audit.events.search` | read-only | `AuditEventsSearched` | `AuditEventsSearched` | rows | validation/denial | broad search may require approval |
| `action.audit.open_detail` | Open detail | View event detail | auditEventId | `audit.events.view_detail` | `audit.events.view_detail` | read-only | sensitive read audit | `AuditEventRead` | detail | not found/denied | no |
| `action.audit.open_related_work_trace` | View work trace | Inspect AgentWorkTrace/workflow trace | traceId | `audit.traces.view` | `audit.traces.view` | read-only | sensitive read audit | `RelatedWorkTraceRead` | trace detail | redacted/denied | no |
| `action.audit.view_prompt_trace` | View prompt trace | Inspect assembly refs/redacted prompt info | promptTraceId | `audit.prompts.view_trace` | `audit.prompts.view_trace` | read-only | sensitive read audit | `PromptAssemblyTraceRead` | redacted trace | denied | elevated for unredacted |
| `action.audit.view_skill_trace` | View skill trace | Inspect skill load allow/deny | skillTraceId | `audit.skills.view_trace` | `audit.skills.view_trace` | read-only | sensitive read audit | `SkillLoadTraceRead` | trace | denied | no |
| `action.audit.view_tool_trace` | View tool trace | Inspect tool/data access | toolTraceId | `audit.tools.view_trace` | `audit.tools.view_trace` | read-only | sensitive read audit | `ToolTraceRead` | redacted trace | denied | elevated for payload |
| `action.audit.request_export` | Request export | Export filtered records | filters, reason, format | `audit.events.export.request` | `audit.events.export.request` | idempotency key | starts export workflow | `AuditExportRequested` | export request | validation/denied | yes |
| `action.audit.approve_export` | Approve export | Approve/deny export request | exportRequestId, decision, rationale | `audit.events.export.approve` | `audit.events.export.approve` | decision idempotent | export generated or rejected | `AuditExportApproved`/`AuditExportDenied` | export status | denied/stale | yes |

## 6. Governed backend capabilities

### `audit.dashboard.summary`
- **Class:** read/evidence.
- **Actors:** Audit UI, AuditSummaryAgent.
- **Scope:** selected tenant/customer/SaaS Owner support context.
- **DTOs:** `{filters?, timeRange?}` -> counts, recent events, lag, export queue.
- **Validation:** time range bounded; scope matches AuthContext.
- **Data:** reads AdminAuditView, TraceSearchView, ExportRequestView.
- **Side effects:** audit dashboard read event, with sensitive flag if showing sensitive-read counts.
- **Tests:** scoped counts, forbidden tenant, projection lag display.

### `audit.events.search`
- **Class:** read/evidence/trace.
- **Actors:** browser, AuditSummaryAgent.
- **DTOs:** `{actor?, action?, target?, tenantId?, customerId?, capabilityId?, traceId?, status?, from?, to?, pageSize, pageToken}` -> `{rows[], nextPageToken, redactions, projectionLag}`.
- **Validation:** filters in scope; max date range/page size; broad/high-volume searches may require approval or explicit reason.
- **Data:** scoped AdminAuditView.
- **Side effects:** records `AuditEventsSearched`; redacts sensitive metadata.
- **Idempotency:** read-only; same query stable by indexed time.
- **Tests:** all filters, pagination, cross-tenant denial, search audit event.

### `audit.events.view_detail`
- **Class:** read/evidence/trace.
- **DTOs:** `{auditEventId, includeRelated?}` -> detail DTO.
- **Validation:** event scope accessible; role redaction applied.
- **Data:** reads immutable audit event, related trace refs, related events.
- **Side effects:** `AuditEventRead`; if detail includes PII/prompt/tool payload then `SensitiveAuditRead`.
- **Tests:** redacted auditor vs tenant admin, not-found without existence leak, sensitive read audit.

### `audit.traces.view`
- **Class:** read/evidence.
- **DTOs:** `{traceId, traceType, redactionPreference?}` -> trace timeline.
- **Validation:** trace scope accessible; elevated capability for unredacted payload.
- **Data:** AgentWorkTrace, workflow trace, capability trace, tool/data access trace.
- **Side effects:** `RelatedWorkTraceRead` and sensitive read as applicable.
- **Tests:** linked trace, denied unredacted payload, tenant isolation.

### `audit.prompts.view_trace`
- **Class:** read/evidence.
- **DTOs:** `{promptAssemblyTraceId}` -> prompt refs, active versions, compact manifest, redacted prompt excerpts if allowed.
- **Validation:** caller has prompt trace capability; prompt content redaction rules.
- **Audit:** `PromptAssemblyTraceRead`, `SensitivePromptTraceRead` when content revealed.
- **Tests:** redacted by default, cross-tenant denied, active version links.

### `audit.skills.view_trace`
- **Class:** read/evidence.
- **DTOs:** `{skillLoadTraceId}` -> allowed/denied, skill id/version/checksum, agent id, auth basis.
- **Audit:** `SkillLoadTraceRead`.
- **Tests:** denied readSkill trace visible, skill body redacted unless elevated.

### `audit.tools.view_trace`
- **Class:** read/evidence.
- **DTOs:** `{toolTraceId}` -> tool id, capability id, input/output redaction, auth decision, side effects.
- **Audit:** `ToolTraceRead`, `SensitiveToolTraceRead` when payload shown.
- **Tests:** tool payload redaction, denial trace, side-effect trace linking.

### `audit.events.export.request`
- **Class:** proposal/workflow.
- **DTOs:** `{filters, reason, format: CSV|JSONL, redactionLevel, idempotencyKey}` -> export request.
- **Validation:** bounded scope/range; reason required; approval required; redaction level permitted.
- **Data:** creates AuditExportRequest and starts workflow paused for approval.
- **Side effects:** audit event; no file generated until approved.
- **Tests:** approval required, invalid broad export, idempotent duplicate.

### `audit.events.export.approve`
- **Class:** approval/workflow.
- **DTOs:** `{exportRequestId, decision, rationale}` -> export status/download metadata.
- **Validation:** approver capability; separation if requester cannot approve own export.
- **Data:** advances workflow; generated export scoped/redacted; expiry timer for download.
- **Audit:** `AuditExportApproved`, `AuditExportDenied`, `AuditExportGenerated`.
- **Tests:** approve/deny, requester self-approval policy, redacted output, download expiry.

## 7. Akka realization expectations

- **Event Sourced Entities:** `AdminAuditEventEntity` or append-only audit event records; `AuditExportRequestEntity` for approval/export lifecycle.
- **Key Value Entities:** export file metadata/status; redaction policy config if current-state only.
- **Workflows:** `AuditExportWorkflow` with request -> approval -> generate -> expire; optional broad-search approval workflow.
- **Views:** `AdminAuditView`, `AuditDashboardView`, `TraceSearchView`, `AuditExportRequestView`.
- **Consumers:** consume audit events/traces from user admin, agent admin, runtime, authz; enrich search indexes.
- **Timed Actions:** export link expiry; retention checks if configured.
- **Agents:** `AuditSummaryAgent` read-only, redacted evidence tools, no export approval.
- **HTTP endpoints:** dashboard, search, detail, trace detail, export request/approval/status/download.
- **SSE/WebSocket:** optional export workflow status updates; polling acceptable for first release.
- **Frontend:** dashboard, list, detail/timeline, trace expansion panels, redaction indicators, export request/approval cards.

## 8. Internal agents, workflows, and event-driven processing

- **AuditSummaryAgent:** summarizes filtered audit events/traces using redacted evidence. Tool boundary read-only: dashboard/search/detail/trace within AuthContext.
- **Model/tool boundaries:** no unredacted payload unless caller and agent boundary allow; no export, delete, mutation tools.
- **Workflows:** export approval workflow pauses until authorized approval; generation retries are idempotent.
- **Consumers:** project AdminAuditEvent and trace events into queryable views; preserve correlation/trace ids.
- **Timers:** expire export downloads and optionally schedule retention reports.
- **Events:** `AuditEventRecorded`, `AuditEventRead`, `SensitiveAuditRead`, `AuditExportRequested`, `AuditExportGenerated`, `AuditExportExpired`.
- **Traces:** audit searches and detail reads are themselves audit events; agent summaries record AgentWorkTrace.

## 9. Security, audit, and compliance

- Audit records immutable from application UI/API.
- Every audit/trace read is scoped and authorized; sensitive reads are separately audited.
- Tenant/customer filtering is enforced in backend views and entity reads.
- Redaction policy hides secrets, tokens, prompt/tool payloads, PII, and cross-tenant identifiers based on role.
- Exports require approval, reason, scoped filters, redaction, expiration, and audit.
- Denials are logged without leaking cross-tenant existence.
- Frontend never stores export secrets beyond short-lived download token; no provider secrets.
- Tests include forbidden/cross-tenant, redaction, sensitive read logging, export approval, projection lag, and disabled-user denial.

## 10. Acceptance criteria

- **Backend:** audit dashboard/search/detail/trace/export capabilities implemented with scope, redaction, audit-on-read, pagination, and safe denial.
- **Frontend:** audit dashboard, list, and detail surfaces render all states, filters, timeline/detail evidence, redactions, trace links, and export workflow status.
- **Auth/security:** users see only in-scope audit records; unredacted payload access requires elevated capability; export is approval-gated.
- **Audit/trace:** audit reads, sensitive reads, trace reads, denials, export requests/decisions are audited with trace ids.
- **Workflows/events/timers:** audit projections update from source events; export workflow is retry-safe and download expires.
- **Fullstack:** auditor can filter by actor/action/target/tenant/capability/trace/status/date, open detail, inspect related redacted traces, and request approved export.
- **Tests:** integration and UI tests cover filters, pagination, cross-tenant denial, forbidden roles, sensitive read logging, prompt/skill/tool trace redaction, export approval, and stale projection display.

## 11. Open questions

- What retention period and export download expiration should be enforced for the first release?
- Which roles, if any, may view unredacted prompt/tool payloads, and is two-person approval required for that access?
