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
- states:
  - loading, no active work, blocked by policy, waiting for review, stale activity stream, trace unavailable
- auth/security:
  - surface only includes goals, decisions, and traces authorized for selected tenant/customer context.
- rendering tests:
  - attention queue ordering, stale stream banner, forbidden goal hidden/denied, trace links and outcome deltas render responsively.
