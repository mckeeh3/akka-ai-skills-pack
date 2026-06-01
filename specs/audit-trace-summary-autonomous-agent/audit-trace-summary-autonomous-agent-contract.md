# Audit/Trace Summary AutonomousAgent Contract

## Status and intent

This contract defines the bounded Audit/Trace summary worker for the starter/reference app. It is an internal/background Akka `AutonomousAgent` task vertical owned by the Audit/Trace workstream. It summarizes a scoped time window of security, authorization, provider, model, tool, workstream, attention, and agent-work trace evidence for authorized audit reviewers.

The worker is advisory. It must not mutate audit records, authorization state, provider configuration, policies, users, memberships, agent behavior, or trace retention. Human accept/reject records only the reviewer disposition of the summary.

## Owning workstream and authority boundary

| Area | Contract |
|---|---|
| Owning functional workstream | `agent-audit-trace` / Audit/Trace. |
| Primary dashboard | Audit/Trace dashboard with summary-task status card and review attention. |
| Result surface | `surface-audit-trace-summary-review` structured review surface. |
| Progress surface | `surface-audit-trace-summary-progress` structured progress/blocker surface. |
| Attention category | Audit/Trace review/configuration attention derived from backend projections and v3 events. |
| Internal workstream agent graph | `AdminAuditSummaryAgent` or equivalent Audit/Trace internal worker node launched by governed capabilities; not a left-rail request/response agent. |
| Retained human authority | Auditor/Admin reviewer decides whether to accept, reject, retry, or open evidence. The AutonomousAgent summary is evidence synthesis only. |
| Forbidden effects | No direct mutation of audit traces, AdminAuditEvent records, roles, memberships, users, tenants, customers, provider config, prompt/skill/reference docs, policy proposals, or attention state except the task-state attention emitted by the worker projection path. |

## Governed capability family

Capability prefix: `audit.trace.summary_task`.

| Capability | Governed-tool id | Actors/callers | Runtime contract |
|---|---|---|---|
| `audit.trace.summary_task.start` | `audit.trace.summaryTask.start` | Auditor, Tenant Admin, SaaS Owner Admin with selected `AuthContext`; optional scheduler/timer with system actor and tenant scope | Validate selected tenant/customer scope, required capabilities, time window, evidence categories, and idempotency key. Resolve governed managed-agent runtime/profile, model/provider config, tool boundary, `readSkill`, `readReferenceDoc`, and `auditTraceSummaryEvidence.read` grant. Create or reuse durable summary task projection. Start `ComponentClient.forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, agentInstanceId).runSingleTask(task)`. Emit `workflow.audit_trace.summary_started`, `worker.task.queued`, and `worker.task.running` when applicable. Missing provider/runtime/tool/evidence config produces blocked provider fail-closed state, not fake success. |
| `audit.trace.summary_task.read` | `audit.trace.summaryTask.read` | Same reviewers plus authorized dashboard/surface readers | Authorize read, project Akka task snapshot/result via `ComponentClient.forTask(autonomousAgentTaskId).get(...)`, return backend-owned browser-safe state, and never trust frontend state. |
| `audit.trace.summary_task.cancel` | `audit.trace.summaryTask.cancel` | Authorized reviewer/admin | Record cancellation in durable projection, attempt Akka task termination where supported, emit `workflow.audit_trace.summary_cancelled` and `worker.task.cancelled`, resolve task-state attention. |
| `audit.trace.summary_task.accept_result` | `audit.trace.summaryTask.acceptResult` | Authorized reviewer/admin | Record human acceptance/disposition of advisory result only, emit `workflow.audit_trace.summary_result_accepted` and `worker.task.accepted`, resolve task-state attention. No separate protected mutation is performed. |
| `audit.trace.summary_task.reject_result` | `audit.trace.summaryTask.rejectResult` | Authorized reviewer/admin | Record rejection reason, emit `workflow.audit_trace.summary_result_rejected` and `worker.task.rejected_result`, keep/upsert review attention. No protected domain mutation is performed. |
| `audit.trace.summary_task.open_evidence` | `audit.trace.summaryTask.openEvidence` | Authorized Audit/Trace readers | Open cited `audit.trace.detail.read`, `audit.trace.timeline.read`, or `audit.trace.failureEvidence.read` surfaces after reauthorization. Source refs do not grant authority. |

All operations must append audit/work trace facts for allowed, denied, duplicate/idempotent, blocked, failed, accepted, rejected, and cancelled outcomes.

## Task input contract

Suggested Java record: `AuditTraceSummaryTask`.

Required fields:

- `summaryTaskId` — stable starter/domain task id, e.g. `audit-summary:<tenantId>:<windowStart>:<windowEnd>:<idempotencyHash>`.
- `tenantId` and nullable `customerId` copied from trusted selected `AuthContext` or scheduler scope.
- `selectedAuthContextId`, `membershipId`, role/capability basis, and browser-safe actor summary.
- `requestedByAccountId` or system actor id.
- `windowStart` / `windowEnd` bounded to a configured maximum lookback.
- `evidenceCategories` allow-list: `admin_audit`, `authorization_denial`, `provider_readiness`, `agent_work`, `tool_invocation`, `prompt_skill_reference`, `attention`, `workstream_event`.
- `severityFloor` and optional `focusFilter`.
- `capabilityId` = `audit.trace.summary_task.start` and `governedToolId` = `audit.trace.summaryTask.start`.
- `idempotencyKey`, `correlationId`, and `causationId`.
- `evidenceRequestId` and redacted evidence source refs prepared before task start.
- governed runtime refs: managed-agent definition id/profile id, prompt assembly trace id, compact skill/reference manifest ids, tool-boundary decision id, model/provider id without secrets.
- maximum result size and maximum findings.

Validation rules:

- tenant/customer in input must match selected `AuthContext` or trusted scheduler scope;
- window must be finite and within configured retention/read limits;
- evidence categories must be allow-listed;
- no raw prompt bodies, provider secrets, raw JWTs, invitation tokens, raw tool payloads, or cross-tenant evidence may be included in task instructions.

## Evidence and redaction contract

The runtime must collect evidence through read-only, authorized loaders before and during the AutonomousAgent task:

- existing `AuditTraceService` read surfaces (`audit.trace.search`, `audit.trace.detail.read`, `audit.trace.timeline.read`, `audit.trace.failureEvidence.read`);
- `AuditTraceEvidenceTools`-style read-only evidence access, extended or wrapped as `auditTraceSummaryEvidence.read` for summary windows;
- v3 `WorkstreamEventEnvelope` refs for task/provider/attention/workflow events;
- AdminAuditEvent and AgentWorkTrace/PromptAssemblyTrace/SkillLoadTrace/ReferenceLoadTrace refs when available.

Evidence rules:

1. Evidence is scoped to selected tenant/customer and authorized capabilities.
2. Evidence is browser-safe and model-safe before insertion into instructions or tool results.
3. Redaction must omit raw JWTs, provider credentials, API keys, raw/hidden prompts, raw tool payloads, invitation tokens, support-only data, and cross-tenant/customer data.
4. Hidden or unauthorized trace ids return `not_found_or_redacted` rather than enumerating existence.
5. Result evidence citations use source refs and trace ids, not raw sensitive payloads.
6. Every evidence read emits allowed/denied trace refs; denied reads can become findings but must not leak hidden data.

## AutonomousAgent component contract

Component name: `AuditTraceSummaryAutonomousAgent`.

The implementation must:

1. extend `akka.javasdk.agent.autonomous.AutonomousAgent`;
2. implement `definition()` returning the Akka autonomous `AgentDefinition` from `define()` (distinct from the app-governed managed-agent `AgentDefinition` record);
3. accept one bounded task family via `TaskAcceptance.of(AuditTraceSummaryTasks.summarizeAuditWindow()).maxIterationsPerTask(3)` unless a lower bound is justified;
4. define a typed `Task<AuditTraceSummaryResult>` with stable name/description, `resultConformsTo(...)`, instructions, and `TaskRule<AuditTraceSummaryResult>` validation;
5. include instructions with tenant/customer scope, correlation id, capability/governed-tool id, redaction rules, evidence refs, advisory-only authority, forbidden effects, and fail-closed behavior;
6. register only authorized read-only summary evidence tools after `ToolPermissionBoundary` enforcement;
7. return typed JSON/result fields, not generic markdown.

Normal success requires the concrete Akka `AutonomousAgent` task lifecycle. Direct provider calls, deterministic service summaries, canned summaries, fixture data, simulated findings, or model-less successful results are not an implemented runtime path.

## Typed result schema

Suggested Java record: `AuditTraceSummaryResult`.

Required fields:

- `summaryTaskId`, `tenantId`, nullable `customerId`, `windowStart`, `windowEnd`, `correlationId`.
- `overallRisk`: `clear`, `watch`, `review_required`, `critical_review_required`.
- `executiveSummary` — browser-safe concise text.
- `findings`: list of typed findings:
  - `findingId`, `category`, `severity`, `title`, `safeSummary`, `evidenceRefs`, `traceRefs`, `recommendedReviewerAction`, `confidence`, `redactionApplied`.
- `providerReadinessFindings` for provider/model/tool-boundary failures and fail-closed signals.
- `authorizationDenialFindings` for denial clusters or unexpected access patterns.
- `agentWorkFindings` for prompt/skill/reference/tool/model traces and failures.
- `attentionRecommendations` — review/acknowledge/open-evidence suggestions only; no direct mutation.
- `omittedEvidenceSummary` and `redactionSummary`.
- `noDirectMutation=true`.
- `generatedAt` and `modelRuntimeRefs` without secrets.

Result validation must reject:

- missing task/scope/correlation fields;
- result scope mismatching task input;
- raw secrets, raw prompts, raw JWTs, raw tool payloads, invitation tokens, or unredacted provider credentials;
- findings without evidence refs;
- commands to mutate users, roles, policies, traces, or provider config;
- claims of provider readiness or successful model-backed work when runtime reported blocked/missing config.

## Runtime adapter and fail-closed behavior

Use `AuditTraceSummaryAutonomousAgentRuntime` as a service boundary around `ComponentClient`.

Normal adapter responsibilities:

- authorize `audit.trace.summary_task.start/read/cancel/accept_result/reject_result`;
- idempotently create/reuse durable projection;
- resolve managed-agent runtime context and model/provider/profile before task start;
- enforce `ToolPermissionBoundary` for `auditTraceSummaryEvidence.read`, `readSkill`, and `readReferenceDoc` if included;
- start the Akka task with `ComponentClient.forAutonomousAgent(...).runSingleTask(...)`;
- read snapshots/results with `ComponentClient.forTask(...).get(...)`;
- map Akka statuses to domain statuses without fabricating findings;
- persist browser-safe projection and source refs.

Fail-closed adapter responsibilities:

- when `ComponentClient`, provider/model config, governed profile, tool grants, evidence access, authorization, or runtime binding is unavailable, record `BLOCKED_PROVIDER_OR_RUNTIME` with actionable recovery text;
- emit `workflow.audit_trace.summary_blocked_provider_or_runtime` and `worker.task.blocked_provider_or_runtime` when authorized source state exists;
- upsert task-state attention for configuration/runtime recovery;
- never return deterministic/model-less successful summaries, canned recommendations, or fake success.

## Durable projection and statuses

Suggested projection record: `AuditTraceSummaryTaskProjection`.

Fields:

- `summaryTaskId`, optional `autonomousAgentTaskId`, status, progress summary, blocker/failure reason;
- tenant/customer/AuthContext and actor summary;
- `windowStart`, `windowEnd`, evidence categories, idempotency key, correlation id;
- source refs, trace refs, prompt/skill/reference/model/tool refs;
- result summary/findings after valid typed Akka result only;
- terminal human disposition: accepted/rejected/cancelled, reviewer id, reason, timestamp.

Statuses:

- `QUEUED`, `RUNNING`, `BLOCKED_PROVIDER_OR_RUNTIME`, `FAILED`, `COMPLETED_REVIEW_REQUIRED`, `CANCELLED`, `ACCEPTED`, `REJECTED_RESULT`.

## v3 events

Publish browser-safe v3 `WorkstreamEventEnvelope` records with `eventFamily=task/worker` for shared events and `eventFamily=workflow/process` for Audit/Trace-specific lifecycle events.

Shared worker events:

- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

Audit/Trace workflow events:

- `workflow.audit_trace.summary_started`
- `workflow.audit_trace.summary_blocked_provider_or_runtime`
- `workflow.audit_trace.summary_failed`
- `workflow.audit_trace.summary_completed_review_required`
- `workflow.audit_trace.summary_cancelled`
- `workflow.audit_trace.summary_result_accepted`
- `workflow.audit_trace.summary_result_rejected`

Required source refs:

- `autonomous_task` with Akka task id when present;
- `projection` or `workflow` with `summaryTaskId`;
- producing `capability` and governed-tool ids;
- `audit_trace` / `work_trace` evidence refs;
- prompt assembly, skill/reference load, model, tool-boundary, and evidence-read trace refs when available;
- idempotency key and correlation id.

Events must be idempotent and must not grant authority or perform protected mutations.

## Attention rules

Stable attention id:

```text
attention:worker-task:<summaryTaskId>:task-state
```

Rules:

- queued/running: update progress projections; no human attention unless stale/overdue;
- blocked provider/runtime: upsert Audit/Trace attention for runtime/provider configuration recovery;
- failed: upsert attention for review/retry/escalation;
- completed review required: upsert attention for authorized auditors/admins to inspect result;
- rejected result: keep/upsert attention with rejection reason;
- cancelled and accepted: resolve task-state attention.

Attention is derived from backend events/projections only. Frontend-only rail badges, fixture state, or demo state are not authoritative.

## Structured surfaces

### `surface-audit-trace-summary-progress`

Progress/blocker surface fields:

- `surfaceContract=audit.trace.summaryProgress.v1`;
- `summaryTaskId`, optional `autonomousAgentTaskId`, status, progress summary, stale/reconnect metadata;
- selected tenant/customer/AuthContext and initiating capability id;
- window and evidence categories;
- blocker/failure reason with actionable provider/runtime recovery text;
- source refs, trace refs, and redaction summary;
- actions: refresh/read, cancel, open attention, open trace/timeline.

### `surface-audit-trace-summary-review`

Review/result surface fields:

- `surfaceContract=audit.trace.summaryReview.v1`;
- all progress identity fields;
- `noDirectMutation=true`;
- overall risk, typed findings, evidence citations, provider readiness findings, denial/tool/model/agent-work findings, omitted evidence/redaction summary;
- `humanDecisionRequired=true` for completed summaries;
- actions mapped to governed capabilities: read/refresh, accept, reject, cancel when non-terminal, open evidence, open trace/timeline, open attention item.

Frontend behavior:

- reload state from backend projections;
- render blocked, failed, review-required, cancelled, accepted, rejected, forbidden, stale, and reconnect states;
- never invent successful summary state from fixtures or client-only data.

## Tests and validation requirements

Implementation tasks must add tests for:

- start/read/cancel/accept/reject authorization, disabled user denial, tenant/customer isolation, support-access limits, and forbidden cross-scope reads;
- start idempotency and duplicate idempotency-key reuse;
- provider/model/profile/runtime missing config maps to blocked provider fail-closed state with v3 events and attention;
- `ToolPermissionBoundary` denial for `auditTraceSummaryEvidence.read`, `readSkill`, or `readReferenceDoc`;
- evidence redaction and `not_found_or_redacted` non-enumeration;
- typed task result validation and invalid-result rejection;
- Akka task snapshots/results as source of truth for completed findings;
- v3 `workflow.audit_trace.*` and `worker.task.*` source refs/idempotency;
- attention upsert/resolve lifecycle;
- structured surface contract and frontend safe rendering states;
- no side effects from advisory accept/reject beyond disposition and attention resolution;
- no fake success: tests may use `TestModelProvider.AutonomousAgentTools` only as test infrastructure, never as the normal runtime substitute.

## Explicit non-goals

- No general digest platform.
- No enterprise SIEM/export integration.
- No autonomous mutation of audit records, policies, users, roles, memberships, provider configuration, or managed-agent behavior.
- No provider-ready/restored events without a real backend readiness source.
- No deterministic/demo/canned/model-less successful summaries in normal runtime.
