# Sprint 02: Frontend Prefill and No-Model Routing Tests

## Goal

Make routed surfaces render prefilled fields and prove the browser workstream path behaves quickly and safely.

## Required outcomes

- Surface envelopes can carry prefill data in a consistent browser-safe shape.
- User Admin Organization Create and invitation-related forms consume prefill data without bypassing validation or submit actions.
- Workstream items clearly state that a surface was opened from the request and that the user must review/submit.
- Frontend tests cover prefill rendering, no immediate mutation, and fallback behavior.

## Validation focus

- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `git diff --check`
