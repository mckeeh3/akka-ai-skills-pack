# User Administration Reference Slice

## Purpose

Define the implementation-ready full-core User Administration slice for generated secure AI-first SaaS applications. This slice closes the current gap where user administration is a partial DCA-style account/role sketch and turns it into a canonical foundation contract for Accounts, Profiles, Settings, Memberships, Roles, Support Access, Admin Audit, Access Review, `/api/me`, scoped admin APIs, views, authorization, and tests.

This is a specification slice only. Follow-up code tasks should implement the components and tests without re-deciding the architecture.

## Scope

Included:

- Canonical foundation roles and capability mapping for SaaS Owner, Tenant, Customer, Auditor, and app-specific role extensions.
- Account, UserProfile, UserSettings, Membership, SupportAccessGrant, AuthContext, and AdminAuditEvent component contracts.
- `/api/me`, profile/settings, context selection, and selected/default AuthContext DTOs.
- User directory, membership, invitation, admin audit, and access-review read models.
- Role replace/remove, membership add/suspend/reactivate/remove, account disable/reactivate, identity relink/reset, support-access grant/revoke/expiry, and last-admin protection.
- Scoped admin HTTP API contracts and User Admin workstream surface payloads.
- Unit, integration, security, view, endpoint, access-review, support-access, and audit tests.

Integrated from `invitation-onboarding-reference-slice.md`:

- Complete Invitation lifecycle remains a separate slice, but this slice consumes `InvitationView`, invitation status, membership activation, resend/revoke eligibility, and invitation audit links as part of User Admin.

Excluded and deferred:

- The concrete invitation entity/workflow/outbox implementation, which is specified in `invitation-onboarding-reference-slice.md`.
- Full Agent Admin runtime implementation, except User Admin may emit agent-generated access-review recommendations and decision-card-ready facts.
- SaaS Owner to Tenant subscription/billing implementation beyond preserving its boundary.
- Product-specific domain roles beyond explicit role-to-capability mapping examples.

## Capability contracts

### `core.user_admin.manage`

- type: command capability
- actors/callers:
  - SaaS Owner Admin for SaaS Owner users, Tenant records, initial Tenant Admin bootstrap, and platform-safe metadata.
  - Tenant Admin for Tenant employees, Customer organizations, Customer Admins, support-access grants, Tenant settings, and Tenant-scoped user administration.
  - Customer Admin for Customer users inside the selected Customer scope.
  - approved admin workflows and decision-card handlers when a risky action has been human-approved.
- AuthContext:
  - active account, selected active membership, account status `ACTIVE`, target scope, role/capability grants, and correlation/idempotency metadata.
  - SaaS Owner context does not grant Tenant application-data access; Tenant-created support-access membership is required for support work.
- side effects:
  - mutate account/profile/settings/membership/support-access state through components.
  - invoke invitation lifecycle for invite/resend/revoke/acceptance-adjacent actions.
  - emit AdminAuditEvent facts for allowed, denied, no-op, and failed consequential actions.
  - create or route decision-card facts for high-risk actions.
- idempotency:
  - commands include idempotency keys and stable target ids.
  - repeated role/membership/support-access commands return current state or documented conflict without duplicate audit-side effects except replay audit markers.

### `core.user_admin.read`

- type: read/evidence capability
- actors/callers: SaaS Owner Admin/Auditor, Tenant Admin/Auditor, Customer Admin/Auditor, User Admin surfaces, access-review agents/tools with explicit read-only boundaries.
- AuthContext: selected active membership with read capability in the requested scope.
- query surfaces: user directory, user detail, memberships, invitations, support access, admin audit, access-review queue, Tenant/Customer settings.
- redaction: WorkOS private subject/provider internals, raw invitation tokens/token hashes, Resend secrets, full email bodies, out-of-scope Tenant/Customer data, sensitive support evidence, and internal policy evidence outside caller authority are not returned.
- audit: list/search may be policy-sampled; sensitive detail, audit detail, support-access use, cross-support context, identity relink evidence, and agent-tool reads are auditable.

### `core.user_admin.self_context`

- type: authenticated self-service read/update capability
- actors/callers: signed-in browser user through WorkOS/AuthKit.
- AuthContext: WorkOS-authenticated subject linked to a local account or linked through a valid invitation acceptance context.
- surfaces: `/api/me`, profile, settings, selected context switch.
- side effects: context-selection update, profile/settings updates, first-login link/activation only when invitation or membership policy is valid.
- denials: disabled account, no active memberships, invalid selected context, expired/revoked invitation, cross-tenant selection, unsupported profile/settings fields.

## Canonical role and capability model

Canonical roles must be present before app-specific roles:

| Role | Scope | Baseline capabilities |
|---|---|---|
| `SAAS_OWNER_ADMIN` | SaaS Owner | `saas_owner.user.manage`, `saas_owner.tenant.read`, `saas_owner.tenant.manage`, `saas_owner.audit.read`, `saas_owner.billing_boundary.manage` |
| `TENANT_ADMIN` | Tenant | `tenant.user.read`, `tenant.user.manage`, `tenant.role.manage`, `tenant.invitation.manage`, `tenant.customer.manage`, `tenant.support_access.manage`, `tenant.audit.read`, `tenant.access_review.manage` |
| `TENANT_EMPLOYEE` | Tenant | Tenant app capabilities assigned by the generated app, no foundation admin mutation by default |
| `CUSTOMER_ADMIN` | Customer | `customer.user.read`, `customer.user.manage`, `customer.role.manage`, `customer.invitation.manage`, `customer.audit.read`, `customer.access_review.manage` |
| `CUSTOMER_USER` | Customer | Customer app capabilities assigned by the generated app, no foundation admin mutation by default |
| `AUDITOR` | SaaS Owner, Tenant, or Customer | scoped `*.audit.read`, `*.access_review.read`, and read-only admin evidence |

Rules:

- App-specific roles extend the canonical capability model; they do not replace `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, or `AUDITOR`.
- Legacy `APP_ADMIN` is allowed only as an explicit app-specific alias mapped to `SAAS_OWNER_ADMIN` or narrower capabilities in reference migrations.
- Role assignment, replacement, and removal must prevent privilege escalation and enforce last-admin protection.
- Role operations are scoped to a Membership, not globally attached to an Account.

## Component contracts

### `AccountEntity`

Recommended substrate: Key Value Entity for current state plus AdminAuditEvent history; Event Sourced Entity is acceptable when the implementation wants audit-grade account lifecycle state in the component.

State fields:

| Field | Notes |
|---|---|
| `accountId` | Stable local id. |
| `workosUserId` | Nullable until linked; never exposed in broad browser lists. |
| `normalizedEmail` | Authentication/linking hint, not authorization by itself. |
| `displayEmail` | Browser-safe display value. |
| `status` | `INVITED`, `ACTIVE`, `DISABLED`, `REMOVED`. |
| `createdAt`, `activatedAt`, `disabledAt`, `removedAt` | Lifecycle timestamps. |
| `createdByAccountId`, `disabledByAccountId`, `reactivatedByAccountId` | Audit metadata. |
| `identityLinkState` | `UNLINKED`, `LINKED`, `RELINK_PENDING`, `RELINK_BLOCKED`. |
| `lastLoginAt`, `lastActivityAt` | Optional access-review inputs. |
| `correlationId`, `policyRefs` | Trace/policy context. |

Commands:

- `createInvitedAccount(accountId, email, displayName, source, idempotencyKey)`.
- `linkWorkosSubject(accountId, workosUserId, invitationId, idempotencyKey)`.
- `activateAccount(accountId, activationReason, idempotencyKey)`.
- `disableAccount(accountId, reason, actorAuthContext, idempotencyKey)`.
- `reactivateAccount(accountId, reason, actorAuthContext, idempotencyKey)`.
- `removeAccount(accountId, reason, actorAuthContext, idempotencyKey)` for hard business removal only when policy allows.
- `requestIdentityRelink(accountId, reason, evidence, actorAuthContext, idempotencyKey)`.
- `completeIdentityRelink(accountId, newWorkosUserId, approvalRef, idempotencyKey)`.

Validation:

- disabled actors cannot mutate.
- disabling/reactivating self as the final admin is denied.
- identity relink requires explicit policy, reason, evidence, audit, and denial of privilege expansion.
- account status changes must ask `MembershipView` or a last-admin policy service before causing last-admin loss.

### `UserProfileEntity`

Recommended substrate: Key Value Entity.

Fields:

- `accountId`, `displayName`, `givenName`, `familyName`, `avatarUrl`, optional app-specific profile extensions, `updatedAt`, `updatedByAccountId`.

Commands:

- `initializeProfile(accountId, email/displayName seed, idempotencyKey)`.
- `patchSelfProfile(accountId, editableFields, idempotencyKey)`.
- `patchAdminVisibleProfile(accountId, scope, allowedFields, reason, idempotencyKey)` when a generated app explicitly allows scoped admin profile edits.

Rules:

- profile changes never grant authorization.
- profile output is redacted to the caller's scope and surface.

### `UserSettingsEntity`

Recommended substrate: Key Value Entity.

Fields:

- `accountId`, `uiMode: LIGHT | DARK`, optional app-specific settings, `updatedAt`.

Commands:

- `initializeSettings(accountId, defaultUiMode, idempotencyKey)`.
- `patchSelfSettings(accountId, uiMode, appSettingsPatch, idempotencyKey)`.

Rules:

- settings never override backend authorization, retention, policy, or audit.
- initial full-core contract requires only `uiMode` plus extension seams.

### `MembershipEntity`

Recommended substrate: Event Sourced Entity for the full-core reference because membership changes are audit-grade and last-admin/risk review depends on lifecycle transitions. KVE is acceptable only for narrower generated apps explicitly labeled as not full-core reference.

State fields:

| Field | Notes |
|---|---|
| `membershipId` | Stable membership id. |
| `accountId` | Owning Account. |
| `scopeType` | `SAAS_OWNER`, `TENANT`, or `CUSTOMER`. |
| `tenantId` | Required for Tenant and Customer scopes. |
| `customerId` | Required for Customer scope. |
| `roles` | Canonical and app-specific roles within this scope. |
| `capabilities` | Derived or materialized browser-safe capabilities. |
| `status` | `INVITED`, `ACTIVE`, `SUSPENDED`, `REMOVED`. |
| `membershipKind` | `STANDARD`, `SUPPORT_ACCESS`, `BOOTSTRAP_ADMIN`, or app-specific extension. |
| `expiresAt` | Required for support access unless policy grants exception. |
| `invitationId` | Present for invitation-created memberships. |
| `supportAccessGrantId` | Present for support access. |
| `createdAt`, `activatedAt`, `suspendedAt`, `removedAt` | Lifecycle timestamps. |
| `reason`, `policyRefs`, `decisionCardId`, `correlationId` | Governance context. |

Commands:

- `createInvitedMembership(accountId, scope, requestedRoles, invitationId, idempotencyKey)`.
- `activateMembership(membershipId, invitationId, idempotencyKey)`.
- `addMembership(accountId, scope, roles, reason, actorAuthContext, idempotencyKey)`.
- `replaceRoles(membershipId, roles, reason, actorAuthContext, idempotencyKey)`.
- `removeRole(membershipId, role, reason, actorAuthContext, idempotencyKey)`.
- `suspendMembership(membershipId, reason, actorAuthContext, idempotencyKey)`.
- `reactivateMembership(membershipId, reason, actorAuthContext, idempotencyKey)`.
- `removeMembership(membershipId, reason, actorAuthContext, idempotencyKey)`.
- `markSupportAccessExpired(membershipId, grantId, systemAuthContext, idempotencyKey)`.

Validation:

- scope ids must match scope type.
- caller capability and selected AuthContext must match the target scope.
- Tenant Admin cannot grant `SAAS_OWNER_ADMIN`; Customer Admin cannot grant Tenant-wide roles.
- role replacement/removal, membership suspension/removal, and account disable must consult last-admin protection before commit.
- removed memberships cannot be reactivated; create a new membership or documented conflict.

### `SupportAccessGrantEntity`

Recommended substrate: Event Sourced Entity or KVE with audit events; Event Sourced Entity preferred for full-core reference.

State fields:

- `grantId`, `tenantId`, optional `customerId`, `supportAccountId`, `membershipId`, `requestedByTenantAdminAccountId`, `reason`, `scopeLimit`, `roles/capabilities`, `status: REQUESTED | ACTIVE | REVOKED | EXPIRED | DENIED`, `startsAt`, `expiresAt`, `approvedBy`, `revokedBy`, `policyRefs`, `decisionCardId`, `correlationId`.

Commands:

- `grantSupportAccess(tenantId, supportAccountId/email, roles, duration, reason, actorAuthContext, idempotencyKey)`.
- `revokeSupportAccess(grantId, reason, actorAuthContext, idempotencyKey)`.
- `expireSupportAccess(grantId, systemAuthContext, idempotencyKey)`.
- `extendSupportAccess(grantId, newExpiresAt, reason, approvalRef, actorAuthContext, idempotencyKey)`.

Rules:

- support access is Tenant-created, time-limited, visible to Tenant Admins, auditable, and revocable.
- SaaS Owner privilege alone does not create or use support access.
- support access appears in `MembershipView`, `AdminAuditView`, and `AccessReviewQueueView`.

### `AdminAuditEvent` write model

Admin audit may be an immutable event entity, topic, or audit record component. The contract must support `AdminAuditView` queries.

Minimum fields:

- `auditEventId`, `timestamp`, `correlationId`, `idempotencyKey`.
- actor account id, actor membership id, actor roles/capabilities, actor scope.
- target account id, target membership id, target invitation id, target grant id, tenant id, customer id.
- action type, result (`ALLOWED`, `DENIED`, `NO_OP`, `FAILED`), reason/error code.
- before/after summaries for role, membership, account, support-access, profile/settings, identity-link, and invitation changes.
- policy refs, decision-card id, risk level, evidence summary, agent recommendation id when present.
- redaction marker and data classification.

Required action families:

- `ACCOUNT_*`, `PROFILE_*`, `SETTINGS_*`, `MEMBERSHIP_*`, `ROLE_*`, `INVITATION_*`, `SUPPORT_ACCESS_*`, `IDENTITY_RELINK_*`, `AUTH_CONTEXT_*`, `ADMIN_READ_*`, `ACCESS_REVIEW_*`, `POLICY_DECISION_*`, `DENIED_*`.

## `/api/me`, profile/settings, and context selection

### `GET /api/me`

Response fields:

- `account`: `accountId`, `email`, `displayName`, `status`, `identityLinkState`.
- `profile`: browser-safe profile fields.
- `settings`: `uiMode` and app-safe preferences.
- `memberships`: active and invited/suspended summaries visible to the signed-in account.
- `selectedContext`: selected/default AuthContext: `scopeType`, `tenantId`, `customerId`, `membershipId`, role names, browser-safe capabilities, support-access marker/expiry when applicable.
- `availableContexts`: context-switch options with organization display names, scope, membership status, and action availability.
- `functionalAgents`: browser-safe availability for User Admin and other role-authorized workstream agents.
- `navigationCapabilities`: browser-safe capabilities used for UI gating.
- `invitationAcceptance`: pending/required/failed acceptance state when a valid acceptance context is supplied.
- `auditCorrelationId` for consequential link/context changes.

Rules:

- valid JWT without local access returns an explicit no-access or invitation-required response, not privileged self-registration.
- first-login linking requires valid invitation token/acceptance context or explicit membership policy.
- disabled account returns a safe disabled response and protected APIs still deny.
- selected context must be active and inside available memberships.
- the endpoint never returns raw JWTs, WorkOS secrets, provider internals, raw invitation tokens, or out-of-scope org data.

### Profile/settings APIs

```text
GET   /api/me/profile
PATCH /api/me/profile
GET   /api/me/settings
PATCH /api/me/settings
POST  /api/me/context
```

Rules:

- profile patch edits only declared editable fields.
- settings patch initially supports `uiMode: LIGHT | DARK`.
- context switch validates the selected membership and emits audit for high-privilege context changes when policy requires it.

## View contracts

All protected views include backend-constrained scope fields and are exposed only through authorized endpoints/tools. Do not rely on frontend filters as the security boundary. Prefer separate query methods over optional-filter `OR` patterns. Any non-SSE `ORDER BY` columns must also appear in the same query's `WHERE` clause.

### `UserDirectoryView`

Source: Account, UserProfile, Membership, Invitation, SupportAccess, and activity/audit summaries. It may be built as one projection from membership/account events plus enrichment events, or split into account and scoped membership directory views when implementation constraints require it.

Row fields:

- `accountId`, `normalizedEmail`, `displayEmail`, `displayName`, profile summary.
- `accountStatus`, `identityLinkState`, `lastLoginAt`, `lastActivityAt`.
- `scopeType`, `tenantId`, `customerId`, `membershipIds`, `membershipStatuses`, `roles`, `capabilitiesSummary`.
- invitation status summary and last invitation id.
- support-access marker and expiry summary.
- admin-safe risk flags and audit links.

Required query paths:

- `searchByTenant(tenantId, emailOrNamePrefix/pageToken/pageSize)`.
- `searchByCustomer(tenantId, customerId, emailOrNamePrefix/pageToken/pageSize)`.
- `listByTenantAccountStatus(tenantId, accountStatus, pageToken/pageSize)`.
- `listByCustomerAccountStatus(tenantId, customerId, accountStatus, pageToken/pageSize)`.
- `listByTenantRole(tenantId, role, pageToken/pageSize)`.
- `listByCustomerRole(tenantId, customerId, role, pageToken/pageSize)`.
- `listByMembershipStatus(scope ids, membershipStatus, pageToken/pageSize)`.
- `findScopedUser(scope ids, accountId)` for detail entry.

### `MembershipView`

Source: `MembershipEntity` events/updates and support-access grant updates.

Row fields:

- `membershipId`, `accountId`, display-safe email/name.
- `scopeType`, `tenantId`, `customerId`, organization display names where available.
- `roles`, `capabilitiesSummary`, `status`, `membershipKind`.
- `invitationId`, `supportAccessGrantId`, `expiresAt`.
- `createdAt`, `activatedAt`, `suspendedAt`, `removedAt`, `updatedAt`.
- `lastAdminRisk: NONE | WOULD_REMOVE_LAST_ADMIN | REVIEW_REQUIRED`.
- `riskLevel`, `reviewStatus`, `auditEventIds`.

Required query paths:

- `listTenantMemberships(tenantId, status, pageToken/pageSize)`.
- `listCustomerMemberships(tenantId, customerId, status, pageToken/pageSize)`.
- `listTenantMembershipsByRole(tenantId, role, pageToken/pageSize)`.
- `listCustomerMembershipsByRole(tenantId, customerId, role, pageToken/pageSize)`.
- `listMembershipsByAccount(accountId)` with endpoint-side scope authorization/redaction.
- `listSupportAccessByTenant(tenantId, expiresBefore/status, pageToken/pageSize)`.
- `countActiveAdmins(scope ids, adminRole)` or explicit last-admin risk query for command-side policy checks.

### `InvitationView`

Use the contract from `invitation-onboarding-reference-slice.md`. User Admin consumes it through the same scoped query paths and must expose invitation rows in user detail, invitation queues, delivery failure queues, and access-review evidence.

Required integration points:

- `findByTenantEmail`, `findByCustomerEmail`, status/delivery-status queues, expiring invitations, resend/revoke eligibility, audit event ids.
- no raw token, token hash, provider secret, or full email body in User Admin DTOs.

### `AdminAuditView`

Source: AdminAuditEvent records or topic.

Row fields:

- `auditEventId`, `timestamp`, `correlationId`, `actionType`, `result`.
- actor account/membership/scope and browser-safe actor display.
- target account/membership/invitation/support grant/tenant/customer ids.
- role, membership status, invitation status, support-access status where applicable.
- policy refs, decision-card id, risk level, agent recommendation id.
- redacted reason/evidence summary and data classification.

Required query paths:

- `listByTenantAndTime(tenantId, from, to, pageToken/pageSize)`.
- `listByCustomerAndTime(tenantId, customerId, from, to, pageToken/pageSize)`.
- `listByActor(scope ids, actorAccountId, from, to, pageToken/pageSize)`.
- `listByTargetUser(scope ids, targetAccountId, from, to, pageToken/pageSize)`.
- `listByActionType(scope ids, actionType, from, to, pageToken/pageSize)`.
- `listByResult(scope ids, result, from, to, pageToken/pageSize)`.
- `listByPolicyOrDecision(scope ids, policyRef/decisionCardId, pageToken/pageSize)`.

### `AccessReviewQueueView`

Source: Membership, Invitation, SupportAccess, AdminAudit, activity signals, and optional agent recommendation events.

Row fields:

- `reviewItemId`, `itemType`: `STALE_INVITATION`, `DELIVERY_FAILURE`, `DORMANT_ADMIN`, `SUPPORT_ACCESS_EXPIRING`, `SUPPORT_ACCESS_EXPIRED`, `LAST_ADMIN_RISK`, `ORPHANED_CUSTOMER_ADMIN_GAP`, `RISKY_ROLE_COMBINATION`, `IDENTITY_RELINK_REVIEW`, `AGENT_RECOMMENDATION`.
- `scopeType`, `tenantId`, `customerId`.
- target account/membership/invitation/grant ids and display-safe target summary.
- `riskLevel`, `dueAt`, `expiresAt`, `reviewStatus`, `assignedReviewerAccountId`.
- evidence summary, policy refs, decision-card id, agent recommendation id, audit links.

Required query paths:

- `listTenantReviewItems(tenantId, reviewStatus, dueBefore, pageToken/pageSize)`.
- `listCustomerReviewItems(tenantId, customerId, reviewStatus, dueBefore, pageToken/pageSize)`.
- `listByRisk(scope ids, riskLevel, pageToken/pageSize)`.
- `listByItemType(scope ids, itemType, pageToken/pageSize)`.
- `listByTargetUser(scope ids, accountId, pageToken/pageSize)`.
- `listSupportAccessExpiring(tenantId, expiresBefore, pageToken/pageSize)`.

## Admin HTTP API contract

All `/api/admin/...` routes require WorkOS JWT authentication plus local backend authorization. Each mutation request includes `idempotencyKey`, `reason`, and optional `correlationId`. Responses are endpoint-facing DTOs, never internal component state.

```text
GET    /api/me
GET    /api/me/profile
PATCH  /api/me/profile
GET    /api/me/settings
PATCH  /api/me/settings
POST   /api/me/context

GET    /api/admin/users
GET    /api/admin/users/{accountId}
PATCH  /api/admin/users/{accountId}/profile
POST   /api/admin/users/{accountId}/disable
POST   /api/admin/users/{accountId}/reactivate
POST   /api/admin/users/{accountId}/identity/relink
POST   /api/admin/users/{accountId}/identity/relink/complete

POST   /api/admin/users/{accountId}/memberships
POST   /api/admin/memberships/{membershipId}/roles/replace
DELETE /api/admin/memberships/{membershipId}/roles/{role}
POST   /api/admin/memberships/{membershipId}/suspend
POST   /api/admin/memberships/{membershipId}/reactivate
DELETE /api/admin/memberships/{membershipId}

GET    /api/admin/invitations
GET    /api/admin/invitations/{invitationId}
POST   /api/admin/invitations
POST   /api/admin/invitations/{invitationId}/resend
POST   /api/admin/invitations/{invitationId}/revoke

POST   /api/admin/support-access/grants
POST   /api/admin/support-access/{grantId}/revoke
POST   /api/admin/support-access/{grantId}/extend
GET    /api/admin/support-access

GET    /api/admin/audit
GET    /api/admin/access-review
POST   /api/admin/access-review/{reviewItemId}/resolve
POST   /api/admin/access-review/{reviewItemId}/create-decision

GET    /api/tenants
POST   /api/tenants
GET    /api/tenants/{tenantId}
PATCH  /api/tenants/{tenantId}/settings
GET    /api/tenants/{tenantId}/customers
POST   /api/tenants/{tenantId}/customers
GET    /api/tenants/{tenantId}/customers/{customerId}
PATCH  /api/tenants/{tenantId}/customers/{customerId}/settings
```

### Common request/response rules

- Scope request fields: `scopeType`, `tenantId`, `customerId`, `membershipId` where needed.
- Pagination fields: `pageToken`, `pageSize` with stable sorting/query shape.
- Error codes: `UNAUTHENTICATED`, `LOCAL_ACCOUNT_REQUIRED`, `ACCOUNT_DISABLED`, `CONTEXT_REQUIRED`, `CONTEXT_FORBIDDEN`, `ROLE_ESCALATION_DENIED`, `LAST_ADMIN_DENIED`, `MEMBERSHIP_NOT_ACTIVE`, `TARGET_NOT_FOUND_OR_FORBIDDEN`, `IDENTITY_RELINK_POLICY_DENIED`, `SUPPORT_ACCESS_EXPIRED`, `IDEMPOTENCY_CONFLICT`.
- Resource-hiding policy: cross-tenant/customer detail access returns 403 or hidden not-found consistently; list queries never reveal out-of-scope rows.
- Audit: every mutation and every denied consequential action emits AdminAuditEvent; sensitive read/detail access emits audit according to policy.

### User detail DTO

`GET /api/admin/users/{accountId}` returns:

- account summary and profile summary.
- settings only when self or policy allows admin visibility.
- scoped memberships with role/status/support/invitation summaries.
- invitation history links from `InvitationView`.
- support-access grants related to the account.
- recent admin audit excerpts and access-review items.
- action availability flags computed from backend policy for UX only.

### Role/membership mutation DTOs

Role replace/remove and membership lifecycle requests include:

- target `membershipId`, expected scope, requested roles where relevant.
- `reason`, `idempotencyKey`, optional `decisionCardId` when approval was required.
- optional `expectedVersion` or current-state token when implementation supports optimistic conflict detection.

Responses include:

- updated membership summary.
- `lastAdminCheck` result.
- audit event id/correlation id.
- decision-card link if the action is blocked pending approval.

### Identity relink DTOs

Relink requests include:

- target account id, reason, evidence summary, requested new WorkOS subject or acceptance context, idempotency key.
- policy/approval reference for completion.

Rules:

- relink never grants roles or memberships.
- relink may require decision card approval for privileged accounts.
- previous subject and new subject are redacted in broad DTOs and available only in audit detail to authorized viewers.

## Support access and last-admin protection

### Support access lifecycle

- Tenant Admin creates support access for a named WorkOS-authenticated human/email.
- Backend creates or reuses Account and creates a Tenant-scoped support Membership with expiry, purpose, and narrowed roles/capabilities.
- Support access is visible in User Detail, Support Access, Admin Audit, and Access Review surfaces.
- Expiry uses a timed action or scheduled review path to mark support membership expired/suspended and emit audit.
- Revocation is idempotent and auditable.
- Extension requires reason and may require a decision card when high-risk or long duration.

### Last-admin protection

Before committing role removal/replacement, membership suspend/remove, account disable/remove, support-access revocation, or identity relink that affects admin reachability:

1. Determine target admin role: Tenant Admin or Customer Admin for the affected scope.
2. Query/derive active admin count from `MembershipView` or authoritative membership state.
3. Exclude memberships that are disabled, suspended, removed, expired, or attached to disabled accounts.
4. Reject actions that would leave zero active admins unless an explicit approved replacement policy exists.
5. Emit AdminAuditEvent and optionally AccessReviewQueue item/decision card.

Denial response code: `LAST_ADMIN_DENIED` with safe scope summary and remediation hint.

## User Admin workstream surface contract

Required surfaces consume the admin APIs and view DTOs. UI action availability is useful but backend authorization remains authoritative.

- Users:
  - directory search, user detail, profile summary, account status, identity link state, role/membership summary, last activity, audit links.
- Invitations:
  - create/list/detail/resend/revoke, delivery failure queue, expiry/acceptance status, no raw tokens.
- Roles/Memberships:
  - membership list, role replace/remove, add/suspend/reactivate/remove, last-admin risk warnings, decision-card entry points.
- Support Access:
  - grant/revoke/extend/list, expiry, purpose, scoped support capability, review links.
- Access Review:
  - stale invitations, dormant admins, support-access expiry, orphaned customer admin gaps, risky role combinations, last-admin risks, agent recommendations.
- Admin Audit:
  - scoped audit search, actor/target/action/time filters, policy/decision links, redacted evidence summaries.
- Tenant/Customer Settings:
  - Tenant and Customer organization metadata, settings, customer admin bootstrap entry points, subscription/billing boundary link for SaaS Owner only.

## Authorization and audit requirements

Authorize every protected route, component command, view query, stream, workflow action, consumer side effect, timer action, and agent/tool read.

Required denials:

- missing/invalid JWT.
- no linked local account where one is required.
- disabled account despite valid JWT.
- no selected AuthContext when multiple contexts exist.
- inactive/suspended/removed membership.
- wrong tenant/customer or unknown scope.
- actor lacks required capability.
- role/capability escalation.
- Customer Admin attempting Tenant-level actions.
- SaaS Owner attempting Tenant data access without explicit Tenant-scoped support access.
- identity relink without policy/approval/evidence.
- support access grant without Tenant Admin authority, reason, expiry, and scoped target.
- last-admin loss.

Audit facts:

- `/api/me` first link/activation and high-privilege context switch.
- profile/settings update.
- user directory sensitive detail read when policy requires.
- account disable/reactivate/remove.
- role assignment, replacement, removal, and denied escalation.
- membership add/activate/suspend/reactivate/remove and denied lifecycle actions.
- support-access grant/extend/revoke/expire/use.
- identity relink requested/completed/denied.
- invitation lifecycle events consumed from invitation slice.
- access-review item created/resolved/decision-created.
- admin/audit read detail and forbidden attempts.

## Access-review and admin-agent handoff

This slice must produce enough structured evidence for the mandatory AI-assisted admin offload responsibilities, without granting agents authority to commit high-risk changes.

Read-only agent/tool surfaces may consume:

- `UserDirectoryView`, `MembershipView`, `InvitationView`, `AdminAuditView`, and `AccessReviewQueueView` through scoped/redacted evidence APIs.
- access-review item creation APIs only when policy permits low-risk task creation.
- decision-card draft APIs for role escalation, last-admin risk, support-access extension, bulk invite, and identity relink.

Agents must not autonomously:

- grant admin roles.
- remove or disable last admins.
- expand support access.
- relink identities.
- change policies/permissions.
- access out-of-scope Tenant/Customer data.

## Implementation order for follow-up code tasks

1. Common security domain types:
   - scope ids, canonical roles, capability mapping, AuthContext, admin DTO error codes, audit metadata.
2. Account/Profile/Settings components:
   - current state, link/activate/disable/reactivate/relink, profile/settings patch, pure validation tests.
3. Membership component:
   - lifecycle, role replace/remove, support-access marker, last-admin policy seam, event replay tests.
4. SupportAccessGrant component and expiry timed action.
5. AdminAuditEvent write path and `AdminAuditView`.
6. `UserDirectoryView` and `MembershipView` with scoped query paths.
7. Integrate existing `InvitationView` from invitation slice into user detail/invitation APIs.
8. `AccessReviewQueueView` and access-review item generation from invitation/membership/support/audit signals.
9. `/api/me`, profile/settings, context-selection endpoints.
10. Admin endpoints for users, roles/memberships, support access, audit, access review, Tenant/Customer settings.
11. Security and integration tests across authorization, tenant isolation, last-admin, support-access, audit, and redaction.
12. Later workstream UI/API alignment using this slice as the backend contract.

## Required tests

### Domain/component tests

- Account create/link/activate is idempotent and requires valid invitation or membership policy.
- Disabled account cannot act even with valid JWT.
- Disable/reactivate transitions preserve audit metadata.
- Identity relink requires reason/evidence/approval policy and cannot grant roles.
- Profile patch accepts only editable fields and never changes authorization.
- Settings patch supports `uiMode: LIGHT | DARK` and rejects unsupported values.
- Membership add/activate/suspend/reactivate/remove validates scope and status transitions.
- Role replace/remove enforces caller authority and rejects role escalation.
- Removed membership cannot be reactivated unless documented policy creates a new membership.
- Support access grant/revoke/expire is scoped, time-limited, idempotent, visible, and auditable.
- Last-admin protection rejects role removal, membership suspension/removal, account disable, and support-access changes that would leave no active Tenant/Customer Admin.

### View tests

- `UserDirectoryView` projects account/profile/membership changes and supports tenant/customer search without caller-supplied user ids.
- `MembershipView` projects role/status/support-access changes and supports last-admin risk queries.
- `InvitationView` integration exposes status/delivery/expiry/resend/revoke summary without raw tokens.
- `AdminAuditView` indexes actor, target, action type, result, role, membership status, invitation status, policy/decision link, and time range.
- `AccessReviewQueueView` flags stale invites, delivery failures, dormant admins, support-access expiry, orphaned customer admin gaps, risky role combinations, last-admin risks, and agent recommendations.
- All scoped view queries return only authorized tenant/customer rows, paginate, redact, and avoid unsupported optional-filter query shapes.

### Endpoint/API tests

- `/api/me` returns browser-safe account, profile, settings, active memberships, selected AuthContext, available contexts, and capabilities.
- `/api/me` rejects disabled users and invalid selected contexts safely.
- profile/settings/context APIs enforce self-only or explicit scoped admin policy.
- user list/search works by authorized scope and does not require known user ids.
- user detail redacts out-of-scope memberships, provider internals, and sensitive support evidence.
- role replace/remove endpoints enforce scope, idempotency, and last-admin protection.
- membership add/suspend/reactivate/remove endpoints enforce Tenant/Customer boundaries.
- account disable/reactivate endpoints reject disabled actors and last-admin loss.
- identity relink endpoints require policy/evidence/approval and audit denials.
- support-access grant/revoke/extend/list endpoints enforce Tenant-created support rule and expiry.
- admin audit and access-review endpoints enforce auditor/admin read boundaries.
- Tenant/Customer settings endpoints preserve SaaS Owner/Tenant/Customer boundary semantics.

### Security/audit tests

- missing JWT is rejected for protected APIs.
- valid JWT without local authorized account cannot self-register privileged access.
- disabled user is rejected on every protected route.
- Tenant Admin cannot manage another Tenant or unrelated Customer.
- Customer Admin cannot manage Tenant-level users.
- SaaS Owner Admin cannot read Tenant application data without Tenant-created support-access membership.
- Tenant Admin cannot grant `SAAS_OWNER_ADMIN`; Customer Admin cannot grant Tenant roles.
- Cross-scope list/detail access does not leak resource existence according to resource-hiding policy.
- AdminAuditEvent facts exist for account, profile/settings, membership, role, support-access, identity relink, invitation integration, access review, denied action, and sensitive read events.
- Frontend/browser DTOs contain no WorkOS secrets, raw JWTs, raw invite tokens, token hashes, Resend secrets, or private provider details.

### Integration with invitation slice tests

- accepted invitation activates the intended membership and becomes visible in `/api/me` and User Directory.
- revoked/expired invitation prevents membership activation and creates access-review/audit evidence.
- delivery failure appears in Invitation and Access Review surfaces.
- duplicate invite does not create duplicate account or membership state.

## Done signal for implementation

The future implementation of this slice is complete when a fresh harness can demonstrate:

- SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, and ordinary users receive correct `/api/me` context/capability payloads.
- Tenant Admin can search users, view detail, replace/remove roles, suspend/reactivate/remove memberships, disable/reactivate accounts, grant/revoke support access, and inspect audit/access-review queues within one Tenant.
- Customer Admin can administer only Customer users.
- SaaS Owner Admin can manage platform-safe users/Tenant metadata but cannot read Tenant app data without Tenant-created support access.
- Last-admin protection, identity relink policy, support-access expiry, and role escalation denials are enforced and audited.
- UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView power admin discovery without caller-supplied user ids and without cross-scope leakage.
- Required component, endpoint, view, security, audit, and integration tests pass.
