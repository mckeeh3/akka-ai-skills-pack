---
name: akka-web-ui-state-rendering
description: Model frontend state and rendering for Akka-hosted full web apps, including loading, empty, error, success, and stale states in standard frontend projects.
---

# Akka Web UI State and Rendering

Use this skill when implementing browser state, structured surface rendering, DOM updates, or workstream/deep-link-aware display logic.

## Generated SaaS input contract

For generated full-stack AI-first SaaS state/rendering work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- owning functional agent, workstream region, structured surface id/type/version, surface action/event, and deep-link behavior;
- governed capability id/class behind each consequential action/query and selected frontend/API/realtime exposure;
- `AuthContext`, tenant/customer scope, roles/capabilities, disabled/forbidden states, stale/reconnect behavior, and backend authorization boundary;
- DTOs, redaction, idempotency/correlation ids, policy/approval/escalation states, audit/work trace links, and rendering/API/realtime tests.

If these are absent for generated SaaS implementation, route back to `akka-web-ui-apps`, `agent-workstream-apps`, and `capability-first-backend` or repair the task brief instead of rendering generic UI state.

## AI-first state role

For AI-first SaaS surfaces, model supervision and governance state explicitly. Do not collapse consequential work into a generic loading/error list. Represent active objective status, plan progress, agent activity, pending approval, exception, blocked-by-policy, evidence-ready, stale/reconnecting, committed policy change, trace-linked decision, and outcome-updated states when those concepts appear in the UI contract.

Prefer state names that match the human operating model, for example `requiresApproval`, `agentWorking`, `exceptionRaised`, `policySimulationReady`, `traceAvailable`, or `outcomePendingReview`, instead of exposing backend component names. Keep authority-sensitive actions disabled with visible reasons when a user lacks permission, evidence is incomplete, a policy gate blocks execution, or the current data is stale.

## Required reading

- `../../docs/web-ui-frontend-decomposition.md`
- `../../docs/web-ui-quality-checklist.md`
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

## Workstream and deep-link state

For generated full-stack AI-first SaaS apps:
- define functional-agent ids, workstream item ids, surface ids, and route/deep-link names in the project's router/state structure
- expose selected functional agent, AuthContext, and active surface state visibly
- support meaningful URLs with hash or history routes when useful, but keep them as deep links into workstreams/surfaces
- render not-found, forbidden, and unavailable-agent states for unknown or unauthorized deep links

## Done criteria

A state/rendering implementation is done when:
- all planned workstream shell regions and structured surfaces render from explicit state
- every data dependency has loading/empty/error behavior
- AI-first supervision surfaces distinguish working, waiting-for-human, exception, policy-blocked, stale, trace-ready, and outcome-review states when applicable
- action results update state without requiring a full page reload unless intentional
- failures are visible to users, not just logged
- approval, rejection, escalation, policy-commit, and feedback actions show concrete success/failure states when those actions are in scope
