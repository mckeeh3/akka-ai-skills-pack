---
name: akka-web-ui-accessibility-responsive
description: Apply accessibility, semantic HTML, keyboard, focus, and responsive layout guidance to lightweight Akka-hosted web UIs.
---

# Akka Web UI Accessibility and Responsive Layout

Use this skill for user-facing browser UI quality. It is required for non-trivial web apps, even with a minimal technical stack.

## Required reading

- `../../../docs/web-ui-quality-checklist.md`
- `../../../docs/web-ui-style-guide.md`
- the selected `app-description/55-ui/style-guide.md` or `specs/cross-cutting/*ui-style-guide*.md` when present
- existing HTML/CSS under `src/main/resources/static-resources/**`

## Accessibility rules

1. Use semantic HTML first: headings, main, nav, section, form, table/list/button as appropriate.
2. Maintain a logical heading order.
3. Every form control has a visible or programmatic label.
4. Buttons are real `<button>` elements unless navigation needs links.
5. All interactive behavior is keyboard-operable.
6. Focus states are visible.
7. Move focus intentionally after navigation, validation failure, modal open/close, or major updates.
8. Use `aria-*` only to supplement semantics, not replace them.
9. Do not rely on color alone to convey status.
10. Prefer `textContent` for dynamic text.
11. Apply the selected style guide's contrast, focus, and status-color constraints; if they are missing, add or request the style-selection question in `specs/pending-questions.md` before completing affected UI implementation work.

## Responsive rules

1. Design for narrow screens first, then enhance for wider layouts.
2. Avoid fixed-width containers that overflow common mobile widths.
3. Use CSS grid/flex with sensible wrapping.
4. Tables/lists with many columns need a narrow-screen strategy.
5. Touch targets should be comfortably clickable.
6. Loading/error/empty states must also work on narrow screens.

## CSS conventions

- keep CSS plain and local to the static resource app
- use meaningful class names tied to UI purpose
- define reusable spacing/status/focus styles from the selected style guide's CSS tokens
- avoid framework-specific utility systems in this pack wave

## Completion checklist

Before finishing, verify:
- keyboard-only navigation reaches all actions
- focus is visible and not trapped
- form errors are announced or associated with inputs
- content remains usable at narrow widths
- status/error information is not color-only
- light/dark mode, focus rings, and status colors satisfy the selected style guide and accessibility contrast expectations
- page has a clear main landmark and title/heading
