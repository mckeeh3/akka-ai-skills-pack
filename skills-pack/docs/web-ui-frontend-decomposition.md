# Web UI frontend decomposition

Use this doc when requirements include a user-facing browser app hosted by Akka HTTP endpoints.

The goal is to make the frontend as intentionally designed as the backend. For generated full-stack AI-first SaaS, decompose the browser app as an agent workstream shell first: left-rail functional agents, main workstream panel, bottom composer, context/authority indicators, and structured surfaces. Conventional routes support implementation and deep links; they are not the primary model for authenticated consequential work.

For product UIs, use a standard frontend project such as React/Vite and route implementation through the full web app skill path.

Read `docs/web-ui-style-guide.md` before implementation when no app-specific style guide is already selected.

For this source repository and downstream forks, use `docs/workstream-ui-reference-architecture.md` and the reusable implementation under root `frontend/src/workstream/**` as the canonical frontend reference. The skills-only install does not export `resources/examples/frontend/**`. The foundation-admin vertical fixtures and root `frontend/src/workstream-user-admin-vertical.contract.test.mjs` demonstrate structured surface flow through dashboard, attention queue, scoped search results, member detail cards, invitation actions, system messages, and trace links. Do not use legacy `frontend/src/screens/**` or page-route tests as the primary generated SaaS UI model.

## Required output

Before implementation, produce a frontend plan with these sections.

### 1. Users and goals

List:
- personas or actors
- primary goals
- permissions or role differences
- success criteria for each user journey

### 2. Workstream shell regions, surfaces, and deep links

For generated SaaS apps, define shell regions and the human surface graph before route/deep-link details. Treat the role-specific dashboard as the trunk surface for the selected functional agent; every table row action, card link, form submission, refresh, deep link, approval, trace link, or workstream switch is a surface graph edge that invokes a browser-tool backed by a governed-tool.
- left rail functional agents: visible agents, hidden/denied agents, default selected agent, attention indicators, and role/capability basis
- main workstream panel: stream item types, grouping, timeline/history behavior, status/progress items, trace links, and stale/reconnect states
- persistent bottom composer: accepted request types, command shortcuts, uploads where allowed, disabled/forbidden states, and selected-agent context
- context/authority indicators: selected account/tenant/customer, role/capability basis, support-access state, pending approvals, and recovery links
- role-specific dashboard surfaces: attention items, attention source, evidence/freshness, allowed next browser-tools, and denied/hidden states
- structured surfaces: dashboards, forms, tables, charts, detail cards, decision/approval/exception cards, diffs, audit timelines, workflow status, evidence bundles, version cards, and outcome panels
- surface graph edges: source surface, target/result/system-message surface, browser-tool name, governed-tool id, capability id, authorization basis, and trace/audit outcome
- deep links: selected functional agent, important stream item, and direct surface URLs when required

For each shell region, surface, surface graph edge, or conventional route/deep-link, define:
- route or UI path, where routes are implementation/deep-link details
- user goal
- purpose
- primary action and why it is primary
- secondary actions and how they stay subordinate
- information hierarchy: most important data, supporting data, metadata/diagnostics
- entry points and exits
- loading behavior
- empty/not-found behavior
- forbidden/unauthorized behavior
- error recovery behavior
- success feedback
- UX copy for key labels, buttons, empty/error/success messages
- narrow-screen behavior
- keyboard/focus path

Prefer explicit route families:
- UI shell/assets: `/ui...`
- session bootstrap: `/api/me`
- workstream/surface/action APIs: `/api/workstreams/...` or similarly explicit generated-app routes
- streams: explicit workstream/surface stream prefixes
- WebSockets: `/websockets/...` only when bidirectional browser/server messaging is central

Conventional object paths, if present, are implementation aliases for capability-backed surface actions; they are not the planning starting point.

### 3. Data dependencies

For each shell region, dashboard attention source, surface, surface graph edge, browser-tool, or route/deep-link, list:
- data needed on first render
- attention source, freshness/stale rules, and evidence links when the surface is a role-specific dashboard or queue
- query/filter/sort inputs
- command/action endpoints
- backend capability id and governed-tool id for each consequential action or query
- polling, SSE, or WebSocket needs
- cache/stale behavior if relevant

### 4. UX handoff and frontend state model

For each structured surface or route-backed region, define the UX handoff:
- first-five-seconds comprehension: what should the user understand immediately?
- primary decision/action
- information hierarchy
- feedback and recovery behavior
- responsive task preservation
- keyboard/focus path

Name states explicitly:
- initial/idle
- loading
- ready
- empty
- validation-error
- API-error
- unauthorized/forbidden
- submitting
- success
- denied/hidden functional agent
- stale/reconnecting when realtime is used

Do not implement only the happy path.

### 5. Forms and actions

For each form/action, define:
- input fields
- labels and helper text
- primary button text
- client validation rules
- backend validation rules
- submit route and method
- duplicate-submit/idempotency expectations
- success behavior and success copy
- server error mapping and recovery copy
- focus behavior after validation failure
- destructive action confirmation copy when applicable

### 6. API contracts

For each browser API, define:
- route and method
- capability id, governed-tool id, browser-tool exposure name, and allowed exposure surface
- request DTO
- success response DTO
- error response DTOs
- authorization/forbidden behavior
- audit/work-trace expectations for consequential actions
- endpoint integration tests

Security/auth requirements may be noted as placeholders, but detailed authentication and authorization implementation belongs in security-specific guidance.

Use browser-facing DTOs, not accidental internal domain objects. Surface actions, hidden buttons, and route names are not authorization; backend capabilities remain authoritative.

### 7. Realtime behavior

If the UI needs live updates, define:
- SSE vs WebSocket decision
- event/message types
- connection status display
- reconnect/stale behavior
- merge/idempotency rules
- cleanup behavior on navigation

### 8. Style guide

Define:
- selected AI-first style id/name from `docs/web-ui-style-guide.md`, a custom style guide, or `unselected`
- source reference and mode policy: light-only, dark-only, or system with both token sets
- typography, spacing, radius, elevation, color, chart, and status tokens
- layout density and shell/navigation treatment
- component style rules for cards, buttons, forms, tables/lists, charts, loading/empty/error/success states
- brand adaptations and a rule not to copy demo names/logos/data from reference images

If the app has a browser UI and style is `unselected`, add a pending `category: ui` style-selection question before materializing web UI implementation tasks.

### 9. Accessibility and responsive behavior

Define:
- first-five-seconds comprehension target
- semantic shell/surface landmarks
- heading structure
- keyboard path for primary flows
- focus movement after major actions
- field labels and error associations
- narrow-screen layout strategy
- style-guide accessibility constraints for contrast, focus rings, and color-not-alone status communication

### 10. UX copy

Define user-facing copy for:
- shell/surface titles and subtitles
- primary and secondary action labels
- field labels and helper text
- empty states
- validation messages
- API/load failures and retry guidance
- success confirmations
- destructive confirmations

Avoid generic copy such as `Error occurred`, `Invalid input`, `Submit`, or `Success` when more specific language is possible.

### 11. Frontend integration shape

Use a standard frontend project: source under `frontend/**`, built assets under `src/main/resources/static-resources/**`.

Also define:
- framework/build tool, such as React/Vite
- source organization for shell, surfaces, API clients, and state modules; for generated AI-first SaaS, prefer the reusable `frontend/src/workstream/**` taxonomy: `types`, `fixtures`, `shell`, `rail`, `composer`, `stream`, `surfaces`, `actions`, and `realtime`
- package manager and build command
- generated asset ownership rule
- SPA/workstream routing choice: hash routing, explicit server entry routes, or in-app-only navigation
- explicit deep-link entries for selected functional agents, stream items, or surfaces when required

### 12. Implementation handoff

List exact skills to load:
- `akka-web-ui-apps`
- `akka-web-ui-ux-design` for non-trivial browser app UX planning
- `akka-web-ui-frontend-project` for standard frontend projects
- focused frontend companions
- Akka HTTP endpoint companions
- testing skills

List files to create or update:
- frontend project source under `frontend/**`
- built/static assets under `src/main/resources/static-resources/**`
- endpoint classes under `src/main/java/**/api/`
- endpoint tests under `src/test/java/**`

## Done criteria

A web UI decomposition is ready for implementation when a coding agent can answer:
- which functional agents appear in the left rail and why?
- how do the main workstream, composer, context indicators, and structured surfaces behave?
- which conventional routes or pages exist only as implementation/deep-link details?
- what should the user understand in the first five seconds of each shell region or surface?
- what is the primary action and why?
- what data does each surface need?
- what human surface graph edges and browser-tool invocations are possible?
- what happens while loading, empty, failed, unauthorized, or submitting?
- what UX copy appears for buttons, empty states, validation, errors, and success?
- what backend endpoints are required?
- which style guide and CSS tokens drive the frontend styling?
- which standard frontend project/framework conventions apply?
- what frontend modules/components should be created or reused from the `frontend/src/workstream/**` reference?
- what tests prove the browser app was delivered correctly, including shell/rail/composer/surface/action/deep-link/realtime and foundation workstream vertical contract coverage?
