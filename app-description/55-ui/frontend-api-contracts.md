# Frontend API Contracts

Canonical workstream UI calls protected backend APIs for data/actions; routes are not authorization. This file exists to prevent unsafe inference from compatibility endpoints: generated browser behavior must use the workstream shell contracts unless a surface explicitly names a protected compatibility/service API as additional evidence.

## User Admin compatibility contracts

Canonical User Admin browser runtime uses the workstream shell contracts (`/api/workstream/bootstrap`, `/api/workstream/actions`, `/api/workstream/shell-requests`, and `/api/workstream/messages`) for typed structured surfaces. The `/api/admin/**` routes are protected compatibility/service APIs for direct JSON access, local smoke checks, and downstream integrations; they must preserve the same backend authorization, selected-context, idempotency, redaction, and audit semantics but do not replace the structured surface envelopes. Compatibility routes must not be used to infer page-first UI, frontend-only authority, missing workstream actions, billing/support/customer-success behavior, or fixture-backed normal runtime success.

- `GET /api/admin/users/dashboard` returns a compatibility `UserAdminDashboardPayload` with scoped dashboard counters, trace refs, and authorized actions. Rich dashboard surface rendering is sourced from `surface-user-admin-dashboard` through the workstream shell.
- `GET /api/admin/users` returns scoped user/invitation rows.
- `GET /api/admin/users/{accountId}` returns `UserAdminUserAccountPayload` for an authorized account detail.
- `POST /api/admin/invitations` creates an invitation request without exposing raw invitation tokens/token hashes.
- `POST /api/admin/invitations/{invitationId}/resend` resends when authorized.
- `POST /api/admin/invitations/{invitationId}/revoke` revokes when authorized.

## SaaS Owner Admin user-management contracts

SaaS Owner Admins must be able to invite and manage other app-owner administrators from a SaaS Owner selected `AuthContext`. These contracts are protected by backend `saas_owner.admin.*` capabilities and never expose tenant/customer application data, raw provider ids, raw invitation tokens, or provider secrets.

- `GET /api/admin/saas-owner-admins?query=&status=&pageSize=&pageToken=` returns browser-safe SaaS Owner Admin memberships and app-owner invitations with redaction, trace refs, and correlation id.
- `POST /api/admin/saas-owner-admins/invitations` invites another SaaS Owner Admin from `{ email, displayName?, roles: ['SAAS_OWNER_ADMIN'], idempotencyKey, reason?, correlationId? }` and returns invitation/detail status or validation/duplicate/outbox-blocked/forbidden system message.
- `POST /api/admin/saas-owner-admins/invitations/{invitationId}/resend` and `/revoke` manage app-owner invitations when authorized.
- `PUT /api/admin/saas-owner-admins/{accountId}/roles` and `POST /api/admin/saas-owner-admins/{accountId}/suspend|reactivate|remove` manage app-owner admin memberships with last-owner-admin and self-action protections.

## SaaS Owner Organization Admin contracts

Browser-facing contracts use **Organization** terminology. Backend implementation maps `organizationId` to internal Tenant ids and enforces SaaS Owner `AuthContext` plus `saas_owner.tenant.read/manage` capabilities server-side.

- `GET /api/admin/organizations?query=&status=&pageSize=&pageToken=` returns `OrganizationListPayload` with browser-safe Organization summaries, filters, trace refs, correlation id, and no hidden counts or tenant/customer app data.
- `GET /api/admin/organizations/{organizationId}` returns `OrganizationDetailPayload` with safe lifecycle metadata, boundary notice, visible actions, recent redacted audit excerpts, trace refs, and no provider secrets, billing-derived authority, support-access internals, or tenant app data.
- `POST /api/admin/organizations` creates an Organization/Tenant boundary from `{ organizationName, idempotencyKey, reason?, correlationId? }` and returns a refreshed `OrganizationDetailPayload` or typed validation/duplicate/forbidden/no-op system message.
- `POST /api/admin/organizations/{organizationId}/rename` renames an Organization from `{ organizationName, idempotencyKey, reason?, correlationId? }` and returns refreshed detail/list state or typed validation/no-op/stale/forbidden system message.
- `POST /api/admin/organizations/{organizationId}/suspend` suspends an Organization from `{ reason, idempotencyKey, correlationId? }` and returns refreshed detail/list state, safe lifecycle warning, or typed no-op/forbidden system message.
- `POST /api/admin/organizations/{organizationId}/reactivate` reactivates an Organization from `{ idempotencyKey, reason?, correlationId? }` and returns refreshed detail/list state or typed no-op/forbidden system message.
- `GET /api/admin/organizations/{organizationId}/admins?query=&status=&pageSize=&pageToken=` lists Organization Admin users and invitations for the selected Organization/Tenant with browser-safe membership/invitation summaries, last-admin risk flags, trace refs, correlation id, and no tenant application data.
- `POST /api/admin/organizations/{organizationId}/admins/invitations` bootstraps/invites an Organization Admin from `{ email, displayName?, roles: ['TENANT_ADMIN'], idempotencyKey, reason?, correlationId? }` after the Organization exists and returns admin invitation/detail status or validation/duplicate/outbox-blocked/forbidden system message.
- `POST /api/admin/organizations/{organizationId}/admins/invitations/{invitationId}/resend` and `/revoke` manage Organization Admin invitations when authorized.
- `PUT /api/admin/organizations/{organizationId}/admins/{accountId}/roles` and `POST /api/admin/organizations/{organizationId}/admins/{accountId}/suspend|reactivate|remove` manage Organization Admin memberships with target-organization validation, `SAAS_OWNER_ADMIN` escalation denial, and last-organization-admin protection.

DTOs:

- `OrganizationSummary`: `{ organizationId, organizationName, status, updatedAt?, safeLifecycleSummary?, visibleTenantAdminCount?, traceRefs[] }`.
- `OrganizationDetailPayload`: `{ organization, safeBoundaryNotice, visibleActions[], recentAuditEvents[], traceRefs[], correlationId, redaction }`.
- `OrganizationListPayload`: `{ organizations[], filters, pageInfo, traceRefs[], correlationId, redaction }`.
- `OrganizationActionResult`: `{ status, organization?, systemMessage?, traceRefs[], correlationId }`.
- `AdminSubjectSummary`: `{ accountId?, invitationId?, displayName?, email?, scopeType, tenantId?, roles[], status, invitationStatus?, deliveryStatus?, lastAdminRisk?, visibleActions[], traceRefs[] }`.
- `SaasOwnerAdminListPayload`: `{ admins[], invitations[], filters, pageInfo, traceRefs[], correlationId, redaction }`.
- `OrganizationAdminListPayload`: `{ organization, admins[], invitations[], filters, pageInfo, safeBoundaryNotice, traceRefs[], correlationId, redaction }`.

## Organization Admin Customer and Customer Admin contracts

Organization Admins must be able to create/manage Customers within their selected Organization/Tenant and invite/manage Customer Admin users for each Customer. These contracts are protected by backend `tenant.customer.*` and `tenant.customer_admin.*` capabilities and never expose sibling-customer facts, tenant-wide application data, raw provider ids, raw invitation tokens, or provider secrets.

- `GET /api/admin/customers?query=&status=&pageSize=&pageToken=` returns `CustomerListPayload` with browser-safe Customer summaries for the selected Organization/Tenant, filters, trace refs, correlation id, and redaction.
- `GET /api/admin/customers/{customerId}` returns `CustomerDetailPayload` with safe lifecycle/admin metadata, boundary notice, visible actions, recent redacted audit excerpts, trace refs, and no sibling-customer or tenant application data.
- `POST /api/admin/customers` creates a Customer from `{ customerName, idempotencyKey, reason?, correlationId? }` in the selected Organization/Tenant and returns refreshed detail or typed validation/duplicate/forbidden/no-op system message.
- `POST /api/admin/customers/{customerId}/rename` renames/updates a Customer from `{ customerName, idempotencyKey, reason?, correlationId? }` and returns refreshed detail/list state or typed validation/no-op/stale/forbidden system message.
- `POST /api/admin/customers/{customerId}/suspend` suspends/archives a Customer from `{ reason, idempotencyKey, correlationId? }` and returns refreshed detail/list state or typed no-op/forbidden system message.
- `POST /api/admin/customers/{customerId}/reactivate` reactivates a Customer from `{ reason?, idempotencyKey, correlationId? }` and returns refreshed detail/list state or typed no-op/forbidden system message.
- `GET /api/admin/customers/{customerId}/admins?query=&status=&pageSize=&pageToken=` lists Customer Admin users and invitations for the selected Customer with browser-safe membership/invitation summaries, last-admin risk flags, trace refs, correlation id, and no sibling-customer data.
- `POST /api/admin/customers/{customerId}/admins/invitations` bootstraps/invites a Customer Admin from `{ email, displayName?, roles: ['CUSTOMER_ADMIN'], idempotencyKey, reason?, correlationId? }` after the Customer exists and returns admin invitation/detail status or validation/duplicate/outbox-blocked/forbidden system message.
- `POST /api/admin/customers/{customerId}/admins/invitations/{invitationId}/resend` and `/revoke` manage Customer Admin invitations when authorized.
- `PUT /api/admin/customers/{customerId}/admins/{accountId}/roles` and `POST /api/admin/customers/{customerId}/admins/{accountId}/suspend|reactivate|remove` manage Customer Admin memberships with target-customer validation, `TENANT_ADMIN`/`SAAS_OWNER_ADMIN` escalation denial, and last-customer-admin protection.

DTOs:

- `CustomerSummary`: `{ customerId, customerName, status, updatedAt?, safeLifecycleSummary?, visibleCustomerAdminCount?, traceRefs[] }`.
- `CustomerDetailPayload`: `{ customer, safeBoundaryNotice, visibleActions[], recentAuditEvents[], traceRefs[], correlationId, redaction }`.
- `CustomerListPayload`: `{ customers[], filters, pageInfo, traceRefs[], correlationId, redaction }`.
- `CustomerActionResult`: `{ status, customer?, systemMessage?, traceRefs[], correlationId }` for Customer create, rename, suspend/archive, reactivate, stale, validation, no-op, duplicate, forbidden, and hidden/not-found outcomes.
- `CustomerAdminListPayload`: `{ customer, admins[], invitations[], filters, pageInfo, safeBoundaryNotice, traceRefs[], correlationId, redaction }`.
- `CustomerAdminActionResult`: `{ status, customer?, adminSubject?, invitation?, systemMessage?, traceRefs[], correlationId }` for Customer Admin invitation, resend, revoke, role, suspend, reactivate, remove, last-admin-risk, stale, validation, no-op, forbidden, and hidden/not-found outcomes.

## Workstream shell contracts

The canonical frontend runtime is the workstream shell described by `global/surfaces/ui-style-and-runtime-contracts.md`. These endpoints return typed structured surface envelopes; compatibility `/api/admin/**` JSON routes do not replace this contract.

- `POST /api/workstream/bootstrap` loads `/api/me`-safe shell state, selected `AuthContext`, authorized functional-agent rail entries, initial workstream attention, structured surfaces, realtime connection metadata, trace/correlation refs, and redaction metadata. Missing bearer, disabled account, inactive membership, missing selected context, provider/model/outbox blockers, or hidden scope returns a typed safe shell/system-message state without secrets or hidden object enumeration.
- `POST /api/workstream/actions` executes capability-backed surface actions with `X-Selected-Context-Id`, backend reauthorization, idempotency/correlation metadata where needed, and typed result surfaces. Browser action ids, disabled controls, route params, and local state are advisory only; backend capability, scope, policy, provider readiness, and approval gates are authoritative.
- `POST /api/workstream/messages` submits a user turn to the selected functional agent/workstream. It records the message, resolves governed agent behavior and evidence tools, invokes the configured provider only when policy/runtime prerequisites pass, and returns a `markdown-response`, structured surface, or fail-closed `system-message`. It must not return canned/model-less normal success when provider/security configuration is missing.
- `POST /api/workstream/shell-requests` handles shell-level commands such as selected-context refresh/switch, workstream open, surface/deep-link reopen, notification/read-state updates, and safe recovery routing. Every request reuses backend `AuthContext` resolution and returns updated shell state or a typed denial/stale/system surface.
- `GET /api/workstream/events` streams server-sent workstream events for the selected context. Events include typed surface updates, projection refresh notices, notification/attention changes, stale/reconnect markers, and bounded replay metadata. The browser ignores malformed, out-of-order, duplicate, or cross-context events and never treats events as authorization grants.

Common envelope fields across these contracts: `surfaceId`, `surfaceType`, `surfaceContract`, `functionalAgentId`, `workstreamId`, `selectedAuthContext`, `capabilityDecision`, `authorizedActions[]`, `traceRefs[]`, `correlationId`, `idempotencyKeyHint?`, `redaction`, `systemStates`, `staleVersion?`, and `realtimeStatus?`. Raw JWT/session values, provider secrets/payloads, invitation tokens/token hashes, raw prompts/model responses, hidden tenant/customer ids, unredacted audit evidence, and raw idempotency keys are forbidden in browser payloads.

## Unsafe-inference rejection rules

- A browser route, query string, disabled button, fixture row, mock response, or compatibility `/api/admin/**` response never grants authority and never creates product scope outside the surface graph.
- If a requested action is retired, deferred, unavailable, provider-blocked, model-blocked, outbox-blocked, hidden, or outside the selected `AuthContext`, the backend returns a typed safe system-message/result surface rather than a guessed success payload.
- Workstream shell payloads must emit canonical workstream, surface, action, capability, and functional-agent ids. Retired ids may be accepted only as stale-client inputs that produce safe unsupported-action/system-message results with trace evidence.
