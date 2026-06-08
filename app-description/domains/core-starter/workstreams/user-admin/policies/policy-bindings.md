# Policies: User Admin

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Binding

Applies backend-authorization-default-deny, tenant-customer-isolation, approval-for-high-impact-change, provider-fail-closed, redaction-and-export-governance.

Policy evaluation is backend-enforced for protected reads, mutations, agent/tool calls, approval gates, redaction, export, model/provider boundaries, and frontend-visible payloads.
