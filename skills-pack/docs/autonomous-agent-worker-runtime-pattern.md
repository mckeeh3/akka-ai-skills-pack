# AutonomousAgent worker runtime pattern

Use this pattern for generated secure AI-first SaaS workstreams that need durable internal/background model-driven work with a real Akka `AutonomousAgent` task lifecycle. It is extracted from the SaaS Foundation App User Admin access-review worker, Agent Admin prompt-risk review worker, Audit/Trace summary worker, Governance/Policy impact-analysis worker, and My Account personal attention digest worker.

This is a runtime completion pattern, not only a design pattern: a worker is done only when the normal local runtime path starts and observes a concrete Akka `AutonomousAgent` task, projects backend-owned state, emits workstream events/attention, renders governed surfaces, and fails closed when provider or governed runtime configuration is missing.

## When to use

Use Akka `AutonomousAgent` when the work is internal/background, task-oriented, and benefits from typed task ids, snapshots, failure/cancellation, notifications, dependencies, or model-driven iteration. Common generated-app examples include access review investigations, prompt/skill/reference risk review, governance/policy impact analysis, audit summaries, personal attention digests, evaluation/replay loops, monitoring/remediation, and specialist follow-up.

Do not use this pattern for:

- default user-facing functional workstream request/response turns; use request-based Akka `Agent`;
- deterministic business orchestration whose correctness depends on known transitions, retries, compensation, timeouts, or approval pauses; use `Workflow`, optionally launching or waiting on an `AutonomousAgent` task;
- deterministic/demo/model-less background jobs that only need normal service, workflow, consumer, or timer code.

## Required worker contract

Every generated-app worker task must preserve these contracts before coding starts:

| Contract area | Required content |
|---|---|
| Owning workstream | Functional/context-area agent id, role-specific dashboard, attention category, internal workstream agent graph placement, result/escalation surface, and retained human authority. |
| Governed capabilities | Capability family for start/read/cancel/accept/reject or task-specific equivalents, governed-tool ids, actor/caller, `AuthContext`, tenant/customer scope, required roles/capabilities, idempotency key, policy/approval rules, audit/work traces, and exposure channels. |
| Task contract | Typed task input, task name/description, instruction builder inputs, result schema, result rules, max iterations, evidence/tool references, forbidden effects, and browser-safe summary fields. |
| Runtime boundary | ComponentClient start/read calls, governed managed-agent runtime resolution, model/provider/profile checks, `ToolPermissionBoundary` checks, authorized read-only evidence tools, `readSkill`/`readReferenceDoc` grants when needed, trace refs, and fail-closed adapter behavior. |
| Durable projection | SaaS Foundation App/domain task id, optional Akka autonomous task id, status/progress/result/blocker fields, correlation/idempotency keys, source refs, and terminal human disposition. |
| Events/attention/surfaces | typed workstream events, shared `worker.task.*` events, attention upsert/resolve rules, structured progress/result surfaces, capability-backed actions, and frontend reload from backend projections. |
| Tests/checks | Unit/integration tests for lifecycle, authorization, idempotency, fail-closed states, typed result validation, event/attention projection, surface/API contracts, and focused scans for fake-success regressions. |

## Standard capability family

Use a small task-specific capability family. The exact prefix belongs to the workstream/domain, for example `user_admin.access_review.*` or `agent_admin.prompt_risk_review.*`.

| Operation | Runtime expectation |
|---|---|
| `<prefix>.start` | Authorize selected `AuthContext`; require scope and idempotency key; create or reuse the durable projection; resolve governed runtime/tool context; start `ComponentClient.forAutonomousAgent(...).runSingleTask(...)`; store `autonomousAgentTaskId`; emit queued/running events. |
| `<prefix>.read` | Authorize reads; project `ComponentClient.forTask(taskId).get(...)` snapshots into backend-owned browser-safe state; never trust frontend state as authoritative. |
| `<prefix>.cancel` | Authorize cancellation; record deterministic projection; attempt task termination/failure where supported; emit cancelled event; resolve task-state attention. |
| `<prefix>.accept_result` | Record human acceptance/disposition of advisory output only; emit accepted event; resolve attention; do not perform separate protected domain mutation. |
| `<prefix>.reject_result` | Record human rejection/reason; emit rejected-result event; keep or create human attention; do not mutate protected domain state. |

If the domain needs additional actions, model them as separate governed capabilities. Do not hide consequential mutations behind worker result acceptance.

## Component and task shape

The Akka component should:

1. extend `akka.javasdk.agent.autonomous.AutonomousAgent`;
2. implement `definition()` returning the Akka autonomous `AgentDefinition` from `define()`;
3. accept one bounded task family with `TaskAcceptance.of(<typed task>).maxIterationsPerTask(3)` unless a different bounded limit is justified;
4. define a typed `Task<R>` with stable name, description, `resultConformsTo(...)`, task-specific instructions, and `TaskRule<R>` validation;
5. keep instructions scoped and explicit: tenant/customer scope, capability id, correlation id, evidence refs, governed runtime context, advisory-only authority, forbidden effects, and fail-closed behavior;
6. return a typed result record, not generic markdown, for findings/recommendations/reasons/evidence refs.

Terminology guardrail: the Akka autonomous `AgentDefinition` returned by `AutonomousAgent.definition()` is distinct from the skills pack's governed managed-agent `AgentDefinition` domain record. Qualify which one is meant whenever both appear.

## Runtime adapter and fail-closed adapter

Use an adapter/service boundary around `ComponentClient` rather than scattering autonomous task calls through endpoints or UI code.

The normal adapter must:

- call `ComponentClient.forAutonomousAgent(WorkerClass.class, agentInstanceId).runSingleTask(task)` for single bounded work;
- call `ComponentClient.forTask(autonomousAgentTaskId).get(taskDefinition)` to project snapshots/results;
- map Akka task statuses to domain projection statuses without fabricating findings;
- resolve governed runtime context before task start, including model/provider/profile configuration, active managed-agent configuration where applicable, prompt/skill/reference traces, evidence tools, and tool-boundary grants;
- register only authorized read-only tools/evidence loaders for the worker slice.

The fail-closed adapter must produce a blocked provider/runtime state when `ComponentClient`, provider/model config, governed profile, tool grants, evidence access, or runtime binding is unavailable. The blocked state must include actionable browser-safe recovery text, correlation/trace refs, typed workstream events, and attention. It must not return a deterministic successful review or canned recommendations. For governance/policy impact analysis, deterministic simulation output may be cited only as scoped evidence; it is not a substitute for a real model-backed `AutonomousAgent` impact result and must never become fake success. For My Account personal attention digest, backend attention evidence may be collected deterministically and redacted before the task, but a normal successful digest summary must still come from the concrete model-backed `AutonomousAgent` path; collected evidence is not a canned/model-less digest result.

## typed workstream events and shared worker-task events

Emit task lifecycle facts through the workstream event backbone so attention, dashboards, audit/traces, and realtime refresh can derive from backend facts.

Use these shared event names unless a future contract explicitly supersedes them:

- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

Source refs should include the domain/SaaS Foundation App task id, Akka autonomous task id when available, producing capability id, idempotency key, event id, tenant/customer scope, evidence refs, and prompt/skill/reference/model/tool/audit trace refs. Events and projections must be idempotent.

## Attention rules

Use a stable attention id shape such as:

```text
attention:worker-task:<domain-task-id>:task-state
```

Default behavior:

- queued/running: update progress surfaces; usually no human attention unless stale/overdue;
- blocked provider/runtime: upsert attention for configuration/runtime recovery;
- failed: upsert attention for review/retry/escalation;
- completed review required: upsert attention for the responsible role to inspect the result;
- rejected result: keep or upsert attention with rejection reason;
- cancelled and accepted: resolve task-state attention.

Attention state must derive from backend events/projections. Frontend-only rail badges, fixtures, or demo state are not authoritative.

## Structured surfaces

When worker state is visible, expose it as backend-projected structured surfaces, not untracked logs or raw task text.

A worker progress/result surface should include:

- stable surface id/type/version and owning workstream;
- domain task id and optional `autonomousAgentTaskId`;
- status, progress summary, blocker/failure reason, stale/reconnect metadata, and human-needed state;
- initiating capability id and authorized next actions;
- selected tenant/customer/AuthContext scope;
- evidence/source refs and trace ids;
- typed findings/recommendations only after a valid typed Akka task result;
- `noDirectMutation=true` for advisory review workers;
- task-specific flags such as `activationBlockedUntilHumanDecision=true` where protected activation is out of scope, especially Governance/Policy impact workers whose advisory result must not approve, reject, activate, roll back, or mutate policy state, or `noDirectMutation=true` for My Account personal attention digest workers whose advisory result must not acknowledge, dismiss, resolve, expire, or mutate source attention;
- actions mapped to governed capabilities: read/refresh, cancel, accept, reject, open evidence, open trace, open attention item, or task-specific follow-up.

The frontend must reload worker state from backend projections and handle blocked, failed, review-required, cancelled, accepted, forbidden, stale, and reconnect states without inventing success.

## Runtime completion guardrails

A generated-app AutonomousAgent worker is incomplete if normal runtime success depends on any of these substitutes:

- deterministic, canned, demo, simulated, fixture, fake, or model-less successful findings;
- direct provider/service calls bypassing Akka `AutonomousAgent` task lifecycle;
- frontend-only state marking a worker complete;
- test-only `TestModelProvider.AutonomousAgentTools.completeTask` or `failTask` helpers outside tests;
- human accept/reject actions that directly perform separate protected mutations;
- successful results when provider/model/governed runtime/tool/evidence configuration is missing.

Missing authorization, disabled user, tenant/customer mismatch, provider/model config, governed runtime profile, tool grants, evidence access, or `ComponentClient` binding must fail closed with safe blocked/denied state, audit/work traces, typed workstream events when appropriate, and actionable recovery text.

## Required tests and validation scans

At minimum, test:

- start/read/cancel/accept/reject authorization, disabled-user denial, tenant/customer isolation, and support-access limits where relevant;
- start idempotency and duplicate idempotency-key reuse;
- governed runtime resolution and `ToolPermissionBoundary` denial for ungranted tools, `readSkill`, `readReferenceDoc`, or evidence readers;
- provider/model/runtime missing configuration maps to blocked provider/runtime state with event and attention;
- typed task result validation and invalid-result rejection through `TaskRule`;
- Akka task snapshots/results are the source of truth for completed findings;
- typed workstream events and `worker.task.*` event payload/source refs;
- attention upsert/resolve behavior;
- structured surface/API contract, frontend secret boundary, and safe blocked/failed/review-required rendering;
- no side effects from advisory accept/reject beyond the review disposition.

Use `TestModelProvider.AutonomousAgentTools` only as test infrastructure. A passing test helper path does not by itself prove production-like local runtime completion; the normal runtime path must invoke the concrete Akka `AutonomousAgent` and fail closed when real provider/security configuration is absent.

Focused repository checks for worker docs/skills should include searches proving coverage for: task contract, governed capabilities, typed workstream events, attention, surfaces, provider fail-closed, and no fake success.
