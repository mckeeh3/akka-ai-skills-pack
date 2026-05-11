---
name: akka-web-ui-state-rendering
description: Model frontend state and rendering for Akka-hosted full web apps, including loading, empty, error, success, and stale states in standard frontend projects.
---

# Akka Web UI State and Rendering

Use this skill when implementing browser state, screen rendering, DOM updates, or navigation-aware display logic.

## AI-first state role

For AI-first SaaS surfaces, model supervision and governance state explicitly. Do not collapse consequential work into a generic loading/error list. Represent active objective status, plan progress, agent activity, pending approval, exception, blocked-by-policy, evidence-ready, stale/reconnecting, committed policy change, trace-linked decision, and outcome-updated states when those concepts appear in the UI contract.

Prefer state names that match the human operating model, for example `requiresApproval`, `agentWorking`, `exceptionRaised`, `policySimulationReady`, `traceAvailable`, or `outcomePendingReview`, instead of exposing backend component names. Keep authority-sensitive actions disabled with visible reasons when a user lacks permission, evidence is incomplete, a policy gate blocks execution, or the current data is stale.

## Required reading

- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-quality-checklist.md`
- existing frontend state/rendering code under `frontend/src/**` if present

## State model rules

Use explicit state shapes. In TypeScript code, prefer discriminated unions for async data:

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

1. Rendering reads UI state and updates the view; it should not call backend APIs.
2. Every async region needs loading, empty, ready, and error output.
3. Preserve focus when rerendering interactive regions where possible.
4. Use semantic HTML before ARIA.
5. Escape user-provided content: use safe component text binding in frontend frameworks or `textContent` for direct DOM updates.
6. Avoid unsafe HTML injection; keep `innerHTML`/dangerous HTML APIs only for static trusted templates or avoid them entirely.
7. Disable controls while submitting and make progress visible.

## Navigation

If the UI has more than one screen:
- define screen IDs or route names in the project's router/navigation structure
- expose active navigation state visibly
- support meaningful URLs with hash or history routes when useful
- render not-found states for unknown routes

## Done criteria

A state/rendering implementation is done when:
- all planned screens render from explicit state
- every data dependency has loading/empty/error behavior
- AI-first supervision screens distinguish working, waiting-for-human, exception, policy-blocked, stale, trace-ready, and outcome-review states when applicable
- action results update state without requiring a full page reload unless intentional
- failures are visible to users, not just logged
- approval, rejection, escalation, policy-commit, and feedback actions show concrete success/failure states when those actions are in scope
