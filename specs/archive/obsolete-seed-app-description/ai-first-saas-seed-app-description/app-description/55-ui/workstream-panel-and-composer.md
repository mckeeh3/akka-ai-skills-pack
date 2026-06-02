# Workstream Panel and Composer

## Workstream panel

- Renders chronological workstream entries for the selected functional agent.
- Embeds structured surfaces from `../12-workstreams/surfaces-index.md`.
- Shows capability result cards with status, denial reason, audit/work-trace link, and follow-up actions.
- Supports stale/reconnect indicators for live workstream and surface updates.
- Compresses routine activity into digest entries only when trace links remain available.
- On every new request, whether submitted from the persistent composer, the standard Show dashboard shell button, or an existing structured surface action, appends a request surface that acknowledges the exact prompt/action text and scrolls that request surface to the top of the visible panel. Agent-selected response surfaces append below that request surface.
- The persistent composer renders a dashboard-icon **Show dashboard** button immediately to the right of **Send prompt**. Pressing it is handled directly by the shell, not by prompting the workstream agent, and appends a `Show dashboard` request surface followed by the selected workstream's dashboard surface.

## Composer

- Uses selected functional-agent context, selected AuthContext, and visible capability hints to interpret requests.
- Must show the active tenant/customer, role/capability basis, and whether the request may create side effects.
- Submits intent to backend capabilities or agent invocations; it cannot perform client-only side effects.
- For denied requests, appends a safe denial entry with recovery guidance and trace/audit link when available.

## Tests

- rail selection swaps workstream context without leaking prior tenant/customer data.
- composer side-effect requests require backend confirmation and idempotency key.
- Show dashboard button request uses shell origin metadata and renders the request surface followed by the selected workstream dashboard without invoking the workstream agent.
- forbidden request creates safe denial rather than hidden failure.
- stale stream state preserves last known surfaces without implying completion.
- request-scroll behavior verifies direct prompt requests and indirect surface-action requests land at the top with response surfaces below.
