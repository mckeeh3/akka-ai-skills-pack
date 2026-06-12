# Production Runtime Contract: User Admin Hardening

## Scope

This contract makes the production behavior for User Admin invitation delivery, identity exception recovery, and model-backed access-review automation explicit before implementation. It does not change runtime code.

Workstream: User Admin / `agent-user-admin` runtime alias for `user-admin-agent`.

Selected context: backend-owned `AuthContext` with App/Tenant/Customer scope. Frontend visibility, prompts, and hidden buttons never grant authority.

## Common production invariants

- All commands, reads, browser actions, agent turns, internal tasks, skill/reference loads, and trace opens are reauthorized server-side against selected `AuthContext`, active account, membership, role/capability grants, tenant/customer ownership, support-access policy, and approval policy.
- Missing provider, outbox, model, tool-boundary, governed prompt/skill/reference, or security configuration is `fail-closed`: return a typed `surface-user-admin-system-message` or task blocker, emit audit/work trace evidence, and do not produce fixture, deterministic, or model-less normal success.
- Browser-safe payloads never expose Resend keys, webhook secrets, raw invitation tokens/token hashes, WorkOS/JWT/provider payloads, raw model/provider config, full prompts, full email bodies, hidden counts, or cross-scope facts.
- Consequential side effects require idempotency key, correlation id, explicit human confirmation or approval where policy requires, safe no-op/replay semantics, and AdminAuditEvent/work-trace evidence.
- Default local/test validation may use captured adapters and test model providers only as tests. Production runtime behavior must be provider/model-backed when configured and must fail closed when not configured.

## Invitation delivery contract

### Behavior

- `user_admin.invite_user` and `user_admin.resend_invitation` enqueue invitation email work through the supported outbox/Resend delivery path; command handlers do not call the provider directly from browser or agent-controlled text.
- Required production settings include backend-only Resend API key and sender configuration. Missing settings block external delivery and return `provider-fail-closed` or `outbox-fail-closed` system-message/status, not a fake sent state.
- Invitation create/resend validates target scope, normalized email, requested role(s), duplicate/open invite state, resend eligibility, revoke state, expiry, idempotency, and last known outbox status.
- Revoke transitions prevent later delivery attempts for obsolete invitation work and render safe no-op when replayed.
- Delivery status is shaped as user-safe lifecycle facts: queued, captured-local, sent, retrying, failed, blocked, revoked, expired, accepted, stale, or no-op. Provider ids and error bodies stay in role-gated diagnostics only.

### Surfaces

- `surface-user-admin-invitation-create` shows provider/outbox readiness before submit when safe and returns invitation detail, validation, duplicate/open-invite, provider/outbox blocked, or denial.
- `surface-user-admin-invitation-detail` shows delivery status, retry eligibility, failure summary, acceptance/expiry state, resend/revoke task entries, redaction note, trace refs, and correlation id.
- `surface-user-admin-invitation-resend-confirmation` and `surface-user-admin-invitation-revoke-confirmation` show consequence/recovery copy, require reason where policy requires it, and return refreshed invitation detail or system-message.
- Dashboard attention includes invitation delivery failure/stale/pending/expired counts only for visible scopes.

### Tests

- Configured captured provider success queues/sends through outbox and records delivery status, attempts, provider-safe result, audit, and traces.
- Missing Resend config returns fail-closed status and no fake success.
- Resend failure/retry/replay is idempotent and user-safe.
- Revoked/expired/accepted invitations do not send obsolete emails.
- Cross-scope target, disabled actor, missing capability, duplicate/open invite, and hidden target produce safe denials.
- Browser/API/frontend checks prove no token, secret, full email body, or raw provider payload is exposed.

## Identity exception recovery contract

### Behavior

- Identity exceptions are durable workflow/state objects, not read-only placeholders. They cover provider-account mismatch, stale link, relink request, disabled identity state, and approved recovery completion.
- Lifecycle states: `reported`, `needs_review`, `approved_for_recovery`, `denied`, `recovery_in_progress`, `completed`, `failed`, `cancelled`, and `stale`.
- Valid commands: request/reopen exception, read review, approve recovery, deny recovery, start/complete recovery, cancel when allowed, and record no-op/replay.
- Approval requires authorized human review, reason, risk summary, evidence refs, idempotency, correlation id, provider-boundary redaction, and audit/work trace evidence.
- Recovery may relink/reset only through deterministic backend capability `user_admin.identity_relink.review` and provider-safe service boundaries. The agent may explain or draft, but cannot relink identity by prompt.
- Cross-scope, hidden target, raw WorkOS/JWT/provider payload request, missing capability, denied policy, stale version, and replayed commands return safe system-message or workflow status.

### Surfaces

- `surface-user-admin-identity-exception-review` shows exception lifecycle/status, scoped user summary, provider-safe mismatch summary, risk/confidence, evidence refs, approval/deny/recovery actions, blocked states, trace refs, and redaction.
- User detail and dashboard route visible identity exception attention into the identity exception review/status surface.
- Approved recovery routes to workflow/status or refreshed user detail; denied recovery records review outcome without mutating identity.

### Tests

- Request, review, approve, deny, recovery start, completion, failed recovery, and cancellation states are covered.
- Repeated approve/deny/complete commands are idempotent or no-op as specified.
- Hidden/cross-scope targets and provider-boundary raw-payload requests are denied without existence leakage.
- Audit/work traces link actor, selected `AuthContext`, reason, policy decision, redaction state, and correlation id.
- Browser-visible payloads contain no raw WorkOS ids unless explicitly policy-safe, no JWT/session payloads, and no provider secrets.

## Model-backed access-review contract

### Behavior

- Production access-review automation invokes a concrete governed Akka Agent path when model configuration is active. Deterministic/model-less worker output cannot count as normal production automation.
- Start/read/cancel/accept/reject access-review capabilities preserve durable task lifecycle and human authority. The model may collect evidence and recommend; it cannot mutate membership, role, support access, invitation, identity, or policy state.
- Runtime requires active AgentDefinition/profile, approved `ModelConfigRef`, model policy, prompt/skill/reference manifests where used, authorized `readSkill`/`readReferenceDoc` loader access, `ToolPermissionBoundary`, selected `AuthContext`, and scoped evidence tools.
- Missing model/provider/profile/boundary/governed-doc config returns `model-fail-closed` or `tool-boundary-denied` blocker with trace evidence and actionable recovery.
- Tool/data use is scoped to the selected tenant/customer and allowed evidence classes. Denied evidence access must not leak hidden targets or privileged audit facts.
- Results include recommendation, confidence, risk, impact, evidence summary, alternatives, policy notes, trace links, and explicit human accept/reject actions. Acceptance records review outcome only; follow-up access mutations route through deterministic User Admin surfaces.

### Surfaces

- `surface-user-admin-access-review-task` shows task lifecycle, progress, model/provider/tool-boundary readiness, blocker states, result summary, human accept/reject, trace refs, and no-direct-mutation flag.
- Dashboard attention includes access-review running/result/blocker counts for visible scopes.
- `surface-user-admin-system-message` carries model-backed blocked states, denied evidence, and recovery guidance without exposing prompts, model provider internals, or hidden tool names.

### Tests

- Configured test-model runtime invokes the governed Akka Agent path and records model, prompt, tool, data, policy, and result traces.
- Missing model/provider/profile/boundary/governed-doc config fails closed and creates a blocker/status surface.
- Tool-boundary denial and cross-scope evidence attempts are safe and traced.
- Agent result cannot mutate access directly; human accept/reject is explicit and auditable.
- Browser/frontend checks prove no raw prompts, model secrets, provider internals, hidden evidence, or unauthorized trace detail is exposed.

## Implementation handoff

The next tasks may implement backend and frontend changes against this contract without inventing provider/model/workflow semantics. If implementation discovers a missing command, state, action id, or authorization rule, block or append a follow-up task instead of silently widening scope.
