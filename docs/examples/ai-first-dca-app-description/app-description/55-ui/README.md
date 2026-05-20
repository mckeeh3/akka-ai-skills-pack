# 55 UI

Purpose: define the DCA vertical reference UI as a secure AI-first SaaS browser experience for supervision, decisions, governance, audit, outcomes, and foundation administration.

This is a compact DCA-specific consolidated UI contract, not the canonical generated-SaaS `55-ui/` split structure. The canonical file structure and preferred generated-app starting point remain in `../../../ai-first-saas-seed-app-description/`; new app descriptions should keep application meaning in `12-workstreams/` and browser realization in split `55-ui/` files.

Current files:
- `ui-surfaces.md` — consolidated DCA reference contract linking functional agents, structured surfaces, capabilities, route/deep-link notes, API needs, tests, realtime/state behavior, accessibility expectations, and Akka/web UI routing.
- `style-guide.md` — selected `atlas-ops-supervisory-console` visual style guide for generated AI-first browser UI.

Key routing notes:
- DCA extends the canonical secure SaaS seed; it does not replace mandatory sign-in, context selection, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, `/api/me`, or backend authorization surfaces.
- In a canonical split app-description, functional/context-area agent definitions, durable workstreams, structured-surface contracts, action-to-capability mappings, trace semantics, and surface tests belong under `12-workstreams/`; this DCA reference records those items in `ui-surfaces.md` only to keep the vertical example compact.
- In a canonical split app-description, `55-ui/` owns browser realization: shell/rail rendering, panel/composer behavior, routes/deep links, interactions/forms, frontend API contracts, state/realtime, accessibility/responsive behavior, and style guide.
- Supplies Autopilot is the first detailed DCA UI slice and must link to `CAP-03`, related tests, trace/outcome requirements, and selected API/realtime contracts before realization.
- If this example is later migrated, split this layer into `12-workstreams/functional-agents.md`, `12-workstreams/surfaces-index.md`, `12-workstreams/surface-contracts/**`, and `55-ui/workstream-shell.md`, `55-ui/functional-agent-rail.md`, `55-ui/workstream-panel-and-composer.md`, `55-ui/structured-surface-rendering.md`, `55-ui/routes-and-deep-links.md`, `55-ui/personas-and-journeys.md`, `55-ui/interactions-and-forms.md`, `55-ui/frontend-api-contracts.md`, `55-ui/states-and-realtime.md`, and `55-ui/accessibility-and-responsive.md`. Until then, `ui-surfaces.md` is the consolidated DCA contract. Routes/deep links remain realization details that open functional-agent workstreams and structured surfaces.
