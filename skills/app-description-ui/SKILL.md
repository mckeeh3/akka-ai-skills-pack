---
name: app-description-ui
description: Maintain authoritative frontend/UI descriptions for description-first Akka apps, including user journeys, screens, navigation, interactions, frontend API contracts, accessibility, and responsive behavior.
---

# App Description UI

Use this skill when description-first work affects the browser frontend of an app.

This skill keeps UI requirements authoritative before realization so generated Akka apps can be fully capable on both backend and frontend.

## Required reading

Read these first if present:
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/web-ui-frontend-decomposition.md`
- `../../docs/web-ui-style-guide.md`
- `../../docs/web-ui-quality-checklist.md`
- `../app-descriptions/SKILL.md`
- existing `app-description/55-ui/**`

## Use this skill when

- the user describes screens, pages, dashboards, portals, admin consoles, or browser workflows
- frontend behavior needs to be captured before code generation
- the app-description needs UI readiness for generation
- a change request affects forms, navigation, frontend validation, realtime browser updates, accessibility, or responsive behavior

## Authoritative UI layer

Prefer this structure when UI is in scope:

```text
app-description/55-ui/
  ui-index.md
  personas-and-journeys.md
  screens-and-navigation.md
  interactions-and-forms.md
  frontend-api-contracts.md
  states-and-realtime.md
  accessibility-and-responsive.md
  style-guide.md
```

Create only files justified by the app. For a very small app, one `ui-index.md` plus `screens-and-navigation.md` may be enough. The `55-ui` prefix keeps UI authoritative while preserving the existing `60-generation` layer for realization metadata.

## What to capture

### Personas and journeys
- user roles/personas
- user goals
- primary journeys
- role-specific access or UI differences

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

### Frontend API contracts
- browser API route and method
- request DTO
- success response DTO
- error response DTO
- auth/session behavior

### States and realtime
- loading/ready/empty/error/submitting/success/stale states
- SSE or WebSocket behavior
- reconnect and stale data UX

### Accessibility and responsive behavior
- semantic structure
- keyboard and focus behavior
- labels and errors
- narrow-screen layout expectations

### Style guide and theme selection
- selected theme id/name from `../../docs/web-ui-style-guide.md`, custom style reference, or `unselected`
- source image/reference and light/dark/system mode policy
- typography, spacing, radius, elevation, color, chart, status, and focus tokens
- layout shell/density and navigation treatment
- component rules for cards, buttons, forms, tables/lists, charts, and feedback states
- brand adaptations and forbidden copied demo content from reference images
- CSS variable/token expectations for generated `app.css`

If a browser UI is in scope and no style is selected, do **not** choose implicitly. Add or request a `category: ui` pending question in `specs/pending-questions.md` using `../../docs/web-ui-style-guide.md`; this blocks only web UI implementation/generation tasks.

## Change handling

For any UI change, update:
1. affected UI description files, including `style-guide.md` when theme, branding, density, tokens, or component styling change
2. behavior flows if user-visible behavior changes
3. tests if acceptance criteria change
4. auth/security if route visibility or roles change
5. readiness status if generation completeness changes

## Realization routing

When realization is requested, route UI work to:
- `akka-web-ui-apps`
- `akka-web-ui-lightweight-typescript`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-realtime` when live updates are required
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`
- `akka-http-endpoint-web-ui`
- HTTP endpoint companion skills as needed
