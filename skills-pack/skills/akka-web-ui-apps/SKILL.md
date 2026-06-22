---
name: akka-web-ui-apps
description: Plan and implement agent workstream browser apps hosted by Akka HTTP endpoints, using standard frontend projects such as React/Vite/TypeScript. Use when generated full-stack AI-first SaaS needs a role-authorized functional-agent shell, continuous workstream, composer, and structured surfaces.
---

# Akka Web UI Apps

Use this skill when the product needs a real browser app, not only JSON APIs. For generated AI-first SaaS, the default UI is an authenticated agent workstream shell: role-authorized functional-agent rail, continuous stream panel, persistent composer, context/authority indicators, structured surfaces, backend-backed actions, realtime/stale state where needed, and accessible responsive rendering.

## Required reading

- `../docs/workstream-ui-reference-architecture.md` — canonical UI architecture
- `../docs/web-ui-style-guide.md` — visual/style contract
- `../docs/web-ui-pattern-selection.md` — route/build pattern summary
- `../docs/web-ui-api-contract-patterns.md` — typed API contracts
- `../docs/structured-surface-contracts.md` — surface payload/action/event contracts
- `../docs/workstream-surface-intent-routing.md` — deterministic composer-to-surface routing and prefill-only behavior
- `../docs/web-ui-quality-checklist.md` — review gate
- `../examples/web-ui/ai-first-workstream-enterprise/README.md` — canonical static visual reference mockups; use anatomy and token roles, not demo content
- `../docs/retired-content-boundaries.md` if old page/static-fixture structures appear

Load focused companions only as needed: `akka-web-ui-frontend-project`, `akka-web-ui-api-client`, `akka-web-ui-ux-design`, `akka-web-ui-accessibility-responsive`, `akka-web-ui-state-rendering`, `akka-web-ui-forms-validation`, `akka-web-ui-realtime`, `akka-web-ui-testing`, and `akka-http-endpoint-web-ui`.

## Canonical architecture

Plan the UI around workstreams and surfaces before conventional pages/routes:

1. authenticated shell and `/api/me` bootstrap
2. functional-agent rail with role/capability visibility and attention indicators
3. main workstream stream with items, progress/status, trace links, and history behavior
4. persistent composer with selected-agent context, deterministic surface intent routing before model fallback, allowed requests, disabled/forbidden state, and uploads only when authorized
5. structured surfaces: dashboard, table, form, detail card, decision card, diff, audit timeline, workflow status, evidence bundle, version card, outcome panel, and system message
6. surface graph edges and dashboard work-object interactions: source surface, clickable/keyboard-operable card/row/counter/badge/chart/task panel/shortcut/button where relevant, browser action/tool, governed backend capability/tool, target/result/system-message surface, request/result append behavior, auth basis, correlation/idempotency id, and trace/audit outcome
7. realtime/stale behavior through SSE/WebSocket/polling only when the product needs it

Routes and deep links support this model; they are not the security boundary and are not the primary decomposition.

## Implementation rules

- Put frontend source under `frontend/**`; build production assets to `src/main/resources/static-resources/**`.
- Protected data loads through authorized `/api/...` routes. Static assets may be public; data, actions, streams, and surface payloads are protected.
- Rendering code consumes frontend-safe DTOs/surface envelopes. It must not import server domain state or secrets.
- Every consequential surface action and dashboard work-object interaction maps to a backend capability and is rechecked server-side. Workstream dashboards are action routers, not reports: ready dashboards must show things that need the current user's attention first and things the current user can do second. Aside from labels and minimal microcopy, visible dashboard content must be actionable/clickable indicators; passive FYI metrics, inert charts, and decorative status grids belong in report/detail/analytics surfaces or must become governed drilldowns. Dashboard objects representing attention or next work are clickable and keyboard-operable by default; ready dashboards should show authorized work rather than disabled forbidden targets.
- Composer-entered operational requests should first pass through a backend-owned deterministic surface intent router. Matched routes append the user's request and open/prepopulate an authorized surface; they do not auto-submit forms or commands. The browser renders prefilled fields as editable advisory state with review/submit copy.
- Denials, validation failures, approval-required results, stale/reconnect states, and no-ops render as structured feedback, not silent UI failures.
- Do not use legacy `frontend/src/screens/**`, removed static UI fixtures, copied demo names, or pack examples as generated-app runtime structure. Static reference mockups under `../examples/web-ui/**` are visual guidance only, not runtime source or fixture completion evidence.

## Planning output

Before implementation, produce or update a compact UI plan with:

- users/personas, scopes, and primary goals
- selected functional agents and visible/hidden/denied rail behavior
- workstream shell regions and default selected workstream
- required structured surfaces with type/version, payload summary, states, actions, dashboard action-router hierarchy, dashboard work-object interaction targets, zero-count behavior, auth basis, traces, and responsive behavior
- surface intent routing plan for composer prompts: high-confidence patterns, target surfaces, prefill rendering, no-mutation copy, ambiguity/fallback behavior, and tests
- API and realtime contracts for each surface/action
- accessibility, keyboard/focus, loading/empty/error/forbidden/stale states
- frontend project/build/hosting checks
- tests: unit/component/contract, endpoint, route/assets, realtime if used, and at least one user-flow/vertical contract when product surface area is non-trivial

## Completion standard

A browser feature is complete only when the intended local Akka/API/UI path works at the stated scope: authentication context, backend authorization, protected API calls, structured surface rendering, action execution, trace/audit links, failure/denial states, accessibility basics, responsive behavior, and tests. Fixture-only, frontend-only, static mockup, or route-existence-only evidence is not enough.
