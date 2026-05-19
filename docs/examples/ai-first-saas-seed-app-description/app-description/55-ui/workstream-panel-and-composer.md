# Workstream Panel and Composer

## Workstream panel

- Renders chronological workstream entries for the selected functional agent.
- Embeds structured surfaces from `../12-workstreams/surfaces-index.md`.
- Shows capability result cards with status, denial reason, audit/work-trace link, and follow-up actions.
- Supports stale/reconnect indicators for live workstream and surface updates.
- Compresses routine activity into digest entries only when trace links remain available.

## Composer

- Uses selected functional-agent context, selected AuthContext, and visible capability hints to interpret requests.
- Must show the active tenant/customer, role/capability basis, and whether the request may create side effects.
- Submits intent to backend capabilities or agent invocations; it cannot perform client-only side effects.
- For denied requests, appends a safe denial entry with recovery guidance and trace/audit link when available.

## Tests

- rail selection swaps workstream context without leaking prior tenant/customer data.
- composer side-effect requests require backend confirmation and idempotency key.
- forbidden request creates safe denial rather than hidden failure.
- stale stream state preserves last known surfaces without implying completion.
