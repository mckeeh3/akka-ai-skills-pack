# Audit/Trace Workstream v0 Contract

## Purpose

Define the v0 contract for the `Audit/Trace Agent` workstream in the secure AI-first SaaS starter/reference runtime. This contract inherits and specializes:

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`

Audit/Trace v0 makes traceability visible and actionable: authorized users can search scoped audit/work traces, inspect timelines and evidence, understand denials and provider/tool failures, and ask bounded trace-explanation questions without exposing cross-tenant/customer data or backend secrets.

## Scope

### In scope for v0

- A role-authorized `Audit/Trace Agent` functional workstream.
- Backend-scoped trace/evidence capabilities before browser actions, API routes, agent tools, or future task operations are exposed.
- Tenant/customer-scoped trace search, list, detail, correlation timeline, denial/provider/tool/capability evidence, and safe investigation guidance.
- User-facing request/response Akka `Agent` turns for bounded explanations over authorized trace evidence.
- Deterministic non-AI services for authorization, trace normalization, redaction, validation, filtering, and timeline assembly.
- Optional task contract for later durable audit-summary/anomaly-review batches using Akka `AutonomousAgent`; no v0 implementation should introduce it unless the runtime task lifecycle is actually implemented and tested.
- UI/API behavior for loading, empty, forbidden, redacted, partial, error, blocked-provider, and trace-link states.
- Tests and local validation for authorization, tenant isolation, safe denial, redaction, trace creation, idempotent reads, and provider fail-closed behavior where model-backed explanations are in scope.

### Out of scope for v0

- Full-core audit retention administration, legal hold, bulk export, or external SIEM integration.
- Cross-tenant support/SaaS-owner investigation unless an explicit support-access authority model is implemented.
- Domain-specific audit semantics beyond the core SaaS workstreams.
- Replacing user-facing request/response turns with Akka `AutonomousAgent`.
- Using fixture, mock, deterministic, or model-less responses as the normal user-facing runtime for model-backed explanations.

## Functional agent responsibility

`Audit/Trace Agent` is the role-authorized functional/context-area agent for audit and trace investigation. It helps authorized users answer:

- what happened for a workstream request, capability call, policy/authorization decision, model invocation, tool invocation, or provider failure;
- why an operation was allowed, denied, redacted, blocked, or failed;
- which related correlation ids, trace events, workstream entries, or audit events are relevant;
- what safe next step the user can take, such as opening an allowed related workstream, refining a search, or requesting a governed review.

The agent must not grant authority. It may summarize only evidence returned by governed backend capabilities under the current `AuthContext`.

## AuthContext and authority

All Audit/Trace capabilities require:

- authenticated account;
- selected `AuthContext` with tenant/customer scope;
- active membership in the selected scope;
- required role/scope/capability such as `audit.trace.read`, `audit.trace.investigate`, or narrower workstream-specific evidence permissions;
- backend-enforced tenant/customer filters on every trace, audit event, workstream entry, model/tool trace, and evidence query;
- safe denial and trace emission for missing selected context, disabled account, missing membership, missing capability, wrong tenant/customer, or unsupported support-access context.

Frontend launcher visibility, prompt text, route names, surface action ids, hidden fields, or loaded skill/reference text are never authorization controls.

## Structured surfaces and actions

The v0 workstream should expose typed surfaces rather than raw logs:

| Surface | Purpose | Primary capabilities |
|---|---|---|
| Audit trace dashboard | Recent trace health, important denials/failures, saved/recent filters, empty/readiness states. | `audit.trace.dashboard.read`, `audit.trace.search` |
| Trace search results | Scoped searchable list with correlation id, event kind, actor, workstream, timestamp, severity, redaction summary, and trace links. | `audit.trace.search` |
| Correlation timeline | Ordered timeline for one correlation id/request id/work item with related audit, workstream, model, tool, capability, and denial events. | `audit.trace.timeline.read` |
| Trace detail/evidence card | Redacted event details, authorization basis, data/tool/model references, denial reason, safe payload fragments, and related links. | `audit.trace.detail.read` |
| Denial/provider/tool evidence panel | Focused explanation of denied authorization, denied tool, missing provider, model failure, or tool failure. | `audit.trace.failureEvidence.read` |
| Explanation response | Bounded markdown or typed answer over already-authorized trace evidence. | `audit.trace.explain` |
| Investigation guidance card | Safe next-step suggestions with actions mapped to governed capabilities. | `audit.trace.investigationGuide.read` |
| Future audit summary task status | Optional progress/result surface for durable audit-summary or anomaly-review work. | `audit.trace.summaryTask.*` only if implemented |

Allowed actions must map to governed backend capabilities, for example: refine search, open trace detail, open correlation timeline, ask explanation, copy correlation id, open related authorized workstream, or start/read/cancel a future audit-summary task.

## Capability and exposure rules

- Trace search/detail/timeline are read/evidence capabilities with HTTP/browser exposure and possible read-only agent-tool exposure.
- Explanation is a bounded request/response capability. It uses a request-based Akka `Agent` only after deterministic capabilities have retrieved and redacted evidence.
- Redaction and timeline construction are deterministic services, not AI behavior.
- Future audit-summary/anomaly-review task operations are autonomous task capabilities only when they need durable lifecycle, progress snapshots, cancellation/failure, or notifications.
- Agent tools are exposure channels for selected read/evidence capabilities. They must enforce `ToolPermissionBoundary`, AuthContext, tenant/customer scope, redaction, and trace emission.

## Agent-type selection

| Work item | Selected substrate | Reason |
|---|---|---|
| User-facing Audit/Trace composer turn | Request-based Akka `Agent` | Bounded request/response explanation; caller expects immediate answer or blocked surface. |
| Trace search/list/detail/timeline | Deterministic services + Views/API | Mechanical scoped reads, filtering, sorting, redaction, and projection. |
| Authorization, tenant filters, redaction, validation | Deterministic non-AI services | Product correctness must not depend on model output. |
| Audit-summary/anomaly-review batch | Optional Akka `AutonomousAgent` only if implemented later | Justified only when durable task lifecycle, progress, notifications, cancellation/failure, or model-driven iteration is required. |
| Approval or retention workflows | Workflow when introduced later | Deterministic ordered process with approval/retry/timeout semantics. |

## Trace and audit obligations

The workstream must read and/or emit durable records for:

- search/list/detail/timeline capability calls and denials;
- data access and redaction decisions;
- prompt assembly, skill/reference loads, model invocation, provider blocked/failure states, tool calls, and tool denials for explanations;
- cross-tenant/customer denied attempts;
- future AutonomousAgent task lifecycle events when introduced;
- correlation ids returned to UI surfaces and safe links.

Trace payloads must be redacted and browser-safe. Provider secrets, raw tokens, hidden prompt content not intended for the viewer, and unauthorized tenant/customer evidence must not be returned or logged into user-visible trace surfaces.

## Provider and model behavior

Model-backed explanations must use the configured backend provider boundary through the governed managed-agent runtime and concrete Akka `Agent` invocation. Missing, empty, or blank provider configuration fails closed with an actionable blocked/error surface and trace. The normal runtime must not silently fall back to canned deterministic explanations.

## Validation path

Implementation tasks must prove the intended local runtime path for their scope. Minimum validation expectations:

- backend tests for successful scoped search/detail/timeline, validation errors, forbidden access, tenant isolation, disabled/missing authority, redaction, idempotent read behavior, and trace emission;
- agent/runtime tests for governed request/response explanation path, tool-boundary denials, provider fail-closed behavior, and trace emission where explanation is implemented;
- frontend tests/typecheck for surfaces, loading/empty/forbidden/redacted/blocked/error states, trace links, safe markdown rendering, and no frontend secret exposure;
- `git diff --check` for every task;
- full starter validation in terminal verification when runtime/template behavior changed.
