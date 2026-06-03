---
name: akka-web-ui-apps
description: Plan and implement agent workstream browser apps hosted by Akka HTTP endpoints, using standard frontend projects such as React/Vite/TypeScript. Use when generated full-stack AI-first SaaS needs a role-authorized functional-agent shell, continuous workstream, composer, and structured surfaces.
---

# Akka Web UI Apps

Use this as the top-level skill for complete browser app work in Akka services when the UI is already selected as a human-facing exposure and supervision surface for backend capabilities.

For generated full-stack AI-first SaaS apps, the default browser architecture is the **agent workstream shell**: role-authorized functional agents in the left rail, a continuous main workstream panel, a persistent bottom composer, context/authority indicators, and typed structured surfaces embedded in the stream. Conventional routes and pages may support implementation, refreshable deep links, public/static content, or direct surface URLs; they are not the primary decomposition for authenticated consequential work.

For broad product, PRD, feature, dashboard, admin, or portal requests, route through `ai-first-saas`, `agent-workstream-apps`, `capability-first-backend`, and `akka-solution-decomposition` before implementing frontend regions. Do not start from CRUD navigation when functional agents, structured surfaces, capability inventory, AuthContext, allowed actions, decision/supervision surfaces, audit needs, and backend authorization semantics are still unclear.

This skill complements `akka-http-endpoint-web-ui`:
- `akka-web-ui-apps` designs the frontend application experience and implementation plan.
- `akka-web-ui-frontend-project` integrates a standard frontend project such as React/Vite with Akka static hosting.
- `akka-http-endpoint-web-ui` hosts generated frontend build assets and connects UI routes to Akka HTTP endpoints.

## Agent workstream shell role

In AI-first SaaS implementations, web UI apps are mandatory agent workstream shells, not optional CRUD dashboards with a chat panel. Prioritize the left-rail functional-agent launcher, main workstream timeline, bottom composer, context/authority indicators, and structured surfaces such as command centers, goal-to-execution workbenches, decision cards, policy/governance panels, async digests, audit/work-trace timelines, and outcome reviews.

Before implementation, make the UI contract explicit for:
- selected account, membership, AuthContext, tenant/customer scope, roles/capabilities, disabled/forbidden states, and `/api/me` bootstrap behavior;
- delegated objective status, active plan progress, agent/team activity, exceptions, and pending approvals;
- recommendation/decision evidence, risk, confidence, impact, alternatives, and action controls;
- policy proposals, simulations, governed commits, learning feedback, and permission boundaries;
- traceability from action or decision back to goal, policy, tool/data access, approver, and outcome;
- realtime or stale-state behavior for live supervision surfaces;
- selected functional agent, available functional agents, denied/hidden agents, and workstream icon descriptors based on `/api/me` and backend capabilities;
- role-specific dashboard attention sources, evidence/freshness display, and allowed next browser-tools;
- human surface graph nodes/edges, including source surface, target/result/system-message surface, and deep-link behavior;
- structured surface type/version, payload, allowed actions including governed surface-request actions such as `open_workstream`, browser-tool/governed-tool/capability ids, stale/reconnect behavior, and trace links.

Pair AI-first web UI work with `agent-workstream-apps` and `ai-first-saas-ui-surfaces` when selecting functional agents and surfaces, then route to endpoint, view, workflow, agent, realtime, state-rendering, accessibility, and testing skills without duplicating their implementation guidance.

## Required reading

Read these first if present:
- `../../docs/workstream-ui-reference-architecture.md` for the canonical reusable `frontend/src/workstream/**` implementation shape and User Admin vertical reference
- `../../docs/web-ui-frontend-decomposition.md`
- `../../docs/web-ui-style-guide.md`
- `../../docs/web-ui-ux-patterns.md`
- `../../docs/web-ui-frontend-project-integration.md`
- `../../docs/structured-surface-contracts.md`
- `../../docs/web-ui-api-contract-patterns.md`
- `../../docs/web-ui-quality-checklist.md`
- `../../docs/web-ui-pattern-selection.md`
- `../akka-http-endpoint-web-ui/SKILL.md`
- existing `frontend/**`
- existing `src/main/resources/static-resources/**`
- matching endpoint and endpoint tests under `src/main/java/**/api` and `src/test/java/**`

Canonical frontend project integration reference:
- `../../docs/frontend-with-akka-backend.md` (use web UI integration sections together with mandatory JWT/request-context and `/api/me` security boundaries for generated SaaS apps)

Canonical full-core starter implementation reference:
- source repository: `../../templates/ai-first-saas-starter/**`
- installed pack: `../../resources/templates/ai-first-saas-starter/**`

Canonical source-repository workstream UI reference:
- reusable implementation modules: `../../frontend/src/workstream/**`
- runtime API contracts: `../../frontend/src/api/WorkstreamApiClient.ts` and `../../frontend/src/api/WorkstreamRealtimeClient.ts`
- integrated shell example: `../../frontend/src/main.tsx`
- User Admin dashboard/list/detail-edit vertical pattern: test-only workstream fixtures and `../../frontend/src/workstream-user-admin-vertical.contract.test.mjs`

Use the starter template as the end-to-end generated-app baseline and these frontend files as reusable UI implementation examples. Test fixtures are contract references only and must not be importable as generated-app normal runtime. Do not use legacy `frontend/src/screens/**` or standalone static examples as the canonical app structure; keep them as mechanics or migration-drift references unless a task explicitly asks for legacy compatibility.

## Use this skill when

- the user asks for a web app, dashboard, admin UI, console, portal, or browser workflow
- the UI needs multiple states, structured surfaces, forms, actions, or data dependencies
- a UI brief must become implementation-ready frontend work
- the frontend should provide excellent UX, with generated build assets hosted by Akka
- the frontend should be excellent and use a standard frontend framework/build tool
- browser code should live in a dedicated frontend project when the UI is a real app

Do not use this as the main skill for generated API documentation assets or raw asset-delivery concerns; keep browser UI work on the React/Vite/TypeScript frontend project path.

## Planning output before coding

Before implementing generated AI-first SaaS UI, verify that a selected style and named-theme contract exist in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or another authoritative UI spec. The contract must record available theme ids/names, default theme id, My Account selection behavior when in scope, and persistence scope. If style or named-theme selection is missing/unselected, add or update `specs/pending-questions.md` with the style-selection question from `../../docs/web-ui-style-guide.md` and stop web UI implementation for the affected tasks.

Before implementing, load `akka-web-ui-ux-design` for any non-trivial app and produce a frontend plan with:
1. User goals and personas
2. Functional-agent left rail: visible agents, hidden/denied agents, default selected agent, unread/attention indicators, workstream icon descriptors rendered through an approved SVG/icon-library registry or semantic SVG fallback (not letters), My Account lower-left signed-in user tile behavior, and role/capability basis
3. Main workstream panel: stream item types, grouping, history/retention behavior, status/progress items, trace links, and stale/reconnect states
4. Persistent composer: accepted natural-language requests, command shortcuts, uploads where allowed, disabled/forbidden states, and selected-agent context
5. Structured surfaces and surface graph: type/version, payload schema, information hierarchy, states, source/result/system-message transitions, browser-tool actions, and rendering tests for dashboards, forms, tables, charts, decisions, diffs, approvals, audit timelines, details, workflow status, and outcome panels
6. UX handoff for each shell region, role-specific dashboard, attention source, and surface: primary action, information hierarchy, evidence/freshness display, state behavior, UX copy, tooltip/aria-label behavior for icons, responsive behavior, and keyboard/focus path
7. Deep-link and route plan for shell entry, selected functional agent, stream item, and direct surface URLs; route/page navigation is an implementation detail, not the primary model
8. Data dependencies and API contracts
9. Actions, forms, and validation rules, with each consequential action mapped to a browser-tool exposure, governed-tool id, and governed capability
10. Frontend state model, including loading/empty/error/success/forbidden/stale states
11. Real-time behavior, if any
12. Frontend implementation shape: standard frontend project (for example React/Vite)
13. Selected web UI style guide, named-theme contract, default/available theme ids, CSS tokens, layout density, component styling, and brand adaptations
14. Accessibility and responsive requirements for left rail, workstream, composer, and surfaces
15. Akka HTTP endpoint route plan, including generated frontend asset and API route separation
16. Auth/session/security UI contract: WorkOS/AuthKit entry, `/api/me` bootstrap, context selection, capability-gated functional agents/actions, disabled-user state, forbidden recovery, and tenant/customer isolation expectations
17. SPA routing choice: hash routing, explicit server entry routes, or in-app navigation only
18. Implementation skills to load
19. Required tests and quality checks

For generated SaaS apps, auth/session/security behavior is expected input. If provider-specific values are unknown, preserve the local `/api/me`, AuthContext, capability, and forbidden-state contract while blocking only provider-specific integration details that cannot be safely implemented.

## Skill routing

Load only the focused companions needed. Keep ownership boundaries crisp:

| Skill | Owns | Does not own |
|---|---|---|
| `akka-web-ui-ux-design` | Workstream shell and structured surface UX intent, hierarchy, copy, feedback, recovery, responsive behavior, and keyboard/focus path. | Backend capability semantics, WorkOS setup, or endpoint implementation. |
| `akka-web-ui-frontend-project` | React/Vite/TypeScript project layout, source/build output, workstream module organization, and static asset integration. | Auth provider secrets, backend authorization, or hand-editing generated assets. |
| `akka-web-ui-api-client` | Browser-safe DTO clients, fetch wrappers, and normalized API errors. | Raw component state exposure or backend permission decisions. |
| `akka-web-ui-state-rendering` | Explicit UI state and structured surface rendering from frontend-safe payloads. | Capability design or treating stale/hidden UI state as authorization. |
| `akka-web-ui-forms-validation` | Client form behavior, accessible validation, submit state, and server validation mapping. | Authoritative business validation or role/scope checks. |
| `akka-web-ui-realtime` | Browser SSE/WebSocket lifecycle, event parsing, dedupe, reconnect, and stale-state UX. | Stream authorization or event payload scoping. |
| `akka-web-ui-accessibility-responsive` | Semantic HTML, keyboard/focus behavior, contrast/status text, and narrow-layout survival. | Visual style selection when no style guide is chosen. |
| `akka-web-ui-testing` | Frontend checks/builds, frontend logic tests, asset route tests, and optional smoke checks. | Replacing endpoint/component/security tests. |

Always pair with Akka hosting/API skills as needed:
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-jwt` for generated SaaS API routes that require authenticated browser or service callers; only public static asset routes are outside authenticated API authorization
- `akka-http-endpoint-request-context` for selected tenant/customer context, correlation/audit metadata, and forbidden-access behavior
- `akka-http-endpoint-testing`

## Default implementation order

1. Define UX handoff for the agent workstream shell: user goals, functional-agent rail, workstream icons, My Account lower-left launcher behavior, main stream, composer, context/authority indicators, structured surfaces, primary actions, information hierarchy, state behavior, UX copy, responsive behavior, and keyboard/focus path.
2. Define structured surface graph contracts: dashboard attention sources, payload schemas, browser-tool actions backed by governed-tools/capabilities, realtime/update events, backend-authoritative auth, trace/audit fields, rendering states, result/system-message surfaces, deep links, and API contracts. Treat conventional pages/routes as implementation and deep-link details.
3. Reuse or adapt the canonical workstream reference modules under `frontend/src/workstream/**`; use the User Admin dashboard → list/search → detail/edit vertical as the first generated foundation-admin example.
4. Confirm frontend project framework/build tool and project conventions.
5. Implement or adjust backend JSON/SSE/WebSocket endpoints.
6. Implement the frontend in its source root (`frontend/src/**`).
7. Build React/Vite/TypeScript frontend assets into `src/main/resources/static-resources/`; treat this as generated output for standard frontend projects.
8. Add/extend endpoint integration tests for shell entry, assets, explicit SPA entry routes, and API route separation.
9. Run frontend checks/build and backend tests.
10. Review with `docs/structured-surface-contracts.md`, `docs/web-ui-quality-checklist.md`, and `docs/web-ui-ux-patterns.md`.

## Quality bar

A complete web UI must apply the selected style guide and named-theme tokens without copying demo content from the reference images. User-facing theme selection must be by available theme name/id, not by a primary `system`, `light`, or `dark` mode selector.

For AI-first surfaces, a complete UI must make human authority obvious: what the system is doing, why it recommends or waits, what evidence and policy triggered the state, what the human can approve/reject/defer/escalate, and where the trace/outcome can be reviewed.

A complete web UI must handle:
- first-five-seconds comprehension: which functional agent am I using, which account/tenant/customer context is active, what matters in this workstream, what can I do, and what authority do I have?
- clear primary action and subordinate secondary actions
- initial loading
- useful empty data state
- successful data
- validation failures with preserved input and focus behavior
- backend/API errors with recovery copy
- unauthenticated, disabled-user, unauthorized/forbidden, and cross-tenant/customer denial states based on `/api/me` and backend responses
- disabled/submitting states for actions
- responsive layout at common viewport widths, including collapsed left rail and usable composer behavior on narrow screens
- keyboard navigation and visible focus across functional-agent rail, stream items, composer, and surface actions
- live-update reconnect/stale behavior when realtime is used
- direct deep links and governed surface-request actions to selected functional agents, important stream items, and structured surfaces when required without making route/page hierarchy the primary UX model

## Anti-patterns

Avoid:
- treating a serious app UI as anything other than a real frontend project
- designing authenticated consequential work as a page-first CRUD console, dashboard-with-chat, or chatbot-bolt-on instead of an agent workstream shell
- exposing internal domain objects directly to the browser
- implementing only the happy path
- producing generic UX copy such as `Error occurred`, `Invalid input`, or `Success`
- making all actions visually equal
- assuming route tests are enough for frontend logic
- hand-editing generated frontend build output under `static-resources/`
- mixing static asset wildcards and backend API routes under ambiguous catch-all paths
- treating auth/session/security as deferrable for generated SaaS UI; only public static assets are outside authenticated API authorization
- treating left-rail visibility, hidden buttons, icons, prompt text, route names, or frontend state as authorization controls; backend capabilities remain authoritative
- silently choosing colors, visual styling, or theme ids when app-description/specs have not selected a style guide and named-theme contract
- skipping accessible labels, focus behavior, or responsive layout
