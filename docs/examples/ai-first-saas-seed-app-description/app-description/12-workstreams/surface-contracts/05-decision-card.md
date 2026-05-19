# Surface Contract: Decision Card

- surface-id: `decision-card`
- type/version: decision-card/v1
- functional agents: Mission Control, Governance/Policy, User Admin
- payload schema:
  - recommendation, required human authority, evidence, risk, confidence, impact, policy trigger, alternatives, originating workflow/agent, allowed actions, trace/outcome links
- allowed actions:
  - approve, reject, counterpropose, request changes, defer, escalate → `governance-decisions-audit`
  - convert correction to policy or behavior proposal where permitted → `governance-decisions-audit`, `managed-agent-foundation`
- states:
  - pending, acted, superseded, conflict/another reviewer acted, approval expired, forbidden action, evidence unavailable
- auth/security:
  - action buttons are hints; backend rechecks reviewer authority, policy gates, tenant/customer scope, and idempotency.
- rendering tests:
  - evidence/risk/impact shown, forbidden action disabled and rejected server-side, conflict state after duplicate approval, audit trace link present.
