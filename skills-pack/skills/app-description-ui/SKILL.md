---
name: app-description-ui
description: Maintain authoritative frontend/UI descriptions for description-first Akka apps, including agent workstream shell semantics, functional-agent surfaces, typed surface contracts, routes/deep links, interactions, frontend API contracts, accessibility, and responsive behavior.
---

# App Description UI

Use this skill for the mandatory browser frontend of generated full-stack AI-first SaaS apps, and for any description-first work that changes UI meaning.

This skill keeps UI requirements authoritative before realization so generated Akka apps are fully capable on both backend and frontend. The web UI is not optional for generated AI-first SaaS. The default UI/application architecture is the agent workstream shell: role-authorized functional-agent rail, continuous workstream panel, persistent composer, context/authority indicators, per-workstream role-specific dashboard/attention summaries, and structured surfaces connected as a human surface graph. Preserve supervision, decision, governance, digest, audit, autonomous task progress/result, and goal-to-execution surfaces; do not turn generated SaaS UI work into a primary page/screen hierarchy.

High-visibility guardrail: `55-ui/` must not create application meaning that is not already owned by `12-workstreams/` functional agents, workstreams, role-specific dashboard/attention contracts, workstream icon descriptors, human surface graph nodes/edges, structured surfaces, surface actions, autonomous task/result surfaces, internal workstream agent graph effects, and `10-capabilities/` governed contracts. UI changes that create or alter user-facing work areas, attention indicators, workstream icon assignments, surfaces, surface graph edges, browser-tool actions, or governed-tool exposure must first update or verify `12-workstreams/` and `10-capabilities/`; `55-ui/` may then add browser rendering, routes/deep links, frontend API, state/realtime, accessibility, responsive, and style details.

## Required reading

Read these first if present:
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` for attention/dashboard/surface-action/autonomous task notification/projection rules
- `../docs/capability-first-backend-architecture.md` for selected capability exposure surfaces, browser action authority, frontend API contracts, audit, and denial semantics
- `../docs/workstream-ui-reference-architecture.md` for the canonical generated-app workstream UI implementation reference under `frontend/src/workstream/**`
- `../docs/web-ui-frontend-decomposition.md`
- `../docs/web-ui-style-guide.md`
- `../docs/web-ui-quality-checklist.md`
- `../app-descriptions/SKILL.md`
- `../app-description-functional-agent-modeling/SKILL.md` for role-authorized functional-agent rail, workstream, surface, capability, prompt/skill/tool, trace, and test semantics
- `../app-description-surface-modeling/SKILL.md` for structured workstream surface contracts, typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, traces, and tests
- `../ai-first-saas-ui-surfaces/SKILL.md` for generated AI-first SaaS supervision, decision, governance, digest, audit, and goal-to-execution UI surfaces
- existing `app-description/55-ui/**`

## Use this skill when

- the user uses UI vocabulary such as screens, pages, dashboards, portals, admin consoles, or browser workflows; normalize generated SaaS requests into functional-agent workstreams, structured surfaces, routes/deep links, and frontend realization details
- the user describes command centers, mission control, attention dashboards, approval queues, decision cards, autonomous task progress/result surfaces, policy/governance centers, async digests, audit traces, or goal launch workbenches
- frontend behavior needs to be captured before code generation
- the app-description needs UI readiness for generation
- a change request affects forms, navigation, frontend validation, realtime browser updates, accessibility, or responsive behavior

## Authoritative UI layer

Prefer this structure for generated full-stack AI-first SaaS apps:

```text
app-description/55-ui/
  ui-index.md
  workstream-shell.md
  functional-agent-rail.md
  workstream-panel-and-composer.md
  structured-surface-rendering.md
  routes-and-deep-links.md
  personas-and-journeys.md
  ai-first-surfaces.md
  agent-catalog-and-detail.md
  prompt-and-skill-governance.md
  skill-manifests-and-tool-permissions.md
  edit-agent-proposals-and-traces.md
  interactions-and-forms.md
  frontend-api-contracts.md
  states-and-realtime.md
  accessibility-and-responsive.md
  style-guide.md
```

This is the canonical generated SaaS `55-ui/` file set and should remain consistent with `../docs/internal-app-description-architecture.md`, `app-description-bootstrap`, and the current AI-first SaaS core app-description template.

Create only files justified by the app, but do not omit the UI layer for generated AI-first SaaS. For `full core` scope, do not omit the managed-agent UI files: `agent-catalog-and-detail.md`, `prompt-and-skill-governance.md`, `skill-manifests-and-tool-permissions.md`, and `edit-agent-proposals-and-traces.md`. They may be omitted or explicitly marked deferred only when `00-system/app-manifest.md`, `readiness-status.md`, and `generation-policy.md` label a narrower scope such as `Module 1-only / not full core`. For a very small full-core app, one `ui-index.md`, `workstream-shell.md`, `functional-agent-rail.md`, `workstream-panel-and-composer.md`, `structured-surface-rendering.md`, `routes-and-deep-links.md`, those managed-agent UI files, and `style-guide.md` may be enough.

For retired/static/page-first UI boundaries, use `../docs/retired-content-boundaries.md`; new generated SaaS descriptions use `routes-and-deep-links.md`, not `screens-and-navigation.md`. The `55-ui` prefix keeps UI authoritative while preserving the existing `60-generation` layer for realization metadata. Keep application meaning in `12-workstreams/`: functional agents, internal agents, durable workstreams, surface contracts, action-to-capability mappings, traces, and tests. `55-ui/` owns browser realization and links back instead of redefining them.

## What to capture

Use `../docs/app-description-skill-output-contracts.md` plus `../docs/workstream-ui-reference-architecture.md` for the detailed UI contract. Capture only the affected browser-realization meaning: workstream shell, functional-agent rail, structured surfaces, routes/deep links, personas/journeys, managed-agent governance UI, forms/interactions, API contracts, realtime/stale states, accessibility/responsive behavior, and style-guide selection. Link every protected action to backend capability/tool authority, AuthContext/scope, denial behavior, traces, and tests.

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
