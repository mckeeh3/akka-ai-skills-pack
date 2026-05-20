# PRD 2: User Admin Workstream PRD

## 1. PRD identity

- **PRD name:** User Admin Functional Agent Workstream PRD
- **Scope:** Tenant/customer-scoped user administration workstream for discovering users, viewing/editing accounts, managing memberships/roles/capabilities, invitations, disable/reactivate, last-admin protection, and audit.
- **Functional agent workstream:** `functional_agent.user_admin`.
- **Goals:** Provide a safe agent-workstream-first administration experience with structured surfaces and backend-governed capabilities for user access operations.
- **Non-goals:** Raw database administration; global cross-tenant user browsing; billing administration; agent prompt/skill governance.
- **Dependencies on other PRDs:** Requires Main/Foundation PRD for auth, AuthContext, audit, email/outbox, invitation foundation, shell, and shared DTOs. Emits audit records consumed by Audit PRD.

## 2. Actors and authority

- **User roles:** `SAAS_OWNER_ADMIN` for platform-safe tenant bootstrap/support-access flows; `TENANT_ADMIN` for tenant user admin; `CUSTOMER_ADMIN` for customer-scoped membership administration; `AUDITOR` read-only where granted.
- **System/internal actors:** `AuthorizationService`, `InvitationWorkflow`, `EmailDeliveryConsumer`, `InvitationExpiryTimedAction`, `AuditEventWriter`.
- **Functional agent:** `functional_agent.user_admin` helps find users, summarize access, draft invitations, explain denials, and prepare safe recommendations. It cannot grant roles or disable users without explicit user action/approval.
- **Internal agents:** optional `UserAdminAgent` for access-review summaries, invitation drafting, role recommendations, and last-admin risk explanation.
- **AuthContext:** selected Tenant or Customer context required; SaaS Owner context only for bootstrap/support-access metadata, not tenant data reads without support access.
- **Tenant/customer scope:** user searches and commands are scoped to selected tenant/customer. Customer admins cannot manage tenant-wide roles unless explicitly granted.
- **Required capabilities:** `user_admin.dashboard.view`, `user_admin.users.search`, `user_admin.users.view`, `user_admin.users.update_profile`, `user_admin.memberships.manage_status`, `user_admin.roles.assign`, `user_admin.invitations.create`, `user_admin.invitations.resend`, `user_admin.invitations.revoke`, `user_admin.accounts.disable`, `user_admin.accounts.reactivate`.
- **Approval/escalation:** assigning admin roles, disabling an admin, bulk changes, support access, and operations that would remove the last admin require approval/decision-card or are blocked.
- **Forbidden behavior:** no last-admin removal; no role escalation beyond caller authority; no editing WorkOS identity claims as authorization; no cross-tenant search; no silent invite to privileged role without audit and optional approval.

## 3. Workstream model

- **Purpose:** continuous administrative workstream for safe access management.
- **Default entry:** render `surface.user_admin.dashboard.v1` with access-risk summary, pending invitations, disabled users, recent admin actions, and trace links.
- **Persistent composer:** accepts scoped requests such as “find Alex”, “draft invite for finance approver”, or “why was this role denied”. It maps to read/proposal capabilities and requires explicit surface actions for side effects.
- **Items/events:** `UserSearchPerformed`, `UserAccountViewed`, `InvitationDrafted`, `InvitationCreated`, `InvitationResent`, `InvitationRevoked`, `MembershipStatusChanged`, `RoleAssignmentChanged`, `UserAccountDisabled`, `UserAccountReactivated`, `LastAdminProtectionTriggered`, `CapabilityDenied`.
- **Trace links:** each item links to AdminAuditEvent and related InvitationWorkflow trace.
- **Realtime/stale/reconnect:** dashboard/list show stale banner when membership or invitation views lag; consequential actions revalidate current membership/version before commit.

## 4. Structured surfaces

### `surface.user_admin.dashboard.v1`
- **Type:** dashboard/attention summary.
- **Purpose:** summarize user administration health and pending work.
- **Placement:** default surface when opening User Admin.
- **Payload fields:** `activeUserCount`, `pendingInvitations`, `failedInvitationDeliveries`, `disabledAccounts`, `adminCount`, `accessReviewItems`, `recentAuditEvents[]`, `allowedActions[]`.
- **States:** loading skeleton; empty when no users; ready summary cards; validation-error for invalid filters; forbidden if missing `user_admin.dashboard.view`; stale/reconnect disables refresh-dependent actions; success/failure surfaced as workstream items.
- **Trace/audit:** cards link to audit/search surfaces.
- **A11y/responsive:** cards collapse to list; count labels are screen-reader friendly.

### `surface.user_admin.users_list.v1`
- **Type:** searchable table/evidence surface.
- **Purpose:** find specific users and invitations without requiring known ids.
- **Placement:** opened by dashboard/search/composer.
- **Payload:** `query`, `filters {email, name, role, membershipStatus, invitationStatus, tenantId, customerId}`, `page`, `rows[] {accountId, displayName, email, membershipStatus, roles, capabilitiesSummary, invitationStatus, lastActiveAt, riskFlags}`, `redactions`, `allowedActions`.
- **States:** loading; empty no matches; ready rows; validation-error invalid filter/date/page; forbidden; stale if view lag detected; success/failure for search and row actions.
- **Trace/audit:** `UserDirectoryRead` sensitive-read audit when rows include PII.
- **A11y/responsive:** keyboard sortable/filterable table; mobile card rows.

### `surface.user_admin.user_account_edit.v1`
- **Type:** detail/edit form plus role/membership panels.
- **Purpose:** inspect and modify allowed profile/account/membership fields.
- **Placement:** opened from list row, audit target link, or composer search result.
- **Payload:** `account`, `profile`, `memberships[]`, `roles[]`, `capabilities[]`, `invitations[]`, `lastAdminRisk`, `version`, `allowedActions`, `auditLinks[]`.
- **States:** loading; empty if target not in scope; ready form; validation-error per field; forbidden with safe reason; stale if version mismatch; success/failure with rollback of optimistic UI.
- **Trace/audit:** link to all user-related audit events.
- **A11y/responsive:** forms have labels/errors; destructive actions require confirmation and explain impact.

## 5. Surface actions

| Action id | Label | Intent | Inputs | Capability id | Required role/capability | Idempotency | Side effects | Audit events | Success | Failure/denial | Approval |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `action.user_admin.refresh_dashboard` | Refresh summary | Load dashboard evidence | filters | `user_admin.dashboard.summary` | `user_admin.dashboard.view` | read-only | sensitive read audit | `UserAdminDashboardRead` | dashboard DTO | safe denial | no |
| `action.user_admin.search_users` | Search users | Find users/invites | query, filters, page | `user_admin.users.search` | `user_admin.users.search` | read-only | sensitive read audit | `UserDirectorySearched` | rows | validation/forbidden | no |
| `action.user_admin.open_user` | Open user | View account detail | `accountId` | `user_admin.users.view` | `user_admin.users.view` | read-only | sensitive read audit | `UserAccountRead` | detail DTO | forbidden/not found | no |
| `action.user_admin.save_profile` | Save profile | Edit allowed profile fields | `accountId`, fields, version | `user_admin.users.update_profile` | `user_admin.users.update_profile` | duplicate version no-op | profile update | `UserAccountUpdated` | updated detail | validation/stale/denied | no |
| `action.user_admin.change_membership_status` | Change membership | Suspend/reactivate/remove | `accountId`, `membershipId`, status, reason, version | `user_admin.memberships.change_status` | `user_admin.memberships.manage_status` | same status no-op | membership change, session invalidation | `MembershipStatusChanged` | updated detail | last-admin/denied | maybe |
| `action.user_admin.assign_roles` | Assign roles | Replace/assign roles/capabilities | `membershipId`, roles, reason, version | `user_admin.roles.assign` | `user_admin.roles.assign` | same role set no-op | role/capability update | `RoleAssignmentChanged` | updated roles | escalation/denied | admin roles require approval |
| `action.user_admin.invite_user` | Invite user | Create invitation | email, scope, roles, message, idempotencyKey | `user_admin.invitations.create` | `user_admin.invitations.create` | key dedupes invitation | InvitationWorkflow + email | `InvitationCreated`, `InvitationEmailQueued` | invitation status | validation/denied | privileged roles maybe |
| `action.user_admin.resend_invitation` | Resend invite | Send pending invite again | `invitationId`, reason | `user_admin.invitations.resend` | `user_admin.invitations.resend` | resend attempt idempotent by key | email attempt | `InvitationResent` | delivery status | expired/revoked/denied | no |
| `action.user_admin.revoke_invitation` | Revoke invite | Cancel pending invite | `invitationId`, reason, version | `user_admin.invitations.revoke` | `user_admin.invitations.revoke` | already revoked no-op | status update | `InvitationRevoked` | revoked status | accepted/denied | no |
| `action.user_admin.disable_account` | Disable user | Disable login/app access | `accountId`, reason, version | `user_admin.accounts.disable` | `user_admin.accounts.disable` | already disabled no-op | account disabled, sessions rejected | `UserAccountDisabled` | disabled detail | last-admin/denied | admin target requires approval |
| `action.user_admin.reactivate_account` | Reactivate user | Restore disabled account | `accountId`, reason, version | `user_admin.accounts.reactivate` | `user_admin.accounts.reactivate` | already active no-op | account active | `UserAccountReactivated` | active detail | denied | maybe |

## 6. Governed backend capabilities

### `user_admin.dashboard.summary`
- **Class:** read/evidence.
- **Actors:** User Admin workstream, `UserAdminAgent`.
- **Scope:** selected AuthContext tenant/customer.
- **Input/Output:** `{filters?}` -> counts, risk flags, pending invites, failed deliveries, recent events.
- **Validation:** caller has dashboard capability; filters in scope.
- **Data:** reads UserDirectoryView, InvitationView, MembershipView, AdminAuditView.
- **Side effects:** sensitive read audit.
- **Tests:** authorized summary, customer-scoped counts, forbidden tenant mismatch, redaction.

### `user_admin.users.search`
- **Class:** read/evidence.
- **Actors:** browser, UserAdminAgent evidence tool.
- **DTOs:** `{query, filters, pageToken, pageSize}` -> `{rows[], nextPageToken, redactions}`.
- **Validation:** bounded page size; filter ids match AuthContext.
- **Data:** scoped UserDirectoryView and InvitationView.
- **Audit:** `UserDirectorySearched` with filter summary, not raw secrets.
- **Tests:** pagination, PII redaction, forbidden cross-tenant, disabled caller.

### `user_admin.users.view`
- **Class:** read/evidence.
- **DTOs:** `{accountId}` -> detail DTO with versions and allowed actions.
- **Data:** Account/UserProfile/Membership/Invitation/Audit summary.
- **Audit:** `UserAccountRead` as sensitive read.
- **Tests:** target not in scope, auditor redaction, allowedActions correctness.

### `user_admin.users.update_profile`
- **Class:** command.
- **DTOs:** `{accountId, displayName?, locale?, timezone?, version, idempotencyKey}` -> updated detail.
- **Validation:** only profile fields; email/provider subject immutable here; version match.
- **Data:** updates UserProfile KVE/state.
- **Idempotency/no-op:** same field values no-op with audit `NOOP`.
- **Audit/tests:** `UserAccountUpdated`; validation/stale/denial tests.

### `user_admin.memberships.change_status`
- **Class:** command/approval.
- **DTOs:** `{membershipId, targetStatus, reason, version, approvalId?}` -> membership DTO.
- **Validation:** allowed transition; last-admin protection; caller outranks or matches policy; target in scope.
- **Side effects:** active sessions invalidated by authz checks; access-review queues update.
- **Audit:** `MembershipStatusChanged`, `LastAdminProtectionTriggered`.
- **Tests:** suspend/reactivate/remove, last admin blocked, idempotent same status.

### `user_admin.roles.assign`
- **Class:** command/approval.
- **DTOs:** `{membershipId, roles[], capabilities?, reason, version, approvalId?}` -> role assignment DTO.
- **Validation:** role exists in scope; caller may grant; admin grants approval-gated; no authority expansion beyond caller.
- **Data:** writes Membership/Role assignment.
- **Audit:** `RoleAssignmentChanged`, denial event for escalation attempts.
- **Tests:** tenant admin grants tenant employee, customer admin denied tenant admin, approval required for admin role.

### `user_admin.invitations.create`
- **Class:** workflow.
- **DTOs:** `{email, scopeType, tenantId, customerId?, roles[], message?, idempotencyKey}` -> invitation DTO.
- **Validation:** email valid; not duplicate active membership unless policy; roles grantable; tenant/customer scope valid.
- **Data:** creates Invitation; starts InvitationWorkflow.
- **Side effects:** Resend/captured email, expiry timer.
- **Audit:** `InvitationCreated`, delivery events.
- **Tests:** invite, duplicate idempotency, captured outbox, production adapter boundary.

### `user_admin.invitations.resend` / `user_admin.invitations.revoke`
- **Class:** command/workflow.
- **DTOs:** `{invitationId, reason, idempotencyKey}` -> invitation status.
- **Validation:** pending/not expired for resend; pending for revoke.
- **Audit:** `InvitationResent`, `InvitationRevoked`.
- **Tests:** expired revoke/resend behavior, duplicate resend, forbidden scope.

### `user_admin.accounts.disable` / `user_admin.accounts.reactivate`
- **Class:** command/approval.
- **DTOs:** `{accountId, reason, version, approvalId?}` -> account status.
- **Validation:** cannot disable self if last admin; privileged target approval; account in scope.
- **Audit:** `UserAccountDisabled`, `UserAccountReactivated`.
- **Tests:** disabled user cannot use `/api/me`, last-admin blocked, reactivation restores membership-based access.

## 7. Akka realization expectations

- **Event Sourced Entities:** `InvitationEntity` for lifecycle; optionally `MembershipAuditEntity` if role history needs replay beyond audit log.
- **Key Value Entities:** `AccountEntity`, `UserProfileEntity`, `MembershipEntity`, `RoleCapabilityRegistryEntity`.
- **Workflows:** `InvitationWorkflow` for create/send/resend/accept/revoke/expire; approval workflow for high-risk role/admin changes if implemented in first release.
- **Views:** `UserDirectoryView`, `MembershipView`, `InvitationView`, `AccessReviewQueueView`, `AdminAuditView`.
- **Consumers:** email delivery; user/membership change projection; audit consumer if separate.
- **Timed Actions:** invitation expiry/reminders; optional stale access review reminders.
- **Agents:** governed `UserAdminAgent` with read-only evidence tools and proposal-only admin tools.
- **HTTP endpoints:** `/api/user-admin/dashboard`, `/api/user-admin/users`, `/api/user-admin/users/{id}`, membership/role/invitation/account action endpoints.
- **Frontend:** dashboard/list/edit surface components, typed API client, form validation, stale version handling, destructive confirmation, accessible tables/forms.

## 8. Internal agents, workflows, and event-driven processing

- **UserAdminAgent responsibilities:** summarize user access, draft invite text, recommend role based on user-provided rationale, explain last-admin/denial outcomes; cannot commit role/status changes.
- **Tool boundaries:** may call `user_admin.dashboard.summary`, `user_admin.users.search`, `user_admin.users.view`; side-effecting tools limited to proposal/draft unless explicit approval policy added.
- **Prompt/skill governance:** AgentDefinition/PromptDocument/SkillDocument seeded and edited through Agent Admin PRD.
- **Workflow/retries:** InvitationWorkflow retries email delivery safely; revoke/accept race resolved by entity state.
- **Events:** user/membership/invitation events update views and audit.
- **Traces:** AgentWorkTrace links to searches and recommendations; admin action audit links to surface action id.

## 9. Security, audit, and compliance

- Backend authorization for every search, view, command, agent tool, workflow step, consumer side effect, and timer.
- Tenant/customer isolation in view queries and command target validation.
- Role changes cannot grant capabilities the caller lacks unless approved by higher authority.
- Sensitive reads of user directory/detail are audited and redacted by role.
- Email messages contain no secrets except invitation token/acceptance link; outbox redacts token except privileged debug view in non-production.
- Denials log reason codes without leaking existence of cross-tenant users.
- Tests cover cross-tenant access, last-admin protection, disabled users, denied role escalation, invitation lifecycle, audit completeness.

## 10. Acceptance criteria

- **Backend:** all listed user admin capabilities implemented with scoped DTOs, validation, idempotency, version checks, audit, and safe denial shape.
- **Frontend:** dashboard, searchable users list, and user account/edit surfaces render required states and allowed actions from backend.
- **Auth/security:** unauthorized users cannot see or invoke User Admin; cross-tenant target ids are denied; last-admin protection enforced.
- **Audit/trace:** every user admin read/action/denial creates or links to scoped audit events.
- **Workflows/events/timers:** invitations send/resend/revoke/expire/accept with retries and captured outbox tests.
- **Fullstack:** admin can invite a user, resend/revoke invite, search user, edit profile, change membership/roles within authority, and observe audit trail.
- **Tests:** unit/integration/UI tests for success, validation, idempotent no-op, forbidden, tenant isolation, stale version, audit, and email/outbox behavior.

## 11. Open questions

- Which role changes require approval in the first implementation beyond admin-role assignment and disabling admin users?
- Should customer-scoped admins be allowed to invite users directly, or only recommend invitations for tenant admins to approve?
