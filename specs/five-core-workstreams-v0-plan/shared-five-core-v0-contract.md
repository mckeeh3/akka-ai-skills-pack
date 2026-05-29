# Shared Five-Core v0 Contract

## Purpose

This contract is inherited by the five core workstream v0 mini-projects:

- My Account
- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy

It defines the minimum shared acceptance rules for extending the starter template as a secure AI-first SaaS reference runtime. Workstream-specific queues may add stricter rules, but must not weaken this contract.

## Scope boundary

Five-core v0 is the next vertical planning layer after the existing starter baseline. It is narrower than full-core SaaS readiness, but it must still preserve production-like runtime validation for the named scope. A workstream is not complete because it has static copy, fixture data, mocked UI state, or deterministic fallback responses.

Each workstream must be implemented as a role-authorized functional workstream with:

- selected `AuthContext` and backend role/capability checks;
- durable request/response timeline entries and trace references;
- governed backend capabilities before browser actions, API routes, workflow steps, timer actions, consumer reactions, or agent tools are exposed;
- UI surfaces that render capability results, denials, blocked states, and trace links safely;
- tests plus local runtime/API/UI validation appropriate to the implemented scope.

## Agent-type selection rules

### Request/response workstream turns

Use request-based Akka `Agent` components for normal user-facing composer turns. A model-backed `markdown_response` or richer structured response is complete only when the normal message path:

1. resolves the selected workstream and `AuthContext`;
2. resolves the active governed managed-agent `AgentDefinition` for that workstream;
3. assembles approved prompt, compact skill manifest, compact reference manifest, and model configuration;
4. enforces `ToolPermissionBoundary` before registering runtime tools;
5. exposes governed `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader tools only when assigned and authorized;
6. invokes the concrete Akka Agent component through `effects().tools(runtimeTools)`;
7. calls the configured provider boundary for model-backed behavior;
8. records prompt assembly, skill/reference load, model invocation, tool invocation, denial, and `AgentWorkTrace` records;
9. returns a safe structured surface or safe blocked/error surface.

Direct service/provider calls that bypass the Akka Agent component do not satisfy the request/response workstream runtime.

### Akka `AutonomousAgent` work

Use Akka `AutonomousAgent` only for durable task-oriented internal/background work where the lifecycle justifies it, such as investigations, access review batches, governance replay/evaluation loops, audit summary jobs, monitoring/remediation, specialist research, task dependencies, delegation/handoff, progress snapshots, cancellation/failure, or notification streams.

AutonomousAgent-backed work must be exposed through governed capabilities for task start, read/progress, result, cancel/suspend/resume, notification, and decision actions. User-visible progress or results must appear as typed workstream surfaces or system-message surfaces and must be authorized, tenant/customer scoped, and traced.

### Deterministic non-AI services

Use deterministic non-AI services/components for mechanical behavior, including authorization, policy evaluation, validation, trace normalization, projections, outbox delivery, lifecycle enforcement, sanitization, and idempotency. These services are not AI agents, must not be mislabeled as model-driven behavior, and must not bypass backend authorization or audit requirements.

## Capability contract required for every protected behavior

Before implementation, each protected operation/query must have a capability contract with:

- stable capability id and purpose;
- human, agent, workflow, service, timer, consumer, or support callers;
- required `AuthContext`, tenant/customer scope, membership status, role/scope/capability, and selected context;
- typed input/output schemas, validation, safe denial shape, redaction, and correlation/idempotency fields;
- data accessed and tenant/customer filters;
- side effects, external calls, events, emails, notifications, timers, workflow starts, or task starts;
- idempotency/no-op/retry behavior;
- policy, approval, exception, escalation, and human-authority rules;
- audit/work trace fields and retention/redaction expectations;
- selected exposure channels: workstream action, structured surface action, browser API, agent tool, MCP, workflow, timer, consumer, view/query, or internal-only;
- tests for success, validation, forbidden access, tenant isolation, disabled/missing authority, idempotency, audit/trace, approval/denial, and exposure behavior.

Agent tools are exposure channels for selected capabilities, not the backend design root. Frontend affordances, prompt text, loaded skill text, route names, and hidden fields are never authorization controls.

## Security and authorization gates

Each workstream must preserve the starter security model:

- no public self-registration or silent privileged account creation;
- WorkOS/AuthKit and bootstrap/local authorization semantics are kept distinct;
- local Akka-owned Account, profile/settings, Membership, Role/Permission/Capability, Tenant/Customer, and selected `AuthContext` remain authoritative;
- `/api/me` remains browser-safe and must not expose backend secrets;
- every protected route, component command, view query, stream, workflow action, timer action, consumer side effect, and agent tool checks backend authorization;
- cross-tenant/customer access is rejected and traced;
- disabled users, missing memberships, missing scopes, and missing selected contexts fail closed;
- support/SaaS-owner authority is separate from tenant/customer user authority where introduced.

## Trace and audit gates

The v0 reference runtime must make traceability visible, not retrofitted later. Workstream slices must create or preserve durable records for:

- identity and selected context;
- role/capability decisions and denials;
- workstream request/response entries;
- prompt assembly, model invocation, skill/reference loads, tool calls, and model/provider failures;
- capability calls, data access, side effects, approval decisions, and policy evaluations;
- AutonomousAgent task lifecycle events when autonomous work is introduced;
- UI trace links or correlation ids where a user needs to inspect what happened.

Audit/Trace workstream implementation may provide richer search and investigation surfaces, but the other four workstreams still must emit usable trace references for their own behavior.

## UI and API gates

A workstream is v0-ready only when its intended local UI/API path works at the stated scope:

- authorized workstream launcher visibility is driven by backend capabilities from `/api/me` or scoped workstream APIs;
- My Account remains launched from the signed-in user tile/email, not duplicated as a top-rail workstream;
- loading, empty, success, forbidden, blocked-provider, validation-error, and safe-denial states are rendered intentionally;
- `markdown_response` content is sanitized before HTML rendering;
- richer surfaces use typed payloads and map every action to a governed capability;
- trace links/correlation ids are visible where the workstream claims auditability;
- frontend tests cover rendering, denial/error states, secret-boundary expectations, and workstream-specific interactions.

## Model-provider and fail-closed rules

Model-backed workstream behavior must use the configured backend provider boundary. Missing, empty, or blank provider configuration must fail closed with an actionable blocked/error surface and trace. It must not silently fall back to deterministic canned responses or model-less normal runtime behavior.

Provider secrets remain backend-only. Frontend source, `frontend/.env*`, built static resources, logs, responses, and trace payloads must not expose backend secret values.

## Runtime validation standard

Required validation for workstream implementation tasks must include the smallest command set that proves the stated scope. At minimum, completed runtime work must run `git diff --check` plus relevant backend tests, frontend tests/typecheck/build, starter validation, or targeted local smoke checks named by that task.

If real provider smoke is in scope and provider credentials are absent, the task must either:

- verify skip behavior loudly while preserving fail-closed runtime semantics; or
- mark provider-backed validation blocked/deferred with a bounded follow-up task.

Mocks, fixtures, deterministic fakes, and test doubles belong in tests or explicitly named fixture/dev adapters only. They must not be used as normal user-facing runtime substitutes to claim a workstream feature is done.

## Workstream-specific inherited acceptance

Each sibling mini-project should reference this contract and then define its own first implementation slice:

- My Account: current account/context/profile/settings self-service, aggregate next steps, and cross-workstream attention links.
- User Admin: bootstrap user/member/role/capability administration and invitation/readiness progression.
- Agent Admin: governed managed-agent definitions, prompts, skills, references, manifests, model refs, tool boundaries, seeds, and behavior-change controls.
- Audit/Trace: trace substrate visibility, search/investigation path, denial/model/tool/capability trace surfaces, and correlation navigation.
- Governance/Policy: policy/permission concepts, approval/governance boundaries, simulations/proposals where introduced, and behavior-change activation controls.

The shared rule is foundation-first: every workstream may be narrow, but it must remain secure, capability-first, traceable, provider-safe, and locally validated through the intended runtime path.
