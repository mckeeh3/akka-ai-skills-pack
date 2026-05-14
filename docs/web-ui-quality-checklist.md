# Web UI quality checklist

Use this checklist before completing any non-trivial Akka-hosted browser UI.

## UX completeness

- [ ] Primary user goals are represented by screens and actions.
- [ ] Each screen communicates where the user is, what matters, and what they can do within the first five seconds.
- [ ] Each screen has one clear purpose or an explicitly read-only purpose.
- [ ] The primary action is visually obvious and uses specific action text.
- [ ] Secondary actions are useful but visually subordinate.
- [ ] Navigation paths are clear and show current location where relevant.
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
- [ ] Client validation handles quick feedback.
- [ ] Backend validation remains authoritative.
- [ ] Server validation errors map to field/form messages.
- [ ] Submit buttons are disabled while submitting.
- [ ] Duplicate-submit/idempotency behavior is clear.

## Style guide and theme

- [ ] A selected style guide/theme is recorded in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or an equivalent authoritative UI spec.
- [ ] The authored stylesheet or component styling implements the selected theme through CSS variables/tokens or project-standard theme configuration rather than scattered hard-coded values.
- [ ] Light/dark/system mode behavior matches the style guide.
- [ ] Brand adaptations are applied without copying demo names, logos, users, or metrics from reference images.
- [ ] Status colors, chart colors, shadows, spacing, radius, and component density match the selected style guide closely enough to guide future regeneration.

## Accessibility

- [ ] Page has a clear title/heading and main landmark.
- [ ] Heading order is logical.
- [ ] Interactive elements are keyboard-operable.
- [ ] Focus states are visible.
- [ ] Focus moves intentionally after validation failure, navigation, modal open/close, or major screen changes.
- [ ] Errors are associated with fields or announced near the form.
- [ ] Style-guide contrast requirements are met for text, controls, charts, badges, and status indicators.
- [ ] Status is not conveyed by color alone.
- [ ] Dynamic user-controlled content uses `textContent` rather than unsafe HTML injection.

## Responsive layout

- [ ] Layout works on narrow screens.
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
- [ ] `/api/me`, selected context, browser-safe capabilities, profile, settings, and context-switch behavior are represented when a browser UI is in scope.

## Build and tests

- [ ] The frontend project checks and production build pass.
- [ ] Served CSS corresponds to the selected web UI style guide.
- [ ] Served JS/CSS assets correspond to the frontend source build output.
- [ ] Endpoint tests fetch packaged HTML/CSS/JS through `httpClient`.
- [ ] Tests assert route references for API/SSE/WebSocket dependencies.
- [ ] UX review notes or tests cover key loading, empty, error, validation, and success states.
- [ ] Backend tests pass for changed endpoint/component behavior.
