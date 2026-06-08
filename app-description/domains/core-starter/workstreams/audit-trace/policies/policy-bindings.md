# Policies: Audit/Trace

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Binding

Applies redaction-and-export-governance, tenant-customer-isolation, backend-authorization-default-deny, frontend-secret-boundary.

Policy evaluation is backend-enforced for protected reads, mutations, agent/tool calls, approval gates, redaction, export, model/provider boundaries, and frontend-visible payloads.
