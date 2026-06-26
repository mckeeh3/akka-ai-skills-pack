# Worker: Organization Admin Human

workerId: `user-admin.organization-admin-human`
workerType: `human`
reasoningEngine: `human`
scope: `workstream-binding`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

Authenticated Tenant/Organization Admin who administers tenant employees, foundation Customer boundaries, Customer Admin bootstrap/maintenance, tenant-scoped invitations, and support access inside one selected Organization/Tenant.

## Responsibility

Owns/does:

- Inspect and manage tenant employees and tenant-scoped invitations/memberships/roles within selected Organization/Tenant scope.
- Create, inspect, rename, suspend, terminally archive, and reactivate eligible foundation Customers inside the Organization/Tenant.
- Bootstrap and maintain Customer Admin (`CUSTOMER_ADMIN`) users for one selected Customer after that Customer exists.
- Grant, extend, or revoke tenant-wide or customer-scoped support access when policy allows.

Does not own/do:

- SaaS Owner/App Admin management, sibling Organization administration, Customer User work outside selected Customer scope, Customer CRM/sales/support/billing data, or support access by implication.
- Role escalation to `SAAS_OWNER_ADMIN` or Customer Admin flows that grant Organization Admin authority.

## Behavior profile

- Instructions/prompt: User Admin tenant-admin surface guidance and denial/system-message copy.
- Skills: least-privilege role review, Customer boundary management, support-access procedure, last-admin protection, invitation troubleshooting.
- Tools: `manage-customers`, `manage-customer-admins`, `search-user-directory`, `create-or-resend-invitation`, `change-membership-role-or-status`, `grant-or-revoke-support-access`, `run-access-review`, and `admin.audit.read` through backend-authorized adapters.
- Evidence profile: browser-safe tenant, Customer, Customer Admin, tenant employee, support-access, access-review, and audit evidence; sibling/customer-hidden facts are redacted.

## Authority and scope

- authorityLevel: `administer` within selected Organization/Tenant.
- AuthContext scope: active Organization/Tenant Admin membership and backend `tenant.*` / `user_admin.*` capabilities.
- Requires approval/confirmation for Customer lifecycle, Customer Admin invitation/manage, tenant employee role/status changes, support access, identity recovery, last-admin-risk changes, and chat-plan execution where later enabled.
- Denied/hidden behavior: no-enumeration `surface-user-admin-system-message` for sibling Organization, sibling Customer, SaaS Owner, hidden target, stale context, or support-access policy denial.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| User Admin structured surfaces | `surface_action` | browser | `surface_action` | Dedicated create/edit/lifecycle/decision surfaces are authoritative for consequential work. |
| User Admin composer + assistant | `human_chat_tool_plan` | browser/chat | `human_chat_tool_plan` | Only catalog-bound low-risk invitation/Customer name operations may be proposed; risky actions remain approval-gated or surface-only. |
| Protected API | `api_call` | browser API | `api_call` | Every request revalidates selected Organization/Tenant and target Customer ownership. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `manage-customers` | `tenant.customer.*` | `surface_action`, `human_chat_tool_plan`, `api_call` | administer | Confirmation for create/rename/lifecycle; archive is terminal. |
| `manage-customer-admins` | `tenant.customer_admin.*` | `surface_action`, `human_chat_tool_plan`, `api_call` | administer | Confirmation for invitation/manage; last-customer-admin guardrails. |
| `create-or-resend-invitation` | `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation` | `surface_action`, `human_chat_tool_plan`, `api_call` | execute | Provider/outbox fail-closed and idempotent. |
| `grant-or-revoke-support-access` | `user_admin.support_access.grant_revoke_extend` | `surface_action`, `api_call` | approve/administer | Tenant Admin approval, expiry, purpose, audit required. |
| `run-access-review` | `user_admin.access_review.*` | `surface_action`, `api_call` | propose/review | Advisory only; follow-up mutations use deterministic tools. |

## Audit and work traces

Trace Customer lifecycle, Customer Admin lifecycle, tenant employee/user changes, invitations, support access, access-review actions, denials, no-ops, and hidden/stale target attempts with worker id, selected tenant/customer scope, adapter, governed tool, capability, correlation/idempotency, policy decision, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: Customer lifecycle, Customer Admin invite/manage, tenant employee invite/status/role changes, support-access approval/expiry, sibling/customer denials, last-admin protections, idempotency, and trace evidence.
- Manual runtime scenario: Organization Admin opens User Admin -> creates a Customer -> invites Customer Admin -> verifies Customer Admin detail/invitation/audit traces without SaaS Owner or sibling-customer leakage.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
