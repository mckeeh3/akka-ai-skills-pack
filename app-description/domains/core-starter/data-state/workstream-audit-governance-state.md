# Data State: Workstream, audit, and governance state

## Responsibility

Durable workstream logs, stream items, attention items, notifications, audit/admin events, agent work traces, Governance/Policy SaaS defaults, tenant overrides, effective-policy decisions, policy-change history, runtime outcome links, and trace evidence for the five core workstreams.

## Lifecycle and invariants

- Workstream requests and structured results include correlation ids and trace links.
- Attention counts are backend-owned and scoped to authorized users and selected context.
- Audit/trace evidence is redacted and permissioned before browser or agent exposure.
- Governance/Policy state preserves simple policy definitions, boolean/counter value types, SaaS defaults, tenant overrides, effective values, winning-scope explanations, required change reasons, actors, timestamps, and linked runtime decision evidence.
- Tenant overrides for business-governance policies become active immediately and win over SaaS defaults; finer-grained matching scopes win over less-specific scopes.
- SaaS owner default updates must not overwrite tenant overrides.
- Hard platform security controls such as tenant isolation, backend authorization, secret protection, redaction boundaries, audit trace integrity, and platform integrity are not overrideable.
- Export/digest requests are governed and never bypass data visibility rules.
- Audit/work trace records are append-only evidence. Correction is represented by a new linked correction, supersession, redaction, investigation note, reset-to-default action, or outcome record rather than mutating prior evidence.
- Trace-gap records have states `detected`, `under_investigation`, `explained`, `remediated`, `accepted_risk`, and `false_positive`; unresolved gaps remain visible to authorized Audit/Trace attention until terminal.
- Investigation notes have states `draft`, `submitted`, `accepted`, `rejected`, `superseded`, and `redacted`. Accepted notes are retained as annotations; edits create new versions and never alter the original accepted note.
- Export requests have states `draft`, `approval_required`, `approved`, `denied`, `queued`, `running`, `completed`, `download_expired`, `cancelled`, and `failed`. Export bundles are generated from authorized redacted evidence only, carry expiry metadata, and are not durable substitutes for the source audit trace.

## Retention and traces

Trace records must answer who or what acted, under which tenant/customer context and authority, which policy/tool/data/model was used, what was denied or allowed, which effective policy source applied, and what outcome evidence followed.

The core starter retention default is one year for security-relevant audit/work traces, admin lifecycle events, invitation acceptance evidence, provider-fail-closed evidence, policy/agent behavior decisions, Governance/Policy default/override/reset history, runtime policy-decision evidence, and trace-gap records. Retention is configurable at SaaS-owner/app, Organization, and Customer layers where policy permits. Retention changes are governed policy changes with audit/work traces and must not expose hidden records across scopes. Browser export links and generated export bundles are available only after approval, are always redacted, and include only evidence still retained and available at generation/download time. Expiry removes download availability, not source evidence. Unredacted browser exports are always forbidden. Legal hold, account erasure, provider-side deletion reconciliation, and bulk purge are out of scope until explicitly described with authorization, redaction, audit, tests, and provider-boundary contracts.
