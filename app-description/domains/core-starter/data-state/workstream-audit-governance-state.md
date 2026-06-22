# Data State: Workstream, audit, and governance state

## Responsibility

Durable workstream logs, stream items, attention items, notifications, audit/admin events, agent work traces, policy proposals, simulations, decisions, activation history, rollback history, and outcome notes for the five core workstreams.

## Lifecycle and invariants

- Workstream requests and structured results include correlation ids and trace links.
- Attention counts are backend-owned and scoped to authorized users and selected context.
- Audit/trace evidence is redacted and permissioned before browser or agent exposure.
- Policy proposals and decisions preserve evidence, risk, confidence, impact, alternatives, reviewer, and outcome linkage.
- Export/digest requests are governed and never bypass data visibility rules.
- Audit/work trace records are append-only evidence. Correction is represented by a new linked correction, supersession, redaction, investigation note, or outcome record rather than mutating prior evidence.
- Trace-gap records have states `detected`, `under_investigation`, `explained`, `remediated`, `accepted_risk`, and `false_positive`; unresolved gaps remain visible to authorized Audit/Trace attention until terminal.
- Investigation notes have states `draft`, `submitted`, `accepted`, `rejected`, `superseded`, and `redacted`. Accepted notes are retained as annotations; edits create new versions and never alter the original accepted note.
- Export requests have states `draft`, `approval_required`, `approved`, `denied`, `queued`, `running`, `completed`, `download_expired`, `cancelled`, and `failed`. Export bundles are generated from authorized redacted evidence only, carry expiry metadata, and are not durable substitutes for the source audit trace.

## Retention and traces

Trace records must answer who or what acted, under which tenant/customer context and authority, which policy/tool/data/model was used, what was denied or approved, and what outcome evidence followed.

The core starter retention default is one year for security-relevant audit/work traces, admin lifecycle events, invitation acceptance evidence, provider-fail-closed evidence, policy/agent behavior decisions, and trace-gap records. Retention is configurable at SaaS-owner/app, Organization, and Customer layers; Organization Admins may adjust Customer-level retention for Customers inside their Organization when policy permits. Retention changes are governed policy changes with audit/work traces and must not expose hidden records across scopes. Browser export links and generated export bundles are available only after approval, are always redacted, and include only evidence still retained and available at generation/download time. Expiry removes download availability, not source evidence. Unredacted browser exports are always forbidden. Legal hold, account erasure, provider-side deletion reconciliation, and bulk purge are out of scope until explicitly described with authorization, redaction, audit, tests, and provider-boundary contracts.
