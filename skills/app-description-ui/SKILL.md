---
name: app-description-ui
description: Maintain authoritative frontend/UI descriptions for description-first Akka apps, including user journeys, screens, navigation, interactions, frontend API contracts, accessibility, and responsive behavior.
---

# App Description UI

Use this skill for the mandatory browser frontend of generated full-stack AI-first SaaS apps, and for any description-first work that changes UI meaning.

This skill keeps UI requirements authoritative before realization so generated Akka apps are fully capable on both backend and frontend. The web UI is not optional for generated AI-first SaaS. Preserve supervision, decision, governance, digest, audit, and goal-to-execution surfaces before falling back to CRUD-oriented screens.

## Required reading

Read these first if present:
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/capability-first-backend-architecture.md` for selected capability exposure surfaces, browser action authority, frontend API contracts, audit, and denial semantics
- `../../docs/web-ui-frontend-decomposition.md`
- `../../docs/web-ui-style-guide.md`
- `../../docs/web-ui-quality-checklist.md`
- `../app-descriptions/SKILL.md`
- `../ai-first-saas-ui-surfaces/SKILL.md` for generated AI-first SaaS supervision, decision, governance, digest, audit, and goal-to-execution UI surfaces
- existing `app-description/55-ui/**`

## Use this skill when

- the user describes screens, pages, dashboards, portals, admin consoles, or browser workflows
- the user describes command centers, mission control, approval queues, decision cards, policy/governance centers, async digests, audit traces, or goal launch workbenches
- frontend behavior needs to be captured before code generation
- the app-description needs UI readiness for generation
- a change request affects forms, navigation, frontend validation, realtime browser updates, accessibility, or responsive behavior

## Authoritative UI layer

Prefer this structure for generated full-stack AI-first SaaS apps:

```text
app-description/55-ui/
  ui-index.md
  personas-and-journeys.md
  ai-first-surfaces.md
  screens-and-navigation.md
  interactions-and-forms.md
  frontend-api-contracts.md
  states-and-realtime.md
  accessibility-and-responsive.md
  style-guide.md
```

Create only files justified by the app, but do not omit the UI layer for generated AI-first SaaS. For a very small app, one `ui-index.md`, `ai-first-surfaces.md`, `screens-and-navigation.md`, and `style-guide.md` may be enough. The `55-ui` prefix keeps UI authoritative while preserving the existing `60-generation` layer for realization metadata.

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

### Screens and navigation
- screens/pages
- route or UI path
- primary and secondary actions
- empty/not-found states
- navigation entry/exit points

### Interactions and forms
- forms and fields
- client validation
- backend validation mapping
- submit/success/failure behavior
- duplicate-submit/idempotency expectations

### Capability-backed actions and frontend API contracts
- linked capability id/class for each protected browser action or query
- browser API route and method as an exposure surface, when selected
- request DTO and idempotency/correlation fields where applicable
- success response DTO and redaction rules
- error/denial response DTO
- required AuthContext, capability grant, and tenant/customer scope
- audit/trace expectation visible to users, supervisors, admins, or auditors when applicable

### States and realtime
- loading/ready/empty/error/submitting/success/stale states
- supervisor attention states: needs review, waiting on evidence, blocked by policy, escalated, autonomous progress, completed, overridden, stale, and trace unavailable
- SSE or WebSocket behavior
- reconnect and stale data UX

### Accessibility and responsive behavior
- semantic structure
- keyboard and focus behavior
- labels and errors
- narrow-screen layout expectations

### Style guide selection
- selected AI-first style id/name from `../../docs/web-ui-style-guide.md`, custom style reference, or `unselected`
- source image/reference and light/dark/system mode policy
- typography, spacing, radius, elevation, color, chart, status, and focus tokens
- layout shell/density and navigation treatment
- component rules for cards, buttons, forms, tables/lists, charts, and feedback states
- brand adaptations and forbidden copied demo content from reference images
- CSS variable/token expectations for frontend styling
- frontend implementation shape: standard frontend project
- UX handoff for each non-trivial screen: primary action, information hierarchy, UX copy, feedback/recovery states, responsive behavior, and keyboard/focus path
- static asset output and Akka hosting route expectations

If no UI style is selected for a generated AI-first SaaS app, do **not** choose implicitly. Add or request a `category: ui` pending question in `specs/pending-questions.md` using `../../docs/web-ui-style-guide.md`; this blocks web UI implementation/generation tasks until style is selected.

## Change handling

For any UI change, update:
1. affected UI description files, including `ai-first-surfaces.md` when delegated work surfaces change and `style-guide.md` when style system, branding, density, tokens, or component styling change
2. `10-capabilities/` via `app-description-capability-modeling` when a browser action/query adds, removes, or changes a capability exposure surface, AuthContext, schema, side effect, approval, audit, or idempotency semantics
3. behavior flows if user-visible behavior changes
4. tests if acceptance criteria, evaluation, realtime, loading/error, authorization, idempotency, or trace-link expectations change
5. auth/security if route visibility, roles, agent/tool permissions, approval authority, or trace access changes
6. observability if the UI needs work traces, decision traces, policy invocations, digests, audit search, or outcome evidence
7. readiness status if generation completeness changes

## Realization routing

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
