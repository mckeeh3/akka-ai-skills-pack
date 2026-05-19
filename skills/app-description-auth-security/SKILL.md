---
name: app-description-auth-security
description: Update the authoritative auth/security layer of the app description by capturing authentication, authorization, trust boundaries, sensitive-data rules, and forbidden access behavior without generating code.
---

# App Description Auth/Security

Use this skill when the user introduces or revises security requirements in the application description.

This skill maintains the **auth/security layer** of the internal app description.
It does not generate code.
It defines the access-control and trust semantics that downstream generation must honor.

## Goal

Create or update auth/security-oriented app-description artifacts that:
- define who may call what
- define how identity is established
- define authorization boundaries and protected operations
- define sensitive-data handling expectations
- define allowed and forbidden access patterns
- define AI-first authority boundaries for agents, tools, data access, approvals, and governed policy changes for generated AI-first SaaS
- define failure behavior for unauthorized and forbidden actions
- identify linked impacts on behavior, tests, observability, and generation readiness

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/capability-first-backend-architecture.md` for protected capability AuthContext, permission, scope, exposure, and audit semantics
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation semantics
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../../docs/security-workos-auth-and-admin.md` when browser user authentication, WorkOS, JWT-secured APIs, or basic administration are in scope

## Use this skill when

The input sounds like:
- "require login for..."
- "use WorkOS for sign-in..."
- "secure frontend/backend calls with JWT..."
- "only admins may..."
- "tenants must not see each other's data"
- "this endpoint should be internal-only"
- "redact sensitive fields in responses or logs"
- "unauthorized access should return..."
- "store or expose this data only under these conditions"
- "the agent may use this tool/data but may not commit changes without approval"
- "policy changes must be simulated and human-approved before activation"

Use it for:
- authentication model changes for WorkOS/AuthKit
- frontend-to-backend JWT bearer-token requirements
- authorization rule changes
- trust-boundary clarification
- tenancy isolation requirements
- basic user administration rules, including initial admin bootstrap, invitations, role assignment, and disabling users
- secret or credential handling expectations
- agent, workflow, tool-use, data-access, and policy-commit permission boundaries
- sensitive-data visibility rules
- allowed and forbidden access paths
- unauthorized and forbidden failure behavior

## Core operating rule

Treat security as part of the app's authoritative meaning, not as a later implementation hardening step.

For every generated SaaS app, start from the `core-saas-foundation` baseline: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, Invitation, AuthContext, `/api/me`, backend authorization, AdminAuditEvent, support-access, billing boundary, tenant/customer scoping, and tenant isolation. WorkOS/AuthKit is the supported browser authentication provider and Resend (resend.com) is the supported production email service for invite/account emails and future app email features. WorkOS/Resend runtime settings may remain open questions during description maintenance, but generated SaaS apps must not choose different auth or production email services unless the skills pack is extended with provider-specific guidance. Local authorization, tenancy, audit, and denial semantics are mandatory.

This skill should define:
- who the actors are
- how trust is established
- what each actor may do
- what each actor must never do
- what agents, workflows, and automated tools may do autonomously versus only recommend
- what evidence or behavior is required when access is denied

## What this skill must capture

For each requested change, identify and describe as applicable:
- secure SaaS foundation impact: Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role, Permission/Capability, Invitation, AuthContext, AdminAuditEvent, support-access, billing boundary, `/api/me`, backend authorization, and tenant/customer scoping
- caller identities or principal categories
- authentication mechanism or trust source, using WorkOS/AuthKit for generated browser users
- frontend-to-backend token propagation expectations
- local user/account linking rules
- authorization rules by capability id/operation/query and selected exposure surface
- tenancy or ownership boundaries
- admin roles and scopes when user administration is in scope
- bootstrap/invite/first-login behavior when relevant, including mandatory email delivery for production, explicit local/dev/test outbox capture, invitation status, expiry, resend, revoke/cancel, acceptance, delivery status, delivery attempts, idempotency, and audit trail
- internal-only versus external access
- agent/tool/data/policy permission grants, autonomy thresholds, and approval gates
- policy, prompt, skill, guardrail, and evaluator update authority
- sensitive-data categories
- visibility, masking, redaction, or retention expectations
- secret-handling constraints
- forbidden access patterns
- unauthorized and forbidden response behavior
- explicit default-deny rule for every route, agent tool, data access path, workflow action, view query, stream, consumer side effect, timer action, and generated UI action unless deliberately public static assets
- dependencies on behavior, test, and observability layers

## Standard auth/security output shape

Use this response shape when updating or summarizing auth/security changes:

```md
# Auth/Security Specification Update

## Requested change
- ...

## Identity and trust model
- ...

## Authorization rules
- capability grants by operation/query:
- AuthContext and tenant/customer scope:
- exposure-surface rules:

## Agent/tool/data authority
- ...

## Trust boundaries and access surfaces
- ...

## Sensitive-data rules
- ...

## Forbidden access behavior
- ...

## Unauthorized / forbidden outcomes
- ...

## Open questions and assumptions
- ...

## Affected linked layers
- behavior:
- tests:
- observability:
```

## Security modeling rules

### Secure SaaS foundation defaults

Every app-description auth/security layer must explicitly capture:
- SaaS Owner, Tenant, and Customer boundaries, including support-access and billing/subscription separation
- Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, and selected AuthContext semantics
- `/api/me` payload boundaries: browser-safe profile, settings, memberships, selected context, and capabilities only
- a central backend authorization rule set used by protected HTTP/gRPC/MCP routes, component commands, view queries, streams, workflow actions, agent tools, consumers, timers, and generated UI actions
- tenant/customer-scoped commands and queries with mechanical rejection of cross-scope access
- disabled-user, inactive-membership, expired support-access, missing role/scope, and forbidden access behavior
- AdminAuditEvent requirements for identity, membership, role, invitation, support-access, billing-boundary, policy, approval, data-access, and consequential AI/tool activity
- security test links for tenant isolation, forbidden access, disabled user, role/scope denial, `/api/me`, audit, and frontend secret-boundary cases

### WorkOS/JWT web app defaults

When the app uses WorkOS with an Akka-hosted frontend, capture these explicitly:
- public static frontend routes versus JWT-protected `/api/...` routes
- WorkOS/AuthKit establishes browser user identity
- frontend sends `Authorization: Bearer <token>` to Akka APIs
- Akka endpoints use `@JWT` and read claims through request context
- local Akka user/account/role state is the application authorization source unless specified otherwise
- `/api/me` returns the current local account, status, roles, and scopes for UX
- backend secrets such as `WORKOS_API_KEY` stay out of frontend env files and built assets

### AI-first authority defaults

When delegated work, agents, or governed automation are in scope, capture:
- human roles that can delegate, supervise, approve, override, escalate, teach, or audit
- agent identities or component principals where useful for permission and trace semantics
- tool, dataset, action, tenant, and customer scopes available to each agent or workflow
- decisions the system may commit autonomously versus decisions requiring approval, exception handling, or escalation
- confidence, risk, impact, spend, data-sensitivity, or policy thresholds that restrict autonomy
- who may create, simulate, approve, activate, or roll back prompt, skill, policy, guardrail, evaluator, and threshold changes
- what trace or audit evidence is required for allowed, denied, approval, override, policy-change, and data-access paths

### Basic administration defaults

Basic administration is part of the secure SaaS foundation for generated SaaS apps. Capture:
- SaaS Owner, Tenant Admin, Customer Admin, and user/member roles appropriate to the scope
- scope boundaries such as SaaS Owner platform metadata, tenant id, customer id, support-access tenant scope, or self
- startup admin bootstrap source, such as `ADMIN_USERS`, and how it creates the first SaaS Owner or Tenant admin without creating a permanent bypass
- complete email-invite onboarding and first-login account-linking behavior: Resend production delivery configuration is required, local/dev/test must use an explicit captured outbox adapter, failed delivery must be visible/auditable, and `/api/me` must link only through a valid invitation or accepted membership policy
- disabled-user behavior and inactive-membership denial
- server-side checks and AdminAuditEvent records for every admin operation

### 1. Define principals explicitly
Do not say "authorized users" if the actual roles, identities, or caller classes matter.
Be explicit about who is acting.

### 2. Separate authentication from authorization
Clarify both:
- how identity is established
- what that identity is allowed to do

### 3. Make boundaries explicit
If the system has tenant, account, team, environment, or internal-service boundaries, define them directly.

### 4. Define sensitive-data behavior, not just access
Capture not only who can access protected information, but also:
- what must be hidden
- what must be redacted
- what must never be logged or exposed

### 5. Make forbidden access concrete
Specify what must not be allowed, including cross-tenant reads, unauthorized mutation, internal endpoint exposure, or excessive visibility.

### 6. Define denial behavior
State what happens on authentication failure or authorization failure.
Do not leave it as an implicit framework detail if the app meaning depends on it.

### 7. Make AI-first permissions enforceable
Do not rely on prompts to restrict consequential action.
Represent tool access, data access, tenant/customer scope, approval gates, and policy-change authority as explicit permissions or thresholds that downstream implementation can enforce and test.

### 8. Flag observability implications
Security requirements often imply audit, alerts, or diagnostic traces.
Call that out explicitly.

## Clarification policy

Ask only the smallest questions needed to make the security model materially safe and usable.

Good questions include:
- "Who are the distinct caller types for this action?"
- "Is this restriction based on role, tenant ownership, or both?"
- "Should unauthorized callers learn that the resource exists, or should the system hide that fact?"
- "Which fields are sensitive and must never appear in logs or responses?"
- "Is this surface internal-service-only, or also callable by end users?"
- "Which actions may the agent commit autonomously, and which must pause for human approval?"
- "Who may approve, simulate, activate, or roll back policy, prompt, guardrail, evaluator, or threshold changes?"

## Handoff to other skills

When the auth/security update is established, route onward as needed:
- to `app-description-capability-modeling` if the security change creates or changes protected capability ids, callers, AuthContext, permissions, named capability grants, exposure surfaces, side effects, approvals, or data-access boundaries
- to `app-description-behavior-specification` if the security change alters core user-visible behavior
- to `app-description-test-specification` to define authorization, denial, redaction, and isolation verification cases
- to `app-description-ui` if route/action visibility, capability-gated navigation, decision cards, supervision queues, or trace access needs UI description updates
- to `app-description-observability` if audit events, alerts, work traces, decision traces, policy invocations, tool/data access traces, or diagnosability expectations need explicit definition
- to AI-first companion skills when authority, governance, decision, audit, or outcome semantics need focused modeling
- to `app-description-readiness-assessment` when the user is asking whether the description is mature enough to realize
- to `akka-workos-user-auth` when realization should implement WorkOS/AuthKit, JWT-secured APIs, or `/api/me`
- to `akka-basic-user-admin` when realization should implement roles, invites, admin bootstrap, or user-management APIs

## Anti-patterns

Avoid:
- reducing security to a vague "must be secure" statement
- merging authentication and authorization into one blurry rule
- forgetting tenant or ownership isolation semantics
- leaving Account/Profile/Settings/Membership/Tenant/Customer/admin/audit foundation behavior undefined
- treating a route, agent tool, data access path, workflow action, view query, stream, or generated UI action as unauthenticated or unauthorized by default
- defining sensitive-data rules without mentioning logs, responses, or audit visibility
- assuming denial behavior is irrelevant because a framework will handle it somehow
- using prompt instructions as the only control for agent tool use, data access, policy commits, or high-impact actions
- delaying auth/security until after behavior is already fixed

## Final review checklist

Before finishing, verify:
- the caller or principal model is explicit
- authentication expectations are explicit where relevant
- secure SaaS foundation objects and `/api/me` semantics are explicit for generated SaaS apps
- authorization rules are explicit by action or surface
- trust boundaries are called out
- sensitive-data rules are included where relevant
- agent/tool/data/policy authority boundaries are explicit for generated AI-first SaaS semantics
- forbidden access behavior is explicit
- denial outcomes are explicit where relevant
- linked impacts on capability contracts, behavior, tests, UI, observability, and readiness are called out
- no code-generation step was assumed

## Response style

When answering:
- state the security change first
- separate identity, authorization, and data-protection rules
- keep the rules declarative and precise
- separate confirmed rules from assumptions
- explicitly note what tests and observability implications follow from the change
