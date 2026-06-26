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
- Audit/work trace records are immutable evidence. Correction or cleanup is represented by new linked facts rather than mutation of original evidence; for the Audit/Trace tenant-admin activity-log scope, removal occurs only by retention expiry.
- Trace-gap, investigation-note, and export-request lifecycle state are not part of the Audit/Trace tenant-admin activity-log scope unless a later current-intent change reintroduces those features.

## Retention and traces

Trace records must answer who or what acted, under which tenant/customer context and authority, which policy/tool/data/model was used, what was denied or allowed, which effective policy source applied, and what outcome evidence followed.

For the Audit/Trace tenant-admin activity-log scope, tenant audit trace retention defaults to 90 days and is tenant-admin configurable from 30 to 365 days. Retention changes are audit-traced and must not expose hidden records across scopes. Records are immutable until retention expiry. Browser export links, generated export bundles, legal hold, account erasure, provider-side deletion reconciliation, and bulk purge are out of scope for Audit/Trace tenant-admin activity-log scope until explicitly described with authorization, redaction, audit, tests, and provider-boundary contracts.
