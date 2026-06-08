# Traces: My Account

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

workstream-log-trace, admin-audit-event for protected self-service changes, agent-work-trace for agent turns, denial/provider traces.

Trace records include actor, selected `AuthContext`, tenant/customer ids, role/capability basis, correlation id, capability/tool id, policy decision, redaction decisions, denial/failure status, and linked surface/workstream item.
