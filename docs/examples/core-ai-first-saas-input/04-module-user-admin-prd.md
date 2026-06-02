# Module 3 PRD: User Administration

## Status

Detailed PRD for the User Administration module in the progressive core AI-first SaaS starter core app. This module follows the agent workstream runtime bootstrap even though the stable file name is retained for input-document continuity.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`
- `03-module-auth-app-access-prd.md`
- `03a-module-agent-workstream-runtime-bootstrap-prd.md`


## Workstream architecture alignment

This module PRD is interpreted under `10-canonical-core-app-prd.md` and `../../agent-workstream-application-architecture.md`. Any legacy references to pages, screens, navigation, or route inventory mean structured workstream surfaces, surface actions, and route/deep-link implementation details inside the agent workstream shell. They must not be used to generate a page-first admin console or chatbot-bolt-on app.

## 1. Module purpose

This module turns the authenticated, agent-runtime-backed shell from Modules 1 and 2 into an administrable SaaS access foundation.

The module gives authorized tenant administrators a visible, secure way to invite people, inspect users, manage memberships and roles, disable access, review access state, and inspect admin audit events. It completes the user administration foundation that later agent, prompt, skill, audit, and evaluation modules rely on.

This module is still a core SaaS foundation module, not full Agent Admin or prompt/skill governance. It must, however, reuse the Module 2 workstream runtime bootstrap so the User Admin experience is a functional-agent workstream backed by protected backend runtime behavior, not a page-first CRUD console. The baseline User Admin outcome must work without requiring the later full Agent Admin UI.

## 2. User-visible outcome

At completion, an authorized tenant admin can:

1. open the User Admin functional agent from the authenticated workstream shell;
2. view users and memberships within the selected tenant;
3. invite a user by email with a selected role/capability set;
4. see invitation delivery/status information;
5. resend or revoke pending invitations;
6. complete or simulate invitation acceptance through a safe onboarding flow;
7. change a member's role or disable/re-enable membership access;
8. see disabled users denied by the app;
9. run a basic access review surface showing who has access and why;
10. inspect admin audit events for invite, membership, role, and denial activity;
11. confirm through tests that tenant isolation, role denial, disabled-user denial, invitation idempotency, and audit emission are enforced.

## 3. MVP boundaries

### In scope

- Tenant-scoped User Admin functional-agent workstream integrated into the Module 1 shell and Module 2 runtime bootstrap.
- User/member directory for the selected tenant.
- Invitation lifecycle: create, resend, revoke/cancel, expire, accept, and delivery status.
- Explicit email delivery boundary with captured outbox adapter for local/dev/test.
- Membership lifecycle: active, disabled, revoked, and accepted-from-invitation states.
- Minimal editable roles/capabilities sufficient for app access and tenant administration.
- Disabled/suspended account or membership handling.
- Admin audit event creation and basic audit list/detail UI.
- Access review basics: list current access, pending invitations, risky/admin roles, stale/disabled access, and review actions.
- Backend authorization for all admin routes and component commands.
- Tests for allowed/denied admin actions, tenant isolation, idempotency, expiry, delivery failure visibility, and audit.

### Out of scope for Module 2

- Enterprise SSO/domain administration, SCIM, MFA policy administration, and identity-provider configuration UI.
- Full SaaS Owner console across all tenants, unless required for seed bootstrap.
- Billing/subscription enforcement beyond preserving the boundary.
- Customer-level administration unless the starter core app chooses to introduce Customers in this module.
- Support-access grant workflow beyond a placeholder or explicitly deferred UI card.
- Advanced policy governance, risk scoring, and AI-drafted admin recommendations beyond simple orientation/status behavior provided by the Module 2 bootstrap runtime.
- Agent definitions, prompt governance, skill governance, work trace timelines, evaluator agents, and closed-loop improvement.
- Rich notification preferences and production email template management.

## 4. Actors

| Actor | Description | Module 2 expectations |
|---|---|---|
| Tenant Admin | Active member with user-administration capabilities in the selected tenant. | Can manage invitations, memberships, roles, disabled access, access review, and admin audit within that tenant. |
| Tenant Member | Active member without admin capabilities. | Can view their own profile/context from Module 1 but cannot access User Admin functional-agent surfaces or APIs. |
| Invited User | Email recipient with a pending valid invitation. | Can accept invitation and become a member after authenticating/linking identity. |
| Disabled Member | Account or membership disabled by an admin. | Cannot access protected app areas for that tenant; denial is visible to admin audit. |
| Revoked/Expired Invitee | Email recipient whose invitation is no longer valid. | Cannot accept the invitation; receives a safe expired/revoked state. |
| Seed Operator | Initial admin from Module 1. | Can bootstrap the first tenant admin flows and validate the module. |
| Auditor/Admin Reviewer | Admin with read-only audit or access-review capability, if separated. | Can inspect user access and audit events without necessarily changing access. |

## 5. Authorization and capability model

Module 2 must convert Module 1's minimal capability placeholders into a tenant-admin capability set that can be enforced by the backend and reflected by the UI.

Required capabilities:

- `admin.users.read` — view tenant users and memberships.
- `admin.invitations.manage` — create, resend, revoke, and inspect invitations.
- `admin.memberships.manage` — enable, disable, revoke, or change member roles.
- `admin.roles.manage` — assign supported roles/capabilities. Full role editing may be limited.
- `admin.access_review.read` — view access review surface.
- `admin.access_review.commit` — mark review items as reviewed or take recommended access actions.
- `admin.audit.read` — inspect admin audit events.
- `app.access` — inherited baseline app access.

Initial roles should include at least:

| Role | Capabilities |
|---|---|
| Tenant Admin | All Module 2 admin capabilities plus app access. |
| Member | App access and own profile/context capabilities only. |
| Access Reviewer | Read access review and audit; optional no mutation capabilities. |

Rules:

- Backend checks are authoritative; UI hiding is not sufficient.
- A user cannot grant capabilities they do not hold unless a higher bootstrap/SaaS-owner rule explicitly allows it.
- A tenant admin cannot manage users in another tenant.
- A tenant admin should not be able to accidentally remove the last tenant admin without an explicit safety rule or confirmation.
- Disabled accounts and disabled/revoked memberships must deny admin access even if the provider session is valid.

## 6. Durable objects and state ownership

Module 2 extends the objects introduced in Module 1.

### Invitation

Represents an invitation for an email address to join a tenant with selected role/capability assignment.

Required fields:

- `invitationId`
- `tenantId`
- optional `customerId` if customer scope is introduced
- invited email and normalized email
- intended role ids/capabilities
- status: `PENDING`, `ACCEPTED`, `REVOKED`, `EXPIRED`, `DELIVERY_FAILED`
- invite token or acceptance context reference; do not expose raw secret values in admin lists
- created by account id
- accepted by account id when accepted
- expiry timestamp
- delivery status: not sent, sent, failed, retry pending
- delivery attempts and last delivery error summary
- timestamps and version

State owner expectation: Event Sourced Entity is preferred because invitation lifecycle, idempotency, auditability, and delivery transitions are consequential. A View should support lookup by tenant, normalized email, token/acceptance reference, and status.

### InvitationWorkflow

Coordinates invitation creation side effects, email delivery outbox, reminder/expiry handling, and acceptance.

Required behavior:

- create pending invitation after authorization and validation;
- enqueue email delivery request;
- record delivery result or failure;
- support resend without creating duplicate active invitations for the same tenant/email/role intent unless explicitly allowed;
- support revoke/cancel;
- expire invitations after deadline;
- accept invitation idempotently when the correct identity/token conditions are met;
- create or link Account and Membership according to the accepted onboarding rule.

State owner expectation: Workflow for multi-step invitation lifecycle and side effects, plus Timed Action for expiry/reminders if not handled internally.

### EmailOutboxMessage

Represents a captured or production email delivery request.

Required fields:

- `messageId`
- `tenantId`
- `invitationId`
- recipient email
- template/type
- status: pending, sent, failed
- attempt count
- provider message id when available
- safe error summary
- timestamps

State owner expectation: KV Entity or outbox topic/view pattern. Local/dev/test must use a captured outbox adapter so tests can inspect intended email without sending.

### Membership

Module 2 expands Membership lifecycle and role assignment.

Additional required fields:

- source: seed, invitation, admin-created, migration
- accepted invitation id where applicable
- assigned role ids/capabilities
- status reason
- disabled/revoked by account id and timestamp
- last reviewed timestamp and reviewer account id if access review is used

State owner expectation: KV Entity remains acceptable for current membership state; event history may be emitted to audit. If membership lifecycle history becomes product-critical, Event Sourced Entity may be selected during decomposition.

### Role / Capability

Module 2 introduces tenant-admin role assignments.

Required fields:

- `roleId`
- name
- description
- capability ids
- system/seeded vs editable flag
- tenant scope if roles are tenant-specific

State owner expectation: seeded configuration or KV state. Full role-definition editing can be deferred, but role assignment must be durable.

### Account / UserProfile / UserSettings

Module 2 may create or link Accounts when invitations are accepted.

Additional expectations:

- account status can be active or disabled;
- profile can be populated from provider claims on first acceptance;
- user settings can store default selected tenant after acceptance.

### AdminAuditEvent

Module 2 expands audit event coverage.

Required event types:

- `INVITATION_CREATED`
- `INVITATION_EMAIL_QUEUED`
- `INVITATION_EMAIL_SENT`
- `INVITATION_EMAIL_FAILED`
- `INVITATION_RESENT`
- `INVITATION_REVOKED`
- `INVITATION_EXPIRED`
- `INVITATION_ACCEPTED`
- `MEMBERSHIP_CREATED`
- `MEMBERSHIP_ROLE_CHANGED`
- `MEMBERSHIP_DISABLED`
- `MEMBERSHIP_REENABLED`
- `MEMBERSHIP_REVOKED`
- `ACCESS_REVIEW_VIEWED`
- `ACCESS_REVIEW_ITEM_MARKED`
- `ADMIN_AUTH_DENIED`

State owner expectation: append-only audit storage/query pattern compatible with Module 6 work-trace expansion.

## 7. Capabilities

### 7.1 Admin navigation and access gate

The app shell must show a User Admin functional-agent rail entry only to users with appropriate capabilities. Direct URL/deep-link access must still be checked by the backend and show a forbidden surface if denied.

User Admin surfaces must be scoped to the selected tenant from Module 1 AuthContext.

### 7.2 User/member directory

Tenant admins can view a directory of people with access to the selected tenant.

List columns:

- display name/email;
- account status;
- membership status;
- role(s);
- source (seed/invitation/admin);
- last seen/last authenticated if available;
- invitation status if pending;
- last reviewed timestamp if available.

Behaviors:

- search/filter by email/name/status/role;
- open member detail surface/panel;
- show empty state for no additional members;
- show forbidden state for non-admins;
- never show users from other tenants.

### 7.3 Invitation creation

Tenant admins can invite a user by email and intended role.

Required validation:

- valid email;
- selected role is assignable by current admin;
- tenant is active;
- no duplicate active membership for the same account/email in tenant;
- no duplicate pending invitation unless resend/update semantics are explicit;
- admin cannot invite into another tenant by changing request payload ids.

Expected result:

- pending Invitation is created;
- email outbox message is queued;
- invitation appears in pending invitation list;
- audit events are emitted;
- UI shows delivery status.

### 7.4 Invitation resend

Tenant admins can resend a pending invitation.

Rules:

- only pending, non-expired, non-revoked invitations can be resent;
- resend creates a new delivery attempt and updates delivery status;
- resend is idempotent enough to avoid duplicate lifecycle records from retrying the same request;
- all resends are audited.

### 7.5 Invitation revoke/cancel

Tenant admins can revoke a pending invitation.

Rules:

- accepted invitations cannot be revoked as invitations; membership must be managed instead;
- revoked invitations cannot be accepted;
- revocation is audited;
- UI updates pending invitation list immediately after success.

### 7.6 Invitation expiry

Invitations expire after a configured duration.

Rules:

- expired invitations cannot be accepted;
- expiry is enforced by backend even if a timer has not yet updated visible status;
- a Timed Action or scheduled process should mark expired invitations for UI visibility;
- expiry emits audit event.

### 7.7 Invitation acceptance

An invited user can accept a valid invitation through WorkOS/AuthKit sign-in.

Required behavior:

- acceptance token/context is validated server-side;
- user authenticates or is already authenticated;
- authenticated email/provider subject must satisfy the invitation acceptance rule;
- Account is created or linked only according to explicit rules;
- Membership is created or activated with intended role(s);
- default selected tenant is set when appropriate;
- invitation status becomes accepted;
- duplicate accept attempts are idempotent and safe;
- audit events are emitted;
- accepted user can enter the app through Module 1 `/api/me`.

### 7.8 Membership and role management

Tenant admins can change a member's role or membership status.

Required actions:

- change role;
- disable membership;
- re-enable disabled membership;
- revoke membership if included in MVP;
- view audit history summary for member changes.

Safety rules:

- cannot manage membership outside selected tenant;
- cannot remove the last tenant admin without explicit safeguard;
- cannot grant unrecognized or unauthorized capabilities;
- changes affect `/api/me` and protected endpoint access immediately or after documented session refresh.

### 7.9 Disabled user handling

Disabled accounts or memberships must be denied protected access.

Required UI behavior:

- admin directory clearly shows disabled status;
- disabled user sees disabled/no-access state on next `/api/me` resolution;
- admin audit records disabled-related denials.

### 7.10 Access review basics

Module 2 includes a lightweight access review surface.

The surface should show:

- all active admins;
- all active members;
- pending invitations;
- disabled/revoked memberships;
- stale or never-seen users when data is available;
- last reviewed timestamp;
- actions to mark reviewed and optionally disable/revoke access.

This is not a full campaign-based access certification system. It is a basic visibility and hygiene surface.

### 7.11 Admin audit list/detail

Tenant admins with `admin.audit.read` can inspect admin audit events for the selected tenant.

List filters:

- event type;
- actor;
- target user/email;
- status/decision;
- time range.

Detail view:

- event type and timestamp;
- actor account;
- target entity;
- tenant context;
- authorization decision;
- safe metadata;
- correlation id.

Audit UI must not expose raw tokens, invitation secret values, provider secrets, or sensitive backend internals.

## 8. UI requirements

### 8.1 Workstream surfaces and route/deep-link inventory

Minimum routes:

- `/app/admin` admin landing/overview;
- `/app/admin/users` user/member directory;
- `/app/admin/users/:accountId-or-membershipId` member detail;
- `/app/admin/invitations` invitation list;
- `/app/admin/invitations/new` invite form or modal route;
- `/accept-invitation` public/auth-transition route for invitation acceptance;
- `/app/admin/access-review` basic access review;
- `/app/admin/audit` admin audit list;
- `/app/admin/audit/:eventId` audit detail.

### 8.2 Admin landing

Admin landing should summarize:

- active users count;
- pending invitations count;
- disabled/revoked access count;
- admin users count;
- recent admin audit events;
- quick action to invite user;
- warnings for delivery failures or access-review items.

### 8.3 Invite user form

Required fields:

- email;
- role;
- optional personal message if safe and deferred-friendly;
- expiry display/read-only configured duration.

Required states:

- initial;
- validation errors;
- submitting;
- success with delivery status;
- duplicate pending invite;
- existing active member;
- forbidden;
- delivery queued but failed/unknown.

### 8.4 Member detail

Member detail should show:

- profile/account summary;
- membership status;
- roles/capabilities;
- invitation source when applicable;
- last seen/last authenticated if available;
- audit summary;
- actions permitted to current admin.

### 8.5 Invitation list/detail

Invitation UI should show:

- pending/accepted/revoked/expired status;
- recipient email;
- intended role;
- created by;
- expiry;
- delivery status;
- resend/revoke actions when allowed;
- safe acceptance link display only in local/dev/test if needed for demo, without exposing production secrets.

### 8.6 Acceptance UI

Invitation acceptance route must handle:

- valid pending invitation;
- expired invitation;
- revoked invitation;
- already accepted invitation;
- authenticated as wrong email/identity if the acceptance rule requires matching;
- successful acceptance and redirect into app;
- no leakage of other tenant membership data.

### 8.7 Access review UI

Access review surface should present reviewable rows with:

- person/email;
- current role/status;
- risk signal labels such as admin role, pending invite, stale access, disabled account;
- last reviewed;
- actions: mark reviewed, open member, disable/revoke where authorized.

### 8.8 Accessibility and responsive behavior

- Admin tables must support keyboard interaction and accessible labels.
- Forms must associate labels, help text, and errors.
- Destructive actions require confirmation and clear result messages.
- Important status badges must not rely on color alone.
- Tables should remain usable on narrower screens through responsive columns or detail cards.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### Users and memberships

- `GET /api/admin/users` — list tenant users/memberships for selected context.
- `GET /api/admin/users/{membershipId}` — get member detail scoped to selected tenant.
- `POST /api/admin/users/{membershipId}/role` — change role.
- `POST /api/admin/users/{membershipId}/disable` — disable membership.
- `POST /api/admin/users/{membershipId}/reenable` — re-enable membership.
- `POST /api/admin/users/{membershipId}/revoke` — revoke membership if included.

### Invitations

- `GET /api/admin/invitations` — list invitations for selected tenant.
- `POST /api/admin/invitations` — create invitation.
- `POST /api/admin/invitations/{invitationId}/resend` — resend pending invitation.
- `POST /api/admin/invitations/{invitationId}/revoke` — revoke pending invitation.
- `GET /api/invitations/acceptance-context` — resolve safe invitation acceptance state from token/context.
- `POST /api/invitations/accept` — accept invitation after auth/linking checks.

### Access review and audit

- `GET /api/admin/access-review` — list access review rows.
- `POST /api/admin/access-review/{itemId}/mark-reviewed` — mark row reviewed.
- `GET /api/admin/audit-events` — list admin audit events.
- `GET /api/admin/audit-events/{eventId}` — event detail.

API rules:

- All admin endpoints require selected active tenant context and relevant admin capability.
- Public invitation acceptance endpoints must validate invitation tokens/context server-side and avoid leaking tenant data.
- All commands must be idempotent or safe under retry where practical.
- Responses must be browser-safe and tenant-scoped.

## 10. Authorization rules

Required backend authorization checks:

- resolve authenticated account and selected AuthContext for every admin endpoint;
- require active account and active selected membership;
- require tenant status active;
- require capability for the specific action;
- verify target invitation/membership/audit event belongs to selected tenant;
- verify role assignment is allowed for the current actor;
- reject cross-tenant ids even if they are well-formed;
- reject disabled/revoked target state transitions when invalid;
- audit allowed consequential admin actions and denied admin attempts.

Special rules:

- Last-admin protection: deny or require explicit bootstrap override when an action would leave a tenant without any active admin.
- Self-demotion/disable protection: require confirmation or deny if the current admin would remove their own ability to recover tenant administration.
- Invitation acceptance must not grant access solely because an email string matches if the provider identity/linking rule is not satisfied.

## 11. Email delivery and outbox requirements

Module 2 must define an explicit email delivery boundary.

Production-like behavior:

- invitation emails are sent through Resend (resend.com), the supported production email service;
- future app email features reuse the same Resend email service/outbox foundation;
- provider secrets remain backend-only;
- delivery failures are captured and visible to admins;
- delivery attempts are auditable.

Local/dev/test behavior:

- use captured outbox adapter by default;
- tests can inspect queued messages and acceptance links/context safely;
- no real emails are sent during automated tests.

Invitation email content must include:

- tenant/app name;
- invited email;
- intended role or access summary if safe;
- expiration time;
- acceptance link or provider-mediated action;
- support/contact text;
- no raw internal authorization details.

## 12. Audit and observability requirements

Admin audit events must be emitted for every consequential admin action and important denial.

Required audit fields:

- audit event id;
- event type;
- timestamp;
- actor account id;
- selected tenant id;
- target account id, membership id, invitation id, or email when applicable;
- previous and new status/role when applicable;
- authorization decision;
- denial reason if denied;
- request/correlation id;
- safe metadata only.

Observability requirements:

- structured logs for invitation lifecycle, delivery attempts, admin denials, and membership changes;
- metrics/counters for pending invitations, delivery failures, admin denials, and disabled users if metrics are in the seed stack;
- correlation id between API request, workflow, outbox message, and audit event.

## 13. Security and privacy requirements

- Tenant admins can see only users, invitations, audit events, and access review rows for their selected tenant.
- Invitation tokens/secrets must be stored and displayed safely; admin UI must not expose production secret token values.
- Raw provider tokens, session cookies, Resend email service secrets, and internal secrets must not appear in API responses or frontend bundles.
- Duplicate invitation and acceptance flows must be idempotent to prevent accidental double membership creation.
- Revoked/expired invitations must be impossible to accept.
- Disabled/revoked memberships must immediately lose access to protected tenant data.
- Admin actions must be protected against CSRF/session misuse according to the WorkOS/AuthKit bearer-token flow.
- Error messages must not allow tenant/user enumeration beyond what an authorized admin can already see.

## 14. Acceptance scenarios

### Scenario 1: Tenant admin opens admin area

Given an active tenant admin is signed in, when they open Admin, then they see the admin overview for the selected tenant and no data from other tenants.

### Scenario 2: Non-admin is forbidden

Given an active member lacks `admin.users.read`, when they open `/app/admin/users` or call the users API, then access is forbidden and an admin denial audit event is emitted.

### Scenario 3: Admin creates invitation

Given a tenant admin enters a valid email and assignable role, when they submit the invite form, then a pending Invitation is created, an email outbox message is queued, invitation appears in the list, and audit events are emitted.

### Scenario 4: Duplicate pending invitation is safe

Given a pending invitation already exists for the same tenant/email/role intent, when the admin retries creation, then the system does not create duplicate active invitations and returns either the existing invitation or a clear duplicate/resend path.

### Scenario 5: Invitation delivery failure is visible

Given the email adapter reports failure, when the invitation is viewed, then admins can see failed delivery status and safe error summary, and the failure is audited.

### Scenario 6: Admin resends invitation

Given a pending non-expired invitation exists, when an admin resends it, then a new delivery attempt is queued, delivery status updates, and the resend is audited.

### Scenario 7: Admin revokes invitation

Given a pending invitation exists, when an admin revokes it, then the invitation cannot be accepted, it leaves the actionable pending list, and the revocation is audited.

### Scenario 8: Invitation expires

Given an invitation is past its expiry, when an invitee attempts acceptance, then access is not granted, the UI shows expired state, and expiry is visible/audited.

### Scenario 9: Invitee accepts invitation

Given a valid pending invitation and matching authenticated identity, when the invitee accepts it, then an Account is created or linked, Membership is created with intended role, invitation becomes accepted, `/api/me` returns active access, and audit events are emitted.

### Scenario 10: Acceptance retry is idempotent

Given an invitation has already been accepted, when the acceptance request is retried, then no duplicate membership is created and the user is directed to the app or an already-accepted state.

### Scenario 11: Admin changes role

Given a tenant admin changes a member from Member to Access Reviewer, when the command succeeds, then `/api/me` and admin UI reflect new capabilities, and audit shows previous and new role.

### Scenario 12: Last admin protection works

Given a tenant has one active Tenant Admin, when that admin attempts to remove their own admin role or disable their membership, then the system blocks or requires explicit safe override according to the accepted rule.

### Scenario 13: Disabled member is denied

Given an admin disables a member, when that member next calls `/api/me` or a protected endpoint, then tenant access is denied and the denial is audited.

### Scenario 14: Cross-tenant admin action is denied

Given an admin in Tenant A submits a membership or invitation id from Tenant B, when the command is processed, then the backend rejects it, returns forbidden/not-found according to security policy, and emits a denial audit event without leaking Tenant B data.

### Scenario 15: Access review records review action

Given an admin marks an access review row as reviewed, when the action succeeds, then the row shows updated review metadata and an audit event is emitted.

### Scenario 16: Admin audit is tenant-scoped

Given audit events exist in two tenants, when a Tenant A admin opens audit list, then only Tenant A events are visible.

## 15. Test requirements

Minimum test coverage:

- User Admin rail entry visible for admin and hidden/forbidden for non-admin.
- User directory returns only selected-tenant members.
- Invitation creation success with outbox message and audit events.
- Invitation create validation errors.
- Duplicate invitation idempotency.
- Invitation resend success and invalid-state denial.
- Invitation revoke success and revoked acceptance denial.
- Invitation expiry enforcement and timer/expiry visibility.
- Invitation acceptance success creates/links account and membership.
- Invitation acceptance retry does not duplicate membership.
- Wrong identity/email acceptance denial when matching is required.
- Role change success and forbidden role assignment denial.
- Disabled membership denies `/api/me` tenant access.
- Last-admin protection.
- Cross-tenant membership/invitation/audit access denial.
- Admin audit event emitted for create/resend/revoke/accept/role-change/disable/denial.
- Access review list and mark-reviewed behavior.
- Frontend states for admin lists/forms: loading, empty, validation error, forbidden, delivery failure, success.
- Frontend bundle/static asset test verifies no provider/email/backend secrets are exposed.

## 16. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Event Sourced Entity for `Invitation` lifecycle.
- Workflow for `InvitationWorkflow` orchestration: create, enqueue email, resend, revoke, accept, and status transitions.
- Consumer for invitation events to create outbox messages or update read models.
- Timed Action for invitation expiry and optional reminder checks.
- Key Value Entity for `Membership` current state, unless decomposition selects Event Sourced Entity for stronger membership history.
- Key Value Entity or seeded configuration for `Role`/`Capability` definitions.
- Key Value Entity for `EmailOutboxMessage` or an outbox projection depending on integration style.
- Views for users by tenant, invitations by tenant/status, memberships by account/tenant, audit events by tenant/time, and access review rows.
- HTTP endpoints for admin users, invitations, acceptance, access review, and audit.
- React/Vite/TypeScript UI for admin overview, directory, invitation forms, acceptance flow, access review, and audit.

Implementation guidance:

- Reuse Module 1 AuthContext and authorization helper for every admin endpoint.
- Keep invitation acceptance public/auth-transition endpoints separate from admin endpoints but still server-validated.
- Model email delivery through an adapter/outbox seam from the start.
- Keep role definitions small and explicit; do not build a full role editor unless accepted for MVP.
- Emit audit events from command-handling paths, not only from frontend actions.
- Prefer tenant-scoped views over client-side filtering.

## 17. Demo flow

A successful Module 2 demo should run as follows:

1. Start the app with Module 1 seed operator as Tenant Admin.
2. Sign in and open Admin overview.
3. View user directory showing the seed operator.
4. Invite a new member by email with Member role.
5. Inspect captured outbox/delivery status.
6. Accept the invitation as the invited user.
7. Sign in as the invited user and confirm `/api/me` shows tenant access.
8. Return as admin, change the user's role or disable membership.
9. Confirm the affected user sees updated access or disabled/no-access state.
10. Open access review and mark a row reviewed.
11. Open admin audit and inspect invitation, membership, and denial events.
12. Run tests proving tenant isolation, forbidden access, idempotency, expiry, and frontend secret boundary.

## 18. Explicit defers to later modules

Deferred to Module 3 Agent Definition Foundation:

- agent records, agent status, model config placeholders, tool permission boundaries.

Deferred to Module 4 Prompt Governance:

- runtime-managed prompts, prompt version history, prompt diff/review/activation.

Deferred to Module 5 Skill Governance:

- governed skill documents, per-agent skill manifests, `readSkill(skillId)` tool.

Deferred to Module 6 Audit and Work Trace:

- full trace timeline UI, cross-module trace search, prompt/skill/model/tool trace payloads, redaction policies beyond admin audit basics.

Deferred to Module 7 Evaluation and Closed-Loop Improvement:

- evaluator agents, improvement proposals, replay/simulation, canary activation, rollback workflows.

Deferred to later security/admin hardening:

- SCIM;
- enterprise SSO setup UI;
- MFA policy administration;
- support access workflow;
- campaign-based access certifications;
- advanced risk scoring and AI-generated access recommendations;
- full custom role editor.

## 19. Readiness checklist

Module 2 is ready for decomposition when the following are true:

- [ ] Module 1 AuthContext, `/api/me`, and app shell assumptions are accepted.
- [ ] Invitation lifecycle states and acceptance/linking rules are accepted.
- [ ] Local/dev/test email outbox behavior is accepted.
- [ ] Minimal roles and capabilities are accepted.
- [ ] Last-admin and self-demotion safety rules are accepted.
- [ ] Membership lifecycle states are accepted.
- [ ] User Admin surface inventory and form/list states are accepted.
- [ ] Access review MVP scope is accepted.
- [ ] Admin audit event coverage is accepted.
- [ ] Tenant isolation, forbidden access, disabled-user, idempotency, expiry, audit, and frontend secret-boundary tests are accepted.
- [ ] Deferred features are confirmed as not part of Module 2.
