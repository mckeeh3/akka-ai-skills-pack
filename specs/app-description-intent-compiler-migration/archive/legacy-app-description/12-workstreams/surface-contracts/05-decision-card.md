# Surface Contract: Decision Card

- surface-id: `decision-card`
- type/version: decision-card/v1
- functional agents: Governance/Policy, Governance/Policy, User Admin
- payload schema:
  - recommendation, required human authority, evidence, risk, confidence, impact, policy trigger, alternatives, originating workflow/agent, allowed actions, trace/outcome links
- allowed actions:
  - approve, reject, counterpropose, request changes, defer, escalate → `governance-decisions-audit`
  - convert correction to policy or behavior proposal where permitted → `governance-decisions-audit`, `managed-agent-foundation`
- UI style notes:
  - render as an `ai-first-workstream-enterprise` decision/exception card with recommendation, evidence, risk, confidence, impact, policy trigger, alternatives, and trace/outcome links visually prioritized over decorative chrome
  - use named-theme tokens, semantic status colors with labels/icons, clear primary/secondary actions, and accessible focus states from `55-ui/style-guide.md`
- states:
  - pending, acted, superseded, conflict/another reviewer acted, approval expired, forbidden action, evidence unavailable
- auth/security:
  - action buttons are hints; backend rechecks reviewer authority, policy gates, tenant/customer scope, and idempotency.
- rendering tests:
  - evidence/risk/impact shown, forbidden action disabled and rejected server-side, conflict state after duplicate approval, audit trace link present.
