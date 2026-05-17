# Capability: Owner Command Center

This is a lightweight capability contract for future refinement. It records the governed boundary for dealer-owner and operations-supervisor visibility, supervision, decisions, and high-level command actions without inventing dashboard metrics or broad admin bypasses.

## Capability definition

- capability-id: `owner-command-center`
- capability number: `CAP-09`
- class: read/evidence, command, approval
- purpose: provide a scoped operational control surface for goals, work queues, decisions, exceptions, outcomes, and supervised automation.
- business outcome: accountable leaders can understand what the system and agents are doing, intervene in high-impact work, and review outcomes without receiving raw cross-tenant data or unauthorized controls.

## In-scope outcomes

- Present role-scoped summaries of lifecycle work, blocked gates, supply decisions, service risk, billing review, onboarding/offboarding, policy proposals, audit highlights, and outcome trends.
- Allow authorized supervision actions such as approve, reject, modify, defer, escalate, request evidence, pause automation, resume allowed work, or open linked evidence.
- Provide realtime or refreshable progress views for active workflows and decision queues where future UI/API specs accept the surface.
- Route users to capability-specific detail views rather than exposing raw internal state.

## Out-of-scope outcomes

- Cross-tenant analytics or SaaS Owner billing views except through `secure-tenant-user-foundation`/owner-boundary rules.
- New domain side effects that bypass the underlying capability contracts and approvals.
- Final metric formulas, realtime transport details, or executive-reporting layouts.

## Authority and contract

- actors/callers: dealer owner, operations supervisor, auditor, agent coordinator, decision/workflow callers, support operator only with scoped support access.
- AuthContext/scope: authenticated account, selected tenant/customer context, active role/capability grants for each visible domain, support-access grant where applicable, and tenant/customer filters on every summary/action.
- inputs: dashboard query, filters, scope, decision/supervision action, evidence request, workflow/action reference, reason, correlation id, and idempotency key for commands.
- outputs: redacted command-center summary, queue items, allowed actions, linked evidence references, safe denial shape, redaction markers, and trace links.
- side effects: only routed supervision/approval/evidence-request/pause/resume actions that call the underlying capability contract; no direct domain mutation bypass.
- idempotency: duplicate supervision commands for the same decision/workflow/action id and evidence version return the existing action result.
- policy/approval: command-center actions inherit the policy/approval requirements of the underlying capability; UI availability is never authorization.
- exposure surfaces: command-center UI, HTTP APIs, dashboard/evidence views, decision queue, supervision actions, realtime/progress surfaces, and scoped summary tools.

## Required future detail

- Dashboard/card schema and exact metric definitions.
- Realtime/SSE/WebSocket needs and stale/reconnect behavior.
- Mapping of every command-center action to an underlying capability id and permission.
- Concrete tests for redaction, forbidden widgets/actions, tenant isolation, delegated support access, idempotent supervision actions, audit, and UI/API behavior.

## Linked layers

- operating model: `../15-operating-model/agent-roles-and-authority.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`, `../15-operating-model/audit-trace-and-outcomes.md`
- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`
- auth/security: `../40-auth-security/authorization-rules.md`, `../40-auth-security/boundary-and-surface-rules.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
