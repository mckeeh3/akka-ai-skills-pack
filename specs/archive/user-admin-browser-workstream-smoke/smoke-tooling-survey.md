# User Admin Browser Workstream Smoke Tooling Survey

## Scope surveyed

This survey covers `TASK-UABWS-01-001` for the User Admin / `agent-user-admin` browser workstream smoke mini-project. It inspected the existing frontend scripts, Vite config, backend hosting endpoints, Workstream API client/endpoint path, and current test dependencies to choose the smallest reliable local smoke approach.

## Existing frontend tooling

- `frontend/package.json` scripts:
  - `npm --prefix frontend test -- --run` runs `node --test src/*.test.mjs` contract tests.
  - `npm --prefix frontend run typecheck` runs `tsc --noEmit`.
  - `npm --prefix frontend run build` runs Vite and writes production assets to `src/main/resources/static-resources`.
  - `npm --prefix frontend run dev` runs the Vite dev server with `/api` and `/ui` proxied to `http://localhost:9000`.
- Dependencies are limited to React, Vite, TypeScript, WorkOS AuthKit, markdown rendering, and types. There is no existing Playwright, Puppeteer, Cypress, JSDOM, happy-dom, or React Testing Library dependency.
- Current frontend tests are source/contract-level Node tests under `frontend/src/*.test.mjs`. They are useful guardrails but are not browser automation and do not execute rendered React against the hosted app.
- Vite production output is already configured for Akka hosting via `../src/main/resources/static-resources`.

## Existing backend/frontend hosting path

- `StarterFrontendEndpoint` serves the production app from Akka static resources:
  - `/` -> `index.html`
  - `/ui` -> `index.html`
  - `/workstream` -> `index.html`
  - `/accept` -> `index.html`
  - `/assets/**` -> static assets
- `WorkstreamEndpoint` exposes the browser workstream API under `/api/workstream` and requires bearer JWT validation:
  - `GET /api/workstream/bootstrap`
  - `GET /api/workstream/functional-agents`
  - `GET /api/workstream/items`
  - `GET /api/workstream/surfaces/{surfaceId}`
  - `POST /api/workstream/actions`
  - `POST /api/workstream/shell-requests`
  - `POST /api/workstream/messages`
  - `GET /api/workstream/events` for SSE refresh hints
- The browser client path is `main.tsx` -> `HttpWorkstreamApiClient` / `HttpWorkstreamRealtimeClient` -> same-origin `/api/workstream/**` with `Authorization`, `X-Selected-Context-Id`, and `X-Correlation-Id` headers.
- The frontend root is intentionally fail-closed when `VITE_WORKOS_CLIENT_ID` is not configured: it renders a WorkOS/AuthKit configuration gate and does not provide a fixture mode in normal runtime.

## Existing authorization and deterministic-data considerations

- The workstream HTTP endpoint obtains identity from JWT claims through `WorkosIdentityResolver.fromClaims(...)` and resolves the selected context from `X-Selected-Context-Id`, `X-Selected-Membership-Id`, or `selectedContextId`.
- Current deterministic User Admin backend tests seed accounts, tenants, memberships, settings, invitations, agent behavior, attention, and traces through test-only repositories in `WorkstreamServiceTest`.
- Normal hosted browser runtime still needs a signed WorkOS/AuthKit session and a resolvable backend identity. The default smoke suite must not require external WorkOS, Resend, or model credentials, so the next task needs a test-only authorized context path before a hosted browser smoke can traverse protected `/api/workstream/**` calls.
- Existing environment caveat remains: inherited `ADMIN_USERS` values can perturb deterministic backend tests. Smoke setup and commands should unset or explicitly control `ADMIN_USERS` unless they are intentionally validating production bootstrap behavior.

## Candidate approaches

### 1. Add Playwright/Cypress browser automation now

Pros:
- Closest match to the desired real browser/workstream smoke.
- Can verify focus, clickability, visible secrets, route transitions, and rendered surfaces.

Cons:
- No browser automation dependency exists today.
- Browser install/runtime requirements would be new and may be heavy for this repository.
- It still needs deterministic local auth before it can exercise protected API paths.

Decision: defer adding Playwright/Cypress until deterministic auth/seed setup exists and the implementation task can decide whether the extra dependency is justified by the final smoke command.

### 2. DOM-driven React smoke with JSDOM/Testing Library

Pros:
- Lower weight than full browser automation.
- Could exercise React components and fake clients.

Cons:
- No DOM test dependency exists today.
- Fake clients would be fixture-only and would not prove the hosted Akka/API path required by the mini-project done state.

Decision: not selected as the primary approach. It can supplement UI state checks later, but it must not be presented as the normal runtime smoke.

### 3. HTTP + static asset smoke only

Pros:
- Minimal dependencies.
- Fits existing Akka `TestKitSupport` + `httpClient` patterns.
- Can prove static asset hosting and protected API denial behavior.

Cons:
- Cannot verify browser interaction, focus recovery, or rendered User Admin traversal by itself.
- Does not prove AuthKit/browser token acquisition or UI clicks.

Decision: use as Stage A only: a required deterministic preflight for `/`, `/ui`, assets, protected API denial, and eventually authorized API bootstrap once test auth exists.

### 4. Staged smoke: Akka HTTP preflight + deterministic authorized workstream setup + browser automation/manual fallback

Pros:
- Matches existing project tooling first and avoids prematurely adding heavy browser dependencies.
- Preserves the real local hosted UI/API target as the final proof path.
- Allows the next task to implement a safe test-only auth/seed seam before selecting final browser runner mechanics.
- Can make progress with machine-checkable HTTP/API assertions while leaving browser automation as the explicit next implementation step.

Cons:
- Requires more than one task before full browser coverage exists.
- Browser command is not finalized until deterministic auth setup is available.

Decision: selected.

## Selected implementation approach

Use a staged smoke approach:

1. **Stage A: Akka-hosted frontend/API preflight**
   - Add or reuse an Akka HTTP integration test/script that verifies `/`, `/ui`, `/workstream`, `index.html`, and referenced `/assets/**` are served from `src/main/resources/static-resources`.
   - Verify `/api/workstream/bootstrap` denies unauthenticated access with an explicit 401/403-style failure rather than leaking data.
   - Once deterministic auth setup exists, verify authorized bootstrap and direct surface fetches for `surface-user-admin-dashboard`, `surface-user-admin-users`, representative detail/task surfaces, and denied/blocked `system_message` surfaces.

2. **Stage B: deterministic local smoke setup**
   - Implement or document a test-only authorized User Admin context that does not require external WorkOS/Resend/model credentials.
   - The setup must be tenant-scoped, selected-context aware, and fail closed outside test mode.
   - It must not weaken production `ADMIN_USERS` bootstrap or expose hidden data/provider identifiers.

3. **Stage C: browser/workstream smoke**
   - Prefer automated browser smoke if a minimal, reliable runner can be added after Stage B.
   - If browser automation is unavailable in the environment, provide a repeatable local smoke command plus a manual fallback checklist as an interim artifact and queue follow-up automation rather than claiming fixture-only completion.
   - The smoke should traverse hosted UI/workstream shell -> User Admin -> dashboard -> User Directory -> read-only detail -> task surface -> typed denied/blocked `system_message` and assert no raw secrets/tokens/provider ids are visible.

## Local app-run options identified

- Production-like hosted assets: run the Akka service locally on `localhost:9000` and browse `/ui` or `/workstream` after building frontend assets with `npm --prefix frontend run build`.
- Frontend dev server: `npm --prefix frontend run dev` serves Vite on `localhost:5173` and proxies `/api` and `/ui` to `localhost:9000`; useful for development, but the smoke should ultimately prove Akka-hosted assets for completion.
- TestKit path: backend integration tests can exercise the frontend endpoint and workstream API through `httpClient` without a separately launched server. This is the safest Stage A implementation path.

## Smoke targets for later tasks

- Functional agent: `agent-user-admin`.
- Dashboard surface: canonical `surface-user-admin-dashboard`, resolved backend surface `surface-user-admin-tenant-dashboard`.
- List surface: `surface-user-admin-users` with contract `user_admin.users.v1`.
- Detail/task examples: user detail, invitation create, access review task, support/role lifecycle surfaces where deterministic data allows.
- Denied/blocked path: typed `system_message` or blocked status for unavailable/forbidden/provider-fail-closed flows.
- Redaction assertions: page/API payloads must not visibly contain raw invite tokens, token hashes, provider secrets, API keys, or provider ids beyond safe trace/model labels already intended for user display.

## Decision for the next task

`TASK-UABWS-02-001` should implement deterministic local smoke setup first. It should provide a safe test-only authorized User Admin context for the protected `/api/workstream/**` path and prove production remains fail-closed outside that mode. After that, `TASK-UABWS-03-001` can add the browser/workstream smoke using the staged approach above without rediscovering auth, seed data, or route targets.
