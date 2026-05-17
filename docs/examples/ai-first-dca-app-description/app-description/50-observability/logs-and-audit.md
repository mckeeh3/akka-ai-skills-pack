# Logs and Audit

## Purpose

Define generation-facing log and audit expectations for the DCA vertical reference. Logs support diagnosis; audit events are durable business/security facts and must not be reconstructed only from logs.

## Structured log context

Every protected request, workflow step, consumer reaction, timer action, integration call, and agent/tool invocation should include safe structured context when available:

- `correlationId`, causation id, request id, workflow id, and idempotency key.
- `tenantId`, `customerId`, selected `AuthContext`, actor account id or trusted service identity.
- agent id/model/tool id when an agent participates.
- capability id (`CAP-00` through `CAP-10`) and exposure surface (`ui`, `http`, `workflow`, `consumer`, `timer`, `agent-tool`, `integration`).
- outcome status, denial/error class, retry/no-op status, and redaction marker.

Logs must omit JWTs, invite raw tokens, provider secrets, supplier credentials, raw WorkOS private data, unrelated tenant/customer data, and full sensitive payloads unless a future approved retention policy explicitly allows a protected audit store.

## Foundation audit events (`CAP-00`)

Durable `AdminAuditEvent` / work-trace facts are required for:

- WorkOS sign-in/link/unlink and local account creation, activation, disable, reactivation, or removal.
- profile/settings update when security-relevant; high-privilege context switch.
- invitation create, resend, revoke/cancel, expire, accept/link, delivery attempt, and delivery failure.
- membership, role, permission/capability, support-access, tenant/customer setting, and access-review lifecycle changes.
- protected admin reads, billing-boundary changes, tenant setup/bootstrap, and subscription/entitlement status changes.
- authorization denials, cross-tenant/customer attempts, disabled-user attempts, last-admin risks, and support-access use.
- admin-assistant agent recommendations, tool/data access, decision-card creation, approvals, and rejected proposals.

## DCA work/decision/policy/tool/data audit events

DCA capabilities must emit durable audit/work traces for consequential behavior:

- `CAP-01` lifecycle gate evaluations, lifecycle transitions, blocked gates, exceptions, and approval outcomes.
- `CAP-02` telemetry ingestion trust decisions, stale/invalid telemetry, scheduled refresh results, and data-quality exceptions.
- `CAP-03` protected evidence reads, denials, policy checks, supply recommendation changes, auto-ship authorization, decision-card lifecycle, suppression, fulfillment command preparation/submission, integration results, retries/no-ops, more-evidence requests, support-access use, and agent/tool activity.
- `CAP-04` service recommendations, dispatch/remote-fix approvals, technician or service-integration handoffs, SLA exceptions, and rollback/compensation actions.
- `CAP-05` meter anomaly reviews, billing-impact approvals, billing handoffs, and correction outcomes.
- `CAP-06` onboarding and installation workflow milestones, customer-safe status exposure, installer handoffs, and validation failures.
- `CAP-07` offboarding approvals, deauthorization, retention/expiry actions, final-billing handoffs, shipment suppression, and archive/anonymization actions.
- `CAP-08` policy proposal, simulation/replay, approval, activation, rollback, and evaluator findings.
- `CAP-09` command-center supervision actions, delegation changes, decision reassignment, and realtime operational acknowledgements.
- `CAP-10` audit search/export/retention/redaction/digest access, including audit of audit access.

## Audit access and redaction

- Audit views enforce tenant, customer, role/capability, support-access, and retention/export boundaries.
- Support access never grants global raw log access; every support audit read records the support grant, reason, scope, and correlation id.
- Audit summaries may include evidence summaries and links, not raw secrets or unrelated customer payloads.
- Export, retention, redaction, deletion/anonymization, and legal-hold actions require explicit policy/approval details in audit facts.
