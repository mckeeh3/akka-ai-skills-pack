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
- AI-assisted admin offload with AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, and decision cards for risky admin actions.
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
- Scoped admin-assistant agents for access review, invitation drafting, role recommendations, support-access review, and audit summarization.
- InvitationWorkflow, expiry/reminder TimedAction, email/outbox Consumer, and admin views as internal callers.

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
  - UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView
  - all reads and writes are filtered by tenant/customer scope unless explicitly SaaS-owner scoped
- side effects:
  - state changes for invitations, memberships, roles, support-access grants, selected context, profile/settings, and account status
  - email/outbox records for invitations and reminders
  - timers for invitation expiry, reminder, support-access expiry, and access-review cadence
  - AdminAuditEvent and work-trace records for protected reads, writes, denials, approvals, support access, and consequential AI/tool activity
- exposure surfaces:
  - browser UI actions for sign-in shell, context selection, profile/settings, admin users, invitations, roles/memberships, access review, support access, and admin audit
  - HTTP APIs including `/api/me` and protected admin routes
  - workflow surface for invitation acceptance and delivery lifecycle
  - timer surface for expiry/reminders
  - consumer surface for email delivery/outbox
  - view/query surfaces for user directory, memberships, invitations, access reviews, and audit
  - scoped agent tools for read/evidence, draft, summarize, and recommend operations only; committing risky admin actions remains human-approved unless a policy grants a narrow autonomous path

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
- UI:
  - `../55-ui/screens-and-navigation.md`
  - `../55-ui/frontend-api-contracts.md`
  - `../55-ui/ai-first-surfaces.md`
- traceability:
  - `../70-traceability/capability-to-behavior-map.md`
  - `../70-traceability/operating-model-to-behavior-map.md`
