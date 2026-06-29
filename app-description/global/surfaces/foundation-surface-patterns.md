# Global Surfaces: Foundation surface patterns

Reusable surface patterns for the core starter workstreams. Browser routes are realization details; these structured surfaces carry current intent.

- `workstream-dashboard`: attention-aware starting surface for a functional agent.
- `markdown-response`: safe agent/user response surface with sanitized markdown and trace links.
- `system-message`: denial, provider-fail-closed, stale, conflict, validation, or completion notice.
- `list-search`: scoped table/search results with empty, loading, stale, forbidden, and failure states.
- `detail-edit`: detail card or edit form with validation, idempotency, and audit result state.
- `decision-card`: recommendation/approval surface with evidence, risk, confidence, impact, alternatives, and actions.
- `audit-timeline`: correlated trace events with redaction and investigation links.
- `governance-diff`: before/after behavior or policy change review surface.
- `notification-center`: personal or workstream notification list and acknowledgement actions.
- `outcome-panel`: metrics, decision outcomes, feedback, and replay/simulation evidence.
- `workflow-status`: long-running action status, retry, pause, approval, and failure state.

Every consequential action maps to a governed backend capability/tool and server-side authorization.

## Shared surface action graph convention

Every workstream surface action must record the following before compile/runtime work starts:

- `surfaceId`, action id, source surface state, and result/system-message surface.
- Actor adapter: normally `surface_action`; confirmed assistant execution uses the same governed tool through `human_chat_tool_plan`; AI execution requires separate `agent_tool_call` permission.
- Governed tool id, capability id, selected `AuthContext`, tenant/customer/account scope, role/capability grant, policy decision, and denial category.
- Input schema, validation, redaction/safe defaults, idempotency key source, correlation id, stale/freshness token when relevant, transaction boundary, no-op/replay behavior, and partial-failure behavior.
- Confirmation or approval requirement for consequential/risky actions, including `confirmedBy` and confirmation id for confirmed chat-plan execution.
- Result shape: updated origin surface, created/changed object detail, refreshed list/dashboard, workflow/status surface, decision/approval surface, denial/no-op/stale/conflict/provider-blocked `system-message`, or attention/projection update.
- Trace source and required trace facts using `../traces/foundation-trace-patterns.md`.
- Test and runtime-validation reference or explicit scenario gap.

Shared surface patterns do not implement role-specific dashboards or local actions. My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy workstream refresh tasks bind these conventions locally.
