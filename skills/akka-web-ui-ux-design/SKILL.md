---
name: akka-web-ui-ux-design
description: Design excellent user experience for Akka-hosted browser apps before frontend implementation, including screen intent, information hierarchy, interaction quality, UX copy, feedback, recovery, accessibility, and responsive behavior.
---

# Akka Web UI UX Design

Use this skill before implementing any non-trivial browser app, dashboard, admin UI, console, portal, or workflow UI hosted by Akka.

Use this skill before implementing details with `akka-web-ui-frontend-project` so the full web app has an explicit UX contract.

## Required reading

- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-ux-patterns.md`
- `../../../docs/web-ui-style-guide.md`
- `../../../docs/web-ui-quality-checklist.md`
- relevant app-description UI files under `app-description/55-ui/**` or specs under `specs/**` when present

## Use this skill when

- a UI brief needs to become implementable screens
- the user asks for a dashboard, portal, admin console, or browser workflow
- a screen has forms, decisions, multi-step flows, dense data, status, or errors
- UX quality matters, not just route/static asset delivery
- existing UI work needs review for clarity, polish, accessibility, or task efficiency

Do not use this for static file serving or a single documentation page unless the page has a real user journey.

## UX plan required before implementation

For each screen or major region, define:

1. **User goal** — what the user is trying to accomplish.
2. **Primary decision/action** — the one thing that must be obvious.
3. **Secondary actions** — useful but visually subordinate actions.
4. **Information hierarchy** — most important data first; supporting detail later.
5. **Entry and exit paths** — how the user arrives, completes, cancels, or moves on.
6. **Loading experience** — skeleton, placeholder, spinner, or progress text.
7. **Empty state** — why there is no data and what the user can do next.
8. **Error recovery** — actionable recovery text and retry/correction path.
9. **Success feedback** — what changed and what the user can do next.
10. **Validation behavior** — field-level messages, focus movement, input preservation.
11. **Destructive/irreversible actions** — confirmation and recovery expectations.
12. **Responsive behavior** — how the primary task survives on narrow screens.
13. **Keyboard/focus path** — how a keyboard-only user completes the primary flow.
14. **UX copy** — labels, button text, helper text, empty/error/success messages.
15. **Style guide application** — how selected tokens support hierarchy and feedback.

## UX copy rules

Prefer specific, actionable copy:

- Buttons use verbs: `Submit request`, `Approve`, `Save changes`, `Retry`.
- Empty states explain the condition and next action.
- Validation messages say how to fix the input.
- Error messages explain what failed and what to try next.
- Success messages confirm the concrete outcome.
- Destructive confirmations name the object and consequence.

Avoid generic copy:

- `Submit` when a more specific action exists
- `Error occurred`
- `Invalid input`
- `Success`
- unexplained disabled controls

## Interaction quality rules

- Each screen has one dominant purpose.
- The primary action is visually obvious and reachable without scrolling on common layouts when practical.
- Secondary/destructive actions do not compete with the primary action.
- Dense data has search, filter, sort, grouping, or progressive disclosure when needed.
- Status is visible through text plus visual treatment, not color alone.
- Slow backend behavior is represented with loading/progress states.
- Users can recover from common mistakes without losing work.
- Navigation shows current location and supports returning to the prior task.

## Output format

Produce a concise UX handoff that downstream implementation can follow:

```text
## UX handoff: <app/screen>

User goal:
Primary action:
Secondary actions:
Information hierarchy:
States:
- loading:
- empty:
- ready:
- validation failure:
- API failure:
- success:
UX copy:
Responsive behavior:
Keyboard/focus behavior:
Implementation notes:
```

## Review checklist

Before coding or accepting UI work, verify:

- the first five seconds communicate where the user is and what they can do
- every screen has a clear primary action or clear read-only purpose
- empty/error/success states are useful, not placeholders
- forms preserve user input and focus the first problem after validation failure
- destructive actions are hard to trigger accidentally
- mobile layout preserves the main task
- keyboard-only flow reaches and completes primary actions
- selected style guide tokens are used to reinforce hierarchy, focus, and status
