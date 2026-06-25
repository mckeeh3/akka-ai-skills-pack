# Web UI quality checklist

Use this checklist before completing any non-trivial Akka-hosted browser UI.

For generated AI-first SaaS work in this source repository and downstream forks, compare end-to-end behavior against the runnable SaaS Foundation App repository root, then compare UI architecture against `./workstream-ui-reference-architecture.md` and the reusable React/Vite reference under root `frontend/src/workstream/**`. The harness install includes pack examples under `.agents/skills/examples/**`, but application code belongs in the target project and root frontend source is not exported into `.agents`. The User Admin vertical contract test (`frontend/src/workstream-user-admin-vertical.contract.test.mjs`) is the canonical foundation-admin UI pattern. Legacy `frontend/src/screens/**` and removed standalone static UI fixtures are not canonical generated-app structure.

## UX completeness

- [ ] For generated AI-first SaaS, primary user goals are represented by role-authorized functional agents, workstream shell regions, structured surfaces, and capability-backed actions.
- [ ] Each workstream shell region or structured surface communicates selected functional agent, organization/customer context, what matters, and what the user can do within the first five seconds.
- [ ] Each structured surface has one clear purpose or an explicitly read-only purpose.
- [ ] Default surface content is designed for the target SaaS user: every visible field, badge, chart, trace/evidence item, and action helps the user decide, act, recover, or understand a business outcome.
- [ ] Internal policy ids, capability/governed-tool ids, backend component names, provider/model details, prompt internals, raw event ids, and correlation/idempotency mechanics are hidden, translated, or role-gated instead of shown as ordinary user-facing copy.
- [ ] Audit/trace surfaces provide user-readable summaries first and expose raw audit/support/developer detail only through authorized drilldowns.
- [ ] Workstream dashboards are action routers, not reports: ready-state content is ordered top-to-bottom as things that need my attention, then things I can do.
- [ ] Aside from labels and minimal microcopy, visible dashboard content is limited to actionable/clickable indicators; passive FYI metrics, inert charts, and decorative card grids are absent or moved to report/detail/analytics surfaces.
- [ ] Dashboard cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons that represent attention or next work are clickable and keyboard-operable by default.
- [ ] Rectangular dashboard tiles/cards/counters with a work-object name and prominent count use the whole shape as the button, not just a nested action control.
- [ ] Zero-count dashboard tiles that represent valid queues/categories remain operable when they should open an empty queue, detail, explanation, setup, history, or validation surface.
- [ ] Dashboard interactions append a request-like workstream item and append/open the target detail, decision, progress, evidence/trace, result, updated dashboard, or typed `system_message` surface.
- [ ] Ready dashboards show authorized work this actor can do; forbidden targets are normally omitted rather than shown as disabled work objects.
- [ ] Non-actionable dashboard objects are visually distinct from actionable work objects and have a recorded `none` reason.
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

- [ ] Structured surfaces, when used, have explicit payload, action, event, auth, trace/audit, and rendering-test contracts per `./structured-surface-contracts.md`.
- [ ] Every surface action and dashboard work-object interaction maps to a governed backend capability, including read/query and surface-request actions; frontend visibility, disabled controls, prompt text, and route names are not treated as authorization.
- [ ] Composer-enabled workstreams have deterministic surface intent routing for high-confidence surface opens/prefills before model fallback; matched routes do not invoke the model, do not mutate state, and render editable prefill with user-review copy.
- [ ] Browser-facing DTOs are intentional and not accidental domain or implementation leaks.
- [ ] DTOs distinguish default user-visible fields, user-requested drilldowns, admin/support/auditor diagnostics, and internal-only metadata that should never be sent to ordinary browser views.
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
- [ ] Permission, policy, trace, and runtime messages use user-safe product language rather than raw internal identifiers.

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
- [ ] Canonical-style UI was compared against `skills-pack/docs/web-ui-component-catalog.md`, `skills-pack/examples/web-ui/ai-first-workstream-enterprise/component-catalog.html`, and the broader reference mockups or installed equivalents for anatomy, hierarchy, token roles, and component craft without copying demo content.
- [ ] The user-facing theme preference is named-theme based; available themes, default theme, tone metadata for contrast testing only, and persistence scope are recorded.
- [ ] Initial named themes are present or explicitly required for generated AI-first SaaS, with multiple named color-token bundles spanning both light-toned and dark-toned options for contrast coverage.
- [ ] My Account theme selection, when in scope, lets the user choose one available named theme, previews the selected named theme immediately on field change, and persists only through the governed save/confirm settings path; it does not present dark/light/system mode choices.
- [ ] Brand adaptations are applied without copying demo names, logos, users, or metrics from reference images.
- [ ] Status colors, chart colors, shadows, spacing, radius, and component density match the selected style guide closely enough to guide future regeneration; switching themes changes color tokens only, not layout, design language, surface anatomy, spacing, or behavior.
- [ ] Dashboard attention/KPI cards use the shared attention-card style across all dashboards, have readable semibold/bold labels, strong numeric hierarchy, enough vertical spacing between label, number, and badge/status text, appear above lower-priority dashboard details/lists, and clearly look like modern high-tech operable controls when they represent attention or available work; metrics that are not actionable are not rendered as dashboard KPI cards.

## Visual craft

- [ ] The UI has an intentional aesthetic direction recorded in the style guide rather than improvised generic SaaS styling.
- [ ] The style guide records a memorable motif and differentiation target, such as authority rails, trace-lit timelines, dense command panels, decision-card priority, or another product-appropriate craft detail.
- [ ] Typography, spacing, and density make the shell and structured surfaces feel production-grade, readable, and intentionally designed rather than generic/default; dashboard labels and metric text are not too small or too light for fast scanning.
- [ ] Color, borders, shadows, texture, and depth clarify hierarchy instead of decorating equally weighted cards.
- [ ] Human-needed work, policy-blocked work, autonomous progress, trace/history, and FYI activity have distinct hierarchy without relying on color alone.
- [ ] Decision cards, structured-surface forms, audit timelines, governance diffs, denials, empty states, and errors feel deliberately designed, not default or placeholder.
- [ ] Motion and transitions clarify state changes and respect reduced-motion preferences; animation is purposeful rather than scattered decoration.
- [ ] Cosmetic effects such as gradients, grain, glow, shadows, or patterns preserve contrast, focus visibility, and surface readability.
- [ ] Visual changes do not alter functional agents, workstreams, surface contracts, capability mappings, authorization, API behavior, tests, or readiness claims.
- [ ] The UI avoids generic AI/SaaS cliches and yesterday's passive CRUD dashboard patterns such as purple-gradient-on-white defaults, browser-native controls, timid evenly spread accents, inert metric tiles, and undifferentiated card grids.

## Accessibility

- [ ] The workstream shell has a clear title/heading and main landmark.
- [ ] Structured surfaces use logical heading order.
- [ ] Interactive elements, including dashboard work objects that represent attention or next work, are keyboard-operable.
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
- [ ] Unauthorized/expired-session and forbidden-action UX is covered for protected routes, including tenant/customer mismatch, disabled users, and role/scope denial, without leaking hidden workstreams, privileged facts, or raw policy/capability ids.
- [ ] `/api/me`, selected context, browser-safe capabilities, profile, settings, and context-switch behavior are represented for generated full-stack AI-first SaaS.

## Build and tests

- [ ] The frontend project checks and production build pass.
- [ ] Served CSS corresponds to the selected web UI style guide.
- [ ] Served JS/CSS assets correspond to the frontend source build output.
- [ ] Endpoint tests fetch packaged HTML/CSS/JS through `httpClient`.
- [ ] Tests assert route references for API/SSE/WebSocket dependencies without promoting page routes as the primary app model.
- [ ] Workstream contract tests cover shell, rail, composer, deterministic surface intent routing, structured surfaces, dashboard work-object interactions, request/result append behavior, capability actions, deep links, forbidden/disabled states, stale/realtime behavior, and the User Admin reference vertical where foundation admin UI is in scope.
- [ ] UX review notes or tests cover key loading, empty, error, validation, success states, structured-surface form control styling, and immediate named-theme preview when those surfaces are in scope.
- [ ] Optional source drift check has been run when practical: `skills-pack/tools/validate-web-ui-style-contract.py --warn-only .` from the target project root, with warnings reconciled or documented.
- [ ] Backend tests pass for changed endpoint/component behavior.
