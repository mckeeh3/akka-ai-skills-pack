# Web UI frontend decomposition

Use this doc when requirements include a user-facing browser app hosted by Akka HTTP endpoints.

The goal is to make the frontend as intentionally designed as the backend while keeping the technical stack minimal: plain HTML, CSS, and framework-free TypeScript.

Read `docs/web-ui-style-guide.md` before implementation when no app-specific style guide is already selected.

## Required output

Before implementation, produce a frontend plan with these sections.

### 1. Users and goals

List:
- personas or actors
- primary goals
- permissions or role differences
- success criteria for each user journey

### 2. Screens and navigation

For each screen, define:
- route or UI path
- purpose
- primary actions
- secondary actions
- entry points and exits
- empty/not-found behavior

Prefer explicit route families:
- UI shell/assets: `/ui...`
- JSON APIs: `/api/...`
- streams: explicit stream prefixes
- WebSockets: `/websockets/...`

### 3. Data dependencies

For each screen or region, list:
- data needed on first render
- query/filter/sort inputs
- command/action endpoints
- polling, SSE, or WebSocket needs
- cache/stale behavior if relevant

### 4. Frontend state model

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
- stale/reconnecting when realtime is used

Do not implement only the happy path.

### 5. Forms and actions

For each form/action, define:
- input fields
- client validation rules
- backend validation rules
- submit route and method
- duplicate-submit/idempotency expectations
- success behavior
- server error mapping
- focus behavior after validation failure

### 6. API contracts

For each browser API, define:
- route and method
- request DTO
- success response DTO
- error response DTOs
- auth requirements
- endpoint integration tests

Use browser-facing DTOs, not accidental internal domain objects.

### 7. Realtime behavior

If the UI needs live updates, define:
- SSE vs WebSocket decision
- event/message types
- connection status display
- reconnect/stale behavior
- merge/idempotency rules
- cleanup behavior on navigation

### 8. Style guide and theme

Define:
- selected theme id/name from `docs/web-ui-style-guide.md`, a custom style guide, or `unselected`
- source reference and mode policy: light-only, dark-only, or system with both token sets
- typography, spacing, radius, elevation, color, chart, and status tokens
- layout density and shell/navigation treatment
- component style rules for cards, buttons, forms, tables/lists, charts, loading/empty/error/success states
- brand adaptations and a rule not to copy demo names/logos/data from reference images

If the app has a browser UI and style is `unselected`, add a pending `category: ui` style-selection question before materializing web UI implementation tasks.

### 9. Accessibility and responsive behavior

Define:
- semantic page landmarks
- heading structure
- keyboard path for primary flows
- focus movement after major actions
- field labels and error associations
- narrow-screen layout strategy
- style-guide accessibility constraints for contrast, focus rings, and color-not-alone status communication

### 10. Implementation handoff

List exact skills to load:
- `akka-web-ui-apps`
- focused frontend companions
- Akka HTTP endpoint companions
- testing skills

List files to create or update:
- TypeScript source files under `src/main/web-ui/<app>/`
- static assets under `src/main/resources/static-resources/<app>/`
- endpoint classes under `src/main/java/**/api/`
- endpoint tests under `src/test/java/**`

## Done criteria

A web UI decomposition is ready for implementation when a coding agent can answer:
- what screens exist?
- what data does each screen need?
- what user actions are possible?
- what happens while loading, empty, failed, unauthorized, or submitting?
- what backend endpoints are required?
- which style guide/theme and CSS tokens drive the generated HTML/CSS/TypeScript?
- what frontend modules should be created?
- what tests prove the browser app was delivered correctly?
