# State Model: Goal, Plan, Decision Lifecycle

- goal:
  - states: draft, planned, active, paused, blocked, completed, canceled
- execution plan:
  - states: draft, awaiting-approval, approved, active, completed, superseded, canceled
- task:
  - states: pending, assigned, running, waiting-for-human, succeeded, failed, skipped
- decision card:
  - states: open, approved, rejected, changes-requested, escalated, resolved
- policy proposal:
  - states: draft, under-review, approved, rejected, activated
- audit/work trace:
  - append-only; never edited in place
