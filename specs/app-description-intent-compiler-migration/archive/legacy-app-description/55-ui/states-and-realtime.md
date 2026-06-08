# States and Realtime

## Common UI states

- loading:
  - dashboard, queues, policy lists, and audit search use skeleton cards/rows
  - command strip remains visible but action submit is disabled until required context loads
- empty:
  - explain why no data is present and what the user can do next
  - examples: no pending decisions, no active goals, no policy proposals, no trace results
- ready:
  - show current data timestamp or stream connection state where realtime data is present
- partial/stale data:
  - show a non-blocking stale indicator on mission control, decision queue, and goal detail
  - preserve visible data while reconnecting
- validation error:
  - show field-level and form-level messages
  - preserve user input and move focus to the first problem
- authorization denied:
  - explain that the user's role cannot access the action or surface
  - provide a safe return path
- API error:
  - identify the failed operation and provide retry/recovery action
- offline/retry:
  - show reconnecting state for streams and prevent duplicate unsafe actions
- submitting:
  - disable duplicate command submission when command semantics are not idempotent
  - keep secondary evidence/details links available when safe
- success:
  - name the completed action, such as `Decision approved`, `Goal launched`, or `Policy proposal submitted`

## Realtime surfaces

- mission control activity stream:
  - agent activity timeline
  - needs-your-attention queue count and items
  - upcoming autonomous action counts
- goal progress updates:
  - plan step changes
  - blocked/waiting-for-human states
  - linked decisions and approvals
- decision queue:
  - item added/updated/resolved
  - priority/risk/due-time changes
  - stale and reconnecting indicators
- governance surfaces:
  - policy proposal status changes
  - simulation/replay completion
- notification/digest/worker-task indicators:
  - unread count
  - material event summary
  - backend-owned worker task notifications for implemented AutonomousAgent verticals, derived from task/event/attention state and carrying governed capability ids, source refs, trace refs, surface targets, and recipient scoping rather than frontend-only badges

## Realtime behavior rules

- prefer SSE for server-to-browser activity, queue, and progress updates
- use WebSocket only for bidirectional collaboration or interactive sessions that cannot be modeled as request/response plus SSE
- stream event payloads should be idempotently mergeable by id and version/timestamp
- resolved queue items may remain briefly with a resolved state before disappearing to avoid disorienting the user
- reconnect keeps existing data visible and marks it stale until fresh data arrives

## Workstream realtime v1 contract

The current `/api/workstream/events` route is a **bounded SSE replay/refresh stream**, not a long-lived true-live notification channel. On connect or resume it returns the currently authorized tenant/customer workstream event refresh hints, then the browser must treat stream close, auth failure, network loss, or an unknown `Last-Event-ID` as a stale state and refresh backend-owned surfaces through normal API/shell requests.

- event source: durable `WorkstreamEventRepository` records plus backend-derived refresh hints; no frontend-only badge or fixture event is authoritative
- scope: selected membership/context, tenant, customer, functional-agent filter, and capability checks are enforced by the backend before events are serialized
- reconnect/resume: `Last-Event-ID` or `lastEventId` returns events after the known id when available; an unavailable id returns a `surface.stale` event instead of silently pretending the stream is complete
- refresh semantics: `projection.refresh.available` means reload/refresh the referenced backend-owned dashboard, attention, list, or workflow-status surface; it does not patch consequential state directly in the browser
- browser freshness: the UI may keep existing cards visible while the replay stream closes/reconnects, but must label them stale or refresh-backed rather than continuously live
- true continuous live SSE from component notification streams is a future enhancement and must preserve the same authorization, redaction, idempotency, trace, and stale-state rules

## Realtime security

- stream subscriptions are tenant and permission scoped
- stream payloads contain no unauthorized cross-tenant data
- backend authorization remains authoritative even if a UI stream shows an action affordance
- rejected or expired actions must return explicit authorization or stale-state errors

## Theme and style state

- user preference supports named theme selection with initial ids `aurora-light`, `cobalt-light`, `obsidian-dark`, `midnight-dark`, and `dark-night`
- theme changes are applied by documented CSS class or `data-theme` token switching
- My Account settings selects one available theme by name and applies the stored theme id to the UI
- style overrides are limited to named theme tokens, color tokens, font-family tokens, product name, logo/icon treatment, and safe brand accents
