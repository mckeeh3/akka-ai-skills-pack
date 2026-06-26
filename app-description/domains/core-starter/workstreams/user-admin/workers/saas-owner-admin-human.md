# Worker: SaaS Owner Admin Human

workerId: `user-admin.saas-owner-admin-human`
workerType: `human`
reasoningEngine: `human`
scope: `workstream-binding`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

Authenticated SaaS Owner/App Admin who administers app-owner users, customer-facing Organizations, and Organization Admin bootstrap/maintenance from a selected SaaS Owner `AuthContext`.

## Responsibility

Owns/does:

- Inspect and manage visible SaaS Owner Admin users and invitations.
- Create, inspect, rename, suspend, terminally archive, and reactivate eligible Organizations backed by Tenant boundaries.
- Bootstrap and maintain Organization Admin (`TENANT_ADMIN`) users for one selected Organization after that Organization exists.
- Confirm side-effecting surface actions and approved `human_chat_tool_plan` steps when policy allows.

Does not own/do:

- Tenant/customer application-data access, support access, billing-derived authority, Customer Admin work, or hidden tenant/customer enumeration by implication.
- Prompt-only authority changes, raw provider/JWT/invitation-token access, or hard-delete/provider-erasure behavior.

## Behavior profile

- Instructions/prompt: User Admin human-operating guidance embedded in User Admin surfaces and system messages.
- Skills: app-owner administration, Organization boundary management, invitation/risk review, last-owner/last-organization-admin protection.
- Tools: `manage-saas-owner-admins`, `manage-organizations`, `manage-organization-admins`, `search-user-directory`, `create-or-resend-invitation`, `change-membership-role-or-status`, and `admin.audit.read` through `surface_action`; selected catalog-bound operations may also use `human_chat_tool_plan` after exact plan confirmation.
- Evidence profile: browser-safe app-owner, Organization, Organization Admin, invitation, readiness, and audit evidence only.

## Authority and scope

- authorityLevel: `administer` with backend capability checks.
- AuthContext scope: selected SaaS Owner/App Admin context.
- Requires approval/confirmation for Organization lifecycle, app-owner/Organization Admin invitation and membership changes, last-admin-risk changes, role escalation, bulk/low-confidence actions, and chat-plan execution.
- Denied/hidden behavior: safe `surface-user-admin-system-message` with no hidden target/count enumeration.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| User Admin structured surfaces | `surface_action` | browser | `surface_action` | Forms/confirmations/decision cards submit backend-authorized commands. |
| User Admin composer + assistant | `human_chat_tool_plan` | browser/chat | `human_chat_tool_plan` | Proposal is no-mutation; execution requires exact human confirmation and per-step reauthorization. |
| Protected API | `api_call` | browser API | `api_call` | API never trusts route, hidden fields, or frontend role labels. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `manage-saas-owner-admins` | `saas_owner.admin.*` | `surface_action`, `human_chat_tool_plan`, `api_call` | administer | Required for invite/manage; last-owner guardrails. |
| `manage-organizations` | `saas_owner.organization.*` | `surface_action`, `human_chat_tool_plan`, `api_call` | administer | Required for create/rename/lifecycle; archive is terminal. |
| `manage-organization-admins` | `saas_owner.organization_admin.*` | `surface_action`, `human_chat_tool_plan`, `api_call` | administer | Required for Organization Admin bootstrap/manage. |
| `search-user-directory` | `user_admin.*`, `admin.audit.read` | `surface_action`, `api_call` | observe | Read evidence is scoped and redacted. |

## Audit and work traces

Trace every surface load, target open, proposal, confirmation, allowed command, denial, stale/no-op result, outbox/provider blocked state, and audit drilldown with this worker id, selected `AuthContext`, governed tool, capability, adapter, correlation/idempotency basis, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: SaaS Owner dashboard branches, Organization create/lifecycle, Organization Admin invitation/manage, last-owner/last-organization-admin denial, tenant/customer app-data denial, chat-plan exact confirmation, idempotency, and trace evidence.
- Manual runtime scenario: SaaS Owner opens User Admin -> creates an Organization -> invites Organization Admin -> verifies invitation/detail/audit traces through protected API/UI.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
