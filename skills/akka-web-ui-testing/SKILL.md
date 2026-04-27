---
name: akka-web-ui-testing
description: Test Akka-hosted lightweight web UIs with TypeScript checks, Akka endpoint integration tests, route/asset assertions, and optional DOM or browser smoke tests.
---

# Akka Web UI Testing

Use this skill when adding or reviewing tests for Akka-hosted browser apps.

## Required reading

- `../../../docs/web-ui-quality-checklist.md`
- `../akka-http-endpoint-testing/SKILL.md`
- existing `src/test/java/**WebUi*Test.java`
- `../../../package.json`
- `../../../tsconfig.web-ui.json`

## Default test layers

### 1. TypeScript compile checks

Run:

```bash
npm run check:web-ui
npm run build:web-ui
```

Use this for DTO types, state unions, API client code, and DOM typing.

### 2. Akka endpoint integration tests

Use `httpClient` to test:
- page route returns packaged HTML
- CSS and JS asset routes return expected content
- page references the expected API/SSE/WebSocket routes
- JSON APIs return browser-facing DTOs
- command APIs cover success and validation failure
- secured APIs return expected unauthorized/forbidden behavior when applicable

### 3. Frontend logic tests

If the project has a lightweight JS test setup, test pure TypeScript helpers:
- API response normalization
- validation functions
- state transitions
- route parsing
- realtime message parsing and merge/idempotency behavior

Do not add a heavy frontend test framework unless the user explicitly wants it.

### 4. Browser smoke tests

Optional. Add only when cheap and stable. Cover:
- page loads
- primary action works
- validation error appears
- keyboard focus path for a key form or dialog

## Required assertions for serious UIs

A complete UI should have tests or explicit manual review notes for:
- loading/empty/error/success states
- form validation and server validation mapping
- packaged asset delivery
- API contract shape
- realtime route references when used
- protected-route behavior when used
- TypeScript build output staying in sync with source

## Anti-patterns

Avoid:
- only testing that `/ui` returns 200
- skipping `npm run build:web-ui` after editing TypeScript
- testing implementation details instead of contracts and state transitions
- adding Playwright/Cypress by default to this pack
