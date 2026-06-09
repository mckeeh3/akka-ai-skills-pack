---
name: app-description-ui
description: Maintain authoritative frontend/UI realization and workstream surface bindings in the app-description current-intent graph for Akka apps, including agent workstream shell semantics, functional-agent surfaces, typed surface contracts, routes/deep links, interactions, frontend API contracts, accessibility, and responsive behavior.
---

# App Description UI

Use this skill for the mandatory browser frontend of generated full-stack AI-first SaaS apps, and for any current-intent work that changes UI meaning.

This skill keeps UI requirements authoritative before realization so generated Akka apps are fully capable on both backend and frontend. The web UI is not optional for generated AI-first SaaS. The default UI/application architecture is the agent workstream shell: role-authorized functional-agent rail, continuous workstream panel, persistent composer, context/authority indicators, per-workstream role-specific dashboard/attention summaries, and structured surfaces connected as a human surface graph. Preserve supervision, decision, governance, digest, audit, autonomous task progress/result, and goal-to-execution surfaces; do not turn generated SaaS UI work into a primary page/screen hierarchy.

High-visibility guardrail: UI realization files must not create application meaning that is not already owned by `domains/<domain>/workstreams/<workstream>/**` functional agents, workstreams, role-specific dashboard/attention contracts, workstream icon descriptors, human surface graph nodes/edges, structured surfaces, surface actions, autonomous task/result surfaces, internal workstream agent graph effects, and `domains/<domain>/capabilities/**` governed contracts. UI changes that create or alter user-facing work areas, attention indicators, workstream icon assignments, surfaces, surface graph edges, browser-tool actions, or governed-tool exposure must first update or verify the workstream and capability nodes; UI realization artifacts may then add browser rendering, routes/deep links, frontend API, state/realtime, accessibility, responsive, and style details.

## Required reading

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for attention/dashboard/surface-action/autonomous task notification/projection rules
- `../docs/capability-first-backend-architecture.md` for selected capability exposure surfaces, browser action authority, frontend API contracts, audit, and denial semantics
- `../docs/workstream-contract.md` for workstream definition/instance/view terminology and readiness labels
- `../docs/workstream-attention-contracts.md` for backend-owned attention, My Account aggregation, and left-rail counts
- `../docs/workstream-ui-reference-architecture.md` for the canonical generated-app workstream UI implementation reference under `frontend/src/workstream/**`
- `../docs/web-ui-frontend-decomposition.md`
- `../docs/web-ui-style-guide.md`
- `../docs/web-ui-component-catalog.md`
- `../docs/web-ui-quality-checklist.md`
- `../app-descriptions/SKILL.md`
- `../app-description-functional-agent-modeling/SKILL.md` for role-authorized functional-agent rail, workstream, surface, capability, prompt/skill/tool, trace, and test semantics
- `../app-description-surface-modeling/SKILL.md` for structured workstream surface contracts, typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, traces, and tests
- `../ai-first-saas-ui-surfaces/SKILL.md` for generated AI-first SaaS supervision, decision, governance, digest, audit, and goal-to-execution UI surfaces
- existing `app-description/domains/<domain>/workstreams/<workstream>/surfaces/**`
- existing `app-description/domains/<domain>/workstreams/<workstream>/realization/frontend-routes.md`
- existing `app-description/global/surfaces/**`

## Use this skill when

- the user uses UI vocabulary such as screens, pages, dashboards, portals, admin consoles, or browser workflows; normalize generated SaaS requests into functional-agent workstreams, structured surfaces, routes/deep links, and frontend realization details
- the user describes command centers, mission control, attention dashboards, approval queues, decision cards, autonomous task progress/result surfaces, policy/governance centers, async digests, audit traces, or goal launch workbenches
- frontend behavior needs to be captured before code generation
- the app-description needs UI readiness for generation
- a change request affects forms, navigation, frontend validation, realtime browser updates, accessibility, or responsive behavior

## Authoritative UI graph targets

Prefer this structure for generated full-stack AI-first SaaS apps:

```text
app-description/global/surfaces/<surface-pattern>.md
app-description/domains/<domain>/workstreams/<workstream>/surfaces/<surface-binding>.md
app-description/domains/<domain>/workstreams/<workstream>/realization/frontend-routes.md
app-description/domains/<domain>/workstreams/<workstream>/realization/api-contracts.md
```

Global surface artifacts define reusable surface patterns. Workstream surface bindings define where the surface appears, which functional agent owns or reuses it, which capability/tool actions it exposes, how denials/stale states render, and which tests/traces prove it. Frontend realization files own browser rendering, route/deep-link, frontend API, realtime, accessibility, responsive, and style details for the workstream.

Create only files justified by the app, but do not omit UI realization for generated AI-first SaaS. For SaaS Foundation App scope, managed-agent UI surfaces such as agent catalog/detail, prompt and skill governance, skill manifests and tool permissions, and edit-agent proposals/traces belong to the foundation domain unless a task explicitly narrows or removes that area.

For retired/static/page-first UI boundaries, use `../docs/retired-content-boundaries.md`; new generated SaaS descriptions use frontend route/deep-link realization under the owning workstream, not `screens-and-navigation.md`. Keep application meaning in workstream access/behavior/surface/agent/tool/policy/trace/test nodes; frontend realization owns browser mechanics and links back instead of redefining them.

## What to capture

Use `../docs/app-description-skill-output-contracts.md`, `../docs/workstream-contract.md`, `../docs/workstream-attention-contracts.md`, and `../docs/workstream-ui-reference-architecture.md` for the detailed UI contract. Capture only the affected browser-realization meaning: workstream shell, functional-agent rail, structured surfaces, routes/deep links, personas/journeys, managed-agent governance UI, forms/interactions, API contracts, realtime/stale states, accessibility/responsive behavior, and style-guide selection. Link every protected action to backend capability/tool authority, AuthContext/scope, denial behavior, traces, and tests.

For each new or substantially changed browser-rendered surface, verify the app-description surface contract has answered the surface-description sufficiency question from `app-description-surface-modeling`: can a developer or generator implement and review the surface without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics? If not, route back to surface description refinement instead of allowing frontend realization to invent missing application meaning.

## Change handling

Update only affected UI description files and linked workstream/surface/capability/behavior/test/security/observability/readiness artifacts. Do not use UI edits to redefine domain meaning, authorization, tool authority, or generated-app completion semantics.

## Realization routing

When realization is requested, preserve the description-level functional-agent and structured-surface contracts, then use the source-repository reference implementation as a concrete mechanics reference:
- reusable workstream components and types: `frontend/src/workstream/**`
- API/realtime client seams to bind to real generated backend endpoints: `frontend/src/api/WorkstreamApiClient.ts` and `frontend/src/api/WorkstreamRealtimeClient.ts`
- integrated shell/deep-link example: `frontend/src/main.tsx`
- User Admin dashboard → list/search → detail/edit contract references: `frontend/src/workstream-user-admin-vertical.contract.test.mjs` plus shared test fixtures under `frontend/src/__tests__/fixtures/**` when needed

Fixtures are contract/test references only. Generated user-facing SaaS UI must connect to real backend API/realtime endpoints and governed capabilities with authorization, audit/trace, failure, and local smoke/manual validation paths. Fixture-backed, frontend-only, or simulated data paths must not satisfy runtime completion for a named feature.

Do not realize new generated SaaS UI as a primary `screens/**` or page-route tree; see `../docs/retired-content-boundaries.md` for migration boundaries.

When realization is requested, route UI work to:
- `ai-first-saas-ui-surfaces` first when the UI is for delegation, supervision, decisions, governance, digests, audit, or outcomes
- `akka-web-ui-apps`
- `akka-web-ui-frontend-project` for standard frontend projects such as React/Vite
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime` when live updates are required
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`
- `akka-http-endpoint-web-ui`
- HTTP endpoint companion skills as needed
