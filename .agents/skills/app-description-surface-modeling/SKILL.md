---
name: app-description-surface-modeling
description: Model structured workstream surfaces in app descriptions, including typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, auth, traces, and tests.
---

# App Description Surface Modeling

Use this skill to update authoritative app-description surface definitions and workstream surface bindings for agent workstream apps. A surface is a typed, backend-backed user interaction unit, not a static mockup.

## Required reading

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/structured-surface-contracts.md`
- `../docs/workstream-contract.md`
- `../docs/workstream-attention-contracts.md`
- `../docs/workstream-ui-reference-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/web-ui-style-guide.md`
- `../docs/web-ui-component-catalog.md`
- `../docs/web-ui-quality-checklist.md`
- current `app-description/global/surfaces/**`, `global/tools/**`, `global/traces/**`, domain capability files, and workstream surface/access/tool/trace/test/realization bindings

## Surface contract fields

For each surface, define:

- whether this is a reusable global surface pattern or a workstream-specific surface binding
- stable id, type, version, owning workstream definition, exactly one owning functional agent, reusable functional agents/workstreams if any, and purpose
- actor roles/scopes and selected `AuthContext` requirements
- payload schema summary with frontend-safe fields only
- loading, empty, ready, submitting, success, validation-error, forbidden, conflict, stale/reconnect, partial-data, and failure states as applicable
- visible and hidden/denied actions
- stable `actionId`, browser action/tool id, governed backend capability/tool id, idempotency/correlation behavior, result surface, and approval gate for each consequential action
- target/result surface or typed `system_message`
- trace/audit/work-trace links and redaction rules
- accessibility/responsive expectations
- acceptance, regression, security, negative, idempotency, and observability tests

## Modeling rules

- Model dashboard attention and surface graph edges before conventional route/page details.
- Treat every workstream dashboard as an action router, not a report: ready dashboard content must be ordered top-to-bottom as **things that need my attention** followed by **things I can do**.
- Aside from section labels, control labels, and minimal explanatory microcopy, model only actionable/clickable dashboard indicators. Cards, rows, counters, badges, chart segments, task/progress panels, shortcuts, icons, and buttons must declare the surface/action they open, the governed capability/browser-tool if protected, and the request/result surfaces they append. Passive FYI metrics, inert charts, decorative status tiles, and content that does not answer “what can I do with this?” belong in report/detail/analytics surfaces or must become governed drilldowns.
- A dense labeled counter or metric tile is itself the interaction target, including valid `0` states that open an empty queue, explanation, setup, history, or validation surface. Use `none` with a recorded reason only for explicit non-actionable exceptions; ready dashboards should normally omit forbidden targets instead of displaying disabled work objects.
- When creating or revising a workstream dashboard, command center, queue, decision, audit, workflow/progress, form, table, detail, or other browser-rendered surface contract, include an explicit surface-description sufficiency review question for human app developers: "Is this surface definition sufficiently unambiguous that a developer or generator can implement and review the surface without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics?" If the answer is no, make another pass on the surface description before creating surface implementation tasks.
- Every browser-rendered surface must link to the selected web UI style guide, named-theme contract, and component-catalog anatomy before implementation. Surface contracts own purpose/authority/capability semantics; frontend realization owns visual/style mechanics and must not invent application meaning.
- If the app lacks a selected UI style/named-theme contract, route to `app-description-ui` or queue a blocking `category: ui` question before creating surface implementation tasks.
- Every consequential read/query/mutation/action maps to a backend capability and is authorized server-side.
- Denials, approval-required results, validation failures, stale/reconnect, no-op, and background-work states are explicit structured outcomes.
- Do not expose secrets, raw provider data, hidden roles, cross-tenant/customer identifiers, or privileged evidence in browser payloads.
- Do not describe fixture/static/mock surfaces as normal generated-app runtime.
- If a surface needs missing capability, governed-tool, action identity, authority, or result-surface semantics, ask or queue a blocking question instead of inventing stable implementation ids. Template examples may propose candidate ids only when clearly marked provisional.
- Process/template baselines may list deferred typed surfaces and first-slice fallbacks; app-level implementation cleanup must replace consequential deferred surfaces before claiming capability readiness.

## Output contract

Update or propose updates to the app-description with:

- new/changed surface contracts
- a surface-description sufficiency review result for each new or substantially changed browser-rendered surface, including whether another description pass is required before code generation
- affected capability/security/test/observability links
- traceability map or graph-link changes
- assumptions and open questions
- generation impact: localized UI/API change, backend capability change, broader workstream redesign, UI style/catalog realization prerequisite, or separate app-level surface implementation cleanup

If a surface requires data, authority, behavior, style/catalog binding, or tests not yet described, queue or ask the blocking question instead of inventing it. Do not treat the generated UI as the only review artifact; the app developer should also be able to review and refine the surface description itself, then use that refined description to revise or repair surface-related code.
