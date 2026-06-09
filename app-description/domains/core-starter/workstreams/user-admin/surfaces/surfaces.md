# Surfaces: User Admin

## Surface architecture

User Admin is an AI-first access operations workstream. Its surfaces are structured, backend-backed work units owned by `user-admin-agent`, not a generic CRUD admin page set. They help authorized admins answer:

1. **What access-administration work needs attention now?**
2. **Which scoped users, memberships, invitations, roles, support grants, identity exceptions, and review tasks are involved?**
3. **What safe action is available, denied, stale, no-op, approval-required, or blocked by provider/model/outbox readiness?**
4. **Which audit/work traces and evidence explain the state, recommendation, denial, or mutation?**

All surfaces use the canonical AI-first workstream shell, structured surface envelope, governed browser-tool actions, selected `AuthContext`, tenant/customer redaction, trace links, and the skills-pack authoritative web UI style guide. Routes and links only reopen functional agents, filtered surfaces, source evidence, or typed result surfaces; they do not define product meaning or bypass backend authorization.

## Surface inventory

| Surface id | Type | Contract | Primary purpose | Status |
|---|---|---|---|---|
| `surface-user-admin-dashboard` | `dashboard` | `user_admin.dashboard.v1` | Attention-first User Admin command center for directory, invitation, role, support, review, provider, and audit health. | Rebuilt from archive |
| `surface-user-admin-member-directory` | `list-search` | `user_admin.member_directory.v1` | Scoped searchable member/user/invitation/support/review table and responsive card list. | Rebuilt from archive |
| `surface-user-admin-invitation-panel` | `detail-edit` / `workflow-status` | `user_admin.invitation_panel.v1` | Invite, resend, revoke, acceptance, expiry, delivery/outbox visibility, and recovery. | Rebuilt from archive |
| `surface-user-admin-user-account` | `detail-card-action-panel` | `user_admin.user_account.v1` | Scoped account, membership, invitation, support-access, access-review, and audit detail. | Rebuilt from archive |
| `surface-user-admin-role-change-preview` | `decision-card` / `diff` | `user_admin.role_change_preview.v1` | Capability delta, affected workstreams, last-admin impact, policy gate, and approval preview before role mutation. | Rebuilt from archive |
| `surface-user-admin-access-review-task` | `workflow-status` / `outcome-panel` | `user_admin.access_review_task.v1` | Durable autonomous access-review task progress, result, blockers, and human accept/reject review. | Rebuilt from archive |
| `surface-user-admin-system-message` | `system-message` | `user_admin.system_message.v1` | Safe denial, validation, provider/outbox/model blocked, stale, conflict, and no-op recovery. | Rebuilt from archive |

## User Admin dashboard surface

### Intent

`surface-user-admin-dashboard` is titled **User Admin Dashboard** and is the default User Admin surface. It orients authorized admins to access-administration attention in the selected tenant/customer context, then provides governed entry points into member directory, invitations, support access, access review, risky-change decision cards, and scoped audit evidence.

This surface must not render hidden counts, use client-side authorization as a shortcut, or become a generic admin KPI grid. Each card, queue row, action, and trace link is a structured surface edge backed by server-side authorization.

### Contract

- Surface id: `surface-user-admin-dashboard`.
- Surface type: `dashboard`.
- Surface contract: `user_admin.dashboard.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Required context: authenticated active account plus selected tenant/customer membership and User Admin read authority.
- Reusable placements: My Account may show personal User Admin attention counters; Audit/Trace may link to dashboard-origin traces.

### User experience model

1. **Orient** — show selected scope label/type, safe tenant/customer labels, actor authority summary, redaction marker, support-access state if visible, and trace/correlation affordances.
2. **Prioritize** — render attention cards for failed invitation delivery, stale/expired invitations, users needing review, dormant/admin risk, support-access expiry, last-admin risk, access-review results needing human review, recent denied admin actions, and provider/outbox/model blockers.
3. **Summarize health** — show active users, pending invitations, expired invitations, suspended/disabled users, role/capability coverage, access-review status, recent consequential activity, and provider/outbox readiness where safe.
4. **Act safely** — expose authorized next actions: invite user, open filtered queue, open member directory, open failed invitations, start access review, open support-access queue, open admin audit evidence, or ask `user-admin-agent` for guidance.
5. **Recover** — first-run, empty, forbidden, stale, provider-fail-closed, disabled-actor, no-membership, and partial-data states include safe recovery without revealing hidden data.

### Frontend-safe payload

- `surfaceContract`, `accountContext`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `authorityBasis`, `redaction`, `capabilityIds`, `traceRefs`, `correlationId`.
- `summaryCards[]`: active users, pending/expired invitations, disabled/suspended users, support-access expiry, access-review status, recent denied actions, and readiness summaries marked safe by backend.
- `attentionQueues[]`: stable queue id, label, severity, count, status text, source capability/tool, target surface id/filter, trace refs, redaction state, and open action id.
- `authorizedActions[]`: action id, label, governed capability/tool id, required idempotency/correlation behavior, result surface, approval/decision-card requirement, and denial hint when visible.
- `recentActivity[]`: browser-safe admin audit excerpts with trace ids and redaction summaries.

Forbidden payload/content: hidden workstream/source names, hidden user identities/counts, cross-tenant/customer facts, raw WorkOS ids unless policy-safe, raw JWT/session data, invitation tokens/token hashes, Resend/provider secrets, full email bodies, raw model/provider config, or fixture/mock data as normal runtime.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh dashboard | `user_admin.view_overview` / `search-user-directory` | Reload backend-owned overview projection. |
| Open member directory / filtered queue | `user_admin.list_members` / `search-user-directory` | Render `surface-user-admin-member-directory` with backend-shaped filter context. |
| Create invitation | `user_admin.invite_user` / `create-or-resend-invitation` | Render invitation panel/form or decision/system message when risky/denied. |
| Open failed/stale/expired invitations | `user_admin.acceptance_status.read` / `search-user-directory` | Render invitation panel or filtered directory. |
| Start access review | `user_admin.access_review.start` / `run-access-review` | Render access-review task progress or blocked/denied message. |
| Open access-review item/result | `user_admin.access_review.read` | Render access-review task or decision card. |
| Open support-access queue | `user_admin.support_access.read` / `grant-or-revoke-support-access` read side | Render filtered directory/detail or denial. |
| Open admin audit evidence | `admin.audit.read` / Audit Trace capability | Render authorized Audit/Trace surface or safe redacted message. |
| Ask User Admin agent | `user_admin.ask_agent` | Invoke governed agent runtime or provider/model blocked system message. |

## Member directory surface

### Intent

`surface-user-admin-member-directory` is a scoped searchable table/card surface for members, accounts, invitations, support-access grants, and access-review flags. It is not an unbounded user export or client-side filtered fixture list.

### Contract

- Surface id: `surface-user-admin-member-directory`.
- Surface type: `list-search`.
- Surface contract: `user_admin.member_directory.v1`.
- Owning functional agent: `user-admin-agent`.
- Required context: selected `AuthContext` plus directory-read capability.

### Frontend-safe payload

Includes normalized filter state, search text, account status, membership status, role, invitation status, support-access marker, review item type, risk level, dashboard-origin queue id, page token, page size, sort, stable query label, `rows[]`, redaction, trace refs, and correlation id.

Rows may include account summary, display name/email when authorized, scoped membership summary, role/capability summary, invitation/support/review badges, last activity/change evidence, risk flags, row trace id, action availability, denial categories, idempotency-key requirements, and decision-card requirements.

Forbidden content includes out-of-scope identities, hidden memberships, raw provider ids/secrets, raw invitation tokens, full email bodies, and total counts not marked safe.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Search/list users | `user_admin.list_members` / `search-user-directory` | Refresh directory with scoped rows. |
| Open user account | `user_admin.read_user_account` / `search-user-directory` | Render `surface-user-admin-user-account`. |
| Create/resend/revoke invitation | `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation` / `create-or-resend-invitation` | Render invitation panel update, no-op, validation, denial, or trace-linked system message. |
| Disable/reactivate membership/account | `user_admin.update_member_status` / `change-membership-role-or-status` | Render detail refresh, no-op, last-admin/self-disable denial, or decision card. |
| Preview/change role | `user_admin.preview_role_change`, `user_admin.change_member_roles` / `change-membership-role-or-status` | Render role-change preview/decision card before mutation. |
| Grant/revoke/extend support access | `user_admin.support_access.*` / `grant-or-revoke-support-access` | Render detail refresh, decision card, or denial. |
| Read/resolve access-review item | `user_admin.access_review.read`, `user_admin.access_review.accept_result`, `user_admin.access_review.reject_result` | Render access-review task or decision/system message. |
| Open audit evidence | `admin.audit.read` | Render authorized Audit/Trace surface or redacted denial. |

### States

Loading preserves submitted filters and disables mutations. Empty distinguishes no users in scope, no search matches, no queue items, and redacted result set. Forbidden hides result counts and identities. Stale disables mutations until refreshed. Responsive table-to-card fallback preserves authority, role, invitation, support, review, risk, trace, and decision-card affordances.

## Invitation panel surface

### Intent

`surface-user-admin-invitation-panel` governs invite/resend/revoke and invitation delivery/acceptance visibility. It explains expiry, duplicates, open-invite state, outbox/Resend readiness, delivery failure, acceptance status, and recovery without exposing raw tokens or provider secrets.

### Contract

- Surface id: `surface-user-admin-invitation-panel`.
- Surface type: `detail-edit` / `workflow-status`.
- Surface contract: `user_admin.invitation_panel.v1`.
- Owning functional agent: `user-admin-agent`.

### Payload and actions

Frontend-safe payload includes invite id, normalized email, requested role, target scope, status, expiry, accepted timestamp, resend/revoke eligibility, outbox delivery state, provider failure summary, idempotency/correlation refs, validation messages, redaction, and trace refs.

Actions map to `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation`, `user_admin.acceptance_status.read`, and trace-open requests. Results render updated panel, workflow status, no-op, validation-error, duplicate/open-invite conflict, provider/outbox blocked, forbidden, or safe system message.

Forbidden content includes raw invitation token/token hash, full email body unless explicitly safe preview, Resend secrets, hidden invitees, cross-scope delivery facts, and email-only authorization claims.

## User account detail surface

### Intent

`surface-user-admin-user-account` provides scoped account, membership, invitation, support-access, access-review, identity-link, and admin-audit detail for a target user. It is not the user's My Account profile and cannot expose self-service-only/private settings unless explicit admin policy allows.

### Contract

- Surface id: `surface-user-admin-user-account`.
- Surface type: `detail-card-action-panel`.
- Surface contract: `user_admin.user_account.v1`.
- Owning functional agent: `user-admin-agent`.

### Frontend-safe payload

Includes selected `AuthContext`, target account id when safe, browser-safe account summary, identity link state, account status, scoped memberships, role/capability summary, support-access grants, invitation history, access-review items, agent recommendations, recent admin audit excerpts, action availability, denial categories, idempotency requirements, decision-card requirements, trace refs, and correlation id.

Forbidden content includes hidden memberships, out-of-scope audit/evidence, raw provider internals, private subject ids unless policy-safe, raw invitation tokens, provider secrets, full email bodies, and target identity details when backend marks them hidden.

### Actions

Actions include return to filtered list, patch admin-visible profile where policy allows, disable/reactivate account, request/complete identity relink, add/suspend/reactivate/remove membership, preview/replace/remove roles, resend/revoke invitation, grant/revoke/extend support access, read/resolve access-review item, and open audit evidence. Every action recomputes backend authorization and returns refreshed detail, role-change preview, decision card, no-op, validation, denial, stale/conflict, or failure system message.

## Role change preview surface

### Intent

`surface-user-admin-role-change-preview` shows capability delta, affected workstreams, last-admin impact, role escalation risk, support/access-review interactions, policy gate, evidence, alternatives, and required approver scope before role mutation.

### Contract

- Surface id: `surface-user-admin-role-change-preview`.
- Surface type: `decision-card` / `diff`.
- Surface contract: `user_admin.role_change_preview.v1`.
- Owning functional agent: `user-admin-agent`.

Actions map to `user_admin.preview_role_change`, `user_admin.change_member_roles`, approval/open-governance requests where introduced, and trace-open requests. Commit requires current preview version, idempotency/correlation key, backend authorization, last-admin protection, and any approval decision. Results render changed detail, approval-required decision card, no-op, validation/denial, conflict/stale, or failure.

## Access review task surface

### Intent

`surface-user-admin-access-review-task` makes durable access-review investigation visible and reviewable. It can use an internal/autonomous worker to collect evidence and draft recommendations, but worker output cannot directly mutate memberships, roles, support access, invitations, or policy.

### Contract

- Surface id: `surface-user-admin-access-review-task`.
- Surface type: `workflow-status` / `outcome-panel`.
- Surface contract: `user_admin.access_review_task.v1`.
- Owning functional agent: `user-admin-agent`.

Payload includes task id, autonomousAgentTaskId when present, initiating capability, selected AuthContext, tenant/customer scope, status, phase, progress events, blockers, provider/model readiness state, evidence refs, scoped member/role/invitation/admin-change summaries, recommendations, risk/confidence, result review state, trace ids, redaction, and correlation id.

Actions map to `user_admin.access_review.start/read/cancel/accept_result/reject_result`. Accept/reject records human review of the result only; actual access changes must route through deterministic User Admin capabilities and decision cards. Missing provider/model configuration renders a blocked system message with trace and no fake success.

## System-message requirements

`surface-user-admin-system-message` handles forbidden access, missing context, disabled actor, inactive membership, validation failure, duplicate/open invitation, last-admin protection, self-disable denial, role escalation denial, unsupported bulk action, no-op mutation, stale/outbox/provider/model failure, tenant isolation denial, missing support grant, hidden/not-found target, missing tool-boundary grant, denied skill/reference load, and recovery guidance.

Payload includes safe reason code, severity, user-safe title/message, selected scope label if safe, recovery steps, required capability/tool/provider readiness hints, trace refs, correlation id, redaction note, and `noFakeSuccess` for provider/model blocked states.

## Scope-aware variants

- **SaaS Owner Admin** may see SaaS-owner users and platform-safe tenant metadata. Tenant/customer user data requires selected tenant-created support-access context and otherwise returns forbidden/redacted metadata.
- **Tenant Admin** may see tenant employees, customer admins/users under the tenant, tenant-created support access, invitations, role/capability data, access reviews, and scoped audit queues.
- **Customer Admin** may see selected customer users, customer invitations, customer roles, customer access-review items, and customer audit excerpts only. Tenant employee rows, tenant-level roles/actions, support-access administration, and tenant-wide audit queues are forbidden.
- **Auditor** may read scoped evidence and traces where permitted and cannot mutate access.

## Common action rules

Every consequential browser action has a stable action id, maps to a governed backend capability/tool, carries correlation and idempotency behavior where needed, recomputes authorization server-side, emits audit/work traces for allow/deny/no-op/failure, and returns a typed result surface, decision card, workflow status, outcome panel, markdown response, or safe system message. Frontend visibility and disabled state are UX hints only.

## Common states

User Admin surfaces define loading, empty, ready, submitting, success, validation-error, forbidden, not_found_or_redacted, conflict, stale/reconnect, partial-data, provider-fail-closed, model-fail-closed, outbox-fail-closed, no-op, approval-required, and failure states. All states preserve selected tenant/customer scoping, browser-safe redaction, trace/correlation links, and recovery guidance.
