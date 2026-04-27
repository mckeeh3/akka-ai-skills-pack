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

## Change handling

For any UI change, update:
1. affected UI description files
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
