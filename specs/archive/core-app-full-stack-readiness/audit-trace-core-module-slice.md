# Audit/Trace Core Module Slice

## Purpose

Define the implementation-ready full-core Audit/Trace module for generated secure AI-first SaaS applications. This slice turns security audit, admin audit, managed-agent runtime traces, and governance activity into a unified tenant-scoped investigation substrate.

This is a specification slice only. Follow-up code tasks should implement the components, APIs, views, frontend surfaces, and tests without re-deciding durable state boundaries, trace field shape, authorization semantics, or redaction rules.

## Scope

Included:

- Unified append-only `AuditTraceEvent` facts for identity, authorization, admin, invitation, agent definition, prompt, skill, manifest, tool-boundary, model, data-access, decision, approval, workflow, consumer, timer, and system activity.
- Correlated `WorkTrace` timelines that group events by correlation id, work id, causation, or explicit links.
- Prompt assembly, skill load, tool invocation, model-use, AgentWorkTrace, admin audit, decision/approval, data-access, and authorization-denial event contracts.
- Tenant/customer-scoped trace search, list, detail, timeline, landing-summary, optional SSE stream, and redacted export/copy contracts.
- Backend redaction and trace-access policy: safe/sensitive/secret-never-store classifications, default-redacted export, and sensitive-detail capability checks.
- Trace access denial auditing, forbidden/not-found non-leak behavior, and trace-read auditing for sensitive detail/export where policy requires.
- Consumers/normalizers for events emitted by Access/Profile, User Admin, Invitation, Agent Admin, Prompt Governance, Skill Governance, Tool Boundary, and future Governance/Policy modules.
- Views/read models for search, work-trace list, timeline, landing cards, and optional active trace stream.
- Audit/Trace functional-agent workstream surfaces and route/deep-link contracts.
- Unit, integration, view, endpoint, redaction, tenant-isolation, correlation, frontend, and security-review tests.

Excluded and deferred:

- Evaluator agents, replay/simulation, improvement proposal generation, canary activation, rollback workflows, outcome learning, and scoring. Those belong to the Governance/Policy core module.
- SIEM integrations, legal hold, advanced retention purge, anomaly detection, and enterprise compliance exports.
- SaaS Owner cross-tenant investigation console and support-access investigation console unless a later support-access slice explicitly adds them.
- Concrete React components; this slice defines API and structured surface contracts for later UI implementation.

## Capability contracts

### `audit.trace.ingest`

- type: internal command/normalization capability.
- actors/callers: protected endpoints, entity command handlers, workflows, consumers, timers, agent runtime resolver, prompt assembly service, `readSkill` tool, tool-boundary enforcer, model gateway, admin components, and seed/bootstrap tasks.
- AuthContext: trusted internal caller plus original actor AuthContext where available; tenant/customer ids must be explicit and validated before persistence.
- side effects: append immutable `AuditTraceEvent`; update or create `WorkTrace` summary; project search/timeline/landing views; optionally publish trace notification for live stream.
- idempotency: producer supplies `sourceComponent`, `sourceEventId` or stable `traceEventId`; duplicate ingestion is a no-op or updates projection links only.
- denials: missing tenant id, unsafe secret payload, invalid category/type, missing correlation for consequential protected request, untrusted internal caller.

### `audit.trace.search`

- type: read/evidence capability.
- actors/callers: Tenant Admin, Auditor/Reviewer, Agent Steward for owned-agent scoped traces when permitted, Audit/Trace workstream UI, Governance/Policy module, future evaluation/replay readers.
- AuthContext: active selected tenant/customer context; `audit.read` for security/admin audit; `trace.read` for work/agent traces; target tenant/customer must match selected context.
- query surfaces: event search/list, work-trace search/list, event detail, work-trace summary, timeline, landing summary.
- side effects: read-only; denied attempts always emit an audit event; sensitive detail reads may emit access audit according to policy.
- redaction: server-side redaction before DTO creation; `trace.sensitive.read` required for permitted sensitive fields; secrets are never returned.
- denials: missing capability, disabled actor, cross-tenant id, out-of-scope customer id, unsupported filter attempting cross-scope enumeration, access to linked artifact without artifact permission.

### `audit.trace.export`

- type: read/export capability.
- actors/callers: Auditor/Reviewer, Tenant Admin with `trace.export`.
- AuthContext: selected tenant/customer and `trace.export`; optional `trace.sensitive.read` controls whether permitted sensitive fields may be included after explicit user confirmation.
- side effects: produce redacted export/copy payload; emit `TRACE_EXPORT_CREATED` audit event with trace id, filters, actor, redaction mode, and correlation id.
- idempotency: export request carries idempotency key when stored; copy-only response may be transient.
- denials: missing export capability, cross-tenant trace, request for secret fields, request for sensitive fields without capability or confirmation.

### `audit.trace.stream`

- type: optional streaming capability.
- actors/callers: Audit/Trace live surface, prompt/skill test console demo flow, supervised agent runtime dashboard.
- AuthContext: selected tenant/customer and `trace.stream`; stream filters are tenant-scoped server-side.
- side effects: read-only stream subscription; connection open/close may be operationally logged; denied subscription emits audit event.
- redaction: same DTO redaction path as list/detail.
- denials: missing stream capability, cross-tenant filter, unsupported high-cardinality filter, disabled actor.

### `audit.trace.redaction_policy.read`

- type: read/evidence capability.
- actors/callers: Tenant Admin, Auditor/Reviewer, Audit/Trace UI.
- AuthContext: selected tenant and `trace.read` or `audit.read`.
- side effects: none.
- output: browser-safe effective redaction policy summary; never returns secret matching rules that would help bypass detection.

## Durable trace contracts

### `AuditTraceEvent`

Recommended substrate: Event Sourced Entity or append-only trace ingestion component. A topic/consumer append flow is acceptable when the persisted event fact remains immutable and queryable. Derived views must not be the only source of audit truth.

Required fields:

| Field | Notes |
|---|---|
| `traceEventId` | Stable immutable id. |
| `tenantId`, `customerId` | Tenant required; customer optional but required for customer-scoped activity. |
| `correlationId` | Required for protected request, workflow, prompt/skill test, runtime agent, export, and denial events. |
| `causationId`, `parentEventId`, `workTraceId` | Optional links for timeline grouping. |
| `timestamp` | Server timestamp. |
| `sourceComponent`, `sourceEventId` | Producer identity for idempotency and provenance. |
| `eventCategory` | `identity`, `authorization`, `admin`, `invitation`, `agent`, `prompt`, `skill`, `tool`, `model`, `data_access`, `decision`, `approval`, `workflow`, `timer`, `consumer`, `system`. |
| `eventType` | Stable symbolic type such as `AUTH_DENIED`, `PROMPT_ASSEMBLED`, `SKILL_LOAD_ALLOWED`, `TOOL_INVOCATION_DENIED`. |
| `severity` | `INFO`, `WARNING`, `RISK`, `ERROR`. |
| `actorType` | `HUMAN`, `AGENT`, `WORKFLOW`, `TIMER`, `CONSUMER`, `SYSTEM`. |
| `actorAccountId`, `agentDefinitionId` | At least one present when applicable. |
| `targetResourceType`, `targetResourceId` | Target of the action or denial. |
| `actionName` | Capability or operation name. |
| `authorizationDecision` | `ALLOWED`, `DENIED`, `NOT_APPLICABLE`. |
| `authorizationBasisSummary` | Membership, role/capability, policy, approval, system rule, or denial reason. |
| `promptDocumentId`, `promptVersion`, `promptChecksum` | Prompt assembly/test/runtime references only; no prompt body. |
| `skillManifestId`, `skillManifestVersion` | Manifest basis for skill availability. |
| `skillDocumentId`, `skillVersion`, `skillChecksum` | Skill load or skill governance references only; no full skill content. |
| `modelConfigRefId`, `modelPolicyRefId`, `modelProviderAlias` | Safe model references; no provider secrets or raw API keys. |
| `toolName`, `toolCategory`, `toolBoundaryId`, `toolBoundaryVersion` | Tool invocation or denial basis. |
| `dataAccessSummary` | Category, target summary, row/count/classification where safe; no raw secret payloads. |
| `policyRefs`, `approvalRefs`, `decisionRefs`, `guardrailRefs` | Governance and decision evidence links. |
| `inputSummary`, `outputSummary` | Safe summaries only; sensitive fields classified and redacted in DTOs. |
| `safeMetadata`, `sensitiveMetadata` | Separate maps to make redaction mechanical. |
| `redactionClassification` | `SAFE`, `SENSITIVE`, `SECRET_REJECTED`, `MIXED_REDACTABLE`. |
| `retentionClass` | Default MVP retention bucket; no destructive purge in this module. |

Commands or internal methods:

- `recordTraceEvent(TraceEventDraft draft, InternalTraceCaller caller)` validates tenant, category, correlation, redaction, idempotency, and appends the event.
- `recordDeniedAccess(AuthContext actor, target, action, reason, correlationId)` convenience path for protected routes/components/tools/views.
- `recordPromptAssembly(PromptAssemblyTraceDraft draft)` stores prompt document/version/checksum, agent, model ref, and safe assembly metadata.
- `recordSkillLoad(SkillLoadTraceDraft draft)` stores manifest, skill version, allowed/denied basis, and safe metadata.
- `recordAgentWork(AgentWorkTraceDraft draft)` stores runtime/test invocation summary, tool/data/model refs, outcome status, and links to prompt/skill/tool events.
- `recordTraceExport(TraceExportDraft draft)` records redacted export/copy audit.

Events/facts should be immutable. Corrections are new trace events linked to the original event, not in-place edits.

### `WorkTrace`

Recommended substrate: Key Value Entity for current summary plus event timeline views, or a view-derived projection when explicit lifecycle transitions are not needed. Use Event Sourced Entity only if the implementation needs audit-grade work-trace status transitions independent of trace events.

Required fields:

| Field | Notes |
|---|---|
| `workTraceId` | Stable id, often correlation id or generated work id. |
| `tenantId`, `customerId` | Scope. |
| `correlationId` | Primary grouping id. |
| `title`, `summary` | Safe browser text. |
| `workType` | `auth`, `admin`, `invitation`, `agent_definition`, `prompt_governance`, `skill_governance`, `agent_test`, `tool_invocation`, `decision_approval`, `future_agent_execution`. |
| `status` | `ACTIVE`, `COMPLETED`, `FAILED`, `DENIED`, `PARTIAL`. |
| `initiatingActorType`, `initiatingActorId` | Human/agent/system initiator. |
| `startedAt`, `completedAt`, `lastEventAt` | Timeline bounds. |
| `linkedEventIds` | Optional bounded recent ids; full timeline comes from view query. |
| `linkedResources` | Agent, prompt, skill, invitation, membership, workflow, decision, approval refs. |
| `outcomeSummary` | Safe summary when known. |
| `highestSeverity`, `hasDeniedEvents`, `hasSensitiveFields` | Landing/list helpers. |

Projection rules:

- First event for a correlation/work id creates the `WorkTrace` summary.
- Denied or error events update status/severity without losing earlier context.
- Completion events close the trace; later correction/appeal/export events remain linked.
- Cross-tenant events with same correlation id must never merge into one visible work trace.

### `TraceRedactionPolicy`

MVP substrate: static configuration plus tests, with an optional read-only DTO. A future policy module may version this.

Rules:

- `safeMetadata` may be returned to any caller with `audit.read`/`trace.read` for the scoped event.
- `sensitiveMetadata`, sensitive summaries, and sensitive data-access details require `trace.sensitive.read` and must be omitted or masked otherwise.
- `secret-never-store` values include JWTs, session cookies, WorkOS secrets, model provider API keys, Resend API keys, invitation secret tokens, password material, raw OAuth codes, and platform credentials. Ingestion rejects these when detected.
- Prompt/skill bodies are not duplicated in trace events; store document/version/checksum references and link to governed artifact surfaces.
- Exports are redacted by default even for sensitive readers unless an explicit non-default sensitive export mode is authorized and audited.
- Denied/cross-tenant detail responses must not reveal resource existence beyond the configured forbidden/not-found policy.

## Required trace event families

| Family | Event types to support | Required references |
|---|---|---|
| Identity/auth | sign-in link, `/api/me`, context select, disabled/no-membership/forbidden denials. | account id, tenant/customer, route/action, correlation, denial reason. |
| Admin audit | membership/role/account/support/invitation allowed, denied, no-op, failed. | actor, target, previous/new state summary, policy refs, decision refs if any. |
| Invitation | create, email queued/sent/failed, resend, revoke, expire, accept, membership created. | invitation id, email summary/redaction, outbox id, workflow id. |
| Agent definition | draft/create/update/activate/disable/archive/denial. | agentDefinitionId, version/checksum, authority/model/prompt/manifest/tool refs. |
| Prompt assembly | prompt assembled/tested/runtime denied. | prompt document/version/checksum, agent, model ref, mode, compact manifest refs. |
| Skill load | `readSkill` allowed/denied/unassigned/inactive/cross-tenant. | agent, manifest version, skill id/version/checksum, mode, denial reason. |
| Tool invocation | allowed, approval-required, denied, failed, completed. | tool name/category, boundary/version, component/MCP target, idempotency key. |
| Model use | model selected, fallback denied/used if allowed, model policy denial. | modelConfigRef, modelPolicyRef, provider alias, no secret config. |
| Data access | view query/tool data lookup/component read allowed/denied. | data class, target summary, count/range if safe, capability basis. |
| Decision/approval | request created, approved, rejected, countered, escalated, expired. | decision/approval id, policy refs, evidence refs, risk/confidence summary. |
| Workflow/consumer/timer | step started/completed/failed, retry, compensation, timer fired. | workflow/timer/consumer id, causation id, target capability. |
| Trace access | trace search/detail/sensitive read/export/stream allowed or denied where auditable. | trace/work ids, filters summary, redaction mode, capability basis. |

## View and query contracts

### `AuditTraceEventSearchView`

Sources: `AuditTraceEvent` facts.

Queries:

- `searchEvents(tenantId, customerId?, timeFrom?, timeTo?, category?, eventType?, actorType?, actorId?, targetResourceType?, targetResourceId?, authorizationDecision?, severity?, correlationId?, workTraceId?, agentDefinitionId?, promptDocumentId?, skillDocumentId?, toolName?, modelConfigRefId?, pageToken?, pageSize?)`.
- `getEventById(tenantId, customerId?, traceEventId)`.
- `recentDeniedEvents(tenantId, customerId?, limit)`.
- `recentGovernanceEvents(tenantId, customerId?, limit)`.

Query rules:

- Every query includes tenant id from AuthContext server-side.
- Avoid optional-filter OR patterns in implementation; use separate query methods or generated query variants as needed.
- Sort by timestamp descending for list queries and ensure Akka View ordering constraints are satisfied by matching where predicates.
- DTO redaction is applied after fetching and before returning.

### `WorkTraceSearchView`

Sources: `WorkTrace` projection and/or `AuditTraceEvent` facts.

Queries:

- `searchWorkTraces(tenantId, customerId?, timeFrom?, timeTo?, workType?, status?, actorId?, linkedResourceType?, linkedResourceId?, hasDeniedEvents?, highestSeverity?, pageToken?, pageSize?)`.
- `getWorkTrace(tenantId, customerId?, workTraceId)`.
- `activeWorkTraces(tenantId, customerId?, workType?, limit)`.

### `WorkTraceTimelineView`

Sources: `AuditTraceEvent` facts by `workTraceId` or `correlationId`.

Queries:

- `timelineByWorkTraceId(tenantId, customerId?, workTraceId, pageToken?, pageSize?)` sorted ascending by timestamp.
- `timelineByCorrelationId(tenantId, customerId?, correlationId, pageToken?, pageSize?)` sorted ascending by timestamp.

### `AuditTraceLandingView`

Sources: event and work-trace projections.

Cards:

- recent authorization denials;
- recent admin changes;
- recent prompt/skill/agent governance changes;
- recent skill loads and denials;
- trace event counts by category/severity;
- active/failed work traces;
- quick investigation filters.

## HTTP and stream API contracts

All APIs require authenticated browser/user context, backend authorization, selected tenant/customer scope, correlation id, server-side tenant filtering, redaction, and denial auditing.

### Search/detail APIs

- `GET /api/audit/events` → `TraceEventSearchResponse`.
- `GET /api/audit/events/{traceEventId}` → `TraceEventDetailResponse`.
- `GET /api/audit/work-traces` → `WorkTraceSearchResponse`.
- `GET /api/audit/work-traces/{workTraceId}` → `WorkTraceDetailResponse`.
- `GET /api/audit/work-traces/{workTraceId}/timeline` → `WorkTraceTimelineResponse`.
- `GET /api/audit/landing` → `AuditTraceLandingResponse`.
- `GET /api/audit/redaction-policy` → browser-safe effective redaction policy summary.

### Internal ingestion APIs/functions

Prefer internal component/client functions over public HTTP:

- `TraceRecorder.recordTraceEvent(...)`.
- `TraceRecorder.recordDeniedAccess(...)`.
- `TraceRecorder.recordPromptAssembly(...)`.
- `TraceRecorder.recordSkillLoad(...)`.
- `TraceRecorder.recordAgentWork(...)`.
- `TraceRecorder.recordToolInvocation(...)`.
- `TraceRecorder.recordModelUse(...)`.
- `TraceRecorder.recordDataAccess(...)`.

A dev/test-only ingestion endpoint may exist only behind test profile/internal ACL and must not be generated as production public API.

### Optional stream/export APIs

- `GET /api/audit/events/stream` → SSE of tenant-scoped redacted trace event DTOs; requires `trace.stream`; no `ORDER BY` view query in SSE source.
- `POST /api/audit/work-traces/{workTraceId}/export` → redacted export/copy payload; requires `trace.export`; emits export audit event.

## Workstream UI surface contracts

Functional agent: `Audit/Trace`.

Minimum deep links:

- `/app/audit` landing.
- `/app/audit/events` event search/list.
- `/app/audit/events/:traceEventId` event detail.
- `/app/audit/work-traces` work trace list.
- `/app/audit/work-traces/:workTraceId` timeline detail.
- optional `/app/audit/live` realtime stream.
- optional `/app/audit/redaction-policy` read-only policy summary.

Structured surfaces:

| Surface | Payload contract | Actions |
|---|---|---|
| AuditTraceLandingSurface | summary cards, counts, recent denials, recent admin/governance changes, active failed traces, quick filters. | open filtered search, open trace, refresh. |
| TraceEventSearchSurface | filters, rows, pagination, redaction badges, forbidden/error states. | apply filters, clear filters, open event, open work trace. |
| TraceEventDetailSurface | normalized event fields, auth basis, source/provenance, artifact refs, redacted metadata, related links. | copy safe summary, open linked artifact if authorized, request sensitive view if allowed. |
| WorkTraceListSurface | work trace rows by type/status/severity/actor/linked resource. | filter, open timeline, export if permitted. |
| WorkTraceTimelineSurface | chronological grouped events, causation links, allowed/denied/warning/error states, artifact refs, redaction badges. | open event detail, copy safe summary, export, refresh. |
| RedactionPolicySurface | safe explanation of field classes, sensitive-read behavior, export defaults, secret-never-store examples. | none except copy documentation link. |
| LiveTraceSurface (optional) | stream connection state, redacted event feed, reconnect/stale state. | pause/resume, filter within authorized scope, fallback to search. |

UI requirements:

- The rail/action visibility is derived from `/api/me` capabilities; backend remains authoritative.
- States: loading, empty, filtered empty, populated, forbidden, error, stale/reconnecting for stream.
- Timeline status must not rely on color alone; badges include text labels.
- Filters, timeline entries, details, and export/copy controls are keyboard accessible.
- Long metadata values wrap/collapse safely on narrow screens.
- Frontend payloads never include provider/model/email/backend secrets, raw tokens, invitation secret tokens, or unredacted sensitive fields unless the API authorized them.

## Akka substrate routing

| Concern | Recommended substrate | Skills to load for implementation |
|---|---|---|
| Immutable trace facts | Event Sourced Entity or append-only ingestion component. | `akka-event-sourced-entities`, `akka-ese-domain-modeling`, `akka-ese-application-entity`, `akka-ese-unit-testing`, `akka-ese-integration-testing`. |
| WorkTrace current summaries | Key Value Entity or view-derived projection. | `akka-key-value-entities` when explicit summary state is implemented; otherwise `akka-views`. |
| Normalization/enrichment | Consumers from entity/workflow/topic/service-stream sources. | `akka-consumers`, source-specific consumer skills, `akka-consumer-testing`. |
| Search/list/timeline/landing | Views from trace facts and work-trace summaries. | `akka-views`, `akka-view-query-patterns`, `akka-view-testing`, optional `akka-view-streaming`. |
| Browser APIs | HTTP endpoints with request context/JWT and component clients. | `akka-http-endpoints`, `akka-http-endpoint-request-context`, `akka-http-endpoint-component-client`, `akka-http-endpoint-testing`. |
| Realtime stream | Optional SSE endpoint over trace event stream. | `akka-http-endpoint-sse`, `akka-view-streaming`, `akka-web-ui-realtime`. |
| Audit/Trace UI | React/Vite workstream surfaces. | `akka-web-ui-apps`, `akka-web-ui-api-client`, `akka-web-ui-state-rendering`, `akka-web-ui-accessibility-responsive`, `akka-web-ui-testing`. |
| Agent-specific traces | Agent runtime trace hooks. | `akka-agent-work-trace`, plus prompt/skill/tool/model governance skills for producers. |

## Security and authorization rules

- Resolve active account and selected AuthContext for every protected API.
- Require `audit.read` for security/admin audit events and `trace.read` for work/agent traces; when a response mixes both, require both or filter to authorized categories.
- Require `trace.sensitive.read` to reveal permitted sensitive fields; secret fields are never stored or displayed.
- Require `trace.export` for export/copy payloads beyond ordinary UI copy of a single safe summary.
- Require `trace.stream` for SSE stream.
- Tenant/customer ids are never accepted as authority from frontend route params alone; they are resolved from AuthContext and checked against target records.
- Cross-tenant trace id access returns the configured forbidden/not-found response without leaking whether the id exists and emits a denial event in the caller tenant context.
- Trace read access does not grant access to linked Account, AgentDefinition, PromptDocument, SkillDocument, Invitation, Membership, Decision, Approval, or domain records. Linked-detail endpoints perform their own authorization.
- Trace ingestion rejects or strips secret-never-store fields before persistence and records a safe `SECRET_REJECTED` classification when appropriate.
- Trace access itself is sensitive; denied trace access is always audited, and sensitive detail/export access is auditable by policy.

## Acceptance and test matrix

Minimum tests for generation readiness:

| Area | Required tests |
|---|---|
| Trace ingestion | representative identity/auth denial, admin membership change, invitation lifecycle, prompt activation, prompt assembly, skill load allowed, skill load denied, tool invocation allowed/denied, model policy selection/denial, data access summary, decision/approval event. |
| Correlation | protected request correlation id propagates through endpoint → command/workflow/consumer/timer where applicable → trace event → work trace timeline. |
| WorkTrace projection | related invitation or prompt/skill test events group into chronological timeline; denied/error events update status/severity. |
| Search/list | tenant-scoped filters by time/category/type/actor/target/decision/severity/correlation/agent/prompt/skill/tool/model; pagination stable. |
| Detail/timeline | event detail shows auth basis and references; timeline preserves order and causation links. |
| Authorization denial | normal member lacking `audit.read`/`trace.read` is forbidden and denial is audited; disabled user denied; role/capability denial covered. |
| Tenant isolation | Tenant A search/detail/timeline/export/stream cannot reveal Tenant B events; cross-tenant trace id denial emits safe caller-scoped denial. |
| Redaction | sensitive fields masked without `trace.sensitive.read`; permitted sensitive reader sees allowed sensitive fields; secret fields remain absent. |
| Prompt/skill traces | prompt assembly includes prompt version/checksum/model ref and no prompt body; skill load includes manifest and skill version; unassigned skill denial includes reason and no skill content. |
| Tool/model/data traces | tool boundary allowed/denied basis captured; model use references safe aliases only; data-access summaries do not expose unauthorized domain data. |
| Export | default export is redacted, includes correlation/time/event summaries/artifact refs, excludes secrets, and emits export audit event. |
| Stream (if included) | SSE sends only selected-tenant redacted events, requires `trace.stream`, handles reconnect/stale UI state. |
| UI | landing/list/detail/timeline states: loading, empty, filtered empty, populated, forbidden, error; keyboard accessible filters/timeline; responsive metadata handling. |
| Frontend secret boundary | built assets and API fixtures contain no WorkOS, Resend, model provider, invitation secret token, JWT, or backend secrets. |
| Security review | backend authorization present on every API/view/stream/export; derived views are not sole audit truth; trace read does not bypass linked-resource authorization. |

## Generation-ready checklist

- [x] Admin audit, prompt assembly, skill load, tool invocation, model use, decision/approval, data access, and denial audit event families are specified.
- [x] Durable `AuditTraceEvent`, `WorkTrace`, and `TraceRedactionPolicy` contracts are specified.
- [x] Search/list/detail/timeline/landing, optional export, and optional stream contracts are specified.
- [x] Backend authorization, tenant/customer scoping, redaction, and trace-access denial auditing rules are specified.
- [x] Akka substrate routing and focused implementation skills are specified.
- [x] Audit/Trace workstream UI surfaces, route/deep-link inventory, states, accessibility, and secret-boundary expectations are specified.
- [x] Acceptance/security test matrix is specified.

## Follow-up implementation order

1. Domain records and redaction helpers for `AuditTraceEvent`, `WorkTrace`, trace DTOs, filters, and classifications.
2. Append-only trace recorder component plus idempotency/secret-rejection tests.
3. WorkTrace projection/update path and timeline grouping tests.
4. Normalization consumers/hooks from auth/admin/invitation/agent/prompt/skill/tool/model producers.
5. Search, detail, landing, and timeline views with tenant-isolation and query tests.
6. Protected HTTP APIs, optional SSE/export endpoints, and endpoint/security tests.
7. Audit/Trace workstream frontend surfaces, typed API client, UI states, accessibility, realtime/export states if included, and frontend secret-boundary tests.
