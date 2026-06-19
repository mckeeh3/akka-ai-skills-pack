# Deferred Scope

This current-intent graph intentionally excludes the following behavior from the secure SaaS core starter until a later accepted intent change adds it with capability, surface, API, auth, trace, and test contracts.

## Explicitly deferred

- Billing, subscription, entitlement, invoice, payment, and billing-provider administration.
- Timer-backed invitation reminders and reminder scheduling.
- App-specific CRM, customer-success, sales/revenue, support/service case, procurement, customer-intelligence, or industry-specific customer domain behavior.

## Rules for deferred areas

- Deferred behavior is not partially implemented by browser copy, fixtures, archived specs, route names, or compatibility aliases.
- Normal runtime must fail closed or omit unavailable actions rather than simulate deferred behavior.
- Any future acceptance of a deferred area must update app-level/domain capability nodes, workstream surfaces, API contracts, authorization, traces, tests, and realization mappings together.
