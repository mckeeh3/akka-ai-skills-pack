# 55 UI

Purpose: define the DCA vertical reference UI as a secure AI-first SaaS browser experience for supervision, decisions, governance, audit, outcomes, and foundation administration.

Current files:
- `ui-surfaces.md` — authoritative surface catalog linking foundation/admin and DCA operational structured surfaces to capabilities, API needs, tests, realtime/state behavior, accessibility expectations, and Akka/web UI routing.
- `style-guide.md` — selected `atlas-ops-supervisory-console` visual style guide for generated AI-first browser UI.

Key routing notes:
- DCA extends the canonical secure SaaS seed; it does not replace mandatory sign-in, context selection, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, `/api/me`, or backend authorization surfaces.
- Supplies Autopilot is the first detailed DCA UI slice and must link to `CAP-03`, related tests, trace/outcome requirements, and selected API/realtime contracts before realization.
- Future downstream realization may split this layer into `workstream-shell.md`, `functional-agent-rail.md`, `structured-surface-rendering.md`, `routes-and-deep-links.md`, `personas-and-journeys.md`, `interactions-and-forms.md`, `frontend-api-contracts.md`, `states-and-realtime.md`, and `accessibility-and-responsive.md`; until then, `ui-surfaces.md` is the consolidated UI contract. Routes/deep links remain realization details that open functional-agent workstreams and structured surfaces.
