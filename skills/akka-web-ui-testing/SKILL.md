---
name: akka-web-ui-testing
description: Test Akka-hosted web UIs, including standard frontend projects and lightweight TypeScript apps, with frontend checks, Akka endpoint integration tests, route/asset assertions, and optional DOM or browser smoke tests.
---

# Akka Web UI Testing

Use this skill when adding or reviewing tests for Akka-hosted browser apps.

## Required reading

- `../../../docs/web-ui-quality-checklist.md`
- `../../../docs/web-ui-style-guide.md`
- `../akka-http-endpoint-testing/SKILL.md`
- existing `src/test/java/**WebUi*Test.java`
- project `frontend/package.json` when present
- project frontend test/build config when present
- root `package.json` and `tsconfig.web-ui.json` when using lightweight TypeScript

## Default test layers

### 1. Frontend checks and build

For a standard frontend project, run the project's configured checks/build, for example:

```bash
cd frontend
npm test -- --run    # if configured
npm run build
```

For lightweight TypeScript, run:

```bash
npm run check:web-ui
npm run build:web-ui
```

Use these for DTO types, state unions, API client code, component/render logic, and DOM typing.

### 2. Akka endpoint integration tests

Use `httpClient` to test:
- page route returns packaged HTML
- CSS and JS asset routes return expected content, including hashed built assets when using Vite or similar bundlers
- page references the expected CSS/JS assets and API/SSE/WebSocket routes
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

Do not add a new heavy frontend test framework unless the user explicitly wants it. If an existing frontend project already has Vitest, React Testing Library, Playwright, or similar, use the existing setup rather than replacing it.

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
- packaged asset delivery, including the stylesheet implementing the selected style guide
- API contract shape
- realtime route references when used
- protected-route behavior when a security task has put it in scope
- CSS style-guide output staying aligned with the authoritative selected style
- frontend build output staying in sync with source

## Anti-patterns

Avoid:
- only testing that `/ui` returns 200
- skipping the frontend build after editing frontend source
- testing implementation details instead of contracts and state transitions
- adding Playwright/Cypress by default when the project does not already use them
