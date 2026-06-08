# Governance/Policy Workstream v0 Capability Inventory

## Inventory rules

Each capability inherits the shared five-core v0 contract. Every protected path must resolve selected `AuthContext`, enforce backend authorization, scope tenant/customer reads/writes, emit audit/work traces for consequential events and denials, and return safe browser/agent responses.

Agent-type defaults:

- request/response Akka `Agent` for user-facing explanations, drafts, and bounded `markdown_response` turns;
- deterministic non-AI services for policy evaluation, proposal lifecycle, authorization checks, validation, redaction, idempotency, and activation mechanics;
- Akka `AutonomousAgent` only for durable policy-impact analysis tasks if implemented by the runtime task.

## Capability summary

| ID | Name | Class | Primary substrate | Exposure |
|---|---|---|---|---|
| `GOVPOL-READ-DASHBOARD` | Read governance dashboard | Read/evidence | View/query + endpoint | workstream dashboard, browser API, optional read-only agent tool |
| `GOVPOL-LIST-POLICIES` | List policy inventory | Read/evidence | View/query + endpoint | inventory surface, browser API, optional read-only agent tool |
| `GOVPOL-READ-POLICY` | Read policy detail/evidence | Read/evidence | View/query + endpoint | detail surface, browser API, optional read-only agent tool |
| `GOVPOL-EXPLAIN-OR-DRAFT` | Explain governance or draft non-committing text | Request/response AI | Akka `Agent` | composer turn, optional structured/markdown response |
| `GOVPOL-DRAFT-PROPOSAL` | Create draft policy-change proposal | Proposal | Event Sourced Entity + deterministic service | workstream action, browser API, governed agent tool only if permitted |
| `GOVPOL-SUBMIT-PROPOSAL` | Submit proposal for review | Command/proposal lifecycle | Event Sourced Entity | workstream action, browser API |
| `GOVPOL-SIMULATE-PROPOSAL` | Simulate proposal impact | Policy/governance | deterministic service, optional Workflow for multi-step evidence | workstream action, browser API, optional read-only agent tool |
| `GOVPOL-DECIDE-PROPOSAL` | Approve or reject proposal | Approval | Event Sourced Entity or Workflow step | decision surface, browser API |
| `GOVPOL-ACTIVATE-POLICY-CHANGE` | Activate approved change | Command/workflow | Workflow or Event Sourced Entity + deterministic service | decision/action surface, browser API |
| `GOVPOL-ROLLBACK-POLICY-CHANGE` | Roll back activated change | Command/workflow | Workflow or Event Sourced Entity + deterministic service | decision/action surface, browser API |
| `GOVPOL-START-IMPACT-ANALYSIS` | Start durable policy-impact analysis | Autonomous task | Akka `AutonomousAgent` | workstream action, browser API, optional workflow start |
| `GOVPOL-READ-IMPACT-ANALYSIS` | Read analysis task status/result | Autonomous task read | AutonomousAgent task client/view | task surface, dashboard, browser API |
| `GOVPOL-CANCEL-IMPACT-ANALYSIS` | Cancel analysis task | Autonomous task command | AutonomousAgent task client | task surface, browser API |

## Shared schemas

### AuthContext requirement

All capabilities require:

- `accountId`
- `selectedContextType` of tenant or customer
- `selectedContextId`
- active membership id and membership status
- backend-granted capability/scope for the requested action
- correlation id; idempotency key for commands

### Safe denial output

All protected capabilities can return a safe denial with:

- `status`: `forbidden`, `unauthorized`, `missing_context`, `disabled`, `validation_error`, or `blocked`
- `message`: browser-safe/actionable text
- `requiredCapability`: optional capability name
- `correlationId`
- `traceId`: when visible to the caller
- no backend secrets, provider secrets, raw prompt text, or unredacted cross-tenant evidence

### Trace fields

Consequential capabilities record:

- `traceId`, `correlationId`, `tenantId`, optional `customerId`
- actor account/member, selected AuthContext, authority basis
- capability id and exposure channel
- input summary/redacted input, output summary/redacted output
- proposal/policy/artifact ids when applicable
- policy evaluation result, approval decision, side effect, no-op, or denial reason
- prompt/model/tool/skill/reference ids for model-backed turns

## Detailed capabilities

### GOVPOL-READ-DASHBOARD: Read governance dashboard

- Purpose: show the authorized user's governance posture, pending proposals, recent decisions, blocked provider/configuration states, and trace links.
- Actors/callers: authorized governance admins, support/SaaS-owner roles only through separate support authority if introduced, `Governance/Policy Agent` as read-only evidence tool.
- AuthContext: selected tenant/customer context; requires `governance.policy.read`.
- Inputs: optional filters for status, attention type, date range; `correlationId`.
- Outputs: `GovernancePolicyDashboardSurface` data with summary counts, pending items, recent decisions, provider/configuration blocked states, and redacted trace references.
- Data access: scoped policy views, proposal views, Agent Admin artifact summaries, User Admin capability names, Audit/Trace evidence summaries.
- Side effects: none except optional read trace/audit if configured.
- Idempotency: read-only, repeatable.
- Policy/approval: no approval required; does not grant authority.
- Audit/trace: protected read trace when policy requires, denials always traced.
- Exposure channels: dashboard surface, browser API, optional read-only agent tool.
- Tests: authorized read, empty state, forbidden tenant/customer, missing context, disabled user, redaction, trace link presence.

### GOVPOL-LIST-POLICIES: List policy inventory

- Purpose: list active/inactive policy concepts, approval gates, tool-boundary rules, and permission/capability concepts relevant to the selected context.
- Actors/callers: authorized governance admins and read-only agent tool.
- AuthContext: `governance.policy.read` in selected context.
- Inputs: status/type/artifact filters; paging; `correlationId`.
- Outputs: redacted inventory rows with `policyId`, name, type, status, affected capability ids, source artifact, last changed by, and trace id.
- Data access: policy/proposal projections, Agent Admin governed artifact summaries, User Admin capability catalog.
- Side effects: none except optional read trace.
- Idempotency: read-only.
- Policy/approval: no approval required.
- Audit/trace: denied/cross-context reads traced; successful protected reads traceable when configured.
- Exposure channels: inventory surface, browser API, optional read-only agent tool.
- Tests: filtering, redaction, cross-tenant denial, no raw secrets/prompts, stable paging.

### GOVPOL-READ-POLICY: Read policy detail/evidence

- Purpose: inspect one policy, proposal, approval gate, or tool-boundary rule with safe evidence and history links.
- Actors/callers: authorized governance admins and read-only agent tool.
- AuthContext: `governance.policy.read`; policy/artifact must belong to selected tenant/customer scope.
- Inputs: `policyId` or artifact reference; optional version; `correlationId`.
- Outputs: detail surface with current state, summary, safe diff/history links, affected capabilities, approval rules, and trace references.
- Data access: scoped policy records/projections, governed behavior artifacts, trace summaries.
- Side effects: none except optional read trace.
- Idempotency: read-only.
- Policy/approval: no approval required; secret or unapproved prompt internals redacted as required.
- Audit/trace: denied reads traced; detail read trace where configured.
- Exposure channels: detail surface, browser API, optional read-only agent tool.
- Tests: authorized detail, not found vs forbidden shape, redacted secret/prompt evidence, trace links.

### GOVPOL-EXPLAIN-OR-DRAFT: Explain governance or draft non-committing text

- Purpose: let the functional agent explain governance posture, summarize evidence, or draft proposal text without committing changes.
- Actors/callers: authorized signed-in user via composer; internal workstream runtime.
- AuthContext: selected context; requires at least `governance.policy.read`; drafting proposal text may require `governance.policy.propose`.
- Inputs: user message, selected surface context, optional referenced policy/proposal ids, `correlationId`.
- Outputs: sanitized `markdown_response` or typed safe surface references; blocked-provider or denial surface when unavailable.
- Data access: only through authorized runtime tools such as read dashboard/inventory/detail and governed `readSkill`/`readReferenceDoc`.
- Side effects: prompt/model/tool traces only; no policy activation or proposal submission unless a separate capability is explicitly invoked.
- Idempotency: per turn/session id; duplicate client retries must not create side effects beyond safe duplicate worktrace handling.
- Policy/approval: cannot grant authority; cannot activate changes; side-effecting tools disabled unless explicitly permitted by tool boundary and capability.
- Audit/trace: PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, model invocation, tool calls/denials, provider failures, AgentWorkTrace.
- Exposure channels: workstream composer/request-response API.
- Agent-type selection: request-based Akka `Agent`; not AutonomousAgent.
- Tests: invokes governed Agent path, enforces ToolPermissionBoundary, fails closed on missing provider config, sanitized markdown, no direct provider bypass, denied tool trace.

### GOVPOL-DRAFT-PROPOSAL: Create draft policy-change proposal

- Purpose: create a non-active draft proposal for a policy, approval gate, managed-agent behavior boundary, or tool-boundary change.
- Actors/callers: authorized governance admin; request/response Agent only through governed side-effecting tool if explicitly permitted.
- AuthContext: `governance.policy.propose` in selected context.
- Inputs: proposal title, target artifact/policy id, proposed change summary/diff, rationale, risk classification, idempotency key, `correlationId`.
- Outputs: `PolicyProposalSurface` in `draft` state with proposal id, version, required approval class, and trace id.
- Data access: existing policy/artifact state, capability catalog, actor authority.
- Side effects: creates proposal record/event; emits audit/work trace.
- Idempotency: idempotency key prevents duplicate draft for same client attempt; duplicate returns existing proposal or safe no-op.
- Policy/approval: draft is not active and does not change authority; may be blocked if target is unsupported or actor lacks proposal authority.
- Audit/trace: proposal-created event with redacted diff summary, authority basis, actor, target, trace id.
- Exposure channels: workstream action, browser API, side-effecting agent tool only when tool boundary allows.
- Tests: success, validation, unsupported target, forbidden actor, cross-tenant target, duplicate idempotency, audit trace.

### GOVPOL-SUBMIT-PROPOSAL: Submit proposal for review

- Purpose: move a draft proposal into review when validation passes.
- Actors/callers: authorized proposal owner or governance admin.
- AuthContext: `governance.policy.propose` or stricter submit scope in selected context.
- Inputs: proposal id, expected version, optional comment, idempotency key, `correlationId`.
- Outputs: proposal state `in_review`, required approver capabilities, trace id, safe validation errors.
- Data access: proposal entity, target artifact summaries, actor authority.
- Side effects: lifecycle event, notification/attention item if implemented, audit trace.
- Idempotency: duplicate submit with same proposal/version is no-op returning current `in_review` state.
- Policy/approval: cannot skip required review; stale version rejected safely.
- Audit/trace: submitted event, validation result, actor, required approval class.
- Exposure channels: proposal surface action, browser API.
- Tests: success, stale version, invalid state, forbidden, duplicate no-op, audit/attention trace.

### GOVPOL-SIMULATE-PROPOSAL: Simulate proposal impact

- Purpose: deterministically evaluate likely impact before approval/activation.
- Actors/callers: authorized governance admin; request/response Agent as read-only evidence tool if permitted.
- AuthContext: `governance.policy.simulate` in selected context.
- Inputs: proposal id or proposed diff, evidence scope, optional sample capability ids, idempotency key for persisted simulation result, `correlationId`.
- Outputs: `PolicySimulationSurface` with affected capabilities/artifacts, expected allows/denials, warnings, unsupported checks, confidence, and trace links.
- Data access: proposal state, policy/capability catalog, selected trace/evidence summaries; scoped/redacted.
- Side effects: may persist simulation result and audit trace; no authority change.
- Idempotency: same proposal/version/scope returns existing simulation result or deterministic equivalent.
- Policy/approval: advisory only; approval still required.
- Audit/trace: simulation event, evidence references, unsupported assumptions, actor.
- Exposure channels: proposal action, browser API, optional read-only agent tool.
- Tests: deterministic result, unsupported target warning, forbidden/cross-tenant, duplicate simulation, audit trace, no activation side effects.

### GOVPOL-DECIDE-PROPOSAL: Approve or reject proposal

- Purpose: record human-governed approval or rejection for an in-review proposal.
- Actors/callers: authorized approver with required capability; not the model alone.
- AuthContext: `governance.policy.approve` plus any target-specific required authority.
- Inputs: proposal id, expected version, decision `approve` or `reject`, rationale, optional conditions, idempotency key, `correlationId`.
- Outputs: proposal state `approved` or `rejected`, decision record, trace id, safe denial/validation errors.
- Data access: proposal entity, actor authority, approval policy, simulation/evidence summaries.
- Side effects: decision event, audit trace, attention update; no activation unless separately invoked.
- Idempotency: duplicate same decision/idempotency key returns existing decision; conflicting duplicate rejected.
- Policy/approval: high-impact/authority-expanding changes require human approval; self-approval restrictions may apply when configured.
- Audit/trace: approval/rejection event with rationale, authority basis, evidence refs.
- Exposure channels: decision surface, browser API.
- Tests: authorized approval, rejection, missing approver capability, stale version, duplicate no-op, conflicting duplicate, audit trace.

### GOVPOL-ACTIVATE-POLICY-CHANGE: Activate approved change

- Purpose: apply an approved policy/governance change to the active backend-governed state.
- Actors/callers: authorized governance admin or workflow step after explicit approval.
- AuthContext: `governance.policy.activate` plus target-specific authority.
- Inputs: proposal id, expected approved version, activation mode, idempotency key, `correlationId`.
- Outputs: activation result, affected artifact ids/versions, rollback reference where available, trace id, safe blocked state.
- Data access: approved proposal, target policy/artifact records, current active state.
- Side effects: state/version activation, lifecycle event, possible workflow completion/notification, audit trace.
- Idempotency: already-active proposal returns no-op success with existing activation trace; stale/unapproved rejected.
- Policy/approval: requires approved proposal; cannot activate draft/rejected/stale proposal; backend enforces all authority.
- Audit/trace: activation event, actor, approval basis, affected capabilities/artifacts, rollback reference.
- Exposure channels: decision/proposal action, browser API, workflow step when needed.
- Tests: success, unapproved blocked, stale version, forbidden, duplicate no-op, rollback reference, audit trace.

### GOVPOL-ROLLBACK-POLICY-CHANGE: Roll back activated change

- Purpose: revert a supported activated governance/policy change to a prior safe state.
- Actors/callers: authorized governance admin.
- AuthContext: `governance.policy.rollback` plus target-specific authority.
- Inputs: activation id or proposal id, rollback target/version, rationale, idempotency key, `correlationId`.
- Outputs: rollback result, restored version/status, trace id, unsupported rollback warning.
- Data access: activation history, current policy/artifact state, rollback metadata.
- Side effects: rollback state change, audit trace, attention update.
- Idempotency: duplicate rollback returns existing result/no-op; conflicting rollback rejected.
- Policy/approval: may require approval for high-impact rollback; unsupported irreversible changes are safely blocked.
- Audit/trace: rollback event, rationale, actor, affected artifacts, no-op/blocked reason.
- Exposure channels: decision/proposal action, browser API, workflow step when needed.
- Tests: supported rollback, unsupported blocked, forbidden, duplicate no-op, cross-tenant denial, audit trace.

### GOVPOL-START-IMPACT-ANALYSIS: Start durable policy-impact analysis

- Purpose: launch a durable background/internal model-driven analysis when a proposal or evidence set needs long-running investigation, replay, or remediation suggestions.
- Actors/callers: authorized governance admin; workflow step if a later implementation needs it.
- AuthContext: `governance.policy.analysis.start` in selected context.
- Inputs: proposal id/evidence scope, task objective, constraints, idempotency key, `correlationId`.
- Outputs: `PolicyAnalysisTaskSurface` with task id, status, progress link, trace id.
- Data access: proposal/policy/trace evidence scoped to selected context; attachments are redacted.
- Side effects: creates AutonomousAgent task, notification/attention item if implemented, audit/work trace.
- Idempotency: same proposal/scope/idempotency key returns existing running task.
- Policy/approval: analysis can recommend but not activate; side-effecting tools denied unless separately approved.
- Audit/trace: task started event, task definition/setup reference, prompt/skill/reference/tool boundary traces.
- Exposure channels: workstream action, browser API, optional workflow start.
- Agent-type selection: Akka `AutonomousAgent` only when implementation includes durable task lifecycle; otherwise defer this capability.
- Tests: authorized start, forbidden, duplicate idempotency, provider fail-closed, task trace, tool-boundary denial.

### GOVPOL-READ-IMPACT-ANALYSIS: Read analysis task status/result

- Purpose: inspect progress, snapshots, notifications, result summaries, and trace links for a policy-impact analysis task.
- Actors/callers: authorized governance admins; My Account aggregate/attention surfaces only as scoped summary if integrated.
- AuthContext: `governance.policy.analysis.read`; task must belong to selected context.
- Inputs: task id, optional snapshot/result view, `correlationId`.
- Outputs: task state, progress, safe result summary, recommendation/evidence refs, trace ids, blocked/failure state.
- Data access: AutonomousAgent task snapshots/results/notifications and redacted evidence refs.
- Side effects: none except read trace if configured.
- Idempotency: read-only.
- Policy/approval: results are advisory; activation still requires explicit approval capabilities.
- Audit/trace: denied reads traced; task lifecycle/work trace linked.
- Exposure channels: task surface, dashboard, browser API.
- Tests: progress read, completed result, failed/blocked state, forbidden/cross-tenant, redaction, trace links.

### GOVPOL-CANCEL-IMPACT-ANALYSIS: Cancel analysis task

- Purpose: cancel a running durable policy-impact analysis task when no longer needed or unsafe.
- Actors/callers: authorized governance admin; workflow supervisor if introduced.
- AuthContext: `governance.policy.analysis.start` or stricter cancel scope in selected context.
- Inputs: task id, reason, idempotency key, `correlationId`.
- Outputs: cancelled/no-op state, trace id, safe failure if task cannot be cancelled.
- Data access: task state and actor authority.
- Side effects: task cancellation/suspend/terminate request, notification update, audit/work trace.
- Idempotency: duplicate cancel returns existing cancelled state; completed task cancel is safe no-op or validation response per implementation.
- Policy/approval: cannot cancel cross-context tasks; cancellation does not delete audit/work traces.
- Audit/trace: cancellation event, actor, reason, prior state, no-op/denial reason.
- Exposure channels: task surface, browser API.
- Tests: authorized cancel, duplicate cancel, completed-task no-op/validation, forbidden/cross-tenant, trace preservation.

## Exposure and validation matrix

| Exposure | Required validation |
|---|---|
| Workstream UI actions | backend denial still enforced; actions hidden only as convenience; loading/empty/success/forbidden/blocked states rendered |
| Browser APIs | token/context validation, safe errors, no secrets, tenant/customer scoping |
| Request/response Agent | governed Agent path, `ToolPermissionBoundary`, `readSkill`/`readReferenceDoc`, provider fail-closed, sanitized markdown |
| Agent tools | capability authorization inside tool facade; read-only by default; side effects only by explicit contract; tool-call traces |
| Workflow/entity commands | validation, idempotency, stale version, no-op, audit events |
| AutonomousAgent tasks | task start/read/cancel authorization, task lifecycle traces, provider/tool-boundary fail-closed, advisory-only results |

## Implementation handoff priorities

1. Implement read dashboard/inventory/detail and proposal lifecycle first; these establish visible governance value without granting unsafe authority.
2. Add request/response explanations through the existing governed runtime path; do not bypass the Akka `Agent` component.
3. Add approval/activation/rollback only with explicit idempotency, audit, and human authority checks.
4. Add AutonomousAgent impact analysis only if the runtime task can implement the real durable task path and validation; otherwise leave the optional capabilities deferred and record the blocker/follow-up.
