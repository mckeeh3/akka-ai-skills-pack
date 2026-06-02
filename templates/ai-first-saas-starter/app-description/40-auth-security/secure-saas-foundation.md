# Secure SaaS Foundation

This layer is mandatory for the scaffolded core app and for every later domain-specific feature.

## Required foundation objects

- Account
- UserProfile
- UserSettings
- Tenant
- Customer
- Membership
- Role
- Permission/Capability grant
- Invitation
- selected AuthContext
- AdminAuditEvent
- support-access record where support access is enabled

## Required runtime contracts

- WorkOS/AuthKit browser authentication is the supported production user-auth service.
- `/api/me` returns browser-safe account, profile, settings, memberships, selected context, visible capabilities, and visible workstreams.
- Every protected backend route, command, query, stream, workflow action, timer, consumer, browser action, and agent tool checks the selected AuthContext server-side.
- Tenant/customer isolation is enforced in backend data access and tested with cross-scope negative cases.
- Disabled or missing authority fails closed with safe denial shapes and audit/work traces.
- Frontend state, prompts, route names, hidden fields, or loaded skill text cannot grant authority.

## Core app coverage

The five core workstreams must preserve these security semantics:

- My Account: self-scope only, selected-context visibility, profile/settings changes within own authority.
- User Admin: scoped user, invitation, membership, role, support-access, and access-review authority.
- Agent Admin: governed behavior records and proposal/review authority; no prompt-driven authority expansion.
- Audit/Trace: scoped read/search/export authority with redaction.
- Governance/Policy: policy proposal, simulation/impact, approval, activation, and rollback authority.

## Domain-specific extension rule

Every new CRM/SMB/domain capability must link back to this file and record:

- actor/caller and AuthContext;
- tenant/customer/account scope;
- permission/capability grant;
- denial shape;
- audit/work trace obligation;
- tenant-isolation and forbidden-access tests.
