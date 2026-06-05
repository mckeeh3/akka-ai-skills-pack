# Workstream UI Reference Architecture

## Status and scope

This is the target implementation architecture for reusable generated-app **agent workstream shell** UI modules. This repository's runnable core app root is the canonical end-to-end implementation baseline; this document remains the canonical UI architecture contract and reusable frontend reference.

It is a source-repository reference asset. It defines how future tasks should build reusable React/Vite/TypeScript modules under `frontend/src/workstream/**`, while preserving useful generic seams from the current frontend (`api/**`, `design-system/**`, `styles/**`) and replacing `screens/**` as the canonical UI taxonomy.

Canonical doctrine:
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/workstream-visual-sessions.md`
- `docs/web-ui-frontend-decomposition.md`
- source-checkout/root-only migration inventory: `specs/workstream-ui-implementation-migration/frontend-stale-code-inventory.md`

Current implementation references:
- full-core core app baseline source: this repository's runnable core app root
- source-controlled app-description core app surface contracts: `templates/ai-first-saas-core-app/app-description/**`
- reusable React/Vite/TypeScript modules: `frontend/src/workstream/**`
- fixture API and realtime seams: `frontend/src/api/WorkstreamApiClient.ts`, `frontend/src/api/WorkstreamRealtimeClient.ts`
- integrated shell example: `frontend/src/main.tsx`
- canonical User Admin UI vertical pattern: `frontend/src/workstream-user-admin-vertical.contract.test.mjs` plus shared test fixtures under `frontend/src/__tests__/fixtures/**` when needed
- shell/surface/action/deep-link/realtime contract coverage: `frontend/src/workstream*.contract.test.mjs`, `frontend/src/frontend.contract.test.mjs`, and `frontend/src/seed-frontend-quality.contract.test.mjs`

Treat those frontend files as the source-repository implementation reference for future generated SaaS UI work. Test fixtures are contract/test references only; generated user-facing runtime must bind to real backend `/api/me`, workstream APIs, authorization, audit/work-trace, and realtime API paths rather than simulated data. Static UI fixture examples were removed from the pack; generated SaaS UI should use the root workstream frontend reference, not standalone static pages.

## Core rule

Authenticated consequential UI is decomposed as:

```text
/api/me bootstrap
→ selected AuthContext
→ role-authorized functional-agent rail
→ unified shell request pipeline for prompts, actions, rail/My Account selection, and deep links
→ role-specific dashboard attention sources
→ continuous workstream shell
→ human surface graph nodes and edges
→ typed stream items and structured surfaces
→ browser-tool actions backed by governed-tools/capabilities
→ realtime events / stale markers
→ conventional routes only as deep links
```

Routes are implementation details for shell entry, selected functional agent, stream item, or direct surface deep links. They must not become the primary application model.

## Target source layout

Create the reusable reference under `frontend/src/workstream/**`:

```text
frontend/src/workstream/
  types/
    auth.ts                 # /api/me, account, memberships, AuthContext, browser-safe capabilities
    agents.ts              # functional-agent and denied/hidden-agent contracts
    workstream.ts          # workstream items, composer requests, trace links, selection/deep-link state
    surfaces.ts            # SurfaceEnvelope, SurfaceAction, surface data unions, UI state unions
    events.ts              # SurfaceEvent/workstream event contracts and realtime connection state
    actions.ts             # capability action request/result/idempotency/confirmation contracts
    index.ts
  shell/
    WorkstreamShell.tsx    # layout composition: rail + context + stream + composer
    ContextAuthorityBar.tsx
    WorkstreamDeepLinks.ts # parse/serialize selected agent, stream item, surface links and deep-link shell requests
    shellState.ts          # explicit loading/ready/forbidden/stale state transitions plus shell request normalization
    index.ts
  rail/
    FunctionalAgentRail.tsx
    FunctionalAgentRailItem.tsx
    CollapsedRailToggle.tsx
    railState.ts           # visible/hidden/denied/attention/selected-agent behavior
    index.ts
  composer/
    WorkstreamComposer.tsx # persistent prompt input; refocuses after submission/workstream changes so users can type next
    ComposerCommandHints.tsx
    composerState.ts       # draft, submit, disabled/forbidden, selected-agent context
    index.ts
  stream/
    WorkstreamStream.tsx
    WorkstreamItem.tsx
    StreamStatusItem.tsx
    ActionFeedbackItem.tsx
    TraceLinkList.tsx
    streamState.ts         # append/update/group/stale/reconnect merge helpers
    index.ts
  surfaces/
    SurfaceRenderer.tsx
    DashboardSurface.tsx
    ListSearchSurface.tsx
    DetailEditSurface.tsx
    DecisionSurface.tsx
    AuditTimelineSurface.tsx
    WorkflowStatusSurface.tsx
    GovernanceDiffSurface.tsx
    OutcomeSurface.tsx
    SurfaceStateFrame.tsx  # loading/empty/error/forbidden/conflict/stale/partial wrappers
    index.ts
  actions/
    CapabilityActionButton.tsx
    CapabilityActionForm.tsx
    ConfirmationGate.tsx
    ActionResultSurface.tsx
    actionState.ts         # idempotency, disabled/denied, submitting, result-surface mapping
    index.ts
  realtime/
    workstreamEvents.ts    # event parsing, validation, dedupe, merge, stale markers
    useWorkstreamRealtime.ts
    index.ts
  tests/
    *.contract.test.mjs    # focused contract tests for contracts, fixtures, state helpers, rendering text
```

Keep these existing roots as supporting layers:
- `frontend/src/api/**`: typed HTTP/realtime client seam; add workstream client families instead of deleting the seam.
- `frontend/src/design-system/**`: generic primitives; deprecate page-named primitives such as `PageHeader` for canonical workstream usage.
- `frontend/src/styles/**`: token, base, component, and layout CSS; revise layout classes around shell/rail/stream/composer/surface names.

Retire or rebuild later:
- `frontend/src/screens/**` and `*Page.tsx` as canonical taxonomy.
- route-shell tests that assert page navigation rather than functional-agent shell behavior.

## Bootstrap and API contract summary

### `/api/me`

The browser bootstraps from `/api/me` before rendering protected workstream UI.

Minimum browser-safe shape:

```ts
type MeResponse = {
  account: {
    accountId: string;
    email: string;
    displayName: string;
    status: "active" | "disabled" | "pending";
  };
  profile: { displayName: string; locale?: string; timeZone?: string };
  settings: {
    preferredThemeId?: "aurora-light" | "cobalt-light" | "obsidian-dark" | "midnight-dark" | "dark-night";
  };
  memberships: MembershipSummary[];
  selectedAuthContext: AuthContext;
  availableAuthContexts: AuthContext[];
  visibleCapabilityIds: string[];
  functionalAgents: FunctionalAgentSummary[];
};
```

`/api/me` must not expose provider secrets, raw tokens, cross-tenant memberships, or backend-only permission internals.

### AuthContext

```ts
type AuthContext = {
  selectedContextId: string;
  tenantId: string;
  tenantName: string;
  customerId?: string;
  customerName?: string;
  membershipId: string;
  roleIds: string[];
  capabilityIds: string[];
  supportAccess?: { active: boolean; reason?: string; expiresAt?: string };
};
```

The context/authority bar renders selected tenant/customer, role/capability basis, support-access state, disabled/forbidden recovery, pending approvals, and trace links where relevant.

### Functional agents

Functional agents are left-rail work areas, not routes.

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

Foundation fixture agents should include My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, and optional Billing/Support Access examples. Rail rendering must be collapsible and role/capability aware. The signed-in user tile/email at the bottom of the rail opens the My Account workstream, whose dashboard exposes Profile, Settings, and Sign out actions as normal workstream surface actions; My Account must not also appear in the top workstream list.

### Workstream items

```ts
type WorkstreamItem = {
  itemId: string;
  functionalAgentId: string;
  kind:
    | "user-request"
    | "surface-request"
    | "agent-response"
    | "surface"
    | "capability-result"
    | "workflow-status"
    | "decision"
    | "audit-trace"
    | "action-feedback"
    | "system-message"
    | "system-status";
  createdAt: string;
  correlationId: string;
  traceIds: string[];
  surfaceId?: string;
  title?: string;
  body?: string;
  status?: "working" | "waiting-for-human" | "blocked" | "ready" | "failed" | "stale";
  requestOrigin?: "user_prompt" | "surface_action" | "deep_link" | "my_account_panel" | "system_suggestion" | "shell_button";
  canonicalPrompt?: string;
};
```

The stream supports grouped history, stable item ids, append/update semantics, trace links, and action-feedback items for non-chat navigation/actions.

Every new user request is acknowledged as a request surface before the agent response surfaces are shown. This applies to direct composer prompts, prompt-entered shell commands such as `show users list`, the standard composer **Show dashboard** shell button, indirect requests raised by existing surface actions, My Account panels, rail selection, and deep-link entry. The stream uses traditional chat ordering: older turn groups remain above and newer turn groups append below them. When the request item is appended, the workstream scrolls that request surface to the top of the visible panel; any resulting markdown or structured response surfaces append below the request so the user sees the prompt/action first and the agent-selected response surfaces in order. The Show dashboard button is handled directly by the shell rather than routed through the workstream agent: it appends a `Show dashboard` request surface and then the selected workstream's dashboard surface. Workstream-switch request items are appended only in the new target workstream. Use `docs/workstream-visual-sessions.md` for turn-group, anchor, per-workstream session, and phased persistence guidance.

Shell request normalization contract:

```ts
type WorkstreamShellRequest = {
  requestType: "show_surface" | "open_workstream" | "refresh_surface" | "open_attention_item";
  origin: "user_prompt" | "surface_action" | "deep_link" | "my_account_panel" | "system_suggestion" | "shell_button";
  displayText: string;
  canonicalPrompt: string; // e.g. "show surface user-admin-user-list"
  targetFunctionalAgentId?: string;
  targetSurfaceId?: string;
  targetItemId?: string;
  sourceFunctionalAgentId?: string;
  sourceSurfaceId?: string;
  sourceActionId?: string;
  scope: "current_workstream" | "authorized_cross_workstream";
  correlationId: string;
};
```

Default prompt resolution is current-workstream scoped. Authorized cross-workstream surface requests are supported for power users and deep links, but unresolved or unauthorized targets must render typed `system_message` denial/recovery surfaces without leaking hidden workstream existence.

### Surface envelopes

Use the envelope from `docs/structured-surface-contracts.md` as the canonical frontend contract:

```ts
type SurfaceEnvelope<TData, TAction extends SurfaceAction = SurfaceAction> = {
  surfaceId: string;
  surfaceType: string;
  surfaceVersion: string;
  title: string;
  ownerFunctionalAgentId: string;
  reusableByFunctionalAgentIds?: string[];
  authContext: {
    tenantId: string;
    customerId?: string;
    selectedContextId: string;
    visibleCapabilityIds: string[];
  };
  correlationId: string;
  traceIds: string[];
  generatedAt: string;
  stale?: { isStale: boolean; reason?: string; lastKnownEventId?: string };
  redaction: {
    profile: "self" | "tenant-admin" | "support" | "auditor" | "agent";
    omittedFieldKeys?: string[];
  };
  data: TData;
  actions: TAction[];
  links?: SurfaceLink[];
};
```

Canonical reusable surface components:
- dashboard / attention
- list and search results
- detail and edit
- decision / approval / exception
- audit or work-trace timeline
- workflow status / progress
- governance diff / proposal review
- outcome review / metrics

Each surface renders loading, empty, ready, submitting, success, pending, approval-needed, error, forbidden, conflict, stale, reconnecting, partial-data, and no-op states where applicable.

### Browser-tool / capability actions

Every workstream surface action is a browser-tool exposure backed by a governed-tool inside a governed backend capability, including read/query actions and surface-request actions such as `show_surface`, `open_workstream`, show dashboard, search, open detail, refresh, or open trace. Frontend action availability is only a UX hint. Surface-request actions may carry `shellRequest` metadata so buttons, links, cards, rows, My Account panels, rail entries, and deep links use the same canonical prompt feedback and origin-aware request item rendering as composer-entered prompts. The action descriptor must identify the source surface, target/result/system-message surface behavior, browser-tool name, governed-tool id, capability id, authorization basis, idempotency, and audit/trace requirements.

```ts
type CapabilityActionRequest = {
  actionId: string;
  browserToolId: string;
  governedToolId: string;
  capabilityId: string;
  sourceSurfaceId?: string;
  targetSurfaceId?: string;
  input: unknown;
  idempotencyKey?: string;
  selectedContextId: string;
  surfaceId?: string;
  correlationId: string;
};

type CapabilityActionResult = {
  status: "accepted" | "denied" | "validation-error" | "approval-required" | "conflict" | "no-op" | "failed";
  message: string;
  correlationId: string;
  traceIds: string[];
  resultSurface?: SurfaceEnvelope<unknown>;
};
```

Action rendering preserves disabled/denied reasons, confirmation, approval requirements, idempotency key generation/preservation, audit event type, trace-required status, and result-surface behavior.

### Events and realtime

Use SSE by default for server-to-browser workstream/surface updates. Use WebSocket only when bidirectional low-latency browser/server messages are central.

```ts
type WorkstreamEvent = {
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

Realtime helpers must treat duplicate, replayed, out-of-order, malformed, forbidden, and cross-context events as safe no-ops plus diagnostics. Reconnect either resumes from `lastEventId` or marks affected surfaces stale.

## State model

Use explicit discriminated unions for shell regions and surface data:

```ts
type RegionState<T> =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "ready"; value: T }
  | { status: "empty"; message: string }
  | { status: "forbidden"; message: string; recovery?: string }
  | { status: "error"; message: string; retryable: boolean }
  | { status: "stale"; value: T; message: string; lastKnownEventId?: string };
```

Required visible state concepts:
- selected functional agent and unavailable/denied agent deep links
- selected AuthContext and authority basis
- disabled account or missing membership
- pending approval and waiting-for-human states
- agent working / workflow progressed
- policy blocked / exception raised
- trace available
- stale/reconnecting/disconnected realtime state

## Deep-link contract

Supported deep links:
- shell entry: `/ui` or equivalent SPA entry
- selected functional agent: selected `functionalAgentId`
- stream item anchor: selected `itemId`
- direct surface: selected `surfaceId` and optional placement (`inline`, `modal`, `side-panel`)

Deep links must render not-found, forbidden, unavailable-agent, stale, and recovery states. They must not bypass `/api/me`, backend authorization, selected AuthContext checks, or capability denial behavior.

## Fixture requirements for next implementation task

Fixtures are required to prove contracts and edge cases during UI implementation, but they are not a generated-app completion target. Any named feature shown to users must also be wired to the real local backend/API/realtime path with authorization, denial, audit/trace, and validation behavior before it is called implemented.

The first implementation slice must include fixtures for:
- `/api/me` active tenant admin, regular member, auditor/support-like viewer, disabled user, and no-membership/forbidden states
- selected `AuthContext` with tenant and optional customer scope
- visible, denied, hidden, disabled, and attention-bearing functional agents
- initial workstream items for user request, agent response, surface, capability result, workflow progress, decision, audit trace, action feedback, system-message surface, and system status
- surface envelopes for every canonical surface type listed above, aligned to `templates/ai-first-saas-core-app/app-description/12-workstreams/**` when implementing the core app core surfaces
- surface actions covering read, command, proposal, approval, workflow, governance, and trace intents, each with browser-tool/governed-tool/capability ids and source/result surface graph behavior
- action results for accepted, denied, validation error, approval required, conflict, no-op, and failed outcomes
- realtime events for created, updated, accepted, denied, workflow progressed, stale, reconnected, duplicate/replay, out-of-order, malformed-safe, and cross-context-denied cases

## Testing target

Focused contract tests should verify:
- types/fixtures include `/api/me`, AuthContext, functional agents, workstream items, surface envelopes, actions, and events
- rail visibility is capability-aware and supports denied/hidden states
- composer is persistent, selected-agent aware, and restores focus to the enabled prompt input after submission, workstream changes, browser refocus, and response completion so the next user input can be typed without clicking the field
- context/authority indicators expose selected tenant/customer, role/capability basis, support-access state, pending approvals, and trace/recovery links
- surface rendering uses envelope identity/type/version, payload, actions, stale markers, redaction, and trace ids
- action controls preserve disabled/denied reasons, confirmation, idempotency, browser-tool/governed-tool/capability mapping, audit, trace, and result/system-message surface mappings
- realtime helpers dedupe and stale-mark without crashing on malformed or unauthorized events
- direct deep links are secondary to workstream/surface state and render forbidden/not-found recovery states

## Migration boundaries

Do not hand-edit generated assets under `src/main/resources/static-resources/**` in component-library tasks. Replace generated Vite output only after the workstream source shell is ready to build.

Do not restore removed static UI fixtures as canonical full-stack SaaS UI guidance. If a task needs asset-serving mechanics, document the focused route shape in the target app and keep product UI source under `frontend/**`.

## Implementation handoff sequence

1. Add or update `frontend/src/workstream/types/**` and focused contract tests. Keep reusable runtime modules under `frontend/src/workstream/**`; put test-only fixtures under `frontend/src/__tests__/fixtures/**` or inside the relevant contract test.
2. Add shell, rail, context/authority, and composer components.
3. Add stream item and structured surface components.
4. Add capability action components and action state helpers.
5. Replace `frontend/src/main.tsx` with the reusable workstream shell and deep-link router.
6. Rebuild the User Admin reference vertical as surfaces, not pages.
7. Regenerate static assets and delete stale unreferenced hashes.
8. Rewrite frontend docs/tests around workstream shell behavior.

## Done checklist for this target architecture

- [x] Source layout distinguishes shell, rail, composer, stream, surfaces, actions, fixtures, realtime, and tests.
- [x] Routes are defined as deep links and implementation details, not primary decomposition.
- [x] `/api/me`, AuthContext, functional agents, workstream items, surfaces, actions, and events have target contracts.
- [x] Reusable surface patterns and action semantics are specific enough for component-library implementation tasks.
- [x] Migration boundaries protect generated assets and stale screens until replacements exist.
