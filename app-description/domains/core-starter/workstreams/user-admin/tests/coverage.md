# Tests: User Admin

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open User Admin, then `surface-user-admin-dashboard` renders scoped data, safe attention queues, trace refs, and only authorized actions.
- Given dashboard queue filters, when a user opens a queue, then `surface-user-admin-users`, `surface-user-admin-user-detail`, `surface-user-admin-invitation-detail`, or `surface-user-admin-access-review-task` renders with backend-shaped filters and no cross-scope leakage.
- Given an allowed invitation action, when submitted with valid input and idempotency/correlation data, then the invitation lifecycle returns updated structured surfaces, outbox/Resend boundary state, and audit/work traces.
- Given an allowed membership/status/role/support-access action, when submitted, then backend policy produces success, no-op, approval-required decision card, validation, denial, stale/conflict, or failure with trace refs.
- Given a User Admin agent guidance request, when provider/model/security configuration is valid, then the governed runtime uses scoped evidence/tool boundaries and returns structured guidance; when config is missing, it fails closed with a blocked system message and trace.
- Given an access-review task, when started/read/cancelled/accepted/rejected, then task progress/result/review surfaces render and no worker output directly mutates access.

## Security and negative

- Disabled users, inactive memberships, missing selected context, missing capability, Customer Admin tenant-level actions, SaaS Owner without support grant, cross-tenant/customer requests, role escalation, last-admin loss, self-disable, support-access policy violations, identity relink denials, unsupported bulk actions, and hidden/not-found targets are denied without protected-data leakage.
- Agent/tool calls cannot exceed governed tool boundary, manifest assignment, skill/reference status, redaction/token limits, model policy, or approval policy.
- Browser payloads never expose raw JWT/session data, WorkOS/provider internals, invitation tokens/token hashes, Resend/provider secrets, full email bodies, hidden authority state, raw model/provider config, fixture data, or cross-scope counts.

## Surface rendering

- Dashboard, users list, user detail, invitation detail, role-change preview, access-review task, decision-card, markdown-response, audit-link, and system-message variants render loading, empty, ready, submitting, validation-error, forbidden, not_found_or_redacted, conflict, stale/reconnect, partial-data, provider/model/outbox fail-closed, no-op, approval-required, and failure states.
- Tenant Admin, Customer Admin, SaaS Owner support, Auditor, disabled-user, forbidden, redacted, empty, stale, and error variants show correct scope labels, queues, rows, redactions, actions, and denials.
- Responsive table-to-card fallback preserves authority, role, invitation, support-access, access-review, risk, trace, and decision-card affordances.

## Idempotency and observability

- Repeated side-effecting actions do not duplicate effects.
- Denials, approval-required outcomes, no-ops, provider/model/outbox blocked states, prompt assembly, skill/reference loads, tool calls, data access, decision cards, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.
