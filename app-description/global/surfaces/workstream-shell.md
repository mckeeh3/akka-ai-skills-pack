# Global Workstream Shell

The workstream shell is the canonical authenticated browser experience for this SaaS Foundation App. It is the app's face and primary UX model: users do not primarily navigate page modules or CRUD screens; they supervise role-authorized functional-agent workstreams through a shared shell, selected context, durable workstream timeline, structured surfaces, governed actions, and traceable recovery states.

This artifact is the global shell contract. Workstream-specific surface files own business payloads, actions, and graph edges; this file owns the shared shell anatomy, interaction model, state model, safety rules, accessibility expectations, and links to backend contracts.

## Purpose and mental model

The shell helps an authorized human answer five questions at all times:

1. **Where am I working?** The selected tenant/customer/account context, role/capability basis, and support-access posture are visible and backend-owned.
2. **Which functional agent owns this work?** The rail exposes only authorized workstreams/functional agents and compact attention status.
3. **What needs attention now?** Dashboards, counters, stream items, and surfaces show actionable, scoped work rather than generic notification noise.
4. **What can I safely do next?** Composer routes, surface actions, buttons, links, and deep links all become governed shell requests or capability-backed actions.
5. **Why did this happen?** Trace, evidence, audit, policy, redaction, stale/reconnect, and denial affordances are visible without leaking secrets or hidden objects.

The shell is not a page hierarchy, generic assistant chat, frontend permission system, static fixture demo, provider bypass, or replacement for backend authorization.

## Layout regions

The desktop shell uses a stable four-region composition; narrow screens may collapse or stack regions without changing semantics.

- **Authenticated app frame:** WorkOS/AuthKit-gated frame that renders only after `/api/me` and shell bootstrap have resolved to an active or safe recovery state.
- **Functional-agent rail:** role-authorized left rail with product mark, visible workstream/agent entries, attention counts, disabled/denied states where safe, governance/audit entries when authorized, and a pinned signed-in user tile that opens My Account. My Account is not duplicated as a primary top rail workstream list item.
- **Context/authority bar:** selected `AuthContext`, organization/customer/account scope, role/capability summary, support-access state, stale/reconnect/provider readiness markers, safe context switch/retry affordances, and trace/recovery links.
- **Workstream panel:** continuous durable timeline for the selected workstream instance, including request items, agent responses, structured surfaces, action feedback, decisions, workflow status, audit/trace summaries, and system messages.
- **Persistent composer:** selected-workstream prompt/input area for deterministic surface-intent routes and governed model-backed functional-agent turns. The composer remains available when allowed, preserves draft/disabled states, and restores focus after submission, workstream changes, browser refocus, and response completion.
- **Surface/action area:** structured surfaces render inline in the workstream panel by default; modal or side-panel placement is allowed only when declared by a surface contract and must preserve focus, trace, redaction, and recovery behavior.

## Functional-agent rail behavior

Rail entries represent workstream definitions owned by exactly one user-facing functional agent. They are not routes and do not grant authority.

- Visible entries come from backend bootstrap using the current selected `AuthContext`.
- Hidden or denied workstreams must not leak through labels, counts, deep links, disabled controls, or stale local state.
- Attention counts are backend-owned summaries for authorized work only.
- Opening a workstream creates/appends a shell request item in the target workstream and returns the authorized default/dashboard, previous durable surface state, or safe system-message recovery.
- Stale rail clicks and deep links are reauthorized through `/api/workstream/shell-requests`.
- Keyboard navigation must support rail traversal, collapsed rail expansion, active item announcement, and safe focus return.

## Workstream instance and surface lifecycle

A workstream definition is a product responsibility; a runtime workstream instance is the durable timeline for one selected organization/customer/AuthContext scope; a browser view/session is only the current rendering of that instance.

Required shell-visible lifecycle states:

- `bootstrapping`: `/api/me` and shell bootstrap are loading.
- `ready`: selected context and workstream are authorized and surfaces can render.
- `empty`: authorized workstream has no persisted surface or attention; render explicit empty/blank workstream affordances where the workstream declares them.
- `loading`: a surface, request, action, or realtime reconnect is pending.
- `working`: functional agent, workflow, outbox, projection, or autonomous/internal work is in progress.
- `waiting-for-human`: decision, confirmation, approval, validation repair, or manual review is required.
- `submitting`: a governed browser action is in flight with idempotency/correlation metadata.
- `success`: an action or request returned an authoritative updated/created/result surface.
- `no-op`: a repeated or already-satisfied action returns traceable no-op result rather than fake success.
- `validation_error`: user-correctable field or request errors are attached to the originating surface/result surface.
- `approval_required`: a decision/approval surface is returned instead of executing automatically.
- `forbidden` / `not_found_or_redacted`: denial or missing target is rendered without hidden-object enumeration.
- `policy_blocked`, `provider_blocked`, `model_blocked`, `outbox_blocked`, `tool_boundary_denied`: fail-closed blocker surfaces with recovery guidance and trace refs.
- `stale` / `reconnecting` / `disconnected`: affected surfaces remain visible with stale markers or safe recovery while realtime resumes or refetches.
- `partial_data`: authorized redacted data renders with omitted-field explanation and trace refs.
- `failed`: unexpected failures render safe system-message surfaces with retry/support guidance, never stack traces or raw provider/tool payloads.

## Structured surface envelope

Every rendered surface uses the structured surface envelope and common workstream API fields. Required shell-level fields include:

- `surfaceId`, `surfaceType`, `surfaceContract` or `surfaceVersion`, `title`.
- `functionalAgentId` or `ownerFunctionalAgentId`, `workstreamId`, and optional explicit reusable-by metadata.
- selected `AuthContext` or `selectedAuthContext` summary.
- `capabilityDecision`, `authorizedActions[]`, action descriptors, and disabled/denied reasons where safe.
- `traceRefs[]` / `traceIds[]`, `correlationId`, and optional `idempotencyKeyHint`.
- `redaction`, omitted field keys/reasons when safe, and browser-safe evidence links.
- `systemStates`, stale/realtime metadata, generated timestamp, and result status.

Surfaces must render loading, empty, ready, submitting, success/result, validation, approval-needed, forbidden, conflict, stale, reconnecting, partial-data, no-op, provider/runtime-blocked, and failure states where applicable. Surface-specific files define exact payload fields, actions, graph edges, traces, and tests.

## Composer and shell requests

The composer is an entry point into the selected workstream, not an authority source.

- Composer prompts first pass through deterministic surface-intent routing for declared safe routes such as show dashboard, open list, create draft/prefill, refresh, or open known surface.
- Matched routes append the original user request and return the authorized surface or editable prefill; they never auto-submit mutations.
- Unmatched prompts proceed to the selected functional agent only when provider/model/runtime/security prerequisites pass.
- Missing provider/model/security configuration returns a fail-closed system-message surface, not canned or model-less success.
- Ambiguous requests ask clarifying questions or return safe fallback surfaces.
- Shell-level commands, rail selection, My Account panels, dashboard cards, deep links, and surface links normalize to shell requests with origin, display text, target, scope, correlation id, and backend reauthorization.

## Capability-backed actions and result surfaces

Every consequential button, link, row activation, form submit, confirmation, retry, refresh, context switch, attention open, trace open, and shell request is a browser-tool exposure of a governed backend capability/tool.

- Frontend-visible action state is advisory; backend capability, selected context, policy, approval gates, provider/outbox/tool readiness, and idempotency are authoritative.
- Actions must include correlation metadata and idempotency metadata where needed.
- Results return an updated originating surface, created/changed object surface, refreshed list/dashboard, decision/approval surface, workflow/progress surface, typed result surface, or safe system-message.
- Repeated, stale, unsupported, hidden, policy-blocked, or denied actions return explicit traceable no-op/denial/stale/system results.
- Consequential human-chat tool plans require review and explicit human confirmation before execution; execution still uses backend authorization and audit/work traces.

## Realtime and stale behavior

The shell uses server-sent events by default for workstream updates. WebSockets are reserved for explicitly bidirectional low-latency behavior.

Realtime events may append or update workstream items, surfaces, attention counts, workflow progress, action outcomes, stale markers, reconnect markers, and projection refresh notices. The browser must safely ignore malformed, duplicate, replayed, out-of-order, cross-context, unauthorized, or stale events. Reconnect resumes from bounded replay metadata when available; otherwise affected surfaces are marked stale and can be refreshed through protected shell/actions APIs.

Realtime events never grant authority and never expose raw backend event ids, secrets, hidden object names, or unredacted evidence.

## Attention, dashboards, and My Account aggregation

Role-specific dashboards are attention-first. They should place what needs the authorized user's attention before lower-priority details.

- Attention comes from backend-owned `AttentionItem` / `WorkstreamAttentionSummary` projections.
- Dashboard objects representing attention or available work are clickable and keyboard-operable by default, and opening them appends a request item before rendering the target surface/result.
- My Account may aggregate only authorized attention from accessible workstreams and must preserve source workstream ids for governed `open_attention_item` or `open_workstream` actions.
- Zero-count, stale, forbidden, unavailable, and no-membership states are explicit states, not missing data.

## Deep links and routes

Routes are secondary shell entry/deep-link mechanics. They may select a functional agent, stream item, surface, mode, or opaque backend-issued target reference, but they never define authority or product scope.

- All deep links re-bootstrap `/api/me` and post a shell request for server-side reauthorization.
- Opaque refs and signed/server-issued filter refs are preferred over raw tenant/customer/account/invitation/task/trace ids.
- Hidden, unavailable, stale, forbidden, and not-found targets render safe system-message/result surfaces without confirming hidden object existence.
- Compatibility/service APIs may support smoke checks and integrations; they do not replace structured shell contracts.

## Security, privacy, and secret boundaries

Browser payloads and visible shell state must never include raw JWT/session values, provider secrets, provider/model internal records, raw model responses, raw prompts, tool payloads, invitation tokens/token hashes, raw idempotency keys, hidden tenant/customer ids, hidden workstream names, unredacted audit evidence, stack traces, fixture/demo data as normal runtime, or backend-only permission internals.

The shell must preserve tenant/customer/account isolation, selected-context enforcement, support-access redaction, non-enumeration, provider fail-closed behavior, frontend secret boundaries, and traceability across bootstrap, composer, actions, shell requests, deep links, realtime events, and recovery surfaces.

## Accessibility and responsive behavior

The shell must be usable without a mouse and across desktop/narrow layouts.

- Use semantic landmarks for rail/navigation, context/header, main workstream panel, composer/form, and status regions.
- Preserve visible focus, keyboard order, skip links or equivalent jump behavior, accessible names for rail entries/actions, and focus return after modals, side panels, submissions, workstream changes, and recovery states.
- Do not rely on color alone for status; pair severity/status colors with text and/or icons.
- Respect reduced-motion settings for realtime append/update, stale/reconnect, and transition effects.
- Narrow layouts may collapse the rail and stack surfaces, but must preserve composer access, context visibility, trace/recovery affordances, and keyboard navigation.

## Style contract

The shell uses the canonical AI-first workstream enterprise style and named-theme model unless a future authoritative UI style artifact records a custom equivalent. Component anatomy remains stable across themes: functional-agent rail, context/authority bar, command/composer strip, attention counters, structured cards, decision cards, audit traces, designed forms, status badges, and tokenized loading/empty/error states.

Changing theme changes color tokens only; it must not alter shell anatomy, workstream/surface inventory, routes, authorization, action mapping, traces, or tests.

## Backend/API contract links

The shell is realized through protected backend calls documented in `app-description/55-ui/frontend-api-contracts.md`:

- `POST /api/workstream/bootstrap`
- `POST /api/workstream/actions`
- `POST /api/workstream/messages`
- `POST /api/workstream/shell-requests`
- `GET /api/workstream/events`

`app-description/global/surfaces/ui-style-and-runtime-contracts.md` owns style/runtime source-of-truth pointers and must point to this shell contract for browser shell behavior.

## Acceptance and regression expectations

Generated/runtime work that changes the shell must include tests or manual runtime evidence for:

- `/api/me` and shell bootstrap active, disabled, no-membership, missing bearer, and safe recovery states.
- rail visibility, hidden/denied non-enumeration, attention counts, My Account placement, keyboard behavior, and focus return.
- selected-context display, context switch/refresh, stale surface marking, and tenant/customer isolation.
- composer deterministic route, unmatched governed-agent path, provider/model fail-closed state, and no canned/model-less success.
- action authorization, validation, approval-required, conflict, stale, no-op, denial, provider/outbox/tool blocked, idempotency, trace, and result-surface mapping.
- deep-link reauthorization, opaque refs, forbidden/not-found recovery, and compatibility-route non-authority.
- realtime update, duplicate/replay/out-of-order/malformed/cross-context event safe handling, reconnect, and stale markers.
- browser secret-boundary checks proving forbidden raw tokens, provider/model/tool payloads, prompts, hidden object names, fixture data, and internal errors are not rendered or submitted.
- accessibility and responsive checks for semantic landmarks, keyboard operation, focus visibility, status semantics, reduced motion, and narrow-screen layout.

## Non-goals

The shell must not become or imply:

- page-first admin screens, standalone CRUD pages, or route-owned product scope;
- frontend-only authorization, local-storage authority, disabled-button security, or fixture-driven normal runtime;
- generic chatbot behavior that bypasses structured surfaces, governed tools, confirmation, or backend audit;
- hidden-object enumeration through labels, counts, errors, deep links, stale local data, or traces;
- mock/demo/model-less success for workstream agents, provider calls, governed actions, or runtime traces;
- billing, CRM, support-case, sales, or other deferred business-domain scope unless explicitly added by current-intent workstream artifacts.
