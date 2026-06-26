# Policies: Audit/Trace

## Uses

Global policies: `../../../../../global/policies/foundation-security-and-governance.md`.

## Tenant-admin activity-log scope bindings

Applies:

- tenant-customer-isolation;
- backend-authorization-default-deny;
- frontend-secret-boundary;
- sensitive-payload-tenant-admin-only;
- immutable-audit-records-until-retention-expiry;
- deterministic-summary-search-only;
- audit-retention-range-30-to-365-days.

Policy evaluation is backend-enforced for protected search, detail reads, tool-call detail reads, retention setting reads/updates, denials, retention expiry, and frontend-visible payloads.

## Denial evidence

Denied actions must produce durable trace evidence. Denied trace records visible to an authorized tenant admin include the denial reason and policy reference. User-facing denial responses must not expose protected data, hidden trace existence, hidden cross-tenant/customer ids, raw policy internals, secrets, or provider credentials.

## Tenant-admin activity-log scope exclusions

Export governance, investigation-note policies, suspicious-activity acknowledgement policies, and AI-summary/provider-runtime policies are not active tenant-admin activity-log scope Audit/Trace workstream policies unless a later current-intent change reintroduces those features.
