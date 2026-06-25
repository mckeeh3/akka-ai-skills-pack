---
name: akka-web-ui-accessibility-responsive
description: Apply accessibility, semantic HTML, keyboard, focus, and responsive layout guidance to Akka-hosted full web apps in standard frontend projects.
---

# Akka Web UI Accessibility and Responsive Layout

Use this skill for user-facing browser UI quality. It is required for full web apps.

## AI-first accessibility role

For AI-first SaaS supervision, decision, governance, digest, and audit surfaces, accessibility protects human authority. Reviewers must be able to understand agent status, evidence, risk, policy triggers, approval requirements, exceptions, and trace links without relying on color, hover-only affordances, dense wide tables, or inaccessible live updates.

Ensure keyboard and screen-reader paths reach consequential actions such as approve, reject, defer, escalate, request more evidence, commit policy, roll back, inspect trace history, review a proposed chat tool plan, confirm or cancel it, and inspect partial-failure results when those actions are in scope. On narrow screens, preserve the primary review or supervision task before secondary diagnostics.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md`, `../docs/app-worker-tool-model.md`, and `../docs/app-description-to-code-compile-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the responsible worker, execution harness, actor adapter, governed tool, capability, AuthContext/scope, DTO, side-effect/idempotency policy, trace/result surface, selected implementation path, and tests are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Required reading

- `../docs/web-ui-quality-checklist.md`
- `../docs/web-ui-style-guide.md`
- the selected `app-description/55-ui/style-guide.md` or `specs/cross-cutting/*ui-style-guide*.md` when present, including available named themes and default theme id
- existing frontend source under `frontend/src/**`

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
10. For direct DOM updates, prefer `textContent` for dynamic text; in component frameworks, rely on normal safe text binding rather than unsafe HTML injection.
11. Apply the selected style guide's named-theme contrast, focus, status-color, and structured-surface form-control constraints; if the style or named-theme contract is missing, add or request the style-selection question in `specs/pending-questions.md` before completing affected UI implementation work.

## Responsive rules

1. Design for narrow screens first, then enhance for wider layouts.
2. Avoid fixed-width containers that overflow common mobile widths.
3. Use CSS grid/flex with sensible wrapping.
4. Tables/lists with many columns need a narrow-screen strategy.
5. Touch targets should be comfortably clickable.
6. Loading/error/empty states must also work on narrow screens.

## Styling conventions

- apply the selected style guide in the frontend source of record
- edit `frontend/src/**` and rebuild; do not hand-edit generated assets under `src/main/resources/static-resources/**`
- use meaningful class names or project-established component/style conventions tied to UI purpose
- define reusable spacing/status/focus styles from the selected style guide's CSS tokens and named-theme bundles
- ensure structured-surface inputs, selects, and textareas keep visible labels, focus rings, hit targets, disabled states, and validation states while avoiding browser-default/native-looking control rendering
- do not introduce a new UI styling framework by default; if the frontend project already uses one, follow its conventions instead of replacing it

## Completion checklist

Before finishing, verify:
- keyboard-only navigation reaches all actions
- focus is visible and not trapped
- form errors are announced or associated with inputs
- content remains usable at narrow widths
- status/error information is not color-only
- AI-first status, evidence, risk, policy-trigger, approval, exception, proposed chat tool-plan detail, confirmation requirement, partial-failure result, trace, and outcome information is available as text when applicable
- narrow layouts preserve the main supervision, decision, or governance task before secondary diagnostics
- each required named theme color-token bundle, focus ring, form control, and status color satisfies the selected style guide and accessibility contrast expectations; light/dark tone is contrast metadata, not a user-facing mode
- My Account named-theme selection previews immediately without breaking keyboard focus or accessible field announcements, and durable persistence still uses the governed save path
- page has a clear main landmark and title/heading
