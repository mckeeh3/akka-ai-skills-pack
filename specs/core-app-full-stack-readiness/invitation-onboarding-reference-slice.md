# Invitation Onboarding Reference Slice

## Purpose

Define the implementation-ready full-core invitation onboarding slice for generated secure AI-first SaaS applications. This slice closes the current gap where invitations are handled as direct account creation plus synchronous email sending, and replaces it with a durable Akka reference contract for invite intent, email delivery/outbox, acceptance, expiry, resend, revoke, scoped admin views, audit, and tests.

This is a specification slice only. Follow-up code tasks should implement the components and tests without re-deciding architecture.

## Scope

Included:

- SaaS Owner, Tenant, and Customer scoped invitation creation.
- Local authorization intent for account and membership in `INVITED` state.
- Durable `Invitation` lifecycle with token hash or acceptance context; raw token only crosses the delivery/acceptance boundary.
- `InvitationWorkflow` orchestration for create, delivery, resend, revoke, expire, accept, and membership activation.
- Resend production email delivery through a reusable email/outbox service.
- Local/dev/test captured outbox behavior.
- Delivery success/failure visibility and repair/resend path.
- Expiry and optional reminder timers.
- Scoped `InvitationView` and invitation audit access.
- Protected admin HTTP API contracts and User Admin invitation surface contract.
- Unit/integration/security tests for lifecycle, authorization, idempotency, audit, tenant isolation, raw-token redaction, and disabled inviter denial.

Excluded and deferred to `user-admin-reference-slice.md`:

- Full membership lifecycle after activation: suspend/reactivate/remove, role replace/remove, support access, and last-admin enforcement beyond invite-time role escalation checks.
- Complete `/api/me`, profile/settings, and context-selection DTOs.
- Full `UserDirectoryView`, `MembershipView`, `AdminAuditView`, and `AccessReviewQueueView` definitions except references needed by invitation acceptance and audit.
- Agent-assisted admin recommendation behavior.

## Capability contract

### `core.invitations.manage`

- type: command/workflow/scheduled/system-side-effect capability
- actors/callers:
  - SaaS Owner Admin inviting SaaS Owner users or initial Tenant Admins.
  - Tenant Admin inviting Tenant employees, Tenant support-access users, Customer Admins, or Customer users within the Tenant boundary.
  - Customer Admin inviting Customer users within the Customer boundary.
  - `InvitationWorkflow` as the durable capability carrier.
  - `EmailDeliveryConsumer` as a system principal for delivery side effects.
  - `InvitationExpiryTimedAction` as a system principal for expiry/reminder callbacks.
- AuthContext:
  - human commands require active account and selected membership with invitation-management capability in the target scope.
  - system callbacks carry `invitationId`, target scope, correlation id, and a service-principal capability such as `system.invitations.deliver` or `system.invitations.expire`.
  - cross-scope or inactive/disabled actor commands are rejected before mutation.
- scope fields:
  - `scopeType`: `SAAS_OWNER`, `TENANT`, or `CUSTOMER`.
  - `tenantId`: required for Tenant and Customer scopes.
  - `customerId`: required for Customer scopes.
- side effects:
  - create local account intent when missing.
  - create target membership intent in `INVITED` state.
  - enqueue outbox email delivery.
  - schedule expiry/reminder timers.
  - link WorkOS subject and activate membership on valid acceptance.
  - emit AdminAuditEvent facts for every allowed or denied consequential transition.
- idempotency:
  - create uses caller-supplied idempotency key plus normalized email, scope, and requested membership policy.
  - resend uses invitation id plus resend command id.
  - delivery attempts use invitation id plus delivery attempt id.
  - accept uses acceptance context plus WorkOS subject and is replay-safe for the same linked subject.
  - timer callbacks are terminal no-op for obsolete states.

### `core.invitations.read`

- type: read/evidence capability
- actors/callers: SaaS Owner Admin/Auditor, Tenant Admin/Auditor, Customer Admin/Auditor within scope, User Admin surface, Access Review surface, support/debug endpoints where authorized.
- AuthContext: active membership with invitation read capability in the queried scope.
- query surfaces: invitation list, detail, delivery failure queue, expiring invitations, target email lookup, audit links.
- redaction: raw invitation token, token hash, provider secret, private WorkOS/provider ids, unrelated tenant/customer data, and full delivery payload are never returned.
- audit: ordinary list/detail reads may be sampled or audited according to app policy; access to failure details, audit detail, cross-support contexts, or sensitive operational metadata must emit read/audit facts.

### `core.invitations.accept`

- type: authenticated acceptance/linking capability
- actors/callers: signed-in browser user through WorkOS/AuthKit, acceptance endpoint, `/api/me` activation path when supplied an acceptance context.
- AuthContext: WorkOS-authenticated subject and email; local account may still be unlinked or invited.
- required input: invitation token or opaque acceptance context plus WorkOS JWT identity.
- side effects: link WorkOS subject to local Account if valid, activate invited Membership, mark Invitation accepted, delete/obsolete reminder timers, emit audit facts.
- denials: missing/expired/revoked/cross-scope/accepted-by-other-subject/delivery-failed-without-override/inactive-membership-policy errors are explicit and auditable.

## Domain model

### Invitation aggregate/state

Use an Event Sourced Entity for the reference implementation because invitation history is audit-grade and state transitions matter. A Key Value Entity is acceptable only in narrower generated apps when AdminAuditEvent captures immutable lifecycle facts and the slice is explicitly labeled narrower than full-core reference.

Recommended package placement for reference code:

- `com.example.domain.security.Invitation`
- `com.example.domain.security.InvitationCommand`
- `com.example.domain.security.InvitationEvent`
- `com.example.application.security.InvitationEntity`

Recommended `Invitation.State` fields:

| Field | Notes |
|---|---|
| `invitationId` | Stable component/entity id. |
| `normalizedEmail` | Lowercase normalized target email; original display email may be stored separately if needed. |
| `scopeType` | `SAAS_OWNER`, `TENANT`, or `CUSTOMER`. |
| `tenantId` | Required for Tenant and Customer scopes. |
| `customerId` | Required for Customer scopes. |
| `requestedRoles` | Foundation roles/capabilities requested for the target membership. |
| `membershipPolicy` | Target membership type, activation policy, support-access flags if any. |
| `accountId` | Local Account intent id. |
| `membershipId` | Target Membership intent id. |
| `status` | `PENDING_DELIVERY`, `SENT`, `DELIVERY_FAILED`, `ACCEPTED`, `EXPIRED`, `REVOKED`. |
| `deliveryStatus` | `NOT_ENQUEUED`, `QUEUED`, `CAPTURED`, `SENT`, `FAILED`. |
| `deliveryAttempts` | Count plus last attempt id/timestamp. |
| `providerMessageIds` | Resend ids or captured outbox ids; admin-safe. |
| `lastDeliveryError` | Redacted error code/message. |
| `acceptanceContextId` | Opaque context id safe to persist; not a raw token. |
| `tokenHash` | Hash of raw invite token; never projected to views. |
| `expiresAt` | Expiry instant. |
| `acceptedAt`, `acceptedByWorkosSubject` | Set on valid acceptance. |
| `revokedAt`, `revokedByAccountId`, `revokeReason` | Set on revoke. |
| `resendCount` | Incremented on successful resend request. |
| `createdByAccountId`, `createdAt`, `reason` | Audit metadata. |
| `idempotencyKey` | Create dedupe key. |
| `correlationId` | Trace/audit correlation. |
| `policyRefs` | Any invite, expiry, role-escalation, or delivery policy refs. |

Recommended events:

- `InvitationCreated`
- `InvitationDeliveryQueued`
- `InvitationDeliveryCaptured`
- `InvitationDeliverySucceeded`
- `InvitationDeliveryFailed`
- `InvitationResendRequested`
- `InvitationRevoked`
- `InvitationExpired`
- `InvitationAccepted`
- `InvitationAcceptanceDenied`
- `InvitationDuplicateObserved`
- `InvitationRoleEscalationDenied`
- `InvitationCommandRejected`

Recommended command handlers:

- `create(CreateInvitation)` validates scope, roles, expiry, duplicate boundary, idempotency, and raw-token hash.
- `queueDelivery(QueueDelivery)` transitions to queued/pending delivery.
- `recordDeliveryAttempt(RecordDeliveryAttempt)` idempotently records capture/send/failure.
- `requestResend(RequestResend)` validates status and increments resend metadata.
- `revoke(RevokeInvitation)` rejects terminal states, otherwise revokes and emits audit event.
- `expire(ExpireInvitation)` returns terminal no-op for accepted/revoked/expired states.
- `accept(AcceptInvitation)` validates context, email/subject policy, expiry, status, and idempotency.

## Workflow contract

### `InvitationWorkflow`

Use `InvitationWorkflow` as a durable orchestration workflow, not as the sole source of authority. The authoritative invitation state remains in `InvitationEntity`; the workflow carries progress, step retries, side-effect ids, correlation, and system actor context.

Recommended state fields:

- `workflowId` equal to or derived from `invitationId`.
- `invitationId`, `accountId`, `membershipId`.
- target scope and normalized email.
- `createdByAuthContext` or immutable actor/scope snapshot.
- `status`: `CREATING_INTENT`, `ENQUEUEING_DELIVERY`, `WAITING_FOR_DELIVERY`, `DELIVERY_FAILED`, `WAITING_FOR_ACCEPTANCE`, `ACCEPTED`, `REVOKED`, `EXPIRED`, `FAILED`.
- delivery attempt ids and retry budget.
- timer names.
- idempotency/correlation ids.
- audit event ids emitted by steps when available.

Recommended commands and transitions:

1. `start(StartInvitationWorkflow)`
   - assumes admin endpoint or service has already authorized create.
   - creates/reuses Account intent and Membership intent in `INVITED` state through idempotent component commands.
   - calls `InvitationEntity.create`.
   - transitions to `enqueueDelivery`.
2. `enqueueDelivery`
   - records delivery queued on `InvitationEntity`.
   - emits an `EmailOutboxMessage` or calls an outbox component with deterministic id.
   - schedules expiry/reminder timer before returning waiting state.
3. `recordDeliveryResult(DeliveryResult)`
   - updates `InvitationEntity` with captured/sent/failed result.
   - if failed, transitions to `DELIVERY_FAILED` but leaves invite resendable.
   - if sent/captured, transitions to `WAITING_FOR_ACCEPTANCE`.
4. `requestResend(ResendInvitation)`
   - reauthorizes human actor against current scope.
   - rejects terminal statuses.
   - rotates acceptance context when policy requires it; otherwise reuses safe context.
   - enqueues a new outbox message with deterministic attempt id.
5. `revoke(RevokeInvitation)`
   - reauthorizes human actor.
   - marks Invitation revoked, suspends or cancels invited membership activation, deletes reminder timer as housekeeping, and remains no-op safe if expiry later fires.
6. `expire(ExpireInvitation)`
   - called by timed action with service principal.
   - terminal no-op for accepted/revoked/expired states.
   - marks Invitation expired and prevents membership activation.
7. `accept(AcceptInvitation)`
   - validates WorkOS subject/email and acceptance context.
   - links local Account to WorkOS subject idempotently.
   - activates Membership only when invitation and membership policy remain valid.
   - marks invitation accepted and emits audit facts.

Retry/idempotency expectations:

- Workflow steps may retry; downstream commands must include stable command ids.
- Email delivery is not performed directly in workflow code; it is delegated through durable outbox/consumer so retry does not duplicate sends without attempt ids.
- Acceptance is safe to replay by the same WorkOS subject and forbidden for another subject.
- Revocation and expiry are terminal idempotent no-ops after prior terminal state.

## Email outbox and Resend delivery

### Components

- `EmailOutboxMessage` domain record or outbox entity row.
- `EmailOutboxEntity` or topic-producing workflow step for durable delivery work.
- `InvitationEmailDeliveryConsumer` consuming queued outbox messages.
- `ResendEmailService` adapter for production.
- `CapturedEmailOutbox` adapter/storage for local/dev/test.

Production provider is fixed: Resend (`resend.com`). Do not introduce provider selection. Missing production Resend configuration blocks production readiness.

Backend-only configuration:

- `RESEND_API_KEY`
- `RESEND_FROM_EMAIL` or `INVITE_EMAIL_FROM`
- `INVITE_EMAIL_SUBJECT`
- optional reply-to/domain allow-list settings

Delivery message fields:

- `messageId` or `outboxId`
- `messageType`: `INVITATION`
- `invitationId`
- `deliveryAttemptId`
- `tenantId`, `customerId`, `scopeType`
- normalized target email and display email
- invite URL containing raw token or one-time acceptance context; raw value is generated at delivery boundary only
- template variables: inviter name, organization name, role summary, expiry time, support contact
- correlation id and audit metadata

Consumer behavior:

- source: outbox entity events or topic messages; choose one in implementation and keep idempotency key stable.
- idempotency key: `invitationId + deliveryAttemptId`.
- production: send through Resend and record provider message id.
- local/dev/test: capture message body/link in safe captured outbox and record captured outbox id.
- delivery failure: record redacted error on Invitation, emit AdminAuditEvent, and leave invite visible as `DELIVERY_FAILED`/resendable.
- invalid/stale messages: audited terminal `done()`/`ignore()`; do not retry forever.
- transient provider failures: fail handler or schedule retry according to retry budget, then record terminal failure when exhausted.

## Timed actions

### `InvitationExpiryTimedAction`

- scheduled by `InvitationWorkflow` after invitation creation.
- timer name: `invitation-expire-<invitationId>`.
- payload: `invitationId`, target scope, `expiresAt`, correlation id, system principal capability.
- handler calls `InvitationWorkflow.expire` or `InvitationEntity.expire` with the same payload.
- accepted/revoked/already-expired/not-found states return done/no-op and emit audit only when policy requires observable stale callback facts.
- cross-scope or mismatched payload is rejected or ignored safely by the authoritative target component.

### `InvitationReminderTimedAction` (optional but reference-ready)

- timer name: `invitation-reminder-<invitationId>`.
- sends or queues a reminder only for resendable, unaccepted, unrevoked, unexpired invitations when reminder policy allows.
- stale terminal states return done/no-op.
- reminder delivery uses the same outbox/consumer path as invites; it must not bypass Resend/captured outbox rules.

## Views

### `InvitationView`

Source: `InvitationEntity` events for audit-grade state, or `InvitationWorkflow` updates if implementation chooses workflow-state projection. Prefer entity events for complete lifecycle history and durable current row.

Row fields:

| Field | Notes |
|---|---|
| `invitationId` | Row id. |
| `normalizedEmail` | Search key; redacted according to caller if needed. |
| `scopeType`, `tenantId`, `customerId` | Required for backend filtering. |
| `requestedRoles` | Browser-safe role names. |
| `membershipId`, `accountId` | Returned only when caller may view user details. |
| `status` | Invitation lifecycle status. |
| `deliveryStatus` | Current delivery state. |
| `deliveryAttempts`, `resendCount` | Operational visibility. |
| `lastDeliveryErrorSummary` | Redacted. |
| `expiresAt`, `createdAt`, `acceptedAt`, `revokedAt` | Sort/filter keys. |
| `createdByAccountId`, `createdByDisplayName` | Inviter metadata. |
| `revokeReason` | Redacted or omitted outside authorized admin detail. |
| `correlationId`, `auditEventIds` | Link to audit/trace surfaces. |
| `canResend`, `canRevoke` | Convenience flags computed from status/policy for UI, not authorization. |

Forbidden fields:

- raw token
- token hash
- Resend API details/secrets
- full email body containing the raw link
- unrelated tenant/customer metadata

Query methods should be separate explicit access paths, avoiding optional-filter `OR` patterns:

- `listByTenantAndStatus(tenantId, status, pageToken/pageSize)`
- `listByCustomerAndStatus(tenantId, customerId, status, pageToken/pageSize)`
- `listByTenantAndDeliveryStatus(tenantId, deliveryStatus, pageToken/pageSize)`
- `listByCustomerAndDeliveryStatus(tenantId, customerId, deliveryStatus, pageToken/pageSize)`
- `findByTenantEmail(tenantId, normalizedEmail)`
- `findByCustomerEmail(tenantId, customerId, normalizedEmail)`
- `listExpiringByTenant(tenantId, expiresBefore, pageToken/pageSize)`
- `listExpiringByCustomer(tenantId, customerId, expiresBefore, pageToken/pageSize)`
- `getScopedInvitation(invitationId, tenantId/customerId/scopeType)` through endpoint authorization plus view query or direct entity read.

Query constraints:

- Include tenant/customer scope columns in every protected query.
- If a query uses `ORDER BY`, every ordered column must also appear in the `WHERE` clause according to Akka View constraints.
- Endpoint authorization must constrain queries with backend AuthContext, never browser-selected filters alone.

## Admin HTTP API contract

All routes require WorkOS JWT authentication plus backend authorization from local AuthContext. All mutation requests include idempotency/correlation ids and target scope. Error responses use browser-safe codes and never disclose token or cross-tenant existence.

```text
GET  /api/admin/invitations
GET  /api/admin/invitations/{invitationId}
POST /api/admin/invitations
POST /api/admin/invitations/{invitationId}/resend
POST /api/admin/invitations/{invitationId}/revoke
GET  /api/admin/invitations/{invitationId}/audit
POST /api/invitations/accept
```

### `POST /api/admin/invitations`

Request:

- `idempotencyKey`
- `scopeType`
- `tenantId`, when needed
- `customerId`, when needed
- `email`
- `displayName`, optional
- `requestedRoles`
- `reason`
- `expiresAt` or policy name
- `sendEmail`: default true; false allowed only for explicit captured/manual development flows, not production omission

Response:

- `invitationId`
- `status`
- `deliveryStatus`
- `expiresAt`
- `targetEmail`
- `scope`
- `requestedRoles`
- `resendEligibility`
- `revokeEligibility`
- `auditCorrelationId`

### `POST /api/admin/invitations/{invitationId}/resend`

Request: `idempotencyKey`, `reason`, optional `rotateAcceptanceContext` when policy allows.

Response: updated invite status, delivery attempt summary, expiry, audit correlation id.

Reject when accepted, revoked, expired, out of scope, disabled caller, role escalation, or resend policy denies.

### `POST /api/admin/invitations/{invitationId}/revoke`

Request: `idempotencyKey`, `reason`.

Response: terminal revoked summary and audit correlation id.

Replay of same revoke command returns current revoked result.

### `POST /api/invitations/accept`

Request: `acceptanceContext` or token, optional selected target scope if the link can resolve multiple memberships.

Caller: WorkOS-authenticated browser user.

Response:

- `accountId`
- `membershipId`
- `scope`
- `membershipStatus: ACTIVE`
- `next`: recommended route/context for `/api/me`

Denials:

- `INVITATION_NOT_FOUND_OR_FORBIDDEN`
- `INVITATION_EXPIRED`
- `INVITATION_REVOKED`
- `INVITATION_ALREADY_ACCEPTED_BY_OTHER_SUBJECT`
- `INVITATION_DELIVERY_NOT_VALID_FOR_ACCEPTANCE`
- `WORKOS_EMAIL_MISMATCH`
- `MEMBERSHIP_POLICY_DENIED`

## User Admin UI surface contract

The User Admin functional-agent invitation surfaces consume the admin APIs and view DTOs. The UI never authorizes; backend responses drive action availability.

Required surfaces:

- Invitation list/search:
  - filters: scope, email, invitation status, delivery status, expiring soon, inviter.
  - rows: target email, scope, requested role summary, status, delivery status, attempts, expiry, inviter, last error summary, audit link.
- Create invitation form:
  - email, scope, role selection, reason, expiry policy.
  - backend validation errors mapped to field or form errors.
  - role escalation and disabled inviter errors visible without exposing policy internals.
- Invitation detail:
  - lifecycle timeline, delivery attempts, resend/revoke actions, acceptance status, audit links.
  - no raw token or email body display except captured outbox developer/test utility outside production admin UI.
- Delivery failure queue:
  - filtered view of `DELIVERY_FAILED` invites with repair/resend action.
- Acceptance recovery:
  - signed-in user sees expired/revoked/already-accepted/no-access states with safe next steps.

## Authorization and audit requirements

Authorize every protected route, component command, view query, workflow action, consumer side effect, and timer action.

Required denials:

- unauthenticated browser caller.
- signed-in caller with no local active account.
- disabled inviter account.
- inactive/suspended/removed membership.
- actor lacks invitation manage/read capability in target scope.
- role escalation beyond actor authority.
- SaaS Owner direct Tenant data access without explicit Tenant-scoped support membership.
- Tenant Admin attempting another Tenant or unrelated Customer.
- Customer Admin attempting Tenant-level or unrelated Customer invites.
- cross-tenant list/detail/resend/revoke/accept.

Audit facts:

- invite create requested/succeeded/denied.
- duplicate invite observed and outcome.
- delivery queued, captured, sent, failed, retried.
- resend requested/succeeded/denied.
- revoke requested/succeeded/denied.
- expiry executed/no-op when material.
- acceptance requested/succeeded/denied/replayed.
- WorkOS subject linked.
- membership activated.
- view/detail access for sensitive failure/audit detail when required.

Audit event minimum fields:

- `auditEventId`, `correlationId`, `actionType`
- actor account id and selected AuthContext or system principal
- target `invitationId`, `accountId`, `membershipId`, normalized email hash/display-safe email
- scope type, tenant id, customer id
- requested roles/capabilities
- result: `ALLOWED`, `DENIED`, `NO_OP`, `FAILED`
- reason/error code
- policy refs
- provider/outbox attempt id where relevant
- timestamp

## Duplicate and idempotency policy

Duplicate boundary:

```text
normalizedEmail + scopeType + tenantId + customerId + requested membership policy
```

Rules:

- Repeat create with same idempotency key returns the same invitation result.
- Repeat create with different key but same duplicate boundary and pending/resendable invite returns existing invite or policy-defined resend outcome; it must not create duplicate invited memberships.
- Re-invite after `REVOKED` or `EXPIRED` may create a new invitation version with a new acceptance context while preserving audit history.
- Re-invite an already active membership is a no-op or documented conflict unless adding a distinct scope/role is allowed by the future membership slice.
- Resend replay by same command id returns same attempt/result.
- Acceptance replay by same linked WorkOS subject returns active membership result.
- Acceptance replay by another WorkOS subject is forbidden and audited.
- Expiry/revoke after terminal state returns done/no-op.

## Implementation order for follow-up code tasks

1. Domain records and pure validation helpers:
   - `Invitation`, commands/events/status enums, scope value objects, token hash/acceptance context helpers, duplicate/idempotency helpers.
2. `InvitationEntity`:
   - lifecycle command handlers and event replay tests.
3. Account/membership intent seams:
   - idempotent calls or temporary reference interfaces to create local account and invited membership; full membership lifecycle remains in later slice.
4. `InvitationWorkflow`:
   - create/account-membership intent, delivery enqueue, resend, revoke, expire, accept orchestration.
5. Email outbox and `InvitationEmailDeliveryConsumer`:
   - Resend adapter boundary, captured outbox adapter, delivery attempt recording.
6. `InvitationExpiryTimedAction` and optional reminder timed action.
7. `InvitationView`:
   - scoped query methods and redacted row DTOs.
8. Admin/acceptance HTTP endpoints:
   - JWT/request-context extraction, backend authorization, DTO/error mapping.
9. Tests:
   - unit, integration, security, view, workflow, consumer, timer, and endpoint tests.
10. UI/API contract alignment:
   - later workstream task maps these DTOs into User Admin surfaces.

## Required tests

### Domain/entity tests

- Create invitation success stores token hash/acceptance context, scope, roles, expiry, and audit metadata.
- Create rejects invalid email, missing scope ids, unsupported roles, expired expiry, and role escalation.
- Duplicate create is idempotent and does not create duplicate membership intent.
- Delivery success/failure updates status, attempt count, provider/captured id, and redacted error.
- Resend allowed from `SENT`, `PENDING_DELIVERY`, and `DELIVERY_FAILED` according to policy.
- Resend rejected from `ACCEPTED`, `EXPIRED`, and `REVOKED`.
- Revoke transitions to terminal state and replay is no-op.
- Expire transitions to terminal state and obsolete expiry callbacks are no-op.
- Accept success transitions to accepted and replay by same subject is idempotent.
- Accept by another subject is forbidden.

### Workflow tests

- Start workflow creates/reuses account and invited membership, creates invitation, enqueues delivery, and schedules expiry.
- Delivery failure keeps workflow visible/recoverable and invite resendable.
- Resend enqueues a new attempt with idempotent replay behavior.
- Revoke prevents later acceptance and cancels/suspends pending membership activation.
- Expiry prevents later acceptance.
- Acceptance links WorkOS subject and activates membership once.
- Step retry does not duplicate outbox sends or membership activation.

### Consumer/email tests

- Production readiness fails or is marked not production-ready when Resend config is missing.
- Local/dev/test captured outbox records invite without external delivery.
- Resend success records provider message id and audit fact.
- Resend/provider failure records visible delivery failure and audit fact.
- Duplicate consumer redelivery does not send duplicate provider messages for same attempt id.
- Stale or unauthorized outbox messages are audited terminal ignore/done.

### Timed-action tests

- Expiry timer fires and expires an unaccepted invitation.
- Accepted/revoked/already-expired invitations return done/no-op.
- Cross-scope or mismatched timer payload cannot affect another tenant/customer invitation.
- Reminder timer, when implemented, only queues reminder for eligible unaccepted invitation.

### View/API tests

- `InvitationView` projects create, delivery failure, resend, revoke, expire, and accept updates eventually.
- Scoped list queries return only authorized tenant/customer rows.
- Cross-tenant/customer list/detail access is forbidden or hidden according to API contract.
- Queries are paginated and redacted; no raw token or token hash appears.
- Delivery failure is visible to authorized admins.
- Invite create/resend/revoke endpoints enforce scopes and idempotency.
- Accept endpoint rejects missing, expired, revoked, already-accepted-by-other-subject, email mismatch, and cross-scope contexts.

### Security/audit tests

- Disabled inviter cannot create, resend, or revoke invites.
- Non-admin or out-of-scope admin cannot invite.
- Role escalation is rejected and audited.
- Same email can receive distinct invitations for different Tenant/Customer scopes without leakage.
- AdminAuditEvent facts exist for create, duplicate, delivery, resend, revoke, expire, accept, denied command, and WorkOS link.
- Frontend/browser-facing DTOs contain no Resend secrets, raw tokens, token hashes, or provider private details.

## Done signal for implementation

The future implementation of this slice is complete when a fresh harness can demonstrate:

- Tenant Admin creates an invite; local/dev/test captured outbox contains the email; `InvitationView` shows `SENT` or `CAPTURED` without raw token leakage.
- Delivery failure is visible and audited; resend repairs it idempotently.
- Revoke and expiry both prevent acceptance.
- A WorkOS-authenticated invitee accepts once; account is linked and membership becomes active; replay by the same subject is safe.
- Cross-tenant access, disabled inviter actions, role escalation, and replay by another subject are rejected and audited.
- Required component, endpoint, view, consumer, timed-action, and security tests pass.
