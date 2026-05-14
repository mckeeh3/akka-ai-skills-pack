# Core SaaS identity, tenancy, and administration

Use this doc to implement the product-agnostic identity and administration foundation for AI-first SaaS applications.

Related docs:
- `core-ai-first-saas-foundation.md`
- `core-saas-owner-tenant-billing.md`
- `security-workos-auth-and-admin.md`

## Design intent

The core model supports a three-level SaaS business structure:

```text
SaaS Owner -> Tenant -> Customer
```

A **Tenant** is the SaaS user organization. A **Customer** is always modeled initially as an organization served by a Tenant. Specific generated applications may later adapt this model for individual consumers, but the seed foundation should start with organization customers.

## Identity and authorization model

WorkOS authenticates the human. The Akka backend owns local authorization state.

```text
WorkOS Identity
  -> Local Account
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
- include scope identifiers in every protected command/query: `tenantId`, `customerId`, or platform owner context as applicable.

## Account and membership concepts

### Local Account

A local account represents a human identity known to the application.

Recommended fields:
- `accountId`
- `workosUserId` or external identity subject, nullable until linked
- `email`
- `displayName`
- `status`: `INVITED`, `ACTIVE`, `DISABLED`, `REMOVED`
- `createdAt`, `activatedAt`, `disabledAt`
- audit metadata: created by, reason, source

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

## Roles

| Role | Scope | Purpose |
|---|---|---|
| `SAAS_OWNER_ADMIN` | SaaS Owner | Manage Tenants, Tenant Admin bootstrap, SaaS Owner to Tenant subscription/billing state. |
| `TENANT_ADMIN` | Tenant | Manage Tenant users, Customer organizations, Customer Admins, and Tenant-owned app configuration/data. |
| `TENANT_EMPLOYEE` | Tenant | Use Tenant-owned application functionality according to app-specific permissions. |
| `CUSTOMER_ADMIN` | Customer | Manage Customer users and supervise Customer-side service use. |
| `CUSTOMER_USER` | Customer | Use Customer-facing online services provided by the Tenant. |

Do not use a global super-admin role for Tenant data access. SaaS Owner permissions are platform-operations permissions only.

## Administration flows

### SaaS Owner Admin creates a Tenant and initial Tenant Admin

1. SaaS Owner Admin creates Tenant organization.
2. SaaS Owner Admin assigns subscription/billing state.
3. SaaS Owner Admin invites one or more Tenant Admins.
4. Invited human signs in through WorkOS.
5. Backend links WorkOS identity to local account and activates Tenant membership.
6. Audit trace records Tenant creation, invitation, link, activation, and subscription state.

### Tenant Admin manages Tenant employees

1. Tenant Admin invites employee by email.
2. Backend creates local account if needed and Tenant membership in `INVITED` state.
3. Employee signs in through WorkOS and links/activates membership.
4. Tenant Admin may change roles, suspend, or remove membership.
5. All changes are Tenant-scoped and auditable.

### Tenant Admin creates Customer organization and Customer Admin

1. Tenant Admin creates Customer organization under Tenant.
2. Tenant Admin invites Customer Admin.
3. Customer Admin signs in through WorkOS and activates Customer membership.
4. Customer Admin can then manage Customer users.

### Customer Admin manages Customer users

1. Customer Admin invites Customer users within the Customer organization.
2. Backend creates local account if needed and Customer membership in `INVITED` state.
3. Customer user signs in through WorkOS and activates membership.
4. Customer Admin may suspend or remove Customer users.

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
- `/api/me` returns available memberships and selected context;
- UI routes and navigation are filtered by selected context;
- switching context must update API request scope;
- hidden navigation is not a security boundary.

## WorkOS integration expectations

Use WorkOS for authentication and identity linking:
- sign-in, callback, session/token acquisition in the React frontend;
- JWT bearer token validation in Akka HTTP endpoints;
- local account lookup/linking using WorkOS identity claims;
- invitation flows that create local authorization state before first sign-in.

`/api/me` should return:
- local `accountId`;
- email and display name;
- account status;
- active memberships;
- available roles and scopes;
- selected/default context if the frontend uses context switching;
- minimal browser-facing data only.

## AI-first administration behavior

Core administration should include AI-first features from the start.

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

### Access review assistant

The system should be able to recommend, not autonomously execute, access cleanup:
- stale invited accounts;
- dormant admins;
- support memberships nearing expiry;
- users with multiple high-privilege memberships;
- customer admins with no recent activity;
- orphaned customer organizations with no active admin.

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
| `MembershipEntity` or scoped membership state | Role/scope lifecycle with audit-grade changes. |
| `TenantEntity` | Tenant organization lifecycle and status. |
| `CustomerEntity` | Customer organization lifecycle under a Tenant. |
| `InvitationWorkflow` | Invite, WorkOS sign-in/link, activation, expiry, resend. |
| `AccessReviewTimedAction` | Periodic stale-access/support-access checks. |
| `AdminAuditView` | Queryable admin audit trail. |
| `MembershipView` | Scoped user lists for SaaS Owner, Tenant Admin, and Customer Admin screens. |
| `AdminRiskAgent` | Read-only analysis and recommendation for risky admin actions. |
| `AdminDecisionWorkflow` | Approval gate for risky admin changes. |

## Acceptance checklist

- [ ] SaaS Owner Admin can create Tenant and invite Tenant Admin.
- [ ] SaaS Owner Admin cannot read Tenant application data.
- [ ] Tenant Admin can manage Tenant employees.
- [ ] Tenant Admin can create Customer organizations and invite Customer Admins.
- [ ] Customer Admin can manage Customer users.
- [ ] Same email can hold multiple memberships and must operate in an explicit context.
- [ ] WorkOS authentication is separate from Akka-owned authorization state.
- [ ] Tenant-created support access is scoped, time-limited, auditable, and revocable.
- [ ] Risky admin actions can produce decision cards.
- [ ] Access review assistant produces recommendations without unauthorized automatic changes.
- [ ] Audit views can answer who changed what, in which scope, when, why, and under which policy.
