# Policies: Agent Admin

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Binding

Applies governed-agent-authority, approval-for-high-impact-change, provider-fail-closed, backend-authorization-default-deny, frontend-secret-boundary.

Policy evaluation is backend-enforced for protected reads, mutations, agent/tool calls, approval gates, redaction, export, model/provider boundaries, and frontend-visible payloads.
