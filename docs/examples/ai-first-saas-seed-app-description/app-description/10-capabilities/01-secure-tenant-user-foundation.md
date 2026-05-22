# Capability: Secure Tenant and User Foundation

This is a reference capability contract for the skills pack's seed app, not this repository's own business application. It demonstrates the expected app-description capability shape for generated secure AI-first SaaS apps.

## Capability definition

- capability-id: `secure-tenant-user-foundation`
- class:
  - command
  - read/evidence
  - workflow
  - scheduled
  - trace/audit
- purpose:
  - provide the required SaaS foundation for human access, tenant/customer isolation, memberships, roles, invitations, permissions, support access, and admin auditability before app-specific features are generated
- business outcome:
  - every protected user, admin, agent, workflow, timer, consumer, route, stream, and view operation has an authenticated account, selected authorization context, enforceable permission boundary, and auditable tenant/customer scope

## In-scope outcomes

- Account, UserProfile, UserSettings, Tenant/Customer, Membership, Role, Permission/Capability, Invitation, AuthContext, and AdminAuditEvent semantics.
- `/api/me` returns browser-safe account, membership, selected context, profile/settings, and capability information.
- Complete invitation lifecycle: create, send, resend, revoke, expire, accept, delivery status, delivery attempts, and audit.
- User directory list/search and user detail within authorized tenant/customer scope.
- Role assignment, replacement, removal, permission checks, and access-review queues.
- Membership add, suspend, reactivate, and remove lifecycle.
- Account disable/reactivate and reset/relink identity subject under policy.
- Support-access grant, revoke, expiry, and review.
- Admin audit/search over identity, membership, role, invitation, support-access, and authorization events.
- AI-assisted admin offload with governed admin responsibilities that may be implemented by a single `UserAdminAgent` with access-review, admin-risk-scoring, invitation-drafting, role-recommendation, support-access-review, and audit-summary skills, or by separate AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, and AdminAuditSummaryAgent classes when useful.
- Governed runtime agent foundation for admin assistants: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, and authorized `readSkill(skillId)`.
- Tenant/customer context switching for users with multiple memberships.
- Tenant/customer-scoped settings and browser navigation/action availability.

## Out-of-scope outcomes

- Billing/subscription management in v1; only the subscription/billing boundary is modeled.
- Enterprise identity-provider configuration UI in v1.
- Cross-tenant impersonation without an explicit support-access grant and audit trail.
- Prompt-only, frontend-only, or hidden-field authorization.
- Raw state dumps to agents or browser clients when a scoped/redacted evidence response is required.

## Actors and callers

- SaaS Owner Admin.
- Tenant Admin.
- Customer Admin.
- Auditor.
- Tenant/customer member.
- Invited user.
- Support operator with time-bound support-access grant.
- AI admin assistant supervisor.
- Scoped admin-assistant agents or skilled `UserAdminAgent` responsibilities for access review, invitation drafting, role recommendations, support-access review, and audit summarization.
- AgentBehaviorEditorAgent for governed prompt, skill, manifest, and tool-boundary change proposals.
- InvitationWorkflow, expiry/reminder TimedAction, Resend email/outbox Consumer, and admin views as internal callers.

## Authority and contract

- AuthContext / scope:
  - every protected operation requires an authenticated account, selected tenant/customer context when tenant/customer scoped, active membership unless the flow is invite acceptance or SaaS-owner support access, and active user/account status
  - tenant/customer ids in commands and queries must match the selected context or an explicit SaaS Owner/support-access authority path
- permissions / named capability grants:
  - `tenant.user.read`, `tenant.user.manage`, `tenant.role.manage`, `tenant.invitation.manage`, `tenant.audit.read`, `tenant.support_access.manage`, `saas_owner.tenant.read`, `saas_owner.support_access.grant`
  - named capability grants mirror these permissions for UI action gating and agent-tool allow lists
- inputs / validation / idempotency:
  - commands include `tenantId` or `customerId` where scoped, target account/member/invitation ids, actor account id, idempotency key, and correlation id
  - invitation email addresses are normalized; duplicate active invitations are idempotent no-ops or safe updates according to invite policy
  - role and membership changes reject disabled actors, disabled targets, missing authority, cross-tenant ids, and unknown roles
- outputs / redaction / denial shape:
  - browser and agent responses expose only display names, normalized emails, membership status, roles, browser-safe permissions/capabilities, invitation status, and audit summaries
  - sensitive identity-provider tokens, secrets, raw JWTs, and unrelated tenant/customer data are never returned
  - denials use a stable forbidden/not-found shape that does not reveal cross-tenant resource existence
- data access:
  - Account, UserProfile, UserSettings, Tenant/Customer, Membership, Role, Permission/Capability, Invitation, SupportAccessGrant, AdminAuditEvent
  - AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace
  - UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView
  - all reads and writes are filtered by tenant/customer scope unless explicitly SaaS-owner scoped
- side effects:
  - state changes for invitations, memberships, roles, support-access grants, selected context, profile/settings, and account status
  - Resend email/outbox records for invitations, reminders, and future app feature emails
  - timers for invitation expiry, reminder, support-access expiry, and access-review cadence
  - AdminAuditEvent and work-trace records for protected reads, writes, denials, approvals, support access, and consequential AI/tool activity
  - PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace records for runtime agent prompt assembly, authorized or denied readSkill requests, and consequential agent work
- exposure surfaces:
  - browser UI actions for sign-in shell, context selection, profile/settings, admin users, invitations, roles/memberships, access review, support access, and admin audit
  - HTTP APIs including `/api/me` and protected admin routes
  - workflow surface for invitation acceptance and delivery lifecycle
  - timer surface for expiry/reminders
  - consumer surface for Resend email delivery/outbox
  - view/query surfaces for user directory, memberships, invitations, access reviews, and audit
  - scoped agent tools for read/evidence, draft, summarize, and recommend operations only; committing risky admin actions remains human-approved unless a policy grants a narrow autonomous path
  - managed-agent UI and API surfaces for agent catalog/detail, prompt/skill governance, manifest management, tool permission boundaries, editing-agent proposal review, and trace review

## User Admin named capability matrix

The three canonical User Admin surfaces expose the following governed backend capabilities. UI action availability, agent tool descriptions, and route names are hints only; every capability recomputes authorization from the selected `AuthContext`, target scope, actor status, membership status, role/capability grant, support-access grant when applicable, idempotency key, and correlation id.

| Capability id | Class | Primary surfaces | Allowed actors/callers | Required authorization and denial semantics |
|---|---|---|---|---|
| `admin.users.dashboard.read` | read/evidence | `user-admin-dashboard` | SaaS Owner Admin/Auditor for SaaS-owner scope, Tenant Admin/Auditor for selected Tenant, Customer Admin/Auditor for selected Customer, support operator only through active support-access context, `UserAdminAgent` read-only tool | Return scoped counts/queues only. Deny disabled actors, inactive memberships, missing read capability, cross-tenant/customer scope, and SaaS Owner Tenant-data reads without Tenant-created support access. Customer Admin receives no Tenant-level queues. |
| `admin.users.search` | read/evidence | `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account` return-to-list | SaaS Owner Admin/Auditor for platform-safe users, Tenant Admin/Auditor, Customer Admin/Auditor, support operator with active support access, `UserAdminAgent` read-only tool | Search after scope filtering and before pagination. Hide or forbid cross-scope matches without existence leaks. Customer Admin cannot search Tenant employees or Tenant-wide roles. |
| `admin.users.detail.read` | read/evidence | `user-admin-user-list`, `user-admin-user-account` | Same as `admin.users.search` with sensitive-detail audit policy | Load detail through scoped views, not trusted caller-known ids. Redact out-of-scope memberships, provider internals, private support evidence, and audit details outside actor authority. |
| `admin.users.profile.patch` | command | `user-admin-user-account` | Tenant Admin or Customer Admin only when policy allows admin-visible fields; support operator only if grant includes profile-maintenance scope | Patch display/profile fields only; never grants authority. Deny unsupported fields, disabled actor/target where policy forbids, cross-scope target, and missing manage capability. |
| `admin.users.disable` / `admin.users.reactivate` | command/approval | `user-admin-user-list`, `user-admin-user-account` | Tenant Admin for Tenant/Customer users, Customer Admin for Customer users, approved decision-card handler; SaaS Owner Admin for SaaS-owner/platform-safe users only | Require reason, idempotency key, audit, and last-admin check. Deny role escalation, disabled actor, cross-tenant/customer target, Customer Admin Tenant action, SaaS Owner Tenant-data action without support access, and any last-admin loss. |
| `admin.users.identity_relink.request` / `admin.users.identity_relink.complete` | proposal/approval command | `user-admin-user-account`, `decision-card` | Tenant Admin or Customer Admin with explicit policy/evidence; approved decision-card handler completes | Request creates decision-card evidence; complete requires approval. Deny missing policy/evidence, privilege expansion, cross-scope target, Customer Admin Tenant action, and unauthorized support access. |
| `admin.invitations.create` | command/workflow | `user-admin-dashboard`, `user-admin-user-list` | Tenant Admin for Tenant/Customer invitations, Customer Admin for Customer invitations, approved workflow/decision-card handler | Normalize email, require target scope, roles, reason when policy requires, idempotency key, and invitation permission. Deny duplicate active invite according to policy as no-op/conflict, role escalation, Customer Admin Tenant invite, cross-tenant/customer target, and disabled actor. |
| `admin.invitations.resend` / `admin.invitations.revoke` | command/workflow | all three User Admin surfaces | Tenant Admin, Customer Admin, support operator only when grant includes invitation support, approved workflow | Require scoped invitation id and no raw token exposure. Resend is idempotent and audited; revoke denies accepted/expired states when policy disallows. Deny cross-scope invitation, missing invitation capability, disabled actor, and SaaS Owner no support access. |
| `admin.memberships.add` | command | `user-admin-user-list`, `user-admin-user-account` | Tenant Admin for Tenant/Customer memberships, Customer Admin for Customer memberships, approved decision-card handler | Require target account, scope, roles, reason, idempotency key, and manage capability. Deny Customer Admin Tenant action, role escalation, unknown roles, cross-tenant/customer target, and disabled actor. |
| `admin.memberships.suspend` / `admin.memberships.reactivate` / `admin.memberships.remove` | command/approval | `user-admin-user-list`, `user-admin-user-account` | Tenant Admin, Customer Admin within Customer scope, approved decision-card handler | Require lifecycle-valid target, reason, idempotency key, audit, and last-admin check. Deny inactive/removed transitions, cross-scope target, missing manage capability, Customer Admin Tenant action, support-access policy violation, and last-admin loss. |
| `admin.roles.replace` / `admin.roles.remove` | command/approval | `user-admin-user-list`, `user-admin-user-account` | Tenant Admin for Tenant/Customer roles, Customer Admin for Customer roles, approved decision-card handler | Require membership id, requested roles/removal, reason, idempotency key, role-management capability, and last-admin check. Deny role escalation above actor authority, assigning SaaS Owner roles from Tenant/Customer contexts, Customer Admin Tenant roles, unknown roles, and last-admin loss. |
| `admin.support_access.read` | read/evidence | `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account` | Tenant Admin/Auditor, SaaS Owner support operator only through active grant, support-access review agent read-only | Return only grants in selected Tenant/Customer scope and redact sensitive evidence. Deny SaaS Owner Tenant-data reads without active Tenant-created support-access membership. Customer Admin has no Tenant-level support administration visibility unless explicitly delegated by Tenant policy. |
| `admin.support_access.grant` / `admin.support_access.revoke` / `admin.support_access.extend` | command/approval | `user-admin-user-list`, `user-admin-user-account`, `decision-card` | Tenant Admin with `tenant.support_access.manage`; approved decision-card handler for high-risk grant/extend | Require support actor, purpose, scope limit, expiry, reason, idempotency key, audit, and policy/approval for grant or extension. Deny Customer Admin support-access administration, SaaS Owner self-grant, missing expiry/reason, role escalation, cross-tenant target, and last-admin/support-policy violations. |
| `admin.access_review.read` | read/evidence | all three User Admin surfaces | Tenant Admin/Auditor, Customer Admin/Auditor in Customer scope, support operator with active grant, `UserAdminAgent` read-only tool | Return scoped review items for stale invites, dormant admins, support expiry, risky roles, orphaned Customer admin gaps, and last-admin risks. Deny cross-scope, missing read capability, disabled actor, and unauthorized support access. |
| `admin.access_review.resolve` | command/approval | `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`, `decision-card` | Tenant Admin, Customer Admin for Customer-scope items, approved decision-card handler; agent may draft recommendation only | Require resolution outcome, reason, idempotency key, trace links, and policy check. High-risk items route to decision-card. Deny Customer Admin resolving Tenant-level item, missing capability, role escalation, support-access expansion, and last-admin loss. |
| `admin.audit.read` | trace/audit read | all three User Admin surfaces, `audit-trace-explorer` | SaaS Owner/Tenant/Customer Auditor, corresponding Admin roles with audit capability, support operator only for grant-scoped evidence, `UserAdminAgent` summary tool | Return scoped/redacted audit summaries and trace links. Deny cross-tenant/customer audit, SaaS Owner Tenant audit without support access, disabled actor, and missing audit capability; do not reveal hidden resource existence through audit filters. |

### Actor variant rules for User Admin capabilities

- **SaaS Owner Admin**: may manage SaaS-owner users, platform-safe tenant metadata, and bootstrap-safe tenant admin records. SaaS Owner authority alone does not allow Tenant/Customer user detail, membership, support-evidence, or audit reads; those require an active Tenant-created support-access membership and are then evaluated as that support context.
- **Tenant Admin**: may read/manage Tenant employees, Customer users under the Tenant, Tenant-created invitations, Tenant/Customer memberships and roles within its authority, Tenant support-access grants, Tenant access-review queues, and Tenant-scoped admin audit.
- **Customer Admin**: may read/manage only selected Customer users, Customer memberships/roles/invitations, Customer access-review items, and Customer audit excerpts. Tenant employee management, Tenant roles, Tenant support-access administration, Tenant-wide audit, and SaaS-owner roles are forbidden.
- **Auditor**: has read-only scoped `admin.users.*.read`, `admin.access_review.read`, and `admin.audit.read` semantics according to its SaaS Owner/Tenant/Customer scope; mutation capabilities are denied.
- **Support-access actor**: may use only the Tenant/Customer scope, expiry window, reason, roles/capabilities, and evidence boundaries of the active support grant; grant use is audited and cannot expand itself.

### Required User Admin denial behavior

All User Admin capability denials use the stable forbidden/hidden-not-found response shape described above and emit an `AdminAuditEvent`/work-trace record when policy requires. Required denial categories:

- `cross_scope`: target tenant/customer/scope does not match selected `AuthContext`; response must not leak resource existence.
- `customer_admin_tenant_action`: Customer Admin attempts Tenant employee, Tenant role, Tenant support-access, Tenant settings, or Tenant-wide audit action.
- `saas_owner_no_support_access`: SaaS Owner attempts Tenant/Customer user data, support evidence, or Tenant audit access without an active Tenant-created support-access membership.
- `disabled_actor`: authenticated actor account is disabled or selected membership is inactive/suspended/removed.
- `missing_capability`: actor lacks the named capability or role/scope grant required for the operation.
- `role_escalation`: requested role/capability exceeds actor authority, assigns SaaS Owner authority from Tenant/Customer context, or expands support access beyond grant/policy.
- `last_admin_loss`: role removal/replacement, membership suspend/remove, account disable/remove, or support-access change would leave no active Tenant/Customer admin where policy requires one.

## Policy, approval, and autonomy

- Default autonomy level:
  - human-approved for role changes, account disable/reactivate, support-access grants, and policy-impacting administrative changes
  - bounded agent recommendation for access review, role recommendation, invitation text drafting, support-access risk summary, and audit summary
  - autonomous only for low-risk scheduled expiry/reminder actions and idempotent delivery retries
- Approval gates:
  - support-access grant or extension requires SaaS Owner Admin approval and expiry
  - high-risk role escalation requires Tenant/Customer Admin approval and audit reason
  - account disable/reactivate requires admin reason and audit event
- Escalation:
  - unclear authority, conflicting memberships, unusual access patterns, or agent low-confidence recommendations produce decision cards for human review

## Audit and trace requirements

- Audit events:
  - identity sign-in/link events, selected-context changes, membership/role/invitation/support-access lifecycle changes, protected admin reads, denials, approvals, AI recommendations, and tool/data-access activity
- Work-trace fields:
  - actor account id, selected AuthContext, tenant/customer id, target resource id, permission/capability checked, decision outcome, policy citation, correlation id, idempotency key, agent id when applicable, and redaction marker
- Retention/redaction:
  - audit records retain administrative accountability while redacting secrets, JWTs, raw model prompts containing sensitive data, and unrelated tenant/customer details

## Required tests

- success:
  - authorized admin can invite a user, assign a role, list directory entries, view audit summaries, and select a valid tenant/customer context
- validation:
  - malformed emails, unknown roles, missing target ids, invalid idempotency keys, and unsupported context switches are rejected safely
- forbidden and tenant isolation:
  - wrong tenant/customer, missing membership, disabled account, missing role/scope, expired support access, and unauthorized agent tool calls are denied without leaking resource existence
- idempotency:
  - duplicate invitation create/resend and retrying delivery/outbox operations are safe and auditable
- approval:
  - support access, risky role escalation, account disable/reactivate, and agent-suggested role changes require human approval unless an explicit accepted policy grants a narrow autonomous path
- audit/trace:
  - success, denial, protected reads, approvals, support-access use, and consequential AI/tool activity create AdminAuditEvent/work-trace records
- surface-specific:
  - `/api/me` returns browser-safe capabilities only; UI hides unavailable actions but backend still denies; agent tools receive only scoped/redacted evidence; timers and consumers are retry-safe
  - disabled agents, unassigned skill reads, unauthorized prompt/skill/tool changes, and approval-required authority expansion are denied and traced

## Linked layers

- operating model:
  - `../15-operating-model/agent-roles-and-authority.md`
  - `../15-operating-model/policies-and-approval-gates.md`
  - `../15-operating-model/decisions-exceptions-and-evidence.md`
  - `../15-operating-model/audit-trace-and-outcomes.md`
- behavior:
  - `../20-behavior/state-models/01-tenant-user-access-model.md`
  - `../20-behavior/flows/01-onboarding-and-access-flow.md`
  - `../20-behavior/rules/01-tenant-authz-rules.md`
- tests:
  - `../30-tests/acceptance/01-seed-app-acceptance.md`
  - `../30-tests/negative/01-forbidden-actions.md`
  - `../30-tests/regression/01-tenant-isolation-and-idempotency.md`
  - `../30-tests/operational/01-observability-and-audit.md`
- auth/security:
  - `../40-auth-security/identity-and-trust.md`
  - `../40-auth-security/authorization-rules.md`
  - `../40-auth-security/boundary-and-surface-rules.md`
  - `../40-auth-security/data-protection.md`
- observability:
  - `../50-observability/logs-and-audit.md`
  - `../50-observability/traces-and-correlation.md`
  - `../50-observability/metrics.md`
- workstreams/UI:
  - `../12-workstreams/functional-agents.md`
  - `../12-workstreams/surfaces-index.md`
  - `../55-ui/workstream-shell.md`
  - `../55-ui/frontend-api-contracts.md`
  - `../55-ui/ai-first-surfaces.md`
- traceability:
  - `../70-traceability/capability-to-behavior-map.md`
  - `../70-traceability/operating-model-to-behavior-map.md`
