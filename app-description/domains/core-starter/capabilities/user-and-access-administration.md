# Capability: User and access administration

## Purpose

Let authorized administrators manage users, memberships, roles/capabilities, invitations, support access, access review, identity relink review, foundation Customer boundaries, Customer Admin bootstrap/maintenance, and admin audit evidence within selected tenant/customer scope without weakening isolation, approval policy, provider boundaries, or traceability.

## Actors and scope

- Tenant admins / Organization Admins manage tenant employees, tenant-owned Customer records, and Customer Admin users within their selected Organization/Tenant scope.
- Customer admins manage customer users, invitations, customer roles, and customer access-review evidence within their selected customer scope.
- Auditors read scoped evidence without mutating access.
- SaaS Owner Admins may invite and manage other SaaS Owner Admin users only from a SaaS Owner selected `AuthContext` with backend `saas_owner.admin.*` capabilities, with last-owner-admin and privilege-escalation protections.
- SaaS Owner Admins may list, read, create, rename, suspend, and reactivate customer-facing Organizations backed by internal Tenant boundaries only from a SaaS Owner selected `AuthContext` with backend `saas_owner.organization.*` product capabilities mapped to internal `saas_owner.tenant.read` or `saas_owner.tenant.manage` grants.
- SaaS Owner Admins may invite, list, and manage Organization Admin users for a selected Organization/Tenant after the Organization exists. Those actions target `TENANT_ADMIN` memberships inside the selected Organization/Tenant but do not switch the actor into tenant app-data authority or support access.
- Organization Admins may create, list, read, rename/update, suspend/archive, and reactivate foundation Customer boundary records within their selected Organization/Tenant, and may invite, list, and manage Customer Admin users for a selected Customer after that Customer exists. Those actions target `CUSTOMER_ADMIN` memberships inside the selected Customer and do not grant sibling-customer, Organization Admin, or SaaS Owner authority.
- SaaS Owner support roles may inspect or explain tenant/customer user data only through active tenant-created support access.
- Billing-boundary authority, billing provider state, and Organization administration do not grant tenant/customer application-data access or support access.
- User Admin functional agent may recommend, draft, summarize, or prepare decision cards but cannot expand authority or mutate access without backend capability authorization and human/approval gates.

## Foundation customer-boundary capability contract

The `tenant.customer.*` capabilities manage the secure SaaS Customer boundary owned by the core starter. They are tenant-scoped administrative operations for authorization, Customer Admin scope, redaction, and audit/work trace attribution; they are not CRM, customer-success, sales/revenue, support/service, billing, or customer-intelligence operations.

The `tenant.customer_admin.*` capabilities bootstrap and maintain Customer Admin users for one selected Customer after that Customer exists. They preserve the Organization Admin actor context, require target-customer proof, and must never grant sibling-customer, Organization Admin, SaaS Owner, billing, support-access, or business customer-domain authority by implication.

All foundation customer-boundary commands require backend capability authorization, selected `AuthContext`, idempotency key, correlation id, safe validation, explicit human confirmation for consequential lifecycle changes, audit/work trace emission, and safe denials for hidden, suspended, sibling-customer, cross-tenant, missing-capability, last-admin, provider/outbox/model, or policy failures.

## Capability inventory

| Capability id | Type | Responsibility | Surface/API exposure |
|---|---|---|---|
| `saas_owner.organization.list` | protected read | List/search browser-safe Organizations visible to SaaS Owner Admin; internally backed by Tenant records and `saas_owner.tenant.read`. | `surface-user-admin-organization-directory`; `GET /api/admin/organizations`. |
| `saas_owner.organization.read` | protected read | Read one Organization detail and platform-safe lifecycle/audit metadata without exposing tenant app data, customer records, provider secrets, hidden counts, or billing-derived authority. | `surface-user-admin-organization-detail`; `GET /api/admin/organizations/{organizationId}`. |
| `saas_owner.organization.create` | command | Create an active Organization/Tenant boundary with normalized name, idempotency key, correlation id, validation, and audit/work trace evidence; internally requires `saas_owner.tenant.manage`. | `surface-user-admin-organization-create`; `POST /api/admin/organizations`; success opens detail. |
| `saas_owner.organization.rename` | command | Rename an Organization display label with validation, no-op replay semantics, idempotency key, and audit/work trace evidence. | `surface-user-admin-organization-rename`; `POST /api/admin/organizations/{organizationId}/rename`; success refreshes detail. |
| `saas_owner.organization.suspend` | command | Suspend an Organization/Tenant boundary without exposing or mutating tenant application data; records reason, idempotency, warnings, and audit/work trace evidence. | `surface-user-admin-organization-suspend-confirmation`; `POST /api/admin/organizations/{organizationId}/suspend`; success refreshes detail. |
| `saas_owner.organization.reactivate` | command | Reactivate a suspended Organization/Tenant boundary with no-op handling, idempotency key, and audit/work trace evidence. | `surface-user-admin-organization-reactivate-confirmation`; `POST /api/admin/organizations/{organizationId}/reactivate`; success refreshes detail. |
| `saas_owner.admin.list` | protected read | List/search SaaS Owner Admin users and invitations without exposing hidden users, raw provider ids, or secrets. | `surface-user-admin-saas-owner-admins`; SaaS Owner Admin APIs/workstream actions. |
| `saas_owner.admin.invite` | command | Invite another SaaS Owner Admin with email normalization, role validation, idempotency, invite delivery/outbox handling, and audit. | `surface-user-admin-saas-owner-admin-invitation-create`; success opens SaaS Owner Admin invitation/detail. |
| `saas_owner.admin.manage` | command | Change SaaS Owner Admin membership status/roles with last-owner-admin protection, self-action guardrails, approval where risky, idempotency, and audit. | SaaS Owner Admin detail, role preview, and lifecycle confirmation surfaces. |
| `saas_owner.organization_admin.list` | protected read | List/search Organization Admin users and invitations for a selected Organization/Tenant from SaaS Owner scope without exposing tenant app data or hidden organization facts. | `surface-user-admin-organization-admins`; organization detail action; `GET /api/admin/organizations/{organizationId}/admins`. |
| `saas_owner.organization_admin.invite` | command | Bootstrap or invite a `TENANT_ADMIN` into the selected Organization/Tenant after the Organization exists, with target-organization validation, role validation, idempotency, outbox/Resend handling, and audit. | `surface-user-admin-organization-admin-invitation-create`; `POST /api/admin/organizations/{organizationId}/admins/invitations`; success opens admin invitation/detail. |
| `saas_owner.organization_admin.manage` | command | Manage Organization Admin memberships for a selected Organization/Tenant, including resend/revoke admin invite, role replacement within tenant-admin-safe roles, suspend/reactivate/remove membership, and last-organization-admin protection. | Organization Admin detail, invitation detail, role preview, lifecycle confirmation surfaces; `POST/PUT /api/admin/organizations/{organizationId}/admins/...`. |
| `tenant.customer.list` | protected read | List/search browser-safe foundation Customer boundaries within the selected Organization/Tenant visible to an Organization Admin, redacting hidden sibling/cross-tenant facts. | `surface-user-admin-customer-directory`; `GET /api/admin/customers`. |
| `tenant.customer.read` | protected read | Read one foundation Customer detail and safe lifecycle/admin metadata within the selected Organization/Tenant without exposing sibling customers, hidden counts, business-domain records, or private cross-scope evidence. | `surface-user-admin-customer-detail`; `GET /api/admin/customers/{customerId}`. |
| `tenant.customer.create` | command | Create a foundation Customer boundary under the selected Organization/Tenant with normalized name, idempotency key, validation, target-scope proof, and audit/work trace evidence. | `surface-user-admin-customer-create`; `POST /api/admin/customers`; success opens detail. |
| `tenant.customer.rename` | command | Rename/update the Customer display label with validation, no-op replay semantics, idempotency, target-scope proof, and audit/work trace evidence; this does not mutate business customer-domain data. | `surface-user-admin-customer-rename`; `POST /api/admin/customers/{customerId}/rename`; success refreshes detail. |
| `tenant.customer.suspend` | command | Suspend/archive a Customer boundary without exposing or mutating unrelated customer application data; records reason, idempotency, warnings, target-scope proof, and audit/work trace evidence. | `surface-user-admin-customer-suspend-confirmation`; `POST /api/admin/customers/{customerId}/suspend`; success refreshes detail. |
| `tenant.customer.reactivate` | command | Reactivate a suspended Customer boundary with no-op handling, idempotency key, target-scope proof, and audit/work trace evidence. | `surface-user-admin-customer-reactivate-confirmation`; `POST /api/admin/customers/{customerId}/reactivate`; success refreshes detail. |
| `tenant.customer_admin.list` | protected read | List/search Customer Admin users and invitations for one selected Customer within the Organization/Tenant. | `surface-user-admin-customer-admins`; customer detail action; `GET /api/admin/customers/{customerId}/admins`. |
| `tenant.customer_admin.invite` | command | Bootstrap or invite a `CUSTOMER_ADMIN` into the selected Customer after the Customer exists, with target-customer validation, role validation, idempotency, outbox/Resend handling, and audit. | `surface-user-admin-customer-admin-invitation-create`; `POST /api/admin/customers/{customerId}/admins/invitations`; success opens admin invitation/detail. |
| `tenant.customer_admin.manage` | command | Manage Customer Admin memberships for a selected Customer, including resend/revoke admin invite, role replacement within customer-admin-safe roles, suspend/reactivate/remove membership, and last-customer-admin protection. | Customer Admin detail, invitation detail, role preview, lifecycle confirmation surfaces; `POST/PUT /api/admin/customers/{customerId}/admins/...`. |
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
- `manage-saas-owner-admins` (`browser-tool`; agent may prepare human-confirmed payloads only): SaaS Owner Admin user invitation, list/read, role/status maintenance, and last-owner-admin protection under SaaS Owner scope.
- `manage-organization-admins` (`browser-tool`; agent may prepare human-confirmed payloads only): SaaS Owner bootstrap and maintenance of `TENANT_ADMIN` users for a selected Organization/Tenant without tenant app-data or support-access authority.
- `manage-customers` (`browser-tool`; agent may prepare human-confirmed payloads only): Organization Admin Customer list/read/create/rename/suspend/reactivate actions inside the selected Organization/Tenant.
- `manage-customer-admins` (`browser-tool`; agent may prepare human-confirmed payloads only): Organization Admin bootstrap and maintenance of `CUSTOMER_ADMIN` users for a selected Customer without sibling-customer, Organization Admin, or SaaS Owner authority.
- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped overview, directory, detail, invitation, support, access-review, and audit evidence views.
- `create-or-resend-invitation` (`browser-tool`; agent may prepare human-confirmed payloads): invitation lifecycle and Resend/captured-outbox delivery boundary.
- `change-membership-role-or-status` (`browser-tool` with approval when risky): membership role/status preview and mutation with idempotency and audit.
- `grant-or-revoke-support-access` (`browser-tool` with expiry/purpose/approval): scoped support access changes.
- `run-access-review` (`agent-tool`, `internal-tool`): durable access-review recommendations and review decisions; no autonomous access mutation.
- `readSkill` / `readReferenceDoc` (`agent-tool` where assigned): governed User Admin expertise loaders with manifest/boundary/status/redaction checks.

## Production runtime contract

Invitation delivery, identity exception recovery, and model-backed access-review automation are governed by this capability contract and the User Admin workstream surface, behavior, tool, trace, and test nodes. Archived hardening specs may provide historical implementation evidence, but current product authority is captured here: Resend/outbox invitation side effects fail closed when unconfigured; identity exception recovery is durable and provider-redacted; access-review automation invokes a governed Akka Agent path when configured and remains advisory until explicit human review.

## Authorization and denials

Every command/query is scoped by selected `AuthContext`, active account, non-disabled actor status, tenant/customer ids, resource ownership, membership status, role/capability grants, support-access state, and approval policy. Cross-tenant/customer access, disabled users, inactive memberships, email-only authorization, prompt-only privilege grants, hidden-target enumeration, raw token/secret access, unredacted audit export, unsupported bulk side effects, last-admin loss, self-disable/self-admin-role-removal, role escalation, tenant/customer application-data access through Organization Admin, support access through Organization Admin, billing-derived authority, and provider/model/outbox fake success are forbidden.

## Outcomes

In scope: governed user/admin operations, SaaS Owner Admin user management, SaaS Owner Organization administration, SaaS Owner bootstrap/maintenance of Organization Admin users, Organization Admin foundation Customer-boundary administration, Organization Admin bootstrap/maintenance of Customer Admin users, invitation lifecycle, member status/role changes, support-access lifecycle, access-review recommendations, decision cards for risky changes, admin audit evidence, scoped User Admin agent guidance, and safe local/test email outbox behavior.

Out of scope: public self-registration, full billing/subscription management, application-data access to managed Organizations, support access by implication, app-specific CRM/customer-success/sales/support/billing/customer-intelligence domains, app-specific customer billing, autonomous high-impact access changes, client-side authorization, deterministic/model-less normal agent success, and fixture/mock normal runtime data.

## Linked graph nodes

- Workstream: `../workstreams/user-admin/workstream.md`
- Surfaces: `../workstreams/user-admin/surfaces/surfaces.md`
- Agent binding: `../workstreams/user-admin/agents/functional-agent.md`
- Tools: `../workstreams/user-admin/tools/governed-tools.md`
- Tests: `../workstreams/user-admin/tests/coverage.md`
- Traces: `../workstreams/user-admin/traces/work-traces.md`
