---
name: akka-web-ui-state-rendering
description: Model frontend state and DOM rendering for lightweight Akka-hosted TypeScript web UIs, including loading, empty, error, success, and stale states.
---

# Akka Web UI State and Rendering

Use this skill when implementing browser state, screen rendering, DOM updates, or navigation-aware display logic.

## Required reading

- `../../../docs/web-ui-lightweight-typescript-architecture.md`
- `../../../docs/web-ui-quality-checklist.md`
- existing `src/main/web-ui/**/state.ts` and `render.ts` if present

## State model rules

Use explicit state shapes. Prefer discriminated unions for async data:

```ts
type RemoteData<T> =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "ready"; value: T }
  | { status: "empty" }
  | { status: "error"; message: string }
  | { status: "stale"; value: T; message: string };
```

Do not hide these states in booleans such as `isLoading` plus nullable data unless the UI is trivial.

## Rendering rules

1. Rendering reads UI state and updates DOM; it should not call backend APIs.
2. Every async region needs loading, empty, ready, and error output.
3. Preserve focus when rerendering interactive regions where possible.
4. Use semantic HTML before ARIA.
5. Escape user-provided content by assigning `textContent`, not `innerHTML`.
6. Keep `innerHTML` only for static trusted templates or avoid it entirely.
7. Disable controls while submitting and make progress visible.

## Navigation

If the UI has more than one screen:
- define screen IDs or route names in `routes.ts`
- expose active navigation state visibly
- support meaningful URLs with hash or history routes when useful
- render not-found states for unknown routes

## Done criteria

A state/rendering implementation is done when:
- all planned screens render from explicit state
- every data dependency has loading/empty/error behavior
- action results update state without requiring a full page reload unless intentional
- failures are visible to users, not just logged
