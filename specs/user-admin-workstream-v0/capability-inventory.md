# User Admin Workstream v0 Capability Inventory

## Inheritance

This inventory implements the `User Admin Agent` v0 workstream contract in `specs/user-admin-workstream-v0/workstream-contract.md` and inherits the shared rules from:

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`

Each capability is a governed backend operation/query before it is exposed through browser UI, API, agent tool, workflow, timer, consumer, or AutonomousAgent task lifecycle.

## Shared capability defaults

Unless a capability states otherwise:

- AuthContext: authenticated Account with selected tenant/customer context, active membership, non-disabled status, required role/scope/capability, and tenant/customer-scoped data filters.
- Safe denial output: stable error code, user-safe message, denied capability id, correlation id/trace reference, no secret/provider exposure, and no cross-tenant data leakage.
- Audit/trace: record caller, AuthContext, selected context, capability id, request/correlation id, authorization basis, data classes accessed, outcome, denial reason when denied, and redaction basis.
- Idempotency: commands accept or derive an idempotency/correlation key; duplicate commands return existing outcome or no-op result without duplicate side effects.
- Tests: success, validation, forbidden/missing authority, tenant/customer isolation, disabled user, idempotency/no-op where relevant, audit/trace creation, exposure-channel behavior, and safe denial shape.
- Agent tools: may expose only the capabilities explicitly listed as agent-tool exposure and must preserve `ToolPermissionBoundary` enforcement plus work trace emission.

## Capability summary

| Capability id | Class | Primary exposure | Akka substrate | Agent-type choice |
|---|---|---|---|---|
| `USERADMIN_VIEW_OVERVIEW` | Read/evidence | Workstream/API/agent tool | View/API facade | Deterministic service/read model |
| `USERADMIN_LIST_INVITATIONS` | Read/evidence | Workstream/API/agent tool | View/API facade | Deterministic service/read model |
| `USERADMIN_SEND_INVITATION` | Command/workflow | Workstream/API/approval-capable agent request | Workflow/entity/outbox/API facade | Deterministic lifecycle service; optional agent drafts only |
| `USERADMIN_RESEND_INVITATION` | Command/workflow | Workstream/API | Workflow/entity/outbox/API facade | Deterministic lifecycle service |
| `USERADMIN_REVOKE_INVITATION` | Command | Workstream/API | Entity/API facade | Deterministic lifecycle service |
| `USERADMIN_LIST_MEMBERS` | Read/evidence | Workstream/API/agent tool | View/API facade | Deterministic service/read model |
| `USERADMIN_UPDATE_MEMBER_STATUS` | Command | Workstream/API | Entity/API facade | Deterministic validation service |
| `USERADMIN_LIST_ROLES_CAPABILITIES` | Read/evidence | Workstream/API/agent tool | View/API facade | Deterministic service/read model |
| `USERADMIN_PREVIEW_ROLE_CHANGE` | Proposal/preview | Workstream/API/agent tool | Service/API facade | Deterministic policy preview; request-based Agent may explain |
| `USERADMIN_CHANGE_MEMBER_ROLES` | Command/approval-aware | Workstream/API | Entity/workflow/API facade | Deterministic validation and audit; approval when high-impact |
| `USERADMIN_AGENT_TURN` | Request/response | Composer/API stream | Akka Agent | Request-based Akka `Agent` |
| `USERADMIN_START_ACCESS_REVIEW_TASK` | Autonomous task | Workstream/API | AutonomousAgent facade | Akka `AutonomousAgent` when implemented |
| `USERADMIN_VIEW_ACCESS_REVIEW_TASK` | Autonomous task/read | Workstream/API/agent tool | AutonomousAgent notifications/view | Akka `AutonomousAgent` when implemented |
| `USERADMIN_CANCEL_ACCESS_REVIEW_TASK` | Autonomous task command | Workstream/API | AutonomousAgent facade | Akka `AutonomousAgent` when implemented |
| `USERADMIN_VIEW_ACCESS_REVIEW_RESULT` | Read/evidence | Workstream/API/agent tool | View/API facade | AutonomousAgent result evidence |
| `USERADMIN_VIEW_TRACE_REFERENCE` | Trace/audit read | Workstream/API/deep link | View/API facade | Deterministic trace redaction service |

## Detailed capabilities

### `USERADMIN_VIEW_OVERVIEW`

- Purpose: return the User Admin overview, readiness status, pending invitations, member counts, role/capability summary, access-review status, allowed next actions, and trace/correlation references.
- Actors/callers: authorized tenant/customer admins, User Admin Agent tool for read-only context, support/SaaS-owner roles only through explicit authority.
- Inputs: selected context, optional filters for tenant/customer scope, request/correlation id.
- Outputs: browser/agent-safe overview DTO with counts, readiness flags, allowed surface actions, denials/blocked states, trace refs, and no secrets.
- Data access: scoped account, membership, role/capability, invitation, access-review task summary, audit summary references.
- Side effects: protected read audit/work trace; no state mutation.
- Idempotency: repeated reads are side-effect-safe except read trace/audit.
- Policy/approval: no approval; all data redacted by caller authority.
- Audit/trace: record protected overview read and any denied data class.
- Exposure channels: overview surface, HTTP/API, read-only agent tool.
- Tests: authorized overview, forbidden context, disabled account, redaction by role, trace ref present.

### `USERADMIN_LIST_INVITATIONS`

- Purpose: list scoped invitations and lifecycle state for administration.
- Actors/callers: authorized admins; User Admin Agent read-only tool.
- Inputs: selected context, status filter, page cursor/limit, correlation id.
- Outputs: invitation list DTO with id, email/domain-safe display, status, expiry, last delivery state, allowed actions, trace refs.
- Data access: Invitation records/view, delivery/outbox status, membership collision evidence.
- Side effects: protected read trace only.
- Idempotency: read-only.
- Policy/approval: no approval; redaction for PII and support contexts.
- Audit/trace: record list access and redaction decisions.
- Exposure channels: invitation surface, HTTP/API, read-only agent tool.
- Tests: success, invalid pagination, forbidden tenant, PII redaction, trace links.

### `USERADMIN_SEND_INVITATION`

- Purpose: invite a user to a tenant/customer context with selected initial role/capability grants.
- Actors/callers: authorized admins with invitation authority; User Admin Agent may draft/explain but must not bypass approval/authorization.
- Inputs: email, target context, requested roles/capabilities, expiry, optional message, idempotency key, correlation id.
- Outputs: invitation id/status, delivery state or queued outbox id, safe validation errors, trace refs.
- Data access: Account/UserProfile lookup, Membership, Role/Capability, Invitation, email outbox/delivery state.
- Side effects: create Invitation, enqueue/send email through shared email service/outbox, emit audit/trace, possibly schedule expiry/reminder.
- Idempotency: duplicate key or same pending invite returns existing invitation/no-op without duplicate email unless explicit resend capability is used.
- Policy/approval: high-impact roles/capabilities may require explicit approval or denial; inviter cannot grant capabilities they do not hold/delegate.
- Audit/trace: invitation created/queued/sent/denied, role grants requested, delivery status, policy basis.
- Exposure channels: invitation surface action, HTTP/API; agent exposure limited to draft/recommend/request when a tool boundary explicitly grants it.
- Akka substrate: Invitation entity/workflow, outbox consumer/timer where present, API facade.
- Tests: success, invalid email/role, duplicate idempotency, forbidden grant, cross-tenant target, disabled inviter, audit/outbox trace.

### `USERADMIN_RESEND_INVITATION`

- Purpose: resend an existing pending invitation.
- Actors/callers: authorized admins.
- Inputs: invitation id, optional idempotency key, correlation id.
- Outputs: invitation status, delivery/outbox status, safe errors, trace ref.
- Data access: scoped Invitation, delivery/outbox status.
- Side effects: enqueue/send another email only when invitation is resendable; audit/trace.
- Idempotency: repeated resend with same key does not enqueue duplicate delivery.
- Policy/approval: no approval unless resend volume/risk policy requires it.
- Exposure channels: invitation surface action, HTTP/API.
- Tests: success, expired/revoked not resendable, forbidden tenant, duplicate key, audit/outbox trace.

### `USERADMIN_REVOKE_INVITATION`

- Purpose: revoke a pending invitation.
- Actors/callers: authorized admins.
- Inputs: invitation id, reason, idempotency key, correlation id.
- Outputs: revoked status or no-op already-revoked result, trace ref.
- Data access: scoped Invitation.
- Side effects: update invitation lifecycle, audit/trace, optional notification/email where supported.
- Idempotency: revoking an already revoked invitation returns no-op success with trace.
- Policy/approval: no approval by default; high-impact support/SaaS-owner contexts may require separate authority.
- Exposure channels: invitation surface action, HTTP/API.
- Tests: success, already revoked no-op, accepted invitation rejection, forbidden tenant, audit trace.

### `USERADMIN_LIST_MEMBERS`

- Purpose: list scoped members, account status, selected role/capability summaries, and allowed actions.
- Actors/callers: authorized admins; User Admin Agent read-only tool.
- Inputs: selected context, status/role filter, page cursor/limit, correlation id.
- Outputs: member directory DTO with account-safe identity fields, membership status, role summaries, allowed actions, trace refs.
- Data access: Account/UserProfile, Membership, Role/Capability views.
- Side effects: protected read trace only.
- Idempotency: read-only.
- Policy/approval: redaction of PII and privileged role details by authority.
- Exposure channels: member directory surface, HTTP/API, read-only agent tool.
- Tests: success, forbidden context, disabled requester, pagination validation, redaction, trace.

### `USERADMIN_UPDATE_MEMBER_STATUS`

- Purpose: enable, disable, suspend, or reactivate a scoped membership according to policy.
- Actors/callers: authorized admins.
- Inputs: member/account id, target status, reason, idempotency key, correlation id.
- Outputs: updated/no-op status, safe denial/validation result, trace ref.
- Data access: Membership, Account status where applicable, dependent role/capability state.
- Side effects: status change, audit/trace, session invalidation or notification where supported.
- Idempotency: repeated same target status returns no-op success.
- Policy/approval: cannot disable last required owner/admin without accepted break-glass/support policy; high-impact changes may require approval.
- Exposure channels: member directory action, HTTP/API.
- Tests: success, no-op, cannot disable self/last admin where policy forbids, forbidden tenant, audit trace.

### `USERADMIN_LIST_ROLES_CAPABILITIES`

- Purpose: show roles, capabilities, assignability, and policy constraints for the selected context.
- Actors/callers: authorized admins; User Admin Agent read-only tool.
- Inputs: selected context, optional role/capability filter, correlation id.
- Outputs: role/capability matrix DTO with assignability flags, policy notes, redacted sensitive capabilities, trace refs.
- Data access: Role, Permission/Capability, Membership assignment views.
- Side effects: protected read trace only.
- Idempotency: read-only.
- Policy/approval: redacts or marks non-assignable capabilities that caller cannot grant.
- Exposure channels: role/capability surface, HTTP/API, read-only agent tool.
- Tests: success, redacted sensitive capability, forbidden requester, trace.

### `USERADMIN_PREVIEW_ROLE_CHANGE`

- Purpose: preview impact, policy checks, approval need, and denials before changing member roles/capabilities.
- Actors/callers: authorized admins; User Admin Agent read-only/proposal tool.
- Inputs: member id, requested role/capability delta, reason, correlation id.
- Outputs: preview DTO with allowed/denied changes, impact/risk, required approvals, no-op items, trace ref.
- Data access: current membership roles, role/capability policy, audit history references where allowed.
- Side effects: protected preview trace; no mutation.
- Idempotency: read-only/proposal.
- Policy/approval: identifies approval gates; does not grant authority.
- Exposure channels: role/capability surface action, HTTP/API, agent tool for explanation/recommendation.
- Tests: success allowed preview, denied expansion, last-admin risk, cross-tenant member, trace.

### `USERADMIN_CHANGE_MEMBER_ROLES`

- Purpose: apply authorized member role/capability assignment changes.
- Actors/callers: authorized admins with delegation authority; approved workflow/human approver when required.
- Inputs: member id, role/capability delta, reason, idempotency key, correlation id, approval reference when required.
- Outputs: updated/no-op assignment result, denied/approval-required result, trace ref.
- Data access: Membership, Role/Capability, approval/policy records where present.
- Side effects: mutate membership role assignments, audit/trace, notify affected user where supported.
- Idempotency: repeated same delta/key returns existing outcome; empty/no-op delta returns no-op result.
- Policy/approval: cannot grant capabilities caller lacks or cannot delegate; high-impact/security/governance roles require approval unless policy explicitly allows.
- Exposure channels: role/capability surface action, HTTP/API; not a default agent side-effect tool.
- Akka substrate: entity command and optional approval workflow/API facade.
- Tests: success, validation/no-op, forbidden grant, approval required, cross-tenant member, disabled actor, audit trace.

### `USERADMIN_AGENT_TURN`

- Purpose: handle normal User Admin composer turns with safe guidance and optionally structured surfaces.
- Actors/callers: authorized workstream users through composer/API.
- Inputs: message, selected context, conversation/session id, requested surface/action context, correlation id.
- Outputs: sanitized markdown response or typed surface/system-message, blocked-provider surface, safe denial, trace refs.
- Data access: governed managed-agent `AgentDefinition`, prompt/skill/reference manifests, scoped read-only evidence capabilities exposed through tools, trace records.
- Side effects: prompt assembly trace, model invocation trace, tool invocation/denial traces, agent work trace; no administrative mutation unless a separately authorized capability/request path handles it.
- Idempotency: repeated message may create distinct conversation turns; duplicate request ids should not duplicate side-effecting tool calls.
- Policy/approval: tool boundary controls callable tools; prompt/skill/reference text cannot grant authority.
- Exposure channels: composer/API stream, User Admin Agent response surface.
- Akka substrate: request-based Akka `Agent` invoked through governed runtime with `effects().tools(runtimeTools)`.
- Tests: authorized turn, missing provider fail-closed, tool-boundary denial, assigned `readSkill`/`readReferenceDoc` success/denial where assigned, sanitized markdown, trace refs, no deterministic normal-runtime fallback.

### `USERADMIN_START_ACCESS_REVIEW_TASK`

- Purpose: start a durable access-review/admin-risk investigation when implemented.
- Actors/callers: authorized admins with access-review authority; workflow/policy callers where explicitly allowed.
- Inputs: scope, review reason, target users/roles, risk parameters, idempotency key, correlation id.
- Outputs: task id, accepted/rejected/blocked status, initial progress surface ref, trace ref.
- Data access: scoped member/role/capability evidence, policy references, prior trace/audit refs as allowed.
- Side effects: create/start AutonomousAgent task, emit lifecycle trace, notification/progress subscription where supported.
- Idempotency: duplicate key returns existing task.
- Policy/approval: high-impact review scopes may require approval; task cannot expand authority beyond caller policy.
- Exposure channels: access-review surface action, HTTP/API.
- Akka substrate: Akka `AutonomousAgent` facade plus task lifecycle records/notifications.
- Tests: start success, duplicate start, forbidden scope, missing provider/tool boundary fail-closed, lifecycle trace.

### `USERADMIN_VIEW_ACCESS_REVIEW_TASK`

- Purpose: read progress for a durable access-review task.
- Actors/callers: authorized admins; User Admin Agent read-only tool when boundary grants it.
- Inputs: task id, selected context, correlation id.
- Outputs: progress snapshot, status, partial findings if safe, next notification/action hints, trace refs.
- Data access: AutonomousAgent task state/snapshots/notifications, scoped evidence refs.
- Side effects: protected read trace only.
- Idempotency: read-only.
- Policy/approval: redact findings outside caller authority.
- Exposure channels: access-review surface, HTTP/API, read-only agent tool.
- Tests: progress success, forbidden task/context, redaction, failed/blocked status, trace.

### `USERADMIN_CANCEL_ACCESS_REVIEW_TASK`

- Purpose: cancel or terminate a running access-review task.
- Actors/callers: authorized admins with task lifecycle authority.
- Inputs: task id, reason, idempotency key, correlation id.
- Outputs: canceled/already-terminal result, trace ref.
- Data access: task lifecycle state.
- Side effects: cancel/terminate AutonomousAgent task, emit lifecycle trace and notification.
- Idempotency: canceling terminal task returns no-op/already-terminal result.
- Policy/approval: cannot cancel tasks outside authority; support/SaaS-owner cancellation requires separate authority.
- Exposure channels: access-review surface action, HTTP/API.
- Tests: success, already terminal no-op, forbidden context, audit/lifecycle trace.

### `USERADMIN_VIEW_ACCESS_REVIEW_RESULT`

- Purpose: view final access-review findings and recommended next actions.
- Actors/callers: authorized admins; User Admin Agent read-only tool when boundary grants it.
- Inputs: task id/result id, selected context, correlation id.
- Outputs: result DTO with findings, evidence refs, recommended actions, denials/redactions, trace refs.
- Data access: AutonomousAgent result, evidence snapshots, trace/audit refs.
- Side effects: protected read trace only.
- Idempotency: read-only.
- Policy/approval: findings are advisory unless separate capabilities apply changes; redact sensitive evidence.
- Exposure channels: result surface, HTTP/API, read-only agent tool.
- Tests: result success, pending/not-ready, forbidden context, redacted evidence, trace.

### `USERADMIN_VIEW_TRACE_REFERENCE`

- Purpose: expose a scoped trace/correlation reference for a User Admin action or denial and prepare for later Audit/Trace deep-linking.
- Actors/callers: authorized admins; support/SaaS-owner only with separate authority.
- Inputs: trace id/correlation id, selected context, requested detail level, correlation id.
- Outputs: trace summary or deep-link target with redacted fields and safe denial.
- Data access: AdminAuditEvent, AgentWorkTrace, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, capability/tool trace refs relevant to this workstream.
- Side effects: protected trace read audit.
- Idempotency: read-only.
- Policy/approval: redaction based on AuthContext; provider secrets and prompt/reference bodies hidden unless separately authorized.
- Exposure channels: trace link action, HTTP/API, optional delegated Audit/Trace deep link when available.
- Tests: success, forbidden cross-tenant trace, redaction, missing trace safe error, read audit.

## Workstream validation matrix

| Validation area | Required evidence in implementation tasks |
|---|---|
| AuthContext | selected context required, disabled/missing membership denied, tenant/customer filters tested |
| Role/capability | insufficient role/scope denies every command and protected query; grant delegation checked |
| Idempotency | invitation send/resend/revoke, membership status, role change, and access-review task start/cancel have duplicate/no-op tests |
| Audit/work trace | successful consequential actions, denials, reads of protected evidence, model/tool calls, provider failures, and AutonomousAgent lifecycle events trace |
| Provider fail-closed | User Admin Agent turn and access-review task model use fail closed when provider/model/tool-boundary config is missing |
| UI/API | surfaces render loading, ready, forbidden, validation, blocked-provider/runtime, empty, success, and trace-link states |
| Secret boundary | frontend/API responses, traces, logs, and DTOs do not expose provider secrets or unredacted governed documents by default |
| Agent tools | read/preview tools enforce `ToolPermissionBoundary`, AuthContext, assignment, trace, and denial semantics |
| AutonomousAgent | only access-review/admin-risk durable work uses task lifecycle; no normal composer turn is replaced by AutonomousAgent by default |

## Implementation handoff notes

- Backend/runtime task should start with deterministic capability facades and tests before agent tool exposure.
- Request/response agent completion requires the concrete Akka Agent runtime path; direct service/provider calls do not satisfy `USERADMIN_AGENT_TURN`.
- Access-review AutonomousAgent work may be deferred or blocked if the durable task lifecycle is not implemented in the selected backend task, but surfaces must not fake task progress.
- Frontend work must map each action to these capability ids and render backend denials rather than duplicating authorization logic in the browser.
