# Acceptance Tests: Seed App

- tenant admin can invite a user, assign a role, and see the active membership after acceptance
- active user can switch only among tenants where membership is active
- supervisor can create a goal, request a draft plan, approve launch, and observe progress
- policy-triggered high-risk work creates a decision card before execution proceeds
- reviewer can approve or reject a decision card and workflow responds accordingly
- auditor can find trace entries for goal, plan, agent, tool, policy, and decision activity
- frontend shell shows authenticated layout, tenant switcher, navigation, loading/empty/error states, and validation feedback

## UI design acceptance checks

- frontend applies the selected `atlas-ops-supervisory-console` style guide without copying mockup product names, people, account names, logos, or metrics
- app shell renders persistent desktop navigation, active route state, notification/user region, and collapsed/mobile navigation behavior
- light, dark, and system mode are available through token-driven styling; core shell and mission-control surfaces preserve readable contrast and visible focus in both modes
- Mission Control / Briefing shows page title/subtitle, AI command strip, KPI summary band, agent execution timeline, needs-your-attention queue, trust controls, and upcoming autonomous actions
- AI command strip exposes suggested operational prompts and does not directly execute high-impact work without creating or routing to durable goals, decisions, approvals, or policy proposals
- decision cards show recommendation, evidence summary, risk, confidence or impact where available, policy trigger, allowed actions, and trace/detail links
- Goal Workbench supports objective, success criteria, constraints, tool/data permissions, approval gates, draft-plan request, and launch approval states
- Governance Center shows policy versions, proposals, simulation/replay status, approval/commit controls, and audit links
- Audit Trace Explorer supports filtering by goal, agent, decision, policy, tool, actor, and time; trace details show authorization basis and correlation id
- realtime-enabled surfaces show connected, stale/reconnecting, and recovered states without losing visible data
- duplicate or replayed realtime events do not duplicate queue rows or activity items
- responsive layout preserves the primary action for mission control, decision review, goal launch, and admin invitation flows on narrow screens
- keyboard-only users can navigate shell, command strip, forms, decision actions, drawers/modals, and trace filters; focus returns predictably after modal close or decision completion
- status and risk are communicated with labels/icons plus color, not color alone
