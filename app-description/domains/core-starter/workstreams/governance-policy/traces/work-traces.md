# Traces: Governance/Policy

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

policy-decision-trace, admin-audit-event, workstream-log-trace, agent-work-trace, and impact-analysis task/result events.

Trace records include actor, selected `AuthContext`, tenant/customer ids, role/capability basis, correlation id, idempotency key reference, capability/tool id, proposal/task id, command mode or impact-result disposition where applicable, policy decision, redaction decisions, denial/failure status, provider/runtime blocker status, and linked surface/workstream item.
