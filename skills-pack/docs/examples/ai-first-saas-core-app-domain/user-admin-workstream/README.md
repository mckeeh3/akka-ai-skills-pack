# User Admin Workstream PRD

## PRD identity

- **Workstream id:** `user_admin`
- **Backing functional agent:** `functional_agent.user_admin`
- **Domain:** `ai_first_saas_core_app`
- **Purpose:** tenant/customer-scoped user administration: user discovery, invitations, memberships, roles/capabilities, disabled access, access review, support-access visibility, and admin audit
- **Minimum starter role:** User Admin participates in the five core workstream starter using `markdown_response`; it is not a valid single-workstream generated SaaS starter by itself, and full-core readiness requires the surfaces/capabilities below

## Invariants

```text
This workstream is backed by exactly one functional/context-area agent.
Surfaces are the only renderable workstream artifacts.
System messages are typed surfaces.
Every surface action, including read/query and surface-request actions, maps to a governed backend capability.
The workstream agent may request surfaces and guide users, but backend capabilities enforce authority.
```

## User intents

The workstream agent must handle:

- `dashboard`, `show user admin dashboard`
- `show users`, `find Alex`, `search by email`, `show disabled users`
- `view user`, `edit this user`, `show roles for this user`
- `invite user`, `resend invitation`, `revoke invitation`
- `disable user`, `reactivate user`, `suspend membership`
- `assign role`, `why can't I grant tenant admin`, `explain this denial`
- `show access review`, `who has admin access`
- `show recent admin audit`
- help/how-to questions for user management

The agent may draft or recommend side-effecting changes, but commit requires explicit surface action and backend authorization.

## Required surfaces

| Surface id | Type | Purpose | Producing capability | Primary actions |
|---|---|---|---|---|
| `surface.user_admin.dashboard.v1` | dashboard | admin health, pending invites, failed deliveries, disabled users, admin count, recent audit | `user_admin.dashboard.view` | search users, open invite form, open invitations, open access review, open audit summary |
| `surface.user_admin.users_list.v1` | data_table/search | scoped user and invitation search | `user_admin.users.search` | open user, filter, sort, page, open invite form |
| `surface.user_admin.user_detail_edit.v1` | detail/form | user profile, memberships, roles, capabilities, status, audit links | `user_admin.users.view` | save profile, change membership status, assign roles, disable/reactivate, open audit timeline |
| `surface.user_admin.invite_user.v1` | form | create invitation | `user_admin.invitations.form` | create invitation, preview, cancel |
| `surface.user_admin.invitations.v1` | data_table | pending/accepted/expired/revoked invitations and delivery status | `user_admin.invitations.search` | resend, revoke, open invitation detail |
| `surface.user_admin.roles_capabilities.v1` | detail/table | role/capability catalog visible to caller | `user_admin.roles.list` | open role detail, use in assignment |
| `surface.user_admin.access_review.v1` | dashboard/table | high-risk access, admins, stale memberships, review items | `user_admin.access_review.view` | open user, start review, mark reviewed, request approval |
| `surface.user_admin.admin_audit_summary.v1` | audit_timeline | recent user-admin audit | `user_admin.audit.summary` | open audit trace, filter, export request if allowed |
| `surface.user_admin.system_message.v1` | system_message | denials, validation, last-admin protection, success, stale/reconnect | capability-specific | retry, open trace, request approval |

## Surface style expectations

These surfaces inherit `ai-first-workstream-enterprise` from `docs/web-ui-style-guide.md`: calm enterprise workstream styling, named-theme tokens, neutral layered cards, blue/indigo accent, sparse semantic status colors, accessible focus states, and strong numeric/table hierarchy. Style clarifies authority, risk, evidence, and auditability; it must not change surface inventory, capability mappings, authorization, routes, or audit behavior.

- Dashboard: render as a mission-control briefing with a KPI strip for admin health, pending invites, failed deliveries, disabled users, and admin counts; keep attention queues for failed delivery, last-admin, approval-required, and stale access-review items visually above routine reports.
- Users and invitations lists: use dense enterprise table/search layouts with clear scoped filters, sortable columns, status badges with text, row-level trace links where allowed, and empty/error states that do not leak cross-tenant existence.
- User detail/edit and invite form: use layered detail/form panels with visible authority context, destructive-action separation, last-admin protection messaging, idempotent submit feedback, and decision/system-message cards for approval-required or denied changes.
- Access review: present high-risk memberships as attention cards or grouped review rows with severity, evidence, reviewer state, and trace links; keep the layout grounded in review workflow and authorization evidence.
- Admin audit summary: render as an audit timeline with timestamps, actor, scope, capability/action, authorization basis, and drill-down links.
- System-message surfaces: use typed cards for denial, validation, stale/reconnect, success, and approval-required states with semantic icon/color plus text, recovery actions, and trace/request-approval affordances when allowed.

## Capability inventory and exposure channels

A capability is the governed backend contract. It may be exposed through one or more channels: surface action, browser API, workstream-agent tool, internal-agent tool, workflow step, timer, consumer, MCP tool, view, or internal method. Browser APIs and agent tools are exposure forms over the same capability; they do not redefine authorization, validation, idempotency, side effects, audit, approval, or denial behavior.

For this workstream, the same invitation capability can support both classic surface submission and conversational operation:

```text
invite-user form submit
→ surface action / browser API
→ user_admin.invitations.create
→ InvitationWorkflow + result surface/system_message

“create an invite for jane.doe@gmail.com”
→ User Admin workstream agent
→ required-field collection / confirmation / approval if needed
→ workstream-agent tool exposure of user_admin.invitations.create or proposal variant
→ InvitationWorkflow + result surface/system_message
```

Read/evidence capabilities may be exposed as workstream-agent tools for search, user detail, roles, invitations, and audit summaries. Side-effecting capabilities default to explicit surface action, confirmation, proposal, or approval flow unless a bounded autonomous policy explicitly allows tool execution.

| Capability id | Class | Purpose | Side effects |
|---|---|---|---|
| `user_admin.dashboard.view` | read/evidence | dashboard summary | read audit if PII/risk details included |
| `user_admin.users.search` | read/evidence | scoped users/invitations search | sensitive-read audit |
| `user_admin.users.view` | read/evidence | user detail/edit payload | sensitive-read audit |
| `user_admin.profile.update` | command | update allowed profile fields | profile update, audit |
| `user_admin.memberships.change_status` | command/approval | suspend/reactivate/remove membership | membership update, session invalidation by authz, audit |
| `user_admin.roles.assign` | command/approval | assign roles/capabilities within authority | role update, audit, approval if privileged |
| `user_admin.invitations.form` | read/evidence | invite form metadata | none/read trace |
| `user_admin.invitations.create` | workflow | create invitation and send email | InvitationWorkflow, Resend/outbox, expiry timer, audit |
| `user_admin.invitations.search` | read/evidence | list invitations | read audit optional |
| `user_admin.invitations.resend` | command/workflow | resend pending invitation | email send/outbox, audit |
| `user_admin.invitations.revoke` | command | revoke pending invitation | invitation status update, audit |
| `user_admin.accounts.disable` | command/approval | disable account/app access | account disabled, audit, session invalidation by authz |
| `user_admin.accounts.reactivate` | command/approval | reactivate disabled account | account active, audit |
| `user_admin.roles.list` | read/evidence | role/capability catalog | read trace |
| `user_admin.access_review.view` | read/evidence | access-risk review | read audit |
| `user_admin.access_review.mark_reviewed` | command | mark review item | audit |
| `user_admin.audit.summary` | trace/audit | recent admin audit | sensitive-read audit |

## Authorization and policy

- Tenant/customer scope required for every read and write.
- Customer admins cannot grant tenant-wide roles unless explicitly authorized.
- Caller cannot grant capabilities they do not have unless approved by higher authority policy.
- Last-admin removal/disable is blocked or approval-gated according to policy; default is block.
- Admin role assignment, admin disable, bulk change, support-access change, and high-risk changes require approval or decision-card.
- Denials must avoid confirming cross-tenant account existence.
- Invitation email uses Resend in production and captured outbox in local/dev/test.

## Workstream-agent prompt requirements

`workstream-agent/prompt.md` must define the agent as the user administration assistant. It must:

- guide user search, invitation, roles, membership, disable/reactivate, and access review tasks;
- interpret shorthand surface requests;
- explain role/capability denials and last-admin protection;
- draft invitation messages and role-change rationales;
- recommend least-privilege roles;
- never commit side effects without explicit surface action;
- never authorize by email alone;
- emit typed system-message surfaces for denial, validation, stale, and success states.

Runtime skills should cover user admin overview, user search/list, user detail/edit, invitations, roles/capabilities, access review, and denial recovery.

## Akka realization candidates

- ESE: `InvitationEntity`; optional approval/role-change request entity.
- KVE: `AccountEntity`, `UserProfileEntity`, `MembershipEntity`, `RoleCapabilityRegistryEntity`.
- Workflow: `InvitationWorkflow`, privileged access-change approval workflow.
- Views: `UserDirectoryView`, `InvitationView`, `MembershipView`, `AccessReviewQueueView`, `AdminAuditView`.
- Consumer: email delivery/outbox, audit projection, access-review projection.
- Timed Action: invitation expiry/reminders, stale access review reminders.
- Agent: `UserAdminAgent` with read/proposal tools; side-effecting tools only via approval/explicit action.
- HTTP: `/api/user-admin/**` surface payload/action endpoints.

## Tests

Required:

- dashboard/list/detail/invite/access-review/admin-audit surface rendering states;
- search users by name/email/status/role with scoped redaction;
- open user from row-click invokes `user_admin.users.view`;
- invite/resend/revoke lifecycle with outbox and idempotency;
- profile update, membership status, role assignment, disable/reactivate success within authority;
- last-admin protection;
- forbidden cross-tenant user search/detail/action;
- customer admin denied tenant-wide role assignment;
- disabled caller denied protected actions;
- stale version conflict returns system-message/result surface;
- audit emitted for reads/actions/denials;
- workstream agent answers how-to and converts shorthand to surfaces.

## Not ready if

- dashboard/list/detail are static mockups;
- row click or dashboard buttons are frontend-only navigation without backend capability calls;
- invitations do not use Resend/outbox lifecycle;
- role assignment lacks caller-authority checks;
- last-admin protection is missing;
- audit/tenant-isolation tests are missing.
