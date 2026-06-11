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
| `surface-user-admin-users` | `list-search` | `user_admin.users.v1` | Scoped searchable member/user/invitation/support/review table and responsive card list. | Rebuilt from archive |
| `surface-user-admin-organization-admin` | `list-detail-action` | `user_admin.organization_admin.v1` | SaaS Owner Organization administration for platform-safe Organization list/read/create/rename/suspend/reactivate actions backed by internal Tenant authorization. | Planned by SaaS Owner Organization Admin |
| `surface-user-admin-invitation-detail` | `detail-edit` / `workflow-status` | `user_admin.invitation_detail.v1` | Invite, resend, revoke, acceptance, expiry, delivery/outbox visibility, and recovery. | Rebuilt from archive |
| `surface-user-admin-user-detail` | `detail-card-action-panel` | `user_admin.user_detail.v1` | Scoped account, membership, invitation, support-access, access-review, and audit detail. | Rebuilt from archive |
| `surface-user-admin-role-change-preview` | `decision-card` / `diff` | `user_admin.role_change_preview.v1` | Capability delta, affected workstreams, last-admin impact, policy gate, and approval preview before role mutation. | Rebuilt from archive |
| `surface-user-admin-access-review-task` | `workflow-status` / `outcome-panel` | `user_admin.access_review_task.v1` | Durable autonomous access-review task progress, result, blockers, and human accept/reject review. | Rebuilt from archive |
| `surface-user-admin-system-message` | `system-message` | `user_admin.system_message.v1` | Safe denial, validation, provider/outbox/model blocked, stale, conflict, and no-op recovery. | Rebuilt from archive |

## User Admin dashboard surface

### Intent

`surface-user-admin-dashboard` is titled **User Admin Dashboard** and is the default User Admin surface. Its first goal is visibility: show the signed-in admin **what needs my attention now** in the selected app-owner, tenant, or customer context. Its second goal is to show **who I can administer** through backend-authorized scope cards for users, memberships, invitations, roles/capabilities, support access, access reviews, identity exceptions, and admin audit evidence.

The dashboard uses positive, capability-aware language. User-facing dashboard copy lists visible administered populations and available work for the current admin level; it does not present unavailable powers as "cannot manage" lists. Empty states use "nothing to manage" or "0 need attention" language for visible scopes, while authorization denials are reserved for direct attempts that backend policy rejects. If something is outside scope or hidden, the dashboard omits it; if an attempted action is denied, it returns a safe recovery/system message focused on visible next steps. This surface must not render hidden counts, use client-side authorization as a shortcut, or become a generic admin KPI grid. Each card, queue row, action, and trace link is a structured surface edge backed by server-side authorization.

### Contract

- Surface id: `surface-user-admin-dashboard`.
- Surface type: `dashboard`.
- Surface contract: `user_admin.dashboard.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Required context: authenticated active account plus selected tenant/customer membership and User Admin read authority.
- Reusable placements: My Account may show personal User Admin attention counters; Audit/Trace may link to dashboard-origin traces.

### User experience model

1. **Hero: orient me to my active scope** — show selected scope label/type, safe tenant/customer labels, admin level, visible administered populations, redaction marker, support-access state if visible, and trace/correlation affordances. The hero uses positive copy such as "You administer tenant employees and customer admins in this tenant" or "You administer customer users for this customer".
2. **Show what needs my attention** — render visible attention cards and queues with numeric counts for each attention type. `0` means no attention is needed for that visible type. Attention types include invitation delivery failures, pending/stale/expired invitations, users or memberships needing review, dormant/admin-risk users, role/capability changes needing approval, last-admin risks, support-access expiry, identity link/relink exceptions, access-review results needing human review, recent consequential admin events, recent safe denial/no-op evidence, and provider/outbox/model blockers. Visibility of all relevant attention types is more important than forcing a universal priority order.
3. **Show who I can administer** — show backend-authorized population cards for the current admin level: App Admin users, Tenant Admin users, tenant employees, Customer Admin users, or Customer Users as applicable to the selected context. Cards show visible counts, attention counts, health/status counts, and open/list actions for the administered population.
4. **Show scoped health and available work** — show active users, admin users by level when visible, pending invitations, expired/stale invitations, suspended/disabled users, role/capability coverage, support-access active/expiring, access-review status, recent consequential activity, and provider/outbox/model readiness where safe. Available work is presented as action affordances attached to visible populations or attention cards.
5. **Recover without implying missing authorization** — first-run, empty, forbidden, stale, provider-fail-closed, disabled-actor, no-membership, and partial-data states provide recovery guidance such as selecting an available scope, refreshing stale data, opening visible audit evidence, or contacting an authorized admin. Visible empty states say "nothing to manage" or show `0`; they do not imply the user is unauthorized. Direct denied attempts use safe system-message language and avoid listing unavailable management powers.

### Frontend-safe payload

- `surfaceContract`, `accountContext`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `adminLevel`, `authorityBasis`, `redaction`, `capabilityIds`, `traceRefs`, `correlationId`.
- `hero`: `{ title, scopeLabel, scopeType, adminLevel, administeredPopulationLabels[], supportAccessState?, redactionSummary, traceRefs[], correlationId }`.
- `attentionCounts[]`: `{ attentionType, label, count, severity, statusText, administeredPopulationType?, targetSurfaceId?, filter?, sourceCapabilityId, traceRefs[], redactionState, openActionId? }`. Counts are non-negative integers; `0` is shown as an explicit no-attention state for visible attention types.
- `administeredPopulations[]`: `{ populationType, label, visibleCount, attentionCount, activeCount?, pendingInvitationCount?, suspendedOrDisabledCount?, staleOrExpiredCount?, reviewCount?, roleCoverageSummary?, targetSurfaceId, openActionId, capabilityIds[], traceRefs[] }` where `populationType` is one of `app_admin_users`, `tenant_admin_users`, `tenant_employees`, `customer_admin_users`, or `customer_users`.
- `summaryCards[]`: active users, admin users by level when visible, pending/expired invitations, disabled/suspended users, support-access active/expiring, access-review status, recent safe denial/no-op evidence, and readiness summaries marked safe by backend.
- `authorizedActions[]`: action id, label, governed capability/tool id, administered population or attention type, required idempotency/correlation behavior, result surface, approval/decision-card requirement, and positive next-step guidance. Unavailable actions are omitted from the dashboard action list.
- `recentActivity[]`: browser-safe admin audit excerpts with trace ids and redaction summaries.

Dashboard payload/content must not include hidden workstream/source names, hidden user identities/counts, cross-tenant/customer facts, raw WorkOS ids unless policy-safe, raw JWT/session data, invitation tokens/token hashes, Resend/provider secrets, full email bodies, raw model/provider config, or fixture/mock data as normal runtime. User-facing copy should describe available scoped work and recovery paths rather than enumerating unavailable powers.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh dashboard | `user_admin.view_overview` / `search-user-directory` | Reload backend-owned overview projection. |
| Open users list / filtered queue | `user_admin.list_members` / `search-user-directory` | Render `surface-user-admin-users` with backend-shaped filter context for visible users, memberships, invitations, support-access markers, and review flags. |
| Manage invitation | `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation` / `create-or-resend-invitation` | Render invitation detail/form, action status, approval step, validation recovery, or system message. |
| Manage scoped user or membership | `user_admin.read_user_account`, `user_admin.update_member_status` / `change-membership-role-or-status` | Render user detail, refreshed dashboard/list, action status, approval step, or system message. |
| Preview/manage roles and capabilities | `user_admin.preview_role_change`, `user_admin.change_member_roles` / `change-membership-role-or-status` | Render role-change preview, capability delta, decision card, refreshed detail, or system message. |
| Manage support access | `user_admin.support_access.*` / `grant-or-revoke-support-access` | Render support-access queue/detail, refreshed account detail, decision card, action status, or system message. |
| Start/open access review | `user_admin.access_review.start`, `user_admin.access_review.read` / `run-access-review` | Render access-review task progress/result, human review step, decision card, or system message. |
| Review identity exception | `user_admin.identity_exception.read`, identity relink/recovery capability where assigned | Render user detail identity section, recovery workflow/status, audit evidence, or system message. |
| Open Organization Admin | `saas_owner.organization.list` / `manage-organizations` | Render `surface-user-admin-organization-admin` for SaaS Owner Admins with backend-authorized Organization list/search and boundary notice; omitted for unsupported scopes. |
| Open admin audit evidence | `admin.audit.read` / Audit Trace capability | Render authorized Audit/Trace surface or safe redacted system message. |
| Ask User Admin agent | `user_admin.ask_agent` | Invoke governed agent runtime or provider/model blocked system message. |

## Organization Admin surface

### Intent

`surface-user-admin-organization-admin` is a SaaS Owner surface inside the User Admin workstream for managing customer-facing Organizations backed 1:1 by internal Tenant isolation boundaries. It lets authorized SaaS Owner Admins list/search Organizations, open safe Organization detail, create new Organizations, rename display labels, suspend Organizations, and reactivate suspended Organizations. It is not a tenant app-data browser, support-access shortcut, billing console, or client-side authority source.

### Contract

- Surface id: `surface-user-admin-organization-admin`.
- Surface type: `list-detail-action`.
- Surface contract: `user_admin.organization_admin.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Required context: authenticated active account, selected SaaS Owner `AuthContext`, and backend `saas_owner.tenant.read` for reads or `saas_owner.tenant.manage` for mutations.
- Product/runtime language: browser copy, route labels, DTOs, and forms use **Organization**; backend enforcement, audit partitioning, and persisted isolation use **Tenant**.

### Frontend-safe payload

- `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `authorityBasis`, `boundaryNotice`, `traceRefs`, `correlationId`, `redaction`.
- `organizations[]`: `{ organizationId, organizationName, status, updatedAt?, safeLifecycleSummary?, visibleTenantAdminCount?, actionAvailability[], traceRefs[] }` where counts are omitted unless backend marks them safe and non-enumerating.
- `organizationDetail?`: `{ organizationId, organizationName, status, safeBoundaryNotice, visibleActions[], recentAuditEvents[], traceRefs[], correlationId }`.
- `filters`: query, status, page size, page token, sort, and backend-shaped empty/forbidden labels.
- `forms`: create/rename/suspend/reactivate fields include validation messages, required idempotency key behavior, reason where consequential, and confirmation text.

Forbidden payload content includes tenant/customer application data, customer records, provider ids/secrets, raw billing provider state, raw JWT/session data, hidden Organization counts, support-access internals, unredacted audit evidence, or any field that would let a browser infer hidden tenant/customer existence.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| List/search Organizations | `saas_owner.organization.list` / `manage-organizations` | Refresh list with safe empty state, forbidden state, or redacted system message. |
| Open Organization detail | `saas_owner.organization.read` / `manage-organizations` | Render safe detail and lifecycle actions, or hidden/not-found system message. |
| Create Organization | `saas_owner.organization.create` / `manage-organizations` | Validate name, require idempotency key/correlation id, create active Organization/Tenant, audit, refresh detail/list, or render validation/duplicate/forbidden/no-op result. |
| Rename Organization | `saas_owner.organization.rename` / `manage-organizations` | Validate display label, handle no-op/replay safely, audit, refresh detail/list, or render stale/conflict/forbidden result. |
| Suspend Organization | `saas_owner.organization.suspend` / `manage-organizations` | Require confirmation and reason, suspend lifecycle boundary without exposing app data, audit, refresh detail/list, or render no-op/forbidden/result warning. |
| Reactivate Organization | `saas_owner.organization.reactivate` / `manage-organizations` | Require confirmation, handle no-op/replay safely, audit, refresh detail/list, or render stale/conflict/forbidden result. |
| Open Organization audit evidence | `admin.audit.read` | Render authorized Audit/Trace evidence or safe redacted denial. |

### States and tests

The surface defines loading, empty, ready, submitting, success, validation-error, forbidden, hidden-not-found, no-op, conflict/stale, partial-data, and failure states. Acceptance tests must cover SaaS Owner list/read/create/rename/suspend/reactivate, Organization-vs-Tenant language, idempotent replay/no-op transitions, safe Tenant Admin and Customer Admin denials, missing capability denial, audit/work trace emission, frontend secret boundary, and support-access/billing-boundary non-authority.

Surface-description sufficiency review: sufficient for the first implementation slice. Backend/API/frontend implementers should not need to invent payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual semantics beyond the named style guide/component catalog realization.

## Users list surface

### Intent

`surface-user-admin-users` is a scoped searchable table/card surface for members, accounts, invitations, support-access grants, and access-review flags. It is not an unbounded user export or client-side filtered fixture list.

### Contract

- Surface id: `surface-user-admin-users`.
- Surface type: `list-search`.
- Surface contract: `user_admin.users.v1`.
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
| Open user detail | `user_admin.read_user_account` / `search-user-directory` | Render `surface-user-admin-user-detail`. |
| Create/resend/revoke invitation | `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation` / `create-or-resend-invitation` | Render invitation detail update, no-op, validation, denial, or trace-linked system message. |
| Disable/reactivate membership/account | `user_admin.update_member_status` / `change-membership-role-or-status` | Render detail refresh, no-op, last-admin/self-disable denial, or decision card. |
| Preview/change role | `user_admin.preview_role_change`, `user_admin.change_member_roles` / `change-membership-role-or-status` | Render role-change preview/decision card before mutation. |
| Grant/revoke/extend support access | `user_admin.support_access.*` / `grant-or-revoke-support-access` | Render detail refresh, decision card, or denial. |
| Read/resolve access-review item | `user_admin.access_review.read`, `user_admin.access_review.accept_result`, `user_admin.access_review.reject_result` | Render access-review task or decision/system message. |
| Open audit evidence | `admin.audit.read` | Render authorized Audit/Trace surface or redacted denial. |

### States

Loading preserves submitted filters and disables mutations. Empty distinguishes no users in scope, no search matches, no queue items, and redacted result set. Forbidden hides result counts and identities. Stale disables mutations until refreshed. Responsive table-to-card fallback preserves authority, role, invitation, support, review, risk, trace, and decision-card affordances.

## Invitation detail surface

### Intent

`surface-user-admin-invitation-detail` governs invite/resend/revoke and invitation delivery/acceptance visibility. It explains expiry, duplicates, open-invite state, outbox/Resend readiness, delivery failure, acceptance status, and recovery without exposing raw tokens or provider secrets.

### Contract

- Surface id: `surface-user-admin-invitation-detail`.
- Surface type: `detail-edit` / `workflow-status`.
- Surface contract: `user_admin.invitation_detail.v1`.
- Owning functional agent: `user-admin-agent`.

### Payload and actions

Frontend-safe payload includes invite id, normalized email, requested role, target scope, status, expiry, accepted timestamp, resend/revoke eligibility, outbox delivery state, provider failure summary, idempotency/correlation refs, validation messages, redaction, and trace refs.

Actions map to `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation`, `user_admin.acceptance_status.read`, and trace-open requests. Results render updated panel, workflow status, no-op, validation-error, duplicate/open-invite conflict, provider/outbox blocked, forbidden, or safe system message.

Forbidden content includes raw invitation token/token hash, full email body unless explicitly safe preview, Resend secrets, hidden invitees, cross-scope delivery facts, and email-only authorization claims.

## User detail surface

### Intent

`surface-user-admin-user-detail` provides scoped account, membership, invitation, support-access, access-review, identity-link, and admin-audit detail for a target user. It is not the user's My Account profile and cannot expose self-service-only/private settings unless explicit admin policy allows.

### Contract

- Surface id: `surface-user-admin-user-detail`.
- Surface type: `detail-card-action-panel`.
- Surface contract: `user_admin.user_detail.v1`.
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

- **App Admin** is the app-owner level. App Admin surfaces focus on App Admin users and Tenant Admin users for SMB tenant accounts, plus platform-safe tenant metadata. Tenant employee, Customer Admin, and Customer User data requires an explicit backend-authorized selected tenant/customer context and otherwise returns forbidden/redacted metadata.
- **Tenant Admin** is the SMB tenant level. Tenant Admin surfaces show tenant employees, Customer Admin users for customers the tenant provides access to, tenant-created support access, invitations, role/capability data, access reviews, and scoped audit queues. Cross-tenant targets and App Admin account administration are forbidden.
- **Customer Admin** is the customer level. Customer Admin surfaces show selected Customer Users, customer invitations, customer roles, customer access-review items, and customer audit excerpts only. Tenant employee rows, tenant-level roles/actions, other customers, support-access administration, App Admin users, Tenant Admin users, and tenant-wide audit queues are forbidden.
- **Auditor** may read scoped evidence and traces where permitted and cannot mutate access.

## Common action rules

Every consequential browser action has a stable action id, maps to a governed backend capability/tool, carries correlation and idempotency behavior where needed, recomputes authorization server-side, emits audit/work traces for allow/deny/no-op/failure, and returns a typed result surface, decision card, workflow status, outcome panel, markdown response, or safe system message. Frontend visibility and disabled state are UX hints only.

## Common states

User Admin surfaces define loading, empty, ready, submitting, success, validation-error, forbidden, not_found_or_redacted, conflict, stale/reconnect, partial-data, provider-fail-closed, model-fail-closed, outbox-fail-closed, no-op, approval-required, and failure states. All states preserve selected tenant/customer scoping, browser-safe redaction, trace/correlation links, and recovery guidance.
