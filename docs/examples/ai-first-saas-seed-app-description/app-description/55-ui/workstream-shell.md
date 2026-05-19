# Workstream Shell

The seed app UI is an agent workstream shell, not a page-first admin console.

Implementation reference: `../../../../../frontend/src/workstream/**` provides the reusable React/Vite shell, rail, composer, stream, surface, action, realtime, type, and fixture modules. `../../../../../frontend/src/main.tsx` shows shell integration and deep-link handling. Treat legacy screen/page files as drift or mechanics references only.

## Regions

- left rail functional agents:
  - Access/Profile
  - User Admin
  - Agent Admin
  - Mission Control
  - Governance/Policy
  - Audit/Trace
- main workstream panel:
  - durable timeline of user requests, agent responses, capability results, decisions, workflow progress, safe denials, and structured surfaces
- persistent composer:
  - natural-language request entry for the selected functional agent
  - attachments only when the selected capability permits them
  - clear scope indicator for tenant/customer and authority basis
- context and authority indicators:
  - selected tenant/customer, membership/role basis, active capability grants, pending approval state, trace link availability

## Shell rules

- The rail is populated from `/api/me` and backend capability grants.
- Hidden or disabled UI controls are never authorization controls.
- Selecting a functional agent opens that agent's workstream and default surface from `../12-workstreams/surfaces-index.md`.
- Route URLs may deep-link to a functional agent, surface, or record, but the app's primary decomposition remains functional agents and surfaces.
