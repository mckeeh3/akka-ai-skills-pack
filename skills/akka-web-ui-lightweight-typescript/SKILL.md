---
name: akka-web-ui-lightweight-typescript
description: Implement framework-free TypeScript browser app structure for Akka-hosted web UIs. Use for modular frontend code without React/Vue/Angular/Vite.
---

# Akka Web UI Lightweight TypeScript

Use this skill when browser code is more than a few lines and needs a maintainable plain-TypeScript structure.

## Required reading

- `../../../docs/web-ui-lightweight-typescript-architecture.md`
- `../../../docs/web-ui-style-guide.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../../../tsconfig.web-ui.json`
- `../../../package.json`
- existing `src/main/web-ui/**`

## Default source layout

For app `<name>` use:

```text
src/main/web-ui/<name>/
  app.ts
  types.ts
  api.ts
  state.ts
  render.ts
  dom.ts
  forms.ts        # only if forms/actions exist
  realtime.ts     # only if SSE/WebSocket exists
  routes.ts       # only if multiple screens or deep links exist
```

Served assets remain under:

```text
src/main/resources/static-resources/<name>/
```

Before authoring `app.css`, confirm the selected style guide in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or another authoritative UI spec. If missing, add or request the pending style-selection question in `specs/pending-questions.md` rather than choosing colors, spacing, typography, or density implicitly.

## Rules

1. Keep Akka backend code in Java; keep browser interaction code in TypeScript.
2. Use plain DOM APIs and browser-native `fetch`, `EventSource`, and `WebSocket`.
3. Prefer small pure functions for mapping API data to UI state.
4. Keep `app.ts` as orchestration/bootstrap, not a dumping ground.
5. Keep DTO types in `types.ts`; do not duplicate incompatible shapes across files.
6. Keep DOM lookup helpers in `dom.ts` and fail visibly when required elements are missing.
7. Use explicit state transitions for loading, ready, empty, error, submitting, and stale states.
8. Implement selected theme tokens in plain CSS variables; TypeScript should toggle documented classes/states, not hard-code visual choices.
9. Run `npm run check:web-ui` and `npm run build:web-ui` after changes.

## Minimal module responsibilities

- `app.ts`: DOMContentLoaded bootstrap, initial load, event wiring.
- `types.ts`: API DTOs, UI state, discriminated unions.
- `api.ts`: typed HTTP calls and HTTP error normalization.
- `state.ts`: initial state and transition helpers.
- `render.ts`: pure-ish DOM rendering from state.
- `dom.ts`: element lookup, event helpers, small DOM utilities.
- `forms.ts`: form parsing, client validation, submit behavior.
- `realtime.ts`: SSE/WebSocket lifecycle and message mapping.
- `routes.ts`: hash/history routing and screen selection.

## Anti-patterns

Avoid:
- adding npm runtime dependencies for basic DOM work
- scattering `document.querySelector` across every module
- combining API calls, validation, rendering, and realtime code in one function
- mutating UI directly from API client functions
- compiling TypeScript into a served path that is hard to correlate with source
- selecting visual style in TypeScript or ad hoc CSS when the authoritative style guide is missing
