# States and Realtime

- common UI states:
  - loading
  - empty
  - partial/stale data
  - validation error
  - authorization denied
  - offline/retry where applicable
- realtime surfaces:
  - mission control activity stream
  - goal progress updates
  - decision queue count and item changes
  - notification/digest indicators
- realtime security:
  - stream subscriptions are tenant and permission scoped
  - stream payloads contain no unauthorized cross-tenant data
