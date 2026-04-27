# Lightweight TypeScript architecture for Akka-hosted web UIs

Use this doc for fully capable browser apps that should remain framework-free.

## Stack boundary

- Backend: Akka Java SDK components and HTTP endpoints.
- Frontend: plain HTML, CSS, and TypeScript compiled to browser JavaScript.
- No React, Angular, Vue, Vite, Webpack, or runtime state libraries by default.

## Source and served paths

Use:

```text
src/main/web-ui/<app>/app.ts
src/main/resources/static-resources/<app>/app.js
src/main/resources/static-resources/<app>/index.html
src/main/resources/static-resources/<app>/app.css
```

Run:

```bash
npm run check:web-ui
npm run build:web-ui
```

## Canonical reference example

Read:
- `src/main/web-ui/frontend-reference/`
- `src/main/resources/static-resources/frontend-reference/`
- `src/main/java/com/example/api/FrontendReferenceUiEndpoint.java`
- `src/main/java/com/example/api/FrontendReferenceApiEndpoint.java`
- `src/test/java/com/example/application/FrontendReferenceWebUiIntegrationTest.java`

## Module layout for non-trivial apps

```text
src/main/web-ui/<app>/
  app.ts       # bootstrap and event wiring; loaded by HTML as `type="module"`
  types.ts     # DTOs and UI state types
  api.ts       # typed fetch client and error normalization
  state.ts     # initial state and state transition helpers
  render.ts    # DOM rendering from state
  dom.ts       # DOM lookup and small DOM utilities
  routes.ts    # optional screen routing
  forms.ts     # optional form parsing and validation
  realtime.ts  # optional SSE/WebSocket lifecycle
```

Create only modules that the app needs, but split once `app.ts` mixes unrelated concerns.

## State pattern

Prefer explicit unions:

```ts
export type RemoteData<T> =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "ready"; value: T }
  | { status: "empty" }
  | { status: "error"; message: string }
  | { status: "stale"; value: T; message: string };
```

Use app-specific state:

```ts
export interface AppState {
  screen: Screen;
  summary: RemoteData<Summary>;
  submit: { status: "idle" | "submitting" | "success" | "error"; message?: string };
}
```

## Rendering pattern

- `render(state, elements)` updates the page from state.
- Renderers do not call `fetch` or open streams.
- Use `textContent` for dynamic user-controlled content.
- Use semantic HTML in `index.html`; TypeScript should enhance it.
- Keep errors visible and actionable.

## API client pattern

Use typed result objects rather than unhandled thrown errors for expected HTTP outcomes:

```ts
export type ApiResult<T> =
  | { ok: true; value: T }
  | { ok: false; error: ApiError };
```

Normalize common failures:
- network
- unauthorized
- forbidden
- notFound
- validation
- server
- malformedResponse

## Forms pattern

- Read form data in `forms.ts`.
- Return structured validation results.
- Keep backend validation authoritative.
- Disable submit while in flight.
- Preserve input on validation failure.
- Focus the first invalid field when possible.

## Realtime pattern

- Keep SSE/WebSocket lifecycle in `realtime.ts`.
- Map messages into state transitions.
- Show connection status.
- Define merge/idempotency rules.
- Close connections when screens no longer need them.

## File-size heuristic

Split a module when it contains more than one of:
- API calls
- DOM lookup
- rendering
- validation
- state transitions
- realtime lifecycle
- navigation

The objective is not ceremony; it is easy agent extension without accidental regressions.
