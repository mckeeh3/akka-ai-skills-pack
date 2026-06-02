# Surface Contract: Mission Control Briefing

- surface-id: `mission-control-briefing`
- type/version: dashboard+timeline+digest/v1
- functional agents: Mission Control
- payload schema:
  - active goals, plan progress, agent activity, upcoming autonomous actions, pending approvals, exceptions, outcome deltas, policy/trust summary, trace links
- allowed actions:
  - create draft goal or launch approved plan → `ai-first-work-management`
  - review exception or pending decision → `governance-decisions-audit`
  - open audit/work trace → `governance-decisions-audit`
- UI style notes:
  - render as the canonical Mission Control / Briefing enterprise workstream surface: surface framing, KPI/attention band, agent activity timeline, pending decisions, exception cards, outcome deltas, policy/trust summary, and trace links use the `ai-first-workstream-enterprise` anatomy from `55-ui/style-guide.md`
  - blocked-by-policy, waiting-for-review, stale activity, autonomous progress, and trace-unavailable states must be visually distinct with text labels plus semantic color/icon treatment in every named theme
- states:
  - loading, no active work, blocked by policy, waiting for review, stale activity stream, trace unavailable
- auth/security:
  - surface only includes goals, decisions, and traces authorized for selected tenant/customer context.
- rendering tests:
  - attention queue ordering, stale stream banner, forbidden goal hidden/denied, trace links and outcome deltas render responsively.
