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
| `surface-user-admin-users` | `list-search` | `user_admin.users.v1` | Scoped searchable directory for users/memberships; discovery only, with every row/card opening a lifecycle-aware user detail surface. | Revised user graph |
| `surface-user-admin-user-detail` | `show-inspection` | `user_admin.user_detail.v1` | Scoped account, membership, invitation, support-access, access-review, identity, and audit inspection; exposes task entry points but does not mutate access inline. | Revised user graph |
| `surface-user-admin-invitation-create` | `create-form` | `user_admin.invitation_create.v1` | Single-purpose invitation creation form with target scope/role validation, idempotency, outbox boundary, and invitation detail result. | New required surface |
| `surface-user-admin-invitation-detail` | `show-inspection` / `workflow-status` | `user_admin.invitation_detail.v1` | Lifecycle-aware invitation inspection for delivery, acceptance, expiry, resend eligibility, revoke eligibility, and recovery. | Revised user graph |
| `surface-user-admin-invitation-resend-confirmation` | `lifecycle-confirmation` | `user_admin.invitation_resend_confirmation.v1` | Single-purpose resend confirmation with reason, idempotency, outbox/provider fail-closed states, and detail result. | New required surface |
| `surface-user-admin-invitation-revoke-confirmation` | `destructive-lifecycle-confirmation` | `user_admin.invitation_revoke_confirmation.v1` | Single-purpose invitation revoke confirmation with consequence copy, reason, audit/work trace, and detail result. | New required surface |
| `surface-user-admin-membership-status-confirmation` | `destructive-lifecycle-confirmation` | `user_admin.membership_status_confirmation.v1` | Single-purpose disable/suspend/reactivate/remove membership or account confirmation with last-admin/self-action protections. | New required surface |
| `surface-user-admin-role-change-preview` | `decision-card` / `diff` | `user_admin.role_change_preview.v1` | Capability delta, affected workstreams, last-admin impact, policy gate, and approval preview before role mutation. | Revised user graph |
| `surface-user-admin-support-access-grant` | `create-form` | `user_admin.support_access_grant.v1` | Single-purpose support-access grant/extend form with purpose, expiry, approval, idempotency, and detail result. | New required surface |
| `surface-user-admin-support-access-revoke-confirmation` | `destructive-lifecycle-confirmation` | `user_admin.support_access_revoke_confirmation.v1` | Single-purpose support-access revoke confirmation with audit/work trace and detail result. | New required surface |
| `surface-user-admin-access-review-task` | `workflow-status` / `outcome-panel` | `user_admin.access_review_task.v1` | Durable access-review task progress, result, blockers, and human accept/reject review. | Revised user graph |
| `surface-user-admin-identity-exception-review` | `decision-card` / `workflow-status` | `user_admin.identity_exception_review.v1` | Identity-link/relink exception review and approved recovery routing without exposing provider internals. | New required surface |
| `surface-user-admin-organization-directory` | `list-search` | `user_admin.organization_directory.v1` | SaaS Owner Organization directory for backend-authorized Organization discovery and row/card selection. | Revised surface graph |
| `surface-user-admin-organization-detail` | `show-inspection` | `user_admin.organization_detail.v1` | Lifecycle-aware Organization inspection surface that exposes safe task entry points. | Revised surface graph |
| `surface-user-admin-organization-create` | `create-form` | `user_admin.organization_create.v1` | Single-purpose Organization creation form with validation, idempotency, audit, and detail result. | Revised surface graph |
| `surface-user-admin-organization-rename` | `edit-form` | `user_admin.organization_rename.v1` | Single-purpose Organization display-name edit surface. | Revised surface graph |
| `surface-user-admin-organization-suspend-confirmation` | `destructive-lifecycle-confirmation` | `user_admin.organization_suspend_confirmation.v1` | Consequential Organization suspension confirmation surface. | Revised surface graph |
| `surface-user-admin-organization-reactivate-confirmation` | `lifecycle-confirmation` | `user_admin.organization_reactivate_confirmation.v1` | Organization reactivation confirmation surface. | Revised surface graph |
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
| Open users list / filtered queue | `user_admin.list_members` / `search-user-directory` | Render `surface-user-admin-users` with backend-shaped filter context. The list is discovery-only; row/card selection opens `surface-user-admin-user-detail`. |
| Invite user | `user_admin.invite_user` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-create`; submission returns `surface-user-admin-invitation-detail`, validation/error state, or safe system message. |
| Open invitation | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Open lifecycle-aware `surface-user-admin-invitation-detail`; resend/revoke are task entry points, not inline mutations. |
| Resend invitation | `user_admin.resend_invitation` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-resend-confirmation`; submission returns refreshed invitation detail, outbox/provider blocked state, no-op, or system message. |
| Revoke invitation | `user_admin.revoke_invitation` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-revoke-confirmation`; submission returns refreshed invitation detail, no-op, denial, or system message. |
| Open scoped user | `user_admin.read_user_account` / `search-user-directory` | Render `surface-user-admin-user-detail` as inspection and task router only. |
| Change membership/account lifecycle | `user_admin.update_member_status` / `change-membership-role-or-status` | Open `surface-user-admin-membership-status-confirmation`; submission returns refreshed detail, last-admin/self-action denial, no-op, or system message. |
| Preview/manage roles and capabilities | `user_admin.preview_role_change`, `user_admin.change_member_roles` / `change-membership-role-or-status` | Render `surface-user-admin-role-change-preview` before mutation; commit returns refreshed detail, approval-required decision, no-op, or system message. |
| Grant/extend support access | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open `surface-user-admin-support-access-grant`; submission returns refreshed user detail, approval-required decision, validation, or denial. |
| Revoke support access | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open `surface-user-admin-support-access-revoke-confirmation`; submission returns refreshed user detail, no-op, or denial. |
| Start/open access review | `user_admin.access_review.start`, `user_admin.access_review.read` / `run-access-review` | Render access-review task progress/result; accept/reject actions record human review and route further access changes through deterministic User Admin task surfaces. |
| Review identity exception | `user_admin.identity_relink.review` | Render `surface-user-admin-identity-exception-review`; approved recovery routes to workflow/status or user detail without exposing provider internals. |
| Open Organization Admin | `saas_owner.organization.list` / `manage-organizations` | Render `surface-user-admin-organization-directory` for SaaS Owner Admins with backend-authorized Organization list/search and boundary notice; omitted for unsupported scopes. |
| Open admin audit evidence | `admin.audit.read` / Audit Trace capability | Render authorized Audit/Trace surface or safe redacted system message. |
| Ask User Admin agent | `user_admin.ask_agent` | Invoke governed agent runtime or provider/model blocked system message. |

## Organization Admin surface graph

### Intent

Organization Admin is a SaaS Owner surface graph inside the User Admin workstream for managing customer-facing Organizations backed 1:1 by internal Tenant isolation boundaries. It is not a single CRUD panel: the directory discovers Organizations, detail inspects one Organization, and dedicated create/edit/lifecycle confirmation surfaces perform consequential mutations. It is not a tenant app-data browser, support-access shortcut, billing console, or client-side authority source.

### Graph and collection-object progression

| Surface role | Surface id | Contract | Purpose and graph behavior |
|---|---|---|---|
| Domain list/search | `surface-user-admin-organization-directory` | `user_admin.organization_directory.v1` | Lists/searches platform-safe Organizations. Every row/card is clickable and keyboard-operable and opens `surface-user-admin-organization-detail` through `action-organization-read`. The dashboard action **Open Organization Admin** opens this surface. |
| Show/inspection | `surface-user-admin-organization-detail` | `user_admin.organization_detail.v1` | Shows one Organization's browser-safe identity, lifecycle status, boundary notice, trace refs, recent redacted audit excerpts, and task entry points. It does not directly mutate lifecycle or editable fields. |
| Create | `surface-user-admin-organization-create` | `user_admin.organization_create.v1` | Owns Organization name/reason fields, validation, idempotency, submission, failure states, audit/work trace, and success routing to `surface-user-admin-organization-detail`. |
| Edit | `surface-user-admin-organization-rename` | `user_admin.organization_rename.v1` | Owns display-name editing for one Organization, including validation, stale/conflict/no-op handling, idempotency, audit/work trace, and success routing back to detail. |
| Destructive lifecycle confirmation | `surface-user-admin-organization-suspend-confirmation` | `user_admin.organization_suspend_confirmation.v1` | Requires consequence copy, confirmation, reason, idempotency, backend authorization, audit/work trace, and result routing to detail or `system_message`. |
| Lifecycle confirmation | `surface-user-admin-organization-reactivate-confirmation` | `user_admin.organization_reactivate_confirmation.v1` | Requires confirmation/reason where policy requires it, idempotency, backend authorization, audit/work trace, and result routing to detail or `system_message`. |

All graph nodes are owned by `user-admin-agent`. Reusable placements are limited to authorized User Admin/SaaS Owner shell contexts unless another workstream explicitly declares a reusable-by edge.

### Shared authority and language

- Required context: authenticated active account, selected SaaS Owner `AuthContext`, and backend `saas_owner.tenant.read` for directory/detail reads or `saas_owner.tenant.manage` for create/rename/suspend/reactivate.
- Product/runtime language: browser copy, route labels, DTOs, and forms use **Organization**; backend enforcement, audit partitioning, and persisted isolation use **Tenant**.
- Forbidden payload content: tenant/customer application data, customer records, provider ids/secrets, raw billing provider state, raw JWT/session data, hidden Organization counts, support-access internals, unredacted audit evidence, or fields that would let a browser infer hidden tenant/customer existence.

### Frontend-safe payloads

- Directory: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `authorityBasis`, `boundaryNotice`, `traceRefs`, `correlationId`, `redaction`, `organizations[]`, `filters`, `systemStates`, `emptyMessage`, `forbiddenMessage`, `lastResult`. Each `organizations[]` row contains `{ organizationId, organizationName, status, updatedAt?, safeLifecycleSummary?, visibleTenantAdminCount?, actionAvailability[], traceRefs[] }`; counts are omitted unless backend marks them safe and non-enumerating.
- Detail: directory shared fields plus `organizationDetail`: `{ organizationId, organizationName, status, safeBoundaryNotice, visibleActions[], recentAuditEvents[], traceRefs[], correlationId }`. Detail task entries route to create/rename/suspend/reactivate surfaces and never perform mutations inline.
- Create: `{ organizationNameDraft?, reasonDraft?, validationMessages?, idempotencyKeyHint, confirmationCopy, boundaryNotice, traceRefs, correlationId, redaction }`. Success opens detail for the created Organization.
- Rename: `{ organizationId, organizationName, proposedOrganizationName?, reasonDraft?, validationMessages?, staleVersion?, idempotencyKeyHint, boundaryNotice, traceRefs, correlationId, redaction }`. Success refreshes detail.
- Suspend/reactivate confirmation: `{ organizationId, organizationName, currentStatus, consequenceCopy, confirmationRequired, reasonRequired, reasonDraft?, validationMessages?, idempotencyKeyHint, boundaryNotice, traceRefs, correlationId, redaction }`. Success refreshes detail; no-op/denied/conflict uses typed `system_message` or refreshed detail with `lastResult`.

### Actions and result surfaces

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| List/search Organizations | `saas_owner.organization.list` / `manage-organizations` | Refresh `surface-user-admin-organization-directory` with safe empty/forbidden/redacted state. |
| Open Organization detail | `saas_owner.organization.read` / `manage-organizations` | Open `surface-user-admin-organization-detail`, or hidden/not-found `system_message`. |
| Open create form | `saas_owner.organization.create` / `manage-organizations` | Open `surface-user-admin-organization-create`; backend remains authoritative when submitted. |
| Create Organization | `saas_owner.organization.create` / `manage-organizations` | Validate name, require idempotency key/correlation id, create active Organization/Tenant, audit, then open detail or validation/duplicate/forbidden/no-op `system_message`. |
| Open rename form | `saas_owner.organization.rename` / `manage-organizations` | Open `surface-user-admin-organization-rename` from detail for Organizations with rename availability. |
| Rename Organization | `saas_owner.organization.rename` / `manage-organizations` | Validate display label, handle no-op/replay safely, audit, then refresh detail or stale/conflict/forbidden `system_message`. |
| Open suspend confirmation | `saas_owner.organization.suspend` / `manage-organizations` | Open `surface-user-admin-organization-suspend-confirmation` when lifecycle state allows suspension. |
| Suspend Organization | `saas_owner.organization.suspend` / `manage-organizations` | Require confirmation and reason, suspend lifecycle boundary without exposing app data, audit, then refresh detail or no-op/forbidden/result warning. |
| Open reactivate confirmation | `saas_owner.organization.reactivate` / `manage-organizations` | Open `surface-user-admin-organization-reactivate-confirmation` when lifecycle state allows reactivation. |
| Reactivate Organization | `saas_owner.organization.reactivate` / `manage-organizations` | Require confirmation, handle no-op/replay safely, audit, then refresh detail or stale/conflict/forbidden result. |
| Open Organization audit evidence | `admin.audit.read` | Render authorized Audit/Trace evidence or safe redacted denial. |

### States and tests

Each graph node defines loading, empty, ready, submitting, success, validation-error, forbidden, hidden-not-found, no-op, conflict/stale, partial-data, and failure states as applicable. Acceptance tests must cover dashboard-to-directory traversal, directory-to-detail traversal, create/rename/suspend/reactivate dedicated form or confirmation surfaces, Organization-vs-Tenant language, idempotent replay/no-op transitions, safe Tenant Admin and Customer Admin denials, missing capability denial, audit/work trace emission, frontend secret boundary, support-access/billing-boundary non-authority, keyboard operation, focus movement, and typed `system_message` results.

Surface-description sufficiency review: sufficient for durable collection-object readiness. The split graph is sufficiently unambiguous for developers/generators to implement and review without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics.

## User and access-administration surface graph

### Intent

The non-Organization User Admin graph follows the canonical durable collection-object progression. The users directory discovers scoped access subjects; user detail inspects one subject and exposes task entry points; dedicated create, edit, lifecycle, confirmation, decision, and workflow surfaces perform consequential work. The list and detail surfaces are never catch-all mutation panels.

### Graph and collection-object progression

| Surface role | Surface id | Contract | Purpose and graph behavior |
|---|---|---|---|
| Domain list/search | `surface-user-admin-users` | `user_admin.users.v1` | Lists/searches visible users, memberships, invitation references, support-access markers, and review flags. Every row/card is clickable and keyboard-operable and opens `surface-user-admin-user-detail` through a backend-authorized read action. |
| Show/inspection | `surface-user-admin-user-detail` | `user_admin.user_detail.v1` | Shows one scoped user's browser-safe identity, account/membership state, role/capability summary, invitation/support/access-review summaries, identity exception state, trace refs, and task entry points. It does not directly mutate access. |
| Create | `surface-user-admin-invitation-create` | `user_admin.invitation_create.v1` | Owns user invitation creation for a selected scope, target email/display label, requested role(s), validation, idempotency, outbox/provider readiness, audit/work trace, and success routing to invitation detail. |
| Invitation show/inspection | `surface-user-admin-invitation-detail` | `user_admin.invitation_detail.v1` | Shows one invitation's lifecycle, delivery/outbox state, expiry, acceptance state, duplicate/open-invite context, resend/revoke eligibility, and recovery actions. |
| Invitation resend lifecycle | `surface-user-admin-invitation-resend-confirmation` | `user_admin.invitation_resend_confirmation.v1` | Single-purpose resend confirmation with reason, idempotency, provider/outbox fail-closed handling, no-op semantics, audit/work trace, and result routing to invitation detail or system message. |
| Invitation destructive lifecycle | `surface-user-admin-invitation-revoke-confirmation` | `user_admin.invitation_revoke_confirmation.v1` | Single-purpose revoke confirmation with consequence copy, reason, backend authorization, idempotency, audit/work trace, and result routing to invitation detail or system message. |
| Membership/account destructive lifecycle | `surface-user-admin-membership-status-confirmation` | `user_admin.membership_status_confirmation.v1` | Single-purpose disable, suspend, reactivate, remove, or account-status confirmation. It owns consequence copy, target membership/account id, reason, last-admin/self-action checks, idempotency, audit/work trace, and result routing to user detail or system message. |
| Role/capability edit decision | `surface-user-admin-role-change-preview` | `user_admin.role_change_preview.v1` | Shows proposed role/capability diff, affected workstreams, last-admin impact, escalation risk, approval policy, evidence, alternatives, and commit/revise/cancel actions. Commit is the only role mutation entry point. |
| Support access create/extend | `surface-user-admin-support-access-grant` | `user_admin.support_access_grant.v1` | Owns grant or extension purpose, expiry, approver policy, validation, idempotency, audit/work trace, and result routing to user detail or decision/system message. |
| Support access destructive lifecycle | `surface-user-admin-support-access-revoke-confirmation` | `user_admin.support_access_revoke_confirmation.v1` | Owns revoke consequence copy, reason, idempotency, audit/work trace, and result routing to user detail or system message. |
| Access review workflow | `surface-user-admin-access-review-task` | `user_admin.access_review_task.v1` | Shows durable access-review task progress, provider/model blockers, evidence, recommendations, human accept/reject review, and follow-up task routing. Worker output cannot directly mutate access. |
| Identity exception review | `surface-user-admin-identity-exception-review` | `user_admin.identity_exception_review.v1` | Shows scoped identity-link/relink exception evidence, risk, provider-boundary redaction, recovery options, approval state, and routing to deterministic recovery/status surfaces. |

All graph nodes are owned by `user-admin-agent`. Reusable placements are limited to authorized My Account attention summaries and Audit/Trace evidence links unless another workstream explicitly declares a reusable-by edge.

### Shared authority and language

- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active membership, and backend capabilities for the exact read, invitation, membership, role, support-access, review, identity, or audit operation.
- Frontend action visibility is advisory only. Every list row selection, detail task entry point, form submission, confirmation, decision, workflow review, deep link, and trace-open action is reauthorized server-side.
- Browser payloads use role-appropriate User Admin language: App Admin users, Tenant Admin users, tenant employees, Customer Admin users, Customer Users, memberships, invitations, support access, access reviews, and identity exceptions. They must not imply authority outside selected scope.
- Forbidden payload content: hidden users/memberships/counts, cross-scope identities, raw WorkOS/provider ids unless policy-safe, raw JWT/session data, invitation tokens/token hashes, Resend/provider secrets, full email bodies, private profile/settings data, raw model/provider config, or unredacted audit evidence.

### Surface payloads and task routing

- Directory: `surfaceContract`, selected `AuthContext`, filters, pagination/sort, dashboard-origin queue id, `rows[]`, redaction, trace refs, and correlation id. Each row includes frontend-safe user/member/invitation/support/review badges and action availability, but row activation only opens user detail or an explicitly typed inspection surface.
- User detail: target account/member id when safe, scoped identity/account/membership summaries, role/capability summary, invitation summaries, support-access state, access-review items, identity exception state, recent browser-safe audit excerpts, visible task entry points, denial categories, trace refs, and correlation id.
- Invitation create/detail/resend/revoke: normalized email/display label where authorized, requested roles, target scope, status, expiry, accepted timestamp, delivery/outbox state, provider failure summary, eligibility flags, validation messages, idempotency/correlation refs, redaction, and trace refs. Tokens and full email bodies are always forbidden.
- Membership status confirmation: target membership/account summary, current and proposed lifecycle state, consequence copy, confirmation requirement, reason requirement, last-admin/self-action analysis, stale version, idempotency hint, trace refs, and redaction.
- Role preview: current and proposed roles/capabilities, capability delta, affected workstreams, policy/approval state, last-admin impact, escalation risk, alternatives, preview version, idempotency hint, trace refs, and redaction.
- Support-access grant/revoke: target support-access subject, current support state, requested expiry/purpose, approver policy, consequence copy, validation messages, idempotency hint, trace refs, and redaction.
- Access review and identity exception: task/exception id, selected scope, lifecycle/status, blockers, provider/model readiness, evidence refs, recommendation/risk/confidence summaries, human review state, no-direct-mutation flag, trace refs, and redaction.

### Actions and result surfaces

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Search/list users | `user_admin.list_members` / `search-user-directory` | Refresh `surface-user-admin-users` with safe empty/forbidden/redacted state. |
| Open user detail | `user_admin.read_user_account` / `search-user-directory` | Open `surface-user-admin-user-detail`, or hidden/not-found `system_message`. |
| Open invite form | `user_admin.invite_user` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-create` with backend-shaped scope/role options. |
| Submit invite | `user_admin.invite_user` / `create-or-resend-invitation` | Validate, require idempotency key/correlation id, enqueue local/Resend outbox according to readiness, audit, then open invitation detail or validation/duplicate/forbidden/outbox-blocked `system_message`. |
| Open invitation detail | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-detail`, or hidden/not-found `system_message`. |
| Resend invitation | `user_admin.resend_invitation` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-resend-confirmation`; submission refreshes detail or returns no-op/forbidden/outbox-blocked result. |
| Revoke invitation | `user_admin.revoke_invitation` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-revoke-confirmation`; submission refreshes detail or returns no-op/forbidden/stale result. |
| Open membership lifecycle confirmation | `user_admin.update_member_status` / `change-membership-role-or-status` | Open `surface-user-admin-membership-status-confirmation` for disable/suspend/reactivate/remove/account-state changes. |
| Commit membership lifecycle change | `user_admin.update_member_status` / `change-membership-role-or-status` | Enforce last-admin/self-action/scope policy, idempotency, and audit; refresh user detail or return denial/no-op/conflict. |
| Preview role change | `user_admin.preview_role_change` / `change-membership-role-or-status` | Open `surface-user-admin-role-change-preview` with capability delta and decision state. |
| Commit role change | `user_admin.change_member_roles` / `change-membership-role-or-status` | Require current preview/version, idempotency/correlation, authorization, approval where needed, and audit; refresh user detail or return approval/denial/no-op/conflict. |
| Open support grant/extend form | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open `surface-user-admin-support-access-grant`; backend supplies allowed expiry/purpose policy. |
| Commit support grant/extend | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Validate purpose/expiry/approval, idempotency, and audit; refresh user detail or return decision/system message. |
| Revoke support access | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open `surface-user-admin-support-access-revoke-confirmation`; submission refreshes user detail or returns no-op/denial. |
| Start/open access review | `user_admin.access_review.start/read/cancel/accept_result/reject_result` / `run-access-review` | Render `surface-user-admin-access-review-task`; accept/reject records review only and routes any real access mutation through membership/role/support task surfaces. |
| Review identity exception | `user_admin.identity_relink.review` | Render `surface-user-admin-identity-exception-review`; recovery actions route to approved workflow/status or safe denial. |
| Open admin audit evidence | `admin.audit.read` | Render authorized Audit/Trace surface or redacted denial. |

### States and tests

Each user-admin graph node defines loading, empty, ready, submitting, success, validation-error, forbidden, hidden-not-found, no-op, conflict/stale, partial-data, provider-fail-closed, model-fail-closed, outbox-fail-closed, approval-required, and failure states as applicable. Acceptance tests must cover dashboard-to-directory traversal, directory-to-detail traversal, each dedicated invitation/membership/role/support/access-review/identity surface, role-specific variants, idempotent replay/no-op transitions, last-admin/self-action denials, cross-tenant/customer denials, audit/work trace emission, frontend secret boundary, keyboard operation, focus movement, responsive table-to-card behavior, and typed `system_message` results.

Surface-description sufficiency review: the previous broad list/detail/action panels are not sufficient for durable collection-object readiness because they combined discovery, inspection, create/edit, and lifecycle mutation. This revised user-admin graph is sufficient for developers/generators to implement and review without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics; existing implementation tasks should be repaired to match the split graph. Organization Admin is intentionally out of scope for this revision.

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
