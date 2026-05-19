# Foundation Onboarding, Admin Audit, Support Access, and Billing Boundary

## Purpose

Define the mandatory foundation details that make the DCA vertical reference current with secure AI-first SaaS onboarding and administration expectations. This file refines `../10-capabilities/01-secure-tenant-user-foundation.md` and is authoritative for future realization slices that touch invitations, admin operations, support access, or SaaS Owner billing/subscription metadata.

## Invitation and onboarding lifecycle

Complete email-invite onboarding is required for SaaS Owner, Tenant, Customer, and user onboarding.

Required lifecycle:

1. An authorized admin creates an invitation for a target SaaS Owner, Tenant, or Customer scope.
2. Backend normalizes target email, validates requested roles/capabilities, creates or reuses local `Account` and `Membership` intent in `INVITED` state, and stores an `Invitation` with token hash or acceptance context, expiry, delivery status, delivery attempts, idempotency key, inviter, reason, and policy references.
3. `InvitationWorkflow` enqueues email delivery through Resend (resend.com) by default for production or an explicit safe captured-outbox adapter for local/dev/test; alternate production providers require an accepted override decision.
4. Delivery success or failure updates `InvitationView` and emits `AdminAuditEvent`; failed delivery remains visible to scoped admins.
5. Admins may resend or revoke only within their authority boundary. The resend-invite action is idempotent and records resend count; revoke prevents later acceptance and suspends pending membership activation.
6. Expiry/reminder timers are stable and retry-safe. Expiry of already accepted, revoked, or expired invitations is a terminal no-op.
7. First login through `/api/me` or an acceptance endpoint validates the WorkOS JWT plus invitation token/acceptance context, target email, tenant/customer scope, invitation status, membership policy, and expiry before linking the WorkOS subject.
8. Acceptance activates the local account and scoped membership only when invitation and membership policy are still valid; replay by the same linked subject is idempotent, and replay by another subject is forbidden.

Privileged self-registration from WorkOS claims alone is forbidden. Unknown identities may receive pending-invite/not-invited responses, but they must not gain privileged roles, Tenant access, Customer access, support access, or DCA operational authority without a valid invitation, accepted membership policy, or bounded audited bootstrap path.

## Administration operations and audit

Administration is capability-driven and auditable; routes and structured surfaces are exposure details only.

Required admin operations:

- invite, resend, revoke/cancel, expire, accept, and view invitation status;
- list/search users through scoped `UserDirectoryView`, not only by known internal ids;
- list/search memberships through `MembershipView` by scope, role, status, support-access expiry, and last-admin risk;
- assign, replace, and remove roles only within caller authority;
- add, suspend, reactivate, and remove memberships with last-admin protection;
- disable and reactivate accounts;
- reset/relink WorkOS identity subject only under explicit policy and audit;
- grant, revoke, expire, and review support-access memberships;
- search `AdminAuditView` and `AccessReviewQueueView` for stale invitations, dormant admins, risky memberships, support-access expiry, delivery failures, and last-admin risks.

Every consequential admin action and protected admin read emits `AdminAuditEvent` or work-trace facts with actor account, selected `AuthContext`, target account/resource, tenant/customer scope, action type, permission/capability checked, policy/decision-card link when present, reason, correlation id, and redaction marker. Raw invitation tokens, JWTs, provider secrets, and unrelated tenant/customer data are never stored in browser-visible views, logs, or audit summaries.

## Support access

Support access is not SaaS Owner super-admin access and is not impersonation by default.

Rules:

- only a Tenant Admin can create a Tenant-scoped support-access membership for SaaS Owner personnel;
- the membership is assigned to a real WorkOS-authenticated human/email, has a reason/purpose, is time-limited by default, and is visible to Tenant Admins;
- permissions are narrowed to the approved support purpose and may not exceed the Tenant Admin's grant authority;
- use, expiry, extension, revocation, and denied support attempts are audited;
- support-access grants/extensions are reviewed by `SupportAccessReviewAgent` and may require decision-card approval for high risk, unusual duration, broad scope, or SaaS Owner email targets;
- expiry timers and access-review queues surface upcoming expiry and stale grants;
- SaaS Owner billing or platform roles alone never authorize Tenant application-data reads.

If future impersonation is approved, it must be separately modeled with visible actor/effective-user context, reason, start/use/end audit events, and high-risk action exclusions.

## SaaS Owner billing/subscription boundary

The DCA foundation includes SaaS Owner to Tenant billing/subscription metadata only. Tenant-to-Customer billing, copier contract pricing, supply costs, service charges, meter billing, and customer invoicing are DCA domain capabilities and remain outside the core foundation.

SaaS Owner billing access may include billing-safe Tenant metadata such as Tenant display/legal name, billing contact fields, plan, subscription status, payment status, invoice/payment provider references, and explicitly defined platform billing metrics. It must not expose Tenant application data, Customer service data, DCA telemetry, device records, AI work traces, supply/service/billing-review payloads, user settings, or non-billing profile details.

High-impact billing actions such as suspension, reactivation, cancellation, grace-period extension, or material plan changes require policy evaluation, decision-card approval when configured, and audit. Tenant entitlement read models may expose only the minimum service-status information needed by Tenant-scoped services to enforce plan limits.

## Readiness implications

Future generation remains blocked until invitation lifecycle, Resend production delivery or local captured-outbox behavior, scoped admin read models, support-access lifecycle, billing-safe data boundary, admin audit facts, and tests for no privileged self-registration are defined in implementation specs and acceptance tests; alternate production providers require an accepted override decision.
