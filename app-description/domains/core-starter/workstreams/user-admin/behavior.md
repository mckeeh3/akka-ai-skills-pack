# Behavior: User Admin

## Current-state behavior

User Admin administers users, memberships, invitations, roles/capabilities, support access, access reviews, identity review/relink exceptions, and admin audit summaries within authorized tenant/customer scope. The workstream starts from `surface-user-admin-dashboard`, accepts contextual composer requests, returns structured surfaces, and maps every consequential action to capability `user-and-access-administration` and governed backend tools.

## Deterministic responsibilities

Keep these responsibilities out of model-only behavior:

- Authorization: selected `AuthContext`, account status, disabled-user denial, membership, role/capability grants, tenant/customer scope, support-access visibility, and last-admin policy.
- Invitation lifecycle: email normalization, duplicate/open-invite handling, role validation, expiry, resend/revoke eligibility, idempotency, outbox/Resend enqueue, delivery status shaping, and audit.
- Membership lifecycle: directory projection, disable/reactivate validation, self-disable denial, last-admin protection, no-op/idempotent results, and audit.
- Role lifecycle: SMB role/capability matrix, preview delta, policy/approval requirement calculation, mutation idempotency, and last-admin preservation.
- Support access: grant/revoke/extend eligibility, expiry, tenant/customer visibility, purpose capture, approval, and audit.
- Access review: task lifecycle, evidence collection, provider/model readiness gates, progress/result shaping, and human result decision recording.
- Trace shaping: correlation ids, redaction, denial/event/result records, audit-work trace links, and browser-safe evidence summaries.

## Agent behavior

`user-admin-agent` may explain, summarize, draft, recommend, compare alternatives, ask clarifying questions, and prepare proposals/decision-card facts only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, mutate access autonomously, infer hidden evidence, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Access-review worker behavior

Internal/autonomous access-review work may collect scoped evidence, summarize risk, and produce recommendations with progress/result surfaces. Worker output cannot directly mutate memberships, roles, invitations, support access, identity links, or policy. Human acceptance/rejection records the review decision; follow-up changes route through deterministic User Admin capabilities and policy gates.

## Edge cases

Repeated commands are idempotent where side-effecting. Stale data returns stale/reconnect or conflict state. Provider, model, outbox, or security misconfiguration returns actionable fail-closed feedback and traces without fixture/model-less success. Unsupported business-domain requests are routed to extension guidance rather than silently added. Direct API/deep-link attempts are authorized server-side and return safe `not_found_or_redacted`/forbidden system messages where appropriate.
