---
name: akka-saas-invitation-onboarding
description: Implement complete mandatory SaaS email-invite onboarding on Akka, including Invitation state, InvitationWorkflow, email delivery/outbox consumer, expiry/reminder timers, InvitationView, admin APIs/UI, and lifecycle tests.
---

# Akka SaaS Invitation Onboarding

Use this skill whenever a generated SaaS app needs user onboarding, tenant/customer admin bootstrap, membership activation, invite delivery, resend, revoke/cancel, expiry, or first-login invitation acceptance.

This is a focused foundation skill. Pair it with:
- `core-saas-foundation` for mandatory SaaS baseline semantics
- `akka-basic-user-admin` for account, membership, role, capability, and admin APIs
- `akka-workos-user-auth` for WorkOS/AuthKit, JWT validation, `/api/me`, and first-login linking
- `akka-workflows` and `akka-workflow-component` for `InvitationWorkflow`
- `akka-consumers` for email delivery/outbox side effects
- `akka-timed-actions` and `akka-timers-scheduling` for expiry/reminders
- `akka-views` and `akka-view-query-patterns` for `InvitationView`
- HTTP and web UI skills for admin endpoints and screens

## Required reading

Read these first when implementing invitation onboarding:
- `../core-saas-foundation/SKILL.md`
- `../akka-basic-user-admin/SKILL.md`
- `../akka-workos-user-auth/SKILL.md`
- `../akka-workflows/SKILL.md`
- `../akka-workflow-component/SKILL.md`
- `../akka-consumers/SKILL.md`
- `../akka-timed-actions/SKILL.md`
- `../akka-timers-scheduling/SKILL.md`
- `../akka-views/SKILL.md`
- `../../docs/core-saas-identity-tenancy-admin.md`
- `../../docs/security-workos-auth-and-admin.md`

## Mandatory outcome

Complete email-invite onboarding is mandatory for generated SaaS foundations:
- production readiness requires configured email delivery or an accepted provider decision;
- local/dev/test must use an explicit safe captured-outbox adapter when external delivery is unavailable;
- missing provider configuration blocks production readiness rather than silently omitting onboarding;
- failed delivery is visible to authorized admins and auditable;
- first-login linking and `/api/me` activation require a valid invitation, invite token, acceptance context, or explicit membership policy.

## Core components

Recommended Akka substrate:

| Component | Responsibility |
|---|---|
| `Invitation` entity or audit-grade record | Authoritative invite intent, target email/scope/roles, status, token hash or acceptance context, expiry, delivery status, delivery attempts, idempotency key, and audit metadata. Use Event Sourced Entity when lifecycle history is first-class; use a Key Value Entity only when AdminAuditEvent captures immutable lifecycle facts. |
| `InvitationWorkflow` | Durable invite orchestration: create local account/membership intent, enqueue delivery, wait for delivery/acceptance, handle resend/revoke/expire, link WorkOS subject on acceptance, activate membership, and emit audit facts. |
| Email delivery/outbox `Consumer` | Consumes invitation delivery commands/events, sends through configured provider in production, captures into local/dev/test outbox, records provider ids, delivery status, delivery attempts, and failure details. |
| Expiry/reminder `TimedAction` | Schedules invite expiry and optional reminder checks with stable names like `invitation-expire-<invitationId>` and `invitation-reminder-<invitationId>`. Obsolete timers must return done/no-op. |
| `InvitationView` | Scoped admin read model for invitation list/search by tenant/customer, target email, status, delivery status, expiry, created time, inviter, and scope. Never expose raw tokens. |
| Admin endpoints/UI | Invite, resend, revoke/cancel, view status, delivery failure detail, acceptance help, and audit links; enforce tenant/customer/admin scope server-side. |

## Lifecycle rules

### Create invite

1. Authorize caller for target SaaS Owner, Tenant, or Customer scope.
2. Normalize target email and validate requested roles/capabilities are within caller authority.
3. Create or reuse an idempotent local Account and target Membership in `INVITED` state.
4. Create an `Invitation` with token hash or acceptance context, status, expiry, idempotency key, delivery status, delivery attempts, and audit metadata.
5. Start `InvitationWorkflow` and enqueue email delivery/outbox work.
6. Schedule expiry and optional reminder timers.
7. Return admin-safe invite status; never return raw token except to the delivery adapter that composes the email.

### Delivery

- Production sends through the configured provider and records provider/message ids.
- Local/dev/test writes to a captured outbox that tests and developers can inspect.
- Delivery attempts are idempotent by invitation id plus attempt/retry key.
- Failed delivery sets visible `DELIVERY_FAILED` state, records last error, and emits AdminAuditEvent.
- Admins can retry/resend according to policy; resend may reuse or rotate acceptance context but must invalidate obsolete raw links when policy requires rotation.

### Resend

- Require current invite status to be resendable, usually `SENT`, `PENDING_DELIVERY`, or `DELIVERY_FAILED`, not `ACCEPTED`, `EXPIRED`, or `REVOKED`.
- Reauthorize caller and target scope.
- Preserve idempotency for repeated resend requests.
- Record resend count, actor, reason, delivery attempts, and audit facts.
- Replace reminder/expiry timers only when the expiry policy changes.

### Revoke/cancel

- Require caller authority over the target scope.
- Mark invitation `REVOKED`, cancel or suspend pending membership activation, and emit audit facts.
- Delete reminder timers as housekeeping; leave expiry handler safe if it still fires.
- `/api/me` and acceptance commands must reject revoked acceptance contexts.

### Expire

- `TimedAction` calls `InvitationWorkflow` or the authoritative Invitation component.
- The target component checks invitation id, tenant/customer scope, and current status.
- Already accepted, already revoked, or already expired invitations are terminal done/no-op.
- Expiry prevents acceptance, updates view status, and emits audit facts.

### Accept and first-login link

1. JWT-protected `/api/me` or acceptance endpoint reads WorkOS subject and email claims.
2. Backend locates a valid invitation by token/acceptance context and normalized email or accepted membership policy.
3. Reject missing, expired, revoked, cross-tenant/customer, delivery-failed-without-override, or already-accepted-by-another-subject invitations.
4. Link WorkOS subject idempotently to the local Account.
5. Activate account/membership only if invitation and membership policy are still valid.
6. Emit AdminAuditEvent facts for link, acceptance, membership activation, and any denial.
7. Replayed acceptance by the same linked subject returns the existing active result; replay by another subject is forbidden.

## Duplicate and idempotency handling

- Use normalized email plus target scope plus requested membership policy as the duplicate-detection boundary.
- Repeated invite for the same pending membership should return existing invitation or perform a policy-defined resend, not create duplicate active memberships.
- Re-invite after revoke/expiry may create a new invitation version while preserving audit history.
- Re-invite an already active membership should be a documented no-op or conflict unless adding a distinct scope/role is allowed.
- All workflow commands and consumer/timer callbacks carry deterministic command ids or idempotency keys.

## Authorization and audit

Every command, query, workflow action, consumer side effect, and timer callback must preserve or reload:
- actor AuthContext or system/service actor with explicit capability;
- target scope type plus `tenantId`/`customerId` when applicable;
- requested roles/capabilities and policy references;
- audit correlation id or invitation id.

Audit these facts:
- invite create, delivery attempt, delivery success/failure, resend, revoke/cancel, expiry, acceptance, replayed acceptance, duplicate handling, forbidden attempt, WorkOS subject link, membership activation, and admin status views where required.

## Admin API and UI surface

Typical protected endpoints:

```text
GET  /api/admin/invitations
GET  /api/admin/invitations/{invitationId}
POST /api/admin/invitations
POST /api/admin/invitations/{invitationId}/resend
POST /api/admin/invitations/{invitationId}/revoke
GET  /api/admin/invitations/{invitationId}/audit
```

UI expectations:
- invitation list/search with status, delivery status, delivery attempts, expiry, target email, scope, inviter, and last error summary;
- create invite form with role/scope validation and capability-gated controls;
- resend and revoke actions with confirmation and visible audit result;
- delivery failure queue or filter so admins can repair onboarding;
- no raw invite tokens in admin lists, logs, browser storage, or audit views.

## View guidance

`InvitationView` should support separate scoped query methods rather than broad optional-filter `OR` queries:
- by tenant/customer scope and status;
- by tenant/customer scope and delivery status;
- by normalized target email within authorized scope;
- expiring soon or expired within authorized scope;
- by inviter/actor when audit or operational triage requires it.

Include tenant/customer scope columns in every scoped row and endpoint query. Endpoint authorization must reject cross-scope access before querying or must constrain the query with the authorized AuthContext.

## Testing checklist

Add unit and integration tests for:
- invite creation success with local account/membership intent;
- production readiness failure when delivery provider configuration is missing;
- local/dev/test captured outbox behavior;
- email delivery success, provider id recording, delivery status, delivery attempts, and audit facts;
- delivery failure visibility to admins and audit events;
- repeated invite and duplicate email handling in the same scope;
- distinct invitations for the same email in different Tenant/Customer scopes;
- resend invite idempotency and status restrictions;
- revoke invite and forbidden acceptance after revoke;
- expire timer behavior and obsolete timer no-op;
- acceptance success and WorkOS subject link;
- replayed acceptance by same subject is idempotent;
- replayed acceptance by another subject is forbidden;
- expired invite cannot activate membership;
- cross-tenant invite/accept/list access is forbidden;
- non-admin or out-of-scope admin cannot invite;
- disabled inviter cannot create/resend/revoke invites;
- role escalation is rejected;
- `InvitationView` query authorization, pagination, redaction, and no raw-token leakage;
- AdminAuditEvent facts for create, delivery, resend, revoke, expire, accept, duplicate, denied, and link events.

## Anti-patterns

Avoid:
- treating invite email delivery as optional for production;
- creating privileged users from WorkOS claims without a valid invitation or membership policy;
- exposing raw invite tokens outside the delivery/acceptance boundary;
- hiding delivery failures only in logs;
- putting email provider keys in frontend env files;
- implementing resend/revoke as frontend-only state;
- relying on timer deletion instead of making expiry commands idempotent;
- building unscoped invitation list queries and filtering only in the browser.
