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
- notification/digest indicators:
  - unread count
  - material event summary

## Realtime behavior rules

- prefer SSE for server-to-browser activity, queue, and progress updates
- use WebSocket only for bidirectional collaboration or interactive sessions that cannot be modeled as request/response plus SSE
- stream event payloads should be idempotently mergeable by id and version/timestamp
- resolved queue items may remain briefly with a resolved state before disappearing to avoid disorienting the user
- reconnect keeps existing data visible and marks it stale until fresh data arrives

## Realtime security

- stream subscriptions are tenant and permission scoped
- stream payloads contain no unauthorized cross-tenant data
- backend authorization remains authoritative even if a UI stream shows an action affordance
- rejected or expired actions must return explicit authorization or stale-state errors

## Mode and style state

- user preference supports light, dark, and system mode
- mode changes are applied by documented CSS class or `data-mode` token switching
- style overrides are limited to color and font tokens
