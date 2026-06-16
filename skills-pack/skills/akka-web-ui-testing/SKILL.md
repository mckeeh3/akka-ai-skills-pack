---
name: akka-web-ui-testing
description: Test Akka-hosted full web apps, including standard frontend project checks, Akka endpoint integration tests, route/asset assertions, and optional DOM or browser smoke tests.
---

# Akka Web UI Testing

Use this skill when adding or reviewing tests for Akka-hosted browser apps.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

- `../docs/web-ui-quality-checklist.md`
- `../docs/web-ui-style-guide.md`
- `../akka-http-endpoint-testing/SKILL.md`
- existing `src/test/java/**WebUi*Test.java`
- project `frontend/package.json` when present
- project frontend test/build config when present

## Default test layers

### 1. Frontend checks and build

Run the frontend project's configured checks/build, for example:

```bash
cd frontend
npm test -- --run    # if configured
npm run build
```

Use these for DTO types, state models, API client code, component/render logic, and DOM typing where applicable.

### 2. Akka endpoint integration tests

Use `httpClient` to test:
- page route returns packaged HTML
- CSS and JS asset routes return expected content, including hashed built assets when using Vite or similar bundlers
- page references the expected CSS/JS assets and API/SSE/WebSocket routes
- JSON APIs return browser-facing DTOs
- command APIs cover success and validation failure
- secured APIs return expected unauthorized/forbidden behavior when applicable

### 3. Frontend logic tests

If the project has a frontend test setup, test focused frontend logic such as:
- API response normalization
- validation functions
- state transitions
- route parsing
- realtime message parsing and merge/idempotency behavior

Do not add a new heavy frontend test framework unless the user explicitly wants it. If an existing frontend project already has Vitest, React Testing Library, Playwright, or similar, use the existing setup rather than replacing it.

### 4. Browser or manual smoke tests

For feature-bearing generated SaaS UI work, the sprint/task must have a smoke path even if the project does not yet have a browser automation framework. Prefer an existing cheap/stable automated browser or DOM smoke test when available. If adding Playwright/Cypress/etc. would be heavy or unstable, record an explicit manual smoke checklist/result instead. A UI feature must not be marked `runtime-ready` from frontend contract tests, screenshots, typecheck, build, fixture rendering, or story/demo data alone.

Cover:
- page loads in the locally running Akka-hosted app for feature-bearing generated SaaS UI; an equivalent test route is acceptable only for non-feature mechanics or an explicitly recorded limitation
- primary action works through the intended real API/client path, not fixture-only or frontend-only data
- role/AuthContext/tenant setup and at least one forbidden/denied/hidden state are exercised when auth is in scope
- trace/correlation/status copy is visible or inspectable without exposing secrets
- validation or forbidden error appears when expected
- keyboard focus path for a key form or dialog

## Required assertions for serious UIs

A complete UI should have tests or explicit manual review notes for:
- loading/empty/error/success states
- form validation and server validation mapping
- structured-surface form control styling, including checks that important detail-edit/settings inputs, selects, and textareas use designed tokenized classes/selectors instead of unstyled browser-default/native controls
- packaged asset delivery, including the stylesheet implementing the selected style guide and named-theme token bundles
- API contract shape
- realtime route references when used
- protected-route behavior for generated SaaS UI, including forbidden/disabled/wrong-context cases for protected surfaces
- local Akka-hosted app smoke path for the visible feature before marking the UI feature complete
- CSS style-guide output staying aligned with the authoritative selected style, available named themes, default theme id, styled structured-surface form controls, and My Account selection behavior when in scope
- immediate named-theme preview on selection plus governed Save/Confirm persistence behavior when My Account theme settings are in scope
- frontend build output staying in sync with source

## Anti-patterns

Avoid:
- only testing that `/ui` returns 200
- skipping the frontend build after editing frontend source
- testing implementation details instead of contracts and state transitions
- adding Playwright/Cypress by default when the project does not already use them
