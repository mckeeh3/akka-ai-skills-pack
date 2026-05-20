# Starter Workstream API Contracts

## Purpose

This is the implementation-ready browser/backend API contract for wiring the starter app's canonical agent workstream shell to real backend endpoints. It refines the generic contracts in `specs/core-app-full-stack-readiness/core-workstream-api-contracts.md` for the starter template under `templates/ai-first-saas-starter/`.

The contract covers:

- `/api/me` bootstrap and selected `AuthContext` handling;
- functional-agent rail discovery;
- workstream item and structured-surface queries;
- capability-backed surface actions;
- realtime/stale event delivery;
- User Admin vertical endpoint alignment for the next implementation task.

## Sources and current alignment

| Source | Role |
| --- | --- |
| `docs/workstream-ui-reference-architecture.md` | canonical shell/client/source layout |
| `docs/structured-surface-contracts.md` | surface/action/event envelope rules |
| `docs/web-ui-api-contract-patterns.md` | Akka HTTP/browser DTO/error conventions |
| `specs/core-app-full-stack-readiness/core-workstream-api-contracts.md` | full-core API family inventory |
| `frontend/src/api/WorkstreamApiClient.ts` | current frontend client seam |
| `frontend/src/workstream/types/**` | current browser DTO names and unions |
| `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/security/MeEndpoint.java` | existing `/api/me` backend route |
| `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/*` | existing security/admin service seams |

Current frontend DTOs already match the generic workstream contract. The backend starter `/api/me` response is close but uses older names (`selectedContext`, `availableContexts`, `navigationCapabilities`, `agentId`) and should be normalized to the frontend names below before production UI wiring.

## Contract invariants

- Browser state bootstraps from `GET /api/me` or `GET /api/workstream/bootstrap`; protected UI does not render privileged data before bootstrap succeeds.
- Every request is scoped by authenticated account plus selected `AuthContext`.
- `X-Selected-Context-Id` is the canonical browser-selected context header. The existing backend `X-Selected-Membership-Id` header should be accepted as a backward-compatible alias only until the workstream API endpoints are added.
- `X-Correlation-Id` is accepted on every protected request; if absent, the backend generates one and echoes it in DTOs/results.
- Frontend visible actions and agents are UX hints only. Backend authorization remains authoritative for every route and capability.
- Surface payloads never include provider secrets, raw JWTs, raw invitation tokens, token hashes, Resend secrets, cross-tenant data, or backend-only permission internals.
- All protected reads/actions produce audit or work-trace records when they expose protected data, deny access, mutate state, start workflows, send email, or access consequential traces.

## API result and error shape

Frontend clients return the existing `ApiResult<T>` shape from `frontend/src/api/types`.

Backend non-2xx responses should be normalized by the client into:

| HTTP | Client error kind | Body contract |
| --- | --- | --- |
| 400 | `validation` or `badRequest` | `{ "message": string, "fields"?: [{ "field": string, "message": string }] }` |
| 401 | `unauthorized` | `{ "message": string, "reasonCode": "AUTHENTICATION_REQUIRED" | string, "correlationId"?: string }` |
| 403 | `forbidden` | `{ "message": string, "reasonCode": string, "correlationId"?: string, "traceIds"?: string[] }` |
| 404 | `notFound` | `{ "message": string, "reasonCode": "TARGET_NOT_FOUND_OR_FORBIDDEN" | string, "correlationId"?: string }` |
| 409 | `conflict` | `{ "message": string, "reasonCode": string, "correlationId"?: string, "traceIds"?: string[] }` |
| 5xx | `server` | `{ "message": string, "correlationId"?: string }` |
| network/malformed | `network` / `malformed` | client-created safe error |

Reason codes used by fixtures and backend implementations must include the canonical codes from `core-workstream-api-contracts.md`: `ACCOUNT_DISABLED`, `CONTEXT_REQUIRED`, `CONTEXT_FORBIDDEN`, `MEMBERSHIP_NOT_ACTIVE`, `TARGET_NOT_FOUND_OR_FORBIDDEN`, `ROLE_ESCALATION_DENIED`, `LAST_ADMIN_DENIED`, `APPROVAL_REQUIRED`, `AUTHORITY_EXPANSION_REQUIRES_APPROVAL`, `AGENT_ARTIFACT_INACTIVE`, `TOOL_BOUNDARY_DENIED`, `MODEL_POLICY_DENIED`, `TRACE_REDACTED`, and `CROSS_CONTEXT_EVENT_IGNORED`.

## Shared request headers

| Header | Required | Meaning |
| --- | --- | --- |
| `Authorization: Bearer <jwt>` | yes for protected APIs | WorkOS/AuthKit-backed user identity token. Local tests may use starter test JWT fixtures. |
| `X-Selected-Context-Id` | yes after bootstrap when multiple contexts exist | Selected browser-safe `AuthContext.selectedContextId`. |
| `X-Correlation-Id` | recommended | Stable client/request correlation id; generated server-side if omitted. |
| `Last-Event-ID` | SSE reconnect only | Last processed workstream event id. |

## Endpoint families

| Client method | Method/route | Capability class | Notes |
| --- | --- | --- | --- |
| `bootstrap()` | `GET /api/workstream/bootstrap` | read/evidence | Aggregates `/api/me`, functional agents, initial items, and default surfaces for selected context. |
| `getMe()` | `GET /api/me` | read/evidence | Browser-safe account/profile/settings/membership/context/capability bootstrap. |
| `listFunctionalAgents()` | `GET /api/workstream/functional-agents` | read/evidence | Returns role-authorized rail entries; can be derived from `/api/me`. |
| `listWorkstreamItems(functionalAgentId?)` | `GET /api/workstream/items?functionalAgentId=...` | read/evidence | Initial or refreshed stream items scoped to selected context. |
| `getSurface(surfaceId)` | `GET /api/workstream/surfaces/{surfaceId}` | read/evidence | Returns one `SurfaceEnvelope<unknown>` or hidden not-found/forbidden. |
| `runCapabilityAction(request)` | `POST /api/workstream/actions` | command/proposal/approval/workflow/governance/trace | Generic action dispatcher enforcing capability contract and idempotency. |
| realtime client | `GET /api/workstream/events` | stream/read | SSE stream scoped by selected context and optional functional agent/surface filters. |

Module-specific User Admin routes may also exist under `/api/admin/**`; the generic workstream routes either compose them into surfaces or delegate to the same application services.

## `/api/me` contract

### Route

```text
GET /api/me
Authorization: Bearer <jwt>
X-Selected-Context-Id: <selectedContextId>   # optional on first request
X-Correlation-Id: <correlationId>            # optional
```

### Response DTO

Use the existing frontend type names:

```ts
type MeResponse = {
  account: {
    accountId: string;
    email: string;
    displayName: string;
    status: "active" | "disabled" | "pending";
  };
  profile: { displayName: string; locale?: string; timeZone?: string };
  settings: { preferredColorMode?: "light" | "dark" | "system" };
  memberships: MembershipSummary[];
  selectedAuthContext: AuthContext;
  availableAuthContexts: AuthContext[];
  visibleCapabilityIds: string[];
  functionalAgents: FunctionalAgentSummary[];
};
```

### Backend starter normalization required

Before wiring the production frontend, update the starter backend `MeResponse` JSON names as follows:

| Existing backend field | Required browser field |
| --- | --- |
| `selectedContext` | `selectedAuthContext` |
| `availableContexts` | `availableAuthContexts` |
| `navigationCapabilities` | `visibleCapabilityIds` |
| `FunctionalAgentSummary.agentId` | `functionalAgentId` |
| `FunctionalAgentSummary.displayName` | `label` |
| `FunctionalAgentSummary.available` | `availability: "visible" | "denied" | "disabled" | "hidden"` plus `requiredCapabilityIds` |
| uppercase enum values | lowercase browser enums (`active`, `disabled`, `pending`, etc.) |

The backend may retain additional internal fields only if they are browser-safe and ignored by the frontend, but generated starter output should prefer the browser contract exactly.

### Authorization and denial behavior

- Missing/invalid JWT: `401 AUTHENTICATION_REQUIRED`.
- Disabled account: either `200` with `account.status = "disabled"`, no privileged capabilities, denied/disabled agents, and no protected surfaces, or `403 ACCOUNT_DISABLED`; the UI must handle both.
- No active membership or invalid selected context: `403 CONTEXT_REQUIRED` or `CONTEXT_FORBIDDEN`; test fixtures must include safe recovery copy.
- Cross-tenant/customer membership selection is denied server-side and audited.

## `GET /api/workstream/bootstrap`

### Purpose

Single-call shell bootstrap for production UI. It avoids multiple loading passes while preserving `/api/me` as the authoritative self/context contract.

### Response

```ts
type WorkstreamBootstrapResponse = {
  me: MeResponse;
  functionalAgents: FunctionalAgentSummary[];
  items: WorkstreamItem[];
  surfaces: SurfaceEnvelope<unknown>[];
};
```

### Rules

- `functionalAgents` must match `me.functionalAgents` unless the endpoint intentionally includes richer rail data; drift is a contract failure.
- `items` should include at least the selected/default functional agent's initial workstream items.
- `surfaces` should include the surfaces referenced by returned `items` when those surfaces are needed for first paint.
- Bootstrap must be safe for partial data: missing optional modules return hidden/disabled agents or placeholder stale/empty surfaces, not unscoped data.

## Functional agents contract

Use existing frontend type:

```ts
type FunctionalAgentSummary = {
  functionalAgentId: string;
  label: string;
  purpose: string;
  icon?: string;
  defaultSurfaceType: string;
  requiredCapabilityIds: string[];
  attention?: { count: number; severity: "info" | "warning" | "critical" };
  availability: "visible" | "hidden" | "denied" | "disabled";
  deniedReason?: string;
};
```

Starter foundation agents:

| Functional agent id | Default surface | Visible when | Required backend capability basis |
| --- | --- | --- | --- |
| `access-profile` | `detail-edit` | active membership | `profile.read`, optionally `profile.update` |
| `user-admin` | `dashboard` | tenant/customer/SaaS owner admin or auditor role | `core.user_admin.read`; actions need `core.user_admin.manage` or scoped equivalents |
| `audit-trace` | `audit-timeline` | auditor/admin/support authority | `audit.trace.read` or module-scoped audit read |
| `governance-policy` | `governance-diff` | policy/governance role | `governance.policy.read` and action-specific proposal/approval capabilities |
| `agent-admin` | `governance-diff` | disabled/hidden until agent-governance sprint completes unless role has agent governance capabilities | `agent.definitions.manage`, `agent.prompts.govern`, `agent.skills.govern`, etc. |

## Workstream item contract

Route:

```text
GET /api/workstream/items?functionalAgentId=<optional>&limit=<optional>&cursor=<optional>
```

Response body for the first implementation wave may be `WorkstreamItem[]`; if pagination is added, wrap it as `{ items, nextCursor }` and update the client contract in the same task.

Each item must include `itemId`, `functionalAgentId`, `kind`, `createdAt`, `correlationId`, and `traceIds`. Items referencing a surface include `surfaceId`. Items must be scoped to the selected context and must not leak cross-context traces.

## Surface query contract

Route:

```text
GET /api/workstream/surfaces/{surfaceId}
```

Response body is the existing `SurfaceEnvelope<unknown>` shape from `frontend/src/workstream/types/surfaces.ts`.

Required surface rules:

- `surfaceId`, `surfaceType`, `surfaceVersion`, `title`, `ownerFunctionalAgentId`, `authContext`, `correlationId`, `traceIds`, `generatedAt`, `redaction`, `data`, and `actions` are mandatory.
- `authContext.selectedContextId`, `tenantId`, and optional `customerId` must match the authorized request context.
- `actions[].capabilityId`, `actions[].idempotency`, and `actions[].audit` are mandatory for every action.
- Forbidden or hidden surfaces return `403`/`404` with `TARGET_NOT_FOUND_OR_FORBIDDEN` rather than a partial cross-scope payload.
- Stale data is represented by `stale.isStale = true` and a safe `reason`; stale is not a security bypass.

## Capability action contract

Route:

```text
POST /api/workstream/actions
Content-Type: application/json
```

Request DTO:

```ts
type CapabilityActionRequest = {
  actionId: string;
  capabilityId: string;
  input: unknown;
  idempotencyKey?: string;
  selectedContextId: string;
  surfaceId?: string;
  correlationId: string;
};
```

Response DTO:

```ts
type CapabilityActionResult = {
  status: "accepted" | "denied" | "validation-error" | "approval-required" | "conflict" | "no-op" | "failed";
  message: string;
  correlationId: string;
  traceIds: string[];
  resultSurface?: SurfaceEnvelope<unknown>;
};
```

Rules:

- If the referenced surface action declared `idempotency.required = true`, the backend rejects missing `idempotencyKey` with validation error.
- Duplicate idempotency keys return `no-op` or the original accepted result and preserve audit trace linkage.
- `selectedContextId` in the body must match the resolved request context; mismatch is `403 CONTEXT_FORBIDDEN` and audited.
- The backend validates `actionId` belongs to `surfaceId` when `surfaceId` is supplied, but authorization still checks `capabilityId` and selected context.
- Action visibility in a previous surface does not authorize stale submissions; stale or unavailable actions return `conflict`, `denied`, or `approval-required`.
- Consequential side effects must append audit/work-trace records and return trace ids.

## User Admin vertical contracts for next task

The next task wires the User Admin workstream UI to real endpoints. It should implement these surfaces first, using the generic routes above and delegating to existing starter `UserAdminService`, `InvitationService`, `InvitationView`, and `UserDirectoryView` seams.

### User Admin command center surface

| Field | Contract |
| --- | --- |
| `surfaceType` | `dashboard` |
| `surfaceId` | stable per selected context, e.g. `surface:user-admin:dashboard:<selectedContextId>` |
| Data | cards for pending invitations, active users, suspended memberships, support access grants, delivery failures, and recent admin audit |
| Read capability | `core.user_admin.read` mapped to starter scoped capabilities (`tenant.user.read`, `customer.user.read`, `saas_owner.user.manage`) |
| Actions | invite user, open user list, open invitation queue, open access review, open admin audit |
| Denials | forbidden for missing read scope; partial data for audit excerpt redaction |

### Users/invitations list surface

| Field | Contract |
| --- | --- |
| `surfaceType` | `list-search` |
| Data | scoped rows from `UserDirectoryView` and `InvitationView`; rows must omit raw invitation tokens/token hashes |
| Read capability | `core.user_admin.read` |
| Actions | open detail, resend invite, revoke invite, suspend/reactivate membership |
| States | ready, empty, forbidden, partial-data, stale, conflict |

### User detail/edit surface

| Field | Contract |
| --- | --- |
| `surfaceType` | `detail-edit` |
| Data | account/profile/settings summary, memberships, invitation state, support access, audit links |
| Read capability | `core.user_admin.read` |
| Manage actions | replace roles, suspend/reactivate membership, disable account, resend/revoke invitation, grant/revoke support access |
| Required denials | `LAST_ADMIN_DENIED`, `ROLE_ESCALATION_DENIED`, `TARGET_NOT_FOUND_OR_FORBIDDEN`, `MEMBERSHIP_NOT_ACTIVE`, `APPROVAL_REQUIRED` |

### User Admin action mappings

| Surface action | Capability id | Backend service seam | Idempotency | Result surface |
| --- | --- | --- | --- | --- |
| Invite user | `core.user_admin.manage` + invitation create | `InvitationService.createInvitation` | required, client-generated | workflow-status or updated dashboard/list |
| Resend invitation | `core.user_admin.manage` | `InvitationService.resend` | required | action-feedback plus updated list/detail |
| Revoke invitation | `core.user_admin.manage` | `InvitationService.revoke` | required | updated list/detail |
| Replace roles | `core.user_admin.manage` | `UserAdminService.replaceRoles` | required | updated detail or denial/approval result |
| Suspend membership | `core.user_admin.manage` | `UserAdminService.suspendMembership` | required | updated detail/list |
| Disable account | `core.user_admin.manage` | `UserAdminService.disableAccount` | required | updated detail/list |
| Open audit trace | `audit.trace.read` | audit repository/view seam | not required | audit-timeline surface |

## Realtime and stale event contract

Route:

```text
GET /api/workstream/events?functionalAgentId=<optional>&surfaceId=<optional>
Accept: text/event-stream
Last-Event-ID: <optional>
```

Event body uses the existing frontend `WorkstreamEvent` / `SurfaceEvent` shape:

```ts
type WorkstreamEvent<TPatch = unknown> = {
  eventId: string;
  eventType:
    | "workstream.item.appended"
    | "workstream.item.updated"
    | "surface.created"
    | "surface.updated"
    | "surface.action.accepted"
    | "surface.action.denied"
    | "surface.workflow.progressed"
    | "surface.stale"
    | "surface.reconnected";
  tenantId: string;
  customerId?: string;
  functionalAgentId: string;
  surfaceId?: string;
  surfaceType?: string;
  surfaceVersion?: string;
  correlationId: string;
  traceIds: string[];
  occurredAt: string;
  sequence?: number;
  patch?: unknown;
};
```

Rules:

- Endpoint is protected and scoped to selected context; cross-context events are not emitted.
- Client merge logic treats duplicate, out-of-order, malformed, forbidden, and cross-context events as safe no-ops or stale markers.
- If replay from `Last-Event-ID` is unavailable, backend emits `surface.stale` or `surface.reconnected` so the client refreshes affected surfaces.
- Events may carry patches, but full surface refresh through `GET /api/workstream/surfaces/{surfaceId}` remains the fallback.
- Denied actions can emit `surface.action.denied` with user-safe reason and trace ids; no secret or raw policy internals are included.

## Backend endpoint implementation notes

- Implement workstream endpoints under `{{JAVA_BASE_PACKAGE}}.api.workstream` or `{{JAVA_BASE_PACKAGE}}.api.security` with explicit `@HttpEndpoint` and `@Acl`.
- Use the same JWT/request-context extraction style as `MeEndpoint`, but resolve selected context by `X-Selected-Context-Id`.
- Keep endpoint DTOs browser-facing; do not expose `domain.security` records directly.
- The first starter slice may use application services and in-memory repositories as seams, but route/capability contracts must remain stable when replaced by Akka components.
- The generic `POST /api/workstream/actions` dispatcher should map capability ids to service methods explicitly, not by reflection or trusting the client to name service methods.

## Frontend client implementation notes

- Keep `frontend/src/api/WorkstreamApiClient.ts` as the typed client seam.
- Add a production implementation that calls the routes in this document; keep fixtures/test clients separate.
- Generate idempotency keys in action state before calling `runCapabilityAction` when the selected action requires them.
- Normalize non-2xx, malformed JSON, SSE disconnects, and network failures into visible shell/surface states.
- Do not call `fetch` directly from React components; all workstream calls go through the API client family.

## Test contract

Next implementation tasks should add or extend tests for:

- `/api/me` success, disabled account, no membership, selected-context forbidden, and browser-safe secret boundary;
- workstream bootstrap returning aligned `me`, `functionalAgents`, items, and surfaces;
- User Admin dashboard/list/detail surfaces with capability-backed actions and trace/correlation ids;
- action success, validation error, idempotent replay/no-op, `LAST_ADMIN_DENIED`, `ROLE_ESCALATION_DENIED`, missing capability, disabled actor, and tenant/customer mismatch;
- audit/trace creation for protected reads, denials, invitation actions, role changes, and support-access changes;
- SSE duplicate/out-of-order/malformed/reconnect/stale/cross-context-safe behavior;
- frontend contract/typecheck/build with no secrets or raw tokens in fixtures, rendered output, or static assets.

## Implementation handoff checklist

- [ ] Normalize starter backend `/api/me` field names to the frontend `MeResponse` contract.
- [ ] Add production `WorkstreamApiClient` implementation for the listed routes.
- [ ] Add generic workstream endpoint family or module-specific endpoints plus adapter functions that satisfy the client methods.
- [ ] Implement User Admin surfaces first and keep Agent Admin/Governance placeholders hidden/disabled until their backend sprint lands.
- [ ] Add realtime/stale handling as SSE or explicit stale refresh markers before enabling live supervision surfaces.
- [ ] Preserve backend-authoritative authorization, tenant isolation, idempotency, audit, and denial semantics across every route and action.
