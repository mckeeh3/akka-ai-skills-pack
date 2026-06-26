# Traces: User Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

User Admin emits durable trace/audit evidence for protected reads, direct API/deep-link denials, validation failures, no-op outcomes, SaaS Owner Admin invite/manage events, Organization lifecycle events, Organization Admin bootstrap/manage events, Customer lifecycle events, Customer Admin bootstrap/manage events, invitation delivery/acceptance lifecycle events, membership/role/status changes, support-access lifecycle, identity relink review, access-review worker lifecycle/result decisions, provider/outbox/model blocked states, prompt assembly, skill/reference loads, tool calls, data access, decision cards, and trace-open actions.

Every traceable operation records the current worker-model chain where applicable: worker id/type, execution harness, actor adapter, governed tool id, capability id, selected `AuthContext` or service authority basis, Akka/API/UI realization path, result surface/event, and adapter-specific trace source. User Admin worker ids include `user-admin.saas-owner-admin-human`, `user-admin.organization-admin-human`, `user-admin.customer-admin-human`, `user-admin.functional-agent-worker`, `user-admin.access-review-agent-worker`, `user-admin.invitation-onboarding-system-worker`, and `user-admin.admin-audit-projection-system-worker`.

Required trace types include `admin-audit-event`, `workstream-log-trace`, `agent-work-trace`, `prompt-assembly-trace`, `skill-load-trace`, `reference-load-trace`, `data-access-trace`, `policy-decision-trace`, and access-review worker task traces.

## Minimum fields

Trace records include:

- trace id and correlation/request/idempotency id;
- worker id, worker type, execution harness, actor adapter, governed tool id, and capability id;
- actor account id and selected `AuthContext`;
- tenant/customer scope and safe resource id/type where allowed;
- role/capability basis, support-access basis where applicable, and policy decision;
- capability/tool/action id and surface/workstream item id;
- outcome: allowed, denied, validation_error, no_op, queued, completed, failed, provider_blocked, model_blocked, outbox_blocked, approval_required;
- denial, validation, stale, conflict, no-op, or readiness reason code;
- deterministic service or agent/worker component name;
- prompt, skill, reference, model, tool-boundary ids for model-backed turns;
- redaction state, evidence refs, and browser-safe summaries.

## Production runtime trace obligations

- Invitation delivery traces record invitation lifecycle command, outbox message id, delivery attempt number, retry/no-op/revoke relationship, provider-safe status, fail-closed reason, selected `AuthContext`, actor, capability/tool/action id, idempotency/correlation id, and redacted error summary. Raw invitation tokens, Resend keys, webhook secrets, and full email bodies are excluded.
- Invitation acceptance traces record token validation outcome without token value/hash exposure, WorkOS/AuthKit account correlation category, invited email match/mismatch category, target scope type, requested role, account/profile link or creation result, membership creation/activation/no-op result, accepted timestamp, `/api/me` selected-context refresh outcome, denial/recovery reason, idempotency/correlation id, and redaction state. Raw invitation tokens, token hashes, JWT/session values, WorkOS raw payloads, provider ids unless policy-safe, hidden tenant/customer ids, and full email bodies are excluded.
- Identity exception traces record request/review/approval/denial/recovery lifecycle, provider-boundary redaction state, policy decision, reviewer reason, recovery result, stale/replay/no-op outcome, selected `AuthContext`, and safe evidence refs. Raw WorkOS/JWT/provider payloads are excluded.
- Model-backed access-review traces record AgentDefinition/profile, ModelConfigRef, model policy decision, prompt assembly, skill/reference load attempts, ToolPermissionBoundary allow/deny decisions, scoped evidence reads, recommendation/result summary, blocker/fail-closed reason, human accept/reject decision, and no-direct-mutation guarantee. Raw prompts, model secrets, provider internals, hidden evidence, and denied tool/loader contents are excluded from browser-safe summaries.

## Redaction and investigation

Trace links in User Admin surfaces are browser-safe references, not proof of read authority. Opening trace detail reauthorizes through Audit/Trace capability. Hidden SaaS Owner Admin users, hidden Organization Admin users, hidden Customer Admin users, hidden users, hidden tenants/customers, sibling-customer facts, raw invitation tokens, WorkOS/provider internals, Resend/provider secrets, model secrets, full email bodies, tenant application data, and unredacted audit evidence must not appear in User Admin browser payloads.


## `human_chat_tool_plan` trace evidence

User Admin must emit durable work/audit trace facts for the `human_chat_tool_plan` adapter in addition to existing surface-action traces. Required event types are `human_chat_tool_plan.proposed`, `human_chat_tool_plan.confirmed`, `human_chat_tool_plan.step_started`, `human_chat_tool_plan.step_completed`, `human_chat_tool_plan.step_failed`, `human_chat_tool_plan.step_skipped`, `human_chat_tool_plan.denied`, and `human_chat_tool_plan.provider_blocked`.

Minimum fields: trace/work trace id, correlation id, causation/parent event id, selected `AuthContext`, tenant/customer scope where applicable, functional agent `user-admin-agent`, requestedBy, confirmedBy for execution, action ids `action-submit-organization-create`; `action-submit-organization-admin-invitation`, governed tool ids `manage-organizations`; `manage-organization-admins`, capability ids `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite`, input schema ref, plan id, plan snapshot id, step id/sequence/dependencies, idempotency key or redacted hash, authorization decision and basis summary, policy/approval refs, prompt/skill/reference/model/tool-boundary refs for proposal generation, result surface ids `surface-user-admin-organization-detail`; `surface-user-admin-invitation-detail`, status, safe error code, redaction classification, and browser-safe input/output summaries.

Trace summaries must distinguish direct surface actions from `human_chat_tool_plan`, preserve no-mutation proposal evidence, record confirmation and per-step transaction outcomes, and omit raw provider secrets, JWTs, invitation tokens, raw email bodies, raw prompts/model payloads, hidden tenant/customer ids, raw tool payloads, and unredacted evidence from browser-visible views.
