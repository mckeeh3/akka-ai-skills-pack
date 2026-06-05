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

This is the canonical generated SaaS `55-ui/` file set and should remain consistent with `docs/internal-app-description-architecture.md`, `app-description-bootstrap`, and the current AI-first SaaS starter core template.

Create only files justified by the app, but do not omit the UI layer for generated AI-first SaaS. For `full core` scope, do not omit the managed-agent UI files: `agent-catalog-and-detail.md`, `prompt-and-skill-governance.md`, `skill-manifests-and-tool-permissions.md`, and `edit-agent-proposals-and-traces.md`. They may be omitted or explicitly marked deferred only when `00-system/app-manifest.md`, `readiness-status.md`, and `generation-policy.md` label a narrower scope such as `Module 1-only / not full core`. For a very small full-core app, one `ui-index.md`, `workstream-shell.md`, `functional-agent-rail.md`, `workstream-panel-and-composer.md`, `structured-surface-rendering.md`, `routes-and-deep-links.md`, those managed-agent UI files, and `style-guide.md` may be enough.

Use `screens-and-navigation.md` only as a legacy compatibility note when maintaining older app descriptions; new generated SaaS descriptions should use `routes-and-deep-links.md`. The `55-ui` prefix keeps UI authoritative while preserving the existing `60-generation` layer for realization metadata. Keep application meaning in `12-workstreams/`: functional agents, internal agents, durable workstreams, workstream icon descriptors (`WorkstreamIconDescriptor` semantics), surface contracts, reusable placement, action-to-capability mappings, traces, and tests. `55-ui/` owns browser realization and links back to `12-workstreams/`, `10-capabilities/`, security, observability, and test artifacts instead of redefining them.

## What to capture

### Personas and journeys
- user roles/personas
- user goals
- primary journeys
- role-specific access or UI differences
- temporal modes for AI-first work: delegation, supervision, review/approval, exception handling, teaching/governance, catch-up, and audit

### AI-first surfaces
- Goal-to-Execution Workbench: intent capture, plan review, agent assignments, tool/data permissions, approval gates, simulation, launch/cancel controls
- Command Center / Mission Control: active objectives, agent activity, progress, risks, exceptions, approval queues, material events, and trace links
- Decision Card / Deviation Review: recommendation, evidence, risk, confidence, impact, policy trigger, alternatives, reviewer actions, and learning options
- Policy / Governance / Learning Center: policy versions, clause IDs, examples, proposals, diffs, simulations/replays, approvals, commits, and rollback controls
- Async Digest / Executive Briefing: routine-activity compression, material events, pending decisions, prior outcomes, and links to traces
- Audit / Work Trace: chronological agent steps, tool calls, data access, policy invocations, approvals, actions, rollbacks, and outcome links

### Managed-agent foundation UI
- Agent catalog: tenant-scoped list/search/filter of active, disabled, draft, and archived `AgentDefinition` records with owner/steward, authority level, model reference, prompt reference, skill manifest, tool permission boundary, status, and trace summary.
- Agent detail: lifecycle controls, prompt/skill/manifest/tool-boundary references, effective capability grants, disabled-agent denial state, recent `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` links.
- Prompt governance: `PromptDocument`/`PromptVersion` list, editor/proposal intake, diff/history, review/approval, activation, rollback, checksum/status, test console, and prompt assembly preview.
- Skill governance: `SkillDocument`/`SkillVersion` list, compact manifest hints, full skill text review, diff/history, approval, activation/deprecation, and `readSkill(skillId)` test console.
- Skill manifests and tool permissions: `AgentSkillManifest` assignment UI, unassigned skill denial visibility, `ToolPermissionBoundary` editor/review, scoped tool/data permissions, approval-required authority expansion, and policy citations.
- Editing agent proposals: `AgentBehaviorEditorAgent` or equivalent proposed diff review, rationale, risk/impact notes, affected artifacts, suggested tests/replays, approval actions, denial reasons, and activation/rollback results.
- Trace surfaces: prompt assembly, allowed/denied skill loads, tool/data access, policy decisions, approval outcomes, and consequential agent work traces with tenant/customer scope, correlation ids, redaction, and auditor views.

### Routes and deep links
- implementation routes, UI paths, direct surface URLs, and auth-transition/public-static paths
- mapping from each route/deep link to the selected functional agent, workstream item, or structured surface it opens
- route/deep-link entry through the same shell request pipeline as prompts and surface actions: normalize to `show surface <surface-id>` or `show workstream <workstream-id>`, append the prompt-like request item in the target workstream only, preserve `origin: "deep_link"`, and render denial as a typed `system_message` surface
- navigation entry/exit points as shell behavior, not as the primary application decomposition
- empty/not-found/forbidden route states and recovery actions
- reminder: primary/secondary actions, loading/error states, and authorization semantics belong first in structured surface contracts and governed capability contracts

### Shell navigation, attention summaries, and workstream icons
- render workstream icon descriptors from `12-workstreams/` using stable icon ids, visual hints, theme accent color tokens, tooltips, accessible labels, and optional approved asset references; realization must use an approved SVG/icon-library registry or semantic SVG fallback derived from the workstream name/responsibility, never letter initials as the normal icon
- show My Account only through the lower-left signed-in user tile/email; do not duplicate it as a top-rail workstream button
- render left-rail counts and My Account aggregate attention from backend-governed projections such as `WorkstreamAttentionSummary`; define hidden/unavailable/zero states and highest-severity behavior in UI, but do not compute authority or attention from frontend-only notification state
- treat buttons, links, icons, cards, rows, and status panels that open protected surfaces or workstreams as governed surface-request actions such as `show_surface` or `open_workstream`, backed by capabilities and denial/system-message behavior
- support prompt-entered shell requests such as `show users list`, `show surface users-list`, and `show workstream user-admin`; transform resolved aliases into canonical prompt feedback such as `show surface users-list` so users learn precise commands
- allow authorized cross-workstream surface requests as a power-user path while defaulting ambiguous surface requests to the selected workstream; unauthorized or unresolved targets return safe `system_message` surfaces without leaking hidden workstreams
- render surface-action, My Account panel, rail, and deep-link navigation as compact prompt-like request items with honest origin metadata, and place workstream-switch request items in the new target workstream only
- keep browser interaction details, tooltip/focus behavior, responsive rail collapse, and visual treatment in `55-ui/`; do not assign domain icon meaning here

### Interactions and forms
- forms and fields
- client validation
- backend validation mapping
- submit/success/failure behavior
- duplicate-submit/idempotency expectations

### Surface graph, browser-tool actions, and frontend API contracts
- dashboard root surfaces, surface graph nodes, and surface-action edges that the browser must render, including prompt-entered, deep-link, row/card/button, denial/recovery, and cross-workstream surface requests
- linked capability id/class and governed-tool id for each protected browser action or query
- browser-tool exposure name when a governed-tool is invoked from a surface action
- browser API route and method as an exposure surface, when selected
- request DTO and idempotency/correlation fields where applicable
- success response DTO and redaction rules
- error/denial response DTO
- required AuthContext, capability grant, and tenant/customer scope
- denial/system-message surface behavior for unauthorized, stale, unresolved, or cross-workstream actions
- governed agent artifact ids when the action reads or changes `AgentDefinition`, `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, `ToolPermissionBoundary`, editing agent proposal, `PromptAssemblyTrace`, `SkillLoadTrace`, or `AgentWorkTrace`
- audit/trace expectation visible to users, supervisors, admins, or auditors when applicable

### States and realtime
- loading/ready/empty/error/submitting/success/stale states
- supervisor attention states: needs review, waiting on evidence, blocked by policy, escalated, autonomous progress, completed, overridden, stale, and trace unavailable
- autonomous task notification states: pending, assigned, in progress, dependency stuck, result rejected, completed with recommendation, failed, cancelled, and escalation-needed
- SSE or WebSocket behavior
- reconnect and stale data UX

### Accessibility and responsive behavior
- semantic structure
- keyboard and focus behavior
- labels and errors
- narrow-screen layout expectations

### Style guide selection
- selected AI-first style id/name from `../docs/web-ui-style-guide.md`, custom style reference, or `unselected`
- visual direction: aesthetic point of view, tone, memorable motif, and forbidden generic patterns
- theme model: named-theme selection; available theme ids/names; default theme id; user preference scope and persistence expectations
- My Account theme behavior when in scope: users choose one available named theme and the UI applies it at the documented scope
- typography, spacing, radius, elevation, color, chart, status, and focus tokens for every available named theme
- layout shell/density and navigation treatment
- component rules for cards, buttons, forms, tables/lists, charts, and feedback states
- motion, texture, background, and elevation rules with reduced-motion and contrast constraints
- brand adaptations and forbidden copied demo content from reference images
- CSS variable/token expectations for frontend styling; TypeScript toggles only documented theme ids/classes/attributes
- frontend implementation shape: standard frontend project
- UX handoff for each non-trivial structured surface or route/deep-link target: primary action, information hierarchy, UX copy, feedback/recovery states, responsive behavior, and keyboard/focus path
- static asset output and Akka hosting route expectations

If no UI style is selected for a generated AI-first SaaS app, do **not** choose implicitly. Add or request a `category: ui` pending question in `specs/pending-questions.md` using `../docs/web-ui-style-guide.md`; this blocks web UI implementation/generation tasks until style is selected.

Cosmetic style work may improve visual quality only within already-authoritative UI meaning. Do not use style-guide updates to add, remove, rename, or reinterpret functional agents, workstreams, workstream icon semantics, structured surfaces, capability-backed actions, authorization, API contracts, tests, or readiness semantics.

## Change handling

For any UI change, update:
1. affected UI description files, including `workstream-shell.md`, `functional-agent-rail.md`, `workstream-panel-and-composer.md`, `structured-surface-rendering.md`, `ai-first-surfaces.md` when delegated work surfaces change, the managed-agent UI files when full-core agent behavior governance changes, and `style-guide.md` when style system, branding, density, tokens, icon rendering, or component styling change
2. `12-workstreams/functional-agents.md` via `app-description-functional-agent-modeling` when a UI change adds, removes, or changes a user-facing functional agent, workstream icon assignment/meaning, prompt intent, skills, tools, surfaces, callable capabilities, authority, traces, or tests
3. `12-workstreams/surfaces-index.md` and `surface-contracts/**` via `app-description-surface-modeling` when a UI change adds, removes, or changes structured surfaces, role-specific dashboard/attention surfaces, human surface graph nodes/edges, autonomous task result/progress surfaces, payload schemas, reusable placement, allowed actions, states, notification/realtime behavior, trace links, or rendering tests
4. `10-capabilities/` via `app-description-capability-modeling` when a browser action/query adds, removes, or changes a capability exposure surface, governed-tool id, browser-tool mapping, AuthContext, schema, side effect, approval, audit, idempotency, notification/projection output, or autonomous task lifecycle semantics
5. behavior flows if user-visible behavior changes
6. tests if acceptance criteria, evaluation, realtime, loading/error, authorization, idempotency, or trace-link expectations change
7. auth/security if route visibility, shell request resolution, cross-workstream surface discovery, roles, agent/tool permissions, prompt/skill/manifest/tool-boundary authority, approval authority, or trace access changes
8. observability if the UI needs work traces, decision traces, policy invocations, digests, audit search, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, editing agent proposal traces, or outcome evidence
9. readiness status if generation completeness changes

## Realization routing

When realization is requested, preserve the description-level functional-agent and structured-surface contracts, then use the source-repository reference implementation as a concrete mechanics reference:
- reusable workstream components and types: `frontend/src/workstream/**`
- API/realtime client seams to bind to real generated backend endpoints: `frontend/src/api/WorkstreamApiClient.ts` and `frontend/src/api/WorkstreamRealtimeClient.ts`
- integrated shell/deep-link example: `frontend/src/main.tsx`
- User Admin dashboard → list/search → detail/edit contract references: `frontend/src/workstream/fixtures/**` plus `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

Fixtures are contract/test references only. Generated user-facing SaaS UI must connect to real backend API/realtime endpoints and governed capabilities with authorization, audit/trace, failure, and local smoke/manual validation paths. Fixture-backed, frontend-only, or simulated data paths must not satisfy runtime completion for a named feature.

Do not realize new generated SaaS UI as a primary `screens/**` or page-route tree. If older app descriptions contain `screens-and-navigation.md`, treat it as legacy compatibility and migrate meaning into `workstream-shell.md`, `functional-agent-rail.md`, `structured-surface-rendering.md`, and `routes-and-deep-links.md`.

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
