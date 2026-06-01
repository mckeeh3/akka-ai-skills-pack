# Governance/Policy Impact AutonomousAgent Contract

## Status and scope

This contract defines the first Governance/Policy policy-change impact worker vertical for the AI-first SaaS starter/reference assets. It is an internal/background Akka `AutonomousAgent` worker, not a user-facing request/response workstream agent and not a policy activation engine.

The worker analyzes a submitted or draft governance/policy proposal, collects scoped browser-safe evidence, produces typed policy impact findings, emits v3 workstream events, creates attention for review/blocker states, and renders Governance/Policy decision surfaces. It never activates, approves, rejects, rolls back, or mutates protected policy state directly.

Out of scope for this slice:

- full policy simulation platform;
- automatic policy activation or rollback;
- model-owned authority expansion;
- deterministic/model-less normal-runtime impact success;
- frontend-only task state, progress, or completion.

## Owning workstream and authority boundary

| Area | Contract |
|---|---|
| Owning functional workstream | `agent-governance-policy` / Governance/Policy workstream. |
| Role surface | Governance/Policy dashboard and review surfaces. |
| Worker placement | Internal workstream agent graph node for `governance_policy.impact_analysis`, launched from submitted/draft proposal surfaces or dashboard attention. |
| Retained human authority | Humans with `governance.policy.approve`, `governance.policy.reject`, `governance.policy.activate`, and `governance.policy.rollback` retain decision and activation authority. |
| Advisory boundary | Worker result acceptance records advisory review disposition only. Activation remains a separate backend capability requiring approved proposal, idempotency key, current proposal state, rollback metadata, AuthContext, audit/work traces, and backend authorization. |
| Primary surfaces | `surface-governance-policy-impact-analysis-task`, `surface-governance-policy-impact-analysis-result`, existing `surface-governance-policy-proposal`, `surface-governance-policy-simulation`, and `surface-governance-policy-decision`. |

## Governed capability family

Prefix: `governance.policy.impact_analysis`.

| Capability / governed-tool id | Actor/caller | Scope and auth | Runtime contract |
|---|---|---|---|
| `governance.policy.impact_analysis.start` | Governance/Policy user or authorized internal workflow | Selected `AuthContext`; tenant required; customer copied when selected; require proposal read plus impact-analysis start authority; idempotency key required | Create or reuse durable impact task projection, resolve governed runtime context, verify provider/model/profile/tool/evidence grants, start `ComponentClient.forAutonomousAgent(GovernancePolicyImpactAutonomousAgent.class, agentInstanceId).runSingleTask(task)`, store Akka autonomous task id, emit queued/running events. |
| `governance.policy.impact_analysis.read` | Governance/Policy user, dashboard, attention, frontend reload | Same tenant/customer scope; require read authority | Read backend projection and, when an Akka task id exists, project `ComponentClient.forTask(taskId).get(...)` snapshot/result into browser-safe task state. Frontend state is never authoritative. |
| `governance.policy.impact_analysis.cancel` | Authorized Governance/Policy reviewer/admin | Same tenant/customer scope; require cancel authority | Record cancelled projection, attempt task termination/failure where SDK support exists, emit cancelled event, resolve task-state attention. Does not change proposal status. |
| `governance.policy.impact_analysis.accept_result` | Human reviewer with governance review authority | Same tenant/customer scope; require explicit human disposition authority | Record that the advisory impact result was accepted as review evidence, emit accepted event, resolve task-state attention. Does not approve, reject, activate, roll back, or mutate policy state. |
| `governance.policy.impact_analysis.reject_result` | Human reviewer with governance review authority | Same tenant/customer scope; require explicit human disposition authority | Record rejection/request-changes reason, emit rejected-result event, keep/upsert attention for follow-up. Does not reject the policy proposal unless a separate proposal-decision capability is invoked. |
| `governance.policy.impact_analysis.request_changes` | Human reviewer | Same tenant/customer scope; require review authority | Record requested changes to the analysis or proposal evidence, emit task-specific request-changes event mapped to `worker.task.rejected_result` attention semantics. Does not mutate policy activation state. |

Every operation must append audit/work trace refs and preserve capability provenance, correlation id, idempotency key, source refs, and redaction hints.

## Task contract

### Akka component shape

- Component extends `akka.javasdk.agent.autonomous.AutonomousAgent`.
- `definition()` returns the Akka autonomous `AgentDefinition` from `define()`; this is distinct from the governed managed-agent domain `AgentDefinition` used for runtime policy/profile resolution.
- Accept one bounded task family with `TaskAcceptance.of(GovernancePolicyImpactTask).maxIterationsPerTask(3)` unless implementation records a justified lower limit.
- Start via adapter/service boundary only, not from endpoints or frontend code directly.

### Task input

`GovernancePolicyImpactTask` fields:

- `impactTaskId` — durable starter/reference task id.
- `proposalId` and optional `targetPolicyId`.
- `tenantId`, nullable `customerId`, selected context id, and browser-safe actor summary.
- `producingCapabilityId = governance.policy.impact_analysis.start`.
- `idempotencyKey` and `correlationId`.
- `proposalSnapshotRef` — source ref to `GovernancePolicyProposal` or proposal surface.
- `evidenceRequest` — bounded focus such as affected capability ids, artifacts, approval rule, threshold, role/scope boundary, provider/model policy, or ToolPermissionBoundary effect.
- `authorizedEvidenceToolIds` — read-only tools granted after `ToolPermissionBoundary` enforcement, including `governancePolicyEvidence.read` and any approved `readSkill`/`readReferenceDoc` grants.
- `traceRefs` — prompt assembly, skill/reference load, evidence read, audit/work trace, model/provider readiness, and source-event refs.
- `forbiddenEffects` — approval, rejection, activation, rollback, role/membership mutation, user-admin mutation, agent-behavior mutation, tool-boundary expansion, provider config mutation, raw-prompt/secret disclosure, cross-tenant reads.

### Instructions

Instructions must state:

- this is advisory policy impact analysis only;
- use selected tenant/customer/AuthContext scope and cited evidence refs only;
- call only authorized read-only evidence loaders;
- identify affected permissions/capabilities, approval gates, ToolPermissionBoundary changes, provider/model fail-closed implications, tenant/customer isolation risks, audit/trace obligations, and required human decisions;
- redact raw prompt bodies, hidden prompt text, provider credentials, API keys, JWTs, raw tool payloads, support-only data, and cross-tenant/customer data;
- if evidence, provider/model config, governed profile, runtime binding, or tool grant is missing, return/record blocked provider/runtime state rather than findings;
- never claim successful impact analysis from deterministic, canned, fake, simulated, fixture, or model-less normal runtime behavior.

### Typed result

`GovernancePolicyImpactResult` fields:

- `resultId`, `impactTaskId`, `proposalId`, `tenantId`, nullable `customerId`.
- `overallRisk`: `low | medium | high | critical | blocked`.
- `reviewState`: `impact_ready | blocked_provider_or_runtime | failed | needs_human_review`.
- `summary` — browser-safe short summary.
- `impactFindings[]` with `findingId`, `category`, `severity`, `affectedCapabilityIds`, `affectedArtifactRefs`, `evidenceRefs`, `redactionLevel`, and `recommendedHumanAction`.
- `approvalGateFindings[]` describing required approval, activation prerequisites, rollback metadata, and denied model-owned authority.
- `tenantIsolationFindings[]` and `redactionFindings[]`.
- `providerRuntimeFindings[]` for provider/model/governed-runtime readiness or fail-closed status.
- `requiredHumanDecisions[]` linking to proposal decision/activation capabilities but not executing them.
- `traceRefs[]`, `sourceRefs[]`, and `redactionHints`.
- `noDirectMutation = true` and `activationBlockedUntilHumanDecision = true`.

### Result rule

A `TaskRule<GovernancePolicyImpactResult>` must reject results that:

- omit proposal/task/tenant ids;
- include raw secrets, raw hidden prompts, raw JWTs, raw provider credentials, or raw tool payloads;
- assert activation, approval, rejection, rollback, role/permission mutation, ToolPermissionBoundary expansion, or provider config mutation;
- produce findings without evidence refs and trace refs;
- claim successful analysis when provider/model/governed runtime/evidence access is blocked;
- lack `noDirectMutation=true` or `activationBlockedUntilHumanDecision=true`.

## Evidence and redaction model

Evidence collection is read-only and scoped by selected backend `AuthContext`.

| Evidence source | Allowed content | Redaction / denial |
|---|---|---|
| `GovernancePolicyProposal` | proposal id, status, target policy, title, rationale, risk classification, affected capability ids, affected artifact refs, required approval id, rollback requirement, lifecycle trace refs | redact secret-like values in proposed content; omit cross-tenant/customer proposals; no activation data beyond browser-safe status/rollback reference labels. |
| Governance policy inventory/detail | active policy summaries, browser-safe affected capabilities/artifacts, current lifecycle/status, trace ids | no raw prompt bodies, hidden prompt text, provider credentials, API keys, JWTs, raw tool payloads, support-only data. |
| Governance simulation surface | deterministic advisory simulation evidence and expected allows/denials | mark as simulation-only; cannot be normal model-backed impact success and cannot grant authority. |
| Managed-agent/runtime traces | prompt/skill/reference/model/tool trace ids and allow/deny summaries | trace ids and safe labels only; no hidden prompt or raw tool payload disclosure. |
| Capability and ToolPermissionBoundary refs | capability ids, governed-tool ids, allowed/denied summary, affected boundary refs | no unauthorized boundary detail; denied reads produce fail-closed/denied evidence. |

The worker may cite existing deterministic simulation output as one evidence input, but the worker's `impact_ready` result requires a real Akka `AutonomousAgent` task result through the governed runtime path.

## Runtime adapter and provider fail-closed behavior

Implement through `GovernancePolicyImpactAutonomousAgentRuntime` and `ComponentClientGovernancePolicyImpactAutonomousAgentRuntime` (or equivalent names).

Normal adapter requirements:

1. authorize start/read/cancel/accept/reject/request-changes against selected `AuthContext`;
2. require idempotency on start and human dispositions;
3. create/reuse durable task projection before model-backed work;
4. resolve governed managed-agent runtime context, model/provider/profile configuration, prompt/skill/reference traces, `ToolPermissionBoundary`, and read-only evidence tools;
5. start the concrete Akka `AutonomousAgent` through `ComponentClient.forAutonomousAgent(...).runSingleTask(...)`;
6. project snapshots/results through `ComponentClient.forTask(...).get(...)`;
7. emit v3 events and audit/work traces from backend state.

Fail-closed adapter requirements:

- Missing `ComponentClient`, provider/model config, governed profile, tool grants, `readSkill`, `readReferenceDoc`, evidence access, runtime binding, authorization, or scope match maps to `BLOCKED_PROVIDER_OR_RUNTIME` / `blocked_provider_or_runtime`.
- The blocked state includes browser-safe actionable recovery text, correlation id, trace refs, source refs, v3 `worker.task.blocked_provider_or_runtime` plus `workflow.governance_policy.impact_analysis.blocked_provider_or_runtime`, and attention.
- It must not return deterministic successful findings, canned recommendations, model-less impact summaries, provider-ready events, or fake success.

## Durable projection

Persist or project an impact task record with:

- `impactTaskId`, optional `autonomousAgentTaskId`, `proposalId`, tenant/customer ids, actor summary;
- status: `QUEUED`, `RUNNING`, `BLOCKED_PROVIDER_OR_RUNTIME`, `FAILED`, `COMPLETED_REVIEW_REQUIRED`, `CANCELLED`, `ACCEPTED`, `REJECTED_RESULT`, `REQUEST_CHANGES`;
- progress summary, blocker/failure reason, stale/reconnect metadata;
- input refs, evidence refs, trace refs, source event ids, idempotency key, correlation id;
- typed result only after valid Akka task result;
- terminal human disposition, reviewer, rationale, disposition trace refs;
- `noDirectMutation=true` and `activationBlockedUntilHumanDecision=true`.

Idempotency keys:

```text
governance-policy-impact:<tenantId>:<customerId-or-none>:<proposalId>:<idempotencyKey>
```

## v3 events

Emit shared worker-task events:

- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

Emit Governance/Policy workflow events for task-specific dashboard grouping:

- `workflow.governance_policy.impact_analysis.started`
- `workflow.governance_policy.impact_analysis.blocked_provider_or_runtime`
- `workflow.governance_policy.impact_analysis.completed_review_required`
- `workflow.governance_policy.impact_analysis.cancelled`
- `workflow.governance_policy.impact_analysis.result_accepted`
- `workflow.governance_policy.impact_analysis.result_rejected`
- `workflow.governance_policy.impact_analysis.request_changes`

Each `WorkstreamEventEnvelope` must include tenant/customer/AuthContext, actor, event family, event type, source refs, capability refs, correlation id, idempotency key, trace refs, owning workstream id, target surface id, browser-safe payload, and redaction hints. Source refs include proposal id, impact task id, optional `autonomous_task` Akka task id, capability id, evidence refs, and trace ids.

Event publication never grants permission, mutates policy state, or substitutes for source-state validation.

## Attention rules

Stable attention id:

```text
attention:worker-task:<impactTaskId>:task-state
```

Mapping:

| State/event | Attention behavior |
|---|---|
| queued/running | update progress surface; no human attention unless stale/overdue. |
| blocked provider/runtime | upsert configuration/runtime recovery attention for Governance/Policy owner/admin. |
| failed | upsert review/retry/escalation attention. |
| completed review required / impact ready | upsert human review attention linked to result surface and proposal surface. |
| rejected result / request changes | keep or upsert follow-up attention with reviewer reason. |
| cancelled / accepted | resolve task-state attention. |

Attention derives only from backend events/projections. Frontend rail badges, fixtures, or demo state are not authoritative.

## Structured surfaces

### Progress/task surface

`surface-governance-policy-impact-analysis-task` includes:

- surface id/type/version and owning workstream;
- `impactTaskId`, optional `autonomousAgentTaskId`, proposal id;
- status/progress/blocker/failure/stale metadata;
- initiating capability id and authorized next actions;
- selected tenant/customer/AuthContext summary;
- source refs, evidence refs, trace ids, event ids;
- `noDirectMutation=true`, `activationBlockedUntilHumanDecision=true`;
- actions mapped to governed capabilities: read/refresh, cancel, open proposal, open evidence, open trace, open attention.

### Result/decision surface

`surface-governance-policy-impact-analysis-result` includes:

- typed impact findings and approval-gate findings only after valid Akka task result;
- overall risk, required human decisions, recommended next steps, confidence/evidence limitations;
- redaction hints and omitted field keys;
- proposal decision links as separate governed actions, not as worker-side mutation;
- accept/reject/request-changes actions that record advisory result disposition only.

Frontend behavior:

- reload state from backend projections;
- render blocked, failed, review-required, accepted, rejected, request-changes, cancelled, forbidden, stale, and reconnect states;
- never invent successful impact findings, progress, or provider readiness;
- distinguish deterministic `governance.policy.simulate` evidence from real `AutonomousAgent` impact result.

## Required tests and validation scans

Implementation tasks must add tests for:

- start/read/cancel/accept/reject/request-changes authorization and disabled-user denial;
- tenant/customer isolation and redaction of policy impact evidence;
- start idempotency and duplicate idempotency-key reuse;
- governed runtime resolution and `ToolPermissionBoundary` denial;
- provider/model/runtime missing configuration producing blocked provider fail-closed event, attention, and surface;
- typed result rule rejection for missing evidence, hidden secret leakage, or mutation claims;
- v3 `worker.task.*` and `workflow.governance_policy.impact_analysis.*` event payload/source refs;
- attention upsert/resolve mapping;
- surface/API contract and frontend no-fake-success rendering;
- human accept/reject/request-changes have no direct policy activation, approval, rejection, rollback, or mutation side effects.

Focused scans should prove the contract and implementation mention: `AutonomousAgent`, policy impact evidence, redaction, provider fail-closed, v3 events, attention, surfaces, human approval, and no fake success.

## Runtime completion guardrails

The Governance/Policy impact worker is not implemented if normal runtime success depends on:

- deterministic, canned, demo, simulated, fixture, fake, or model-less successful policy impact findings;
- direct provider/service calls bypassing Akka `AutonomousAgent` task lifecycle;
- frontend-only completion/progress/success state;
- test-only `TestModelProvider.AutonomousAgentTools.completeTask` or `failTask` helpers outside tests;
- result acceptance that directly approves, rejects, activates, rolls back, or mutates policy/governance state;
- successful results when provider/model/governed runtime/tool/evidence configuration is missing.

Missing authorization, disabled user, tenant/customer mismatch, provider/model config, governed runtime profile, tool grants, evidence access, or `ComponentClient` binding must fail closed with safe blocked/denied state, audit/work traces, v3 events, attention, and actionable recovery text.
