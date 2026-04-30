---
name: akka-web-ui-apps
description: Plan and implement fully capable browser apps hosted by Akka HTTP endpoints, including standard frontend projects such as React/Vite and lightweight framework-free TypeScript apps. Use when the task is a user-facing web app, not merely static file serving.
---

# Akka Web UI Apps

Use this as the top-level skill for complete browser app work in Akka services.

This skill complements `akka-http-endpoint-web-ui`:
- `akka-web-ui-apps` designs the frontend application experience and chooses the frontend implementation shape.
- `akka-web-ui-frontend-project` integrates a standard frontend project such as React/Vite with Akka static hosting.
- `akka-web-ui-lightweight-typescript` remains available for small framework-free TypeScript apps.
- `akka-http-endpoint-web-ui` hosts packaged assets and connects UI routes to Akka HTTP endpoints.

## Required reading

Read these first if present:
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-style-guide.md`
- `../../../docs/web-ui-frontend-project-integration.md`
- `../../../docs/web-ui-lightweight-typescript-architecture.md`
- `../../../docs/web-ui-api-contract-patterns.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../../../docs/web-ui-pattern-selection.md`
- `../akka-http-endpoint-web-ui/SKILL.md`
- existing `frontend/**`
- existing `src/main/web-ui/**`
- existing `src/main/resources/static-resources/**`
- matching endpoint and endpoint tests under `src/main/java/**/api` and `src/test/java/**`

Canonical frontend project integration reference:
- `../../../frontend-with-akka-backend.md` (use only web UI integration sections unless security is explicitly in scope)

Canonical lightweight TypeScript reference example:
- `../../../src/main/java/com/example/api/FrontendReferenceUiEndpoint.java`
- `../../../src/main/java/com/example/api/FrontendReferenceApiEndpoint.java`
- `../../../src/main/web-ui/frontend-reference/`
- `../../../src/main/resources/static-resources/frontend-reference/`
- `../../../src/test/java/com/example/application/FrontendReferenceWebUiIntegrationTest.java`

## Use this skill when

- the user asks for a web app, dashboard, admin UI, console, portal, or browser workflow
- the UI needs multiple states, screens, forms, actions, or data dependencies
- a UI brief must become implementation-ready frontend work
- the frontend should be excellent and may need a standard frontend framework/build tool
- browser code should live in a dedicated frontend project when the UI is a real app
- plain TypeScript without a framework is still appropriate for intentionally lightweight apps

Do not use this as the main skill for a single static page or OpenAPI file; use `akka-http-endpoint-static-content` instead.

## Planning output before coding

Before implementing, verify that a selected style exists in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or another authoritative UI spec. If a browser UI is in scope and style is missing/unselected, add or update `specs/pending-questions.md` with the style-selection question from `../../../docs/web-ui-style-guide.md` and stop web UI implementation for the affected tasks.

Before implementing, produce a frontend plan with:
1. User goals and personas
2. Screens and navigation
3. Data dependencies and API contracts
4. Actions, forms, and validation rules
5. Frontend state model, including loading/empty/error/success states
6. Real-time behavior, if any
7. Frontend implementation shape: standard frontend project (for example React/Vite) or lightweight framework-free TypeScript
8. Selected web UI style guide/theme, mode policy, CSS tokens, layout density, component styling, and brand adaptations
9. Accessibility and responsive requirements
10. Akka HTTP endpoint route plan, including static asset and API route separation
11. SPA routing choice: hash routing, explicit server entry routes, or in-app navigation only
12. Implementation skills to load
13. Required tests and quality checks

For this skill family, defer auth/session/security implementation details unless the user explicitly asks for them. Record any known auth-dependent UI states as placeholders for the later security pass.

## Skill routing

Load only the focused companions needed:

- `akka-web-ui-frontend-project` — standard frontend project integration, such as React/Vite build output hosted by Akka
- `akka-web-ui-lightweight-typescript` — module structure and browser app architecture for framework-free apps
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
- `akka-http-endpoint-jwt` only when security implementation is explicitly in scope
- `akka-http-endpoint-testing`

## Default implementation order

1. Define UI screens, states, and API contracts.
2. Choose frontend shape: standard frontend project for full apps, lightweight TypeScript only for deliberately small apps.
3. Implement or adjust backend JSON/SSE/WebSocket endpoints.
4. Implement the frontend in its source root (`frontend/src/**` for frontend projects, or `src/main/web-ui/**` for lightweight TypeScript).
5. Build frontend assets into `src/main/resources/static-resources/`.
6. Add/extend endpoint integration tests for page, assets, explicit SPA entry routes, and API route separation.
7. Run frontend checks/build and backend tests.
8. Review with `docs/web-ui-quality-checklist.md`.

## Quality bar

A complete web UI must apply the selected style guide without copying demo content from the reference images.

A complete web UI must handle:
- initial loading
- empty data
- successful data
- validation failures
- backend/API errors
- unauthorized/forbidden placeholders when security behavior is already defined
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
- using the lightweight TypeScript pattern for a UI that clearly needs a full frontend project
- hand-editing generated frontend build output under `static-resources/`
- mixing static asset wildcards and backend API routes under ambiguous catch-all paths
- implementing auth/session/security details during a web UI integration-only pass
- silently choosing colors/theme when app-description/specs have not selected a style guide
- skipping accessible labels, focus behavior, or responsive layout
