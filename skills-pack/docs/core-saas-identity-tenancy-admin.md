# Core SaaS identity, tenancy, and administration

Use this doc to implement the product-agnostic identity and administration foundation for AI-first SaaS applications.

Related docs:
- `./core-ai-first-saas-foundation.md`
- `./core-saas-owner-tenant-billing.md`
- `./security-workos-auth-and-admin.md`

## Design intent

The core model supports a three-level SaaS business structure:

```text
SaaS Owner -> Tenant -> Customer
```

A **Tenant** is the SaaS user organization. A **Customer** is always modeled initially as an organization served by a Tenant. Specific generated applications may later adapt this model for individual consumers, but the SaaS Foundation App should start with organization customers.

## Identity and authorization model

WorkOS authenticates the human. The Akka backend owns local authorization state.

```text
WorkOS Identity
  -> Local Account
     -> User Profile
     -> User Settings
     -> Membership(s)
        -> Scope: SaaS Owner | Tenant | Customer
        -> Role(s) within that scope
```

Rules:
- use WorkOS subject/email for authentication and account linking;
- never rely on frontend-only role checks;
- do not rely on email alone for authorization;
- allow the same human/email to hold accounts or memberships at multiple levels;
- require explicit context selection when a signed-in user has more than one active membership;
- include scope identifiers in every protected command/query: `tenantId`, `customerId`, or platform owner context as applicable;
- keep profile and settings state separate from authorization state; profile/settings changes must not change roles, scopes, or access.

## Account and membership concepts

### Local Account

A local account represents a human identity known to the application.

Recommended fields:
- `accountId`
- `workosUserId`, nullable until linked
- `email`
- `displayName`
- `status`: `INVITED`, `ACTIVE`, `DISABLED`, `REMOVED`
- `createdAt`, `activatedAt`, `disabledAt`
- audit metadata: created by, reason, source

### User Profile

A user profile stores human-facing attributes that the application can display or let the user edit. It is not the source of authentication or authorization.

Base viewable fields:
- `accountId`
- `email`, sourced from WorkOS/local account and usually read-only in the app
- `displayName`
- `givenName`, optional
- `familyName`, optional
- `avatarUrl`, optional
- selected or active membership/context summary when relevant

Base editable fields:
- `displayName`
- `givenName`
- `familyName`
- `avatarUrl`, when the app supports user-managed avatars

Profile extension rules:
- app-specific implementations may add fields, such as title, department, phone, locale, timezone, expertise, notification contact preferences, or onboarding attributes;
- tenant-scoped or customer-scoped profile extensions must include the relevant `tenantId` and/or `customerId` and be visible only to authorized users in that scope;
- profile fields must not be used as permission grants; use memberships and roles for authorization;
- changes to sensitive or organization-visible profile fields should be auditable when required by the app.

### User Settings

User settings store preferences that affect application behavior or presentation for a signed-in human. Settings are extensible by generated application requirements.

Base viewable and editable fields:
- `accountId`
- `preferredThemeId`: one of the available named theme ids from the app UI style guide, initially `aurora-light`, `cobalt-light`, `obsidian-dark`, `midnight-dark`, or `dark-night`

Initial rule:
- support named-theme selection first; users choose an available theme by name and the app stores/applies the selected theme id without treating theme choice as authorization, policy, audit, or capability state.

Settings extension rules:
- app-specific implementations may add settings such as notification preferences, default landing page, digest cadence, table density, locale, timezone, or AI-assistance preferences;
- settings may be account-wide by default, or scoped to a membership/context when the same human needs different preferences for SaaS Owner, Tenant, or Customer work;
- settings must not override backend authorization, policy gates, retention rules, or audit requirements.

### Membership

A membership grants a local account permissions in one scope.

Recommended fields:
- `membershipId`
- `accountId`
- `scopeType`: `SAAS_OWNER`, `TENANT`, `CUSTOMER`
- `tenantId`, required for Tenant and Customer scopes
- `customerId`, required for Customer scopes
- `roles`
- `status`: `INVITED`, `ACTIVE`, `SUSPENDED`, `REMOVED`
- `expiresAt`, optional, especially for support-access accounts
- audit metadata: granted by, approved by, reason, policy references

Membership lifecycle requirements:
- administrators can add, suspend membership, reactivate membership, and remove membership only within their authority boundary;
- role changes must support assign, replace, and remove roles, not only append roles;
- last-admin protection must reject actions that would leave a Tenant or Customer with no active admin;
- every lifecycle command must be idempotent or return a documented conflict and emit AdminAuditEvent facts.

### Invitation

Complete email-invite onboarding is mandatory for generated SaaS foundations.

Recommended fields:
- `invitationId`
- normalized target `email`
- target scope: SaaS Owner, Tenant, or Customer plus `tenantId`/`customerId` where required
- requested roles/capabilities and membership policy
- invite token hash or acceptance context identifier; never expose raw tokens in views or logs
- `status`: `PENDING_DELIVERY`, `SENT`, `DELIVERY_FAILED`, `ACCEPTED`, `EXPIRED`, `REVOKED`
- `expiresAt`, `acceptedAt`, `revokedAt`
- delivery provider/message ids, `deliveryStatus`, `deliveryAttempts`, last delivery error, and captured outbox id for local/dev/test
- resend count and idempotency key for duplicate invite handling
- audit metadata: invited by, resent by, revoked by, accepted by, reason, policy references

Lifecycle rules:
- invite creation must create local authorization intent before first sign-in;
- production email delivery uses Resend (resend.com), the supported production email service in this pack;
- local/dev/test may use an explicit safe email adapter that captures messages in an outbox without external delivery;
- failed delivery remains visible to authorized admins and creates an audit event;
- resend must reuse or rotate acceptance context according to policy and remain idempotent;
- revoke/cancel and expiry must prevent acceptance and be auditable;
- acceptance must be idempotent and must not activate a membership unless the invitation and membership policy are valid.

## Roles

Canonical foundation roles:

| Role | Scope | Purpose |
|---|---|---|
| `SAAS_OWNER_ADMIN` | SaaS Owner | Manage SaaS Owner users, Tenants, Tenant Admin bootstrap, and SaaS Owner to Tenant subscription/billing state. |
| `TENANT_ADMIN` | Tenant | Manage Tenant users, Customer organizations, Customer Admins, support-access grants, and Tenant-owned configuration/data within the Tenant boundary. |
| `TENANT_EMPLOYEE` | Tenant | Use Tenant-owned application functionality according to app-specific permissions. |
| `CUSTOMER_ADMIN` | Customer | Manage Customer users and supervise Customer-side service use. |
| `CUSTOMER_USER` | Customer | Use Customer-facing online services provided by the Tenant. |
| `AUDITOR` | SaaS Owner, Tenant, or Customer | Read scoped admin audit/search and access-review surfaces without mutation rights. |

App-specific roles are extensions mapped to permissions/capabilities inside these scopes. They may express domain authority such as supervisor, policy owner, dealer owner, reviewer, or operator, but they must not replace the foundation roles, membership status checks, scope checks, or support-access rules.

Do not use `APP_ADMIN` as the preferred generic platform role; use `SAAS_OWNER_ADMIN` for SaaS Owner administration. Existing examples that mention `APP_ADMIN`, dealer roles, or policy roles are app-specific aliases or reference roles and must be documented as such. Do not use a global super-admin role for Tenant data access. SaaS Owner permissions are platform-operations permissions only.

## Tenant management capabilities

Tenant administration is a foundation capability set, not an app-specific business-domain feature. Keep these authority boundaries explicit:

- `SAAS_OWNER_ADMIN` may create Tenant organizations, maintain billing-safe tenant metadata and tenant lifecycle/status, and bootstrap one or more Tenant Admins through the mandatory Invitation lifecycle.
- `TENANT_ADMIN` may maintain only their own Tenant's allowed profile/settings, invite and manage Tenant Admins and Tenant employees within that Tenant, create/manage Customer organizations under that Tenant, and grant/revoke scoped support access.
- `TENANT_ADMIN` must not create sibling Tenants, assign SaaS Owner roles, or manage users outside the selected Tenant context.
- SaaS Owner users must not access Tenant application data by virtue of SaaS Owner authority; support requires a separate Tenant-scoped membership granted by a Tenant Admin.

Recommended Tenant fields:
- `tenantId`
- `displayName`, `legalName`, optional slug or public label
- `status`: `DRAFT`, `ONBOARDING`, `ACTIVE`, `SUSPENDED`, `CLOSED`
- billing-safe contact or subscription references when the core billing foundation is enabled
- operational metadata such as created/activated/suspended/closed timestamps
- audit metadata: created by, status changed by, reason, policy references

Baseline governed capabilities:
- `tenant:create`
- `tenant:view`
- `tenant:list`
- `tenant:update_profile`
- `tenant:update_status`
- `tenant_admin:invite`
- `tenant_admin:list`
- `tenant_admin:replace_roles`
- `tenant_admin:suspend_membership`
- `tenant_admin:reactivate_membership`
- `tenant_admin:remove_membership`
- `tenant_admin:last_admin_protection_check`

Capability rules:
- tenant creation and status changes require `SAAS_OWNER_ADMIN` and emit `AdminAuditEvent` facts;
- Tenant Admin invitation by a SaaS Owner creates an invited local account if needed, a Tenant-scoped `TENANT_ADMIN` membership intent, and an Invitation with delivery/expiry/revoke/audit state;
- Tenant Admin maintenance by a Tenant Admin is limited to the caller's selected Tenant context and cannot grant SaaS Owner roles;
- role and membership changes must be idempotent where possible and must run last-admin protection before removing, suspending, disabling, or role-changing the final active Tenant Admin;
- Tenant status and billing/subscription state remain separate concepts, even when status changes are driven by billing decisions.

Typical Tenant Administration APIs:

```text
GET    /api/tenants
POST   /api/tenants
GET    /api/tenants/{tenantId}
PATCH  /api/tenants/{tenantId}
POST   /api/tenants/{tenantId}/activate
POST   /api/tenants/{tenantId}/suspend
POST   /api/tenants/{tenantId}/close
GET    /api/tenants/{tenantId}/admins
POST   /api/tenants/{tenantId}/admins/invite
PUT    /api/tenants/{tenantId}/admins/{accountId}/roles
POST   /api/tenants/{tenantId}/admins/{accountId}/suspend
POST   /api/tenants/{tenantId}/admins/{accountId}/reactivate
DELETE /api/tenants/{tenantId}/admins/{accountId}
```

These endpoints are browser-facing capability surfaces only. Every endpoint must validate JWT identity, resolve local account and selected `AuthContext`, authorize through local Membership/Role/Capability state, scope all queries by authority, return browser-safe DTOs, and emit audit for consequential changes.

## Administration read models

Admins must be able to discover and repair access state without already knowing internal user IDs. Generated SaaS foundations should include these scoped views from the first admin slice:

| View | Purpose |
|---|---|
| `TenantDirectoryView` | SaaS Owner Tenant list/search by platform-safe metadata, tenant status, billing-safe subscription indicators, onboarding state, and bootstrap Tenant Admin status. |
| `TenantAdminView` | Tenant admin list/search by tenant, account, role, membership status, invitation state, last-admin risk, and support-access relationship. |
| `UserDirectoryView` | List/search users by authorized SaaS Owner, Tenant, or Customer scope; filter by email/name, account status, role, membership status, identity link state, and last activity when available. |
| `MembershipView` | List memberships and role summaries by scope, account, status, role, support-access expiry, and last-admin risk. |
| `InvitationView` | List invitations by target email, scope, invitation status, delivery status, expiry, inviter, resend count, and delivery failure. |
| `AdminAuditView` | Search admin audit events by actor, target user, action type, scope, role, membership status, invitation status, support-access grant, policy, and time range. |
| `AccessReviewQueueView` | Queue stale invitations, dormant admins, risky role combinations, support-access nearing expiry, orphaned customers without active admins, and last-admin protection risks. |

View endpoints must constrain queries with the caller's AuthContext and must redact or omit fields outside the caller's scope. Frontend filtering is never the security boundary.

Required first-slice query paths and filters:
- `TenantDirectoryView`: tenant id/name, tenant status, onboarding state, subscription/billing-safe status, bootstrap Tenant Admin status, and lifecycle risk.
- `TenantAdminView`: tenant, account/email/name, admin role, membership status, invitation status, identity link state, last-admin risk, support-access relationship, and lifecycle review status.
- `UserDirectoryView`: tenant, customer, email/name, account status, role, membership status, identity link state, and last activity.
- `MembershipView`: tenant, customer, account, role, membership status, support-access expiry, last-admin risk, and lifecycle review status.
- `InvitationView`: target email, tenant/customer scope, invitation status, delivery status, delivery attempts, inviter, expiry/due time, and resend/revoke eligibility.
- `AdminAuditView`: actor, target user, tenant, customer, role, membership status, invitation status, action type, support-access grant, policy or decision-card link, risk where present, and time range.
- `AccessReviewQueueView`: target user, tenant, customer, role, membership status, invitation status, delivery status, risk, due/expiry time, review status, item type, and agent recommendation source.

Authorization and redaction rules:
- SaaS Owner Admin and Auditor views are limited to platform-safe metadata unless a scoped support-access membership authorizes Tenant data access;
- Tenant Admin views are limited to their Tenant and authorized Customer scopes;
- Customer Admin views are limited to their Customer scope;
- Auditor capabilities allow read-only audit/search and access-review access only within granted scope;
- raw invitation tokens, provider secrets, private WorkOS/provider ids, unrelated tenant/customer data, and sensitive support details must never be returned to unauthorized callers;
- admin/audit/search endpoints must return paginated browser-safe DTOs and must be tested for query authorization, cross-scope filtering, redaction, stale invite/access-review correctness, and audit trace completeness.

## Administration operations

Required baseline operations:
- create Tenant organizations, update billing-safe Tenant profile metadata, and activate/suspend/close Tenant status within SaaS Owner authority;
- bootstrap initial Tenant Admins and maintain Tenant Admin memberships through the mandatory Invitation lifecycle and last-admin protection;
- invite users, resend invite, revoke invite, and view invitation status through the mandatory Invitation lifecycle;
- list Tenants, Tenant Admins, users, search/filter them, and view detail through scoped views rather than requiring caller-supplied internal IDs;
- edit declared profile fields only;
- assign, replace, and remove roles within caller authority;
- add, suspend, reactivate, and remove memberships;
- disable and reactivate accounts;
- reset/relink identity subject under explicit policy and audit;
- grant, revoke, and expire support-access memberships;
- enforce last-admin protection for Tenant and Customer admin scopes;
- expose Admin Audit and Access Review queues to authorized Admins and Auditors.

## Administration UI surfaces

For generated full-stack AI-first SaaS apps, the foundation includes mandatory first-slice User Admin functional-agent surfaces for: Tenant Administration, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, and Tenant/Customer Settings. These surfaces are capability-gated for SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, and app-specific admins; backend endpoints remain authoritative. SaaS Owner Admins need Tenant directory, create Tenant, Tenant detail, Tenant status, bootstrap Tenant Admin, Tenant Admin list, and Tenant audit timeline surfaces. Tenant Admins need own-Tenant settings, Tenant Admins, Tenant users, invitations, access review, last-admin warnings, and support-access surfaces. The UI must let admins discover stale/dormant access, failed or expiring invitations, support-access expiry, last-admin risk, and agent-generated admin recommendations without knowing internal ids upfront.

## Administration flows

### SaaS Owner Admin creates a Tenant and initial Tenant Admin

1. SaaS Owner Admin creates Tenant organization.
2. SaaS Owner Admin assigns subscription/billing state.
3. SaaS Owner Admin invites one or more Tenant Admins.
4. Invited human signs in through WorkOS.
5. Backend validates a non-expired, non-revoked invitation or accepted membership policy, then links WorkOS identity to the local account and activates Tenant membership.
6. Audit trace records Tenant creation, invitation, link, activation, and subscription state.

### Tenant Admin manages Tenant employees

1. Tenant Admin invites employee by email.
2. Backend creates local account if needed, Tenant membership in `INVITED` state, and an Invitation with expiry, delivery status, and audit trail.
3. Backend sends or captures invite email through the configured delivery adapter.
4. Employee signs in through WorkOS and links/activates membership only through a valid invitation or membership policy.
5. Tenant Admin can list/search users, view user detail, edit allowed profile fields, assign/replace/remove roles, suspend membership, reactivate membership, remove membership, disable/reactivate accounts, and run access review actions within Tenant scope.
6. Last-admin protection rejects any change that would leave the Tenant without an active Tenant Admin.
7. All changes are Tenant-scoped and auditable.

### Tenant Admin creates Customer organization and Customer Admin

1. Tenant Admin creates Customer organization under Tenant.
2. Tenant Admin invites Customer Admin through the mandatory Invitation lifecycle.
3. Customer Admin signs in through WorkOS and activates Customer membership only through a valid invitation or membership policy.
4. Customer Admin can then manage Customer users.

### Customer Admin manages Customer users

1. Customer Admin invites Customer users within the Customer organization.
2. Backend creates local account if needed, Customer membership in `INVITED` state, and an Invitation with expiry, delivery status, delivery attempts, and audit trail.
3. Backend sends or captures invite email through the configured delivery adapter.
4. Customer user signs in through WorkOS and activates membership only through a valid invitation or membership policy.
5. Customer Admin may suspend or remove Customer users.

## SaaS Owner support access rule

SaaS Owner users have no direct access to Tenant application data.

If a Tenant wants SaaS Owner personnel to assist with Tenant-owned data or configuration, the Tenant Admin must create a normal Tenant-scoped account or membership for that person.

Support-access membership requirements:
- created by a Tenant Admin, not by SaaS Owner privilege;
- scoped to the Tenant and optionally narrowed to app-specific permissions;
- assigned to a real WorkOS-authenticated human/email;
- time-limited by default;
- includes reason/purpose;
- visible to Tenant Admins;
- fully audited;
- revocable by Tenant Admins;
- eligible for AI-first risk review and expiry reminders.

This pattern permits the same email to be both a SaaS Owner Admin and a Tenant support user, but those are separate authorization contexts.

## Tenant and Customer data isolation

Backend rules:
- every Tenant-scoped record includes `tenantId`;
- every Customer-scoped record includes both `tenantId` and `customerId`;
- all queries filter by authorized membership scope;
- all commands validate membership status and role before loading or mutating domain state;
- SaaS Owner endpoints must not query Tenant application data views;
- cross-tenant reports may use only platform/billing/operational metadata that excludes Tenant application data.

Frontend rules:
- `/api/me` returns available memberships, selected context, base profile, and base settings needed to render the shell;
- UI routes and navigation are filtered by selected context;
- switching context must update API request scope;
- profile and settings surfaces must call backend APIs and respect scoped visibility/editability rules;
- hidden navigation is not a security boundary.

## WorkOS integration expectations

Use WorkOS/AuthKit as the supported user authentication service for authentication and identity linking:
- sign-in, callback, session/token acquisition in the React frontend;
- JWT bearer token validation in Akka HTTP endpoints;
- local account lookup/linking using WorkOS identity claims;
- invitation flows that create local authorization state and deliver/capture invite email before first sign-in.

`/api/me` should return:
- local `accountId`;
- email and display name;
- account status;
- active memberships;
- available roles and scopes;
- selected/default context if the frontend uses context switching;
- base profile fields needed by the app shell;
- base settings, including `preferredThemeId` for the selected named UI theme;
- minimal browser-facing data only.

First-login linking through `/api/me` must require a valid invitation, invite token/acceptance context, or explicit membership policy. It must not silently self-register privileged users from WorkOS claims alone.

## Profile and settings APIs

Baseline authenticated user APIs:

```text
GET  /api/me
GET  /api/me/profile
PATCH /api/me/profile
GET  /api/me/settings
PATCH /api/me/settings
```

Optional scoped administration APIs, when profile visibility or support workflows require them:

```text
GET /api/tenant/{tenantId}/users/{accountId}/profile
GET /api/tenant/{tenantId}/customers/{customerId}/users/{accountId}/profile
```

API rules:
- profile update requests may edit only declared editable profile fields;
- settings update requests initially support `preferredThemeId` constrained to the app's available named theme ids;
- app-specific fields must declare whether they are account-wide, tenant-scoped, or customer-scoped;
- tenant/customer administrators may view only the profile attributes needed for administration and collaboration;
- administrators must not edit another user's personal settings unless the generated app explicitly adds delegated preference management.

## AI-first administration behavior

Core administration should include AI-first features from the start. Generated foundations must manage admin offload as governed runtime behavior, not only as static code classes. Each physical foundation agent, including a consolidated `UserAdminAgent` when used, needs an active tenant-scoped `AgentDefinition`, approved active `PromptDocument`/`PromptVersion`, assigned `SkillDocument`/`SkillVersion` entries through an `AgentSkillManifest`, a deny-by-default `ToolPermissionBoundary`, and durable `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` records.

### Decision cards

Use decision cards for high-risk or unusual admin actions:
- granting admin roles;
- adding a SaaS Owner email as a Tenant support user;
- disabling the last active Tenant Admin or Customer Admin;
- suspending a Tenant;
- bulk invitations;
- unusual cross-level memberships;
- extending support access.

Decision card fields:
- requested action;
- actor and target account;
- scope and affected organization;
- policy triggers;
- risk/confidence score;
- evidence and relevant history;
- alternatives;
- approve/deny/request-changes actions;
- audit trace link.

### Mandatory AI-assisted admin offload

The foundation must include bounded admin offload responsibilities that recommend or draft work, not autonomously execute high-risk access changes. These responsibilities may be implemented by one governed `UserAdminAgent` with approved skills such as `access-review`, `admin-risk-scoring`, `invitation-drafting`, `role-recommendation`, `support-access-review`, and `audit-summary`, or by separate specialized agents when separate lifecycle, model, prompt, tool boundary, steward, or scaling concerns justify it.

Required responsibilities:
- access review, often `AccessReviewAgent`, identifies stale invited accounts, dormant admins, users with multiple high-privilege memberships, orphaned customer organizations with no active admin, and last-admin risks;
- admin risk scoring, often `AdminRiskAgent`, scores proposed admin actions and produces risk, confidence, policy triggers, alternatives, and approval needs;
- invitation drafting, often `InvitationDraftAgent`, drafts invitation copy, role rationale, and bulk invite preparation without exposing raw tokens;
- role recommendation, often `RoleRecommendationAgent`, recommends least-privilege roles/capabilities from scoped context;
- support-access review, often `SupportAccessReviewAgent`, reviews support memberships nearing expiry, unusual use, purpose, and revocation candidates;
- admin audit summary, often `AdminAuditSummaryAgent`, summarizes admin audit/search results with actor, target user, scope, policy, and trace links;
- policy proposal drafting, often `AdminPolicyProposalAgent` when enabled for policy governance, drafts policy/permission/threshold proposals only; human governance is required for commits.

Agents may autonomously draft invites, summarize risk, recommend roles, identify stale/dormant access, prepare bulk invite drafts, create low-risk admin tasks, generate audit summaries, and create decision cards. They must not autonomously grant admin roles, remove last admin, expand support access, suspend tenants, bulk disable users, change policy/permissions, or access tenant/customer data outside authorized tool scope.

Runtime assembly contract: resolve the active `AgentDefinition`; reject disabled, archived, cross-tenant, or unauthorized agents; assemble only approved active prompt versions; include compact skill manifest entries rather than full skill text; authorize `readSkill(skillId)` against tenant, agent, manifest, skill status/version, mode, and `AuthContext`; trace prompt assembly and every allowed or denied skill load. Prompt and skill content is behavior guidance only and cannot grant tool permissions, data access, role capabilities, tenant/customer scope, or approval authority.

High-risk recommendations route to decision cards with evidence, risk, confidence, alternatives, policy triggers, and audit links.

### Audit trace

Record audit events for:
- account invite/link/activation/disable/remove;
- membership grant/change/suspend/remove;
- role changes;
- context switches for high-privilege users when relevant;
- support-access creation/use/expiry/removal;
- policy checks and decision-card outcomes;
- WorkOS identity link/unlink events.

## Suggested implementation components

| Component | Responsibility |
|---|---|
| `AccountEntity` | Local account lifecycle and identity-link history. |
| `UserProfileEntity` | Current profile attributes; use audit-grade history only when required by the app. |
| `UserSettingsEntity` | Current user preferences, starting with `preferredThemeId` named-theme selection. |
| `MembershipEntity` or scoped membership state | Role/scope lifecycle with audit-grade changes. |
| `TenantEntity` | Tenant organization lifecycle, billing-safe profile metadata, status transitions, bootstrap state, and audit-grade Tenant management history; prefer Event Sourced Entity when lifecycle history matters. |
| `TenantDirectoryView` | SaaS Owner Tenant list/search by platform-safe tenant metadata, status, onboarding, subscription indicators, and bootstrap Tenant Admin status. |
| `TenantAdminView` | Tenant admin list/search by tenant, account, admin role, membership status, invitation state, identity link state, and last-admin risk. |
| `CustomerEntity` | Customer organization lifecycle under a Tenant. |
| `InvitationWorkflow` | Invite creation, email delivery/outbox, WorkOS sign-in/link, activation, expiry, resend, revoke/cancel, acceptance idempotency, delivery status, delivery attempts, and audit. |
| `AccessReviewTimedAction` | Periodic stale-access/support-access checks. |
| `UserDirectoryView` | Scoped user list/search and user detail entry points without requiring known user ids. |
| `MembershipView` | Scoped membership lifecycle, role/status filters, support-access expiry, and last-admin risk rows for SaaS Owner, Tenant Admin, and Customer Admin surfaces. |
| `InvitationView` | Scoped invitation status, delivery status, resend/revoke visibility, expiry, and delivery failure rows. |
| `AdminAuditView` | Queryable admin audit trail with actor, target user, action type, scope, role, membership status, invitation status, risk/policy metadata, and time-range filters. |
| `AccessReviewQueueView` | Stale invite, dormant access, risky role combination, support-access, last-admin review queue, due/expiry time, and agent-generated recommendation index. |
| `AgentDefinitionEntity` | Governed runtime profile for foundation agents: lifecycle, owner/steward, authority, prompt reference, skill manifest reference, model config reference, and tool permission boundary. |
| `PromptDocumentEntity` / `PromptVersionEntity` | Governed prompt lifecycle and immutable active prompt snapshots used during prompt assembly. |
| `SkillDocumentEntity` / `SkillVersionEntity` | Governed runtime skill lifecycle and immutable skill snapshots that can be assigned to agents. |
| `AgentSkillManifestEntity` | Per-agent allowed skill manifest used as compact prompt context and as the authorization basis for `readSkill(skillId)`. |
| `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace` | Durable trace records for prompt assembly, allowed/denied skill loads, and agent execution/tool/data decisions. |
| `UserAdminAgent` | Optional consolidated governed agent that carries approved admin skills in its `AgentSkillManifest` for access review, admin risk scoring, invitation drafting, role recommendation, support-access review, and audit summaries. |
| `AccessReviewAgent` | Optional specialized agent for the mandatory access-review responsibility: reads scoped access views and recommends stale/dormant access cleanup, last-admin risk review, and low-risk admin tasks. |
| `AdminRiskAgent` | Optional specialized agent for the mandatory admin-risk responsibility: read-only analysis and recommendation for risky admin actions. |
| `InvitationDraftAgent` | Optional specialized agent for the mandatory invitation-drafting responsibility: drafts invite messages, role rationale, and bulk invite preparation without sending or exposing tokens. |
| `RoleRecommendationAgent` | Optional specialized agent for the mandatory role-recommendation responsibility: least-privilege role/capability recommendations. |
| `SupportAccessReviewAgent` | Optional specialized agent for the mandatory support-access review responsibility: expiry, purpose, usage, and revocation recommendations. |
| `AdminAuditSummaryAgent` | Optional specialized agent for the mandatory audit-summary responsibility: audit/search summaries for supervisors and auditors. |
| `AdminPolicyProposalAgent` | Optional specialized agent for conditional policy-proposal responsibility when products allow policy governance drafts; policy commits remain human-governed. |
| `AdminDecisionWorkflow` | Approval gate for risky admin changes and agent-generated decision cards. |

## Acceptance checklist

- [ ] SaaS Owner Admin can create Tenant, update billing-safe Tenant profile metadata, change Tenant status, and invite/bootstrap Tenant Admin with mandatory Resend email delivery or captured outbox behavior.
- [ ] SaaS Owner Admin can list/search Tenants through `TenantDirectoryView` without reading Tenant application data.
- [ ] SaaS Owner Admin cannot read Tenant application data.
- [ ] Tenant Admin can manage Tenant Admins and Tenant employees only within their own Tenant through invite, resend, revoke/cancel, expiry, acceptance, role/membership lifecycle, and delivery-failure visibility.
- [ ] Cross-tenant Tenant Admin attempts and Tenant Admin attempts to create sibling Tenants or assign SaaS Owner roles are forbidden and audited.
- [ ] Tenant Admin can create Customer organizations and invite Customer Admins.
- [ ] Customer Admin can manage Customer users through the same Invitation lifecycle.
- [ ] Same email can hold multiple memberships and must operate in an explicit context.
- [ ] WorkOS authentication is separate from Akka-owned authorization state.
- [ ] Base profile fields are viewable/editable according to scope rules and do not grant permissions.
- [ ] Base user settings include editable `preferredThemeId` constrained to available named themes, initially `aurora-light`, `cobalt-light`, `obsidian-dark`, `midnight-dark`, and `dark-night`.
- [ ] Profile and settings models can be extended by app-specific requirements.
- [ ] Admins can list/search users, view user detail, and manage users without already knowing internal user IDs.
- [ ] Admins can assign/replace/remove roles and add/suspend/reactivate/remove memberships inside their authority boundary.
- [ ] Last-admin protection prevents removing the final active Tenant Admin or Customer Admin.
- [ ] Tenant-created support access is scoped, time-limited, auditable, revocable, and visible in support-access and access-review surfaces.
- [ ] UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView are first-slice read models with required query filters, scoped query authorization, redaction, pagination, stale invite/access-review correctness, and audit trace completeness tests.
- [ ] Risky admin actions can produce decision cards.
- [ ] Governed runtime agent foundation is present for foundation agents: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, authorized `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`.
- [ ] Access-review, admin-risk-scoring, invitation-drafting, role-recommendation, support-access-review, and admin-audit-summary responsibilities are present, either through one governed `UserAdminAgent` with assigned skills or through specialized agents such as AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, and AdminAuditSummaryAgent.
- [ ] Admin agents produce recommendations, drafts, audit summaries, and decision cards without unauthorized automatic changes.
- [ ] Audit views can answer who changed what, in which scope, when, why, and under which policy.
