# Traces: User Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

User Admin emits durable trace/audit evidence for protected reads, direct API/deep-link denials, validation failures, no-op outcomes, SaaS Owner Admin invite/manage events, Organization lifecycle events, Organization Admin bootstrap/manage events, Customer lifecycle events, Customer Admin bootstrap/manage events, invitation lifecycle events, membership/role/status changes, support-access lifecycle, identity relink review, access-review worker lifecycle/result decisions, provider/outbox/model blocked states, prompt assembly, skill/reference loads, tool calls, data access, decision cards, and trace-open actions.

Required trace types include `admin-audit-event`, `workstream-log-trace`, `agent-work-trace`, `prompt-assembly-trace`, `skill-load-trace`, `reference-load-trace`, `data-access-trace`, `policy-decision-trace`, and access-review worker task traces.

## Minimum fields

Trace records include:

- trace id and correlation/request/idempotency id;
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
- Identity exception traces record request/review/approval/denial/recovery lifecycle, provider-boundary redaction state, policy decision, reviewer reason, recovery result, stale/replay/no-op outcome, selected `AuthContext`, and safe evidence refs. Raw WorkOS/JWT/provider payloads are excluded.
- Model-backed access-review traces record AgentDefinition/profile, ModelConfigRef, model policy decision, prompt assembly, skill/reference load attempts, ToolPermissionBoundary allow/deny decisions, scoped evidence reads, recommendation/result summary, blocker/fail-closed reason, human accept/reject decision, and no-direct-mutation guarantee. Raw prompts, model secrets, provider internals, hidden evidence, and denied tool/loader contents are excluded from browser-safe summaries.

## Redaction and investigation

Trace links in User Admin surfaces are browser-safe references, not proof of read authority. Opening trace detail reauthorizes through Audit/Trace capability. Hidden SaaS Owner Admin users, hidden Organization Admin users, hidden Customer Admin users, hidden users, hidden tenants/customers, sibling-customer facts, raw invitation tokens, WorkOS/provider internals, Resend/provider secrets, model secrets, full email bodies, tenant application data, and unredacted audit evidence must not appear in User Admin browser payloads.
