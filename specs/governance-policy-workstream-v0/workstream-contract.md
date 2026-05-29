# Governance/Policy Workstream v0 Contract

## Purpose

Define the v0 implementation contract for the `Governance/Policy Agent` workstream in the secure AI-first SaaS starter/reference runtime.

The workstream lets authorized tenant/customer administrators inspect governance posture, review policy-impacting proposals, simulate narrow policy changes, approve or reject activation, and inspect trace evidence for governance decisions. It inherits `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md` and must not weaken the starter security, runtime, provider, trace, or UI validation rules.

## Functional agent responsibility

- Functional agent: `Governance/Policy Agent`.
- Workstream id: `governance-policy`.
- Primary user value: explain current governance and policy posture, surface pending authority-changing decisions, and guide authorized humans through proposal/simulation/approval flows.
- User-facing turn type: request/response Akka `Agent` by default.
- Durable task type: Akka `AutonomousAgent` only for explicitly requested or scheduled policy-impact analysis, replay/evaluation, or remediation-proposal work that needs task identity, progress, cancellation, result surfaces, and notifications.
- Deterministic service role: authorization, policy evaluation, proposal lifecycle enforcement, simulation normalization, redaction, trace correlation, activation/rollback mechanics, idempotency, and audit emission.

## Scope for v0

In scope:

- Governance dashboard/summary for the selected `AuthContext`.
- Policy and permission concept inventory derived from backend state and Agent Admin/User Admin artifacts.
- Read-only evidence surfaces for policies, permissions, behavior artifacts, tool boundaries, pending proposals, and related traces.
- Proposal drafting for policy or behavior-boundary changes without automatic activation.
- Deterministic simulation of proposed changes against scoped evidence where the backend has enough data.
- Human approval/rejection of authority-changing proposals by authorized actors only.
- Activation and rollback of approved changes through backend capabilities with audit/work traces.
- Request/response explanation and drafting through the governed managed-agent runtime.
- Optional AutonomousAgent-backed policy-impact analysis only when implemented as a durable internal/background task.

Out of scope for v0:

- Domain-specific policy rules unrelated to the starter foundation.
- Cross-workstream reimplementation of User Admin, Agent Admin, or Audit/Trace.
- Fully autonomous policy activation without a documented narrow authority boundary and human-governed approval.
- Policy text, prompt text, UI affordances, or hidden frontend fields as authorization controls.
- Model-less deterministic fallback as the normal runtime for model-backed explanations or drafts.

## AuthContext and authority model

Every protected capability requires:

- authenticated account;
- selected tenant/customer `AuthContext`;
- active membership in that context;
- non-disabled user/account status;
- backend role/scope/capability check for the specific action;
- tenant/customer filters on every read and write;
- safe denial shape and denial trace for missing, disabled, or cross-context authority.

Suggested v0 capability names for authorization checks:

- `governance.policy.read`
- `governance.policy.simulate`
- `governance.policy.propose`
- `governance.policy.approve`
- `governance.policy.activate`
- `governance.policy.rollback`
- `governance.policy.analysis.start`
- `governance.policy.analysis.read`

Policy text and managed-agent instructions may explain required authority but never grant it. Frontend launcher visibility may use browser-safe capabilities from `/api/me`, but backend checks remain authoritative.

## Structured surfaces and user actions

### Surfaces

- `GovernancePolicyDashboardSurface`: overall posture, pending proposals, blocked configuration, recent decisions, attention items, and trace links.
- `PolicyInventorySurface`: active policies, permission/capability concepts, approval gates, activation status, owning artifact, and last-change trace.
- `PolicyProposalSurface`: proposed change, source, diff/summary, risk/impact classification, required approval, current lifecycle state, simulation summary, and actions.
- `PolicySimulationSurface`: deterministic simulation inputs, affected capabilities/artifacts, expected denials/allows, warnings, confidence, and evidence trace links.
- `GovernanceDecisionSurface`: approval/rejection/activation/rollback decision, actor, authority basis, rationale, result, and audit correlation id.
- `PolicyAnalysisTaskSurface` when AutonomousAgent work is implemented: task status, progress snapshots, cancellation state, result summary, notifications, and trace links.
- Safe system-message surfaces for forbidden, validation-error, provider-blocked, simulation-unavailable, task-blocked, and stale-proposal states.

### Actions mapped to capabilities

- Open dashboard -> `GOVPOL-READ-DASHBOARD`.
- Inspect policy inventory -> `GOVPOL-LIST-POLICIES` and `GOVPOL-READ-POLICY`.
- Ask for explanation/draft -> `GOVPOL-EXPLAIN-OR-DRAFT` through request/response Akka `Agent`.
- Draft proposal -> `GOVPOL-DRAFT-PROPOSAL`.
- Submit proposal for review -> `GOVPOL-SUBMIT-PROPOSAL`.
- Simulate proposal -> `GOVPOL-SIMULATE-PROPOSAL`.
- Approve or reject proposal -> `GOVPOL-DECIDE-PROPOSAL`.
- Activate approved proposal -> `GOVPOL-ACTIVATE-POLICY-CHANGE`.
- Roll back activated change -> `GOVPOL-ROLLBACK-POLICY-CHANGE`.
- Start/read/cancel policy-impact analysis task, if implemented -> `GOVPOL-START-IMPACT-ANALYSIS`, `GOVPOL-READ-IMPACT-ANALYSIS`, and `GOVPOL-CANCEL-IMPACT-ANALYSIS`.

## Capability classes and Akka substrate choices

- Dashboard and inventory reads: scoped View/query plus HTTP endpoint exposure; request-based Agent may call read-only evidence tools when permitted.
- Proposal lifecycle: Event Sourced Entity for audit-grade proposal state and decisions, with deterministic service methods for validation and lifecycle rules.
- Approval/activation/rollback: Workflow where activation spans governed artifacts or needs approval pause/resume, retries, or rollback coordination; otherwise entity command plus deterministic service is acceptable if atomic.
- Request/response explanation/drafting: Akka `Agent` invoked through governed managed-agent runtime, `ToolPermissionBoundary`, configured provider boundary, `readSkill`, `readReferenceDoc`, and trace emission.
- Policy-impact analysis/replay: Akka `AutonomousAgent` only when a durable background task is actually introduced; expose task operations as capabilities.
- Audit and trace search links: consume existing Audit/Trace surfaces and records through scoped read capabilities; this workstream emits its own decision and policy traces.

## Governed runtime and tool boundaries

Model-backed workstream turns are complete only when normal message submission:

1. resolves workstream id and selected `AuthContext`;
2. resolves active governed managed-agent `AgentDefinition` for `Governance/Policy Agent`;
3. assembles approved prompt, compact skill/reference manifests, and model configuration;
4. enforces `ToolPermissionBoundary` before registering runtime tools;
5. registers only authorized read-only evidence tools by default, plus side-effecting proposal tools only when the capability contract permits them;
6. exposes `readSkill(skillId)` and `readReferenceDoc(referenceId)` only for assigned and authorized documents;
7. invokes the concrete request-based Akka `Agent` with `effects().tools(runtimeTools)`;
8. records prompt assembly, skill/reference loads, tool calls/denials, model/provider failures, and `AgentWorkTrace` references;
9. returns sanitized markdown or typed surfaces with safe blocked/denial shapes.

Missing model/provider/security configuration must fail closed with an actionable blocked surface and trace. Direct provider/service calls that bypass the Akka Agent component do not satisfy this contract.

## Approval and human authority

- Read-only evidence may be available to authorized humans and to the request/response Agent through audited tools.
- Proposal drafting does not commit side effects.
- Simulations are advisory and deterministic; simulation output does not grant authority.
- Authority-expanding, security-sensitive, behavior-changing, tool-boundary-changing, approval-threshold-changing, or rollback actions require authorized human approval.
- AutonomousAgent tasks may recommend or prepare evidence, but they must not activate policy changes unless a later contract defines a narrow accepted autonomous boundary.
- Approval, rejection, activation, and rollback must be idempotent and audited.

## Trace and audit requirements

Emit or preserve durable records for:

- dashboard/inventory protected reads where required by policy;
- proposal creation, submission, simulation, approval, rejection, activation, rollback, and no-op duplicate attempts;
- backend authorization denials and cross-context attempts;
- prompt assembly, model invocation, provider failures, skill/reference loads, and tool calls/denials;
- policy evaluation inputs/outputs, affected capability ids, evidence references, and redaction decisions;
- AutonomousAgent task lifecycle events when those tasks are implemented;
- frontend-visible correlation ids or trace links for consequential decisions and blocked states.

Trace payloads must be scoped/redacted for browser safety and must not expose backend secrets.

## Validation path

Implementation tasks must validate the real local path at the stated scope:

- backend tests for success, validation failure, forbidden access, tenant isolation, disabled/missing authority, idempotency/no-op, audit/work trace, approval/denial, and provider fail-closed behavior where model-backed;
- frontend tests/typecheck for dashboard, inventory, proposal, simulation, decision, trace-link, denial, blocked-provider, and safe markdown rendering states;
- targeted runtime or fullstack starter validation when runtime behavior changes;
- no deterministic/demo/mock/model-less normal runtime substitute may be used to mark model-backed behavior done.

## Downstream implementation notes

- Runtime task should implement the smallest vertical that satisfies this contract rather than full governance maturity.
- Frontend task should render only actions returned as available by backend capability state, while still expecting backend denials.
- Verification task must compare completed work against this contract, `capability-inventory.md`, the shared five-core contract, and runtime validation evidence; if gaps remain, append bounded follow-up tasks before a new terminal verification task.
