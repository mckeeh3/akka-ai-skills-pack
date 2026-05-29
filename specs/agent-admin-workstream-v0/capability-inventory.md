# Agent Admin Workstream v0 Capability Inventory

## Inventory rules

Each capability below inherits `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md` and `workstream-contract.md`.

Common requirements for every protected capability:

- AuthContext: authenticated account, selected tenant/customer context, active membership, non-disabled account, tenant/customer scoped target id, and backend role/scope/capability check.
- Denial shape: safe structured denial with correlation id, reason category, no secrets, and trace link when authorized to view the trace.
- Trace: emit allowed/denied capability audit record plus work trace/correlation id where consequential.
- Exposure channels: browser/workstream/API/agent tool/Workflow/AutonomousAgent/timer/consumer exposure is explicit; unlisted channels are not allowed.
- Tests: success, validation, forbidden, tenant isolation, disabled/missing authority, idempotency/no-op where relevant, audit/work trace, exposure-channel behavior, and provider/tool fail-closed behavior where relevant.

## Capability index

| Capability id | Class | Primary substrate | Exposure channels | Required authority |
|---|---|---|---|---|
| `agent_admin.submit_turn` | request/response agent turn | request-based Akka Agent through governed runtime | workstream composer, HTTP API | `agent_admin.use` |
| `agent_admin.list_definitions` | read/evidence | View/query + deterministic resolver | workstream surface, HTTP API, read-only agent tool | `agent_admin.read` |
| `agent_admin.get_definition` | read/evidence | View/query + deterministic resolver | workstream surface, HTTP API, read-only agent tool | `agent_admin.read` |
| `agent_admin.get_prompt_version` | read/evidence | audited document state + View/query | workstream surface, HTTP API, read-only agent tool | `agent_admin.read` |
| `agent_admin.get_skill_version` | read/evidence | audited document state + View/query | workstream surface, HTTP API, governed `readSkill` path where assigned | `agent_admin.read` plus manifest assignment for loader use |
| `agent_admin.get_reference_version` | read/evidence | audited document state + View/query | workstream surface, HTTP API, governed `readReferenceDoc` path where assigned | `agent_admin.read` plus manifest assignment for loader use |
| `agent_admin.get_manifest` | read/evidence | audited manifest state + View/query | workstream surface, HTTP API, read-only agent tool | `agent_admin.read` |
| `agent_admin.get_model_ref` | read/evidence | governed model config projection | workstream surface, HTTP API | `agent_admin.read` |
| `agent_admin.get_tool_boundary` | read/evidence | deterministic ToolPermissionBoundary resolver + projection | workstream surface, HTTP API, read-only agent tool | `agent_admin.read` |
| `agent_admin.simulate_tool_boundary` | policy/governance | deterministic service | workstream action, HTTP API, agent tool as read-only explanation | `agent_admin.review` |
| `agent_admin.draft_behavior_change` | proposal | request-based Agent + deterministic validators + audited draft state | workstream action, HTTP API, agent tool | `agent_admin.propose` |
| `agent_admin.submit_behavior_change_for_review` | command/proposal | audited state or Workflow | workstream action, HTTP API | `agent_admin.propose` |
| `agent_admin.approve_behavior_change` | approval | audited state or Workflow | workstream action, HTTP API | `agent_admin.review` |
| `agent_admin.reject_behavior_change` | approval | audited state or Workflow | workstream action, HTTP API | `agent_admin.review` |
| `agent_admin.activate_behavior_change` | policy/governance command | audited state/Workflow + deterministic activator | workstream action, HTTP API | `agent_admin.activate` |
| `agent_admin.cancel_behavior_change` | command | audited state or Workflow | workstream action, HTTP API | owner with `agent_admin.propose` or reviewer with `agent_admin.review` |
| `agent_admin.list_seed_material` | read/evidence | deterministic seed registry + projection | workstream surface, HTTP API | `agent_admin.read` |
| `agent_admin.reseed_missing_defaults` | command | deterministic idempotent seed service | workstream action, HTTP API | `agent_admin.seed` |
| `agent_admin.start_behavior_review_task` | autonomous task | Akka AutonomousAgent through governed task capability | workstream action, HTTP API, Workflow step optional | `agent_admin.evaluate` |
| `agent_admin.get_behavior_review_task` | autonomous task read | AutonomousAgent task state/projection | workstream surface, HTTP API, notification surface | `agent_admin.evaluate` or trace/read authority scoped to task |
| `agent_admin.cancel_behavior_review_task` | autonomous task command | AutonomousAgent task control facade | workstream action, HTTP API | `agent_admin.evaluate` |

## Detailed capabilities

### `agent_admin.submit_turn`

- Purpose: Handle an Agent Admin workstream composer request and return safe markdown_response or structured surface guidance.
- Actors/callers: authorized administrator human through the workstream shell; backend request handler; Agent Admin Agent component.
- AuthContext: selected tenant/customer context; `agent_admin.use`; target workstream id `agent_admin`.
- Inputs: message text, optional selected surface id, optional referenced agent/config ids, idempotency/correlation id, client locale/display hints.
- Outputs: sanitized markdown_response, structured surface request, safe blocked/error surface, trace/correlation id.
- Data access: active Agent Admin governed managed-agent `AgentDefinition`, prompt, AgentSkillManifest, AgentReferenceManifest, model ref, ToolPermissionBoundary, permitted read evidence.
- Side effects: workstream timeline entry, PromptAssemblyTrace, SkillLoadTrace/ReferenceLoadTrace when used, tool/model traces, AgentWorkTrace.
- Idempotency: duplicate idempotency key returns existing timeline/result or no-op duplicate trace.
- Policy/approval: no direct behavior-changing side effect except through separate governed capabilities; side-effecting tools require explicit tool-boundary grant and approval where configured.
- Audit/trace: record auth basis, prompt assembly, provider call or provider blocked state, tools registered/invoked/denied, response shape.
- Exposure channels: workstream composer and HTTP API only.
- Tests: authorized success through request-based Akka Agent, validation for blank/oversized messages, forbidden tenant/capability denial, provider missing fail-closed trace, ToolPermissionBoundary denial, no deterministic normal-runtime fallback.

### `agent_admin.list_definitions`

- Purpose: List managed-agent definitions visible to the selected context.
- Actors/callers: administrator, Agent Admin Agent read-only tool, workstream surface loader.
- AuthContext: selected tenant/customer; `agent_admin.read`.
- Inputs: paging, status filter, owner/steward filter, optional workstream id, correlation id.
- Outputs: redacted list of agent ids, names, lifecycle status, active/default markers, authority tier, model-ref label, health/blocked state, trace id.
- Data access: AgentDefinition projection scoped by tenant/customer.
- Side effects: read audit/work trace only.
- Idempotency: read-only.
- Policy/approval: none beyond read authorization.
- Audit/trace: record scoped read, filters, result count, redaction policy.
- Exposure channels: structured surface, HTTP API, read-only agent tool.
- Tests: success, paging validation, cross-tenant denial, redaction, audit trace.

### `agent_admin.get_definition`

- Purpose: Retrieve a redacted detail view for one managed-agent definition.
- Actors/callers: administrator, Agent Admin Agent read-only tool, surface loader.
- AuthContext: selected tenant/customer matching definition; `agent_admin.read`.
- Inputs: agentDefinitionId, optional requested version/status, correlation id.
- Outputs: definition metadata, lifecycle status, prompt/skill/reference manifest summaries, model-ref summary, tool-boundary summary, seed/default provenance, trace links.
- Data access: AgentDefinition state/projection and related document/manifest references.
- Side effects: read audit/work trace only.
- Idempotency: read-only.
- Policy/approval: no content secrets or provider secrets returned.
- Audit/trace: record read and redactions.
- Exposure channels: structured surface, HTTP API, read-only agent tool.
- Tests: success, not found, cross-tenant denial, disabled user denial, redacted output, trace link.

### `agent_admin.get_prompt_version`

- Purpose: Show approved/draft prompt metadata and content preview according to authority.
- Actors/callers: administrator, Agent Admin Agent read-only explanation tool.
- AuthContext: selected tenant/customer; `agent_admin.read`; prompt belongs to accessible agent or global allowed scope.
- Inputs: promptDocumentId, version id/status, optional diff base id, correlation id.
- Outputs: version metadata, lifecycle status, redacted body/preview, diff summary when allowed, assigned agents/manifests, provenance, trace link.
- Data access: PromptDocument/PromptVersion state and assignment projection.
- Side effects: read audit/work trace.
- Idempotency: read-only.
- Policy/approval: draft or sensitive content may require `agent_admin.review`; secrets must be redacted.
- Audit/trace: record read, redaction, version ids.
- Exposure channels: structured surface, HTTP API, read-only agent tool.
- Tests: authorized read, draft authority denial, cross-tenant denial, redaction, audit trace.

### `agent_admin.get_skill_version`

- Purpose: Show governed skill metadata/content preview and support governed runtime `readSkill(skillId)` loading when assigned.
- Actors/callers: administrator, Agent Admin Agent through backend-authorized read-only tool, governed loader tool.
- AuthContext: selected tenant/customer; `agent_admin.read`; for `readSkill`, active agent definition and manifest assignment must match.
- Inputs: skillId/version id/status or loader `skillId`, active agent id when invoked by runtime, correlation id.
- Outputs: skill metadata, version status, compact manifest summary, redacted body/preview or loader content, denial shape if not assigned/authorized.
- Data access: SkillDocument/SkillVersion, AgentSkillManifest, active AgentDefinition.
- Side effects: read audit; SkillLoadTrace for loader path.
- Idempotency: read-only.
- Policy/approval: loaded skill text is guidance only and grants no authority.
- Audit/trace: record allowed/denied loads, manifest basis, version id, redaction.
- Exposure channels: structured surface, HTTP API, governed `readSkill(skillId)` tool only when assigned.
- Tests: assigned load success, unassigned denial, wrong tenant denial, inactive version denial, SkillLoadTrace emission.

### `agent_admin.get_reference_version`

- Purpose: Show governed reference metadata/content preview and support governed runtime `readReferenceDoc(referenceId)` loading when assigned.
- Actors/callers: administrator, Agent Admin Agent through read-only tool, governed loader tool.
- AuthContext: selected tenant/customer; `agent_admin.read`; for loader path, active agent definition and manifest assignment must match.
- Inputs: referenceId/version id/status or loader `referenceId`, active agent id when invoked by runtime, correlation id.
- Outputs: reference metadata, version status, compact manifest summary, redacted body/preview or loader content, safe denial.
- Data access: ReferenceDocument/ReferenceVersion, AgentReferenceManifest, active AgentDefinition.
- Side effects: read audit; ReferenceLoadTrace for loader path.
- Idempotency: read-only.
- Policy/approval: reference evidence may be redacted by tenant/customer scope and data classification.
- Audit/trace: record allowed/denied loads, manifest basis, version id, redaction.
- Exposure channels: structured surface, HTTP API, governed `readReferenceDoc(referenceId)` tool only when assigned.
- Tests: assigned load success, unassigned denial, cross-tenant denial, redacted content, ReferenceLoadTrace emission.

### `agent_admin.get_manifest`

- Purpose: Read compact skill/reference manifest assignments for an agent.
- Actors/callers: administrator, Agent Admin Agent read-only tool, runtime resolver.
- AuthContext: selected tenant/customer; `agent_admin.read`; runtime resolver also requires active target agent id.
- Inputs: agentDefinitionId, manifest type, version/status, correlation id.
- Outputs: compact manifest entries with ids/names/descriptions/when-to-use hints, assignment status, version/provenance, trace link.
- Data access: AgentSkillManifest and AgentReferenceManifest state/projection.
- Side effects: read audit; prompt assembly trace references for runtime path.
- Idempotency: read-only.
- Policy/approval: manifest assignment does not authorize actions; it only permits controlled guidance/evidence loading.
- Audit/trace: record manifest read, active version basis, denied missing/inactive manifests.
- Exposure channels: structured surface, HTTP API, read-only agent tool, internal runtime resolver.
- Tests: success, inactive manifest denial/blocked state, cross-tenant denial, trace emission.

### `agent_admin.get_model_ref`

- Purpose: Show browser-safe model configuration reference status for a managed agent.
- Actors/callers: administrator, surface loader, runtime resolver.
- AuthContext: selected tenant/customer; `agent_admin.read`.
- Inputs: modelConfigRef id or agentDefinitionId, correlation id.
- Outputs: provider/model labels, status, policy class, configured/blocked state, no secret values, trace link.
- Data access: governed model config projection with secrets excluded.
- Side effects: read audit; runtime blocked trace if missing provider config.
- Idempotency: read-only.
- Policy/approval: provider secrets remain backend-only; missing config fails closed for model-backed behavior.
- Audit/trace: record read and redaction; provider blocked traces where relevant.
- Exposure channels: structured surface, HTTP API, internal runtime resolver.
- Tests: redaction/secret-boundary, missing config blocked shape, forbidden read, trace.

### `agent_admin.get_tool_boundary`

- Purpose: Show the active ToolPermissionBoundary for a managed agent.
- Actors/callers: administrator, Agent Admin Agent read-only tool, runtime resolver.
- AuthContext: selected tenant/customer; `agent_admin.read`.
- Inputs: agentDefinitionId or toolBoundaryId, optional tool filter, correlation id.
- Outputs: allowed/denied tool ids, read-only/side-effect classification, approval requirements, boundary version/status, denial trace links.
- Data access: ToolPermissionBoundary state/projection and backend tool registry metadata.
- Side effects: read audit; runtime uses same boundary to register tools.
- Idempotency: read-only.
- Policy/approval: boundary controls tool registration and invocation eligibility but still does not bypass capability authorization.
- Audit/trace: record boundary read, redaction, active basis.
- Exposure channels: structured surface, HTTP API, read-only agent tool, internal runtime resolver.
- Tests: success, wrong tenant denial, inactive boundary blocked state, audit trace.

### `agent_admin.simulate_tool_boundary`

- Purpose: Explain whether a requested tool would be allowed for a given agent/context without executing the tool.
- Actors/callers: administrator/reviewer, Agent Admin Agent explanation tool.
- AuthContext: selected tenant/customer; `agent_admin.review`.
- Inputs: agentDefinitionId, tool id, proposed input classification, side-effect flag, target capability id, correlation id.
- Outputs: allow/deny decision, reasons, required permission/approval, boundary version, safe trace id.
- Data access: ToolPermissionBoundary, tool registry metadata, AuthContext capability grants, policy settings.
- Side effects: simulation audit/work trace only.
- Idempotency: deterministic read-only simulation.
- Policy/approval: cannot execute side effects or grant authority; outputs recommendation/evidence only.
- Audit/trace: record simulation request and decision.
- Exposure channels: workstream action, HTTP API, read-only agent tool.
- Tests: allowed read-only tool, denied side-effecting tool, missing authority, tenant isolation, trace.

### `agent_admin.draft_behavior_change`

- Purpose: Draft a change proposal for prompt, skill, reference, manifest, model ref, or ToolPermissionBoundary without activating it.
- Actors/callers: authorized administrator, Agent Admin Agent as drafting assistant.
- AuthContext: selected tenant/customer; `agent_admin.propose`.
- Inputs: target artifact id/type, desired change description or structured patch, reason, evidence ids, idempotency key, correlation id.
- Outputs: draft proposal id, normalized diff, risk/impact classification, authority-expansion flag, validation findings, required reviewer authority, trace link.
- Data access: current target artifact versions, allowed references/evidence, validator rules.
- Side effects: create draft proposal/version; audit/work trace; no activation.
- Idempotency: duplicate key returns existing draft or no-op duplicate result.
- Policy/approval: authority-expanding, model, tool-boundary, or production active changes require review/activation capability; Agent can recommend but not self-approve.
- Audit/trace: record source request, model involvement if used, validation, redaction, proposal creation.
- Exposure channels: workstream action, HTTP API, agent tool with proposal-only authority.
- Tests: draft success, validation errors, forbidden target, duplicate idempotency, trace, no active config change.

### `agent_admin.submit_behavior_change_for_review`

- Purpose: Move a valid draft proposal into review.
- Actors/callers: proposal owner or authorized administrator.
- AuthContext: selected tenant/customer; `agent_admin.propose`.
- Inputs: proposal id, expected version, reviewer notes, idempotency key, correlation id.
- Outputs: review status, required reviewer authority, trace link.
- Data access: proposal state and target artifact metadata.
- Side effects: state transition, notification/attention item if supported, audit trace.
- Idempotency: repeated submit on already-in-review proposal returns no-op success.
- Policy/approval: rejected/invalid/cancelled drafts cannot be submitted; authority-expanding proposals flagged.
- Audit/trace: record state transition and actor.
- Exposure channels: workstream action, HTTP API.
- Tests: success, invalid state, stale expected version, duplicate no-op, forbidden actor, audit.

### `agent_admin.approve_behavior_change`

- Purpose: Human-governed approval of a behavior-change proposal before activation.
- Actors/callers: authorized reviewer/approver.
- AuthContext: selected tenant/customer; `agent_admin.review`; additional permission for authority-expanding categories if needed.
- Inputs: proposal id, expected version, approval decision note, idempotency key, correlation id.
- Outputs: approved status, activation eligibility, trace link.
- Data access: proposal, target artifact, risk/impact findings, reviewer authority basis.
- Side effects: proposal state transition, audit event, optional notification.
- Idempotency: repeated identical approval returns existing approved state; conflicting decisions are rejected.
- Policy/approval: proposer self-approval may be denied when separation of duties is configured.
- Audit/trace: record reviewer, authority basis, risk acceptance, correlation id.
- Exposure channels: workstream action, HTTP API.
- Tests: approval success, forbidden/missing reviewer authority, self-approval denial where configured, duplicate no-op, audit.

### `agent_admin.reject_behavior_change`

- Purpose: Reject a behavior-change proposal with a reason.
- Actors/callers: authorized reviewer/approver.
- AuthContext: selected tenant/customer; `agent_admin.review`.
- Inputs: proposal id, expected version, rejection reason, idempotency key, correlation id.
- Outputs: rejected status, trace link, optional remediation guidance.
- Data access: proposal and target artifact metadata.
- Side effects: proposal state transition, audit event, optional notification.
- Idempotency: repeated identical rejection returns existing rejected state; conflicting decision rejected.
- Policy/approval: rejected proposal cannot be activated without new draft/review.
- Audit/trace: record reviewer, reason category, correlation id.
- Exposure channels: workstream action, HTTP API.
- Tests: success, missing reason validation, forbidden reviewer, duplicate no-op, audit.

### `agent_admin.activate_behavior_change`

- Purpose: Activate an approved prompt/skill/reference/manifest/model/tool-boundary change.
- Actors/callers: authorized activator or reviewer with activation authority.
- AuthContext: selected tenant/customer; `agent_admin.activate`.
- Inputs: proposal id, approved version, activation mode, idempotency key, correlation id.
- Outputs: activation result, new active version ids, previous version ids, rollback reference where available, trace link.
- Data access: approved proposal, target artifact state, runtime resolver indexes/projections.
- Side effects: active version transition, cache/projection invalidation if applicable, audit/work trace, optional notification.
- Idempotency: repeated activation of same approved version returns no-op active result; stale/conflicting activation rejected.
- Policy/approval: only approved proposals are activatable; high-impact categories may require additional approval gates.
- Audit/trace: record activation actor, target ids, old/new versions, authority basis.
- Exposure channels: workstream action, HTTP API, internal deterministic activator.
- Tests: success, unapproved proposal denial, stale version validation, duplicate no-op, runtime resolver sees active change, audit.

### `agent_admin.cancel_behavior_change`

- Purpose: Cancel a draft or review proposal that should no longer proceed.
- Actors/callers: proposal owner with propose authority or reviewer.
- AuthContext: selected tenant/customer; `agent_admin.propose` for own draft or `agent_admin.review` for any scoped proposal.
- Inputs: proposal id, expected version, reason, idempotency key, correlation id.
- Outputs: cancelled status and trace link.
- Data access: proposal state.
- Side effects: proposal state transition, audit event.
- Idempotency: repeated cancellation returns no-op success.
- Policy/approval: active/already-applied changes cannot be cancelled; require rollback proposal later.
- Audit/trace: record actor, reason, state transition.
- Exposure channels: workstream action, HTTP API.
- Tests: success, forbidden actor, invalid state, duplicate no-op, audit.

### `agent_admin.list_seed_material`

- Purpose: Show implementation-developed default managed-agent records and tenant override/installed status.
- Actors/callers: administrator, seed readiness surface.
- AuthContext: selected tenant/customer; `agent_admin.read`.
- Inputs: seed category filter, status filter, correlation id.
- Outputs: seed ids, artifact types, version/provenance, installed/missing/overridden status, safe trace link.
- Data access: seed registry and tenant installed/default artifact projection.
- Side effects: read audit only.
- Idempotency: read-only.
- Policy/approval: no seed content with secrets; override state is informational.
- Audit/trace: record read and result counts.
- Exposure channels: structured surface, HTTP API.
- Tests: success, redaction, cross-tenant denial, audit.

### `agent_admin.reseed_missing_defaults`

- Purpose: Idempotently install missing implementation-developed default Agent Admin artifacts without overwriting tenant customizations.
- Actors/callers: administrator with seed authority; deterministic seed service.
- AuthContext: selected tenant/customer; `agent_admin.seed`.
- Inputs: optional seed category ids, dry-run flag, idempotency key, correlation id.
- Outputs: installed/skipped/already-present/conflict results, trace link.
- Data access: seed registry, target governed artifact state, installed seed ledger.
- Side effects: create missing default versions/manifests/boundaries, ledger entries, audit/work trace; no overwrite of customized active tenant artifacts.
- Idempotency: repeated request returns already-present/skipped result; dry-run makes no changes.
- Policy/approval: cannot silently overwrite approved active tenant customization; conflicts require proposal/review.
- Audit/trace: record each seed decision with provenance and actor.
- Exposure channels: workstream action, HTTP API, deterministic seed service.
- Tests: missing install, already-present no-op, customization skip, forbidden seed authority, audit.

### `agent_admin.start_behavior_review_task`

- Purpose: Start an optional durable background evaluation/review task for behavior changes or managed-agent health when task lifecycle is justified.
- Actors/callers: authorized administrator/reviewer; optional Workflow step.
- AuthContext: selected tenant/customer; `agent_admin.evaluate`; target artifacts scoped to context.
- Inputs: target agent/artifact ids, review goal, acceptance criteria, attachments/evidence ids, model policy ref, idempotency key, correlation id.
- Outputs: task id, accepted/rejected/blocked status, initial progress snapshot, trace link.
- Data access: target artifacts, allowed evidence/reference docs, model config policy, ToolPermissionBoundary for the autonomous reviewer.
- Side effects: AutonomousAgent task start, task lifecycle trace, notification/attention item where supported.
- Idempotency: duplicate key returns existing task or no-op duplicate result.
- Policy/approval: task may produce findings/recommendations only unless separate approval/activation capability grants side effects; missing provider/config fails closed.
- Audit/trace: record task start, authority, setup, model/provider blocked state, tool boundary.
- Exposure channels: workstream action, HTTP API, optional Workflow step.
- Tests: accepted task start, validation rejection, forbidden authority, provider missing blocked state, idempotent duplicate, trace.

### `agent_admin.get_behavior_review_task`

- Purpose: Read progress, blocked/failure state, notifications, and result of an AutonomousAgent-backed review task.
- Actors/callers: administrator/reviewer, workstream dashboard/surface, notification surface.
- AuthContext: selected tenant/customer; `agent_admin.evaluate` or scoped read authority for created task.
- Inputs: task id, optional snapshot/result version, correlation id.
- Outputs: task status, progress summary, findings, recommendation/proposal links, blocked/provider failure state, trace links.
- Data access: AutonomousAgent task state/projection, notifications, result artifacts, trace records.
- Side effects: read audit; optional notification read acknowledgement if explicitly requested via separate action.
- Idempotency: read-only.
- Policy/approval: findings do not activate behavior changes; side effects require proposal/review/activation capabilities.
- Audit/trace: record task read and redactions.
- Exposure channels: structured surface, HTTP API, notification surface.
- Tests: success, task not found, cross-tenant denial, redacted result, blocked/failure rendering, audit.

### `agent_admin.cancel_behavior_review_task`

- Purpose: Cancel/suspend a running AutonomousAgent-backed review task when authorized.
- Actors/callers: authorized administrator/reviewer.
- AuthContext: selected tenant/customer; `agent_admin.evaluate`.
- Inputs: task id, reason, expected task state/version, idempotency key, correlation id.
- Outputs: cancelled/already-finished/already-cancelled status, trace link.
- Data access: AutonomousAgent task state/projection.
- Side effects: task cancellation/control call, lifecycle trace, notification/attention update.
- Idempotency: repeated cancel returns no-op cancelled result; completed task returns already-finished safe result.
- Policy/approval: cannot delete trace/history; cancellation does not revoke already-created proposals.
- Audit/trace: record actor, reason, previous/new state.
- Exposure channels: workstream action, HTTP API.
- Tests: cancel success, already completed no-op, forbidden actor, stale state validation, trace.

## Cross-capability validation matrix

| Concern | Required coverage |
|---|---|
| AuthContext and authorization | Every capability tests selected context, membership, role/scope/capability, disabled account, and tenant/customer isolation for at least representative read/write/tool/task paths. |
| Request/response Agent runtime | `agent_admin.submit_turn` tests active AgentDefinition resolution, prompt assembly, ToolPermissionBoundary enforcement, real Akka Agent invocation path, trace emission, and provider fail-closed behavior. |
| ToolPermissionBoundary | Runtime tool registration/invocation denial plus `simulate_tool_boundary` deterministic explanation are tested. |
| Governed loader tools | `readSkill(skillId)` and `readReferenceDoc(referenceId)` succeed only for active assigned manifests and emit load traces; unassigned/wrong-tenant/inactive versions are denied. |
| Behavior changes | Draft/review/approval/rejection/activation/cancellation tests prove no active config mutation occurs before approved activation and duplicate commands are idempotent. |
| Seed defaults | Reseed installs missing defaults only, skips existing/customized artifacts, and records provenance/audit. |
| AutonomousAgent task | If implemented, task start/read/cancel tests prove durable task identity, progress/result surfaces, cancellation/failure, provider blocked behavior, and traces. |
| Deterministic services | Resolver, validator, redactor, seed, policy, lifecycle, and trace-normalization behavior are not treated as AI agents and do not bypass backend authorization. |
| UI/API exposure | Browser surfaces render success, empty, forbidden, validation-error, provider-blocked, tool-denied, and trace-link states; frontend never exposes provider secrets or decides authorization alone. |

## Runtime completion notes

This inventory is an implementation contract. A later runtime task may narrow a capability only by updating this file and the workstream contract first. A capability is not implemented merely because static data, prompt text, or a frontend-only mock exists. Runtime completion requires the intended local Akka/API/UI path to execute with backend authorization, trace/audit records, provider fail-closed behavior, and tests at the stated scope.
