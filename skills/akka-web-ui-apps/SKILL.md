---
name: akka-web-ui-apps
description: Plan and implement fully capable lightweight browser apps hosted by Akka HTTP endpoints using plain HTML, CSS, and framework-free TypeScript. Use when the task is a user-facing web app, not merely static file serving.
---

# Akka Web UI Apps

Use this as the top-level skill for complete browser app work in Akka services.

This skill complements `akka-http-endpoint-web-ui`:
- `akka-web-ui-apps` designs and implements the frontend application experience.
- `akka-http-endpoint-web-ui` hosts packaged assets and connects UI routes to Akka HTTP endpoints.

## Required reading

Read these first if present:
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-lightweight-typescript-architecture.md`
- `../../../docs/web-ui-api-contract-patterns.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../../../docs/web-ui-pattern-selection.md`
- `../akka-http-endpoint-web-ui/SKILL.md`
- existing `src/main/web-ui/**`
- existing `src/main/resources/static-resources/**`
- matching endpoint and endpoint tests under `src/main/java/**/api` and `src/test/java/**`

Canonical reference example:
- `../../../src/main/java/com/example/api/FrontendReferenceUiEndpoint.java`
- `../../../src/main/java/com/example/api/FrontendReferenceApiEndpoint.java`
- `../../../src/main/web-ui/frontend-reference/`
- `../../../src/main/resources/static-resources/frontend-reference/`
- `../../../src/test/java/com/example/application/FrontendReferenceWebUiIntegrationTest.java`

## Use this skill when

- the user asks for a web app, dashboard, admin UI, console, portal, or browser workflow
- the UI needs multiple states, screens, forms, actions, or data dependencies
- a UI brief must become implementation-ready frontend work
- the frontend should be excellent while keeping the stack minimal
- browser code should be plain TypeScript with no frontend framework

Do not use this as the main skill for a single static page or OpenAPI file; use `akka-http-endpoint-static-content` instead.

## Planning output before coding

Before implementing, produce a frontend plan with:
1. User goals and personas
2. Screens and navigation
3. Data dependencies and API contracts
4. Actions, forms, and validation rules
5. Frontend state model, including loading/empty/error/success states
6. Real-time behavior, if any
7. Auth/session and authorization UX
8. Accessibility and responsive requirements
9. Akka HTTP endpoint route plan
10. Implementation skills to load
11. Required tests and quality checks

## Skill routing

Load only the focused companions needed:

- `akka-web-ui-lightweight-typescript` — module structure and browser app architecture
- `akka-web-ui-api-client` — typed fetch clients and API error mapping
- `akka-web-ui-state-rendering` — state model, render functions, DOM update boundaries
- `akka-web-ui-forms-validation` — forms, validation, submit state, server error mapping
- `akka-web-ui-realtime` — SSE/WebSocket browser behavior
- `akka-web-ui-accessibility-responsive` — semantic HTML, keyboard, focus, responsive layout
- `akka-web-ui-testing` — TypeScript checks, endpoint tests, optional DOM/browser smoke checks

Always pair with Akka hosting/API skills as needed:
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-sse`
- `akka-http-endpoint-websocket`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-testing`

## Default implementation order

1. Define UI screens, states, and API contracts.
2. Implement or adjust backend JSON/SSE/WebSocket endpoints.
3. Add static HTML/CSS shell and accessible structure.
4. Add TypeScript modules: types, API client, state, render, actions/forms, realtime.
5. Compile with `npm run build:web-ui`.
6. Add/extend endpoint integration tests for page, assets, and API routes.
7. Run `npm run check:web-ui` and backend tests.
8. Review with `docs/web-ui-quality-checklist.md`.

## Quality bar

A complete web UI must handle:
- initial loading
- empty data
- successful data
- validation failures
- backend/API errors
- unauthorized/forbidden responses when protected
- disabled/submitting states for actions
- responsive layout at common viewport widths
- keyboard navigation and visible focus
- live-update reconnect/stale behavior when realtime is used

## Anti-patterns

Avoid:
- treating a serious app UI as one inline `app.ts` file
- exposing internal domain objects directly to the browser
- implementing only the happy path
- assuming route tests are enough for frontend logic
- adding React, Angular, Vue, Vite, Webpack, or heavy state libraries for this pack wave
- hiding auth/session behavior from the UI plan
- skipping accessible labels, focus behavior, or responsive layout
