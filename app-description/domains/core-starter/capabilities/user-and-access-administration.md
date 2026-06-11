# Capability: User and access administration

## Purpose

Let authorized administrators manage users, memberships, roles/capabilities, invitations, support access, access review, identity relink review, and admin audit evidence within selected tenant/customer scope without weakening isolation, approval policy, provider boundaries, or traceability.

## Actors and scope

- Tenant admins manage tenant employees and tenant-owned customer administration within authorized tenant/customer scope.
- Customer admins manage customer users, invitations, customer roles, and customer access-review evidence within their customer scope.
- Auditors read scoped evidence without mutating access.
- SaaS Owner Admins may list, read, create, rename, suspend, and reactivate customer-facing Organizations backed by internal Tenant boundaries only from a SaaS Owner selected `AuthContext` with backend `saas_owner.tenant.read` or `saas_owner.tenant.manage` grants.
- SaaS Owner support roles may inspect or explain tenant/customer user data only through active tenant-created support access.
- Billing-boundary authority, billing provider state, and Organization administration do not grant tenant/customer application-data access or support access.
- User Admin functional agent may recommend, draft, summarize, or prepare decision cards but cannot expand authority or mutate access without backend capability authorization and human/approval gates.

## Capability inventory

| Capability id | Type | Responsibility | Surface/API exposure |
|---|---|---|---|
| `saas_owner.organization.list` | protected read | List/search browser-safe Organizations visible to SaaS Owner Admin; internally backed by Tenant records and `saas_owner.tenant.read`. | `surface-user-admin-organization-directory`; `GET /api/admin/organizations`. |
| `saas_owner.organization.read` | protected read | Read one Organization detail and platform-safe lifecycle/audit metadata without exposing tenant app data, customer records, provider secrets, hidden counts, or billing-derived authority. | `surface-user-admin-organization-detail`; `GET /api/admin/organizations/{organizationId}`. |
| `saas_owner.organization.create` | command | Create an active Organization/Tenant boundary with normalized name, idempotency key, correlation id, validation, and audit/work trace evidence; internally requires `saas_owner.tenant.manage`. | `surface-user-admin-organization-create`; `POST /api/admin/organizations`; success opens detail. |
| `saas_owner.organization.rename` | command | Rename an Organization display label with validation, no-op replay semantics, idempotency key, and audit/work trace evidence. | `surface-user-admin-organization-rename`; `POST /api/admin/organizations/{organizationId}/rename`; success refreshes detail. |
| `saas_owner.organization.suspend` | command | Suspend an Organization/Tenant boundary without exposing or mutating tenant application data; records reason, idempotency, warnings, and audit/work trace evidence. | `surface-user-admin-organization-suspend-confirmation`; `POST /api/admin/organizations/{organizationId}/suspend`; success refreshes detail. |
| `saas_owner.organization.reactivate` | command | Reactivate a suspended Organization/Tenant boundary with no-op handling, idempotency key, and audit/work trace evidence. | `surface-user-admin-organization-reactivate-confirmation`; `POST /api/admin/organizations/{organizationId}/reactivate`; success refreshes detail. |
| `user_admin.view_overview` | protected read | Scoped dashboard projection, attention counts, provider/outbox/model readiness, recent admin activity, redaction. | `surface-user-admin-dashboard`; agent evidence read. |
| `user_admin.list_members` | protected read | Scoped directory, membership, invitation, support-access, and access-review rows with pagination/filter validation; discovery only. | `surface-user-admin-users`; row/card selection opens `surface-user-admin-user-detail`. |
| `user_admin.read_user_account` | protected read | Scoped account/member/invitation/support/review/audit inspection by backend-authorized target; task entry points only. | `surface-user-admin-user-detail`. |
| `user_admin.invite_user` | command | Email normalization, duplicate/open-invite check, role validation, idempotency, outbox/Resend enqueue, audit. | `surface-user-admin-invitation-create`; success opens `surface-user-admin-invitation-detail`. |
| `user_admin.resend_invitation` | command | Resend eligibility, reason, idempotency, outbox enqueue/status update, no-op handling, audit. | `surface-user-admin-invitation-resend-confirmation`; result refreshes `surface-user-admin-invitation-detail`. |
| `user_admin.revoke_invitation` | command | Open/revokable invitation transition, consequence copy, reason, no-op handling, audit. | `surface-user-admin-invitation-revoke-confirmation`; result refreshes `surface-user-admin-invitation-detail`. |
| `user_admin.acceptance_status.read` | protected read | Invitation expiry, accepted, failed-delivery, bounced, pending, and recovery evidence shaping. | Dashboard, `surface-user-admin-invitation-detail`, directory badges. |
| `user_admin.update_member_status` | command | Disable/reactivate/suspend/remove validation, self-disable and last-admin guardrails, no-op/idempotency, audit. | `surface-user-admin-membership-status-confirmation`; result refreshes `surface-user-admin-user-detail`. |
| `user_admin.preview_role_change` | protected read/proposal | Role/capability diff, policy/approval requirement, affected workstreams, last-admin impact, trace evidence. | `surface-user-admin-role-change-preview`. |
| `user_admin.change_member_roles` | command | Role mutation after current preview, authorization/approval, idempotency, last-admin preservation, audit. | `surface-user-admin-role-change-preview` commit; result refreshes `surface-user-admin-user-detail`. |
| `user_admin.support_access.read` | protected read | Scoped support grant state, expiry, purpose, visibility limits, audit evidence. | Dashboard, `surface-user-admin-users`, `surface-user-admin-user-detail`. |
| `user_admin.support_access.grant_revoke_extend` | command | Grant/revoke/extend eligibility, expiry/purpose capture, approval, idempotency, audit. | `surface-user-admin-support-access-grant` or `surface-user-admin-support-access-revoke-confirmation`; result refreshes `surface-user-admin-user-detail`. |
| `user_admin.identity_relink.review` | proposal/command gated | Exceptional identity link/relink review, policy gate, provider-boundary redaction, audit. | `surface-user-admin-identity-exception-review`; approved recovery routes to workflow/status or user detail. |
| `user_admin.ask_agent` | request/response agent turn | Governed prompt/skill/reference/tool assembly, scoped evidence use, provider/model readiness, fail-closed trace. | Composer, dashboard ask actions, markdown/structured surfaces. |
| `user_admin.access_review.start` | internal worker start | Scope validation, idempotent task start, provider/model readiness gate. | Access-review task surface. |
| `user_admin.access_review.read` | protected read | Task progress/result shaping, evidence refs, redaction, trace links. | Access-review task surface. |
| `user_admin.access_review.cancel` | command | Lifecycle cancellation and trace. | Access-review task surface. |
| `user_admin.access_review.accept_result` | human decision | Record acceptance of advisory result only. | Access-review result decision. |
| `user_admin.access_review.reject_result` | human decision | Reason-validated rejection of advisory result. | Access-review result decision. |
| `admin.audit.read` | protected read | Scoped admin audit and work-trace evidence reauthorization. | Audit/Trace surfaces and trace links. |

## Governed tools and exposure

- `manage-organizations` (`browser-tool`; agent may prepare human-confirmed payloads only): SaaS Owner Organization list/read/create/rename/suspend/reactivate actions backed by internal Tenant authorization, idempotency, safe denials, and audit/work traces.
- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped overview, directory, detail, invitation, support, access-review, and audit evidence views.
- `create-or-resend-invitation` (`browser-tool`; agent may prepare human-confirmed payloads): invitation lifecycle and Resend/captured-outbox delivery boundary.
- `change-membership-role-or-status` (`browser-tool` with approval when risky): membership role/status preview and mutation with idempotency and audit.
- `grant-or-revoke-support-access` (`browser-tool` with expiry/purpose/approval): scoped support access changes.
- `run-access-review` (`agent-tool`, `internal-tool`): durable access-review recommendations and review decisions; no autonomous access mutation.
- `readSkill` / `readReferenceDoc` (`agent-tool` where assigned): governed User Admin expertise loaders with manifest/boundary/status/redaction checks.

## Authorization and denials

Every command/query is scoped by selected `AuthContext`, active account, non-disabled actor status, tenant/customer ids, resource ownership, membership status, role/capability grants, support-access state, and approval policy. Cross-tenant/customer access, disabled users, inactive memberships, email-only authorization, prompt-only privilege grants, hidden-target enumeration, raw token/secret access, unredacted audit export, unsupported bulk side effects, last-admin loss, self-disable/self-admin-role-removal, role escalation, tenant/customer application-data access through Organization Admin, support access through Organization Admin, billing-derived authority, and provider/model/outbox fake success are forbidden.

## Outcomes

In scope: governed user/admin operations, SaaS Owner Organization administration, invitation lifecycle, member status/role changes, support-access lifecycle, access-review recommendations, decision cards for risky changes, admin audit evidence, scoped User Admin agent guidance, and safe local/test email outbox behavior.

Out of scope: public self-registration, full billing/subscription management, application-data access to managed Organizations, support access by implication, app-specific customer billing, autonomous high-impact access changes, client-side authorization, deterministic/model-less normal agent success, and fixture/mock normal runtime data.

## Linked graph nodes

- Workstream: `../workstreams/user-admin/workstream.md`
- Surfaces: `../workstreams/user-admin/surfaces/surfaces.md`
- Agent binding: `../workstreams/user-admin/agents/functional-agent.md`
- Tools: `../workstreams/user-admin/tools/governed-tools.md`
- Tests: `../workstreams/user-admin/tests/coverage.md`
- Traces: `../workstreams/user-admin/traces/work-traces.md`
