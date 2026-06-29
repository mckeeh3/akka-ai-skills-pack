# Tools: User Admin

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Allowed governed tools:

- `manage-saas-owner-admins` (`browser-tool`; human-confirmed agent preparation only): SaaS Owner Admin list/read, app-owner invitation create/resend/revoke, role/status maintenance, last-owner-admin checks, and audit evidence.
- `manage-organizations` (`browser-tool`; human-confirmed agent preparation only): SaaS Owner Organization list/read/create/rename/suspend/archive/reactivate backed by internal Tenant authorization and terminal archive semantics.
- `manage-organization-admins` (`browser-tool`; human-confirmed agent preparation only): SaaS Owner bootstrap and maintenance of `TENANT_ADMIN` users for a selected Organization/Tenant, including admin invitation lifecycle, role/status changes, and last-organization-admin checks.
- `manage-customers` (`browser-tool`; human-confirmed agent preparation only): Organization Admin Customer list/read/create/rename/suspend/archive/reactivate actions inside the selected Organization/Tenant with terminal archive semantics.
- `manage-customer-admins` (`browser-tool`; human-confirmed agent preparation only): Organization Admin bootstrap and maintenance of `CUSTOMER_ADMIN` users for a selected Customer, including admin invitation lifecycle, role/status changes, and last-customer-admin checks.
- `search-user-directory` (`browser-tool`, `agent-tool` read): scoped dashboard, users directory, user detail inspection, invitation status, support-access state, access-review state, and audit-evidence views. Directory output is discovery-only; row/card activation opens inspection surfaces.
- `create-or-resend-invitation` (`browser-tool`; human-confirmed agent preparation only): admin invitation create, detail read, resend confirmation, revoke confirmation, and delivery/outbox visibility.
- `accept-invitation` (`onboarding-tool`; invitee/system only, no agent exposure): signed-token plus WorkOS/AuthKit invitation acceptance, account/membership link or creation, accepted status update, idempotent repeat completion, selected-context refresh guidance, and safe expired/revoked/mismatched/hidden recovery.
- `change-membership-role-or-status` (`browser-tool`; approval/decision-card when risky): membership/account lifecycle confirmations and role preview/change commits through dedicated task surfaces.
- `grant-or-revoke-support-access` (`browser-tool`; expiry/purpose/approval required): support-access grant/extend forms and revoke confirmations.
- `run-access-review` (`agent-tool`, `internal-tool`): start/read/cancel/review durable access-review recommendations; recommendations route follow-up access changes through deterministic User Admin task surfaces.
- `admin.audit.read` (`browser-tool`, `internal-tool`; read-only `agent-tool` only when boundary-granted): scoped admin-audit evidence summaries, trace-link handoff metadata, denial/no-op evidence, and redacted investigation context for User Admin surfaces.
- `readSkill` / `readReferenceDoc` (`agent-tool` only when assigned and boundary-granted): load active User Admin expertise documents after manifest, scope, status, token, and redaction checks.

## Boundaries

Tools are exposed as browser, agent, API, workflow, timer, consumer, or internal tools only as stated by the linked capability. Side-effecting or high-impact tools require idempotency, correlation, authorization, confirmation or approval policy, and audit/work traces. Denied tool calls are traced and return safe feedback.

Adapter/source matrix:

| Governed tool id | Tool type / exposure | Allowed actor adapter sources | Result and transaction boundary |
|---|---|---|---|
| `create-or-resend-invitation` | invitation lifecycle browser/API/system tool | `surface_action`, confirmed `human_chat_tool_plan`, `api_call`, outbox `consumer_reaction`, expiry/reminder `timer_invocation` where enabled | Create/resend/revoke are idempotent per invitation target and return invitation detail, partial-failure/outbox-blocked result, or system-message; invitee acceptance is separate. |
| `accept-invitation` | onboarding/system tool | invitee `api_call`, `internal_call` | Signed-token + WorkOS/AuthKit identity acceptance is idempotent and returns onboarding result/selected-context refresh guidance; no agent exposure. |
| `change-membership-role-or-status` | membership/role browser/API tool | `surface_action`, approved/confirmed `human_chat_tool_plan` only when cataloged, protected `api_call` | Role commit uses one backend transaction boundary after preview/version revalidation; membership status changes use dedicated confirmation/idempotency and return user detail, decision, partial-failure result, or system-message. |
| `grant-or-revoke-support-access` | support-access browser/API tool | `surface_action`, protected `api_call`; chat only as proposal unless later cataloged | Tenant Admin approval, purpose, expiry, idempotency, read-time expiry, and audit are mandatory; result surfaces distinguish success, approval-required, no-op, denial, and partial failure. |
| `run-access-review` | advisory agent/internal workflow tool | `surface_action`, `agent_tool_call`, `workflow_step`, `internal_call` | Access-review tasks produce progress/result/review surfaces; accept/reject records review only and never directly mutates access. |
| `admin.audit.read` | admin-audit read/evidence tool | `surface_action`, protected `api_call`, `internal_call`, read-only bounded `agent_tool_call` | Returns browser-safe audit summaries or Audit/Trace handoff refs; trace detail reauthorizes and denials return no-enumeration system-message. |

The agent may prepare payloads and explain tool outcomes, but cannot autonomously invite SaaS Owner Admins, invite Organization Admins, invite Customer Admins, create Customers, send invitations, accept invitations for users, change roles, disable/reactivate users, alter support access, resolve reviews, or expand authority. Tool output must be browser-safe and app-owner/tenant/customer scoped.


## `human_chat_tool_plan` expanded current-intent catalog

This catalog records current intent for later runtime expansion. It reuses the same governed tool ids as browser surface actions and does not by itself change runtime behavior. Exposure channel `human_chat_tool_plan` remains proposal-and-confirmation only: deterministic no-mutation surface routing runs first, the initial execution-oriented chat request may only return a no-mutation plan proposal, and no state changes until exact human plan-snapshot confirmation and backend authorization succeed.

Allowed expanded entries:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability ids | Input schema and validation | Result surface(s) |
|---|---|---|---|---|---|---|
| `chat-executable-now` | `create org "Org 1" and invite alex@example.com as org admin`; `invite alex@example.com as a tenant user`; `invite alex@example.com as a SaaS owner admin`; `invite alex@example.com as customer admin for Acme` | `action-submit-organization-create`; `action-submit-organization-admin-invitation`; `action-submit-user-admin-invitation`; `action-submit-saas-owner-admin-invitation`; `action-customer-admin-invite` | `manage-organizations`; `manage-organization-admins`; `create-or-resend-invitation`; `manage-saas-owner-admins`; `manage-customer-admins` | `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite`; `user_admin.invite_user`; `saas_owner.admin.invite`; `tenant.customer_admin.invite` | Organization/customer/role-specific invitation schemas; roles are pinned to backend-authorized options such as `TENANT_ADMIN` or `CUSTOMER_ADMIN`; visible Organization/Customer binding, provider/outbox fail-closed, duplicate handling, and idempotency key required | Organization/customer/invitation detail surfaces |
| `chat-executable-now` | `resend this invitation`; `revoke this invitation because it was sent to the wrong address` | `action-useradmin-resend-invitation`; `action-confirm-user-admin-invitation-revoke` | `create-or-resend-invitation` | `user_admin.resend_invitation`; `user_admin.revoke_invitation` | `schema.invitation.resend.v1`; `schema.invitation.revoke.v1`; require backend-visible invitation id/status, reason for revoke, provider/outbox fail-closed checks, no-op/idempotent replay handling | `surface-user-admin-invitation-detail`; `surface-user-admin-invitation-revoke-confirmation`; `surface-user-admin-system-message` |
| `chat-executable-now` | `create customer "Acme"`; `rename this customer to Acme North`; `rename this organization to Example SMB` | `action-submit-customer-create`; `action-submit-customer-rename`; `action-submit-organization-rename` | `manage-customers`; `manage-organizations` | `tenant.customer.create`; `tenant.customer.rename`; `saas_owner.organization.rename` | `schema.customer-admin.create.submit.v1`; `.rename.v1`; `schema.organization-admin.rename.submit.v1`; require selected Organization/Tenant authority, visible customer/org id for rename, duplicate/conflict handling, and idempotency | `surface-user-admin-customer-detail`; `surface-user-admin-organization-detail` |

Expanded classification and blocked/surface-only rationale:

| Classification | Action groups | Rationale and boundary |
|---|---|---|
| `router-only` | Dashboard/list/detail opens, branch returns, broad directories, audit-trace opens | Deterministic surfaces preserve row visibility and no-enumeration; execution plans must not invent target ids. |
| `surface-only` | Open-only confirmation surfaces, target-specific reads without visible-row binding, support-access read/validate, access-review read/result disposition, identity exception review opens | The browser surface carries required target context, backend-authored options, stale-surface handling, or evidence review. |
| `chat-proposal-only` | Role/capability preview, access-review recommendations, advisory preparation that does not commit access | Safe only as evidence/proposal; final access mutation still needs dedicated approval/confirmation semantics. |
| `approval-gated` | Organization/customer suspend/archive/reactivate; membership/account disable/reactivate/remove; role-change commits; support-access grant/revoke/extend; access-review start/cancel/accept/reject; identity relink approve/deny/complete | These alter authority, lifecycle, support access, or identity recovery; chat confirmation alone is insufficient and last-admin/self-action/provider policies must remain authoritative. |
| `blocked-pending-design` | Permanent account removal and provider identity recovery completion paths | Require explicit recovery/destructive lifecycle design, provider redaction, and audit policy before any chat exposure. |
| `internal-only` | Invitation acceptance, WorkOS/AuthKit linking, expiry jobs, delivery retry workers, audit projection consumers | Service/provider/background paths not appropriate as direct chat catalog steps. |
| `out-of-scope` | Business-domain user permissions outside the foundation admin hierarchy | Outside the five foundation workstreams for this catalog expansion. |

Execution requirements for every accepted User Admin entry:

- proposal step validation rejects out-of-catalog action/tool/capability combinations, hidden targets, unsafe output bindings, missing provider/runtime/tool-boundary readiness, unsupported fields, unauthorized roles, and cross-tenant/customer targets;
- confirmation must bind `planId`, `planSnapshotId`, selected `AuthContext`, requestedBy, confirmedBy, step hashes, idempotency root, correlation id, and visible side-effect acknowledgements;
- every confirmed step recomputes backend authorization, selected tenant/customer ownership, last-admin/self-action constraints where applicable, provider/outbox readiness, approval policy, idempotency, and trace emission;
- idempotent replay returns the prior proposal/result without duplicate Organizations, Customers, invitations, outbox sends, or traces; partial failure reports completed, failed, skipped, and recovery steps;
- no workstream agent, prompt, frontend route, disabled/visible control, or tool description grants autonomous mutation authority.
