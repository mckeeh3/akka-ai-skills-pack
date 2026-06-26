# Worker: Customer Admin Human

workerId: `user-admin.customer-admin-human`
workerType: `human`
reasoningEngine: `human`
scope: `workstream-binding`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

Authenticated Customer Admin who administers Customer Users, customer-scoped invitations, memberships, roles, and access-review evidence inside one selected Customer scope.

## Responsibility

Owns/does:

- Inspect and manage visible Customer Users and invitations for the selected Customer.
- Review customer-scoped role/capability recommendations, invitation status, user detail, and access-review evidence.
- Confirm allowed customer-scoped user invitation, membership lifecycle, and role-change actions.

Does not own/do:

- Create Customers, manage sibling Customers, manage tenant employees, manage Customer Admin peers unless explicitly allowed by backend policy, manage Organization Admins/SaaS Owner Admins, grant support access, or see hidden tenant/customer evidence.

## Behavior profile

- Instructions/prompt: User Admin customer-admin surface guidance and safe denial copy.
- Skills: Customer User invitation review, least-privilege customer role assignment, last-customer-admin/user-impact awareness, redacted audit evidence review.
- Tools: `search-user-directory`, `create-or-resend-invitation`, and `change-membership-role-or-status` through `surface_action` and protected API where customer policy permits.
- Evidence profile: browser-safe Customer User, invitation, membership, role, access-review, and redacted audit evidence for the selected Customer only.

## Authority and scope

- authorityLevel: `administer` within selected Customer for Customer User administration only.
- AuthContext scope: active Customer Admin membership and customer-scoped backend capabilities.
- Requires confirmation for invitations, resend/revoke, role/status changes, and any approval-gated Customer User action.
- Denied/hidden behavior: safe system message for tenant-level actions, sibling-customer targets, SaaS Owner/Organization Admin targets, support-access changes, hidden/not-found users, and stale context.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| User Admin structured surfaces | `surface_action` | browser | `surface_action` | Customer User list/detail/invitation/role/status surfaces only. |
| User Admin composer + assistant | `human_chat_tool_plan` | browser/chat | `human_chat_tool_plan` | No direct high-impact Customer Admin chat execution unless a later catalog entry explicitly allows it. |
| Protected API | `api_call` | browser API | `api_call` | Revalidates selected Customer scope and target ownership every request. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `search-user-directory` | `user_admin.list_members`, `user_admin.read_user_account` | `surface_action`, `api_call` | observe | Scoped/redacted Customer User reads. |
| `create-or-resend-invitation` | `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation` | `surface_action`, `api_call` | execute | Confirmation, role validation, provider/outbox fail-closed. |
| `change-membership-role-or-status` | `user_admin.preview_role_change`, `user_admin.change_member_roles`, `user_admin.update_member_status` | `surface_action`, `api_call` | execute/approve | Preview/confirmation and last-admin/self-action policy. |

## Audit and work traces

Trace customer-scoped reads, invitations, role/status actions, denied tenant/sibling/SaaS Owner attempts, no-ops, stale targets, and audit drilldowns with worker id, selected Customer scope, governed tool, capability, adapter, correlation/idempotency, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: Customer Admin Customer User list/invite/detail/status/role paths, sibling-customer and tenant-level denials, support-access denial, browser secret boundaries, idempotency, and traces.
- Manual runtime scenario: Customer Admin opens User Admin -> lists Customer Users -> invites one Customer User -> verifies invitation detail and trace evidence without tenant employee or sibling-customer leakage.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
