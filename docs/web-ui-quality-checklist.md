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

## Accessibility

- [ ] Page has a clear title/heading and main landmark.
- [ ] Heading order is logical.
- [ ] Interactive elements are keyboard-operable.
- [ ] Focus states are visible.
- [ ] Focus moves intentionally after validation failure or major screen changes.
- [ ] Errors are associated with fields or announced near the form.
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

- [ ] Public UI routes and protected API routes are intentionally separated.
- [ ] Unauthorized/expired-session behavior is visible to the user.
- [ ] Forbidden actions are hidden, disabled, or fail with clear messaging as appropriate.
- [ ] Sensitive data is not embedded in static assets.

## Build and tests

- [ ] `npm run check:web-ui` passes when TypeScript exists.
- [ ] `npm run build:web-ui` has been run after TypeScript changes.
- [ ] Served JS corresponds to the TypeScript source.
- [ ] Endpoint tests fetch packaged HTML/CSS/JS through `httpClient`.
- [ ] Tests assert route references for API/SSE/WebSocket dependencies.
- [ ] Backend tests pass for changed endpoint/component behavior.
