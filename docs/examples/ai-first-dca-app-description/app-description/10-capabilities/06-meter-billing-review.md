# Capability: Meter Billing Review

This is a lightweight capability contract for future refinement. It records the governed boundary for meter-read evidence, billing anomaly review, billing approval, and export handoff without inventing pricing formulas, invoice schemas, or billing-system APIs.

## Capability definition

- capability-id: `meter-billing-review`
- capability number: `CAP-05`
- class: read/evidence, proposal, approval, workflow, command
- purpose: validate meter and contract evidence before billing-impacting data is approved or exported.
- business outcome: tenant billing users can rely on auditable meter evidence and agent-prepared anomaly explanations while humans retain authority over estimates, adjustments, waivers, and invoice-impacting exceptions.

## In-scope outcomes

- Validate meter freshness, monotonicity, lifecycle state, device assignment, customer mapping, and contract mapping.
- Prepare billing event drafts or batch-review evidence summaries.
- Identify missing reads, usage spikes, manual adjustments, final-read gaps, contract mismatches, and estimated/waived-read cases.
- Create decision cards for billing-impacting anomalies and approvals.
- Export or hand off approved billing data only through accepted backend integration boundaries.

## Out-of-scope outcomes

- Tenant/customer pricing logic, tax, invoicing, ERP schemas, payment collection, or revenue recognition.
- Automatic approval of anomalies, manual adjustments, final-read waivers, or contract exceptions without accepted policy or approval.
- Raw contract documents, billing secrets, or unrelated customer financial data in browser, agent, or support outputs.

## Authority and contract

- actors/callers: tenant billing analyst, billing owner, dealer owner, billing-review agent, meter telemetry consumer, billing workflow, billing integration caller, auditor.
- AuthContext/scope: authenticated account or trusted service identity, selected tenant/customer context, meter-read and billing-review grants, billing-impact approval permission, and tenant/customer/device filters.
- inputs: meter evidence reference, device assignment id, billing period or final-read context, contract summary/version, anomaly reason, proposed action, correlation id, and idempotency key.
- outputs: billing review summary, anomaly/approval card, approved export status, safe denial/error shape, redaction markers, and trace links.
- side effects: billing review workflow, anomaly decision card, approved billing handoff/export command, hold/suppress record, scheduled recheck, notification, and audit/work trace.
- idempotency: duplicate review/export for the same tenant/customer/device/period/evidence version returns the existing review, hold, approval, or export record.
- policy/approval: apply accepted `BILL-*` policy clauses; missing reads, spikes, adjustments, final-read waivers, or contract mismatch require review unless future policy grants a narrow safe boundary.
- exposure surfaces: billing review UI, HTTP APIs, review workflow, anomaly/evidence views, decision cards, scoped agent recommendations, backend-only billing integration handoff, timers, and consumers.

## Required future detail

- Accepted billing-period, anomaly, estimate, adjustment, and waiver policies.
- Billing integration and export contracts.
- Redaction rules for contract/billing fields by role.
- Concrete tests for missing reads, spikes, approvals, duplicate export, tenant isolation, audit, redaction, and integration failure.

## Linked layers

- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`, `../20-behavior/state-models/01-lifecycle-foundation.md`
- operating model: `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`
- auth/security: `../40-auth-security/data-protection.md`, `../40-auth-security/authorization-rules.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
