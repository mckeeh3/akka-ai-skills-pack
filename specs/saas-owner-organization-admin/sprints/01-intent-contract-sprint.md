# Sprint 01: Intent and Contract Alignment

## Goal

Make SaaS Owner Organization Admin explicit in the current-intent graph, capability model, and API contracts before runtime implementation.

## Scope

- Clarify SaaS Owner Admin role terminology and authority boundaries.
- Add Organization Admin capability/action semantics to User Admin or an adjacent SaaS Owner surface contract.
- Add frontend API contract entries and DTO terminology.
- Preserve the Organization-vs-Tenant naming convention.

## Acceptance

- App-description describes which users can list/create/rename/suspend/reactivate Organizations.
- App-description states that Organization management does not grant tenant/customer app-data access or support access.
- Denials, idempotency, audit/work traces, billing-boundary non-authority, and frontend secret boundaries are specified.
- Future backend/API/frontend tasks can implement without inventing authority semantics.
