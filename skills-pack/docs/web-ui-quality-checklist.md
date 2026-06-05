# Web UI quality checklist

Use this checklist before completing any non-trivial Akka-hosted browser UI.

For generated AI-first SaaS work in this source repository and downstream forks, compare end-to-end behavior against the runnable core app repository root, then compare UI architecture against `docs/workstream-ui-reference-architecture.md` and the reusable React/Vite reference under root `frontend/src/workstream/**`. The harness install includes pack examples under `.agents/skills/examples/**`, but application code belongs in the target project and root frontend source is not exported into `.agents`. The User Admin vertical contract test (`frontend/src/workstream-user-admin-vertical.contract.test.mjs`) is the canonical foundation-admin UI pattern. Legacy `frontend/src/screens/**` and removed standalone static UI fixtures are not canonical generated-app structure.

## UX completeness

- [ ] For generated AI-first SaaS, primary user goals are represented by role-authorized functional agents, workstream shell regions, structured surfaces, and capability-backed actions.
- [ ] Each workstream shell region or structured surface communicates selected functional agent, tenant/customer context, what matters, and what the user can do within the first five seconds.
- [ ] Each structured surface has one clear purpose or an explicitly read-only purpose.
- [ ] The primary action is visually obvious and uses specific action text.
- [ ] Secondary actions are useful but visually subordinate.
- [ ] Routes/navigation are treated as implementation and deep-link details into workstreams/surfaces, with current location/context visible where relevant.
- [ ] Empty states explain what happened and what to do next.
- [ ] Loading states are visible and context-specific for async regions.
- [ ] Success states confirm what changed and the next available action.
- [ ] Errors are visible, actionable, and preserve user work when possible.
- [ ] Destructive or irreversible actions have appropriate confirmation or recovery behavior.
- [ ] Dense data has appropriate search, filter, sort, grouping, or progressive disclosure.

## Frontend state

- [ ] UI state explicitly represents loading, ready, empty, error, submitting, success, and stale/reconnecting where relevant.
- [ ] Rendering/component code updates the view from state without making API calls.
- [ ] API calls do not directly mutate unrelated view regions.
- [ ] User input is preserved after validation errors.

## API contracts

- [ ] Structured surfaces, when used, have explicit payload, action, event, auth, trace/audit, and rendering-test contracts per `structured-surface-contracts.md`.
- [ ] Every surface action maps to a governed backend capability, including read/query and surface-request actions; frontend visibility, disabled controls, prompt text, and route names are not treated as authorization.
- [ ] Browser-facing DTOs are intentional and not accidental domain leaks.
- [ ] API routes use clear `/api/...` paths.
- [ ] Validation errors have a documented shape.
- [ ] Unauthorized, forbidden, not-found, conflict, and server errors are handled.
- [ ] Endpoint integration tests cover success and important failures.

## UX copy

- [ ] Button labels use specific verb phrases instead of generic `Submit` where possible.
- [ ] Empty states explain the condition and next action.
- [ ] Validation messages explain how to fix the input.
- [ ] API/load error messages say what failed and how to recover.
- [ ] Success messages confirm the concrete outcome.
- [ ] Destructive confirmations name the object and consequence.
- [ ] Disabled controls have visible explanation when the reason is not obvious.

## Forms and validation

- [ ] Every input has a label.
- [ ] Structured-surface form controls, including detail-edit inputs/selects/textareas, render with tokenized designed control styling rather than browser-default/native-looking controls.
- [ ] Client validation handles quick feedback.
- [ ] Backend validation remains authoritative.
- [ ] Server validation errors map to field/form messages.
- [ ] Submit buttons are disabled while submitting.
- [ ] Duplicate-submit/idempotency behavior is clear.

## Style guide

- [ ] A selected style guide is recorded in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or an equivalent authoritative UI spec.
- [ ] The authored stylesheet or component styling implements the selected AI-first style guide through CSS variables/tokens or project-standard styling configuration rather than scattered hard-coded values.
- [ ] The user-facing theme preference is named-theme based; available themes, default theme, and persistence scope are recorded.
- [ ] At least four initial named themes are present or explicitly required for generated AI-first SaaS: two light and two dark.
- [ ] My Account theme selection, when in scope, lets the user choose one available named theme, previews the selected named theme immediately on field change, and persists only through the governed save/confirm settings path.
- [ ] Brand adaptations are applied without copying demo names, logos, users, or metrics from reference images.
- [ ] Status colors, chart colors, shadows, spacing, radius, and component density match the selected style guide closely enough to guide future regeneration.

## Visual craft

- [ ] The UI has an intentional aesthetic direction recorded in the style guide rather than improvised generic SaaS styling.
- [ ] Typography, spacing, and density make the shell and structured surfaces feel production-grade and readable.
- [ ] Human-needed work, policy-blocked work, autonomous progress, trace/history, and FYI activity have distinct hierarchy without relying on color alone.
- [ ] Decision cards, structured-surface forms, audit timelines, governance diffs, denials, empty states, and errors feel deliberately designed, not default or placeholder.
- [ ] Motion and transitions clarify state changes and respect reduced-motion preferences.
- [ ] Cosmetic effects such as gradients, grain, glow, shadows, or patterns preserve contrast, focus visibility, and surface readability.
- [ ] Visual changes do not alter functional agents, workstreams, surface contracts, capability mappings, authorization, API behavior, tests, or readiness claims.

## Accessibility

- [ ] The workstream shell has a clear title/heading and main landmark.
- [ ] Structured surfaces use logical heading order.
- [ ] Interactive elements are keyboard-operable.
- [ ] Focus states are visible.
- [ ] Focus moves intentionally after validation failure, functional-agent change, route/deep-link change, modal open/close, or major surface changes.
- [ ] Errors are associated with fields or announced near the form.
- [ ] Style-guide contrast requirements are met for text, controls, charts, badges, and status indicators.
- [ ] Status is not conveyed by color alone.
- [ ] Dynamic user-controlled content uses `textContent` rather than unsafe HTML injection.

## Responsive layout

- [ ] Workstream shell and structured surface layout works on narrow screens.
- [ ] The primary task remains possible and obvious on narrow screens.
- [ ] Controls are comfortably clickable/tappable.
- [ ] Tables or dense lists have a narrow-screen strategy such as cards, reduced columns, or intentional horizontal scroll.
- [ ] Loading, empty, and error states work responsively.

## Realtime behavior

If SSE/WebSocket is used:
- [ ] Connection status is visible.
- [ ] Reconnecting/stale behavior is defined.
- [ ] Malformed messages do not crash the UI.
- [ ] Duplicate or replayed messages are handled idempotently.
- [ ] Connections are cleaned up when no longer needed.

## Security/session UX

For generated SaaS applications, the browser UI inherits the mandatory secure foundation. Record provider-specific placeholders when details are unknown, but do not invent or omit identity-provider, JWT, AuthContext, authorization, tenant/customer, or audit behavior. Only public static asset routes are outside authenticated API authorization.

- [ ] Public UI asset routes and backend API routes are intentionally separated.
- [ ] Sensitive data is not embedded in static assets.
- [ ] Unauthorized/expired-session and forbidden-action UX is covered for protected routes, including tenant/customer mismatch, disabled users, and role/scope denial.
- [ ] `/api/me`, selected context, browser-safe capabilities, profile, settings, and context-switch behavior are represented for generated full-stack AI-first SaaS.

## Build and tests

- [ ] The frontend project checks and production build pass.
- [ ] Served CSS corresponds to the selected web UI style guide.
- [ ] Served JS/CSS assets correspond to the frontend source build output.
- [ ] Endpoint tests fetch packaged HTML/CSS/JS through `httpClient`.
- [ ] Tests assert route references for API/SSE/WebSocket dependencies without promoting page routes as the primary app model.
- [ ] Workstream contract tests cover shell, rail, composer, structured surfaces, capability actions, deep links, forbidden/disabled states, stale/realtime behavior, and the User Admin reference vertical where foundation admin UI is in scope.
- [ ] UX review notes or tests cover key loading, empty, error, validation, success states, structured-surface form control styling, and immediate named-theme preview when those surfaces are in scope.
- [ ] Backend tests pass for changed endpoint/component behavior.
