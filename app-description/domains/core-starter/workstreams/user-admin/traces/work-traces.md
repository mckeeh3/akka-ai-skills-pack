# Traces: User Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

admin-audit-event, workstream-log-trace, agent-work-trace, policy-decision-trace for approval-gated changes.

Trace records include actor, selected `AuthContext`, tenant/customer ids, role/capability basis, correlation id, capability/tool id, policy decision, redaction decisions, denial/failure status, and linked surface/workstream item.
