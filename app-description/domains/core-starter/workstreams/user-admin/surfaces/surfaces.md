# Surfaces: User Admin

## Surface architecture

User Admin is an AI-first access operations workstream. Its surfaces are structured, backend-backed work units owned by `user-admin-agent`, not a generic CRUD admin page set. They help authorized admins answer:

1. **What access-administration work needs attention now?**
2. **Which scoped users, memberships, invitations, roles, support grants, identity exceptions, and review tasks are involved?**
3. **What safe action is available, denied, stale, no-op, approval-required, or blocked by provider/model/outbox readiness?**
4. **Which audit/work traces and evidence explain the state, recommendation, denial, or mutation?**

All surfaces use the canonical AI-first workstream shell, structured surface envelope, governed browser-tool actions, selected `AuthContext`, tenant/customer redaction, trace links, and the skills-pack authoritative web UI style guide. Routes and links only reopen functional agents, filtered surfaces, source evidence, or typed result surfaces; they do not define product meaning or bypass backend authorization.

## Conformance policy for realization cleanup

- Canonical surface types are authoritative for User Admin descendants: `dashboard`, `list-search`, `show-inspection`, `create-form`, `edit-form`, `lifecycle-confirmation`, `destructive-lifecycle-confirmation`, `decision-card`, `workflow-status`, `outcome-panel`, `diff`, and `system-message`. Existing runtime compatibility shims such as `detail-edit` may be accepted only when they carry a documented canonical mapping and must not collapse inspection, edit, and destructive lifecycle semantics into one UI path.
- `surface-user-admin-dashboard` is the dashboard trunk. Role-specific dashboard variants may be produced only as backend-authored variants of this trunk with explicit `selectedAuthContext`, `adminLevel`, `authorityBasis`, visible attention/population payloads, and omitted-forbidden actions. Variants do not create separate product dashboards or frontend-inferred authority.
- `user-admin-agent` is the canonical functional-agent id. `agent-user-admin` is a runtime alias for compatibility until implementation normalization; no surface graph edge should treat the two ids as separate user-facing agents.
- `surface-user-admin-user-detail`, `surface-user-admin-invitation-detail`, and `surface-user-admin-organization-detail` are `show-inspection` task routers. They must not perform inline role, status, support-access, invitation, access-review, identity, or Organization lifecycle mutations. Consequential work opens dedicated task, form, decision, workflow, lifecycle-confirmation, destructive-lifecycle-confirmation, result, or `surface-user-admin-system-message` surfaces.
- Dashboard attention, administered populations, branch actions, list row/card activation, return actions, form options, policy limits, eligibility, target surface ids, target object types, and action ids are backend-authored. Frontend rendering may display backend data and submit browser-tool actions, but it must not derive hidden queues, hidden counts, row routing, role choices, expiry choices, or authority from client-local labels/status.
- Default browser payloads use business language and expose only user-useful fields. Raw capability ids, governed-tool ids, correlation/idempotency mechanics, internal trace/event ids, provider/model/outbox internals, and implementation diagnostics belong in role-gated audit/support/developer drilldowns or diagnostic metadata, never in the default surface view.
- Safe result handling is typed: denied, stale, hidden/not-found, validation, duplicate/open-invite, no-op, provider-fail-closed, outbox-fail-closed, model-fail-closed, missing context, disabled actor, and tenant/customer-scope conflicts return `surface-user-admin-system-message` or a typed decision/workflow/status result with user-safe recovery, trace/correlation refs, redaction notes, and no fake success.
- Access-review and identity-exception starter semantics are durable review/task semantics. Access-review worker or model output cannot directly mutate access; human accept/reject records review and routes any real access change through membership, role, support-access, or invitation task surfaces. Identity exception recovery is fail-closed, provider-safe, and routes through approved recovery/status surfaces without exposing raw provider internals.

Surface-description sufficiency review: this conformance policy is sufficient for implementation cleanup. Developers/generators have explicit canonical type mapping, dashboard variant, functional-agent alias, inspection/task-router, backend-authored routing/options, metadata visibility, typed system-message, access-review, and identity-exception decisions without inventing payload fields, actions, states, auth/tenant behavior, trace links, or visual/component semantics.

## Surface inventory

| Surface id | Type | Contract | Primary purpose | Status |
|---|---|---|---|---|
| `surface-user-admin-dashboard` | `dashboard` | `user_admin.dashboard.v1` | Attention-first User Admin command center for SaaS Owner Admin, Organization, Organization Admin, directory, invitation, role, support, review, provider, and audit health. | Rebuilt from archive |
| `surface-user-admin-saas-owner-admins` | `list-search` | `user_admin.saas_owner_admins.v1` | SaaS Owner scoped directory for app-owner/admin users and invitations; discovery only, with rows opening SaaS Owner Admin detail or invitation detail. | New required surface |
| `surface-user-admin-saas-owner-admin-invitation-create` | `create-form` | `user_admin.saas_owner_admin_invitation_create.v1` | Single-purpose invitation form for another SaaS Owner Admin with role validation, idempotency, outbox boundary, and last-owner-admin-safe audit. | New required surface |
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
| `surface-user-admin-organization-detail` | `show-inspection` | `user_admin.organization_detail.v1` | Lifecycle-aware Organization inspection surface that exposes safe Organization lifecycle and Organization Admin management task entry points. | Revised surface graph |
| `surface-user-admin-organization-admins` | `list-search` | `user_admin.organization_admins.v1` | SaaS Owner scoped directory of Organization Admin users/invitations for one selected Organization/Tenant. | New required surface |
| `surface-user-admin-organization-admin-invitation-create` | `create-form` | `user_admin.organization_admin_invitation_create.v1` | Single-purpose bootstrap/invite form for a `TENANT_ADMIN` in the selected Organization with idempotency, target-scope validation, outbox boundary, and audit. | New required surface |
| `surface-user-admin-organization-admin-detail` | `show-inspection` | `user_admin.organization_admin_detail.v1` | Shows one Organization Admin membership/invitation and routes role/status/resend/revoke tasks without tenant app-data exposure. | New required surface |
| `surface-user-admin-organization-create` | `create-form` | `user_admin.organization_create.v1` | Single-purpose Organization creation form with validation, idempotency, audit, and detail result. | Revised surface graph |
| `surface-user-admin-organization-rename` | `edit-form` | `user_admin.organization_rename.v1` | Single-purpose Organization display-name edit surface. | Revised surface graph |
| `surface-user-admin-organization-suspend-confirmation` | `destructive-lifecycle-confirmation` | `user_admin.organization_suspend_confirmation.v1` | Consequential Organization suspension confirmation surface. | Revised surface graph |
| `surface-user-admin-organization-reactivate-confirmation` | `lifecycle-confirmation` | `user_admin.organization_reactivate_confirmation.v1` | Organization reactivation confirmation surface. | Revised surface graph |
| `surface-user-admin-customer-directory` | `list-search` | `user_admin.customer_directory.v1` | Organization Admin Customer directory for backend-authorized Customer discovery and row/card selection within the selected Organization/Tenant. | New required surface |
| `surface-user-admin-customer-detail` | `show-inspection` | `user_admin.customer_detail.v1` | Lifecycle-aware Customer inspection surface that exposes safe Customer lifecycle and Customer Admin management task entry points. | New required surface |
| `surface-user-admin-customer-admins` | `list-search` | `user_admin.customer_admins.v1` | Organization Admin scoped directory of Customer Admin users/invitations for one selected Customer. | New required surface |
| `surface-user-admin-customer-admin-invitation-create` | `create-form` | `user_admin.customer_admin_invitation_create.v1` | Single-purpose bootstrap/invite form for a `CUSTOMER_ADMIN` in the selected Customer with idempotency, target-scope validation, outbox boundary, and audit. | New required surface |
| `surface-user-admin-customer-admin-detail` | `show-inspection` | `user_admin.customer_admin_detail.v1` | Shows one Customer Admin membership/invitation and routes role/status/resend/revoke tasks without sibling-customer or tenant-wide authority exposure. | New required surface |
| `surface-user-admin-customer-create` | `create-form` | `user_admin.customer_create.v1` | Single-purpose Customer creation form with selected Organization/Tenant validation, idempotency, audit, and detail result. | New required surface |
| `surface-user-admin-customer-rename` | `edit-form` | `user_admin.customer_rename.v1` | Single-purpose Customer display-name/profile edit surface. | New required surface |
| `surface-user-admin-customer-suspend-confirmation` | `destructive-lifecycle-confirmation` | `user_admin.customer_suspend_confirmation.v1` | Consequential Customer suspension/archive confirmation surface. | New required surface |
| `surface-user-admin-customer-reactivate-confirmation` | `lifecycle-confirmation` | `user_admin.customer_reactivate_confirmation.v1` | Customer reactivation confirmation surface. | New required surface |
| `surface-user-admin-system-message` | `system-message` | `user_admin.system_message.v1` | Safe denial, validation, provider/outbox/model blocked, stale, conflict, and no-op recovery. | Rebuilt from archive |

## Navigation tree contract

The User Admin human surface graph is a tree for navigation and auditability:

```text
surface-user-admin-dashboard (trunk)
├── surface-user-admin-saas-owner-admins (SaaS Owner Admin branch root; SaaS Owner/App Admin only)
│   └── surface-user-admin-saas-owner-admin-invitation-create
├── surface-user-admin-customer-directory (Customer Directory branch root; Organization/Tenant Admin only)
│   ├── surface-user-admin-customer-detail
│   ├── surface-user-admin-customer-admins
│   ├── surface-user-admin-customer-admin-invitation-create
│   ├── surface-user-admin-customer-admin-detail
│   ├── surface-user-admin-customer-create
│   ├── surface-user-admin-customer-rename
│   ├── surface-user-admin-customer-suspend-confirmation
│   └── surface-user-admin-customer-reactivate-confirmation
├── surface-user-admin-users (User Directory branch root)
│   ├── surface-user-admin-user-detail
│   ├── surface-user-admin-invitation-create
│   ├── surface-user-admin-invitation-detail
│   ├── surface-user-admin-invitation-resend-confirmation
│   ├── surface-user-admin-invitation-revoke-confirmation
│   ├── surface-user-admin-membership-status-confirmation
│   ├── surface-user-admin-role-change-preview
│   ├── surface-user-admin-support-access-grant
│   ├── surface-user-admin-support-access-revoke-confirmation
│   ├── surface-user-admin-access-review-task
│   └── surface-user-admin-identity-exception-review
└── surface-user-admin-organization-directory (Organization Directory branch root; SaaS Owner/App Admin only)
    ├── surface-user-admin-organization-detail
    ├── surface-user-admin-organization-admins
    ├── surface-user-admin-organization-admin-invitation-create
    ├── surface-user-admin-organization-admin-detail
    ├── surface-user-admin-organization-create
    ├── surface-user-admin-organization-rename
    ├── surface-user-admin-organization-suspend-confirmation
    └── surface-user-admin-organization-reactivate-confirmation
```

Graph rules:

- `surface-user-admin-dashboard` is the trunk and must declare backend-authored branch actions for **Show users** / open User Directory and, when authorized, **Show organizations** / open Organization Directory.
- `surface-user-admin-saas-owner-admins`, `surface-user-admin-customer-directory`, `surface-user-admin-users`, and `surface-user-admin-organization-directory` are the only first-level branch roots. They own discovery, filtering, pagination, row/card activation, and create entry points for their branches. `surface-user-admin-saas-owner-admins` is omitted unless the selected context has SaaS Owner Admin user-management authority. `surface-user-admin-customer-directory` is omitted unless the selected context has Organization/Tenant Admin Customer-management authority.
- Every descendant surface includes branch navigation metadata in its browser-safe payload: `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, safe filter/context preservation hints, trace refs, and correlation id.
- User branch descendants expose `action-user-admin-show-users` with label **Show users** or **Back to users**, `browserToolId: user-admin.show-users`, governed tool `search-user-directory`, capability `user_admin.list_members`, result surface `surface-user-admin-users`, and backend-authored safe filters only.
- Organization branch descendants expose `action-user-admin-show-organizations` with label **Show organizations** or **Back to organizations**, `browserToolId: user-admin.show-organizations`, governed tool `manage-organizations`, capability `saas_owner.organization.list`, result surface `surface-user-admin-organization-directory`, and backend-authored safe filters only.
- Customer branch descendants expose `action-user-admin-show-customers` with label **Show customers** or **Back to customers**, `browserToolId: user-admin.show-customers`, governed tool `manage-customers`, capability `tenant.customer.list`, result surface `surface-user-admin-customer-directory`, and backend-authored safe filters only.
- Dashboard, deep-link, row/card, and branch-return traversal is always reauthorized server-side. Unsupported, stale, hidden, cross-scope, or unauthorized traversal returns `surface-user-admin-system-message` with user-safe recovery and trace/correlation links; ready dashboards normally omit forbidden Organization branch actions instead of displaying disabled privileged work.
- Frontend rendering may use the branch metadata for buttons, breadcrumbs, and focus recovery, but frontend visibility is advisory only and must not infer authority or hidden object existence.

Surface-description sufficiency review: sufficient for tree-navigation implementation. The trunk, branch roots, descendant nodes, branch-return action ids/labels, capability mappings, auth behavior, stale/forbidden result surface, trace/correlation requirements, and tests are explicit enough for developers/generators to implement without inventing surface ids, action ids, states, auth/tenant behavior, or trace links.

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
| Show SaaS Owner Admins / Open app-owner admins | `saas_owner.admin.list` / `manage-saas-owner-admins` | Render `surface-user-admin-saas-owner-admins` for SaaS Owner/App Admin selected contexts with backend-authorized app-owner admin list/search; omitted for Tenant Admin/Customer Admin or unsupported scopes. |
| Invite SaaS Owner Admin | `saas_owner.admin.invite` / `manage-saas-owner-admins` | Open `surface-user-admin-saas-owner-admin-invitation-create`; submission returns admin invitation/detail, validation, provider/outbox blocked state, no-op, or safe system message. |
| Show organizations / Open Organization Directory | `saas_owner.organization.list` / `manage-organizations` | Render `surface-user-admin-organization-directory` for SaaS Owner/App Admin selected contexts with backend-authorized Organization list/search and boundary notice; omitted for Tenant Admin/Customer Admin or unsupported scopes. |
| Show customers / Open Customer Directory | `tenant.customer.list` / `manage-customers` | Render `surface-user-admin-customer-directory` for Organization/Tenant Admin selected contexts with backend-authorized Customer list/search and boundary notice; omitted for SaaS Owner-only, Customer Admin, or unsupported scopes. |
| Open admin audit evidence | `admin.audit.read` / Audit Trace capability | Render authorized Audit/Trace surface or safe redacted system message. |
| Ask User Admin agent | `user_admin.ask_agent` | Invoke governed agent runtime or provider/model blocked system message. |

## SaaS Owner Admin user-management surface graph

### Intent

SaaS Owner Admin user management is an app-owner surface graph inside User Admin for inviting and maintaining the people who can administer the SaaS app itself. It is separate from Organization Admin management: app-owner admins operate in SaaS Owner scope, while Organization Admins operate in a selected Organization/Tenant scope.

### Graph and progression

| Surface role | Surface id | Contract | Purpose and graph behavior |
|---|---|---|---|
| App-owner admin list/search | `surface-user-admin-saas-owner-admins` | `user_admin.saas_owner_admins.v1` | Lists/searches visible SaaS Owner Admin users and app-owner invitations. Every row/card opens a user/invitation detail through backend-authored target routing. |
| App-owner admin invite | `surface-user-admin-saas-owner-admin-invitation-create` | `user_admin.saas_owner_admin_invitation_create.v1` | Owns invitation of another `SAAS_OWNER_ADMIN`, including role validation, idempotency, outbox/Resend boundary, last-owner-admin policy context, and success routing to detail. |

#### `surface-user-admin-saas-owner-admins` list/search contract

- Surface id: `surface-user-admin-saas-owner-admins`.
- Surface type: `list-search`.
- Surface contract: `user_admin.saas_owner_admins.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: first-level SaaS Owner Admin branch root opened from `surface-user-admin-dashboard` by `action-user-admin-show-saas-owner-admins`; branch descendants return through `action-user-admin-show-saas-owner-admins`.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, backend `saas_owner.admin.list` capability, and SaaS Owner scope visibility. Tenant Admin, Customer Admin, missing-membership, disabled-actor, support-only, or stale selected context attempts return `surface-user-admin-system-message` without revealing whether app-owner admins exist.
- User goal: find visible SaaS Owner Admin accounts and open app-owner admin invitation records in order to inspect authority, invitation state, and safe next actions. The list is discovery-only; it never changes roles, membership status, or invitation lifecycle inline.

Frontend-safe payload for `user_admin.saas_owner_admins.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `query`, `filters`, `sort`, `pageInfo`, `emptyMessage`, `systemStates`, and `lastResult`.
- `summary`: `{ visibleAdminCount, visibleInvitationCount, activeAdminCount, pendingInvitationCount, expiredInvitationCount, lastOwnerAdminRiskCount?, outboxBlockedCount?, providerBlockedCount?, reviewNeededCount?, traceRefs[] }`. Counts are backend-authorized and must be omitted or redacted if they would disclose hidden app-owner population details.
- `rows[]`: unified rows sorted by backend policy, each with `{ rowId, recordKind: admin_account|admin_membership|admin_invitation, displayName?, email?, status, roles[], invitationStatus?, deliveryStatus?, lastOwnerAdminRisk?, attentionBadges[], actionAvailability[], targetSurfaceId, targetActionId, traceRefs[], redactionState }`.
- `filters`: backend-authored safe options for text query, status, role, invitation status, attention state, and delivery/outbox state. Filter values must never include hidden counts or unsupported roles.
- `authorizedActions[]`: refresh/list, open invite form, open visible admin/detail row, open visible invitation detail row, open role/status task where backend exposes it, open app-owner audit evidence, and return to dashboard. Unavailable actions are omitted.
- `diagnosticMetadata` is role-gated and visually subordinate; default rows may show trace/evidence labels but not raw trace event ids unless the actor has audit/support scope.

Redaction and forbidden payload boundaries:

- Must not expose raw WorkOS ids, raw JWT/session values, invitation token/token hash, Resend/provider payload or secrets, hidden app-owner admin identities/counts, tenant/customer application data, support-access internals, billing authority, model/provider config, raw correlation/idempotency keys, or hidden capability/role lists.
- App-owner copy uses positive scoped language such as “SaaS Owner Admins visible to you” and “No SaaS Owner Admin invitations need attention.” Direct denied attempts use safe no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-saas-owner-admins` | `saas_owner.admin.list` / `manage-saas-owner-admins` | Reload this list with backend-shaped filters and branch metadata. |
| `action-open-saas-owner-admin-invitation-create` | `saas_owner.admin.invite` / `manage-saas-owner-admins` | Open `surface-user-admin-saas-owner-admin-invitation-create`; unavailable when last-owner or policy state blocks safe invite setup. |
| `action-open-saas-owner-admin-detail` | `saas_owner.admin.list` / `manage-saas-owner-admins` | Open the lifecycle-aware app-owner admin detail surface when implemented or map to `surface-user-admin-user-detail` with app-owner branch context; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-open-saas-owner-admin-invitation-detail` | `saas_owner.admin.list` / `manage-saas-owner-admins` | Open `surface-user-admin-invitation-detail` or a dedicated app-owner invitation detail with branch context; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-open-saas-owner-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` with focus on the SaaS Owner Admin population card. |

States and outcomes:

- Loading: skeleton list with selected SaaS Owner scope label only.
- Empty: no visible app-owner admins or invitations matching filters; show safe invite action only when authorized.
- Ready: rows and summaries are backend-derived and keyboard-operable; selecting a row opens its target surface/action.
- Submitting/searching: preserve current filters and focus while backend reauthorizes.
- Validation-error: invalid query/filter/sort is reported inline without running a broad search.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no app-owner population enumeration.
- Stale/conflict: refresh guidance when selected context, membership, invitation status, or list cursor changed.
- Partial-data/failure: show safe provider/outbox/readiness messages without fake counts or fixture data.

Trace, audit, and tests:

- Every load, filter, row open, invite-form open, denial, stale result, and audit drilldown emits/links an admin work trace with correlation id and selected `AuthContext` summary.
- Acceptance/regression coverage must verify dashboard-to-list traversal, list filtering and empty state, row/card activation to admin/invitation detail or mapped detail, invite-form open, SaaS Owner vs Tenant/Customer Admin authorization, hidden/stale target denial without enumeration, last-owner risk display, provider/outbox fail-closed indicators, audit/work trace/correlation links, frontend secret boundaries, keyboard row activation, focus return, and responsive list/card rendering.

Payloads for the app-owner graph must include selected SaaS Owner `AuthContext`, app-owner scope label, browser-safe account/invitation rows, role options limited to `SAAS_OWNER_ADMIN` and any explicitly safe app-owner auditor/support variants, last-owner-admin risk flags, safe actions, trace refs, correlation id, and redaction metadata. Payloads must not expose raw WorkOS/JWT/provider ids, invitation tokens, hidden app-owner counts, provider secrets, or tenant/customer application data.

Actions include list/search (`saas_owner.admin.list` / `manage-saas-owner-admins`), open invite (`saas_owner.admin.invite`), submit invite (`saas_owner.admin.invite`), open app-owner admin detail (`saas_owner.admin.list`), and manage app-owner admin roles/status (`saas_owner.admin.manage` through role preview or lifecycle confirmation). Denials include missing SaaS Owner context, missing capability, self-removal, last-owner-admin loss, role escalation outside app-owner-safe roles, duplicate/open invite, outbox/provider fail-closed, and hidden/not-found targets.

Surface-description sufficiency review: `surface-user-admin-saas-owner-admins` is sufficiently unambiguous for developers/generators to implement and review the list/search objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Downstream runtime realization may still need to add or map concrete app-owner admin detail/role/lifecycle surfaces for row destinations before claiming those separate surfaces fully implemented.

#### `surface-user-admin-saas-owner-admin-invitation-create` create-form contract

- Surface id: `surface-user-admin-saas-owner-admin-invitation-create`.
- Surface type: `create-form`.
- Surface contract: `user_admin.saas_owner_admin_invitation_create.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: child of the SaaS Owner Admin branch root, opened from `surface-user-admin-saas-owner-admins` by `action-open-saas-owner-admin-invitation-create`; return navigation uses `action-user-admin-show-saas-owner-admins` with label **Back to SaaS Owner Admins**.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, backend `saas_owner.admin.invite` capability, and active inviter account. Tenant Admin, Customer Admin, support-only, disabled-actor, stale selected context, missing membership, or policy-blocked attempts return `surface-user-admin-system-message` without revealing hidden app-owner population details.
- User goal: invite another trusted SaaS Owner Admin to administer the SaaS app itself. The form is single-purpose: it validates the invitee email/display name/reason, limits role selection to app-owner-safe roles, submits an idempotent invitation request, and routes success to a lifecycle-aware invitation detail/result surface. It never changes an existing membership inline and never exposes tenant/customer application data.

Frontend-safe payload for `user_admin.saas_owner_admin_invitation_create.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `form`: `{ emailDraft?, displayNameDraft?, reasonDraft?, targetRoleOptions[], selectedTargetRole, idempotencyKeyHint, submitLabel, cancelActionId, submitActionId, disabledReason? }`. `targetRoleOptions[]` defaults to `SAAS_OWNER_ADMIN` only unless a later policy explicitly adds a browser-safe app-owner auditor/support role; no tenant, customer, billing, or provider roles are offered.
- `policyContext`: `{ lastOwnerAdminRisk, duplicateOpenInvitePolicy, allowedEmailDomains?, maxPendingInviteCount?, approvalRequired?, traceRefs[] }` rendered as user-facing guidance rather than raw policy ids.
- `deliveryReadiness`: `{ outboxStatus, providerStatus, retryEligible, failClosedMessage?, traceRefs[] }`; provider/outbox failures are shown as blocked or retryable states and must not fabricate delivery success.
- `authorizedActions[]`: submit invite, validate draft, return to SaaS Owner Admins, open app-owner audit evidence, and open dashboard. Unavailable actions are omitted or represented by a safe disabled reason only when showing the reason does not reveal hidden policy/population facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include trace/evidence labels but not raw trace event ids, idempotency/correlation secrets, provider payloads, or outbox internals.

Redaction and forbidden payload boundaries:

- Must not expose invitation tokens/token hashes, raw WorkOS ids, raw JWT/session values, Resend/provider payloads or secrets, hidden app-owner admin identities/counts, hidden capability/role lists, tenant/customer application data, billing authority, model/provider config, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses scoped language such as “Invite a SaaS Owner Admin” and “This person will help administer the SaaS app.” Direct denied or stale attempts use `surface-user-admin-system-message` with no-enumeration recovery.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-saas-owner-admin-invitation-create` | `saas_owner.admin.invite` / `manage-saas-owner-admins` | Open this create form with backend-authored role options, policy context, delivery readiness, branch metadata, and trace refs. |
| `action-submit-saas-owner-admin-invitation` | `saas_owner.admin.invite` / `manage-saas-owner-admins` | Validate email/display name/reason/target role, enforce idempotency and duplicate/open-invite policy, create or reuse an app-owner invitation, enqueue delivery only when outbox/provider readiness is available, audit the attempt, then open `surface-user-admin-invitation-detail` or a dedicated app-owner invitation detail with branch context. Validation, duplicate, provider/outbox blocked, stale, approval-required, no-op, or denial results return this form with inline state or `surface-user-admin-system-message`; fake delivery success is forbidden. |
| `action-user-admin-show-saas-owner-admins` | `saas_owner.admin.list` / `manage-saas-owner-admins` | Return to `surface-user-admin-saas-owner-admins` with safe filter/context preservation. |
| `action-open-saas-owner-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` focused on the SaaS Owner Admin population card when authorized. |

States and outcomes:

- Loading: skeleton create form with selected SaaS Owner scope label and no role/options until backend authorizes them.
- Ready: backend-authored role options, boundary notice, policy context, delivery readiness, trace/correlation, and submit/return actions are visible.
- Draft validation-error: invalid email format, unsupported domain, missing reason when required, unsupported role, duplicate open invite, self-invite, disabled inviter, max pending invites, or last-owner-admin policy conflict are reported inline without creating or sending an invitation.
- Submitting: preserve form input and focus while backend reauthorizes selected context, role, duplicate state, provider/outbox readiness, and idempotency.
- Success: route to invitation detail/result with browser-safe invitation summary, delivery status, trace/correlation, and branch return metadata.
- Duplicate/no-op: show the existing visible invitation/detail when authorized, or a safe no-enumeration system message when the duplicate target is hidden.
- Provider/outbox blocked: return a blocked state or system message with recovery steps, trace refs, `noFakeSuccess`, and no invitation-token or provider payload exposure.
- Forbidden/hidden-not-found/stale/conflict: return `surface-user-admin-system-message` with no app-owner population enumeration.
- Partial-data/failure: keep draft safe for retry and show user-safe recovery without fixture/mock delivery status.

Trace, audit, and tests:

- Every open, draft validation, submit, duplicate/no-op, provider/outbox blocked result, denial, branch return, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, inviter account summary, redacted target email/domain, result surface id, and delivery/outbox readiness summary.
- Acceptance/regression coverage must verify SaaS Owner dashboard/list-to-create traversal, protected direct create-form load, successful app-owner invite submission and result routing, idempotent replay/duplicate-open-invite behavior, email/role/reason validation, outbox/provider fail-closed behavior, Tenant/Customer Admin authorization denial without enumeration, disabled/stale context denial, browser secret boundaries, trace/correlation links, keyboard form operation, focus return to list, and responsive create-form layout.
- Surface-description sufficiency review: `surface-user-admin-saas-owner-admin-invitation-create` is sufficiently unambiguous for developers/generators to implement and review the create-form objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove provider/outbox fail-closed behavior and real protected API/action paths before marking implementation/testing objectives done.

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
| Organization Admin list/search | `surface-user-admin-organization-admins` | `user_admin.organization_admins.v1` | Lists Organization Admin users and invitations for one selected Organization/Tenant. Every row/card is clickable and opens Organization Admin detail or invitation detail through backend-authored target routing. |
| Organization Admin invite/bootstrap | `surface-user-admin-organization-admin-invitation-create` | `user_admin.organization_admin_invitation_create.v1` | Owns first/admin invitation for a selected Organization with `TENANT_ADMIN` role validation, idempotency, outbox/Resend boundary, and success routing to detail. |
| Organization Admin show/inspection | `surface-user-admin-organization-admin-detail` | `user_admin.organization_admin_detail.v1` | Shows one Organization Admin membership or invitation and exposes task entry points for resend/revoke, role replacement, suspend/reactivate/remove, and audit evidence. It never exposes tenant application data. |

All graph nodes are owned by `user-admin-agent`. Reusable placements are limited to authorized User Admin/SaaS Owner shell contexts unless another workstream explicitly declares a reusable-by edge.

### Shared authority and language

- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, and backend `saas_owner.organization.list` / `saas_owner.organization.read` for directory/detail reads or `saas_owner.organization.create`, `saas_owner.organization.rename`, `saas_owner.organization.suspend`, and `saas_owner.organization.reactivate` for create/rename/suspend/reactivate. Implementation may map these product capabilities to internal Tenant services, but browser payloads, surface actions, tests, and denial copy use Organization capability language.
- Product/runtime language: browser copy, route labels, DTOs, and forms use **Organization**; backend enforcement, audit partitioning, and persisted isolation use **Tenant**.
- Forbidden payload content: tenant/customer application data, customer records, provider ids/secrets, raw billing provider state, raw JWT/session data, hidden Organization counts, support-access internals, unredacted audit evidence, or fields that would let a browser infer hidden tenant/customer existence.

### `surface-user-admin-organization-directory` list/search contract

- Surface id: `surface-user-admin-organization-directory`.
- Surface type: `list-search`.
- Surface contract: `user_admin.organization_directory.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch root opened from `surface-user-admin-dashboard` by `action-user-admin-show-organizations`, from visible SaaS Owner Organization attention/population cards, and from Organization-branch return actions. Branch descendants return through `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, and backend `saas_owner.organization.list` capability for directory reads. Tenant Admin, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, or missing-capability direct loads return `surface-user-admin-system-message` without revealing whether hidden Organizations, tenants, admins, invitations, customers, or counts exist.
- User goal: find visible customer-facing Organizations, understand lifecycle/readiness at a safe summary level, and open the lifecycle-aware Organization detail or a dedicated Organization create/admin task surface. The directory is discovery-only: filtering, paging, sorting, row/card activation, and create/admin entry points are allowed; inline Organization lifecycle, rename, admin invitation, role, status, billing, support-access, or tenant application-data mutation is forbidden.

Frontend-safe payload for `user_admin.organization_directory.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `query`, `filters`, `sort`, `pageInfo`, `emptyMessage`, `systemStates`, `forbiddenMessage`, and `lastResult`.
- `summary`: `{ visibleOrganizationCount, activeOrganizationCount, suspendedOrganizationCount, pendingSetupCount?, organizationAdminAttentionCount?, invitationAttentionCount?, providerBlockedCount?, outboxBlockedCount?, traceRefs[] }`. Counts are backend-authorized for SaaS Owner scope and are omitted/redacted when they would disclose hidden tenants, customers, admins, invitations, or lifecycle facts.
- `organizations[]`: backend-ordered rows/cards with `{ organizationId, organizationName, status, updatedAt?, safeLifecycleSummary?, visibleTenantAdminCount?, pendingAdminInvitationCount?, attentionBadges[], actionAvailability[], targetSurfaceId: 'surface-user-admin-organization-detail', openActionId: 'action-organization-read', traceRefs[], redactionState }`. Row/card activation always submits the backend-authored `openActionId`; frontend status labels must not infer lifecycle eligibility or hidden routing.
- `filters`: backend-authored safe options for text query, lifecycle status, admin-attention state, invitation state, setup/readiness state, and visible attention badges. Filter values must not expose hidden counts, unsupported lifecycle states, tenant/customer identifiers, or role/capability lists outside SaaS Owner scope.
- `authorizedActions[]`: refresh/search, open visible Organization detail row/card, open Organization create form, open Organization Admins for a selected visible Organization when available, open authorized audit evidence, and return to dashboard. Unavailable actions are omitted; disabled reasons are shown only when they do not reveal hidden Organization or policy facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default rows may show trace/evidence labels but not raw event ids, raw correlation/idempotency values, provider payloads, tenant internals, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts within an Organization, hidden Organization identities/counts, raw WorkOS/provider ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, billing provider state, support-access internals, raw model/provider config, hidden roles/capabilities, cross-tenant facts, or unredacted audit evidence.
- User-facing copy uses positive SaaS Owner language such as “Organizations visible to you” and “No Organizations need attention.” Empty visible scopes show safe zero/empty copy; direct denied or stale attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Reload this directory with backend-shaped filters, pagination, branch metadata, trace refs, and selected SaaS Owner scope summary. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Open `surface-user-admin-organization-detail` for a visible Organization; hidden, stale, disabled-actor, cross-scope, or missing-capability targets return `surface-user-admin-system-message` without enumerating hidden Organizations. |
| `action-open-organization-create` | `saas_owner.organization.create` / `manage-organizations` | Open `surface-user-admin-organization-create` with boundary notice, validation rules, idempotency guidance, trace refs, and branch return metadata. |
| `action-user-admin-show-organization-admins` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Open `surface-user-admin-organization-admins` for the selected visible Organization when backend exposes it; hidden/stale targets return a safe system message. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` with focus on the originating Organization branch card or attention queue. |

States and outcomes:

- Loading: skeleton directory with selected SaaS Owner scope label and no rows until backend authorization completes.
- Empty: no visible Organizations match the filters; show create action only when `saas_owner.organization.create` is authorized.
- Ready: backend-derived rows, summaries, filters, branch metadata, trace/correlation, and keyboard-operable row/card actions are visible.
- Searching/submitting: preserve current filters and focus while backend reauthorizes selected context and query.
- Validation-error: invalid query/filter/page/sort is reported inline without broadening the search.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Organization, tenant, customer, admin, or invitation enumeration.
- Stale/conflict: refresh guidance when selected context, Organization lifecycle, row target, or page cursor changed.
- Partial-data/failure: show safe provider/outbox/readiness messages without fake Organizations, hidden counts, fixture data, or provider internals.

Trace, audit, accessibility, and tests:

- Every load, search/filter, page/sort, row open, create-form open, Organization Admins open, denied hidden target, stale result, branch return, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, safe query/filter summary, redaction summary, and result surface id.
- Accessibility/responsive expectations: list rows are keyboard-operable, cards preserve the same backend action ids as table rows, focus returns to the originating dashboard card or list row after navigation, responsive layout does not hide action availability or redaction notices, and empty/error states remain announced to assistive tech.
- Acceptance/regression coverage must verify dashboard-to-Organization-directory traversal, scoped list filtering/search/empty state, row activation to Organization detail, create-form open, Organization Admins open for a visible Organization, SaaS Owner vs Tenant Admin vs Customer Admin authorization behavior, hidden/cross-scope denial without enumeration, stale row recovery, provider/outbox fail-closed indicators, audit/work trace/correlation links, frontend secret boundaries, keyboard row activation, focus return, and responsive table-to-card rendering.

Surface-description sufficiency review: `surface-user-admin-organization-directory` is sufficiently unambiguous for developers/generators to implement and review the list/search objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Organization row routing, SaaS Owner scope authorization, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-detail` show/inspection contract

- Surface id: `surface-user-admin-organization-detail`.
- Surface type: `show-inspection` and Organization task-router; it must not perform inline rename, suspend, reactivate, admin-invitation, role/status, support, billing, or tenant application-data mutations.
- Surface contract: `user_admin.organization_detail.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-directory` rows/cards, SaaS Owner Organization attention cards, create/rename/lifecycle result routing, and authorized deep links by `action-organization-read`; branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, and backend `saas_owner.organization.read` capability for the requested Organization/Tenant boundary. Tenant Admin, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden Organization, cross-scope, or missing-capability direct loads return `surface-user-admin-system-message` without revealing whether hidden Organizations, tenants, admins, invitations, customers, or counts exist.
- User goal: inspect one visible Organization, understand its lifecycle and administration readiness at a browser-safe level, and choose a dedicated task surface for Organization rename, suspend/reactivate, Organization Admin list/invite/detail, return-to-directory, or authorized audit evidence. The detail surface is inspection-first and action-routing-only.

Frontend-safe payload for `user_admin.organization_detail.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `organizationDetail`: `{ organizationId, organizationName, status, safeLifecycleSummary, lifecycleFreshness?, setupReadiness?, safeBoundaryNotice, visibleActions[], traceRefs[], correlationId }`. The default view may mention that the Organization is backed by an isolated Tenant boundary, but must not expose tenant application data, raw tenant internals, customer records, provider ids/secrets, billing provider state, hidden counts, or raw policy ids.
- `adminSummary`: visible SaaS Owner-safe Organization Admin totals, pending invitation summary, first-admin/bootstrap eligibility, last-admin risk summary, provider/outbox readiness summary, and target action ids for Organization Admin list or invite task surfaces. Counts are omitted or redacted when they would disclose hidden admins, invitations, customers, or tenant facts.
- `lifecycleTaskSummary`: rename eligibility, suspend eligibility, reactivate eligibility, policy/approval/reason/idempotency summaries, stale/conflict markers, no-op expectations, and target action ids. It summarizes task availability only; mutations occur only on dedicated rename or lifecycle confirmation surfaces.
- `auditSummary`: recent browser-safe Organization administration events, denial/no-op excerpts, trace labels, redaction notes, and role-gated audit drilldown action ids. Default view must not show raw event ids, raw correlation/idempotency values, provider payloads, tenant internals, or unredacted audit evidence.
- `availableTaskActions[]`: backend-authored action id, label, purpose, governed capability/tool, target surface id/type, target object type/id when safe, enabled/disabled state, disabled reason only when it does not disclose hidden policy or Organization facts, required confirmation/approval/reason/idempotency summary, and expected result surface.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Reload this inspection surface with current Organization summary, branch metadata, trace refs, and no-enumeration redaction; hidden, stale, disabled-actor, cross-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filters, branch metadata, and focus hint. |
| `action-open-organization-rename` | `saas_owner.organization.rename` / `manage-organizations` | Open `surface-user-admin-organization-rename` with current display-name state, validation rules, idempotency guidance, and trace refs; no inline rename occurs on detail. |
| `action-open-organization-suspend` (compatibility alias: `action-open-organization-suspend-confirmation`) | `saas_owner.organization.suspend` / `manage-organizations` | Open `surface-user-admin-organization-suspend-confirmation` when lifecycle state allows suspension; unavailable/hidden/stale targets return a safe system message. |
| `action-open-organization-reactivate-confirmation` | `saas_owner.organization.reactivate` / `manage-organizations` | Open `surface-user-admin-organization-reactivate-confirmation` when lifecycle state allows reactivation; unavailable/hidden/stale targets return a safe system message. |
| `action-user-admin-show-organization-admins` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Open `surface-user-admin-organization-admins` for the selected visible Organization or return safe hidden/not-found/forbidden recovery. |
| `action-open-organization-admin-invitation-create` | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Open `surface-user-admin-organization-admin-invitation-create` with Organization boundary, target-role options, outbox/provider readiness, and branch return metadata. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected SaaS Owner scope and Organization placeholder only after backend authorization starts; no hidden identifiers or counts are rendered while authorization is unresolved.
- Ready: Organization identity, lifecycle, admin readiness, task actions, branch return, trace refs, correlation, and redaction notices are backend-derived.
- Empty/limited: Organization is visible but has no Organization Admins, invitations, lifecycle alerts, or audit excerpts; show safe absence copy and authorized task entry points only.
- Submitting/opening task: preserve detail context and focus while backend reauthorizes the target action.
- Validation-error: malformed Organization id, selected context, task target, or action payload is reported without broadening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Organization, tenant, customer, admin, invitation, count, role, or capability enumeration.
- Conflict/stale: selected context, Organization lifecycle, policy eligibility, or admin readiness changed; show refresh/back-to-organizations recovery and trace refs.
- Provider/outbox fail-closed: show provider/outbox readiness summaries only for visible admin-invitation task areas; never invent fixture success or expose provider internals.
- No-op/success: detail refresh and task-open no-ops are traceable; consequential success is shown by the dedicated result/detail surface after the task surface completes.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every detail load, branch return, task-open, and audit drilldown is evaluated against the selected backend SaaS Owner/App Admin `AuthContext`; Tenant Admins and Customer Admins cannot open Organization detail through this surface graph, support-only access is non-authoritative, disabled/missing actors receive safe recovery, and no browser payload contains tenant application data or hidden customer/Organization facts.
- Trace/audit contract: each detail load, denied hidden target, branch return, task-open, audit-open, stale result, no-op, and provider/outbox blocked result emits or links an admin work trace with actor account, selected context, Organization summary only when visible, action id, capability decision, result surface, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, tenant internals, or raw correlation/idempotency mechanics.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope boundary, Organization summary sections with semantic headings, task actions as keyboard-operable grouped controls, status/denial messages announced as status text, focus return to the originating row or first result message, and responsive stacking in Organization summary, admin readiness, lifecycle tasks, and audit order without losing action availability or redaction notices.
- Acceptance/regression coverage must verify Organization-directory-to-detail traversal, direct protected detail load, hidden/cross-scope denial without enumeration, SaaS Owner/App Admin authorization and Tenant/Customer Admin denial, branch return, rename/suspend/reactivate/Admin-list/Admin-invite task entry routing, stale/conflict recovery, provider/outbox fail-closed summaries for admin invitation tasks, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus return, and responsive rendering.

Surface-description sufficiency review: `surface-user-admin-organization-detail` is sufficiently unambiguous for developers/generators to implement and review the show/inspection objective without inventing payload fields, task actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived task routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, trace/correlation, provider/outbox fail-closed boundaries, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-create` create-form contract

- Surface id: `surface-user-admin-organization-create`.
- Surface type: `create-form`.
- Surface contract: `user_admin.organization_create.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-directory`, `surface-user-admin-organization-detail`, or a visible dashboard Organization action by `action-open-organization-create`; success routes to `surface-user-admin-organization-detail`; branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, backend `saas_owner.organization.create` capability, and an actor that is not disabled, stale, support-only, Tenant Admin, or Customer Admin. Unauthorized or stale direct loads and submissions return `surface-user-admin-system-message` without revealing hidden Organizations, tenants, customers, admins, invitations, counts, roles, capabilities, provider state, or policy internals.
- User goal: create a new customer-facing Organization backed by an isolated Tenant boundary, with safe Organization-language copy, backend-authored validation rules, idempotent submission, audit/work trace evidence, and an immediate route to the created Organization detail. The form never exposes tenant application data, billing/provider internals, customer records, or Organization Admin bootstrap shortcuts as inline mutations.

Frontend-safe payload for `user_admin.organization_create.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `form`: `{ organizationNameDraft?, reasonDraft?, submitLabel, submitActionId: 'action-submit-organization-create', cancelActionId: 'action-user-admin-show-organizations', idempotencyKeyHint, disabledReason? }`. The default form asks only for the Organization display name and audit reason unless a later policy explicitly adds browser-safe fields; the browser must not infer tenant identifiers, lifecycle state, Organization Admin roles, customer setup, billing details, or provider configuration.
- `validationPolicy`: `{ organizationNameRules, reasonRequired, duplicateNamePolicy, maxLength?, allowedCharacterSummary?, idempotentReplayPolicy, traceRefs[] }` rendered as user-facing guidance rather than raw policy ids.
- `creationBoundary`: `{ createsIsolatedTenantBoundary: true, initialLifecycleStatus: 'active' | 'pending_setup', tenantApplicationDataExcluded: true, organizationAdminBootstrapSeparateSurfaceId: 'surface-user-admin-organization-admin-invitation-create', traceRefs[] }`; default copy explains the Organization/Tenant isolation boundary without exposing internal Tenant records.
- `authorizedActions[]`: open/reload create form, submit create, validate draft, return to Organization Directory, open authorized Organization audit evidence, and return to dashboard. Unavailable actions are omitted or represented with safe disabled copy only when it does not disclose hidden policy or Organization facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include trace/evidence labels but not raw trace event ids, raw idempotency/correlation values, raw provider ids, backend component names, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts, hidden Organization identities/counts, raw tenant ids unless explicitly policy-safe, raw WorkOS/provider ids, raw JWT/session values, billing provider state, invitation tokens/token hashes, support-access internals, raw model/provider config, hidden roles/capabilities, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses positive SaaS Owner language such as “Create Organization” and “Set up an isolated Organization boundary.” Validation and denial copy describes visible recovery steps without enumerating unavailable Organizations, tenant internals, or missing privileged capabilities.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-organization-create` | `saas_owner.organization.create` / `manage-organizations` | Open or reload this create form with selected SaaS Owner scope, backend-authored validation policy, boundary notice, idempotency guidance, trace refs, and branch metadata. |
| `action-submit-organization-create` | `saas_owner.organization.create` / `manage-organizations` | Validate Organization name and reason, enforce selected AuthContext, duplicate/open-name policy, idempotency/correlation, and disabled/stale actor checks, create or reuse the Organization/Tenant boundary when authorized, audit the attempt, then open `surface-user-admin-organization-detail` for the visible result. Validation-error, duplicate/no-op, idempotent replay, conflict/stale, provider/runtime blocked, approval-required, forbidden, or hidden results return this form with inline state or `surface-user-admin-system-message`; fake success is forbidden. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filters, focus restoration, trace refs, and no hidden Organization counts. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` focused on the Organization branch or originating attention queue when authorized. |

States and outcomes:

- Loading: skeleton create form with selected SaaS Owner scope label and boundary notice only after backend authorization starts; no hidden Organizations, tenant ids, customer facts, provider data, or policy internals render while authorization is unresolved.
- Ready: backend-authored draft fields, validation policy, boundary copy, submit/return actions, trace refs, correlation id, redaction, style/catalog bindings, and keyboard-operable controls are visible.
- Draft validation-error: missing/invalid Organization name, unsupported characters/length, missing reason when required, duplicate visible Organization name, disabled actor, stale selected context, or policy gate is reported inline without creating a Tenant boundary or broadening search scope.
- Submitting: preserve draft input and focus while backend reauthorizes selected context, create capability, duplicate state, idempotency replay, and policy/runtime readiness.
- Success: route to `surface-user-admin-organization-detail` with the created visible Organization summary, `lastResult`, trace/correlation refs, and branch return metadata; Organization Admin bootstrap remains a separate task surface.
- Duplicate/no-op/idempotent replay: route to the visible existing/created Organization detail when authorized, or return safe no-enumeration recovery when the duplicate target is hidden.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` without hidden Organization, tenant, customer, admin, invitation, role, capability, count, provider, or policy enumeration.
- Conflict/stale/partial-data/failure: keep the draft safe for retry, show refresh/back-to-organizations recovery, and never fabricate Organization creation, provider readiness, audit evidence, or fixture data.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every create-form load, draft validation, submit, branch return, and audit drilldown is evaluated against the selected backend SaaS Owner/App Admin `AuthContext` and app-owner membership. Tenant Admins, Customer Admins, support-only actors, disabled actors, stale contexts, missing memberships, and missing capabilities receive safe recovery; no browser payload contains tenant/customer app data, hidden Organization facts, hidden capabilities, raw provider data, or raw JWT/session values.
- Trace/audit contract: each open, submit attempt, validation failure, duplicate/no-op, idempotent replay, success, denial, stale/conflict result, branch return, and audit drilldown emits or links an admin work trace with actor account, selected context summary, redacted draft Organization name/reason, action id, capability decision, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw event ids, raw correlation/idempotency mechanics, provider secrets, tenant internals, hidden roles, hidden Organization facts, or unredacted audit evidence.
- Accessibility/responsive expectations: the form has a stable heading, selected-scope and Organization/Tenant boundary notice, semantic field/error/help text associations, keyboard-operable submit/cancel/audit controls, announced validation/submission/result messages, focus return to the originating Organization Directory action or first result message, and responsive stacking that preserves boundary, validation, trace, and redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, duplicate state, tenant identifiers, lifecycle status, Organization Admin options, provider readiness, or result routing from client-local labels/status.
- Acceptance/regression coverage must verify dashboard/directory/detail-to-create traversal, protected direct create-form load, successful Organization creation and detail result routing, idempotent replay/no-op behavior, duplicate visible-name handling, validation errors, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, disabled/stale context denial, hidden/cross-scope duplicate no-enumeration, audit/work trace/correlation links, browser secret boundaries, keyboard form operation, focus return to Organization Directory, responsive rendering, and separation of Organization creation from Organization Admin bootstrap.

Surface-description sufficiency review: `surface-user-admin-organization-create` is sufficiently unambiguous for developers/generators to implement and review the create-form objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived validation/result routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, idempotent replay/duplicate handling, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-rename` edit-form contract

- Surface id: `surface-user-admin-organization-rename`.
- Surface type: `edit-form`.
- Surface contract: `user_admin.organization_rename.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-detail` by `action-open-organization-rename`; success, no-op, idempotent replay, and stale/conflict recovery route back to `surface-user-admin-organization-detail` or `surface-user-admin-system-message` as described below. Branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints; local detail return uses `action-organization-read`.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, a visible target Organization/Tenant boundary, backend `saas_owner.organization.rename` capability, and an actor that is not disabled, stale, support-only, Tenant Admin, or Customer Admin. Unauthorized, hidden, cross-scope, stale, missing-membership, or missing-capability direct loads/submissions return `surface-user-admin-system-message` without revealing whether hidden Organizations, tenants, customers, admins, invitations, counts, roles, capabilities, provider state, or policy internals exist.
- User goal: safely edit the browser-facing Organization display name for one visible Organization, with validation, change preview, idempotent submission, audit/work trace evidence, and a clear route back to Organization detail. The form changes only the Organization display label; it never exposes or edits tenant internals, customer records, lifecycle status, Organization Admin roles, billing, provider settings, support access, or application data.

Frontend-safe payload for `user_admin.organization_rename.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `organizationId`, `organizationName`, `organizationStatus`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `detailReturnActionId: 'action-organization-read'`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `form`: `{ currentOrganizationName, proposedOrganizationNameDraft?, reasonDraft?, submitLabel, submitActionId: 'action-submit-organization-rename', cancelActionId: 'action-organization-read', idempotencyKeyHint, freshnessVersionHint?, disabledReason? }`. The form asks only for the new Organization display name and audit reason when policy requires it; the browser must not infer or edit tenant identifiers, lifecycle status, Organization Admin options, Customer data, billing details, provider configuration, or hidden policy values.
- `validationPolicy`: `{ organizationNameRules, reasonRequired, noOpPolicy, duplicateNamePolicy, maxLength?, allowedCharacterSummary?, freshnessRequired, idempotentReplayPolicy, traceRefs[] }` rendered as user-facing guidance rather than raw policy ids.
- `changePreview`: `{ currentDisplayName, proposedDisplayName?, visibleImpactSummary, tenantApplicationDataExcluded: true, customersUnaffectedByRename: true, traceRefs[] }`; default copy explains that the rename updates the Organization label only and does not disclose tenant internals or customer application data.
- `authorizedActions[]`: open/reload rename form, validate draft, submit rename, return to Organization detail, return to Organization Directory, open authorized Organization audit evidence, and return to dashboard. Unavailable actions are omitted or represented with safe disabled copy only when it does not disclose hidden Organization, tenant, admin, customer, policy, provider, or capability facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include evidence labels but not raw trace event ids, raw correlation/idempotency values, raw provider ids, backend component names, tenant internals, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts, hidden Organization identities/counts, raw tenant ids unless explicitly policy-safe, raw WorkOS/provider ids, raw JWT/session values, billing provider state, invitation tokens/token hashes, support-access internals, raw model/provider config, hidden roles/capabilities, raw idempotency keys, unredacted audit evidence, or hidden duplicate-name targets.
- User-facing copy uses positive SaaS Owner language such as “Rename Organization” and “Update the display name shown to authorized admins.” Validation, stale, no-op, and denial copy describes visible recovery steps without enumerating unavailable Organizations, tenant internals, hidden duplicates, or missing privileged capabilities.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-organization-rename` | `saas_owner.organization.rename` / `manage-organizations` | Open or reload this edit form with selected SaaS Owner scope, visible Organization summary, current display-name state, validation policy, idempotency/freshness guidance, trace refs, and branch metadata. |
| `action-submit-organization-rename` | `saas_owner.organization.rename` / `manage-organizations` | Validate Organization visibility/lifecycle, proposed display name, reason requirement, selected `AuthContext`, stale/freshness state, duplicate-name policy, idempotency/correlation, and disabled actor checks; audit the attempt; update the visible Organization display name when authorized; then open `surface-user-admin-organization-detail` with `lastResult`. Validation-error, duplicate-visible, hidden-duplicate, no-op, idempotent replay, conflict/stale, approval-required, forbidden, hidden, or runtime-blocked results return this form with inline state or `surface-user-admin-system-message`; fake rename success is forbidden. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Return to `surface-user-admin-organization-detail` for the selected visible Organization with backend-owned context, focus restoration, trace refs, and no hidden Organization/customer enumeration. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filters, focus restoration, trace refs, and no hidden Organization counts. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` focused on the Organization branch or originating attention queue when authorized. |

States and outcomes:

- Loading: skeleton edit form with selected SaaS Owner scope and Organization placeholder only after backend authorization starts; no hidden Organizations, tenant ids, customer facts, duplicate targets, provider data, or policy internals render while authorization is unresolved.
- Ready: backend-authored current name, draft field, validation policy, change preview, submit/return actions, trace refs, correlation id, redaction, style/catalog bindings, and keyboard-operable controls are visible.
- Draft validation-error: missing/invalid Organization name, unsupported characters/length, unchanged name, missing reason when required, duplicate visible Organization name, disabled actor, stale selected context, stale Organization version, or policy gate is reported inline without changing the Organization or broadening search scope.
- Submitting: preserve draft input and focus while backend reauthorizes selected context, Organization visibility, rename capability, stale/freshness state, duplicate state, idempotency replay, and policy/runtime readiness.
- Success: route to `surface-user-admin-organization-detail` with the updated visible Organization summary, `lastResult`, trace/correlation refs, and branch return metadata.
- No-op/idempotent replay: route to refreshed detail when the requested name already matches or the replayed operation is visible and authorized; otherwise return safe no-enumeration recovery.
- Duplicate/hidden-duplicate: visible duplicates are reported as validation/conflict with safe recovery; hidden duplicates return no-enumeration recovery without identifying the hidden Organization.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` without hidden Organization, tenant, customer, admin, invitation, role, capability, count, provider, duplicate, or policy enumeration.
- Conflict/stale/partial-data/failure: keep the draft safe for retry, show refresh/back-to-Organization recovery, and never fabricate Organization rename, audit evidence, or fixture data.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every rename-form load, draft validation, submit, branch return, detail return, and audit drilldown is evaluated against the selected backend SaaS Owner/App Admin `AuthContext`, active app-owner membership, visible Organization/Tenant boundary, and `saas_owner.organization.rename` capability. Tenant Admins, Customer Admins, support-only actors, disabled actors, stale contexts, missing memberships, hidden/cross-scope Organizations, and missing capabilities receive safe recovery; no browser payload contains tenant/customer app data, hidden Organization facts, hidden capabilities, raw provider data, or raw JWT/session values.
- Trace/audit contract: each open, submit attempt, validation failure, no-op, duplicate-visible, hidden-duplicate, idempotent replay, success, denial, stale/conflict result, branch/detail return, and audit drilldown emits or links an admin work trace with actor account, selected context summary, visible Organization summary, redacted proposed display name/reason, action id, capability decision, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw event ids, raw correlation/idempotency mechanics, provider secrets, tenant internals, hidden roles, hidden Organization facts, or unredacted audit evidence.
- Accessibility/responsive expectations: the edit form has a stable heading, selected-scope and Organization/Tenant boundary notice, semantic current/proposed-name fields, error/help text associations, keyboard-operable submit/cancel/audit controls, announced validation/submission/result messages, focus return to the originating Organization detail task or first result message, and responsive stacking that preserves boundary, validation, change preview, trace, and redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, duplicate state, freshness, tenant identifiers, lifecycle status, provider readiness, or result routing from client-local labels/status.
- Acceptance/regression coverage must verify Organization-detail-to-rename traversal, protected direct rename-form load, successful Organization rename and detail result routing, idempotent replay/no-op behavior, duplicate visible-name handling, hidden-duplicate no-enumeration, validation errors, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, disabled/stale context denial, hidden/cross-scope Organization denial, audit/work trace/correlation links, browser secret boundaries, keyboard form operation, focus return to Organization detail and Organization Directory, responsive rendering, and separation of Organization rename from lifecycle, Organization Admin, Customer, billing, provider, and tenant application-data changes.

Surface-description sufficiency review: `surface-user-admin-organization-rename` is sufficiently unambiguous for developers/generators to implement and review the edit-form objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived validation/result routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, idempotent replay/no-op and duplicate/stale handling, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-suspend-confirmation` destructive lifecycle contract

- Surface id: `surface-user-admin-organization-suspend-confirmation`.
- Surface type: `destructive-lifecycle-confirmation`.
- Surface contract: `user_admin.organization_suspend_confirmation.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-detail` by `action-open-organization-suspend`; compatibility alias `action-open-organization-suspend-confirmation` may reopen the same surface while existing clients are normalized. Successful suspension, no-op, idempotent replay, and stale/conflict recovery route back to `surface-user-admin-organization-detail` or `surface-user-admin-system-message` as described below. Branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints; local detail return uses `action-organization-read`.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, a visible active Organization/Tenant boundary, backend `saas_owner.organization.suspend` capability, and an actor that is not disabled, stale, support-only, Tenant Admin, or Customer Admin. Unauthorized, hidden, cross-scope, stale, already-suspended, missing-membership, missing-capability, or policy-blocked direct loads/submissions return `surface-user-admin-system-message` or this confirmation with safe disabled state without revealing whether hidden Organizations, tenants, customers, admins, invitations, counts, roles, capabilities, provider state, billing state, or policy internals exist.
- User goal: confirm a consequential Organization suspension before changing the Organization/Tenant lifecycle boundary, understand the user-visible consequences, provide an audit reason, submit idempotently, and return to Organization detail with traceable outcome evidence. The surface never exposes or mutates tenant application data, customer records, billing/provider settings, Organization Admin roles, support access, or raw Tenant internals.

Frontend-safe payload for `user_admin.organization_suspend_confirmation.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `organizationId`, `organizationName`, `currentStatus`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `detailReturnActionId: 'action-organization-read'`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `confirmation`: `{ consequenceCopy, confirmationPhrase?, confirmationRequired: true, reasonDraft?, reasonRequired: true, submitLabel, submitActionId: 'action-organization-suspend', cancelActionId: 'action-organization-read', idempotencyKeyHint, freshnessVersionHint?, disabledReason? }`. Default copy explains that suspension changes the Organization/Tenant lifecycle boundary and may pause access to Organization-scoped administration, but does not delete the Organization, expose tenant application data, alter customer records, remove Organization Admins, or change billing/provider configuration unless a later policy explicitly describes those consequences.
- `suspensionEligibility`: `{ currentStatus, allowedStatuses, blockedReason?, lastAdminOrBootstrapRisk?, openCustomerOrAdminWorkWarning?, providerOrOutboxReadinessSummary?, approvalRequired?, traceRefs[] }` rendered as user-facing guidance rather than raw policy ids. Eligibility is backend-authored; the frontend must not infer suspension availability from status labels.
- `authorizedActions[]`: open/reload suspend confirmation, validate confirmation/reason, submit suspension, return to Organization detail, return to Organization Directory, open authorized Organization audit evidence, and return to dashboard. Unavailable actions are omitted or represented with safe disabled copy only when it does not disclose hidden Organization, tenant, admin, customer, policy, provider, billing, or capability facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include evidence labels but not raw trace event ids, raw correlation/idempotency values, raw provider ids, backend component names, tenant internals, billing provider state, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts, hidden Organization identities/counts, raw tenant ids unless explicitly policy-safe, raw WorkOS/provider ids, raw JWT/session values, billing provider state, invitation tokens/token hashes, support-access internals, raw model/provider config, hidden roles/capabilities, raw idempotency keys, unredacted audit evidence, or hidden policy predicates.
- User-facing copy uses clear SaaS Owner language such as “Suspend Organization” and “Confirm Organization suspension.” Validation, stale, no-op, and denial copy describes visible recovery steps without enumerating unavailable Organizations, tenant internals, hidden blockers, or missing privileged capabilities.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-organization-suspend` (compatibility alias: `action-open-organization-suspend-confirmation`) | `saas_owner.organization.suspend` / `manage-organizations` | Open or reload this confirmation with selected SaaS Owner scope, visible Organization summary, lifecycle/consequence copy, reason and confirmation requirements, idempotency/freshness guidance, trace refs, and branch metadata. Hidden, stale, disabled-actor, cross-scope, unsupported-status, or missing-capability targets return `surface-user-admin-system-message` without enumeration. |
| `action-organization-suspend` | `saas_owner.organization.suspend` / `manage-organizations` | Validate Organization visibility/lifecycle, confirmation phrase if required, reason, selected `AuthContext`, stale/freshness state, idempotency/correlation, approval gate, and disabled actor checks; audit the attempt; suspend the visible Organization/Tenant lifecycle boundary when authorized; then open `surface-user-admin-organization-detail` with `lastResult`. Validation-error, already-suspended/no-op, idempotent replay, conflict/stale, approval-required, forbidden, hidden, policy-blocked, or runtime-blocked results return this confirmation with inline state or `surface-user-admin-system-message`; fake suspension success is forbidden. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Cancel or return to `surface-user-admin-organization-detail` for the selected visible Organization with backend-owned context, focus restoration, trace refs, and no hidden Organization/customer enumeration. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filters, focus restoration, trace refs, and no hidden Organization counts. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` focused on the Organization branch or originating attention queue when authorized. |

States and outcomes:

- Loading: skeleton confirmation with selected SaaS Owner scope and Organization placeholder only after backend authorization starts; no hidden Organizations, tenant ids, customer facts, blockers, provider data, billing data, or policy internals render while authorization is unresolved.
- Ready: backend-authored Organization summary, lifecycle/consequence copy, reason and confirmation controls, eligibility/warnings, submit/return actions, trace refs, correlation id, redaction, style/catalog bindings, and keyboard-operable controls are visible.
- Validation-error: missing reason, missing/incorrect confirmation phrase, unsupported current status, stale selected context, stale Organization version, approval-required state, disabled actor, or policy gate is reported inline without changing the Organization or broadening scope.
- Submitting: preserve confirmation/reason input and focus while backend reauthorizes selected context, Organization visibility, suspend capability, lifecycle/freshness state, approval gate, idempotency replay, and policy/runtime readiness.
- Success: route to `surface-user-admin-organization-detail` with suspended lifecycle status, `lastResult`, trace/correlation refs, and branch return metadata.
- No-op/idempotent replay: route to refreshed detail when the Organization is already suspended or the replayed suspension is visible and authorized; otherwise return safe no-enumeration recovery.
- Approval-required/policy-blocked: show a safe decision or system-message result with recovery and audit evidence; do not claim suspension or expose raw policy predicates.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` without hidden Organization, tenant, customer, admin, invitation, role, capability, count, provider, billing, blocker, or policy enumeration.
- Conflict/stale/partial-data/failure: keep the confirmation safe for retry, show refresh/back-to-Organization recovery, and never fabricate Organization suspension, audit evidence, provider readiness, or fixture data.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every confirmation load, submit, branch return, detail return, and audit drilldown is evaluated against the selected backend SaaS Owner/App Admin `AuthContext`, active app-owner membership, visible Organization/Tenant boundary, lifecycle eligibility, and `saas_owner.organization.suspend` capability. Tenant Admins, Customer Admins, support-only actors, disabled actors, stale contexts, missing memberships, hidden/cross-scope Organizations, unsupported lifecycle states, and missing capabilities receive safe recovery; no browser payload contains tenant/customer app data, hidden Organization facts, hidden capabilities, raw provider data, raw billing state, or raw JWT/session values.
- Trace/audit contract: each open, submit attempt, validation failure, no-op, idempotent replay, success, approval-required, policy-blocked, denial, stale/conflict result, branch/detail return, and audit drilldown emits or links an admin work trace with actor account, selected context summary, visible Organization summary, redacted reason/confirmation state, action id, capability decision, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw event ids, raw correlation/idempotency mechanics, provider secrets, tenant internals, hidden roles, hidden Organization facts, billing provider internals, or unredacted audit evidence.
- Accessibility/responsive expectations: the confirmation has a stable heading, selected-scope and Organization/Tenant boundary notice, clearly announced destructive consequence copy, semantic reason/confirmation fields, error/help text associations, keyboard-operable submit/cancel/audit controls, announced validation/submission/result messages, focus return to the originating Organization detail task or first result message, and responsive stacking that preserves boundary, consequence, validation, trace, and redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, lifecycle eligibility, blockers, tenant identifiers, provider/billing readiness, or result routing from client-local labels/status.
- Acceptance/regression coverage must verify Organization-detail-to-suspend traversal, protected direct suspend-confirmation load, successful Organization suspension and detail result routing, idempotent replay/no-op behavior, required reason/confirmation validation, unsupported-status and stale/conflict handling, approval-required or policy-blocked recovery where configured, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, disabled/stale context denial, hidden/cross-scope Organization denial, audit/work trace/correlation links, browser secret boundaries, keyboard form operation, focus return to Organization detail and Organization Directory, responsive rendering, and separation of Organization suspension from Organization Admin role/status, Customer data, billing/provider settings, support access, and tenant application-data changes.

Surface-description sufficiency review: `surface-user-admin-organization-suspend-confirmation` is sufficiently unambiguous for developers/generators to implement and review the destructive-lifecycle objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived eligibility/result routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, idempotent replay/no-op and stale/policy-blocked handling, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-reactivate-confirmation` lifecycle confirmation contract

- Surface id: `surface-user-admin-organization-reactivate-confirmation`.
- Surface type: `lifecycle-confirmation`.
- Surface contract: `user_admin.organization_reactivate_confirmation.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-detail` by `action-open-organization-reactivate-confirmation`. Successful reactivation, no-op, idempotent replay, and stale/conflict recovery route back to `surface-user-admin-organization-detail` or `surface-user-admin-system-message` as described below. Branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints; local detail return uses `action-organization-read`.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, a visible suspended/inactive Organization/Tenant boundary eligible for reactivation, backend `saas_owner.organization.reactivate` capability, and an actor that is not disabled, stale, support-only, Tenant Admin, or Customer Admin. Unauthorized, hidden, cross-scope, stale, already-active, missing-membership, missing-capability, or policy-blocked direct loads/submissions return `surface-user-admin-system-message` or this confirmation with safe disabled state without revealing whether hidden Organizations, tenants, customers, admins, invitations, counts, roles, capabilities, provider state, billing state, or policy internals exist.
- User goal: confirm reactivation of one visible Organization lifecycle boundary, understand the safe user-facing effect of returning the Organization to active administration, provide an audit reason when policy requires one, submit idempotently, and return to Organization detail with traceable outcome evidence. The surface never exposes or mutates tenant application data, customer records, billing/provider settings, Organization Admin roles, support access, or raw Tenant internals.

Frontend-safe payload for `user_admin.organization_reactivate_confirmation.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `organizationId`, `organizationName`, `currentStatus`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `detailReturnActionId: 'action-organization-read'`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `confirmation`: `{ consequenceCopy, confirmationRequired: true, confirmationPhrase?, reasonDraft?, reasonRequired, submitLabel, submitActionId: 'action-organization-reactivate', cancelActionId: 'action-organization-read', idempotencyKeyHint, freshnessVersionHint?, disabledReason? }`. Default copy explains that reactivation changes the Organization/Tenant lifecycle boundary back to active administration without deleting, exposing, or rewriting tenant application data, customer records, Organization Admin memberships, billing/provider settings, or support access.
- `reactivationEligibility`: `{ currentStatus, allowedStatuses, blockedReason?, suspendedSince?, unresolvedSuspensionReasonSummary?, providerOrOutboxReadinessSummary?, approvalRequired?, traceRefs[] }` rendered as user-facing guidance rather than raw policy ids. Eligibility is backend-authored; the frontend must not infer reactivation availability from status labels.
- `authorizedActions[]`: open/reload reactivate confirmation, validate confirmation/reason, submit reactivation, return to Organization detail, return to Organization Directory, open authorized Organization audit evidence, and return to dashboard. Unavailable actions are omitted or represented with safe disabled copy only when it does not disclose hidden Organization, tenant, admin, customer, policy, provider, billing, or capability facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include evidence labels but not raw trace event ids, raw correlation/idempotency values, raw provider ids, backend component names, tenant internals, billing provider state, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts, hidden Organization identities/counts, raw tenant ids unless explicitly policy-safe, raw WorkOS/provider ids, raw JWT/session values, billing provider state, invitation tokens/token hashes, support-access internals, raw model/provider config, hidden roles/capabilities, raw idempotency keys, unredacted audit evidence, or hidden policy predicates.
- User-facing copy uses clear SaaS Owner language such as “Reactivate Organization” and “Confirm Organization reactivation.” Validation, stale, no-op, and denial copy describes visible recovery steps without enumerating unavailable Organizations, tenant internals, hidden blockers, or missing privileged capabilities.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-organization-reactivate-confirmation` | `saas_owner.organization.reactivate` / `manage-organizations` | Open or reload this confirmation with selected SaaS Owner scope, visible Organization summary, lifecycle reactivation copy, reason/confirmation requirements, idempotency/freshness guidance, trace refs, and branch metadata. Hidden, stale, disabled-actor, cross-scope, unsupported-status, or missing-capability targets return `surface-user-admin-system-message` without enumeration. |
| `action-organization-reactivate` | `saas_owner.organization.reactivate` / `manage-organizations` | Validate Organization visibility/lifecycle, confirmation phrase if required, reason when required, selected `AuthContext`, stale/freshness state, idempotency/correlation, approval gate, and disabled actor checks; audit the attempt; reactivate the visible Organization/Tenant lifecycle boundary when authorized; then open `surface-user-admin-organization-detail` with `lastResult`. Validation-error, already-active/no-op, idempotent replay, conflict/stale, approval-required, forbidden, hidden, policy-blocked, or runtime-blocked results return this confirmation with inline state or `surface-user-admin-system-message`; fake reactivation success is forbidden. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Cancel or return to `surface-user-admin-organization-detail` for the selected visible Organization with backend-owned context, focus restoration, trace refs, and no hidden Organization/customer enumeration. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filters, focus restoration, trace refs, and no hidden Organization counts. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` focused on the Organization branch or originating attention queue when authorized. |

States and outcomes:

- Loading: skeleton confirmation with selected SaaS Owner scope and Organization placeholder only after backend authorization starts; no hidden Organizations, tenant ids, customer facts, blockers, provider data, billing data, or policy internals render while authorization is unresolved.
- Ready: backend-authored Organization summary, lifecycle reactivation copy, reason/confirmation controls, eligibility/warnings, submit/return actions, trace refs, correlation id, redaction, style/catalog bindings, and keyboard-operable controls are visible.
- Validation-error: missing required reason, missing/incorrect confirmation phrase when configured, unsupported current status, stale selected context, stale Organization version, approval-required state, disabled actor, or policy gate is reported inline without changing the Organization or broadening scope.
- Submitting: preserve confirmation/reason input and focus while backend reauthorizes selected context, Organization visibility, reactivation capability, lifecycle/freshness state, approval gate, idempotency replay, and policy/runtime readiness.
- Success: route to `surface-user-admin-organization-detail` with active lifecycle status, `lastResult`, trace/correlation refs, and branch return metadata.
- No-op/idempotent replay: route to refreshed detail when the Organization is already active or the replayed reactivation is visible and authorized; otherwise return safe no-enumeration recovery.
- Approval-required/policy-blocked: show a safe decision or system-message result with recovery and audit evidence; do not claim reactivation or expose raw policy predicates.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` without hidden Organization, tenant, customer, admin, invitation, role, capability, count, provider, billing, blocker, or policy enumeration.
- Conflict/stale/partial-data/failure: keep the confirmation safe for retry, show refresh/back-to-Organization recovery, and never fabricate Organization reactivation, audit evidence, provider readiness, or fixture data.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every confirmation load, submit, branch return, detail return, and audit drilldown is evaluated against the selected backend SaaS Owner/App Admin `AuthContext`, active app-owner membership, visible Organization/Tenant boundary, lifecycle eligibility, and `saas_owner.organization.reactivate` capability. Tenant Admins, Customer Admins, support-only actors, disabled actors, stale contexts, missing memberships, hidden/cross-scope Organizations, unsupported lifecycle states, and missing capabilities receive safe recovery; no browser payload contains tenant/customer app data, hidden Organization facts, hidden capabilities, raw provider data, raw billing state, or raw JWT/session values.
- Trace/audit contract: each open, submit attempt, validation failure, no-op, idempotent replay, success, approval-required, policy-blocked, denial, stale/conflict result, branch/detail return, and audit drilldown emits or links an admin work trace with actor account, selected context summary, visible Organization summary, redacted reason/confirmation state, action id, capability decision, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw event ids, raw correlation/idempotency mechanics, provider secrets, tenant internals, hidden roles, hidden Organization facts, billing provider internals, or unredacted audit evidence.
- Accessibility/responsive expectations: the confirmation has a stable heading, selected-scope and Organization/Tenant boundary notice, clearly announced lifecycle consequence copy, semantic reason/confirmation fields, error/help text associations, keyboard-operable submit/cancel/audit controls, announced validation/submission/result messages, focus return to the originating Organization detail task or first result message, and responsive stacking that preserves boundary, consequence, validation, trace, and redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, lifecycle eligibility, blockers, tenant identifiers, provider/billing readiness, or result routing from client-local labels/status.
- Acceptance/regression coverage must verify Organization-detail-to-reactivate traversal, protected direct reactivate-confirmation load, successful Organization reactivation and detail result routing, idempotent replay/no-op behavior, required reason/confirmation validation when configured, unsupported-status and stale/conflict handling, approval-required or policy-blocked recovery where configured, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, disabled/stale context denial, hidden/cross-scope Organization denial, audit/work trace/correlation links, frontend secret boundaries, keyboard form operation, focus return to Organization detail and Organization Directory, responsive rendering, and separation of Organization reactivation from Organization Admin role/status, Customer data, billing/provider settings, support access, and tenant application-data changes.

Surface-description sufficiency review: `surface-user-admin-organization-reactivate-confirmation` is sufficiently unambiguous for developers/generators to implement and review the lifecycle-confirmation objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived eligibility/result routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, idempotent replay/no-op and stale/policy-blocked handling, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-admins` list/search contract

- Surface id: `surface-user-admin-organization-admins`.
- Surface type: `list-search`.
- Surface contract: `user_admin.organization_admins.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-detail`, visible Organization Directory rows/cards when backend exposes the task, and authorized deep links by `action-user-admin-show-organization-admins`; branch return to the Organization list uses `action-user-admin-show-organizations`, and local return to the selected Organization detail uses `action-organization-read`.
- Required context: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, a visible selected Organization/Tenant boundary, and backend `saas_owner.organization_admin.list` capability. Tenant Admin, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden Organization, cross-scope Organization, missing-capability, or hidden admin target attempts return `surface-user-admin-system-message` without revealing whether Organization Admins, invitations, customers, app data, counts, or roles exist.
- User goal: review the browser-safe Organization Admin users and invitations for one selected Organization, find admin-readiness issues such as pending or expired invites and last-admin risk, and open a dedicated Organization Admin detail or invitation/create task surface. The list is discovery-only; it never changes roles, membership/account status, invitation lifecycle, or Organization lifecycle inline.

Frontend-safe payload for `user_admin.organization_admins.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `organizationId`, `organizationName`, `organizationStatus`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `detailReturnActionId: 'action-organization-read'`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `query`, `filters`, `sort`, `pageInfo`, `emptyMessage`, `systemStates`, and `lastResult`.
- `adminSummary`: `{ visibleAdminCount, activeAdminCount, suspendedOrDisabledAdminCount?, pendingInvitationCount, expiredInvitationCount?, deliveryFailureCount?, firstAdminBootstrapEligible, lastAdminRiskCount?, providerBlockedCount?, outboxBlockedCount?, traceRefs[] }`. Counts are backend-authorized for the selected visible Organization and are omitted/redacted if they would disclose hidden people, invitations, customers, tenants, or app-owner policy facts.
- `rows[]`: unified Organization Admin account/membership/invitation rows sorted by backend policy, each with `{ rowId, recordKind: organization_admin_membership|organization_admin_invitation, displayName?, email?, membershipId?, invitationId?, status, roles[], invitationStatus?, deliveryStatus?, lastAdminRisk?, attentionBadges[], actionAvailability[], targetSurfaceId, targetActionId, traceRefs[], redactionState }`. Roles are limited to Organization Admin-safe roles such as `TENANT_ADMIN`; SaaS Owner, Customer Admin, billing, support, or hidden role lists are never inferred or offered by the browser.
- `filters`: backend-authored safe options for text query, membership status, invitation status, attention state, delivery/outbox state, and last-admin risk. Filter labels and option availability must not expose hidden counts, unsupported roles, customer identities, tenant application data, or policy internals.
- `authorizedActions[]`: refresh/list, open visible Organization Admin detail row, open visible invitation detail row, open Organization Admin invite/bootstrap form, return to Organization detail, return to Organization Directory, and open authorized audit evidence. Unavailable actions are omitted; disabled reasons are shown only when they do not disclose hidden admin, invitation, Organization, tenant, or policy facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default rows may show evidence labels and redaction summaries but not raw trace event ids, raw correlation/idempotency values, provider payloads, invitation tokens, tenant internals, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts, hidden Organization Admin identities/counts, raw WorkOS/provider ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, provider payloads or secrets, billing provider state, support-access internals, raw model/provider config, hidden roles/capabilities, cross-tenant facts, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses positive SaaS Owner language such as “Organization Admins visible to you for this Organization” and “No Organization Admin invitations need attention.” Direct denied, hidden, or stale attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-organization-admins` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Reload this list for the selected visible Organization with backend-shaped filters, pagination, branch metadata, trace refs, and selected SaaS Owner scope summary. |
| `action-open-organization-admin-invitation-create` | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Open `surface-user-admin-organization-admin-invitation-create` with target-role options limited to `TENANT_ADMIN`, Organization boundary copy, idempotency guidance, outbox/provider readiness, and branch return metadata. |
| `action-open-organization-admin-detail` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Open `surface-user-admin-organization-admin-detail` for a visible Organization Admin membership; hidden, stale, disabled-actor, cross-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-open-organization-admin-invitation-detail` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Open `surface-user-admin-organization-admin-detail` or `surface-user-admin-invitation-detail` with Organization Admin branch context for a visible invitation; hidden, stale, duplicate-hidden, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Return to `surface-user-admin-organization-detail` for the selected Organization with backend-owned context and focus on admin readiness. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with safe filter/context preservation. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |

States and outcomes:

- Loading: skeleton list with selected SaaS Owner scope and selected Organization label only after backend authorization starts; no hidden admins, invitations, counts, or tenant/customer app data are rendered while authorization is unresolved.
- Empty: no visible Organization Admin memberships or invitations match the filters; show invite/bootstrap only when `saas_owner.organization_admin.invite` is authorized and safe for the selected Organization state.
- Ready: backend-derived summary, rows/cards, filters, branch metadata, trace/correlation, redaction notices, and keyboard-operable row/card actions are visible.
- Searching/submitting: preserve current filters, Organization context, and focus while backend reauthorizes selected context, Organization visibility, query, and cursor.
- Validation-error: malformed Organization id, query/filter/page/sort, row target, or action payload is reported without broadening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Organization, admin, invitation, customer, tenant, role, or count enumeration.
- Conflict/stale: selected context, Organization lifecycle, membership, invitation, row target, or page cursor changed; show refresh/back-to-Organization recovery and trace refs.
- Provider/outbox fail-closed: show safe invitation delivery readiness or blocked-state summaries only for visible Organization Admin invitation tasks; never invent delivery success or expose provider internals.
- No-op/success: list reloads and row/detail opens are traceable; consequential success is shown only by the dedicated invite, detail, invitation, lifecycle, or system-message result surface.

Trace, audit, accessibility, and tests:

- Every load, search/filter, page/sort, row open, invitation-create open, branch return, denied hidden target, stale result, provider/outbox blocked result, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, visible Organization summary, safe query/filter summary, result surface id, capability decision, and redaction summary.
- Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, tenant/customer app data, or raw correlation/idempotency mechanics.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Organization boundary notice, semantic summary/list/filter sections, keyboard-operable rows and cards with equivalent backend action ids, announced empty/error/stale states, focus return to the originating Organization detail task or list row, and responsive table-to-card rendering that preserves action availability, redaction notices, and branch-return controls.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, row routing, role options, lifecycle eligibility, or provider readiness from client-local labels/status.
- Acceptance/regression coverage must verify Organization-detail-to-Organization-Admins traversal, direct protected list load, list filtering/search/empty state, Organization Admin row activation, invitation row activation, invite/bootstrap form open, return to Organization detail and Organization Directory, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, hidden/cross-scope Organization and hidden row denial, stale row recovery, last-admin risk display, provider/outbox fail-closed indicators, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus return, and responsive table-to-card rendering.

Surface-description sufficiency review: `surface-user-admin-organization-admins` is sufficiently unambiguous for developers/generators to implement and review the list/search objective without inventing payload fields, row actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Organization Admin row routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, trace/correlation, provider/outbox fail-closed boundaries, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-admin-invitation-create` create-form contract

- Surface id: `surface-user-admin-organization-admin-invitation-create`.
- Surface type: `create-form`.
- Surface contract: `user_admin.organization_admin_invitation_create.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-detail` or `surface-user-admin-organization-admins` by `action-open-organization-admin-invitation-create`; branch return to the Organization Admin list uses `action-user-admin-show-organization-admins`, local return to Organization detail uses `action-organization-read`, and Organization branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints.
- Required context and auth: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, a visible selected Organization/Tenant boundary, and backend `saas_owner.organization_admin.invite` capability through `manage-organization-admins`. Tenant Admin, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden Organization, cross-scope Organization, missing-capability, unsupported role, or policy-blocked direct loads/submissions return `surface-user-admin-system-message` without revealing whether hidden Organizations, tenants, admins, invitations, customers, roles, counts, or app data exist.
- User goal: bootstrap or invite a `TENANT_ADMIN` for one visible Organization/Tenant using a single-purpose form. The surface validates invitee email/display name/reason and the backend-authored target role, submits an idempotent Organization Admin invitation request, and routes success to an Organization Admin detail/invitation detail result. It never changes existing memberships inline, never offers SaaS Owner or Customer roles, and never exposes tenant/customer application data.

Frontend-safe payload for `user_admin.organization_admin_invitation_create.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `organizationId`, `organizationName`, `organizationStatus`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `adminListReturnActionId: 'action-user-admin-show-organization-admins'`, `detailReturnActionId: 'action-organization-read'`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `form`: `{ emailDraft?, displayNameDraft?, reasonDraft?, targetRoleOptions: ['TENANT_ADMIN'], selectedTargetRole: 'TENANT_ADMIN', idempotencyKeyHint, submitLabel, cancelActionId, submitActionId, disabledReason? }`. Role options are backend-authored and limited to Organization Admin-safe roles; the browser must not infer or offer SaaS Owner, Customer Admin, support, billing, provider, or hidden roles.
- `policyContext`: `{ firstAdminBootstrapEligible, lastAdminRiskSummary?, duplicateOpenInvitePolicy, allowedEmailDomains?, maxPendingInviteCount?, approvalRequired?, organizationLifecycleAllowsInvite, traceRefs[] }` rendered as product guidance rather than raw policy ids.
- `deliveryReadiness`: `{ outboxStatus, providerStatus, retryEligible, failClosedMessage?, traceRefs[] }`; provider/outbox failures are blocked or retryable states and must not fabricate delivery success.
- `authorizedActions[]`: open/create form, validate draft, submit Organization Admin invitation, return to Organization Admins, return to Organization detail, return to Organization Directory, open authorized audit evidence, and open dashboard. Unavailable actions are omitted or represented by safe disabled reasons only when the reason does not reveal hidden Organization, tenant, admin, invitation, role, capability, provider, or policy facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include trace/evidence labels but not raw trace event ids, raw correlation/idempotency values, provider payloads, invitation tokens, token hashes, tenant internals, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose tenant/customer application data, customer identities/counts, hidden Organization Admin identities/counts, raw WorkOS/provider ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, Resend/provider payloads or secrets, billing provider state, support-access internals, raw model/provider config, hidden roles/capabilities, cross-tenant facts, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses scoped SaaS Owner language such as “Invite an Organization Admin” and “This person will administer the selected Organization.” Direct denied, hidden, stale, duplicate-hidden, or policy-blocked attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-organization-admin-invitation-create` | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Open this create form with the selected visible Organization boundary, backend-authored `TENANT_ADMIN` role option, policy context, delivery readiness, branch metadata, and trace refs. |
| `action-submit-organization-admin-invitation` | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Validate Organization visibility/lifecycle, email/display name/reason/target role, duplicate/open-invite policy, idempotency, and provider/outbox readiness; create or reuse a visible Organization Admin invitation, audit the attempt, and open `surface-user-admin-organization-admin-detail` or `surface-user-admin-invitation-detail` with Organization Admin branch context. Validation, duplicate-hidden, provider/outbox blocked, stale, conflict, approval-required, no-op, or denial results return this form with inline state or `surface-user-admin-system-message`; fake delivery success is forbidden. |
| `action-user-admin-show-organization-admins` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Return to `surface-user-admin-organization-admins` for the selected visible Organization with safe filters, focus restoration, trace refs, and no hidden admin/invitation enumeration. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Return to `surface-user-admin-organization-detail` for the selected Organization with backend-owned context and focus on admin readiness. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filter/context preservation. |
| `action-open-organization-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |

States and outcomes:

- Loading: skeleton create form with selected SaaS Owner scope and Organization label only after backend authorization starts; role options, invite counts, provider state, and hidden target details are not rendered while authorization is unresolved.
- Ready: backend-authored Organization boundary, `TENANT_ADMIN` role option, policy context, delivery readiness, trace/correlation, branch-return actions, and submit/cancel controls are visible.
- Draft validation-error: invalid email format, unsupported domain, missing reason when required, unsupported role, duplicate open invite, self-invite where policy forbids it, disabled inviter, max pending invites, Organization lifecycle block, missing selected Organization, or stale context is reported inline without creating or sending an invitation and without broadening scope.
- Submitting: preserve form input and focus while backend reauthorizes selected context, Organization visibility, role, duplicate state, provider/outbox readiness, and idempotency.
- Success: route to Organization Admin detail/invitation detail with browser-safe invitation summary, delivery status, trace/correlation, and branch return metadata.
- Duplicate/no-op: show the existing visible Organization Admin invitation/detail when authorized, or a safe no-enumeration system message when the duplicate target is hidden.
- Provider/outbox blocked: return a blocked state or system message with recovery steps, trace refs, `noFakeSuccess`, and no invitation-token or provider payload exposure.
- Forbidden/hidden-not-found/stale/conflict: return `surface-user-admin-system-message` with no hidden Organization, tenant, admin, invitation, customer, role, capability, provider, or count enumeration.
- Partial-data/failure: keep draft safe for retry and show user-safe recovery without fixture/mock delivery status.

Trace, audit, accessibility, and tests:

- Every open, draft validation, submit, duplicate/no-op, provider/outbox blocked result, denial, branch return, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, visible Organization summary, inviter account summary, redacted target email/domain, action id, capability decision, result surface id, redaction level, and delivery/outbox readiness summary.
- Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, tenant/customer app data, raw correlation/idempotency mechanics, or unredacted audit evidence.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Organization boundary notice, semantic form fields, inline validation tied to controls, status messages announced as status text, keyboard-operable submit/cancel/return controls, focus return to the originating Organization Admin list or Organization detail action, and responsive form layout that preserves delivery-readiness, policy guidance, redaction notices, and branch-return controls.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, role options, lifecycle eligibility, provider readiness, or delivery state from client-local labels/status.
- Acceptance/regression coverage must verify Organization-detail-to-create-form and Organization-Admins-list-to-create-form traversal, protected direct create-form load, successful Organization Admin invite submission and result routing, first-admin bootstrap eligibility, idempotent replay/duplicate-open-invite behavior, email/role/reason validation, unsupported-role rejection, hidden/cross-scope Organization denial without enumeration, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, disabled/stale context denial, provider/outbox fail-closed behavior, audit/work trace/correlation links, frontend secret boundaries, keyboard form operation, focus return, and responsive create-form layout.

Surface-description sufficiency review: `surface-user-admin-organization-admin-invitation-create` is sufficiently unambiguous for developers/generators to implement and review the create-form objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Organization boundary and role options, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, idempotency, provider/outbox fail-closed boundaries, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-organization-admin-detail` show/inspection contract

- Surface id: `surface-user-admin-organization-admin-detail`.
- Surface type: `show-inspection` and Organization Admin task-router; it must not perform inline role, membership-status, invitation resend/revoke, account, support, billing, customer, or tenant application-data mutations.
- Surface contract: `user_admin.organization_admin_detail.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Organization Directory branch descendant opened from `surface-user-admin-organization-admins` rows, `surface-user-admin-organization-admin-invitation-create` success/no-op result routing, Organization Admin attention cards, and authorized deep links by `action-open-organization-admin-detail` or `action-open-organization-admin-invitation-detail`; branch return to the Organization Admin list uses `action-user-admin-show-organization-admins`, local return to Organization detail uses `action-organization-read`, and Organization branch return uses `action-user-admin-show-organizations` with backend-shaped safe filters and focus hints.
- Required context and auth: authenticated active account, selected SaaS Owner/App Admin `AuthContext`, active app-owner membership, a visible selected Organization/Tenant boundary, a visible Organization Admin membership or invitation target, and backend `saas_owner.organization_admin.list` for inspection. Task-entry actions additionally require the specific backend authority for the routed work: `saas_owner.organization_admin.manage` for Organization Admin role/status changes, `saas_owner.organization_admin.invite` or invitation lifecycle authority for resend/revoke, and `admin.audit.read` for audit drilldown. Tenant Admin, Customer Admin, support-only, disabled-actor, stale selected context, hidden Organization, cross-scope Organization, hidden admin/invitation, missing-membership, missing-capability, unsupported role, or last-admin-risk attempts return `surface-user-admin-system-message` without revealing whether hidden Organizations, tenants, admins, invitations, customers, roles, or counts exist.
- User goal: inspect one visible Organization Admin membership or invitation, understand current status, role, invitation/delivery state, last-admin and policy risk, and choose a dedicated task surface for role preview/replacement, membership/account lifecycle, invitation resend/revoke, return navigation, or authorized audit evidence. The detail is inspection-first and action-routing-only; consequential changes are performed only by the dedicated task or confirmation surfaces.

Frontend-safe payload for `user_admin.organization_admin_detail.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: saas_owner`, `authorityBasis`, `organizationId`, `organizationName`, `organizationStatus`, `branchRootSurfaceId: 'surface-user-admin-organization-directory'`, `branchReturnActionId: 'action-user-admin-show-organizations'`, `branchReturnLabel`, `adminListReturnActionId: 'action-user-admin-show-organization-admins'`, `organizationDetailReturnActionId: 'action-organization-read'`, `recordId`, `recordKind`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `adminTarget`: `{ recordKind: organization_admin_membership|organization_admin_invitation, adminAccountId?, membershipId?, invitationId?, displayName?, email?, organizationAdminStatus, roles[], roleLabel, membershipStatus?, accountStatus?, invitationStatus?, deliveryStatus?, acceptedAt?, expiresAt?, lastActivitySummary?, traceRefs[], redactionState }`. Roles are limited to Organization Admin-safe roles such as `TENANT_ADMIN`; SaaS Owner, Customer Admin, support, billing, provider, hidden role lists, and tenant/customer application data are never exposed or inferred by the browser.
- `policySummary`: `{ lastAdminRisk, selfActionRisk?, roleReplacementEligible, lifecycleChangeEligible, invitationResendEligible?, invitationRevokeEligible?, duplicateOpenInviteSummary?, providerBlockedCount?, outboxBlockedCount?, approvalRequired?, disabledReason?, traceRefs[] }` in user-safe language. Raw policy ids, raw capability lists, hidden counts, and internal provider/outbox diagnostics are diagnostic-only and role-gated.
- `taskEntryPoints[]`: backend-authored actions for reload detail, return to Organization Admins, return to Organization detail, return to Organization Directory, open role-change preview, open membership-status confirmation, open invitation resend confirmation, open invitation revoke confirmation, open linked invitation detail when applicable, and open authorized audit evidence. Each entry includes action id, label, purpose, governed capability/tool, target surface id/type, target object type/id only when safe, enabled/disabled state, safe disabled reason, required reason/confirmation/idempotency summary, and expected result surface.
- `auditSummary`: recent browser-safe Organization Admin administration events, delivery/outbox evidence labels, denial/no-op excerpts, trace labels, redaction notes, and role-gated audit drilldown action ids. Default payload must not show raw trace event ids, raw correlation/idempotency values, WorkOS/provider ids unless policy-safe, invitation tokens/token hashes, provider payloads/secrets, tenant internals, support-access internals, billing state, model/provider config, or unredacted audit evidence.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-organization-admin-detail` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Reload this detail with the selected visible Organization and Organization Admin membership, branch metadata, trace refs, and no-enumeration redaction; hidden, stale, disabled-actor, cross-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-open-organization-admin-invitation-detail` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Reload this detail or the shared invitation detail for a visible Organization Admin invitation with Organization branch context; hidden, stale, duplicate-hidden, terminal-out-of-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-role-change-preview` | `saas_owner.organization_admin.manage` / `manage-organization-admins` | Open `surface-user-admin-role-change-preview` with Organization Admin branch context, target role options limited to Organization Admin-safe roles, capability-delta summary, last-admin impact, and approval policy; commit does not occur on this detail surface. |
| `action-open-user-admin-membership-status-confirmation` | `saas_owner.organization_admin.manage` / `manage-organization-admins` | Open `surface-user-admin-membership-status-confirmation` for eligible visible Organization Admin suspend/reactivate/remove/account-state work with consequence copy, last-admin/self-action checks, idempotency guidance, and branch return metadata. |
| `action-open-user-admin-invitation-resend-confirmation` | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Open `surface-user-admin-invitation-resend-confirmation` for an eligible visible Organization Admin invitation with delivery/outbox readiness and `noFakeSuccess`; ineligible, terminal, stale, hidden, or provider/outbox blocked targets return safe disabled/recovery state or `surface-user-admin-system-message`. |
| `action-open-user-admin-invitation-revoke-confirmation` | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Open `surface-user-admin-invitation-revoke-confirmation` for an eligible visible Organization Admin invitation with consequence copy, last-admin risk, idempotency guidance, and branch return metadata; accepted, already revoked, hidden, stale, or last-admin-risk targets return safe no-op/denial/system-message recovery. |
| `action-user-admin-show-organization-admins` | `saas_owner.organization_admin.list` / `manage-organization-admins` | Return to `surface-user-admin-organization-admins` for the selected visible Organization with backend-shaped safe filters, focus restoration, trace refs, and no hidden admin/invitation enumeration. |
| `action-organization-read` | `saas_owner.organization.read` / `manage-organizations` | Return to `surface-user-admin-organization-detail` for the selected Organization with backend-owned context and focus on admin readiness. |
| `action-user-admin-show-organizations` | `saas_owner.organization.list` / `manage-organizations` | Return to `surface-user-admin-organization-directory` with backend-shaped safe filter/context preservation. |
| `action-open-organization-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |

States and outcomes:

- Loading: skeleton detail with selected SaaS Owner scope and Organization label only after backend authorization starts; no hidden admin identities, invitation tokens, counts, roles, tenant/customer app data, or provider state are rendered while authorization is unresolved.
- Ready: backend-derived target summary, safe role/status/invitation/delivery state, policy summary, task entry points, branch metadata, trace/correlation, and redaction notices are visible.
- Empty/limited: target is visible but has no lifecycle alerts, delivery issues, or audit excerpts; show safe absence copy and authorized task entry points only.
- Opening task/submitting: preserve current detail context and focus while backend reauthorizes selected context, Organization visibility, target visibility, requested task, policy risk, and provider/outbox readiness.
- Validation-error: malformed Organization id, membership id, invitation id, selected context, task target, or action payload is reported without broadening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Organization, tenant, customer, admin, invitation, role, capability, count, provider, or policy enumeration.
- Conflict/stale: selected context, Organization lifecycle, membership, invitation, role, provider/outbox readiness, or policy eligibility changed; show refresh/back-to-Organization-Admins recovery and trace refs.
- Provider/outbox fail-closed: show safe delivery readiness only for visible invitation tasks; never invent invitation delivery success, expose provider internals, or mutate membership/roles from a provider-blocked state.
- No-op/success: detail reload and task-open no-ops are traceable; consequential success is shown only by the dedicated role, lifecycle, invitation, or system-message result surface.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every detail load, branch return, task-open, and audit drilldown is evaluated against the selected backend SaaS Owner/App Admin `AuthContext`, visible Organization/Tenant boundary, and visible Organization Admin target. Tenant Admins and Customer Admins cannot open this Organization Admin graph, support-only access is non-authoritative, disabled/missing actors receive safe recovery, and no browser payload contains tenant/customer application data, hidden customer/Organization facts, unsupported roles, or hidden admin/invitation facts.
- Trace/audit contract: each detail load, denied hidden target, invitation/membership task entry, branch return, stale result, no-op, provider/outbox blocked result, and audit drilldown emits or links an admin work trace with actor account, selected context, visible Organization summary, target summary only when visible, action id, capability decision, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, tenant/customer app data, raw correlation/idempotency mechanics, or unredacted audit evidence.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Organization boundary notice, semantic target/policy/action/audit sections, task actions as keyboard-operable grouped controls, announced ready/empty/error/stale states, focus return to the originating Organization Admin row or result message, and responsive stacking that preserves action availability, branch-return controls, and redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, role options, lifecycle eligibility, last-admin risk, provider readiness, or row routing from client-local labels/status.
- Acceptance/regression coverage must verify Organization-Admins-list-to-detail traversal, invitation-create-to-detail result routing, protected direct detail load for membership and invitation targets, role-change preview open, membership-status confirmation open, invitation resend/revoke confirmation open, return to Organization Admins/Organization detail/Organization Directory, SaaS Owner/App Admin authorization, Tenant Admin and Customer Admin denial without enumeration, hidden/cross-scope Organization and hidden target denial, stale/conflict recovery, last-admin and self-action risk presentation, provider/outbox fail-closed invitation readiness, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus return, and responsive rendering.

Surface-description sufficiency review: `surface-user-admin-organization-admin-detail` is sufficiently unambiguous for developers/generators to implement and review the show/inspection objective without inventing payload fields, task actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Organization Admin target routing, SaaS Owner scope authorization, Tenant/Customer Admin denial/no-enumeration behavior, last-admin protections, provider/outbox fail-closed boundaries, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### Frontend-safe payloads

- Directory: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `authorityBasis`, `boundaryNotice`, `traceRefs`, `correlationId`, `redaction`, `organizations[]`, `filters`, `systemStates`, `emptyMessage`, `forbiddenMessage`, `lastResult`. Each `organizations[]` row contains `{ organizationId, organizationName, status, updatedAt?, safeLifecycleSummary?, visibleTenantAdminCount?, actionAvailability[], traceRefs[] }`; counts are omitted unless backend marks them safe and non-enumerating.
- Detail: directory shared fields plus `organizationDetail`: `{ organizationId, organizationName, status, safeBoundaryNotice, visibleActions[], adminSummary?, recentAuditEvents[], traceRefs[], correlationId }`. Detail task entries route to create/rename/suspend/reactivate surfaces and Organization Admin list/invite surfaces and never perform mutations inline.
- Create: `{ organizationNameDraft?, reasonDraft?, validationMessages?, idempotencyKeyHint, confirmationCopy, boundaryNotice, traceRefs, correlationId, redaction }`. Success opens detail for the created Organization.
- Rename: `{ organizationId, organizationName, proposedOrganizationName?, reasonDraft?, validationMessages?, staleVersion?, idempotencyKeyHint, boundaryNotice, traceRefs, correlationId, redaction }`. Success refreshes detail.
- Suspend/reactivate confirmation: `{ organizationId, organizationName, currentStatus, consequenceCopy, confirmationRequired, reasonRequired, reasonDraft?, validationMessages?, idempotencyKeyHint, boundaryNotice, traceRefs, correlationId, redaction }`. Success refreshes detail; no-op/denied/conflict uses typed `system_message` or refreshed detail with `lastResult`.
- Organization Admin list: `{ organizationId, organizationName, admins[], invitations[], filters, adminSummary, safeBoundaryNotice, branchRootSurfaceId, branchReturnActionId, traceRefs, correlationId, redaction }`. Each admin/invitation row exposes only browser-safe account/membership/invitation status, roles limited to Organization Admin-safe roles, action availability, and backend-authored target surface ids.
- Organization Admin invite/bootstrap: `{ organizationId, organizationName, targetRoleOptions: ['TENANT_ADMIN'], emailDraft?, displayNameDraft?, reasonDraft?, validationMessages?, idempotencyKeyHint, outboxReadiness, boundaryNotice, traceRefs, correlationId, redaction }`. Success opens Organization Admin detail/invitation detail; provider/outbox failures return system-message without fake success.
- Organization Admin detail: `{ organizationId, organizationName, adminAccountId?, membershipId?, invitationId?, displayName?, email?, status, roles[], invitationStatus?, deliveryStatus?, visibleActions[], lastAdminRisk?, recentAuditEvents[], traceRefs, correlationId, redaction }`. Consequential changes route to existing invitation, membership lifecycle, and role-preview surfaces specialized to the selected Organization Admin target.

### Branch-return actions

Every Organization branch descendant (`surface-user-admin-organization-detail`, `surface-user-admin-organization-create`, `surface-user-admin-organization-rename`, `surface-user-admin-organization-suspend-confirmation`, and `surface-user-admin-organization-reactivate-confirmation`) includes the following return action unless the selected context is no longer authorized, in which case the same action returns `surface-user-admin-system-message`:

| Action id | Label | Governed backend capability/tool | Result behavior |
|---|---|---|---|
| `action-user-admin-show-organizations` | `Show organizations` or `Back to organizations` | `saas_owner.organization.list` / `manage-organizations` | Reopen `surface-user-admin-organization-directory` with backend-shaped safe filter/context preservation; no hidden Organization counts or cross-tenant facts are exposed. |

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
| Open Organization Admins | `saas_owner.organization_admin.list` / `manage-organization-admins` | Open `surface-user-admin-organization-admins` for the selected Organization, or safe hidden/not-found/forbidden system-message. |
| Invite/bootstrap Organization Admin | `saas_owner.organization_admin.invite` / `manage-organization-admins` | Open `surface-user-admin-organization-admin-invitation-create`; submission validates the Organization exists, target role is `TENANT_ADMIN`, invitation delivery is ready, and returns admin detail/invitation detail or system-message. |
| Open Organization Admin detail | `saas_owner.organization_admin.list` / `manage-organization-admins` | Open `surface-user-admin-organization-admin-detail` for a visible admin membership or invitation. |
| Manage Organization Admin role/status/invite lifecycle | `saas_owner.organization_admin.manage` / `manage-organization-admins` | Route to role preview, membership lifecycle confirmation, invitation resend/revoke confirmation, or refreshed Organization Admin detail. Enforce last Organization Admin protection and prevent `SAAS_OWNER_ADMIN` assignment through Organization flows. |
| Open Organization audit evidence | `admin.audit.read` | Render authorized Audit/Trace evidence or safe redacted denial. |

### States and tests

Each graph node defines loading, empty, ready, submitting, success, validation-error, forbidden, hidden-not-found, no-op, conflict/stale, partial-data, and failure states as applicable. Acceptance tests must cover dashboard-to-directory traversal, directory-to-detail traversal, create/rename/suspend/reactivate dedicated form or confirmation surfaces, Organization Admin list/invite/detail/manage traversal after Organization creation, first Organization Admin bootstrap, last Organization Admin protection, Organization-vs-Tenant language, idempotent replay/no-op transitions, safe Tenant Admin and Customer Admin denials, missing capability denial, audit/work trace emission, frontend secret boundary, support-access/billing-boundary non-authority, keyboard operation, focus movement, and typed `system_message` results.

Surface-description sufficiency review: sufficient for durable collection-object readiness. The split graph is sufficiently unambiguous for developers/generators to implement and review without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics.

## Customer and Customer Admin surface graph

### Intent

Customer administration is an Organization Admin surface graph inside the User Admin workstream for managing Customers within the selected Organization/Tenant and bootstrapping Customer Admin users for those Customers. It mirrors the SaaS Owner Organization/Organization Admin model one level down: the Customer directory discovers Customers, Customer detail inspects one Customer, dedicated create/edit/lifecycle surfaces perform consequential Customer changes, and Customer Admin surfaces bootstrap and maintain `CUSTOMER_ADMIN` users for a selected Customer. It is not a sibling-tenant browser, SaaS Owner shortcut, or client-side authority source.

### Graph and collection-object progression

| Surface role | Surface id | Contract | Purpose and graph behavior |
|---|---|---|---|
| Customer list/search | `surface-user-admin-customer-directory` | `user_admin.customer_directory.v1` | Lists/searches Customers visible inside the selected Organization/Tenant. Every row/card is clickable and opens `surface-user-admin-customer-detail` through backend-authored target routing. The dashboard action **Show customers** opens this surface for Organization/Tenant Admin contexts. |
| Customer show/inspection | `surface-user-admin-customer-detail` | `user_admin.customer_detail.v1` | Shows one Customer's browser-safe identity, lifecycle status, boundary notice, trace refs, recent redacted audit excerpts, and task entry points for Customer lifecycle and Customer Admin management. It does not mutate inline. |
| Customer create | `surface-user-admin-customer-create` | `user_admin.customer_create.v1` | Owns Customer name/reason fields, selected Organization/Tenant validation, idempotency, audit/work trace, and success routing to Customer detail. |
| Customer edit | `surface-user-admin-customer-rename` | `user_admin.customer_rename.v1` | Owns foundation Customer display-name editing only, validation, stale/conflict/no-op handling, idempotency, audit/work trace, and success routing back to detail; CRM/account profile fields belong to downstream business domains. |
| Customer destructive lifecycle | `surface-user-admin-customer-suspend-confirmation` | `user_admin.customer_suspend_confirmation.v1` | Requires consequence copy, confirmation, reason, idempotency, backend authorization, audit/work trace, and result routing to detail or `system_message`. |
| Customer lifecycle | `surface-user-admin-customer-reactivate-confirmation` | `user_admin.customer_reactivate_confirmation.v1` | Requires confirmation/reason where policy requires it, idempotency, backend authorization, audit/work trace, and result routing to detail or `system_message`. |
| Customer Admin list/search | `surface-user-admin-customer-admins` | `user_admin.customer_admins.v1` | Lists Customer Admin users and invitations for one selected Customer. Every row/card opens Customer Admin detail or invitation detail through backend-authored target routing. |
| Customer Admin invite/bootstrap | `surface-user-admin-customer-admin-invitation-create` | `user_admin.customer_admin_invitation_create.v1` | Owns first/admin invitation for a selected Customer with `CUSTOMER_ADMIN` role validation, idempotency, outbox/Resend boundary, and success routing to detail. |
| Customer Admin show/inspection | `surface-user-admin-customer-admin-detail` | `user_admin.customer_admin_detail.v1` | Shows one Customer Admin membership or invitation and exposes task entry points for resend/revoke, role replacement, suspend/reactivate/remove, and audit evidence. It never exposes sibling-customer or tenant-wide authority. |

### Shared authority and language

- Required context: authenticated active account, selected Organization/Tenant Admin `AuthContext`, and backend `tenant.customer.list/read/create/rename/suspend/reactivate` for Customer lifecycle work or `tenant.customer_admin.list/invite/manage` for Customer Admin work. Customer Admin selected contexts can manage Customer Users through the User branch, but cannot create sibling Customers or manage Customer Admin peers unless explicit backend policy grants it.
- Product/runtime language: browser copy, route labels, DTOs, and forms use **Customer**; backend enforcement, audit partitioning, and persisted isolation use the selected Tenant plus Customer boundary.
- Forbidden payload content: sibling Customer identities/counts, tenant application data beyond Customer admin metadata, Organization Admin/SaaS Owner role internals, provider ids/secrets, raw JWT/session data, raw invitation tokens, hidden counts, support-access internals, or unredacted audit evidence.

### `surface-user-admin-customer-directory` list/search contract

- Surface id: `surface-user-admin-customer-directory`.
- Surface type: `list-search`.
- Surface contract: `user_admin.customer_directory.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Customer Directory branch root opened from `surface-user-admin-dashboard` by `action-user-admin-show-customers`, from visible Organization/Tenant Admin Customer attention or population cards, and from Customer-branch return actions. Branch descendants return through `action-user-admin-show-customers` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected Organization/Tenant Admin `AuthContext`, active tenant membership, visible selected Organization/Tenant boundary, and backend `tenant.customer.list` capability. SaaS Owner-only, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden/cross-tenant boundary, or missing-capability direct loads return `surface-user-admin-system-message` without revealing whether hidden Customers, Customer Admins, invitations, users, app data, or counts exist.
- User goal: find visible Customers inside the selected Organization/Tenant, understand Customer lifecycle/readiness at a safe summary level, and open the lifecycle-aware Customer detail or dedicated Customer create / Customer Admin task surface. The directory is discovery-only: filtering, paging, sorting, row/card activation, create entry points, Customer Admin list/invite entry points, return navigation, and audit drilldowns are allowed; inline Customer lifecycle, rename, Customer Admin invitation, role/status, billing, support-access, sibling-customer, tenant-wide, or app-data mutation is forbidden.

Frontend-safe payload for `user_admin.customer_directory.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: tenant`, `tenantId` (browser-safe selected Organization/Tenant boundary id), `authorityBasis`, `branchRootSurfaceId: 'surface-user-admin-customer-directory'`, `branchReturnActionId: 'action-user-admin-show-customers'`, `branchReturnLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `query`, `filters`, `sort`, `pageInfo`, `emptyMessage`, `systemStates`, `forbiddenMessage`, and `lastResult`.
- `summary`: `{ visibleCustomerCount, activeCustomerCount, suspendedCustomerCount?, pendingSetupCount?, customerAdminAttentionCount?, pendingAdminInvitationCount?, providerBlockedCount?, outboxBlockedCount?, traceRefs[] }`. Counts are backend-authorized for the selected Organization/Tenant and are omitted/redacted when they would disclose hidden Customers, sibling Customers, Customer Admins, invitations, users, or tenant app facts.
- `customers[]`: backend-ordered rows/cards with `{ customerId, customerName, status, updatedAt?, safeLifecycleSummary?, visibleCustomerAdminCount?, pendingCustomerAdminInvitationCount?, attentionBadges[], actionAvailability[], targetSurfaceId: 'surface-user-admin-customer-detail', openActionId: 'action-customer-read', traceRefs[], redactionState }`. Row/card activation always submits the backend-authored `openActionId`; frontend labels must not infer lifecycle eligibility, hidden routing, sibling-customer existence, or Customer Admin authority.
- `filters`: backend-authored safe options for text query, lifecycle status, setup/readiness state, Customer Admin attention, invitation/delivery attention, and visible attention badges. Filter values must not expose hidden counts, unsupported lifecycle states, sibling Customer identities, tenant application data, raw provider ids, or role/capability lists outside the selected Organization/Tenant scope.
- `authorizedActions[]`: refresh/search, open visible Customer detail row/card, open Customer create form, open Customer Admins for a selected visible Customer when available, open Customer Admin invite/bootstrap for a selected visible Customer when available, open authorized audit evidence, and return to dashboard. Unavailable actions are omitted; disabled reasons are shown only when they do not reveal hidden Customer, admin, invitation, tenant, or policy facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default rows may show trace/evidence labels and redaction summaries but not raw event ids, raw correlation/idempotency values, raw tenant internals, provider payloads, invitation tokens, support-access internals, billing facts, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose sibling Customer identities/counts, tenant application data beyond Customer administration metadata, hidden Customer/Admin/invitation identities or counts, raw WorkOS/provider ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, provider payloads or secrets, billing provider state, support-access internals, raw model/provider config, hidden roles/capabilities, cross-tenant/customer facts, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses positive Organization Admin language such as “Customers visible to you in this Organization” and “No Customers need attention.” Empty visible scopes show safe zero/empty copy; direct denied, hidden, cross-scope, or stale attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-customers` | `tenant.customer.list` / `manage-customers` | Reload this directory with backend-shaped filters, pagination, branch metadata, trace refs, and selected Organization/Tenant scope summary. |
| `action-customer-read` | `tenant.customer.read` / `manage-customers` | Open `surface-user-admin-customer-detail` for a visible Customer; hidden, stale, disabled-actor, cross-scope, or missing-capability targets return `surface-user-admin-system-message` without enumerating hidden Customers. |
| `action-open-customer-create` | `tenant.customer.create` / `manage-customers` | Open `surface-user-admin-customer-create` with selected Organization/Tenant boundary notice, validation rules, idempotency guidance, trace refs, and branch return metadata. |
| `action-user-admin-show-customer-admins` | `tenant.customer_admin.list` / `manage-customer-admins` | Open `surface-user-admin-customer-admins` for the selected visible Customer when backend exposes it; hidden/stale targets return a safe no-enumeration system message. |
| `action-open-customer-admin-invitation-create` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open `surface-user-admin-customer-admin-invitation-create` for the selected visible Customer with `CUSTOMER_ADMIN` role options, delivery readiness, idempotency guidance, and branch return metadata. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` with focus on the originating Customer branch card or attention queue. |

States and outcomes:

- Loading: skeleton directory with selected Organization/Tenant scope label and no rows until backend authorization completes; no hidden Customers, Customer Admins, invitations, counts, or tenant app data render while authorization is unresolved.
- Empty: no visible Customers match the filters; show Customer create only when `tenant.customer.create` is authorized and safe for the selected Organization/Tenant state.
- Ready: backend-derived rows, summaries, filters, branch metadata, trace/correlation, redaction notices, and keyboard-operable row/card actions are visible.
- Searching/submitting: preserve current filters, pagination, selected Organization/Tenant context, and focus while backend reauthorizes selected context, query, cursor, and row/action target.
- Validation-error: invalid query/filter/page/sort, malformed Customer target, stale selected scope, or unsupported action payload is reported inline without broadening the search.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Customer, sibling Customer, Customer Admin, invitation, user, role, capability, tenant app-data, or count enumeration.
- Stale/conflict: selected context, Customer lifecycle, row target, Customer Admin readiness, or page cursor changed; show refresh/back-to-customers recovery with trace refs.
- Partial-data/failure/provider/outbox blocked: show safe readiness or delivery-blocked summaries only for visible Customer/Admin tasks; never fabricate Customers, admin counts, invitation delivery, provider readiness, audit evidence, fixture data, or hidden counts.

Trace, audit, accessibility, and tests:

- Every load, search/filter, page/sort, row open, create-form open, Customer Admins open, Customer Admin invite open, denied hidden target, stale result, branch return, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, safe query/filter summary, visible Customer summary when applicable, capability decision, redaction summary, and result surface id.
- Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles/capabilities, sibling Customer facts, tenant app data, or raw correlation/idempotency mechanics.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Customer boundary notice, semantic summary/list/filter sections, keyboard-operable rows and cards with equivalent backend action ids, announced empty/error/stale states, focus return to the originating dashboard card, Customer detail task, or list row, and responsive table-to-card rendering that preserves action availability, redaction notices, and branch-return controls.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, row routing, lifecycle eligibility, Customer Admin role options, provider/outbox readiness, or result routing from client-local labels/status.
- Acceptance/regression coverage must verify dashboard-to-Customer-directory traversal, direct protected list load, scoped list filtering/search/empty state, row activation to Customer detail, Customer create-form open, Customer Admin list/invite open for a visible Customer, Organization/Tenant Admin authorization, SaaS Owner-only and Customer Admin denial without enumeration, hidden/cross-scope Customer denial, stale row recovery, provider/outbox fail-closed indicators for Customer Admin invitations, audit/work trace/correlation links, frontend secret boundaries, keyboard row/card activation, focus return, responsive table-to-card rendering, and separation from sibling Customer, tenant app-data, billing, support-access, and provider-secret exposure.

Surface-description sufficiency review: `surface-user-admin-customer-directory` is sufficiently unambiguous for developers/generators to implement and review the list/search objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Customer row routing, Organization/Tenant Admin scope authorization, SaaS Owner-only and Customer Admin denial/no-enumeration behavior, trace/correlation, provider/outbox fail-closed boundaries for Customer Admin invitation entry points, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-customer-detail` show/inspection contract

- Surface id: `surface-user-admin-customer-detail`.
- Surface type: `show-inspection` and Customer task-router; it must not perform inline Customer rename, suspend, reactivate, Customer Admin invitation, role/status, support-access, billing, tenant-wide, sibling-customer, or tenant application-data mutations.
- Surface contract: `user_admin.customer_detail.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Customer Directory branch descendant opened from `surface-user-admin-customer-directory` rows/cards, visible Organization/Tenant Admin Customer attention cards, Customer create/rename/lifecycle result routing, Customer Admin task returns, and authorized deep links by `action-customer-read`; branch return uses `action-user-admin-show-customers` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected Organization/Tenant Admin `AuthContext`, active tenant membership, visible selected Organization/Tenant boundary, visible target Customer, and backend `tenant.customer.read` capability. SaaS Owner-only, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden Customer, sibling Customer, cross-tenant/customer boundary, or missing-capability direct loads return `surface-user-admin-system-message` without revealing whether hidden Customers, Customer Admins, invitations, users, tenant app data, or counts exist.
- User goal: inspect one visible Customer, understand lifecycle and Customer Admin readiness at a browser-safe level, and choose a dedicated task surface for Customer rename, suspend/reactivate, Customer Admin list/invite/detail, return-to-directory, or authorized audit evidence. The detail surface is inspection-first and action-routing-only.

Frontend-safe payload for `user_admin.customer_detail.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: tenant`, `tenantId` (browser-safe selected Organization/Tenant boundary id), `authorityBasis`, `branchRootSurfaceId: 'surface-user-admin-customer-directory'`, `branchReturnActionId: 'action-user-admin-show-customers'`, `branchReturnLabel`, `recordId`, `recordLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `customerDetail`: `{ customerId, customerName, status, safeLifecycleSummary, lifecycleFreshness?, setupReadiness?, safeBoundaryNotice, visibleActions[], traceRefs[], correlationId }`. The default view may mention that the Customer is contained inside the selected Organization/Tenant boundary, but must not expose sibling Customer facts, tenant application data, raw tenant/customer internals, billing provider state, provider ids/secrets, hidden counts, or raw policy ids.
- `customerAdminSummary`: visible Customer Admin-safe totals, pending invitation summary, first-admin/bootstrap eligibility, last Customer Admin risk summary, provider/outbox readiness summary, and target action ids for Customer Admin list or invite task surfaces. Counts are omitted or redacted when they would disclose hidden Customer Admins, invitations, users, sibling Customers, tenant facts, or role/capability internals.
- `lifecycleTaskSummary`: rename eligibility, suspend eligibility, reactivate eligibility, policy/approval/reason/idempotency summaries, stale/conflict markers, no-op expectations, and target action ids. It summarizes task availability only; mutations occur only on dedicated rename or lifecycle confirmation surfaces.
- `auditSummary`: recent browser-safe Customer administration events, denial/no-op excerpts, trace labels, redaction notes, and role-gated audit drilldown action ids. Default view must not show raw event ids, raw correlation/idempotency values, provider payloads, tenant internals, sibling-customer facts, invitation tokens, or unredacted audit evidence.
- `availableTaskActions[]`: backend-authored action id, label, purpose, governed capability/tool, target surface id/type, target object type/id when safe, enabled/disabled state, disabled reason only when it does not disclose hidden Customer, admin, invitation, tenant, policy, or sibling-customer facts, required confirmation/approval/reason/idempotency summary, and expected result surface.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include evidence labels but not raw trace event ids, raw correlation/idempotency values, raw provider ids, backend component names, tenant internals, support-access details, billing facts, hidden roles/capabilities, or policy implementation ids.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-customer-read` | `tenant.customer.read` / `manage-customers` | Reload this inspection surface with current Customer summary, branch metadata, trace refs, and no-enumeration redaction; hidden, stale, disabled-actor, sibling-customer, cross-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-customers` | `tenant.customer.list` / `manage-customers` | Return to `surface-user-admin-customer-directory` with backend-shaped safe filters, branch metadata, and focus hint. |
| `action-open-customer-rename` | `tenant.customer.rename` / `manage-customers` | Open `surface-user-admin-customer-rename` with current display-name state, validation rules, idempotency guidance, and trace refs; no inline rename occurs on detail. |
| `action-open-customer-suspend` (compatibility alias: `action-open-customer-suspend-confirmation`) | `tenant.customer.suspend` / `manage-customers` | Open `surface-user-admin-customer-suspend-confirmation` when lifecycle state allows suspension; unavailable/hidden/stale/sibling-customer targets return a safe system message. |
| `action-open-customer-reactivate` (compatibility alias: `action-open-customer-reactivate-confirmation`) | `tenant.customer.reactivate` / `manage-customers` | Open `surface-user-admin-customer-reactivate-confirmation` when lifecycle state allows reactivation; unavailable/hidden/stale/sibling-customer targets return a safe system message. |
| `action-user-admin-show-customer-admins` | `tenant.customer_admin.list` / `manage-customer-admins` | Open `surface-user-admin-customer-admins` for the selected visible Customer or return safe hidden/not-found/forbidden recovery. |
| `action-open-customer-admin-invitation-create` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open `surface-user-admin-customer-admin-invitation-create` with Customer boundary, `CUSTOMER_ADMIN` target-role options, outbox/provider readiness, and branch return metadata. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected Organization/Tenant scope and Customer placeholder only after backend authorization starts; no hidden Customer identities, Customer Admins, invitations, sibling-customer facts, tenant app data, or counts are rendered while authorization is unresolved.
- Ready: Customer identity, lifecycle, Customer Admin readiness, task actions, branch return, trace refs, correlation, and redaction notices are backend-derived.
- Empty/limited: Customer is visible but has no Customer Admins, invitations, lifecycle alerts, or audit excerpts; show safe absence copy and authorized task entry points only.
- Submitting/opening task: preserve detail context and focus while backend reauthorizes the target action.
- Validation-error: malformed Customer id, selected context, task target, or action payload is reported without broadening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Customer, sibling Customer, Customer Admin, invitation, user, tenant app-data, count, role, or capability enumeration.
- Conflict/stale: selected context, Customer lifecycle, policy eligibility, Customer Admin readiness, or branch filters changed; show refresh/back-to-customers recovery and trace refs.
- Provider/outbox fail-closed: show provider/outbox readiness summaries only for visible Customer Admin invitation task areas; never invent fixture success or expose provider internals.
- No-op/success: detail refresh and task-open no-ops are traceable; consequential success is shown by the dedicated result/detail surface after the task surface completes.

Authorization, trace, accessibility, and tests:

- Authorization and tenant/customer rules: every detail load, branch return, task-open, and audit drilldown is evaluated against the selected backend Organization/Tenant Admin `AuthContext`, active tenant membership, target Customer visibility, and required Customer or Customer Admin capability. SaaS Owner-only and Customer Admin contexts cannot open Customer detail through this Customer administration graph; support-only access is non-authoritative; disabled/missing actors receive safe recovery; no browser payload contains sibling Customer facts, tenant application data, hidden Customer/Admin/invitation counts, hidden capabilities, raw provider data, raw JWT/session values, support-access internals, or billing authority.
- Trace/audit contract: each detail load, denied hidden/sibling target, branch return, task-open, audit-open, stale result, no-op, and provider/outbox blocked result emits or links an admin work trace with actor account, selected context, visible Customer summary only when visible, action id, capability decision, result surface, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles/capabilities, sibling Customer facts, tenant app data, or raw correlation/idempotency mechanics.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Customer boundary notice, Customer summary sections with semantic headings, task actions as keyboard-operable grouped controls, status/denial messages announced as status text, focus return to the originating Customer row, task action, or first result message, and responsive stacking in Customer summary, Customer Admin readiness, lifecycle tasks, and audit order without losing action availability or redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, task routing, lifecycle eligibility, Customer Admin role options, provider/outbox readiness, or result routing from client-local labels/status.
- Acceptance/regression coverage must verify Customer-directory-to-detail traversal, direct protected detail load, hidden/sibling/cross-scope Customer denial without enumeration, Organization/Tenant Admin authorization and SaaS Owner-only/Customer Admin denial behavior, branch return, rename/suspend/reactivate/Customer Admin list/Customer Admin invite task entry routing, stale/conflict recovery, provider/outbox fail-closed summaries for Customer Admin invitation tasks, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus return, responsive rendering, and separation from sibling Customer data, tenant app-data, billing, support-access, provider secrets, and inline mutations.

Surface-description sufficiency review: `surface-user-admin-customer-detail` is sufficiently unambiguous for developers/generators to implement and review the show/inspection objective without inventing payload fields, task actions, states, auth/tenant/customer behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived task routing, Organization/Tenant Admin scope authorization, SaaS Owner-only and Customer Admin denial/no-enumeration behavior, trace/correlation, provider/outbox fail-closed boundaries, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-customer-admins` list/search contract

- Surface id: `surface-user-admin-customer-admins`.
- Surface type: `list-search`.
- Surface contract: `user_admin.customer_admins.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Customer Directory branch descendant opened from `surface-user-admin-customer-detail`, visible Customer Directory rows/cards when backend exposes the task, and authorized deep links by `action-user-admin-show-customer-admins`; branch return to the Customer list uses `action-user-admin-show-customers`, and local return to the selected Customer detail uses `action-customer-read`.
- Required context: authenticated active account, selected Organization/Tenant Admin `AuthContext`, active tenant membership, a visible selected Customer within the selected Organization/Tenant boundary, and backend `tenant.customer_admin.list` capability. SaaS Owner-only, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden Customer, sibling-customer, cross-tenant/customer boundary, missing-capability, or hidden Customer Admin target attempts return `surface-user-admin-system-message` without revealing whether Customer Admins, invitations, sibling Customers, tenant application data, counts, or roles exist.
- User goal: review browser-safe Customer Admin users and invitations for one selected Customer, find readiness issues such as missing first admin, pending/expired invites, delivery failures, and last Customer Admin risk, and open a dedicated Customer Admin detail or invite/bootstrap task surface. The list is discovery-only; it never changes roles, membership/account status, invitation lifecycle, Customer lifecycle, or tenant/customer scope inline.

Frontend-safe payload for `user_admin.customer_admins.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: tenant`, `tenantId`, `customerId`, `customerName`, `customerStatus`, `authorityBasis`, `branchRootSurfaceId: 'surface-user-admin-customer-directory'`, `branchReturnActionId: 'action-user-admin-show-customers'`, `branchReturnLabel`, `detailReturnActionId: 'action-customer-read'`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `query`, `filters`, `sort`, `pageInfo`, `emptyMessage`, `systemStates`, and `lastResult`.
- `adminSummary`: `{ visibleAdminCount, activeAdminCount, suspendedOrDisabledAdminCount?, pendingInvitationCount, expiredInvitationCount?, deliveryFailureCount?, firstAdminBootstrapEligible, lastCustomerAdminRiskCount?, providerBlockedCount?, outboxBlockedCount?, traceRefs[] }`. Counts are backend-authorized for the selected visible Customer and are omitted/redacted if they would disclose hidden people, invitations, sibling Customers, tenant facts, or policy internals.
- `rows[]`: unified Customer Admin account/membership/invitation rows sorted by backend policy, each with `{ rowId, recordKind: customer_admin_membership|customer_admin_invitation, displayName?, email?, membershipId?, invitationId?, status, roles[], invitationStatus?, deliveryStatus?, lastCustomerAdminRisk?, attentionBadges[], actionAvailability[], targetSurfaceId, targetActionId, traceRefs[], redactionState }`. Roles are limited to Customer Admin-safe roles such as `CUSTOMER_ADMIN`; Tenant Admin, SaaS Owner, billing, support, sibling-customer, or hidden role lists are never inferred or offered by the browser.
- `filters`: backend-authored safe options for text query, membership status, invitation status, attention state, delivery/outbox state, and last Customer Admin risk. Filter labels and option availability must not expose hidden counts, unsupported roles, sibling Customer identities, tenant application data, or policy internals.
- `authorizedActions[]`: refresh/list, open visible Customer Admin detail row, open visible invitation detail row, open Customer Admin invite/bootstrap form, return to Customer detail, return to Customer Directory, and open authorized audit evidence. Unavailable actions are omitted; disabled reasons are shown only when they do not disclose hidden admin, invitation, Customer, tenant, policy, or sibling-customer facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default rows may show evidence labels and redaction summaries but not raw trace event ids, raw correlation/idempotency values, provider payloads, invitation tokens, tenant internals, sibling-customer facts, support-access internals, billing state, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose sibling Customer identities/counts, tenant application data beyond Customer administration metadata, hidden Customer Admin identities/counts, raw WorkOS/provider ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, provider payloads or secrets, billing provider state, support-access internals, raw model/provider config, hidden roles/capabilities, cross-tenant/customer facts, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses positive Organization Admin language such as “Customer Admins visible to you for this Customer” and “No Customer Admin invitations need attention.” Direct denied, hidden, sibling-customer, cross-scope, or stale attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-customer-admins` | `tenant.customer_admin.list` / `manage-customer-admins` | Reload this list for the selected visible Customer with backend-shaped filters, pagination, branch metadata, trace refs, and selected Organization/Tenant scope summary. |
| `action-open-customer-admin-invitation-create` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open `surface-user-admin-customer-admin-invitation-create` with target-role options limited to `CUSTOMER_ADMIN`, Customer boundary copy, idempotency guidance, outbox/provider readiness, and branch return metadata. |
| `action-open-customer-admin-detail` | `tenant.customer_admin.list` / `manage-customer-admins` | Open `surface-user-admin-customer-admin-detail` for a visible Customer Admin membership; hidden, stale, disabled-actor, sibling-customer, cross-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-open-customer-admin-invitation-detail` | `tenant.customer_admin.list` / `manage-customer-admins` | Open `surface-user-admin-customer-admin-detail` or `surface-user-admin-invitation-detail` with Customer Admin branch context for a visible invitation; hidden, stale, duplicate-hidden, sibling-customer, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-customer-read` | `tenant.customer.read` / `manage-customers` | Return to `surface-user-admin-customer-detail` for the selected Customer with backend-owned context and focus on Customer Admin readiness. |
| `action-user-admin-show-customers` | `tenant.customer.list` / `manage-customers` | Return to `surface-user-admin-customer-directory` with safe filter/context preservation. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |

States and outcomes:

- Loading: skeleton list with selected Organization/Tenant scope and selected Customer label only after backend authorization starts; no hidden admins, invitations, counts, sibling Customer facts, or tenant app data render while authorization is unresolved.
- Empty: no visible Customer Admin memberships or invitations match the filters; show invite/bootstrap only when `tenant.customer_admin.invite` is authorized and safe for the selected Customer lifecycle state.
- Ready: backend-derived summary, rows/cards, filters, branch metadata, trace/correlation, redaction notices, and keyboard-operable row/card actions are visible.
- Searching/submitting: preserve current filters, Customer context, selected Organization/Tenant boundary, and focus while backend reauthorizes selected context, Customer visibility, query, cursor, and row target.
- Validation-error: malformed Customer id, query/filter/page/sort, row target, unsupported role, or action payload is reported without broadening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Customer, sibling Customer, Customer Admin, invitation, user, tenant app-data, role, capability, or count enumeration.
- Conflict/stale: selected context, Customer lifecycle, membership, invitation, row target, or page cursor changed; show refresh/back-to-Customer recovery and trace refs.
- Provider/outbox fail-closed: show safe invitation delivery readiness or blocked-state summaries only for visible Customer Admin invitation tasks; never invent delivery success or expose provider internals.
- No-op/success: list reloads and row/detail opens are traceable; consequential success is shown only by the dedicated invite, detail, invitation, lifecycle, or system-message result surface.

Trace, audit, accessibility, and tests:

- Every load, search/filter, page/sort, row open, invitation-create open, branch return, denied hidden/sibling target, stale result, provider/outbox blocked result, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, visible Customer summary, safe query/filter summary, result surface id, capability decision, and redaction summary.
- Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, sibling Customer facts, tenant app data, or raw correlation/idempotency mechanics.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Customer boundary notice, semantic summary/list/filter sections, keyboard-operable rows and cards with equivalent backend action ids, announced empty/error/stale states, focus return to the originating Customer detail task or list row, and responsive table-to-card rendering that preserves action availability, redaction notices, and branch-return controls.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, row routing, role options, lifecycle eligibility, or provider/outbox readiness from client-local labels/status.
- Acceptance/regression coverage must verify Customer-detail-to-Customer-Admins traversal, direct protected list load, list filtering/search/empty state, Customer Admin row activation, invitation row activation, invite/bootstrap form open, return to Customer detail and Customer Directory, Organization/Tenant Admin authorization, SaaS Owner-only and Customer Admin denial without enumeration, hidden/sibling/cross-scope Customer and hidden row denial, stale row recovery, last Customer Admin risk display, provider/outbox fail-closed indicators, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus return, and responsive table-to-card rendering.

Surface-description sufficiency review: `surface-user-admin-customer-admins` is sufficiently unambiguous for developers/generators to implement and review the list/search objective without inventing payload fields, row actions, states, auth/tenant/customer behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Customer Admin row routing, Organization/Tenant Admin scope authorization, SaaS Owner-only and Customer Admin denial/no-enumeration behavior, trace/correlation, provider/outbox fail-closed boundaries, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-customer-admin-invitation-create` create-form contract

- Surface id: `surface-user-admin-customer-admin-invitation-create`.
- Surface type: `create-form`.
- Surface contract: `user_admin.customer_admin_invitation_create.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Customer Directory branch descendant opened from `surface-user-admin-customer-detail` or `surface-user-admin-customer-admins` by `action-open-customer-admin-invitation-create`; branch return to the Customer Admin list uses `action-user-admin-show-customer-admins`, local return to Customer detail uses `action-customer-read`, and Customer branch return uses `action-user-admin-show-customers` with backend-shaped safe filters and focus hints.
- Required context and auth: authenticated active account, selected Organization/Tenant Admin `AuthContext`, active tenant membership, a visible selected Customer within that tenant, and backend `tenant.customer_admin.invite` capability through `manage-customer-admins`. SaaS Owner-only, Customer Admin, support-only, disabled-actor, stale selected context, missing-membership, hidden Customer, sibling-customer, cross-tenant/customer boundary, missing-capability, unsupported role, or policy-blocked direct loads/submissions return `surface-user-admin-system-message` without revealing whether hidden Customers, Customer Admins, invitations, users, roles, counts, sibling Customers, or tenant application data exist.
- User goal: bootstrap or invite a `CUSTOMER_ADMIN` for one visible Customer using a single-purpose form. The surface validates invitee email/display name/reason and the backend-authored target role, submits an idempotent Customer Admin invitation request, and routes success to a Customer Admin detail/invitation detail result. It never changes existing memberships inline, never offers Tenant Admin or SaaS Owner roles, and never exposes sibling-customer or tenant-wide application data.

Frontend-safe payload for `user_admin.customer_admin_invitation_create.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: tenant`, `tenantId`, `customerId`, `customerName`, `customerStatus`, `authorityBasis`, `branchRootSurfaceId: 'surface-user-admin-customer-directory'`, `branchReturnActionId: 'action-user-admin-show-customers'`, `branchReturnLabel`, `adminListReturnActionId: 'action-user-admin-show-customer-admins'`, `detailReturnActionId: 'action-customer-read'`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `form`: `{ emailDraft?, displayNameDraft?, reasonDraft?, targetRoleOptions: ['CUSTOMER_ADMIN'], selectedTargetRole: 'CUSTOMER_ADMIN', idempotencyKeyHint, submitLabel, cancelActionId, submitActionId, disabledReason? }`. Role options are backend-authored and limited to Customer Admin-safe roles; the browser must not infer or offer Tenant Admin, SaaS Owner, support, billing, provider, sibling-customer, or hidden roles.
- `policyContext`: `{ firstAdminBootstrapEligible, lastCustomerAdminRiskSummary?, duplicateOpenInvitePolicy, allowedEmailDomains?, maxPendingInviteCount?, approvalRequired?, customerLifecycleAllowsInvite, traceRefs[] }` rendered as product guidance rather than raw policy ids.
- `deliveryReadiness`: `{ outboxStatus, providerStatus, retryEligible, failClosedMessage?, traceRefs[] }`; provider/outbox failures are blocked or retryable states and must not fabricate delivery success.
- `authorizedActions[]`: open/create form, validate draft, submit Customer Admin invitation, return to Customer Admins, return to Customer detail, return to Customer Directory, open authorized audit evidence, and open dashboard. Unavailable actions are omitted or represented by safe disabled reasons only when the reason does not reveal hidden Customer, admin, invitation, role, capability, provider, policy, sibling-customer, or tenant app-data facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may include trace/evidence labels but not raw trace event ids, raw correlation/idempotency values, provider payloads, invitation tokens, token hashes, tenant internals, sibling-customer facts, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose sibling Customer identities/counts, tenant application data beyond Customer administration metadata, hidden Customer Admin identities/counts, raw WorkOS/provider ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, Resend/provider payloads or secrets, billing provider state, support-access internals, raw model/provider config, hidden roles/capabilities, cross-tenant/customer facts, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses scoped Organization Admin language such as “Invite a Customer Admin” and “This person will administer the selected Customer.” Direct denied, hidden, stale, duplicate-hidden, sibling-customer, or policy-blocked attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-customer-admin-invitation-create` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open this create form with the selected visible Customer boundary, backend-authored `CUSTOMER_ADMIN` role option, policy context, delivery readiness, branch metadata, and trace refs. |
| `action-customer-admin-invite` | `tenant.customer_admin.invite` / `manage-customer-admins` | Validate Customer visibility/lifecycle, email/display name/reason/target role, duplicate/open-invite policy, idempotency, and provider/outbox readiness; create or reuse a visible Customer Admin invitation, audit the attempt, and open `surface-user-admin-customer-admin-detail` or `surface-user-admin-invitation-detail` with Customer Admin branch context. Validation, duplicate-hidden, provider/outbox blocked, stale, conflict, approval-required, no-op, or denial results return this form with inline state or `surface-user-admin-system-message`; fake delivery success is forbidden. |
| `action-user-admin-show-customer-admins` | `tenant.customer_admin.list` / `manage-customer-admins` | Return to `surface-user-admin-customer-admins` for the selected visible Customer with safe filters, focus restoration, trace refs, and no hidden admin/invitation enumeration. |
| `action-customer-read` | `tenant.customer.read` / `manage-customers` | Return to `surface-user-admin-customer-detail` for the selected Customer with backend-owned context and focus on Customer Admin readiness. |
| `action-user-admin-show-customers` | `tenant.customer.list` / `manage-customers` | Return to `surface-user-admin-customer-directory` with backend-shaped safe filter/context preservation. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |

States and outcomes:

- Loading: skeleton create form with selected Organization/Tenant scope and Customer label only after backend authorization starts; role options, invite counts, provider state, hidden target details, sibling Customer facts, and tenant app data are not rendered while authorization is unresolved.
- Ready: backend-authored Customer boundary, `CUSTOMER_ADMIN` role option, policy context, delivery readiness, trace/correlation, branch-return actions, and submit/cancel controls are visible.
- Draft validation-error: invalid email format, unsupported domain, missing reason when required, unsupported role, duplicate open invite, self-invite where policy forbids it, disabled inviter, max pending invites, Customer lifecycle block, missing selected Customer, stale context, or sibling-customer target is reported inline without creating or sending an invitation and without broadening scope.
- Submitting: preserve form input and focus while backend reauthorizes selected context, Customer visibility, role, duplicate state, provider/outbox readiness, and idempotency.
- Success: route to Customer Admin detail/invitation detail with browser-safe invitation summary, delivery status, trace/correlation, and branch return metadata.
- Duplicate/no-op: show the existing visible Customer Admin invitation/detail when authorized, or a safe no-enumeration system message when the duplicate target is hidden.
- Provider/outbox blocked: return a blocked state or system message with recovery steps, trace refs, `noFakeSuccess`, and no invitation-token or provider payload exposure.
- Forbidden/hidden-not-found/stale/conflict: return `surface-user-admin-system-message` with no hidden Customer, sibling Customer, tenant app data, Customer Admin, invitation, user, role, capability, provider, or count enumeration.
- Partial-data/failure: keep draft safe for retry and show user-safe recovery without fixture/mock delivery status.

Trace, audit, accessibility, and tests:

- Every open, draft validation, submit, duplicate/no-op, provider/outbox blocked result, denial, branch return, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, visible Customer summary, inviter account summary, redacted target email/domain, action id, capability decision, result surface id, redaction level, and delivery/outbox readiness summary.
- Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, sibling Customer facts, tenant app data, raw correlation/idempotency mechanics, or unredacted audit evidence.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Customer boundary notice, semantic form fields, inline validation tied to controls, status messages announced as status text, keyboard-operable submit/cancel/return controls, focus return to the originating Customer Admin list or Customer detail action, and responsive form layout that preserves delivery-readiness, policy guidance, redaction notices, and branch-return controls.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, role options, lifecycle eligibility, provider readiness, delivery state, or sibling-customer facts from client-local labels/status.
- Acceptance/regression coverage must verify Customer-detail-to-create-form and Customer-Admins-list-to-create-form traversal, protected direct create-form load, successful Customer Admin invite submission and result routing, first-admin bootstrap eligibility, idempotent replay/duplicate-open-invite behavior, email/role/reason validation, unsupported-role rejection, hidden/sibling/cross-scope Customer denial without enumeration, Organization/Tenant Admin authorization, SaaS Owner-only and Customer Admin denial without enumeration, disabled/stale context denial, provider/outbox fail-closed behavior, audit/work trace/correlation links, frontend secret boundaries, keyboard form operation, focus return, and responsive create-form layout.

Surface-description sufficiency review: `surface-user-admin-customer-admin-invitation-create` is sufficiently unambiguous for developers/generators to implement and review the create-form objective without inventing payload fields, actions, states, auth/tenant/customer behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Customer boundary and role options, Organization/Tenant Admin scope authorization, SaaS Owner-only and Customer Admin denial/no-enumeration behavior, idempotency, provider/outbox fail-closed boundaries, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-customer-admin-detail` show/inspection contract

- Surface id: `surface-user-admin-customer-admin-detail`.
- Surface type: `show-inspection` and Customer Admin task-router; it must not perform inline role, membership-status, invitation resend/revoke, account, support, billing, tenant-wide, sibling-customer, or customer application-data mutations.
- Surface contract: `user_admin.customer_admin_detail.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: Customer Directory branch descendant opened from `surface-user-admin-customer-admins` rows, `surface-user-admin-customer-admin-invitation-create` success/no-op result routing, Customer Admin attention cards, and authorized deep links by `action-open-customer-admin-detail` or `action-open-customer-admin-invitation-detail`; branch return to the Customer Admin list uses `action-user-admin-show-customer-admins`, local return to Customer detail uses `action-customer-read`, and Customer branch return uses `action-user-admin-show-customers` with backend-shaped safe filters and focus hints.
- Required context and auth: authenticated active account, selected Organization/Tenant Admin `AuthContext`, active tenant membership, a visible selected Customer inside the selected Organization/Tenant boundary, a visible Customer Admin membership or invitation target for that Customer, and backend `tenant.customer_admin.list` for inspection. Task-entry actions additionally require the specific backend authority for the routed work: `tenant.customer_admin.manage` for Customer Admin role/status changes, `tenant.customer_admin.invite` or invitation lifecycle authority for resend/revoke, and `admin.audit.read` for audit drilldown. SaaS Owner-only, Customer Admin, support-only, disabled-actor, stale selected context, hidden Customer, sibling-customer, cross-tenant/customer boundary, hidden admin/invitation, missing-membership, missing-capability, unsupported role, or last-Customer-Admin-risk attempts return `surface-user-admin-system-message` without revealing whether hidden Customers, admins, invitations, users, roles, counts, tenant app data, or sibling-customer facts exist.
- User goal: inspect one visible Customer Admin membership or invitation, understand current status, role, invitation/delivery state, last-Customer-Admin and policy risk, and choose a dedicated task surface for role preview/replacement, membership/account lifecycle, invitation resend/revoke, return navigation, or authorized audit evidence. The detail is inspection-first and action-routing-only; consequential changes are performed only by dedicated task or confirmation surfaces.

Frontend-safe payload for `user_admin.customer_admin_detail.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType: tenant`, `tenantId`, `customerId`, `customerName`, `customerStatus`, `authorityBasis`, `branchRootSurfaceId: 'surface-user-admin-customer-directory'`, `branchReturnActionId: 'action-user-admin-show-customers'`, `branchReturnLabel`, `adminListReturnActionId: 'action-user-admin-show-customer-admins'`, `customerDetailReturnActionId: 'action-customer-read'`, `recordId`, `recordKind`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `adminTarget`: `{ recordKind: customer_admin_membership|customer_admin_invitation, adminAccountId?, membershipId?, invitationId?, displayName?, email?, customerAdminStatus, roles[], roleLabel, membershipStatus?, accountStatus?, invitationStatus?, deliveryStatus?, acceptedAt?, expiresAt?, lastActivitySummary?, traceRefs[], redactionState }`. Roles are limited to Customer Admin-safe roles such as `CUSTOMER_ADMIN`; Tenant Admin, SaaS Owner, support, billing, provider, sibling-customer, hidden role lists, and tenant/customer application data are never exposed or inferred by the browser.
- `policySummary`: `{ lastCustomerAdminRisk, selfActionRisk?, roleReplacementEligible, lifecycleChangeEligible, invitationResendEligible?, invitationRevokeEligible?, duplicateOpenInviteSummary?, providerBlockedCount?, outboxBlockedCount?, approvalRequired?, disabledReason?, traceRefs[] }` in user-safe language. Raw policy ids, raw capability lists, hidden counts, and internal provider/outbox diagnostics are diagnostic-only and role-gated.
- `taskEntryPoints[]`: backend-authored actions for reload detail, return to Customer Admins, return to Customer detail, return to Customer Directory, open role-change preview, open membership-status confirmation, open invitation resend confirmation, open invitation revoke confirmation, open linked invitation detail when applicable, and open authorized audit evidence. Each entry includes action id, label, purpose, governed capability/tool, target surface id/type, target object type/id only when safe, enabled/disabled state, safe disabled reason, required reason/confirmation/idempotency summary, and expected result surface.
- `auditSummary`: recent browser-safe Customer Admin administration events, delivery/outbox evidence labels, denial/no-op excerpts, trace labels, redaction notes, and role-gated audit drilldown action ids. Default payload must not show raw trace event ids, raw correlation/idempotency values, WorkOS/provider ids unless policy-safe, invitation tokens/token hashes, provider payloads/secrets, tenant internals, sibling-customer facts, support-access internals, billing state, model/provider config, or unredacted audit evidence.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-customer-admin-detail` | `tenant.customer_admin.list` / `manage-customer-admins` | Reload this detail with the selected visible Customer and Customer Admin membership, branch metadata, trace refs, and no-enumeration redaction; hidden, stale, disabled-actor, sibling-customer, cross-scope, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-open-customer-admin-invitation-detail` | `tenant.customer_admin.list` / `manage-customer-admins` | Reload this detail or the shared invitation detail for a visible Customer Admin invitation with Customer branch context; hidden, stale, duplicate-hidden, terminal-out-of-scope, sibling-customer, or missing-capability targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-role-change-preview` | `tenant.customer_admin.manage` / `manage-customer-admins` | Open `surface-user-admin-role-change-preview` with Customer Admin branch context, target role options limited to Customer Admin-safe roles, capability-delta summary, last-Customer-Admin impact, and approval policy; commit does not occur on this detail surface. |
| `action-open-user-admin-membership-status-confirmation` | `tenant.customer_admin.manage` / `manage-customer-admins` | Open `surface-user-admin-membership-status-confirmation` for eligible visible Customer Admin suspend/reactivate/remove/account-state work with consequence copy, last-Customer-Admin/self-action checks, idempotency guidance, and branch return metadata. |
| `action-open-user-admin-invitation-resend-confirmation` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open `surface-user-admin-invitation-resend-confirmation` for an eligible visible Customer Admin invitation with delivery/outbox readiness and `noFakeSuccess`; ineligible, terminal, stale, hidden, sibling-customer, or provider/outbox blocked targets return safe disabled/recovery state or `surface-user-admin-system-message`. |
| `action-open-user-admin-invitation-revoke-confirmation` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open `surface-user-admin-invitation-revoke-confirmation` for an eligible visible Customer Admin invitation with consequence copy, last-Customer-Admin risk, idempotency guidance, and branch return metadata; accepted, already revoked, hidden, stale, sibling-customer, or last-Customer-Admin-risk targets return safe no-op/denial/system-message recovery. |
| `action-user-admin-show-customer-admins` | `tenant.customer_admin.list` / `manage-customer-admins` | Return to `surface-user-admin-customer-admins` for the selected visible Customer with backend-shaped safe filters, focus restoration, trace refs, and no hidden admin/invitation enumeration. |
| `action-customer-read` | `tenant.customer.read` / `manage-customers` | Return to `surface-user-admin-customer-detail` for the selected Customer with backend-owned context and focus on Customer Admin readiness. |
| `action-user-admin-show-customers` | `tenant.customer.list` / `manage-customers` | Return to `surface-user-admin-customer-directory` with backend-shaped safe filter/context preservation. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or a safe redacted system message. |

States and outcomes:

- Loading: skeleton detail with selected Organization/Tenant scope and Customer label only after backend authorization starts; no hidden admin identities, invitation tokens, counts, roles, sibling Customer facts, tenant/customer app data, or provider state are rendered while authorization is unresolved.
- Ready: backend-derived target summary, safe role/status/invitation/delivery state, policy summary, task entry points, branch metadata, trace/correlation, and redaction notices are visible.
- Empty/limited: target is visible but has no lifecycle alerts, delivery issues, or audit excerpts; show safe absence copy and authorized task entry points only.
- Opening task/submitting: preserve current detail context and focus while backend reauthorizes selected context, Customer visibility, target visibility, requested task, policy risk, and provider/outbox readiness.
- Validation-error: malformed Customer id, membership id, invitation id, selected context, task target, or action payload is reported without broadening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden Customer, sibling Customer, Customer Admin, invitation, user, role, capability, tenant app-data, count, provider, or policy enumeration.
- Conflict/stale: selected context, Customer lifecycle, membership, invitation, role, provider/outbox readiness, or policy eligibility changed; show refresh/back-to-Customer-Admins recovery and trace refs.
- Provider/outbox fail-closed: show safe delivery readiness only for visible invitation tasks; never invent invitation delivery success, expose provider internals, or mutate membership/roles from a provider-blocked state.
- No-op/success: detail reload and task-open no-ops are traceable; consequential success is shown only by the dedicated role, lifecycle, invitation, or system-message result surface.

Authorization, trace, accessibility, and tests:

- Authorization and tenant/customer rules: every detail load, branch return, task-open, and audit drilldown is evaluated against the selected backend Organization/Tenant Admin `AuthContext`, active tenant membership, visible Customer boundary, and visible Customer Admin target. SaaS Owner-only and Customer Admin contexts cannot open this Customer Admin management graph unless explicit backend policy grants it, support-only access is non-authoritative, disabled/missing actors receive safe recovery, and no browser payload contains sibling Customer facts, tenant/customer application data, unsupported roles, hidden admin/invitation facts, hidden counts, or hidden capabilities.
- Trace/audit contract: each detail load, denied hidden/sibling target, invitation/membership task entry, branch return, stale result, no-op, provider/outbox blocked result, and audit drilldown emits or links an admin work trace with actor account, selected context, visible Customer summary, target summary only when visible, action id, capability decision, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, sibling Customer facts, tenant app data, raw correlation/idempotency mechanics, or unredacted audit evidence.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope and Customer boundary notice, semantic target/policy/action/audit sections, task actions as keyboard-operable grouped controls, announced ready/empty/error/stale states, focus return to the originating Customer Admin row or result message, and responsive stacking that preserves action availability, branch-return controls, and redaction notices.
- UI realization must follow the current web UI style guide, named-theme contract, and component catalog anatomy; frontend code may render backend payloads and submit backend-authored actions but must not infer authority, hidden counts, role options, lifecycle eligibility, last-Customer-Admin risk, provider readiness, row routing, or sibling-customer facts from client-local labels/status.
- Acceptance/regression coverage must verify Customer-Admins-list-to-detail traversal, invitation-create-to-detail result routing, protected direct detail load for membership and invitation targets, role-change preview open, membership-status confirmation open, invitation resend/revoke confirmation open, return to Customer Admins/Customer detail/Customer Directory, Organization/Tenant Admin authorization, SaaS Owner-only and Customer Admin denial without enumeration, hidden/sibling/cross-scope Customer and hidden target denial, stale/conflict recovery, last-Customer-Admin and self-action risk presentation, provider/outbox fail-closed invitation readiness, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus return, and responsive rendering.

Surface-description sufficiency review: `surface-user-admin-customer-admin-detail` is sufficiently unambiguous for developers/generators to implement and review the show/inspection objective without inventing payload fields, task actions, states, auth/tenant/customer behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived Customer Admin target routing, Organization/Tenant Admin scope authorization, SaaS Owner-only and Customer Admin denial/no-enumeration behavior, last-Customer-Admin protections, provider/outbox fail-closed boundaries, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### Frontend-safe payloads

- Customer directory: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `tenantId`, `authorityBasis`, `boundaryNotice`, `traceRefs`, `correlationId`, `redaction`, `customers[]`, `filters`, `systemStates`, `emptyMessage`, `forbiddenMessage`, `lastResult`. Each `customers[]` row contains `{ customerId, customerName, status, updatedAt?, safeLifecycleSummary?, visibleCustomerAdminCount?, actionAvailability[], traceRefs[] }`; counts are omitted unless backend marks them safe and non-enumerating.
- Customer detail: directory shared fields plus `customerDetail`: `{ customerId, customerName, status, safeBoundaryNotice, visibleActions[], customerAdminSummary?, lifecycleTaskSummary?, auditSummary?, recentAuditEvents[], traceRefs[], correlationId }`. Detail task entries route to rename/suspend/reactivate and Customer Admin list/invite surfaces and never perform mutations inline.
- Customer Admin list: `{ customerId, customerName, admins[], invitations[], filters, adminSummary, safeBoundaryNotice, branchRootSurfaceId, branchReturnActionId, traceRefs, correlationId, redaction }`. Each row exposes only browser-safe account/membership/invitation status, roles limited to Customer Admin-safe roles, action availability, and backend-authored target surface ids.
- Customer Admin invite/bootstrap: `{ customerId, customerName, targetRoleOptions: ['CUSTOMER_ADMIN'], emailDraft?, displayNameDraft?, reasonDraft?, validationMessages?, idempotencyKeyHint, outboxReadiness, boundaryNotice, traceRefs, correlationId, redaction }`. Success opens Customer Admin detail/invitation detail; provider/outbox failures return system-message without fake success.
- Customer Admin detail: `{ customerId, customerName, adminAccountId?, membershipId?, invitationId?, displayName?, email?, status, roles[], invitationStatus?, deliveryStatus?, visibleActions[], lastAdminRisk?, recentAuditEvents[], traceRefs, correlationId, redaction }`. Consequential changes route to invitation, membership lifecycle, and role-preview surfaces specialized to the selected Customer Admin target.

### Branch-return actions

Every Customer branch descendant includes `action-user-admin-show-customers` with label **Show customers** or **Back to customers**, browser tool `user-admin.show-customers`, governed tool `manage-customers`, capability `tenant.customer.list`, result surface `surface-user-admin-customer-directory`, and backend-authored safe filters only. If the selected context is no longer authorized, the same action returns `surface-user-admin-system-message`.

### Actions and result surfaces

| Canonical action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-customers` | `tenant.customer.list` / `manage-customers` | Refresh `surface-user-admin-customer-directory` with safe empty/forbidden/redacted state. This is the canonical list/search action; `action-customer-list` is not an active alias. |
| `action-customer-read` | `tenant.customer.read` / `manage-customers` | Open `surface-user-admin-customer-detail`, or hidden/not-found `system_message`. |
| `action-open-customer-create` / `action-customer-create` | `tenant.customer.create` / `manage-customers` | Open/submit `surface-user-admin-customer-create`; validate name, selected tenant, idempotency, audit, then open detail or system-message. |
| `action-open-customer-rename` / `action-customer-rename` | `tenant.customer.rename` / `manage-customers` | Open/submit `surface-user-admin-customer-rename`; handle validation/no-op/conflict/forbidden and refresh detail. |
| `action-open-customer-suspend` / `action-customer-suspend` | `tenant.customer.suspend` / `manage-customers` | Open/submit `surface-user-admin-customer-suspend-confirmation`; require reason and audit, then refresh detail or system-message. |
| `action-open-customer-reactivate` / `action-customer-reactivate` | `tenant.customer.reactivate` / `manage-customers` | Open/submit `surface-user-admin-customer-reactivate-confirmation`; handle no-op/replay safely and refresh detail. |
| `action-user-admin-show-customer-admins` | `tenant.customer_admin.list` / `manage-customer-admins` | Open `surface-user-admin-customer-admins` for the selected Customer, or safe hidden/not-found/forbidden system-message. This is the canonical Customer Admin list action; `action-customer-admin-list` is not an active alias. |
| `action-open-customer-admin-invitation-create` / `action-customer-admin-invite` | `tenant.customer_admin.invite` / `manage-customer-admins` | Open `surface-user-admin-customer-admin-invitation-create`; submission validates the Customer exists, target role is `CUSTOMER_ADMIN`, invitation delivery is ready, and returns admin detail/invitation detail or system-message. |
| Dedicated invitation/member lifecycle task actions for the selected Customer Admin target | `tenant.customer_admin.manage` / `manage-customer-admins` | Route to role preview, membership lifecycle confirmation, invitation resend/revoke confirmation, or refreshed Customer Admin detail. `action-customer-admin-manage` is not an active alias; runtime uses the specific User Admin invitation and membership task action ids after the selected Customer Admin target is resolved. Enforce last Customer Admin protection and prevent `TENANT_ADMIN` or `SAAS_OWNER_ADMIN` assignment through Customer flows. |

### States and tests

Each graph node defines loading, empty, ready, submitting, success, validation-error, forbidden, hidden-not-found, no-op, conflict/stale, partial-data, and failure states as applicable. Acceptance tests must cover dashboard-to-customer-directory traversal, directory-to-detail traversal, create/rename/suspend/reactivate dedicated form or confirmation surfaces, Customer Admin list/invite/detail/manage traversal after Customer creation, first Customer Admin bootstrap, last Customer Admin protection, selected Organization/Tenant scope, idempotent replay/no-op transitions, safe Customer Admin and sibling-tenant denials, missing capability denial, audit/work trace emission, frontend secret boundary, support-access/billing-boundary non-authority, keyboard operation, focus movement, and typed `system_message` results.

Surface-description sufficiency review: sufficient for intent-level durable collection-object readiness. The split Customer and Customer Admin graph is sufficiently unambiguous for developers/generators to implement and review without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics.

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

### Dashboard-out flow verification

The User Admin dashboard is the trunk. Every visible dashboard object must declare a target surface and action id. The required dashboard-to-surface flows are:

| Dashboard object/state | Required target flow |
|---|---|
| Visible administered population card, such as tenant employees or Customer Users | Opens `surface-user-admin-users` with backend-shaped `populationType` and selected-scope filters. |
| "Create user" / "Invite user" action for a visible population | Opens `surface-user-admin-invitation-create` with target scope, allowable role options, and outbox/provider readiness. This action is available from the dashboard and from the users directory empty/ready states. |
| Pending, stale, expired, failed-delivery, or duplicate/open invitation attention | Opens `surface-user-admin-invitation-detail` for the relevant invitation when there is one target; queue variants open `surface-user-admin-users` filtered to invitation rows whose activation opens invitation detail. |
| Dormant, disabled, risky, or review-needed user/member attention | Opens `surface-user-admin-user-detail` for a single target or `surface-user-admin-users` filtered to the relevant queue. The selected row then opens the lifecycle-aware detail surface. |
| Role/capability approval or last-admin risk attention | Opens `surface-user-admin-role-change-preview` when the pending preview is known; otherwise opens user detail with a task entry point to preview role change. |
| Support-access active/expiring/policy attention | Opens user detail for the support-access subject, then routes to `surface-user-admin-support-access-grant` for grant/extend or `surface-user-admin-support-access-revoke-confirmation` for revoke. |
| Access-review result needing human review | Opens `surface-user-admin-access-review-task`; any follow-up mutation routes to membership, role, or support-access task surfaces. |
| Identity link/relink exception | Opens `surface-user-admin-identity-exception-review`; approved recovery routes to workflow/status or user detail without provider internals. |
| Recent denial/no-op/audit evidence | Opens authorized Audit/Trace surfaces or `surface-user-admin-system-message` when evidence is redacted or forbidden. |

### Branch-return actions

Every User branch descendant (`surface-user-admin-user-detail`, `surface-user-admin-invitation-create`, `surface-user-admin-invitation-detail`, `surface-user-admin-invitation-resend-confirmation`, `surface-user-admin-invitation-revoke-confirmation`, `surface-user-admin-membership-status-confirmation`, `surface-user-admin-role-change-preview`, `surface-user-admin-support-access-grant`, `surface-user-admin-support-access-revoke-confirmation`, `surface-user-admin-access-review-task`, and `surface-user-admin-identity-exception-review`) includes the following return action unless the selected context is no longer authorized, in which case the same action returns `surface-user-admin-system-message`:

| Action id | Label | Governed backend capability/tool | Result behavior |
|---|---|---|---|
| `action-user-admin-show-users` | `Show users` or `Back to users` | `user_admin.list_members` / `search-user-directory` | Reopen `surface-user-admin-users` with backend-shaped safe filter/context preservation; no hidden users, memberships, invitations, counts, roles, or cross-scope facts are exposed. |

### Users directory row-selection routing

`surface-user-admin-users` is the collection list/search surface and must expose a **Create/Invite user** action independent of row selection whenever `user_admin.invite_user` is visible for the selected scope. Row/card activation is state-aware and backend-authored:

| Row state from backend | Row/card activation target |
|---|---|
| Active account with active membership | `surface-user-admin-user-detail`, showing role, membership, support-access, access-review, identity, audit summaries, and task entry points. |
| Invited person with no accepted account yet | `surface-user-admin-invitation-detail`, not user detail, because the durable object is the invitation until acceptance/linkage completes. |
| Invitation failed delivery, stale, expired, revoked, or duplicate/open-invite conflict | `surface-user-admin-invitation-detail` with resend/revoke/recovery task entry points determined by backend eligibility. |
| Disabled account, suspended/inactive membership, pending removal, or last-admin risk | `surface-user-admin-user-detail` with lifecycle task entry points; consequential state changes route to `surface-user-admin-membership-status-confirmation`. |
| Role/capability change pending, approval-required, or escalation-risk row | `surface-user-admin-role-change-preview` when a preview/proposal id is present; otherwise `surface-user-admin-user-detail` with a preview-role-change task entry. |
| Support access active, expiring, requested, or policy-blocked row | `surface-user-admin-user-detail` with support-access task entries; grant/extend opens `surface-user-admin-support-access-grant`, revoke opens `surface-user-admin-support-access-revoke-confirmation`. |
| Access review in progress or result-ready row | `surface-user-admin-access-review-task` when a task id is present; otherwise user detail with a start/open review task entry. |
| Identity exception, provider mismatch, or relink-required row | `surface-user-admin-identity-exception-review` when an exception id is present; otherwise user detail with identity exception summary and recovery entry point. |
| Hidden, forbidden, redacted, stale, or cross-scope row/deep link | `surface-user-admin-system-message` with safe reason/recovery, no hidden identities/counts, and trace refs where allowed. |

The backend owns row `targetSurfaceId`, `targetSurfaceType`, `targetObjectType`, `openActionId`, eligibility flags, and redaction state. The frontend must not infer target surfaces from labels, hidden role ids, or client-only status checks.

### Shared authority and language

- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active membership, and backend capabilities for the exact read, invitation, membership, role, support-access, review, identity, or audit operation.
- Frontend action visibility is advisory only. Every list row selection, detail task entry point, form submission, confirmation, decision, workflow review, deep link, and trace-open action is reauthorized server-side.
- Browser payloads use role-appropriate User Admin language: App Admin users, Tenant Admin users, tenant employees, Customer Admin users, Customer Users, memberships, invitations, support access, access reviews, and identity exceptions. They must not imply authority outside selected scope.
- Forbidden payload content: hidden users/memberships/counts, cross-scope identities, raw WorkOS/provider ids unless policy-safe, raw JWT/session data, invitation tokens/token hashes, Resend/provider secrets, full email bodies, private profile/settings data, raw model/provider config, or unredacted audit evidence.

### `surface-user-admin-users` list/search contract

- Surface id: `surface-user-admin-users`.
- Surface type: `list-search`.
- Surface contract: `user_admin.users.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch root opened from `surface-user-admin-dashboard` by `action-user-admin-show-users`, from visible dashboard population/attention cards, and from user-branch return actions. Branch descendants return through `action-user-admin-show-users` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected app-owner, tenant, or customer `AuthContext`, active membership, and backend `user_admin.list_members` capability for the selected scope. SaaS Owner/App Admin, Tenant Admin, Customer Admin, auditor/support, disabled actor, stale selected context, or missing-membership variants are backend-authorized per selected scope; unsupported or hidden direct loads return `surface-user-admin-system-message` without enumerating hidden people, memberships, invitations, customers, tenants, roles, or counts.
- User goal: find visible users, memberships, invitations, support-access markers, access-review items, and identity exceptions in the current admin scope, then open the lifecycle-appropriate inspection or task surface. The directory is discovery-only: filtering, paging, row/card activation, and create/invite entry are allowed; inline role, status, support-access, invitation, identity, or access-review mutation is forbidden.

Frontend-safe payload for `user_admin.users.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `authorityBasis`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `query`, `filters`, `sort`, `pageInfo`, `emptyMessage`, `systemStates`, and `lastResult`.
- `summary`: `{ visibleUserCount, visibleMembershipCount, visibleInvitationCount, activeCount, pendingInvitationCount, expiredInvitationCount, disabledOrSuspendedCount, supportAccessAttentionCount?, accessReviewAttentionCount?, identityExceptionCount?, providerBlockedCount?, outboxBlockedCount?, traceRefs[] }`. Counts are backend-authorized for the selected scope and omitted/redacted when they would disclose hidden app-owner, tenant, customer, or cross-scope facts.
- `rows[]`: unified backend-ordered rows with `{ rowId, recordKind: account|membership|invitation|support_access|access_review|identity_exception, displayName?, email?, scopeLabel, scopeType, customerLabel?, status, roles[], invitationStatus?, deliveryStatus?, supportAccessState?, reviewState?, identityExceptionState?, attentionBadges[], actionAvailability[], targetSurfaceId, targetSurfaceType, targetObjectType, openActionId, traceRefs[], redactionState }`. Row/card activation always submits the backend-authored `openActionId` and opens the target result surface; frontend status labels must not infer routing.
- `filters`: backend-authored safe options for text query, population type, status, role, invitation status, support-access state, review state, identity exception state, attention state, customer label where authorized, and delivery/outbox state. Filter values must not expose hidden counts, hidden role lists, unsupported scopes, or cross-tenant/customer identifiers.
- `authorizedActions[]`: refresh/search, open invite form, open visible user/detail row, open visible invitation/detail row, open support/access-review/identity task row, open admin audit evidence, and return to dashboard. Unavailable actions are omitted; disabled reasons are shown only when the reason does not reveal hidden population or policy facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default rows may show trace/evidence labels but not raw event ids, raw correlation/idempotency values, provider payloads, token hashes, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose raw WorkOS ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, Resend/provider payloads or secrets, full email bodies, model/provider config, hidden user identities/counts, hidden roles/capabilities, sibling customer identities, cross-tenant facts, private profile/settings data beyond the browser-safe User Admin summary, support-access internals outside the selected scope, or unredacted audit evidence.
- User-facing copy uses positive scoped language such as “Users visible in this Organization” or “Customer Users visible to you.” Empty visible scopes show safe zero/empty copy; direct denied or stale attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Reload this directory with backend-shaped filters, pagination, branch metadata, trace refs, and selected-scope summary. |
| `action-open-user-admin-invitation-create` | `user_admin.invite_user` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-create` with selected scope, role options, outbox/provider readiness, and branch return metadata. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Open `surface-user-admin-user-detail` for a visible account/membership; hidden, stale, disabled-actor, or cross-scope targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-invitation-detail` | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-detail` for a visible invitation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-support-access-task` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open user detail or the support grant/revoke surface selected by backend eligibility. |
| `action-open-user-admin-access-review-task` | `user_admin.access_review.read` / `run-access-review` | Open `surface-user-admin-access-review-task` for a visible review task or a safe system message when hidden/stale. |
| `action-open-user-admin-identity-exception-review` | `user_admin.identity_relink.review` | Open `surface-user-admin-identity-exception-review` for a visible exception or a safe system message when hidden/stale. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` with focus on the originating population card or attention queue. |

States and outcomes:

- Loading: skeleton directory with selected scope label and no rows until backend authorization completes.
- Empty: no visible users, memberships, invitations, or tasks match the filters; show create/invite action only when authorized for the selected scope.
- Ready: backend-derived rows, summaries, filters, branch metadata, trace/correlation, and keyboard-operable row/card actions are visible.
- Searching/submitting: preserve current filters and focus while backend reauthorizes selected context and query.
- Validation-error: invalid query/filter/page/sort is reported inline without broadening the search.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden population enumeration.
- Stale/conflict: refresh guidance when selected context, membership, invitation status, row target, or page cursor changed.
- Partial-data/failure: show safe provider/outbox/readiness messages without fake rows, hidden counts, fixture data, or provider internals.

Trace, audit, accessibility, and tests:

- Every load, search/filter, page/sort, row open, invite-form open, denied hidden target, stale result, branch return, and audit drilldown emits or links an admin work trace with correlation id, selected `AuthContext` summary, safe query/filter summary, redaction summary, and result surface id.
- Accessibility/responsive expectations: list rows are keyboard-operable, cards preserve the same backend action ids as table rows, focus returns to the originating dashboard card or list row after navigation, responsive layout does not hide action availability or redaction notices, and empty/error states remain announced to assistive tech.
- Acceptance/regression coverage must verify dashboard-to-users traversal, scoped list filtering/search/empty state, row activation to user detail and invitation detail, invitation create-form open, support/access-review/identity row routing, SaaS Owner vs Tenant Admin vs Customer Admin scope behavior, hidden/cross-tenant/customer denial without enumeration, stale row recovery, provider/outbox fail-closed indicators, audit/work trace/correlation links, frontend secret boundaries, keyboard row activation, focus return, and responsive table-to-card rendering.

Surface-description sufficiency review: `surface-user-admin-users` is sufficiently unambiguous for developers/generators to implement and review the list/search objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived row routing, role-specific scope variants, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-user-detail` show/inspection contract

- Surface id: `surface-user-admin-user-detail`.
- Surface type: `show-inspection` and task-router; it must not perform inline mutations.
- Surface contract: `user_admin.user_detail.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-users` rows, dashboard single-target attention, and authorized deep links by `action-open-user-admin-user-detail`; branch return uses `action-user-admin-show-users` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, and backend `user_admin.read_user_account` capability for the requested account or membership in the selected scope. Hidden, stale, disabled-actor, cross-tenant, sibling-customer, or out-of-scope direct loads return `surface-user-admin-system-message` without enumerating hidden identities, roles, customers, invitations, support grants, access-review tasks, or identity exceptions.
- User goal: inspect one visible access subject, understand why they are visible in the selected scope, and choose the next dedicated task surface for lifecycle, role, support-access, access-review, identity, invitation, or audit work. The detail surface is inspection-first and action-routing-only.

Frontend-safe payload for `user_admin.user_detail.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind`, `recordLabel`, `authorityBasis`, `redaction`, `traceRefs[]`, `correlationId`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `identitySummary`: browser-safe display name, email, account status, account/member/invitation identifiers only when policy-safe, identity provider family/status, verified-email state, and provider-boundary copy. It must not include raw WorkOS ids unless explicitly policy-safe, raw JWT/session data, provider payloads, secrets, or private My Account preferences.
- `membershipSummary`: selected-scope membership id, scope type/label, membership status, role labels, admin level, customer label where authorized, active/suspended/disabled/removed markers, last-admin/self-action risk summary, and stale/freshness marker.
- `roleCapabilitySummary`: current role labels, visible capability category counts, affected workstream summaries, role/capability change eligibility, approval requirement summary, and safe target action ids. Raw capability policy internals are hidden outside role-gated audit/support drilldowns.
- `invitationSummary`: pending/open/accepted/expired/revoked/failed-delivery invitation references for this subject when visible, delivery/outbox/provider readiness status, expiry/accepted timestamps, resend/revoke eligibility, and target invitation-detail action ids. Invitation tokens, token hashes, full email bodies, and provider payloads are forbidden.
- `supportAccessSummary`: active/expiring/requested/revoked/support-only state, purpose/expiry/approver summary when visible, grant/extend/revoke eligibility, policy blocker summary, and target support-access task action ids. It must not expose support session secrets or unrelated tenant/customer data.
- `accessReviewSummary`: latest review task status, review-needed badges, provider/model readiness or blocked summary, recommendation/result state when visible, human-review requirement, `noDirectMutation` marker for review output, and target access-review action ids.
- `identityExceptionSummary`: link/relink/provider mismatch state, risk level, recovery eligibility, provider-boundary redaction, and target identity-exception-review action ids without raw provider internals.
- `auditSummary`: recent browser-safe admin events, denial/no-op excerpts, trace labels, redaction notes, and role-gated audit drilldown action ids. Default view must not show raw event ids, raw correlation/idempotency values, or unredacted audit evidence.
- `availableTaskActions[]`: backend-authored action id, label, purpose, governed capability/tool, target surface id/type, target object type/id when safe, enabled/disabled state, disabled reason only when it does not disclose hidden policy facts, required confirmation/approval/reason/idempotency summary, and expected result surface.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Reload this inspection surface with current selected-scope summaries, trace refs, and no-enumeration redaction. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped filters, branch metadata, and focus hint. |
| `action-open-user-admin-invitation-detail` | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Open visible invitation detail; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-membership-status-confirmation` | `user_admin.update_member_status` / `change-membership-role-or-status` | Open dedicated lifecycle confirmation for disable/suspend/reactivate/remove/account-state work; self-action and last-admin protections are evaluated there before mutation. |
| `action-open-user-admin-role-change-preview` | `user_admin.preview_role_change` / `change-membership-role-or-status` | Open role/capability diff decision surface; commit is not performed by user detail. |
| `action-open-user-admin-support-access-grant` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open support grant/extend form with backend-supplied policy limits and approval state. |
| `action-open-user-admin-support-access-revoke-confirmation` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open revoke confirmation; no inline support-access mutation occurs on user detail. |
| `action-open-user-admin-access-review-task` | `user_admin.access_review.read` / `run-access-review` | Open durable access-review task progress/result or provider/model blocked recovery; review output remains advisory until human-routed task surfaces act. |
| `action-open-user-admin-identity-exception-review` | `user_admin.identity_relink.review` | Open identity exception review/recovery surface without provider internals. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and subject placeholder only after backend authorization starts.
- Ready: identity, membership, role/capability, invitation, support-access, access-review, identity-exception, audit, task actions, branch return, trace refs, and correlation are backend-derived.
- Empty/limited: subject is visible but has no invitation/support/review/identity-exception summaries; show safe absence copy and available task entry points only when authorized.
- Submitting/opening task: preserve detail context and focus while backend reauthorizes the target action.
- Validation-error: malformed subject id, selected context, task target, or action payload is reported without widening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden identity, role, invitation, customer, or capability enumeration.
- Conflict/stale: selected context, membership status, invitation state, role version, support grant, review task, or identity exception changed; show refresh/back-to-users recovery and trace refs.
- Provider/model/outbox fail-closed: show provider/model/outbox readiness summaries only for visible task areas; never invent fixture success or mutate source access from advisory review output.
- No-op/success: detail refresh and task-open no-ops are traceable; consequential success is shown by the dedicated result/detail surface after the task surface completes.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every refresh, branch return, task-open, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot see sibling tenants, Customer Admins cannot see sibling customers or tenant-wide users, SaaS Owner admins cannot inspect tenant application data unless a separate authorized Organization branch grants it, support-only access is bounded by policy, and disabled/missing actors receive safe recovery.
- Trace/audit contract: each detail load, denied hidden target, branch return, task-open, audit-open, stale result, and provider/model/outbox blocked result emits or links an admin work trace with actor account, selected context, subject summary only when visible, action id, capability decision, result surface, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, or sibling-scope facts.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope boundary, summary sections with semantic headings, task actions as keyboard-operable grouped controls, status/denial messages announced as status text, focus return to the originating row or first result message, and responsive stacking in identity, membership, task actions, support/review/identity, and audit order without losing action availability.
- Acceptance/regression coverage must verify users-directory-to-detail traversal, direct protected detail load, hidden/cross-tenant/customer denial without enumeration, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, branch return, lifecycle/role/support/access-review/identity task entry routing, stale/conflict recovery, last-admin/self-action risk visibility without mutation, provider/model/outbox fail-closed summaries, audit/work trace/correlation links, frontend secret boundaries, keyboard task activation, focus return, and no inline mutation from the detail surface.

Surface-description sufficiency review: `surface-user-admin-user-detail` is sufficiently unambiguous for developers/generators to implement and review the show/inspection objective without inventing payload fields, task actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived task routing, role-specific scope variants, denial/no-enumeration behavior, trace/correlation, provider/outbox/model fail-closed summaries, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-invitation-create` create-form contract

- Surface id: `surface-user-admin-invitation-create`.
- Surface type: `create-form`.
- Surface contract: `user_admin.invitation_create.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-dashboard` visible invite actions, `surface-user-admin-users` empty/ready invite actions, and authorized deep links by `action-open-user-admin-invitation-create`; branch return uses `action-user-admin-show-users` with backend-shaped safe filters and focus hints.
- Required context: authenticated active account, selected app-owner, tenant, or customer `AuthContext`, active actor membership, and backend `user_admin.invite_user` capability for the exact target scope. SaaS Owner/App Admin, Tenant Admin, and Customer Admin role options are backend-derived for the selected scope; disabled actors, stale contexts, missing memberships, support-only contexts, hidden targets, cross-tenant/customer scopes, and missing capability attempts return `surface-user-admin-system-message` without enumerating hidden people, roles, customers, tenants, invitations, or policies.
- User goal: invite a user into the currently selected authorized scope with a safe role, clear reason, outbox/provider readiness, idempotent submission, and success routing to the lifecycle-aware invitation detail surface. The form never mutates an existing membership inline and never sends or claims email delivery outside the governed invitation/outbox path.

Frontend-safe payload for `user_admin.invitation_create.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `authorityBasis`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `dashboardOriginQueueId?`, `traceRefs[]`, `correlationId`, `redaction`, `boundaryNotice`, `formState`, `validationMessages`, `systemStates`, and `lastResult`.
- `form`: `{ emailDraft?, displayNameDraft?, reasonDraft?, targetRoleOptions[], selectedTargetRoles[], targetCustomerOptions?, selectedCustomerId?, expiryOptions[], selectedExpiry?, idempotencyKeyHint, submitLabel, cancelActionId, submitActionId, disabledReason? }`. Role, customer, and expiry options are backend-authored for the selected scope; unsupported roles, sibling customers, hidden tenants, provider-managed fields, and policy-internal ids are not rendered.
- `scopeSummary`: browser-safe selected-scope label/type, visible administered population, invite authority summary, customer boundary when applicable, support-access boundary when applicable, and stale-context freshness marker.
- `policyContext`: `{ duplicateOpenInvitePolicy, selfInvitePolicy, lastAdminPolicyImpact?, allowedEmailDomains?, maxPendingInviteCount?, approvalRequired?, reasonRequired, traceRefs[] }` rendered as user-facing guidance rather than raw policy clauses.
- `deliveryReadiness`: `{ outboxStatus, providerStatus, retryEligible, failClosedMessage?, expectedDeliveryChannel: in_app_or_email, noFakeSuccess, traceRefs[] }`; provider/outbox blocked states must not fabricate invitation delivery or hide failed readiness.
- `authorizedActions[]`: validate draft, submit invitation, return to users, return to dashboard, open visible duplicate invitation detail when authorized, and open admin audit evidence. Unavailable actions are omitted or include only a safe disabled reason.
- Role-gated `diagnosticMetadata` may include support/audit labels for trace or outbox evidence; default payload must not expose raw trace event ids, idempotency/correlation secrets, provider payloads, token hashes, or policy implementation ids.

Redaction and forbidden payload boundaries:

- Must not expose raw WorkOS ids unless explicitly policy-safe, raw JWT/session values, invitation tokens/token hashes, Resend/provider payloads or secrets, full email body content, hidden user or invitation identities/counts, hidden role/capability lists, sibling customer or cross-tenant facts, private My Account settings/profile data beyond the browser-safe target draft, model/provider config, raw idempotency keys, or unredacted audit evidence.
- User-facing copy uses scoped language such as “Invite a Tenant Admin”, “Invite a Customer User”, or “Invite a user visible in this Organization.” Direct denied, duplicate-hidden, stale, or cross-scope attempts use no-enumeration recovery through `surface-user-admin-system-message`.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-invitation-create` | `user_admin.invite_user` / `create-or-resend-invitation` | Open this create form with selected scope, role/customer/expiry options, provider/outbox readiness, branch metadata, trace refs, and safe draft defaults. |
| `action-validate-user-admin-invitation-draft` | `user_admin.invite_user` / `create-or-resend-invitation` | Validate email, display label, reason, role, customer/scope, duplicate/open-invite state, and policy limits without creating or sending an invitation; return inline validation messages on this form. |
| `action-submit-user-admin-invitation` | `user_admin.invite_user` / `create-or-resend-invitation` | Reauthorize selected context and target scope, validate draft, enforce idempotency and duplicate/open-invite policy, create or reuse a visible invitation, enqueue delivery only when provider/outbox readiness allows it, audit the attempt, and route to `surface-user-admin-invitation-detail`. Validation, duplicate-hidden, provider/outbox blocked, stale/conflict, approval-required, no-op, or denial results return this form with inline state or `surface-user-admin-system-message`; fake delivery success is forbidden. |
| `action-open-user-admin-invitation-detail` | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Open the visible created or duplicate invitation detail; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with safe filter/context preservation and focus on the invite action or created invitation row when visible. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-user-admin-return-dashboard` | `user_admin.view_overview` / `search-user-directory` | Return to `surface-user-admin-dashboard` with focus on the originating invite/population card when authorized. |

States and outcomes:

- Loading: skeleton create form with selected scope label only until backend authorization and options complete.
- Ready: backend-authored scope, role/customer/expiry options, boundary notice, policy context, delivery readiness, trace/correlation, branch return, and submit action are visible.
- Draft validation-error: invalid email, unsupported domain, missing required reason, unsupported role, target customer outside selected scope, duplicate open invitation, self-invite, disabled actor, max pending invites, stale selected context, or policy conflict is reported inline without creating or sending an invitation.
- Submitting: preserve draft values and focus while backend reauthorizes selected context, target role/scope/customer, duplicate state, provider/outbox readiness, idempotency, and policy.
- Success: route to `surface-user-admin-invitation-detail` with browser-safe invitation summary, delivery/outbox status, trace/correlation, and branch return metadata.
- Duplicate/no-op: route to the existing visible invitation detail when authorized or a safe no-enumeration system message when the duplicate target is hidden.
- Provider/outbox blocked: return a blocked state or system message with recovery steps, trace refs, `noFakeSuccess`, no invitation-token exposure, and no fake delivery state.
- Forbidden/hidden-not-found/stale/conflict: return `surface-user-admin-system-message` with no hidden population, role, customer, tenant, invitation, or policy enumeration.
- Partial-data/failure: keep draft values safe for retry and show recovery without fixture/mock delivery data.

Trace, audit, accessibility, and tests:

- Every open, draft validation, submit, duplicate/no-op, provider/outbox blocked result, denial, stale/conflict, branch return, invitation-detail routing, and audit drilldown emits or links an admin work trace with actor account, selected `AuthContext`, safe target email/domain summary, target scope summary, action id, capability decision, provider/outbox readiness summary, result surface id, redaction level, and correlation id.
- Accessibility/responsive expectations: the form has a stable heading, selected-scope boundary notice, labeled email/display/reason/role/customer/expiry controls, inline errors associated with fields, status messages announced as status text, keyboard-operable submit/cancel/return controls, focus restored to the first invalid field or result message, and responsive stacking that keeps policy/readiness guidance visible before submission.
- Acceptance/regression coverage must verify dashboard/list-to-create traversal, protected direct create-form load, role-specific SaaS Owner/Tenant Admin/Customer Admin option shaping, successful invitation submission and detail routing, idempotent replay or duplicate-open-invite handling, email/role/customer/reason validation, provider/outbox fail-closed behavior with `noFakeSuccess`, disabled/stale context denial, hidden/cross-tenant/customer denial without enumeration, branch return to users, audit/work trace/correlation links, frontend secret boundaries, keyboard form operation, focus recovery, and responsive form rendering.

Surface-description sufficiency review: `surface-user-admin-invitation-create` is sufficiently unambiguous for developers/generators to implement and review the create-form objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-authored role/customer/expiry options, idempotent invitation creation or duplicate routing, provider/outbox fail-closed behavior, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-invitation-detail` show/inspection and workflow-status contract

- Surface id: `surface-user-admin-invitation-detail`.
- Surface type: `show-inspection` and lifecycle `workflow-status`; it inspects one invitation and routes resend/revoke/recovery work to dedicated lifecycle surfaces instead of mutating invitation state inline.
- Surface contract: `user_admin.invitation_detail.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-users` invitation rows, `surface-user-admin-invitation-create` success/duplicate results, dashboard single-target invitation attention, SaaS Owner/Admin and Organization/Customer Admin invitation rows that map into the shared detail contract, and authorized deep links by `action-open-user-admin-invitation-detail`; branch return uses the relevant backend-authored return action (`action-user-admin-show-users`, `action-user-admin-show-saas-owner-admins`, `action-user-admin-show-organizations`, or `action-user-admin-show-customers`) with safe filter/focus hints.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, and backend `user_admin.acceptance_status.read` or the narrower app-owner/organization/customer admin invitation-read capability for the requested invitation and selected scope. Hidden, stale, revoked-out-of-scope, cross-tenant, sibling-customer, support-only, disabled-actor, missing-membership, and missing-capability attempts return `surface-user-admin-system-message` without revealing whether the invitation, target email, role, tenant, customer, or delivery record exists.
- User goal: understand one visible invitation's lifecycle, target scope, requested role, delivery/outbox state, expiry/acceptance/revocation status, duplicate/open-invite relationship, and safe next actions. The surface is inspection-first; resend opens `surface-user-admin-invitation-resend-confirmation`, revoke opens `surface-user-admin-invitation-revoke-confirmation`, and acceptance/account-link recovery opens the backend-selected deterministic detail, workflow, or system-message surface.

Frontend-safe payload for `user_admin.invitation_detail.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: invitation`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `invitationSummary`: browser-safe target display name/email domain or masked email according to policy, target scope label/type, requested role labels, invitation status (`draft`, `pending`, `sent`, `accepted`, `expired`, `revoked`, `failed_delivery`, `provider_blocked`, `outbox_blocked`, `duplicate_open`, `stale`, or `unknown_safe`), created/sent/accepted/expires/revoked timestamps when authorized, inviter summary, and freshness marker. It must not include invitation tokens, token hashes, raw WorkOS ids unless explicitly policy-safe, raw JWT/session data, full email content, or provider payloads.
- `deliveryStatus`: `{ channel, outboxStatus, providerStatus, retryEligible, lastAttemptSummary?, nextRetryAt?, failClosedMessage?, noFakeSuccess, traceRefs[] }` with provider/outbox details summarized in user language and raw Resend/provider error bodies kept internal or role-gated.
- `lifecycleStatus`: `{ currentState, terminal, acceptedAccountSummary?, linkedMembershipSummary?, expiredReason?, revokedReasonSummary?, duplicateInvitationSummary?, staleVersion?, recoveryGuidance[], traceRefs[] }`; account/membership links are shown only when the selected context may inspect them.
- `eligibility`: `{ canResend, resendDisabledReason?, canRevoke, revokeDisabledReason?, canOpenAcceptedUser, canOpenAudit, approvalRequired?, reasonRequired?, idempotencyHint?, traceRefs[] }` where disabled reasons are user-safe and never enumerate hidden policy or population facts.
- `availableTaskActions[]`: backend-authored action id, label, purpose, governed capability/tool, target surface id/type, target object id when safe, enabled/disabled state, idempotency/correlation summary, required reason/confirmation, result surface, and safe disabled reason when it does not disclose hidden facts.
- `diagnosticMetadata` is role-gated and visually subordinate; default payload may expose trace/evidence labels but not raw trace event ids, idempotency/correlation secrets, provider payloads, email bodies, token hashes, hidden capabilities, or policy implementation ids.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-invitation-detail` | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Reload this detail with authorized invitation lifecycle, delivery/outbox state, branch metadata, trace refs, and no-enumeration redaction. Hidden/stale targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-invitation-resend-confirmation` | `user_admin.resend_invitation` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-resend-confirmation` for an eligible visible pending/failed/expired invitation; ineligible, terminal, stale, provider-blocked, hidden, or out-of-scope targets return a safe system message or disabled state. |
| `action-open-user-admin-invitation-revoke-confirmation` | `user_admin.revoke_invitation` / `create-or-resend-invitation` | Open `surface-user-admin-invitation-revoke-confirmation` for an eligible visible invitation; accepted, already revoked, hidden, stale, last-admin-risk, or out-of-scope targets return safe no-op/denial/system-message recovery. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Open the accepted account or linked membership only when authorized; otherwise return `surface-user-admin-system-message` without exposing hidden account existence. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped invitation filters and focus hint when this detail belongs to the User Directory branch. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and invitation placeholder only after backend authorization starts.
- Ready: invitation summary, lifecycle status, delivery/outbox readiness, duplicate/open-invite context, eligibility, task actions, branch return, trace refs, correlation id, and redaction are backend-derived.
- Empty/limited: visible invitation has no delivery attempts, duplicate context, accepted account, or recovery tasks; show safe absence copy and only authorized actions.
- Submitting/opening task: preserve invitation context and focus while backend reauthorizes target action and current invitation version.
- Validation-error: malformed invitation id, stale action payload, missing reason, invalid branch context, or unsupported requested action is reported without widening scope.
- Forbidden/hidden-not-found: return `surface-user-admin-system-message` with no hidden target email, role, tenant, customer, delivery, or invitation enumeration.
- Conflict/stale: invitation status, expiry, duplicate state, selected context, actor membership, or provider/outbox readiness changed; show refresh/back-to-list recovery and trace refs.
- Provider/outbox fail-closed: show delivery readiness and retry/recovery guidance with `noFakeSuccess`; do not fabricate sent/resent email success.
- No-op/success: repeated read/open, already-terminal resend/revoke attempts, duplicate-open invite reuse, and branch returns are traceable; consequential lifecycle success is shown by the resend/revoke confirmation result or refreshed detail surface.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every detail load, task-open, accepted-user open, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot inspect sibling tenants, Customer Admins cannot inspect sibling customers or tenant-wide invitations outside their scope, SaaS Owner admins cannot inspect tenant application invitations without an authorized Organization branch, support-only access is bounded by policy, and disabled/missing actors receive safe recovery.
- Trace/audit contract: each detail load, hidden denial, stale result, resend/revoke task-open, accepted-account open, provider/outbox blocked state, branch return, and audit-open emits or links an admin work trace with actor account, selected context, invitation summary only when visible, action id, capability decision, delivery/outbox readiness summary, result surface, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, or sibling-scope facts.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope boundary notice, lifecycle/delivery summaries with semantic headings, task actions as keyboard-operable grouped controls, status/denial messages announced as status text, focus return to the originating row/form/action result, and responsive stacking that preserves lifecycle, delivery, eligibility, trace, and branch-return visibility.
- Acceptance/regression coverage must verify create-to-detail and list-row-to-detail traversal, protected direct detail load, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, branch return, accepted-account open when authorized, resend/revoke task entry routing, terminal/no-op and stale/conflict recovery, duplicate/open-invite context, provider/outbox fail-closed summaries with `noFakeSuccess`, audit/work trace/correlation links, frontend secret boundaries, keyboard action activation, focus return, and responsive workflow-status rendering.

Surface-description sufficiency review: `surface-user-admin-invitation-detail` is sufficiently unambiguous for developers/generators to implement and review the show/inspection and lifecycle workflow-status objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived invitation state and task routing, role-specific scope variants, denial/no-enumeration behavior, trace/correlation, provider/outbox fail-closed summaries, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-invitation-resend-confirmation` lifecycle-confirmation contract

- Surface id: `surface-user-admin-invitation-resend-confirmation`.
- Surface type: `lifecycle-confirmation`; it confirms one invitation resend attempt and never changes invitation lifecycle inline before submit.
- Surface contract: `user_admin.invitation_resend_confirmation.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-invitation-detail`, invitation attention queues, and authorized deep links by `action-open-user-admin-invitation-resend-confirmation`; branch return uses the backend-authored invitation-detail return action and `action-user-admin-show-users` for the containing list branch.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, a visible invitation in the selected scope, and backend `user_admin.resend_invitation` or narrower app-owner/organization/customer invitation-resend authority. Hidden, revoked, accepted, cross-tenant, sibling-customer, support-only, disabled-actor, missing-membership, stale, and missing-capability attempts return `surface-user-admin-system-message` without revealing whether the target invitation, email, role, delivery record, tenant, or customer exists.
- User goal: verify the visible invitation, understand why resend is allowed or blocked, optionally provide a reason, confirm a governed resend attempt, and return to refreshed invitation detail with delivery/outbox status and trace evidence. This surface must not fabricate email delivery success; provider/outbox unavailability is a fail-closed result with recovery steps and `noFakeSuccess`.

Frontend-safe payload for `user_admin.invitation_resend_confirmation.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: invitation`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `invitationSummary`: masked target email/display label according to policy, target scope label/type, requested role labels, current invitation status, expiry timestamp, last sent/attempt timestamp, inviter summary, and freshness marker. Invitation tokens, token hashes, raw provider ids, raw JWT/session data, full email bodies, and hidden target details are forbidden.
- `resendEligibility`: `{ canResend, disabledReason?, terminalState?, staleVersion?, retryWindow?, duplicateOpenInviteSummary?, approvalRequired?, reasonRequired, traceRefs[] }` with disabled reasons written in user-safe language and no hidden capability or population enumeration.
- `deliveryReadiness`: `{ channel, outboxStatus, providerStatus, retryEligible, failClosedMessage?, lastAttemptSummary?, nextRetryAt?, noFakeSuccess, traceRefs[] }`; raw Resend/provider errors, outbox payloads, SMTP/email bodies, provider secrets, and diagnostic stack traces are internal or role-gated only.
- `confirmationForm`: `{ reasonDraft?, reasonRequired, confirmationCopy, submitLabel, cancelActionId, submitActionId, idempotencyHint, disabledReason? }`; the idempotency hint is user-language only and never exposes raw idempotency/correlation secrets.
- `authorizedActions[]`: confirm resend, cancel/back to invitation detail, return to users, open authorized audit evidence, and refresh eligibility. Unavailable actions are omitted or use safe disabled reasons that do not reveal hidden policy facts.
- Role-gated `diagnosticMetadata` may include audit/support labels for trace or outbox evidence; the default browser payload must not expose raw event ids, correlation/idempotency keys, provider payloads, email contents, token hashes, hidden roles, hidden capabilities, or policy implementation ids.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-invitation-resend-confirmation` | `user_admin.resend_invitation` / `create-or-resend-invitation` | Open this confirmation with backend-derived invitation summary, eligibility, delivery readiness, branch metadata, and trace refs; hidden, terminal, stale, or out-of-scope targets return `surface-user-admin-system-message`. |
| `action-confirm-user-admin-invitation-resend` | `user_admin.resend_invitation` / `create-or-resend-invitation` | Reauthorize selected context and invitation visibility, validate reason/eligibility/current version, enforce idempotency, enqueue a resend only when outbox/provider readiness allows it, audit the attempt, and return refreshed `surface-user-admin-invitation-detail`. Validation, stale/conflict, already-terminal, no-op replay, provider/outbox blocked, hidden, or denial results return this confirmation with inline state or `surface-user-admin-system-message`; fake delivery success is forbidden. |
| `action-open-user-admin-invitation-detail` | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Cancel or return to the visible invitation detail with safe focus/context preservation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped invitation filters and focus hint when authorized. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and invitation placeholder only after backend authorization starts.
- Ready: invitation summary, resend eligibility, delivery readiness, confirmation copy, branch return, trace refs, correlation id, and redaction are backend-derived.
- Validation-error: malformed invitation id, missing required reason, stale version, unsupported action, invalid selected context, or ineligible state is reported without running a resend.
- Submitting: preserve reason and focus while backend reauthorizes selected context, invitation version, eligibility, provider/outbox readiness, and idempotency.
- Success: route to `surface-user-admin-invitation-detail` with updated delivery status, last attempt summary, trace/correlation, and branch return metadata.
- Duplicate/no-op replay: repeated confirm with the same idempotency scope returns the current invitation detail or a no-op confirmation/result state with trace evidence and no extra delivery attempt.
- Provider/outbox blocked: return this confirmation or `surface-user-admin-system-message` with recovery steps, retry eligibility, `noFakeSuccess`, and no invitation-token or provider-payload exposure.
- Forbidden/hidden-not-found/stale/conflict: return `surface-user-admin-system-message` with no hidden invitation, target email, role, tenant, customer, policy, or provider enumeration.
- Partial-data/failure: keep only safe draft reason text for retry and show user-safe recovery without fixture/mock delivery state.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, confirm, cancel/detail return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot resend sibling-tenant invitations, Customer Admins cannot resend sibling-customer or tenant-wide invitations, SaaS Owner admins cannot resend tenant/customer invitations without an authorized Organization/Customer branch, support-only contexts are bounded by policy, and disabled/missing actors receive safe recovery.
- Trace/audit contract: each confirmation load, eligibility denial, submit, provider/outbox blocked result, no-op replay, stale/conflict result, hidden denial, detail return, branch return, and audit-open emits or links an admin work trace with actor account, selected context, invitation summary only when visible, action id, capability decision, delivery/outbox readiness summary, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, email bodies, hidden roles, or sibling-scope facts.
- Accessibility/responsive expectations: the confirmation has a stable heading, selected-scope boundary notice, invitation and readiness summaries with semantic headings, reason field with associated validation, explicit consequence/confirmation copy, keyboard-operable confirm/cancel/return controls, status messages announced as status text, focus restored to the first invalid field or result message, and responsive stacking that keeps eligibility and fail-closed recovery visible before submission.
- Acceptance/regression coverage must verify invitation-detail-to-resend traversal, protected direct confirmation load, successful resend and refreshed-detail routing, idempotent replay/no duplicate delivery attempt, required-reason validation, terminal/ineligible/no-op behavior, stale/conflict recovery, provider/outbox fail-closed behavior with `noFakeSuccess`, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, branch return to invitation detail and users, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus recovery, and responsive lifecycle-confirmation rendering.

Surface-description sufficiency review: `surface-user-admin-invitation-resend-confirmation` is sufficiently unambiguous for developers/generators to implement and review the lifecycle-confirmation objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived resend eligibility, idempotent delivery attempts, provider/outbox fail-closed behavior, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-invitation-revoke-confirmation` destructive-lifecycle-confirmation contract

- Surface id: `surface-user-admin-invitation-revoke-confirmation`.
- Surface type: `destructive-lifecycle-confirmation`; it confirms one invitation revocation and never mutates invitation lifecycle until the user submits the governed confirm action.
- Surface contract: `user_admin.invitation_revoke_confirmation.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-invitation-detail`, invitation attention queues, Organization/Customer/Admin invitation detail mappings, and authorized deep links by `action-open-user-admin-invitation-revoke-confirmation`; branch return uses the backend-authored invitation-detail return action and `action-user-admin-show-users` for the containing list branch.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, a visible pending/sent/failed/expired invitation in the selected scope, and backend `user_admin.revoke_invitation` or narrower app-owner/organization/customer invitation-revoke authority. Hidden, accepted, already revoked, cross-tenant, sibling-customer, support-only, disabled-actor, missing-membership, stale, last-admin-risk, and missing-capability attempts return `surface-user-admin-system-message` without revealing whether the target invitation, email, role, tenant, customer, delivery record, or policy fact exists.
- User goal: verify the visible invitation and its scope, understand the consequence that the invite link will no longer be usable, provide the required revocation reason/confirmation, submit a traceable revoke, and return to refreshed invitation detail or safe recovery. This surface is destructive and advisory copy must make clear that revocation does not disable an already accepted account or membership; accepted targets route to user/member lifecycle task surfaces instead.

Frontend-safe payload for `user_admin.invitation_revoke_confirmation.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: invitation`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `invitationSummary`: masked target email/display label according to policy, target scope label/type, requested role labels, current invitation status, created/sent/expires timestamps, inviter summary, accepted/revoked markers when visible, and freshness marker. Invitation tokens, token hashes, raw provider ids, raw JWT/session data, full email bodies, hidden target facts, and provider payloads are forbidden.
- `revokeEligibility`: `{ canRevoke, disabledReason?, terminalState?, staleVersion?, acceptedAccountSummary?, lastAdminRisk?, approvalRequired?, reasonRequired, confirmationRequired, traceRefs[] }` with user-safe disabled reasons and no hidden role, capability, population, or policy enumeration.
- `consequenceSummary`: `{ consequenceCopy, affectedAccessCopy, acceptedAccountCopy?, deliveryCancellationCopy, noDirectMembershipMutation, noFakeSuccess, recoveryGuidance[], traceRefs[] }`; it explains the invite can no longer be accepted and that any existing account/membership changes require separate authorized lifecycle tasks.
- `confirmationForm`: `{ reasonDraft?, reasonRequired, confirmationTextRequired?, confirmationTextPrompt?, submitLabel, cancelActionId, submitActionId, idempotencyHint, disabledReason? }`; the idempotency hint is user-language only and never exposes raw idempotency/correlation secrets.
- `authorizedActions[]`: confirm revoke, cancel/back to invitation detail, return to users, open authorized audit evidence, and refresh eligibility. Unavailable actions are omitted or use safe disabled reasons that do not reveal hidden policy or population facts.
- Role-gated `diagnosticMetadata` may include audit/support labels for trace evidence; the default browser payload must not expose raw event ids, correlation/idempotency keys, provider payloads, email contents, token hashes, hidden roles, hidden capabilities, or policy implementation ids.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-invitation-revoke-confirmation` | `user_admin.revoke_invitation` / `create-or-resend-invitation` | Open this confirmation with backend-derived invitation summary, revoke eligibility, consequence copy, branch metadata, and trace refs; hidden, accepted, already revoked, stale, last-admin-risk, or out-of-scope targets return `surface-user-admin-system-message` or a safe disabled state. |
| `action-confirm-user-admin-invitation-revoke` | `user_admin.revoke_invitation` / `create-or-resend-invitation` | Reauthorize selected context and invitation visibility, validate reason/confirmation/current version, enforce idempotency, revoke the invitation only when eligible, audit the attempt, and return refreshed `surface-user-admin-invitation-detail`. Validation, stale/conflict, already-terminal, no-op replay, hidden, last-admin-risk, accepted-target, or denial results return this confirmation with inline state or `surface-user-admin-system-message`; fake success and direct membership mutation are forbidden. |
| `action-open-user-admin-invitation-detail` | `user_admin.acceptance_status.read` / `create-or-resend-invitation` | Cancel or return to the visible invitation detail with safe focus/context preservation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped invitation filters and focus hint when authorized. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and invitation placeholder only after backend authorization starts.
- Ready: invitation summary, revoke eligibility, consequence summary, confirmation copy, branch return, trace refs, correlation id, and redaction are backend-derived.
- Validation-error: malformed invitation id, missing required reason, missing confirmation text, stale version, unsupported action, invalid selected context, accepted target, or ineligible state is reported without revoking.
- Submitting: preserve reason/confirmation and focus while backend reauthorizes selected context, invitation version, eligibility, last-admin/self-action protections, and idempotency.
- Success: route to `surface-user-admin-invitation-detail` with revoked lifecycle status, revoked reason summary, trace/correlation, and branch return metadata.
- Duplicate/no-op replay: repeated confirm with the same idempotency scope returns current invitation detail or a no-op confirmation/result state with trace evidence and no additional lifecycle mutation.
- Forbidden/hidden-not-found/stale/conflict/last-admin-risk: return `surface-user-admin-system-message` with no hidden invitation, target email, role, tenant, customer, policy, provider, or population enumeration.
- Partial-data/failure: keep only safe draft reason/confirmation text for retry and show user-safe recovery without fixture/mock lifecycle state.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, confirm, cancel/detail return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot revoke sibling-tenant invitations, Customer Admins cannot revoke sibling-customer or tenant-wide invitations, SaaS Owner admins cannot revoke tenant/customer invitations without an authorized Organization/Customer branch, support-only contexts are bounded by policy, and disabled/missing actors receive safe recovery. Accepted invitations must route to authorized user/member lifecycle tasks rather than revoking the already-consumed invite.
- Trace/audit contract: each confirmation load, eligibility denial, submit, no-op replay, stale/conflict result, hidden denial, last-admin-risk denial, accepted-target recovery, detail return, branch return, and audit-open emits or links an admin work trace with actor account, selected context, invitation summary only when visible, action id, capability decision, lifecycle result, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, email bodies, hidden roles, or sibling-scope facts.
- Accessibility/responsive expectations: the confirmation has a stable heading, selected-scope boundary notice, invitation and consequence summaries with semantic headings, reason/confirmation fields with associated validation, explicit destructive copy, keyboard-operable confirm/cancel/return controls, status messages announced as status text, focus restored to the first invalid field or result message, and responsive stacking that keeps consequence and recovery guidance visible before submission.
- Acceptance/regression coverage must verify invitation-detail-to-revoke traversal, protected direct confirmation load, successful revoke and refreshed-detail routing, idempotent replay/no duplicate lifecycle mutation, required-reason and confirmation validation, accepted/already-revoked/ineligible/no-op behavior, stale/conflict recovery, last-admin-risk and self-action protections where applicable, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, branch return to invitation detail and users, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus recovery, and responsive destructive-lifecycle-confirmation rendering.

Surface-description sufficiency review: `surface-user-admin-invitation-revoke-confirmation` is sufficiently unambiguous for developers/generators to implement and review the destructive-lifecycle-confirmation objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived revoke eligibility, idempotent lifecycle mutation, accepted/terminal/no-op recovery, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-membership-status-confirmation` destructive-lifecycle-confirmation contract

- Surface id: `surface-user-admin-membership-status-confirmation`.
- Surface type: `destructive-lifecycle-confirmation` for disable, suspend, remove, and account-disable changes; `lifecycle-confirmation` behavior is allowed only for non-destructive reactivation while preserving the same single-purpose contract.
- Surface contract: `user_admin.membership_status_confirmation.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-user-detail`, dashboard/user-list lifecycle attention, and authorized deep links by `action-open-user-admin-membership-status-confirmation`; branch return uses `action-open-user-admin-user-detail` for the visible subject and `action-user-admin-show-users` for the containing list branch.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, a visible target account or membership in the selected scope, and backend `user_admin.update_member_status` authority through `change-membership-role-or-status`. Hidden users, stale memberships, sibling tenants/customers, self-disable/self-remove attempts, last-admin-loss risk, support-only contexts, disabled actors, missing memberships, and missing capability attempts return `surface-user-admin-system-message` without revealing hidden identities, roles, customers, tenants, or policy facts.
- User goal: verify the visible membership/account and selected scope, understand the consequence of the proposed lifecycle change, provide required reason/confirmation, submit one traceable status change, and return to refreshed user detail or safe recovery. The surface never changes roles/capabilities, support access, invitations, identity provider state, or source access-review recommendations inline.

Frontend-safe payload for `user_admin.membership_status_confirmation.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: membership|account`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `targetSummary`: browser-safe display name/email or masked identifier, account id and membership id only when policy-safe, selected scope label/type, customer label where authorized, current account status, current membership status, role labels/admin level, support-access presence summary, and freshness marker. Raw WorkOS ids unless policy-safe, raw JWT/session values, provider payloads, private My Account settings, hidden roles/capabilities, sibling-scope facts, and unredacted audit evidence are forbidden.
- `proposedLifecycleChange`: `{ requestedOperation, currentState, proposedState, destructive, reversible, affectsSignIn, affectsSelectedScopeOnly, affectsCustomerScope?, accountWideImpact?, roleUnchanged, invitationUnchanged, traceRefs[] }` where operations include `disable_account`, `reactivate_account`, `suspend_membership`, `reactivate_membership`, and `remove_membership` only when backend policy exposes them for the selected target.
- `eligibility`: `{ canSubmit, disabledReason?, lastAdminRisk?, selfActionRisk?, approvalRequired?, reasonRequired, confirmationRequired, staleVersion?, policySummary, traceRefs[] }` with user-safe disabled reasons and no hidden policy, population, role, or capability enumeration.
- `consequenceSummary`: `{ consequenceCopy, affectedAccessCopy, downstreamWorkstreamImpact[], recoveryGuidance[], noRoleMutation, noSupportAccessMutation, noInvitationMutation, noDirectAccessReviewMutation, noFakeSuccess, traceRefs[] }`.
- `confirmationForm`: `{ reasonDraft?, reasonRequired, confirmationTextRequired?, confirmationTextPrompt?, submitLabel, cancelActionId, submitActionId, idempotencyHint, disabledReason? }`; the idempotency hint is user-language only and never exposes raw idempotency/correlation secrets.
- `authorizedActions[]`: confirm lifecycle change, cancel/back to user detail, return to users, open role preview when status change is not the right task, open authorized audit evidence, and refresh eligibility. Unavailable actions are omitted or use safe disabled reasons that do not reveal hidden facts.
- Role-gated `diagnosticMetadata` may include audit/support trace labels; default browser payload must not expose raw event ids, correlation/idempotency keys, provider payloads, token/session data, hidden roles/capabilities, policy implementation ids, or raw account-provider state.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-membership-status-confirmation` | `user_admin.update_member_status` / `change-membership-role-or-status` | Open this confirmation with backend-derived target summary, proposed operation, eligibility, consequence copy, branch metadata, and trace refs; hidden, stale, self-action, last-admin-risk, or out-of-scope targets return `surface-user-admin-system-message` or a safe disabled state. |
| `action-confirm-user-admin-membership-status-change` | `user_admin.update_member_status` / `change-membership-role-or-status` | Reauthorize selected context and target visibility, validate reason/confirmation/current version/requested operation, enforce last-admin and self-action protections, enforce idempotency, mutate only the requested account or membership lifecycle state, audit the attempt, and return refreshed `surface-user-admin-user-detail`. Validation, stale/conflict, already-in-state, no-op replay, approval-required, hidden, last-admin-risk, self-action, or denial results return this confirmation with inline state or `surface-user-admin-system-message`; fake success and unrelated role/support/invitation mutations are forbidden. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Cancel or return to the visible user detail with safe focus/context preservation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped lifecycle filters and focus hint when authorized. |
| `action-open-user-admin-role-change-preview` | `user_admin.preview_role_change` / `change-membership-role-or-status` | Route to role/capability preview when the intended work is a role change rather than lifecycle change. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and target placeholder only after backend authorization starts.
- Ready: target summary, proposed lifecycle change, eligibility, consequence summary, confirmation form, branch return, trace refs, correlation id, and redaction are backend-derived.
- Validation-error: malformed target id, unsupported requested operation, missing required reason, missing confirmation text, stale version, invalid selected context, self-action, or ineligible state is reported without mutation.
- Submitting: preserve reason/confirmation and focus while backend reauthorizes selected context, target visibility, current status/version, last-admin/self-action policy, and idempotency.
- Success: route to `surface-user-admin-user-detail` with updated account/membership status, lifecycle result summary, trace/correlation, and branch return metadata.
- Duplicate/no-op replay: repeated confirm with the same idempotency scope or already-in-state target returns refreshed user detail or a no-op confirmation/result state with trace evidence and no additional lifecycle mutation.
- Approval-required: return a decision/system-message surface or disabled confirmation state with safe next steps when policy requires another admin; do not perform partial mutation.
- Forbidden/hidden-not-found/stale/conflict/last-admin-risk/self-action-risk: return `surface-user-admin-system-message` with no hidden user, role, tenant, customer, membership, policy, or population enumeration.
- Partial-data/failure: keep only safe draft reason/confirmation text for retry and show user-safe recovery without fixture/mock lifecycle state.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, confirm, cancel/detail return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot mutate sibling-tenant memberships, Customer Admins cannot mutate sibling-customer or tenant-wide memberships outside their scope, SaaS Owner admins cannot mutate tenant/customer memberships without an authorized Organization/Customer branch, support-only contexts are bounded by policy, disabled/missing actors receive safe recovery, self-disable/self-remove is denied unless an explicit future policy permits it, and last-admin-loss is denied or routed to approval/recovery.
- Trace/audit contract: each confirmation load, eligibility denial, submit, no-op replay, stale/conflict result, hidden denial, last-admin/self-action denial, detail return, branch return, approval-required result, and audit-open emits or links an admin work trace with actor account, selected context, target summary only when visible, requested operation, capability decision, lifecycle result, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, provider secrets, hidden roles, hidden capabilities, or sibling-scope facts.
- Accessibility/responsive expectations: the confirmation has a stable heading, selected-scope boundary notice, target and consequence summaries with semantic headings, reason/confirmation fields with associated validation, explicit destructive or reactivation copy, keyboard-operable confirm/cancel/return controls, status messages announced as status text, focus restored to the first invalid field or result message, and responsive stacking that keeps consequence, eligibility, and recovery guidance visible before submission.
- Acceptance/regression coverage must verify user-detail-to-membership-status traversal, protected direct confirmation load, successful suspend/disable/reactivate/remove routing to refreshed user detail as policy allows, idempotent replay/no duplicate lifecycle mutation, required-reason and confirmation validation, already-in-state/no-op behavior, stale/conflict recovery, last-admin and self-action protections, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, branch return to user detail and users, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus recovery, responsive destructive-lifecycle-confirmation rendering, and no mutation of roles/support access/invitations/access-review recommendations.

Surface-description sufficiency review: `surface-user-admin-membership-status-confirmation` is sufficiently unambiguous for developers/generators to implement and review the destructive-lifecycle-confirmation objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived lifecycle eligibility, idempotent account/membership lifecycle mutation, last-admin/self-action/no-op recovery, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-role-change-preview` decision-card / diff contract

- Surface id: `surface-user-admin-role-change-preview`.
- Surface type: `decision-card` / `diff`; it previews and decides one role/capability proposal and is the only User branch entry point that may commit a role mutation.
- Surface contract: `user_admin.role_change_preview.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-user-detail`, dashboard/user-list role-risk attention, pending approval queues, and authorized deep links by `action-open-user-admin-role-change-preview`; branch return uses `action-open-user-admin-user-detail` for the visible subject and `action-user-admin-show-users` for the containing list branch.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, a visible target account or membership in the selected scope, backend `user_admin.preview_role_change` authority to load the preview, and backend `user_admin.change_member_roles` authority to commit when policy allows. Hidden users, stale memberships, sibling tenants/customers, self-escalation or self-demotion attempts, last-admin-loss risk, unsupported target roles, support-only contexts, disabled actors, missing memberships, and missing capability attempts return `surface-user-admin-system-message` without revealing hidden identities, roles, customers, tenants, capabilities, or policy facts.
- User goal: compare current and proposed roles before any access change, understand capability/workstream deltas, last-admin and escalation impacts, approval requirements, alternatives, and evidence, then either commit the approved proposal, revise/cancel safely, or return to user detail. The surface never mutates membership lifecycle, support access, invitations, identity provider state, or access-review recommendations inline.

Frontend-safe payload for `user_admin.role_change_preview.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: membership|account|role_change_preview`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `targetSummary`: browser-safe display name/email or masked identifier, account id and membership id only when policy-safe, selected scope label/type, customer label where authorized, current membership status, current role labels/admin level, support-access boundary summary, and freshness marker. Raw WorkOS ids unless policy-safe, raw JWT/session values, provider payloads, private profile/settings, hidden role names, sibling-scope facts, and unredacted audit evidence are forbidden.
- `roleChangeProposal`: `{ previewId?, currentRoleLabels[], proposedRoleLabels[], addedRoles[], removedRoles[], unchangedRoles[], requestedBySummary, requestedReason?, proposedEffectiveScope, proposedEffectiveAt?, previewVersion, idempotencyHint, traceRefs[] }` with role labels limited to backend-authorized choices for the selected scope.
- `capabilityDelta`: `{ addedCapabilitiesByWorkstream[], removedCapabilitiesByWorkstream[], unchangedCapabilitySummary[], affectedWorkstreams[], browserToolImpact[], adminAuthorityDelta, customerScopeDelta?, tenantScopeDelta?, riskLevel, traceRefs[] }`; default copy uses human-readable capability categories and workstream effects, not raw policy ids or hidden capability lists.
- `policyDecision`: `{ canCommit, approvalRequired, approvalState?, approverSummary?, blockedReason?, lastAdminImpact?, selfActionRisk?, escalationRisk?, separationOfDutiesImpact?, staleVersion?, policySummary, alternatives[], traceRefs[] }` where blocked reasons are user-safe and do not enumerate hidden population or policy internals.
- `decisionEvidence`: `{ evidenceWindow, currentAccessEvidence[], proposedAccessEvidence[], recentAdminEvents[], denialOrNoOpHistory[], redactionSummary, auditDrilldownActionId? }`; default evidence is summarized for the acting admin, with raw audit/event ids only in role-gated drilldowns.
- `confirmationForm`: `{ reasonDraft?, reasonRequired, confirmationTextRequired?, confirmationTextPrompt?, submitLabel, reviseActionId, cancelActionId, submitActionId, idempotencyHint, disabledReason? }`; the idempotency hint is user-language only and never exposes raw idempotency/correlation secrets.
- `authorizedActions[]`: commit role change, revise/open user detail, cancel/back to user detail, return to users, open membership lifecycle confirmation when the requested work is status/lifecycle rather than role, open authorized audit evidence, and refresh preview. Unavailable actions are omitted or use safe disabled reasons that do not reveal hidden facts.
- Role-gated `diagnosticMetadata` may include audit/support trace labels; default browser payload must not expose raw event ids, raw correlation/idempotency keys, raw capability policy internals, provider payloads, token/session data, hidden roles/capabilities, model/provider config, or raw account-provider state.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-role-change-preview` | `user_admin.preview_role_change` / `change-membership-role-or-status` | Open this decision/diff surface with backend-derived target summary, current/proposed role data, capability delta, policy decision, branch metadata, and trace refs; hidden, stale, unsupported-role, self-action, last-admin-risk, or out-of-scope targets return `surface-user-admin-system-message` or a safe disabled state. |
| `action-commit-user-admin-role-change` | `user_admin.change_member_roles` / `change-membership-role-or-status` | Reauthorize selected context and target visibility, validate preview version/current membership version/reason/confirmation/proposed roles, enforce last-admin, self-action, escalation, separation-of-duties, and approval policy, enforce idempotency, mutate only the target membership roles/capability grants, audit the attempt, and return refreshed `surface-user-admin-user-detail`. Validation, stale/conflict, already-in-role, no-op replay, approval-required, hidden, last-admin-risk, self-action, unsupported-role, or denial results return this preview with inline state or `surface-user-admin-system-message`; fake success and unrelated lifecycle/support/invitation mutations are forbidden. |
| `action-revise-user-admin-role-change` | `user_admin.preview_role_change` / `change-membership-role-or-status` | Return to `surface-user-admin-user-detail` or a backend-selected role-selection entry state with the visible target and safe proposal context; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Cancel or return to the visible user detail with safe focus/context preservation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped role-risk filters and focus hint when authorized. |
| `action-open-user-admin-membership-status-confirmation` | `user_admin.update_member_status` / `change-membership-role-or-status` | Route to lifecycle confirmation when the intended work is membership/account status rather than role/capability change. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and target placeholder only after backend authorization starts.
- Ready: target summary, proposal, capability delta, policy decision, decision evidence, confirmation form, branch return, trace refs, correlation id, and redaction are backend-derived.
- Validation-error: malformed target id, unsupported proposed role, missing required reason, missing confirmation text, stale preview version, invalid selected context, self-action, escalation beyond actor authority, or ineligible target state is reported without mutation.
- Submitting: preserve reason/confirmation and focus while backend reauthorizes selected context, target visibility, current role version, approval policy, last-admin/self-action checks, and idempotency.
- Success: route to `surface-user-admin-user-detail` with updated role/capability summary, role-change result summary, trace/correlation, and branch return metadata.
- Duplicate/no-op replay: repeated commit with the same idempotency scope or already-applied role set returns refreshed user detail or a no-op preview/result state with trace evidence and no additional role mutation.
- Approval-required: return this decision card or a backend-selected approval/status surface with safe next steps; do not perform partial mutation before approval is satisfied.
- Forbidden/hidden-not-found/stale/conflict/last-admin-risk/self-action-risk/unsupported-role: return `surface-user-admin-system-message` with no hidden user, role, tenant, customer, capability, policy, or population enumeration.
- Partial-data/failure: keep only safe draft reason/confirmation/proposal summary for retry and show user-safe recovery without fixture/mock access-change state.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, commit, revise/cancel/detail return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot change sibling-tenant roles, Customer Admins cannot grant tenant-wide roles or affect sibling customers, SaaS Owner admins cannot mutate tenant/customer roles without an authorized Organization/Customer branch, support-only contexts are bounded by policy, disabled/missing actors receive safe recovery, self-escalation/self-demotion is denied unless an explicit future policy permits it, and last-admin-loss is denied or routed to approval/recovery.
- Trace/audit contract: each preview load, capability-delta calculation, eligibility denial, commit, no-op replay, approval-required result, stale/conflict result, hidden denial, last-admin/self-action denial, revise/cancel, detail return, branch return, and audit-open emits or links an admin work trace with actor account, selected context, visible target summary, proposed role labels, capability decision, policy outcome, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, provider secrets, hidden roles, hidden capabilities, raw policy clauses, or sibling-scope facts.
- Accessibility/responsive expectations: the decision card has a stable heading, selected-scope boundary notice, target and proposal summaries with semantic headings, added/removed role and capability-delta groups readable by screen readers, risk/approval badges with text alternatives, reason/confirmation fields with associated validation, keyboard-operable commit/revise/cancel/return controls, status messages announced as status text, focus restored to the first invalid field or result message, and responsive stacking that keeps consequence, policy, and recovery guidance visible before submission.
- Acceptance/regression coverage must verify user-detail-to-role-preview traversal, protected direct preview load, visible role proposal and capability delta rendering, successful role commit and refreshed user detail as policy allows, idempotent replay/no duplicate role mutation, required-reason/confirmation validation, already-in-role/no-op behavior, stale/conflict recovery, approval-required routing, last-admin and self-action protections, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, branch return to user detail and users, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus recovery, responsive decision-card/diff rendering, and no mutation of lifecycle status/support access/invitations/access-review recommendations.

Surface-description sufficiency review: `surface-user-admin-role-change-preview` is sufficiently unambiguous for developers/generators to implement and review the decision-card/diff objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived role/capability deltas, idempotent role mutation, approval/last-admin/self-action/no-op recovery, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-support-access-grant` create-form contract

- Surface id: `surface-user-admin-support-access-grant`.
- Surface type: `create-form`; it grants or extends bounded support access for one visible subject and never changes roles, membership lifecycle, invitations, identity provider state, or access-review recommendations inline.
- Surface contract: `user_admin.support_access_grant.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-user-detail`, dashboard/user-list support-access attention, expiring-support queues, and authorized deep links by `action-open-user-admin-support-access-grant`; branch return uses `action-open-user-admin-user-detail` for the visible subject and `action-user-admin-show-users` for the containing list branch.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, a visible target account or membership in the selected scope, and backend `user_admin.support_access.grant_revoke_extend` authority. Hidden users, stale memberships, sibling tenants/customers, support-only actors without grant authority, disabled actors, missing memberships, expired selected context, unsupported target scope, policy-blocked purpose/expiry, or missing capability attempts return `surface-user-admin-system-message` without revealing hidden identities, support sessions, tenants, customers, roles, policies, or population counts.
- User goal: grant or extend time-bounded support access with a clear business purpose, expiry, approval/policy state, and audit evidence, then return to the refreshed user detail or a typed decision/system-message result. The form must make support access visibly temporary, scoped, auditable, and separate from role membership.

Frontend-safe payload for `user_admin.support_access_grant.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: support_access_grant|membership|account`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `targetSummary`: browser-safe target display name/email or masked identifier, account id and membership id only when policy-safe, selected scope label/type, customer label where authorized, current membership status, role labels/admin level, current support-access state, and freshness marker. Raw WorkOS ids unless policy-safe, raw JWT/session values, provider payloads, support session secrets, private profile/settings, hidden roles, hidden support grants, sibling-scope facts, and unredacted audit evidence are forbidden.
- `currentSupportAccess`: `{ state, activeGrantId?, purposeSummary?, grantedBySummary?, approverSummary?, startsAt?, expiresAt?, expiringSoon, extendEligible, revokeEligible, policyBlockedReason?, traceRefs[] }` with hidden or unrelated support grants omitted or summarized as redacted.
- `grantRequestForm`: `{ mode: grant|extend, purposeDraft?, purposeRequired, allowedPurposeCategories[], selectedPurposeCategory?, expiryOptions[], selectedExpiry?, maxDurationSummary, startTimingOptions[], approvalRequired, approverHint?, reasonDraft?, idempotencyHint, submitLabel, cancelActionId, submitActionId, disabledReason? }`. Purpose categories, expiry choices, and approval hints are backend-authored for the selected scope and rendered in user language rather than raw policy ids.
- `policyContext`: `{ allowedScopeSummary, supportBoundarySummary, approvalRequired, separationOfDutiesImpact?, existingGrantImpact?, maxDurationSummary, requiredEvidence[], traceRefs[] }` explaining what the support actor can and cannot access without exposing hidden capability internals.
- `decisionEvidence`: `{ recentSupportEvents[], relatedDenialsOrNoOps[], auditDrilldownActionId?, redactionSummary, evidenceWindow }` using summarized, role-appropriate evidence.
- `authorizedActions[]`: open grant/extend form, submit support grant/extend, validate draft, cancel/back to user detail, return to users, open revoke confirmation when eligible, open authorized audit evidence, and return to dashboard. Unavailable actions are omitted or use safe disabled reasons that do not reveal hidden policy/population facts.
- Role-gated `diagnosticMetadata` may include support/audit labels for trace and policy evidence; default browser payload must not expose raw event ids, raw correlation/idempotency keys, raw capability policy internals, provider payloads, support session tokens, hidden support grants, hidden roles/capabilities, model/provider config, or raw account-provider state.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-support-access-grant` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open this create-form with backend-derived target summary, current support-access state, purpose/expiry policy, approval state, branch metadata, and trace refs; hidden, stale, unsupported-scope, support-only, disabled-actor, or out-of-scope targets return `surface-user-admin-system-message` or a safe disabled state. |
| `action-validate-user-admin-support-access-grant` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Validate purpose, expiry, target scope, current grant version, approval requirement, and actor eligibility without creating or extending support access; return inline validation messages on this form. |
| `action-submit-user-admin-support-access-grant` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Reauthorize selected context and target visibility, validate current support-access version/purpose/expiry/reason/approval, enforce separation-of-duties and max-duration policy, enforce idempotency, create or extend only the target support grant, audit the attempt, and return refreshed `surface-user-admin-user-detail`. Validation, stale/conflict, already-covered no-op, approval-required, hidden, policy-blocked, unsupported expiry, or denial results return this form with inline state or `surface-user-admin-system-message`; fake success and unrelated role/lifecycle/invitation mutations are forbidden. |
| `action-open-user-admin-support-access-revoke-confirmation` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Route to revoke confirmation for an eligible active grant; hidden/stale/ineligible targets return a safe system message or disabled state. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Cancel or return to the visible user detail with safe focus/context preservation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped support-access filters and focus hint when authorized. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and target placeholder only after backend authorization starts.
- Ready: target summary, current support-access state, purpose/expiry options, policy context, decision evidence, branch return, trace refs, correlation id, and redaction are backend-derived.
- Draft validation-error: malformed target id, missing required purpose/reason, unsupported purpose category, invalid or overlong expiry, stale current grant version, invalid selected context, separation-of-duties conflict, disabled actor, or ineligible target state is reported without mutation.
- Submitting: preserve draft values and focus while backend reauthorizes selected context, target visibility, current support-access version, approval policy, and idempotency.
- Success: route to `surface-user-admin-user-detail` with updated support-access summary, grant/extension result summary, trace/correlation, and branch return metadata.
- Duplicate/no-op replay: repeated submit with the same idempotency scope or already-covered active grant returns refreshed user detail or a no-op form/result state with trace evidence and no additional support grant mutation.
- Approval-required: return this form or a backend-selected decision/status surface with safe next steps; do not create or extend support access before approval is satisfied.
- Forbidden/hidden-not-found/stale/conflict/policy-blocked/unsupported-expiry: return `surface-user-admin-system-message` with no hidden user, support grant, role, tenant, customer, policy, or population enumeration.
- Partial-data/failure: keep only safe draft purpose/reason/expiry values for retry and show user-safe recovery without fixture/mock support-access state.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, validate, submit, cancel/detail return, branch return, revoke-routing, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot grant support access for sibling tenants, Customer Admins cannot grant tenant-wide or sibling-customer support access, SaaS Owner admins cannot grant tenant/customer support access without an authorized Organization/Customer branch, support-only actors cannot self-extend by default, disabled/missing actors receive safe recovery, and policy limits for duration, purpose, approver, and separation of duties are enforced server-side.
- Trace/audit contract: each form load, draft validation, eligibility denial, submit, no-op replay, approval-required result, stale/conflict result, hidden denial, policy-blocked result, detail return, branch return, revoke routing, and audit-open emits or links an admin work trace with actor account, selected context, visible target summary, support grant mode, requested expiry/purpose category, capability decision, policy outcome, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, provider secrets, support session secrets, hidden roles, hidden capabilities, raw policy clauses, or sibling-scope facts.
- Accessibility/responsive expectations: the create form has a stable heading, selected-scope boundary notice, target and current-support summaries with semantic headings, purpose/category/expiry/reason controls with associated labels and inline errors, approval and support-boundary guidance announced as status text, keyboard-operable submit/cancel/return/revoke controls, focus restored to the first invalid field or result message, and responsive stacking that keeps purpose, expiry, policy, and recovery guidance visible before submission.
- Acceptance/regression coverage must verify user-detail-to-support-grant traversal, protected direct form load, visible current support state and backend-authored purpose/expiry rendering, successful grant and extension routing to refreshed user detail as policy allows, idempotent replay/no duplicate support grant mutation, purpose/expiry/reason validation, already-covered/no-op behavior, stale/conflict recovery, approval-required routing, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, support-only self-extension denial, branch return to user detail and users, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus recovery, responsive create-form rendering, and no mutation of roles/membership status/invitations/access-review recommendations.

Surface-description sufficiency review: `surface-user-admin-support-access-grant` is sufficiently unambiguous for developers/generators to implement and review the create-form objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived support eligibility and purpose/expiry policy, idempotent support grant or extension, approval/policy/no-op recovery, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-support-access-revoke-confirmation` destructive-lifecycle-confirmation contract

- Surface id: `surface-user-admin-support-access-revoke-confirmation`.
- Surface type: `destructive-lifecycle-confirmation`; it revokes or schedules the end of one visible active support-access grant and never changes roles, membership lifecycle, invitations, identity provider state, access-review recommendations, or unrelated support grants inline.
- Surface contract: `user_admin.support_access_revoke_confirmation.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from `surface-user-admin-user-detail`, `surface-user-admin-support-access-grant`, support-access attention rows, and authorized deep links by `action-open-user-admin-support-access-revoke-confirmation`; branch return uses `action-open-user-admin-user-detail` for the visible subject and `action-user-admin-show-users` for the containing list branch.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, a visible target account or membership in the selected scope, an eligible active support-access grant for that target, and backend `user_admin.support_access.grant_revoke_extend` authority. Hidden users, stale grants, inactive or already-revoked grants, sibling tenants/customers, support-only actors without revoke authority, disabled actors, missing memberships, expired selected context, unsupported target scope, separation-of-duties conflicts, or missing capability attempts return `surface-user-admin-system-message` without revealing hidden identities, support sessions, tenants, customers, roles, policies, grants, or population counts.
- User goal: review who currently has support access, why it exists, what revocation changes, provide required reason/confirmation, revoke only that support grant, and return to refreshed user detail or safe recovery. The confirmation makes support access visibly temporary, scoped, auditable, and separate from role membership.

Frontend-safe payload for `user_admin.support_access_revoke_confirmation.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `recordId`, `recordKind: support_access_revoke|support_grant|membership|account`, `recordLabel`, `authorityBasis`, `redaction`, `boundaryNotice`, `traceRefs[]`, `correlationId`, `systemStates`, and `lastResult`.
- `targetSummary`: browser-safe target display name/email or masked identifier, account id and membership id only when policy-safe, selected scope label/type, customer label where authorized, current membership status, role labels/admin level, support-access state, and freshness marker. Raw WorkOS ids unless policy-safe, raw JWT/session values, provider payloads, support session secrets, private profile/settings, hidden roles, hidden support grants, sibling-scope facts, and unredacted audit evidence are forbidden.
- `activeSupportGrant`: `{ grantId?, state, purposeSummary, grantedBySummary, approverSummary?, startsAt, expiresAt, expiresSoon, scopeSummary, revokeEligible, currentVersion?, traceRefs[] }` with grant ids and versions omitted unless policy-safe; hidden or unrelated support grants are omitted or summarized as redacted.
- `revokeConsequenceSummary`: `{ consequenceCopy, accessEndsAtCopy, affectedSupportWorkCopy, downstreamWorkstreamImpact[], recoveryGuidance[], noRoleMutation, noMembershipMutation, noInvitationMutation, noDirectAccessReviewMutation, noFakeSuccess, traceRefs[] }`.
- `eligibility`: `{ canSubmit, disabledReason?, alreadyRevoked?, staleVersion?, approvalRequired?, separationOfDutiesImpact?, reasonRequired, confirmationRequired, policySummary, traceRefs[] }` with user-safe disabled reasons and no hidden policy, population, role, capability, or grant enumeration.
- `confirmationForm`: `{ reasonDraft?, reasonRequired, reasonCategories[], selectedReasonCategory?, confirmationTextRequired?, confirmationTextPrompt?, submitLabel, cancelActionId, submitActionId, idempotencyHint, disabledReason? }`; the idempotency hint is user-language only and never exposes raw idempotency/correlation secrets.
- `decisionEvidence`: `{ recentSupportEvents[], relatedDenialsOrNoOps[], auditDrilldownActionId?, redactionSummary, evidenceWindow }` using summarized, role-appropriate evidence.
- `authorizedActions[]`: open revoke confirmation, confirm support revoke, cancel/back to user detail, return to users, reopen grant/extend form when revocation is not the right task, open authorized audit evidence, and return to dashboard. Unavailable actions are omitted or use safe disabled reasons that do not reveal hidden policy/population/grant facts.
- Role-gated `diagnosticMetadata` may include support/audit labels for trace and policy evidence; default browser payload must not expose raw event ids, raw correlation/idempotency keys, raw capability policy internals, provider payloads, support session tokens, hidden support grants, hidden roles/capabilities, model/provider config, or raw account-provider state.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-support-access-revoke-confirmation` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open this confirmation with backend-derived target summary, active grant state, consequence copy, eligibility, branch metadata, and trace refs; hidden, stale, already-revoked, unsupported-scope, support-only, disabled-actor, or out-of-scope targets return `surface-user-admin-system-message` or a safe disabled state. |
| `action-confirm-user-admin-support-access-revoke` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Reauthorize selected context and target visibility, validate active grant version/reason/confirmation/current state, enforce separation-of-duties and revocation policy, enforce idempotency, revoke only the selected target support grant, audit the attempt, and return refreshed `surface-user-admin-user-detail`. Validation, stale/conflict, already-revoked no-op, approval-required, hidden, policy-blocked, unsupported scope, or denial results return this confirmation with inline state or `surface-user-admin-system-message`; fake success and unrelated role/lifecycle/invitation/access-review mutations are forbidden. |
| `action-open-user-admin-support-access-grant` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Route back to grant/extend form when the intended work is extension rather than revocation; hidden/stale/ineligible targets return a safe system message or disabled state. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Cancel or return to the visible user detail with safe focus/context preservation; hidden/stale targets return `surface-user-admin-system-message`. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to `surface-user-admin-users` with backend-shaped support-access filters and focus hint when authorized. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and target placeholder only after backend authorization starts.
- Ready: target summary, active support grant, consequence summary, eligibility, confirmation form, decision evidence, branch return, trace refs, correlation id, and redaction are backend-derived.
- Validation-error: malformed target id, missing required reason, missing confirmation text, stale current grant version, invalid selected context, already-revoked grant, separation-of-duties conflict, disabled actor, or ineligible target state is reported without mutation.
- Submitting: preserve reason/confirmation and focus while backend reauthorizes selected context, target visibility, active support grant version, revocation policy, and idempotency.
- Success: route to `surface-user-admin-user-detail` with support-access revoked/ended summary, trace/correlation, branch return metadata, and no change to roles, membership status, invitations, or access-review recommendations.
- Duplicate/no-op replay: repeated confirm with the same idempotency scope or already-revoked active grant returns refreshed user detail or a no-op confirmation/result state with trace evidence and no additional support mutation.
- Approval-required: return this confirmation or a backend-selected decision/status surface with safe next steps; do not partially revoke support access before approval is satisfied.
- Forbidden/hidden-not-found/stale/conflict/policy-blocked/unsupported-scope: return `surface-user-admin-system-message` with no hidden user, support grant, role, tenant, customer, policy, or population enumeration.
- Partial-data/failure: keep only safe draft reason/category/confirmation values for retry and show user-safe recovery without fixture/mock support-access state.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, confirm, grant-form return, cancel/detail return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot revoke sibling-tenant support grants, Customer Admins cannot revoke tenant-wide or sibling-customer support access, SaaS Owner admins cannot revoke tenant/customer support grants without an authorized Organization/Customer branch, support-only actors cannot self-revoke or self-extend by default unless an explicit policy permits it, disabled/missing actors receive safe recovery, and policy limits for active grant state, purpose, approver, separation of duties, and stale version are enforced server-side.
- Trace/audit contract: each confirmation load, eligibility denial, confirm, no-op replay, approval-required result, stale/conflict result, hidden denial, policy-blocked result, grant-form return, detail return, branch return, and audit-open emits or links an admin work trace with actor account, selected context, visible target summary, support grant scope, revoke reason category, capability decision, policy outcome, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw event ids, raw JWTs, provider secrets, support session secrets, hidden support grants, hidden roles, hidden capabilities, raw policy clauses, or sibling-scope facts.
- Accessibility/responsive expectations: the destructive confirmation has a stable heading, selected-scope boundary notice, target and active-support summaries with semantic headings, consequence copy before the submit button, reason/category/confirmation controls with associated labels and inline errors, destructive action wording announced as status text, keyboard-operable confirm/cancel/return/grant-form controls, focus restored to the first invalid field or result message, and responsive stacking that keeps target, consequence, eligibility, and recovery guidance visible before submission.
- Acceptance/regression coverage must verify user-detail-to-support-revoke traversal, protected direct confirmation load, visible active support grant and backend-authored consequence rendering, successful revoke routing to refreshed user detail as policy allows, idempotent replay/no duplicate support mutation, required-reason/confirmation validation, already-revoked/no-op behavior, stale/conflict recovery, approval-required routing, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, support-only self-action denial, branch return to user detail and users, audit/work trace/correlation links, frontend secret boundaries, keyboard operation, focus recovery, responsive destructive-lifecycle-confirmation rendering, and no mutation of roles/membership status/invitations/access-review recommendations.

Surface-description sufficiency review: `surface-user-admin-support-access-revoke-confirmation` is sufficiently unambiguous for developers/generators to implement and review the destructive-lifecycle-confirmation objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, backend-derived active support grant eligibility and consequence copy, idempotent support revoke, approval/policy/no-op recovery, denial/no-enumeration behavior, trace/correlation, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-access-review-task` workflow-status / outcome-panel contract

- Surface id: `surface-user-admin-access-review-task`.
- Surface type: `workflow-status` while queued/running/blocked/cancelled and `outcome-panel` when a recommendation or human-review result is available.
- Surface contract: `user_admin.access_review_task.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from dashboard access-review attention, `surface-user-admin-users` access-review rows, `surface-user-admin-user-detail` task entries, and authorized deep links by `action-open-user-admin-access-review-task`; branch return uses `action-user-admin-show-users` and subject return uses `action-open-user-admin-user-detail` when the reviewed subject is still visible.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, and backend `user_admin.access_review.read` for reads, `user_admin.access_review.start` for starts, `user_admin.access_review.cancel` for cancellation, and `user_admin.access_review.accept_result` or `user_admin.access_review.reject_result` for human review. Hidden, stale, disabled-actor, cross-tenant, sibling-customer, support-only, missing task, missing subject, or unsupported-scope attempts return `surface-user-admin-system-message` without enumerating hidden tasks, users, memberships, customers, roles, model providers, policy internals, or evidence.
- User goal: understand durable access-review progress or the final advisory recommendation for one visible subject/scope, inspect safe evidence and omissions, make an explicit human accept/reject decision when authorized, and route any real access change through the dedicated membership, role, or support-access task surfaces. The access-review task never directly mutates roles, membership lifecycle, invitations, support access, identity provider state, or source audit records.

Frontend-safe payload for `user_admin.access_review_task.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `subjectReturnActionId?`, `recordId`, `taskId`, `taskVersion`, `taskStatus`, `phase`, `surfaceMode: progress|blocked|result|reviewed`, `authorityBasis`, `redaction`, `traceRefs[]`, `correlationId`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `subjectSummary`: browser-safe reviewed account/member/invitation/support-access subject, display label, email only when authorized, selected-scope membership status, current role labels, support-access marker, last-admin/self-action risk summary, and stale/freshness marker. Raw WorkOS ids, JWT/session values, private My Account settings, hidden sibling-scope labels, and provider payloads are forbidden.
- `taskSummary`: durable access-review task id when safe, requested review type, requested-by actor summary, start/read timestamps, current lifecycle status (`not_started`, `queued`, `running`, `blocked_provider_or_runtime`, `blocked_policy`, `needs_human_review`, `accepted`, `rejected`, `cancelled`, `failed`), phase label, progress percentage only when backend-derived, retry eligibility, cancellation eligibility, human-review requirement, and `noDirectMutation` marker.
- `progressEvents[]`: ordered durable timeline entries with user-safe labels, timestamps, severity, source (`policy`, `data_snapshot`, `model`, `tool`, `human_review`, `system`), trace refs, redaction state, and recovery hints. Progress events must not expose prompts, raw model messages, provider payloads, raw event ids, hidden identities, or policy clause internals in the default view.
- `readinessSummary`: model/provider/tool/data/policy readiness, fail-closed blocker category/code, missing configuration copy, retry eligibility, admin recovery steps, and `noFakeSuccess` for provider/model/tool blocked states.
- `evidenceSummary`: authorized evidence window, included source categories, excluded/omitted categories with safe reasons, redacted evidence counts, source recency, policy constraints considered, and role-gated audit drilldown action ids. Evidence refs are safe trace/audit links, not raw audit records.
- `recommendation`: present only for completed review output and includes recommendation label (`no_change`, `review_roles`, `suspend_or_disable`, `remove_membership`, `expire_support_access`, `manual_follow_up`), risk level, confidence band, rationale summary, affected workstreams/capability categories, suggested follow-up target surface/action, alternatives, and `advisoryOnly: true`.
- `humanReviewState`: `{ required, status: pending|accepted|rejected|cancelled|not_required, reviewerSummary?, reviewedAt?, reasonRequired, reasonDraft?, acceptedRecommendationId?, rejectedRecommendationId?, decisionTraceRefs[] }`. Human accept/reject records review only; follow-up mutations are separate actions/surfaces.
- `availableActions[]`: backend-authored open/read, start review, refresh progress, cancel task, accept result, reject result, open suggested role preview, open membership lifecycle confirmation, open support-access revoke/grant, open user detail, return to users, open audit evidence, and open dashboard actions as authorized. Unavailable actions are omitted; disabled reasons are safe and do not reveal hidden policy or population facts.
- Role-gated `diagnosticMetadata` may include model/provider family labels, tool-boundary ids, policy bundle versions, and trace labels only for authorized admin/audit/support actors; default payload must not expose raw prompts, model responses, provider secrets, tool arguments with secrets, raw idempotency/correlation values, raw policy clauses, or unredacted audit evidence.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-access-review-task` | `user_admin.access_review.read` / `run-access-review` | Open an existing visible task or a safe not-started/blocked task surface for the selected visible subject; hidden, stale, or unauthorized targets return `surface-user-admin-system-message`. |
| `action-start-user-admin-access-review` | `user_admin.access_review.start` / `run-access-review` | Create or reuse a durable review task for the visible subject/scope, audit the start, and return this progress surface. Provider/model/tool/data readiness failures return this surface in blocked mode with `noFakeSuccess` and `noDirectMutation`. |
| `action-refresh-user-admin-access-review-task` | `user_admin.access_review.read` / `run-access-review` | Reload progress/result from durable task state, preserving safe evidence and trace refs. |
| `action-cancel-user-admin-access-review-task` | `user_admin.access_review.cancel` / `run-access-review` | Cancel only a cancellable queued/running task, audit the decision, and return cancelled progress; terminal or already-cancelled tasks return no-op state with trace evidence. |
| `action-accept-user-admin-access-review-result` | `user_admin.access_review.accept_result` / `run-access-review` | Record human acceptance of advisory output with reason/correlation, return reviewed outcome state, and expose follow-up task actions; it must not directly mutate roles, membership lifecycle, support access, invitations, or identity provider state. |
| `action-reject-user-admin-access-review-result` | `user_admin.access_review.reject_result` / `run-access-review` | Record human rejection with required reason, return reviewed outcome state, and preserve evidence for audit; it must not directly mutate source access. |
| `action-open-access-review-suggested-role-preview` | `user_admin.preview_role_change` / `change-membership-role-or-status` | Open `surface-user-admin-role-change-preview` with backend-shaped proposal context when the accepted recommendation suggests role work; hidden/stale subjects return `surface-user-admin-system-message`. |
| `action-open-access-review-suggested-membership-status` | `user_admin.update_member_status` / `change-membership-role-or-status` | Open `surface-user-admin-membership-status-confirmation` with backend-shaped context when the accepted recommendation suggests lifecycle work. |
| `action-open-access-review-suggested-support-access` | `user_admin.support_access.grant_revoke_extend` / `grant-or-revoke-support-access` | Open support-access grant/revoke surfaces when the accepted recommendation suggests support-access work. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Return to the reviewed subject detail when still visible; otherwise safe system message. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to User Directory with backend-shaped access-review filter/focus preservation. |

States and outcomes:

- Loading: show selected scope, subject placeholder, and task id only after backend authorization begins.
- Not-started/empty: visible subject has no active review task; show start action only when `user_admin.access_review.start` is authorized.
- Queued/running: show durable timeline, safe data/model/tool readiness, refresh/cancel actions, trace/correlation, and no direct mutation.
- Provider/model/tool/data/policy blocked: show blocker category, recovery steps, retry/admin readiness hints, `noFakeSuccess`, `noDirectMutation`, and safe trace refs; no fixture/model-less recommendation is allowed.
- Result ready / needs human review: show advisory recommendation, confidence/risk/evidence/omissions, accept/reject actions when authorized, and follow-up task routing; source access remains unchanged.
- Accepted/rejected/cancelled: show terminal human/task state, reason summary, trace/correlation, idempotent replay/no-op semantics, and follow-up actions only when safe.
- Validation-error: malformed task id, subject id, reason, selected context, stale task version, unsupported decision, or missing required reason is reported inline without broadening scope.
- Forbidden/hidden-not-found/stale/conflict/unsupported-scope: return `surface-user-admin-system-message` with no hidden task, subject, evidence, model, provider, role, customer, or policy enumeration.
- Partial-data/failure: preserve safe progress state and recovery guidance without fake evidence, hidden counts, raw provider/model output, or direct source mutation.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, start, refresh, cancel, accept, reject, follow-up task routing, subject return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot review sibling-tenant users, Customer Admins cannot review sibling-customer or tenant-wide users, SaaS Owner admins cannot inspect tenant application access without an authorized Organization/Customer branch, support-only contexts cannot approve their own support-access review by default, disabled/missing actors receive safe recovery, and stale task/subject versions are rejected or refreshed server-side.
- Trace/audit contract: each task load, start/reuse, progress refresh, provider/model/tool blocker, evidence inclusion/omission, recommendation, human accept/reject, cancellation, idempotent replay, follow-up open, hidden denial, stale/conflict result, branch return, and audit-open emits or links an admin work trace with actor account, selected context, visible subject summary, task id/version when safe, model/provider/tool readiness summary, policy outcome, human decision summary, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw prompts, model responses, provider records, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, hidden capabilities, raw policy clauses, sibling-scope facts, or unredacted evidence.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope boundary notice, task status region announced as status text, timeline entries with semantic ordering, evidence and recommendation sections with headings, accept/reject/cancel/follow-up controls as keyboard-operable grouped actions, inline validation errors tied to reason fields, focus restored to first invalid field or result status, and responsive stacking that keeps blocker/recommendation/decision actions visible before diagnostic metadata.
- Acceptance/regression coverage must verify dashboard/users/detail-to-access-review traversal, protected direct task load, start/reuse, queued/running progress, provider/model/tool fail-closed blocked state with `noFakeSuccess`, advisory result rendering, accept/reject with required reason and idempotent replay, cancellation/no-op behavior, follow-up routing to role/membership/support task surfaces without direct mutation, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, stale task-version recovery, audit/work trace/correlation links, frontend secret/model/provider boundary, keyboard operation, focus recovery, responsive workflow-status/outcome-panel rendering, and no mutation of roles/membership status/support access/invitations/identity provider state from the review surface itself.

Surface-description sufficiency review: `surface-user-admin-access-review-task` is sufficiently unambiguous for developers/generators to implement and review the workflow-status/outcome-panel objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, durable task state, model/provider/tool fail-closed behavior, advisory-only human accept/reject, denial/no-enumeration behavior, trace/correlation, follow-up task routing, and browser secret boundaries before marking implementation/testing objectives done.

### `surface-user-admin-identity-exception-review` decision-card / workflow-status contract

- Surface id: `surface-user-admin-identity-exception-review`.
- Surface type: `decision-card` while approval/recovery is pending and `workflow-status` while provider-safe relink/recovery work is queued, blocked, completed, or rejected.
- Surface contract: `user_admin.identity_exception_review.v1`.
- Owning workstream: User Admin.
- Owning functional agent: `user-admin-agent`.
- Placement: User Directory branch descendant opened from dashboard identity-exception attention, `surface-user-admin-users` identity-exception rows, `surface-user-admin-user-detail` task entries, and authorized deep links by `action-open-user-admin-identity-exception-review`; branch return uses `action-user-admin-show-users` and subject return uses `action-open-user-admin-user-detail` when the subject remains visible.
- Required context: authenticated active account, selected app-owner/tenant/customer `AuthContext`, active actor membership, visible target subject or exception id, and backend `user_admin.identity_relink.review` for reads/review decisions. Recovery-submit actions additionally require the backend recovery capability selected by policy for the exact scope. Hidden, stale, disabled-actor, cross-tenant, sibling-customer, support-only, missing exception, missing subject, provider-unavailable, or unsupported-scope attempts return `surface-user-admin-system-message` without enumerating hidden users, memberships, provider records, identity ids, roles, customers, tenants, or policy internals.
- User goal: understand one visible identity-link, provider-mismatch, duplicate-account, or relink-required exception, review safe evidence and risk, choose approve/deny/request-more-info or an allowed recovery route, and track deterministic provider-safe recovery without exposing raw provider internals. The surface never directly edits roles, membership status, invitations, support access, or arbitrary identity-provider state outside the approved recovery workflow.

Frontend-safe payload for `user_admin.identity_exception_review.v1`:

- Envelope fields: `surfaceContract`, `selectedAuthContext`, `scopeLabel`, `scopeType`, `customerLabel?`, `branchRootSurfaceId`, `branchReturnActionId`, `branchReturnLabel`, `subjectReturnActionId?`, `recordId`, `exceptionId`, `exceptionVersion`, `surfaceMode: decision|blocked|workflow|resolved`, `authorityBasis`, `redaction`, `traceRefs[]`, `correlationId`, `boundaryNotice`, `systemStates`, and `lastResult`.
- `subjectSummary`: browser-safe visible account/member/invitation summary, display label, email only when authorized, selected-scope membership status, current role labels, provider family label, verified-email/link status, duplicate-account hints when policy-safe, and stale/freshness marker. Raw WorkOS ids, provider subject ids, JWT/session values, private profile/settings, sibling-scope labels, and provider payloads are forbidden.
- `exceptionSummary`: exception category (`link_mismatch`, `duplicate_identity`, `relink_required`, `provider_profile_conflict`, `orphaned_invitation`, `manual_review_required`), lifecycle status (`open`, `needs_human_review`, `approved_for_recovery`, `recovery_running`, `blocked_provider_or_policy`, `resolved`, `rejected`, `cancelled`), severity, risk level, detected-at/source labels, affected visible scopes, recovery eligibility, no-enumeration redaction state, and `noDirectMutation` marker.
- `evidenceSummary`: authorized evidence window, included source categories, omitted/redacted categories with safe reasons, provider-boundary summary, identity-link history labels, duplicate/open-invite context when visible, policy constraints considered, confidence/risk rationale, and role-gated audit drilldown action ids. Evidence refs are safe trace/audit links, not raw provider records.
- `providerReadiness`: provider/runtime/tool/policy readiness, fail-closed blocker category/code, retry eligibility, admin recovery steps, and `noFakeSuccess` for provider/tool/policy blocked states. Missing provider configuration must block recovery instead of fabricating a resolved identity state.
- `reviewDecision`: `{ required, status: pending|approved|denied|more_info_requested|not_required, reviewerSummary?, reviewedAt?, reasonRequired, reasonDraft?, approvedRecoveryPlanId?, deniedReasonCode?, decisionTraceRefs[] }`. Human approval authorizes a bounded recovery route only; it does not itself mutate access or identity-provider state.
- `recoveryPlan`: backend-authored allowed recovery options such as re-link existing provider identity, attach accepted invitation to visible account, merge duplicate account reference for follow-up, request provider admin intervention, or return to user detail with no change. Each option includes user-facing consequence copy, approval requirement, idempotency/correlation behavior summary, target result surface, and safe rollback/retry guidance.
- `progressEvents[]`: ordered workflow entries with user-safe labels, timestamps, severity, source (`policy`, `provider`, `identity_link`, `human_review`, `system`), trace refs, redaction state, and recovery hints. Entries must not expose provider payloads, raw WorkOS ids, tokens, secret tool arguments, raw event ids, hidden identities, or policy clause internals in the default view.
- `availableActions[]`: backend-authored open/read, refresh, approve recovery, deny recovery, request more information, cancel recovery where safe, retry provider-safe recovery, open user detail, return to users, open audit evidence, and open dashboard actions as authorized. Unavailable actions are omitted; disabled reasons are safe and do not reveal hidden policy or population facts.
- Role-gated `diagnosticMetadata` may include provider family labels, recovery workflow ids, policy bundle versions, and trace labels for authorized admin/audit/support actors; default payload must not expose raw provider records, tokens, raw idempotency/correlation values, raw policy clauses, hidden identities, or unredacted audit evidence.

Actions and result surfaces:

| Action id | Governed backend capability/tool | Result behavior |
|---|---|---|
| `action-open-user-admin-identity-exception-review` | `user_admin.identity_relink.review` / `review-identity-exception` | Open an existing visible exception, or a safe no-exception state for a visible subject; hidden, stale, unsupported, or unauthorized targets return `surface-user-admin-system-message`. |
| `action-refresh-user-admin-identity-exception-review` | `user_admin.identity_relink.review` / `review-identity-exception` | Reload exception/recovery status from durable state, preserving safe evidence, trace refs, and branch metadata. |
| `action-approve-user-admin-identity-recovery` | `user_admin.identity_relink.review` plus policy-selected recovery capability / `review-identity-exception` | Record human approval with required reason, select one backend-authored recovery plan, start or resume bounded provider-safe recovery, and return this surface in workflow/status mode. It must not directly mutate roles, membership lifecycle, invitations, support access, or unrelated provider identities. |
| `action-deny-user-admin-identity-recovery` | `user_admin.identity_relink.review` / `review-identity-exception` | Record denial with required reason, return rejected decision state, keep source access unchanged, and preserve evidence for audit. |
| `action-request-user-admin-identity-more-info` | `user_admin.identity_relink.review` / `review-identity-exception` | Mark the exception as awaiting additional provider/user/admin evidence and return this decision-card with recovery guidance. |
| `action-retry-user-admin-identity-recovery` | policy-selected recovery capability / `review-identity-exception` | Retry only an approved, idempotent, provider-safe recovery step; provider/tool/policy blockers return blocked workflow state with `noFakeSuccess`. |
| `action-cancel-user-admin-identity-recovery` | `user_admin.identity_relink.review` / `review-identity-exception` | Cancel only a cancellable pending recovery workflow; terminal or already-cancelled workflows return no-op state with trace evidence. |
| `action-open-user-admin-user-detail` | `user_admin.read_user_account` / `search-user-directory` | Return to the visible subject detail when still authorized; otherwise safe system message. |
| `action-user-admin-show-users` | `user_admin.list_members` / `search-user-directory` | Return to User Directory with backend-shaped identity-exception filter/focus preservation. |
| `action-open-user-admin-audit` | `admin.audit.read` | Open authorized Audit/Trace evidence or safe redacted system message. |

States and outcomes:

- Loading: show selected scope and exception placeholder only after backend authorization begins.
- Empty/no-current-exception: visible subject has no open identity exception; show return/detail actions and traceable absence copy only when the subject is authorized.
- Ready / needs human review: show exception summary, safe evidence, risk/confidence, provider boundary, approved recovery options, review decision controls, trace/correlation, and no direct mutation.
- Approved recovery queued/running: show durable workflow progress, provider/tool/policy readiness, retry/cancel actions where safe, and no direct access mutation.
- Provider/tool/policy blocked: show blocker category, recovery steps, retry/admin readiness hints, `noFakeSuccess`, `noDirectMutation`, and safe trace refs; no fixture/provider-less resolved identity is allowed.
- Resolved/rejected/cancelled: show terminal human/recovery state, reason summary, provider-safe result summary, idempotent replay/no-op semantics, and user-detail return actions only when safe.
- Validation-error: malformed exception id, subject id, recovery option, reason, selected context, stale exception version, unsupported decision, or missing required reason is reported inline without broadening scope.
- Forbidden/hidden-not-found/stale/conflict/unsupported-scope: return `surface-user-admin-system-message` with no hidden exception, subject, provider record, identity id, role, customer, tenant, or policy enumeration.
- Partial-data/failure: preserve only safe decision/recovery state and show recovery guidance without fake provider state, hidden counts, raw provider output, or direct source mutation.

Authorization, trace, accessibility, and tests:

- Authorization and tenant rules: every open, refresh, approve, deny, more-info, retry, cancel, subject return, branch return, and audit drilldown is evaluated against the selected backend `AuthContext`; Tenant Admins cannot review sibling-tenant identities, Customer Admins cannot review sibling-customer or tenant-wide users, SaaS Owner admins cannot inspect tenant identity exceptions without an authorized Organization/Customer branch, support-only contexts cannot approve their own identity recovery by default, disabled/missing actors receive safe recovery, and stale exception/subject versions are rejected or refreshed server-side.
- Trace/audit contract: each exception load, review decision, recovery-plan selection, provider/tool/policy blocker, evidence inclusion/omission, retry/cancel, no-op replay, hidden denial, stale/conflict result, branch return, subject return, and audit-open emits or links an admin work trace with actor account, selected context, visible subject summary, exception id/version when safe, provider-boundary summary, policy outcome, human decision summary, result surface id, redaction level, and correlation id. Browser-visible trace summaries never expose raw provider records, raw WorkOS ids, raw event ids, raw JWTs, invitation tokens, provider secrets, hidden roles, hidden capabilities, raw policy clauses, sibling-scope facts, or unredacted evidence.
- Accessibility/responsive expectations: the surface has a stable heading, selected-scope boundary notice, decision/status region announced as status text, evidence/risk/provider-boundary/recovery sections with semantic headings, approve/deny/more-info/retry/cancel controls as keyboard-operable grouped actions, inline validation errors tied to reason/recovery controls, focus restored to the first invalid field or result status, and responsive stacking that keeps blocker/recovery decisions visible before diagnostic metadata.
- Acceptance/regression coverage must verify dashboard/users/detail-to-identity-exception traversal, protected direct exception load, no-current-exception state, approve/deny/more-info with required reason and idempotent replay, provider/tool/policy fail-closed blocked state with `noFakeSuccess`, approved recovery progress/result without role/membership/invitation/support mutation, retry/cancel/no-op behavior, role-specific SaaS Owner/Tenant Admin/Customer Admin visibility, hidden/cross-tenant/customer denial without enumeration, stale exception-version recovery, audit/work trace/correlation links, frontend provider/JWT/token secret boundary, keyboard operation, focus recovery, responsive decision-card/workflow-status rendering, and no mutation of roles/membership status/support access/invitations from the review decision itself.

Surface-description sufficiency review: `surface-user-admin-identity-exception-review` is sufficiently unambiguous for developers/generators to implement and review the decision-card/workflow-status objective without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. Runtime realization must still prove protected API/action paths, durable exception/recovery state, provider/tool/policy fail-closed behavior, no-enumeration denial behavior, trace/correlation, approved recovery routing, and browser secret boundaries before marking implementation/testing objectives done.

### Surface payloads and task routing

- Directory: `surfaceContract`, selected `AuthContext`, filters, pagination/sort, dashboard-origin queue id, `rows[]`, visible create/invite action where allowed, redaction, trace refs, and correlation id. Each row includes frontend-safe user/member/invitation/support/review badges, backend-authored `targetSurfaceId`, `targetObjectType`, `openActionId`, action availability, and redaction state; activation opens the target inspection/task surface and never performs mutation inline.
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

Surface-description sufficiency review: the previous broad list/detail/action panels are not sufficient for durable collection-object readiness because they combined discovery, inspection, create/edit, and lifecycle mutation. This revised user-admin graph is sufficient for developers/generators to implement and review without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics; existing implementation tasks should be repaired to match the split graph. Organization Admin is included through the Organization Directory branch contract above, and Customer/Customer Admin administration is included through the Customer Directory branch contract above.

## Production runtime surface requirements

- Invitation surfaces (`surface-user-admin-invitation-create`, `surface-user-admin-invitation-detail`, `surface-user-admin-invitation-resend-confirmation`, and `surface-user-admin-invitation-revoke-confirmation`) expose user-safe delivery status, retry/recovery eligibility, provider/outbox blocked states, no-op/replay outcomes, audit/work trace refs, and correlation ids. They never expose raw invitation tokens, Resend secrets, provider error bodies, full email bodies, or email-only authorization.
- `surface-user-admin-identity-exception-review` is a durable workflow/status surface for identity exception review and recovery. It shows scoped lifecycle state, review decision, recovery blocker/result, provider-boundary redaction, risk/evidence summaries, and approve/deny/recovery actions where authorized; raw WorkOS/JWT/provider payloads are hidden.
- `surface-user-admin-access-review-task` shows model-backed task progress/result or a typed fail-closed blocker. It summarizes model/tool/data/policy usage through safe trace links, keeps human accept/reject explicit, and marks recommendations as advisory with no direct access mutation.
- Dashboard attention includes visible invitation delivery failures, identity exception reviews, access-review results, and provider/outbox/model blockers. Hidden scopes and hidden counts remain omitted.

Surface-description sufficiency review: these production runtime additions are sufficient for implementation tasks. The affected surfaces have explicit status, blocker, action, redaction, trace, auth/scope, and no-fake-success semantics; no implementation task should invent provider/model/workflow behavior beyond the production runtime contract.

## System-message requirements

`surface-user-admin-system-message` handles forbidden access, missing context, disabled actor, inactive membership, validation failure, duplicate/open invitation, last-admin protection, self-disable denial, role escalation denial, unsupported bulk action, no-op mutation, stale/outbox/provider/model failure, tenant isolation denial, missing support grant, hidden/not-found target, missing tool-boundary grant, denied skill/reference load, and recovery guidance.

Payload includes safe reason code, severity, user-safe title/message, selected scope label if safe, recovery steps, required capability/tool/provider readiness hints, trace refs, correlation id, redaction note, and `noFakeSuccess` for provider/model blocked states.

## Scope-aware variants

- **App Admin** is the app-owner level. App Admin surfaces focus on App Admin users and Tenant Admin users for SMB tenant accounts, plus platform-safe tenant metadata. Tenant employee, Customer Admin, and Customer User data requires an explicit backend-authorized selected tenant/customer context and otherwise returns forbidden/redacted metadata.
- **Tenant Admin** is the SMB tenant level. Tenant Admin surfaces show tenant employees, Customer Admin users for customers the tenant provides access to, tenant-created support access, invitations, role/capability data, access reviews, and scoped audit queues. Cross-tenant targets and App Admin account administration are forbidden.
- **Customer Admin** is the customer level. Customer Admin surfaces show selected Customer Users, customer invitations, customer roles, customer access-review items, and customer audit excerpts only. Tenant employee rows, tenant-level roles/actions, other customers, support-access administration, App Admin users, Tenant Admin users, and tenant-wide audit queues are forbidden.
- **Auditor** may read scoped evidence and traces where permitted and cannot mutate access.

## Common action rules

Every consequential browser action and every navigation/read surface-request action has a stable action id, maps to a governed backend capability/tool, carries correlation and idempotency behavior where needed, recomputes authorization server-side, emits audit/work traces for allow/deny/no-op/failure, and returns a typed result surface, decision card, workflow status, outcome panel, markdown response, or safe system message. Frontend visibility and disabled state are UX hints only.

## Common states

User Admin surfaces define loading, empty, ready, submitting, success, validation-error, forbidden, not_found_or_redacted, conflict, stale/reconnect, partial-data, provider-fail-closed, model-fail-closed, outbox-fail-closed, no-op, approval-required, and failure states. All states preserve selected tenant/customer scoping, browser-safe redaction, trace/correlation links, and recovery guidance.
