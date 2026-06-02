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
  - **Send prompt** action plus a dashboard-icon **Show dashboard** button positioned immediately to its right
  - attachments only when the selected capability permits them
  - clear scope indicator for tenant/customer and authority basis
- context and authority indicators:
  - selected tenant/customer, membership/role basis, active capability grants, pending approval state, trace link availability

## Shell rules

- The rail is populated from `/api/me` and backend capability grants.
- Hidden or disabled UI controls are never authorization controls.
- Selecting a functional agent opens that agent's workstream and default surface from `../12-workstreams/surfaces-index.md` through the shell request pipeline.
- The persistent composer accepts shell requests such as `show users list`, `show surface user-admin-user-list`, and `show workstream user-admin`; resolved aliases are echoed as canonical prompt-like request surfaces such as `show surface user-admin-user-list`.
- The standard Show dashboard button is handled directly by the shell rather than by prompting the workstream agent; it appends a `Show dashboard` request surface followed by the selected workstream dashboard surface and preserves `shell_button` origin metadata.
- Buttons, links, cards, rows, My Account panels, rail entries, and deep links that open protected surfaces or workstreams use the same typed shell request pipeline as prompts, preserving origin metadata (`user_prompt`, `surface_action`, `my_account_panel`, `shell_button`, or `deep_link`).
- Workstream switching renders the prompt-like request item in the new target workstream only.
- Route URLs may deep-link to a functional agent, surface, or record, but the app's primary decomposition remains functional agents and surfaces and deep links still resolve through shell requests.
