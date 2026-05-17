# Capability: Telemetry Intelligence

This is a lightweight capability contract for future refinement. It records the governed boundary for DCA telemetry ingestion, evidence creation, health interpretation, and safe reuse by downstream capabilities without inventing vendor payloads or thresholds.

## Capability definition

- capability-id: `telemetry-intelligence`
- capability number: `CAP-02`
- class: reactive, read/evidence, scheduled, proposal
- purpose: turn trusted DCA telemetry into scoped, redacted, auditable evidence for lifecycle, supplies, service, meter, billing, and outcome review.
- business outcome: tenant operators and bounded agents can rely on fresh telemetry evidence while stale, contradictory, unmapped, or suspicious readings become explicit exceptions instead of hidden assumptions.

## In-scope outcomes

- Accept telemetry events or scheduled refresh results from trusted DCA ingestion boundaries.
- Validate tenant/customer/device/collector mapping and telemetry freshness using accepted source-trust rules.
- Produce scoped evidence summaries for supply level, meter reads, fault state, collector health, discovery status, and signal freshness.
- Flag stale, missing, conflicting, unmapped, duplicate, or suspicious telemetry for decision cards or exception workflows.
- Provide read-only evidence surfaces for authorized humans, workflows, and agents.

## Out-of-scope outcomes

- Vendor-specific DCA payload schemas, protocol adapters, polling schedules, or device-specific normalization rules.
- Autonomous shipment, dispatch, billing, lifecycle transition, or customer communication side effects.
- Raw telemetry dumps to browsers, agent tools, support users, or audit summaries unless a future contract explicitly authorizes a redacted shape.

## Authority and contract

- actors/callers: DCA telemetry ingest service, telemetry consumer, tenant operations users, telemetry analyst agent, lifecycle/supplies/service/billing workflows, scheduled refresh/recheck timers, auditor/support with scoped grants.
- AuthContext/scope: trusted service identity or authenticated account, selected tenant/customer context, device/collector scope, telemetry ingest/read permission, source-trust boundary, and tenant/customer filters.
- inputs: telemetry event or refresh reference, source system id, tenant/customer/device/collector mapping ids, observed-at timestamp, payload summary/hash, correlation id, and idempotency key.
- outputs: telemetry evidence summary, freshness status, mapping status, confidence/risk markers, safe error/denial shape, redaction marker, and trace links.
- data access: device assignment, collector lifecycle, source mapping, prior telemetry summaries, accepted baselines, related open exceptions, and trace records.
- side effects: evidence record/update, exception marker, workflow notification, scheduled recheck, and audit/work-trace record; no direct operational side effects.
- idempotency: duplicate telemetry events or refreshes by source/event id or observed-at/evidence hash update existing evidence or return no-op.
- policy/approval: low-confidence, unmapped, stale, contradictory, or billing-impacting evidence must be surfaced for review before downstream consequential use.
- exposure surfaces: service/event ingestion, scoped telemetry views, scheduled refresh/recheck timers, exception decision cards, read-only evidence tools, and consumer reactions.

## Required future detail

- Accepted source-trust and freshness policy definitions.
- Vendor payload contracts and normalization maps.
- Telemetry baseline and anomaly formulas.
- Concrete tests for ingestion idempotency, tenant isolation, stale/conflicting data, redaction, audit, and downstream evidence use.

## Linked layers

- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`, `../20-behavior/state-models/01-lifecycle-foundation.md`
- operating model: `../15-operating-model/decisions-exceptions-and-evidence.md`
- auth/security: `../40-auth-security/data-protection.md`, `../40-auth-security/boundary-and-surface-rules.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
