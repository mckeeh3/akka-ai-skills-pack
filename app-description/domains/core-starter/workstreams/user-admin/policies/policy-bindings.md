# Policies: User Admin

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Binding

Applies backend-authorization-default-deny, tenant-customer-isolation, approval-for-high-impact-change, provider-fail-closed, redaction-and-export-governance, last-admin protection, support-access expiry/purpose governance, idempotent-side-effect governance, and model/tool-boundary governance.

Policy evaluation is backend-enforced for protected reads, mutations, agent/tool calls, skill/reference loads, approval gates, redaction, export, model/provider boundaries, outbox delivery boundaries, and frontend-visible payloads.

## High-impact/risky actions

Decision-card or explicit approval routing is required for last-admin-affecting changes, role escalation, support-access expansion, identity relink/reset, access-review resolution, unsupported/bulk-like changes, low-confidence agent recommendations, and any policy-defined sensitive action.

## Forbidden policy outcomes

Prompt text, skill/reference content, compact manifests, rail visibility, hidden UI state, stale client payloads, or frontend filters cannot grant roles, capabilities, support access, trace access, or tenant/customer scope. Missing provider/model/outbox configuration fails closed and must not return fake normal success.
