# Web UI quality checklist

Use this checklist before completing any non-trivial Akka-hosted browser UI.

## UX completeness

- [ ] Primary user goals are represented by screens and actions.
- [ ] Navigation paths are clear.
- [ ] Empty states explain what to do next.
- [ ] Loading states are visible for async regions.
- [ ] Success states are visible after actions.
- [ ] Errors are visible and actionable.
- [ ] Destructive or irreversible actions have appropriate confirmation or recovery behavior.

## Frontend state

- [ ] UI state explicitly represents loading, ready, empty, error, submitting, success, and stale/reconnecting where relevant.
- [ ] Render functions update the DOM from state without making API calls.
- [ ] API calls do not directly mutate unrelated DOM regions.
- [ ] User input is preserved after validation errors.

## API contracts

- [ ] Browser-facing DTOs are intentional and not accidental domain leaks.
- [ ] API routes use clear `/api/...` paths.
- [ ] Validation errors have a documented shape.
- [ ] Unauthorized, forbidden, not-found, conflict, and server errors are handled.
- [ ] Endpoint integration tests cover success and important failures.

## Forms and validation

- [ ] Every input has a label.
- [ ] Client validation handles quick feedback.
- [ ] Backend validation remains authoritative.
- [ ] Server validation errors map to field/form messages.
- [ ] Submit buttons are disabled while submitting.
- [ ] Duplicate-submit/idempotency behavior is clear.

## Style guide and theme

- [ ] A selected style guide/theme is recorded in `app-description/55-ui/style-guide.md`, `specs/cross-cutting/*ui-style-guide*.md`, or an equivalent authoritative UI spec.
- [ ] `app.css` implements the selected theme through CSS variables/tokens rather than scattered hard-coded values.
- [ ] Light/dark/system mode behavior matches the style guide.
- [ ] Brand adaptations are applied without copying demo names, logos, users, or metrics from reference images.
- [ ] Status colors, chart colors, shadows, spacing, radius, and component density match the selected style guide closely enough to guide future regeneration.

## Accessibility

- [ ] Page has a clear title/heading and main landmark.
- [ ] Heading order is logical.
- [ ] Interactive elements are keyboard-operable.
- [ ] Focus states are visible.
- [ ] Focus moves intentionally after validation failure or major screen changes.
- [ ] Errors are associated with fields or announced near the form.
- [ ] Style-guide contrast requirements are met for text, controls, charts, badges, and status indicators.
- [ ] Status is not conveyed by color alone.
- [ ] Dynamic user-controlled content uses `textContent` rather than unsafe HTML injection.

## Responsive layout

- [ ] Layout works on narrow screens.
- [ ] Controls are comfortably clickable/tappable.
- [ ] Tables or dense lists have a narrow-screen strategy.
- [ ] Loading, empty, and error states work responsively.

## Realtime behavior

If SSE/WebSocket is used:
- [ ] Connection status is visible.
- [ ] Reconnecting/stale behavior is defined.
- [ ] Malformed messages do not crash the UI.
- [ ] Duplicate or replayed messages are handled idempotently.
- [ ] Connections are cleaned up when no longer needed.

## Security/session UX

Security implementation is covered by separate security guidance. For web UI integration-only work, record any auth/session placeholders and do not invent identity-provider, JWT, or authorization behavior.

- [ ] Public UI asset routes and backend API routes are intentionally separated.
- [ ] Sensitive data is not embedded in static assets.
- [ ] Unauthorized/expired-session and forbidden-action UX is covered when security is in scope.

## Build and tests

- [ ] Standard frontend project checks/build pass when `frontend/` exists.
- [ ] `npm run check:web-ui` and `npm run build:web-ui` pass when using lightweight TypeScript.
- [ ] Served CSS corresponds to the selected web UI style guide.
- [ ] Served JS/CSS assets correspond to the frontend source build output.
- [ ] Endpoint tests fetch packaged HTML/CSS/JS through `httpClient`.
- [ ] Tests assert route references for API/SSE/WebSocket dependencies.
- [ ] Backend tests pass for changed endpoint/component behavior.
