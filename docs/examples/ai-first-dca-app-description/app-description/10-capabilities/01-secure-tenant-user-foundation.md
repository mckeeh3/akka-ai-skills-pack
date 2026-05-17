# Capability: Secure Tenant and User Foundation

This capability is the mandatory secure AI-first SaaS foundation for the DCA vertical reference. It comes before DCA-specific lifecycle, telemetry, supplies, service, billing-review, governance, command-center, and audit/outcome capabilities.

## Capability definition

- capability-id: `secure-tenant-user-foundation`
- capability number: `CAP-00`
- class:
  - command
  - read/evidence
  - workflow
  - scheduled
  - trace/audit
  - policy/governance
- purpose:
  - provide authenticated human access, Akka-owned authorization, tenant/customer isolation, memberships, roles, invitations, support access, SaaS Owner billing boundary, and admin auditability before DCA automation is generation-ready
- business outcome:
  - every protected DCA user action, admin action, workflow action, view query, agent tool, timer, consumer, API route, and UI surface operates under an authenticated account, selected `AuthContext`, enforceable permission/capability grant, and auditable tenant/customer scope

## In-scope outcomes

- SaaS Owner, Tenant, and Customer organization boundaries, where Tenants are copier-dealer organizations using the DCA app and Customers are organizations served by a Tenant.
- Local `Account` linked to a WorkOS-authenticated human identity.
- `UserProfile` and `UserSettings` as user-experience state, not authorization state.
- `Membership` records for SaaS Owner, Tenant, and Customer scopes, with status, roles, expiry where applicable, and audit metadata.
- Canonical foundation roles: `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, and `AUDITOR`.
- DCA extension roles such as `DEALER_OWNER`, `OPERATIONS_SUPERVISOR`, and `POLICY_OWNER` mapped to scoped foundation roles and named permissions/capabilities.
- `Permission/Capability` grants used by backend routes, commands, queries, workflows, views, timers, consumers, and agent tools.
- `Invitation` lifecycle for SaaS Owner, Tenant, Customer, and user onboarding: create, deliver/capture email, resend, revoke/cancel, expire, accept, delivery status, delivery attempts, idempotency, and audit.
- `/api/me` as the browser-safe account, profile, settings, membership, selected-context, role, and capability contract.
- `AuthContext` containing account id, selected scope, tenant/customer ids when applicable, active membership, roles/capabilities, actor metadata, and correlation id.
- `AdminAuditEvent` records for identity, account, membership, role, invitation, support-access, policy, approval, billing-boundary, data-access, denial, and consequential AI/tool activity.
- Scoped admin read models: `UserDirectoryView`, `MembershipView`, `InvitationView`, `AdminAuditView`, and `AccessReviewQueueView`.
- Tenant-created support-access memberships for SaaS Owner personnel, time-limited and auditable, with no global super-admin bypass.
- SaaS Owner to Tenant subscription/billing boundary, including plan/subscription/entitlement metadata that excludes Tenant application data.
- AI-assisted admin offload: AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, and AdminPolicyProposalAgent where policy proposal drafting is enabled.
- Foundation UI surfaces for sign-in, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, admin-agent recommendations, and decision cards.

## Out-of-scope outcomes

- DCA supplies, service, telemetry, lifecycle, meter-review, or policy-threshold automation beyond the foundation permission and audit scaffolding.
- Tenant-to-Customer billing, copier contract pricing, supply costs, service charges, and meter-billing calculations; those are DCA domain capabilities.
- SaaS Owner access to Tenant application data without a Tenant-created support-access membership.
- Self-registration of privileged users from WorkOS claims alone.
- Prompt-only, frontend-only, hidden-field, or navigation-only authorization.
- Raw invitation tokens, provider secrets, WorkOS private identifiers, JWTs, or unrelated tenant/customer data in browser, view, log, audit summary, or agent-tool responses.

## Actors and callers

- SaaS Owner Admin operating platform-safe Tenant setup, subscription/billing boundary, and Tenant Admin bootstrap.
- Tenant Admin managing dealer employees, Customer organizations, Customer Admins, support-access grants, tenant settings, and DCA role/capability mappings.
- Customer Admin managing customer-scoped users and viewing customer-scoped DCA service context where allowed.
- Tenant employee, Customer user, Auditor, invited user, and support operator with an active support-access membership.
- DCA roles mapped to foundation authority: Dealer Owner, Operations Supervisor, Policy Owner, and scoped operational users.
- Admin-assistant agents for access review, risk analysis, invitation drafting, role recommendation, support-access review, audit summary, and policy proposal drafting.
- Internal callers: InvitationWorkflow, email/outbox Consumer, expiry/reminder TimedAction, access-review TimedAction, admin views, authorization service, `/api/me`, and protected admin/API routes.

## Authority and contract

- AuthContext / scope:
  - WorkOS authenticates browser humans; Akka-owned local state authorizes all DCA and foundation actions.
  - Protected operations require an authenticated account, active local account status, selected `AuthContext`, active membership, matching tenant/customer scope, and required role/permission/capability.
  - Invite acceptance and first-login linking may proceed only through a valid invitation, acceptance context, or explicitly modeled membership policy.
  - SaaS Owner Admin authority is limited to platform-safe metadata, Tenant bootstrap, and SaaS Owner to Tenant billing/subscription state; it does not grant Tenant application-data access.
  - Support access is a normal Tenant-scoped membership created by a Tenant Admin, time-limited by default, reasoned, visible, revocable, and audited.
- permissions / named capability grants:
  - foundation examples: `saas_owner.tenant.manage`, `saas_owner.billing.manage`, `tenant.user.read`, `tenant.user.manage`, `tenant.role.manage`, `tenant.invitation.manage`, `tenant.support_access.manage`, `tenant.audit.read`, `customer.user.manage`, `customer.audit.read`, `account.profile.edit`, `account.settings.edit`.
  - DCA capabilities must map app-specific authority, such as supplies approval or policy activation, onto these scoped foundation permissions rather than bypassing them.
- inputs / validation / idempotency:
  - commands include actor account id, selected scope, `tenantId` and `customerId` where required, target ids, correlation id, idempotency key, reason, and policy references where applicable.
  - invitations normalize email addresses and deduplicate active invites according to invitation policy.
  - role/membership changes reject disabled actors or targets, unknown roles, invalid scopes, self-widening authority, cross-tenant ids, and last-admin removal.
  - `/api/me` linking is idempotent and must not duplicate accounts, widen scopes, or overwrite administrator-managed roles.
- outputs / redaction / denial shape:
  - browser and agent responses contain only browser-safe profile fields, account status, membership summaries, selected context, role/capability hints, invitation status, delivery status, audit summaries, and decision-card metadata.
  - denials use stable `401`, `403`, pending-invite, not-found, or validation shapes that avoid leaking unrelated tenant/customer resource existence.
  - raw tokens, secrets, private provider data, and unrelated tenant/customer data are never returned.
- data access:
  - Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role, Permission/Capability, Invitation, SupportAccessGrant, Plan, TenantSubscription, BillingAccount, TenantEntitlement, AdminAuditEvent, and decision-card records.
  - Scoped views: UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, and billing-safe owner/tenant entitlement views.
  - every DCA domain read/write must include tenant/customer filters and apply redaction before browser or agent exposure.
- side effects:
  - create/update local accounts, profiles, settings, tenants, customers, memberships, roles, invitations, support-access grants, billing-safe subscription/entitlement records, selected context, and admin audit events.
  - send or capture invite/reminder email through a configured provider or safe outbox adapter.
  - schedule invitation expiry, support-access expiry, reminders, access-review checks, and billing/grace-period checks where modeled.
  - create decision cards for risky role, support-access, billing, account, policy, or admin-agent recommendations.
- exposure surfaces:
  - browser UI shell, context selector, profile/settings, foundation admin screens, support-access screen, access-review queue, admin audit, and decision cards.
  - JWT-protected HTTP APIs including `/api/me`, profile/settings APIs, admin user/membership/invitation APIs, support-access APIs, audit APIs, and billing-boundary APIs.
  - workflow surface for invitation, support-access approval, billing decision, and risky admin decision flows.
  - timer surface for expiry/reminders/access review.
  - consumer surface for email/outbox delivery and provider events when configured.
  - view/query surfaces for scoped user, invitation, membership, access-review, audit, and entitlement evidence.
  - agent tools only for scoped read/evidence, draft, summarize, recommend, and decision-card proposal operations unless a future policy grants a narrow autonomous boundary.

## Policy, approval, and autonomy

- Default autonomy:
  - human-approved for role escalation, account disable/reactivate, support-access grant/extension, last-admin risk, policy activation, tenant suspension, and high-impact billing actions.
  - agent-assisted for drafting invites, recommending least-privilege roles, summarizing audit/search results, identifying stale access, scoring admin risk, preparing support-access reviews, and drafting policy proposals.
  - autonomous only for low-risk expiry, reminder, idempotent delivery retry, and read-only evidence refresh behaviors explicitly allowed by policy.
- Approval gates:
  - risky role changes, high-privilege assignments, support-access grants/extensions, account relink/reset, bulk user actions, policy/permission changes, and subscription suspension/reactivation produce decision cards when policy requires.
  - DCA supply, service, meter, lifecycle, and policy actions must later cite this foundation AuthContext and approval model.
- Escalation:
  - missing authority, cross-scope attempts, stale invitations, dormant admins, unusual memberships, last-admin risks, expired support access, suspicious admin behavior, or low-confidence agent recommendations are routed to access review or decision cards.

## Audit and trace requirements

- Audit events:
  - WorkOS sign-in/link/unlink, local account creation/activation/disable/reactivate/removal, profile/settings update where required, context switch for high-privilege users, invitation create/resend/revoke/expire/accept/delivery failure, membership/role lifecycle, support-access lifecycle/use, billing-boundary changes, protected admin reads, denials, policy checks, approvals, and admin-agent/tool activity.
- Work-trace fields:
  - actor account id, effective principal, agent id when applicable, selected AuthContext, tenant/customer id, target resource id, permission/capability checked, policy clause/version, decision outcome, reason, risk/confidence, correlation id, idempotency key, trace link, and redaction marker.
- Retention/redaction:
  - retain accountability facts while redacting JWTs, invite raw tokens, provider secrets, WorkOS private data not needed by authorized operators, full sensitive payloads, and unrelated tenant/customer details.

## Required tests

- success:
  - SaaS Owner Admin creates a Tenant, assigns billing-safe subscription state, and invites initial Tenant Admin.
  - Tenant Admin invites employees, creates Customer organizations, invites Customer Admins, grants and revokes support access, and searches scoped users without knowing internal ids.
  - Customer Admin invites Customer users and manages customer-scoped memberships.
  - `/api/me` returns browser-safe account, profile, settings, memberships, selected context, and capabilities.
- validation:
  - malformed email, invalid role, missing tenant/customer id, unsupported context switch, invalid idempotency key, and invalid support-access expiry are rejected safely.
- forbidden and tenant isolation:
  - wrong tenant/customer, missing membership, disabled account, expired support access, denied role/scope, last-admin removal, unauthorized SaaS Owner Tenant-data access, unauthorized billing mutation, and unauthorized agent tool calls are denied without leaking resource existence.
- idempotency:
  - repeated `/api/me`, duplicate invitation create/resend, invite acceptance retry, delivery retry, and support-access expiry retry are safe and auditable.
- approval:
  - risky role escalation, support-access grant/extension, account relink/reset, bulk admin action, tenant suspension/reactivation, billing override, and agent-suggested role change require human approval unless an explicit accepted policy allows a narrow autonomous path.
- audit/trace:
  - success, denial, protected read, delivery failure, support-access use, billing-boundary change, approval outcome, and consequential AI/tool activity create AdminAuditEvent/work-trace records.
- surface-specific:
  - frontend hides unavailable actions but backend still denies; `/api/me` exposes no secrets; admin views are scoped/redacted/paginated; agent tools receive only scoped/redacted evidence; timers and consumers are retry-safe; generated frontend assets do not contain backend secrets.

## Linked layers

- operating model:
  - `../15-operating-model/agent-roles-and-authority.md`
  - `../15-operating-model/policies-and-approval-gates.md`
  - `../15-operating-model/decisions-exceptions-and-evidence.md`
  - `../15-operating-model/audit-trace-and-outcomes.md`
  - `../15-operating-model/outcomes-and-learning-loops.md`
- behavior:
  - `../20-behavior/state-models/01-lifecycle-foundation.md`
  - `../20-behavior/flows/02-lifecycle-and-exception-flows.md`
  - future foundation behavior refresh should add explicit tenant/user/invitation/access flows
- tests:
  - `../30-tests/README.md`
  - future test refresh should add foundation acceptance, negative, regression, and operational specs
- auth/security:
  - `../40-auth-security/identity-and-trust.md`
  - `../40-auth-security/authorization-rules.md`
  - `../40-auth-security/agent-permissions.md`
  - `../40-auth-security/data-protection.md`
  - `../40-auth-security/boundary-and-surface-rules.md`
- observability:
  - `../50-observability/audit-trace-and-outcomes.md`
- UI:
  - `../55-ui/ui-surfaces.md`
  - `../55-ui/style-guide.md`
- generation:
  - `../60-generation/implementation-slices.md`
- traceability:
  - `../70-traceability/ai-first-coverage-map.md`
- review:
  - `../80-review/structure-gap-summary.md`

## Refresh notes

This file establishes the first-class foundation capability. Later migration tasks should align auth/security, test, UI, observability, generation-slice, and traceability files to this contract without treating endpoints, workflows, agents, or entities as capability roots.
