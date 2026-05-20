# PRD 1: Main / Foundation PRD

## 1. PRD identity

- **PRD name:** Core AI-First SaaS Foundation PRD
- **Scope:** Full-stack secure SaaS foundation for Akka Java SDK backend and React/Vite/TypeScript agent-workstream frontend.
- **Functional agent workstream:** Shell-level foundation for `user_admin`, `agent_admin`, and `audit`; includes context/authority indicators and shared workstream runtime.
- **Goals:**
  - Authenticate browser users with WorkOS/AuthKit and authorize every protected capability with Akka-owned Account, Tenant, Customer, Membership, Role, Capability, and selected AuthContext records.
  - Provide `/api/me`, context selection, admin bootstrap, invitation email foundation with Resend, captured local/dev/test outbox, backend authorization, shared denial/error DTOs, audit event foundation, and static frontend hosting.
  - Render an authenticated agent workstream shell with role-authorized functional-agent rail for User Admin, Agent Admin, and Audit.
- **Non-goals:** App-specific business domain workflows outside core administration; alternate auth/email providers; prompt-only authorization; unscoped global admin bypasses.
- **Dependencies on other PRDs:** Enables User Admin, Agent Admin, and Audit PRDs. Those PRDs may add specialized capabilities but must reuse this foundation.

## 2. Actors and authority

- **User roles:** `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, `AUDITOR`, plus capability grants returned by `/api/me`.
- **System/internal actors:** `BootstrapInitializer`, `WorkOSJwtVerifier`, `AuthorizationService`, `EmailDeliveryConsumer`, `InvitationExpiryTimedAction`, `AuditEventWriter`, `FrontendStaticHost`.
- **Functional agents:** `functional_agent.user_admin`, `functional_agent.agent_admin`, `functional_agent.audit`, shown only when selected AuthContext includes required capabilities.
- **Internal agents:** none required in the foundation except seed records for future governed agents.
- **AuthContext requirements:** every protected API/capability receives account id, selected membership id, tenant id when tenant-scoped, optional customer id, roles, capabilities, actor type, correlation id, and trace id.
- **Tenant/customer scope rules:** all tenant/customer records include scope ids; reads filter by selected scope; writes reject mismatched ids; SaaS Owner can manage platform-safe tenant metadata but cannot read tenant application data without explicit support-access membership.
- **Role/capability requirements:** backend checks named capabilities, not frontend visibility. Minimum foundation capabilities: `core.access.me`, `core.context.select`, `core.authz.evaluate`, `core.audit.record`, `core.email.send_invitation`, `core.outbox.capture`, `core.frontend.serve_static`.
- **Approval/escalation:** tenant bootstrap and first admin creation require explicit seed/bootstrap configuration. Support access and privileged role grants are approval-gated in downstream workstreams.
- **Forbidden/denied behavior:** no self-registration into privileged roles; no cross-tenant reads/writes; no disabled-account access; no provider secret exposure to frontend; no email delivery in local/dev/test except captured outbox.

## 3. Workstream model

- **Shell purpose:** authenticated operating surface for role-authorized functional agents.
- **Default entry behavior:** unauthenticated users see AuthKit sign-in. Authenticated users call `core.access.me`; if no selectable context exists, show safe blocked/onboarding state. If one context exists, select it; if multiple, show context selector.
- **Persistent composer behavior:** present after context selection. Composer is scoped to selected functional agent and AuthContext. Foundation composer can navigate/open surfaces and ask for allowed summaries; side-effecting natural-language requests are converted to explicit surface actions.
- **Workstream items/events:** `SignedIn`, `AuthContextSelected`, `CapabilityDenied`, `AuditEventRecorded`, `InvitationEmailCaptured`, `FrontendShellLoaded`.
- **Trace links:** each capability result includes `traceId`; shell exposes audit detail links when user has `audit.events.view`.
- **Realtime/stale/reconnect:** shell polls or subscribes to context/capability changes where implemented; when connection is stale, disable consequential actions and show retry.

## 4. Structured surfaces

### `surface.core.shell.v1`
- **Type:** workstream shell.
- **Purpose:** render functional-agent rail, main workstream, composer, context and authority indicators.
- **Placement:** root authenticated experience.
- **Payload fields:** `account`, `selectedContext`, `availableContexts[]`, `functionalAgents[]`, `capabilities[]`, `traceLinks[]`, `frontendBuildVersion`.
- **States:** loading while `/api/me` resolves; empty when no memberships; ready when context and agents load; validation-error for invalid context id; forbidden for disabled account or missing membership; stale/reconnect shows disabled actions; success/failure via toast and workstream events.
- **Trace/audit links:** `/audit/events?traceId=...` when authorized.
- **Responsive/accessibility:** keyboard navigable rail/composer, visible focus, ARIA landmarks, mobile rail drawer, no hidden auth-only controls as enforcement.

### `surface.core.context_selector.v1`
- **Type:** selection card/list.
- **Purpose:** choose Tenant/Customer/SaaS Owner operating context.
- **Payload:** `contexts[] {contextId, scopeType, tenantId, customerId, roles, capabilitiesPreview, default}`.
- **States:** loading, empty blocked state, ready selectable list, forbidden disabled membership, stale if membership changed.
- **Actions:** `action.core.select_context`.

### `surface.core.identity_status.v1`
- **Type:** status/detail card.
- **Purpose:** show browser-safe identity, profile, roles/capabilities, auth provider link status.
- **Payload:** `accountId`, `workosSubject`, `email`, `profile`, `membershipStatus`, `capabilities`, `redactions`.
- **States:** same as above; sensitive provider metadata redacted.

### `surface.core.outbox_status.v1`
- **Type:** admin evidence card for non-production environments.
- **Purpose:** show captured invitation emails without external delivery.
- **Payload:** `environment`, `capturedMessages[]`, `deliveryAdapter`, `lastFailure`.
- **Forbidden:** hidden unless caller has `core.outbox.view_captured` and environment is non-production.

## 5. Surface actions

| Action id | Label | Intent | Input fields | Backend capability | Required authority | Idempotency | Side effects | Audit events | Result | Denial | Approval |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `action.core.select_context` | Select context | Change selected AuthContext | `contextId` | `core.context.select` | active membership | same context is no-op | updates session/browser preference | `AuthContextSelected` | refreshed `/api/me` | `CapabilityDenied` | no |
| `action.core.refresh_me` | Refresh access | Reload current authority | none | `core.access.me` | authenticated account | safe repeat | none | `CurrentUserRead` | browser-safe me DTO | denial if disabled/no membership | no |
| `action.core.open_functional_agent` | Open workstream | Switch left-rail workstream | `agentId` | `core.workstream.open` | capability for agent | same agent no-op | workstream event | `FunctionalAgentOpened` | target surface | forbidden agent hidden/denied | no |
| `action.core.view_captured_outbox` | View captured outbox | Inspect test/dev emails | filters | `core.outbox.search_captured` | `core.outbox.view_captured` | read-only | sensitive read audit | `CapturedOutboxRead` | redacted outbox rows | forbidden in production | no |

## 6. Governed backend capabilities

### `core.access.me`
- **Class:** read/evidence.
- **Actors/callers:** browser, shell, tests.
- **Scope:** authenticated WorkOS subject linked to local Account; selected/default AuthContext optional.
- **Input DTO:** `{requestedContextId?, correlationId}`.
- **Output DTO:** `{account, profile, settings, memberships[], selectedContext, functionalAgents[], capabilities[], denial?}`.
- **Validation:** JWT valid; local account not disabled; requested context belongs to active membership.
- **Data behavior:** reads Account, UserProfile, UserSettings, MembershipView, Role/Capability registry.
- **Side effects:** may create non-privileged pending Account on first login only when invitation/accepted policy permits; otherwise blocked.
- **Idempotency:** repeated reads stable; context default selection deterministic.
- **Audit/trace:** `CurrentUserRead`, `FirstLoginLinked`, denials logged.
- **Exposure:** HTTP `/api/me`, shell load.
- **Tests:** valid user, no membership, disabled user, invalid context, cross-tenant context, capabilities redacted.

### `core.context.select`
- **Class:** command.
- **Actors:** browser.
- **Scope:** active membership in selected tenant/customer/owner context.
- **DTOs:** input `{contextId}`; output same as `core.access.me` selected context subset.
- **Validation:** context id exists for account; membership active; roles non-empty.
- **Data:** writes UserSettings/default context or session state.
- **Side effects:** audit event.
- **Idempotency:** selecting current context is no-op with audit optional as read.
- **Exposure/tests:** HTTP API; tests for forbidden cross-account context and disabled membership.

### `core.bootstrap.admin`
- **Class:** governance/command.
- **Actors:** `BootstrapInitializer`, SaaS Owner installation operator.
- **Scope:** bootstrap configuration only; one-time idempotent seed.
- **DTOs:** `{tenantName, adminEmail, role, idempotencyKey}` -> `{tenantId, invitationId|membershipId, created}`.
- **Validation:** only allowed when no owner/tenant admin exists or explicit bootstrap policy permits.
- **Data:** creates Tenant, Account placeholder, Membership/Role or Invitation.
- **Side effects:** invitation email via Resend/outbox, audit.
- **Audit:** `AdminBootstrapPerformed`, `InvitationCreated`, `InvitationEmailQueued`.
- **Tests:** duplicate idempotency, last-admin bootstrap, forbidden after configured.

### `core.email.send_invitation`
- **Class:** reactive/command.
- **Actors:** User Admin capabilities, EmailDeliveryConsumer.
- **Scope:** invitation tenant/customer scope.
- **DTOs:** `{invitationId, recipientEmail, templateId, locale}` -> `{deliveryStatus, messageId?}`.
- **Validation:** invitation pending, recipient matches, template approved.
- **Data:** writes EmailOutbox/Invitation delivery status.
- **Side effects:** production Resend call; local/dev/test captured outbox only.
- **Idempotency:** idempotency key per invitation delivery attempt; duplicate resend returns existing status or creates explicit new attempt.
- **Audit:** `InvitationEmailQueued`, `InvitationEmailSent`, `InvitationEmailCaptured`, `InvitationEmailFailed`.
- **Exposure:** internal service, consumer, tests; no direct browser secret exposure.

### `core.authz.evaluate`
- **Class:** read/evidence/internal governance.
- **Actors:** endpoints, workflows, tools, timers, consumers.
- **Scope:** supplied AuthContext or stored service authority basis.
- **DTOs:** `{capabilityId, action, resourceScope, targetIds}` -> `{allowed, reasonCode, redactions}`.
- **Validation:** known capability; context active; resource scope compatible.
- **Data:** reads roles/capabilities/memberships/support access.
- **Side effects:** denial audit for protected actions.
- **Exposure:** internal only.
- **Tests:** missing role, wrong tenant, disabled account, service actor authority.

### `core.audit.record`
- **Class:** trace/audit.
- **Actors:** all protected backend code.
- **Scope:** tenant/customer or SaaS Owner scope.
- **DTOs:** `{eventName, actor, target, capabilityId, status, metadata, redactions, correlationId}` -> `{auditEventId, traceId}`.
- **Validation:** event schema known; sensitive fields redacted.
- **Data:** appends durable AdminAuditEvent.
- **Idempotency:** dedupe by `correlationId + eventName + target + idempotencyKey` when supplied.
- **Exposure:** internal, Audit workstream views.
- **Tests:** event creation, redaction, tenant isolation, denied-event persistence.

## 7. Akka realization expectations

- **Event Sourced Entities:** `AdminAuditEventEntity` or audit-log entity for immutable audit events; `InvitationEntity` where lifecycle history matters; possibly `TenantEntity` for bootstrap events.
- **Key Value Entities:** `AccountEntity`, `UserProfileEntity`, `UserSettingsEntity`, `MembershipEntity`, `RoleCapabilityRegistryEntity`, `EmailOutboxMessageEntity` for current state where full event replay is not needed.
- **Workflows:** `InvitationWorkflow` for create/send/resend/accept/expire; bootstrap may use direct idempotent command unless multi-step.
- **Views:** `MembershipView`, `UserDirectoryView`, `InvitationView`, `AdminAuditView`, `CapturedOutboxView`.
- **Consumers:** email delivery/outbox consumer; audit projection consumers if needed.
- **Timed Actions:** invitation expiry/reminder; support-access expiry reserved for User Admin.
- **Agents:** seed governed records only; no LLM call required in foundation.
- **HTTP endpoints:** WorkOS JWT protected `/api/me`, context selection, static frontend, health/version; admin endpoints added in PRD 2-4.
- **SSE/WebSocket:** optional shell notification stream for context/capability changes; polling acceptable for first implementation.
- **Frontend:** React/Vite/TS shell, AuthKit integration, typed API client, workstream rail, composer, shared surface renderer, denial/error mapping, static build hosted by Akka.

## 8. Internal agents, workflows, and event-driven processing

- **Workflows:** invitation lifecycle persists delivery state, retries transient Resend failures, records captured outbox in dev/test, expires pending invites.
- **Consumers:** process EmailOutbox messages idempotently; never read frontend secrets.
- **Timers:** schedule invitation expiry/reminder with tenant scope and audit authority basis.
- **Events:** `AdminBootstrapPerformed`, `AccountLinked`, `MembershipActivated`, `InvitationCreated`, `InvitationAccepted`, `InvitationExpired`, `AuditEventRecorded`.
- **Retries/compensation:** email send retries update delivery attempts; no duplicate membership creation on repeated invitation acceptance.
- **Traces:** every workflow step carries correlation id and audit event links.

## 9. Security, audit, and compliance

- WorkOS validates identity; Akka-owned records authorize capabilities.
- Every protected endpoint, component command/query, stream, workflow, consumer, timer, and tool checks backend authorization.
- Tenant isolation enforced by scoped ids and query filters.
- Support/admin boundaries require explicit support-access membership; no omnipotent SaaS Owner tenant-data reads.
- Secrets: WorkOS and Resend secrets backend-only; frontend receives public AuthKit config only.
- Audit events: identity, account linking, context selection, invitation lifecycle, email delivery, denials, sensitive reads, bootstrap, static frontend version served if needed.
- Tests: cross-tenant, disabled user, missing role, frontend secret scanning, audit redaction, denial logging.

## 10. Acceptance criteria

- **Backend:** `/api/me` returns correct browser-safe AuthContext and capabilities; bootstrap is idempotent; invitation/email foundation works with Resend adapter and captured outbox adapter.
- **Frontend:** AuthKit sign-in, context selector, functional-agent rail, authority indicators, shell loading/empty/forbidden/stale states render correctly.
- **Auth/security:** all protected APIs reject missing/invalid JWT, disabled account, inactive membership, wrong tenant/customer, missing capability.
- **Audit/trace:** authorized and denied foundation actions create scoped redacted audit events with trace ids.
- **Workflows/events/timers:** invitation send/resend/expiry flow is retry-safe and auditable.
- **Fullstack behavior:** authenticated user can sign in, select context, see only authorized workstreams, and receive safe denials.
- **Tests:** unit/integration/frontend tests cover `/api/me`, authz, tenant isolation, email outbox, static hosting, frontend secret boundary, and smoke navigation.

## 11. Open questions

- What bootstrap policy identifies the first SaaS Owner or first Tenant Admin in each deployment environment?
- Are Customer-scoped memberships required in the first implementation or should the first release support Tenant scope only while preserving Customer fields?
