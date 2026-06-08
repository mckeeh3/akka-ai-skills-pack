# Traces: Agent Admin

## Uses

Global traces: `../../../../../global/traces/foundation-trace-patterns.md`.

## Required evidence

prompt-assembly-trace, skill-reference-load-trace, agent-work-trace, admin-audit-event, policy-decision-trace.

Trace records include actor, selected `AuthContext`, tenant/customer ids, role/capability basis, correlation id, capability/tool id, policy decision, redaction decisions, denial/failure status, and linked surface/workstream item.
