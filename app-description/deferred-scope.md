# Deferred Scope

This current-intent graph intentionally excludes the following behavior from the secure SaaS core starter until a later accepted intent change adds it with capability, surface, API, auth, trace, and test contracts.

## Explicitly deferred

- Billing, subscription, entitlement, invoice, payment, and billing-provider administration.
- Timer-backed invitation reminders and reminder scheduling.
- App-specific CRM, customer-success, sales/revenue, support/service case, procurement, customer-intelligence, or industry-specific customer domain behavior.

## Rules for deferred areas

- Deferred behavior is not partially implemented by browser copy, fixtures, archived specs, route names, compatibility aliases, sample data, disabled controls, or placeholder workstream labels.
- Normal runtime must fail closed or omit unavailable actions rather than simulate deferred behavior.
- Generators must not create entities, endpoints, routes, frontend panels, background jobs, timers, reminders, fake billing/customer-success/support/sales records, or acceptance tests for deferred areas unless the same accepted change updates the current-intent graph first.
- If archived specs, legacy code, tests, or fixtures mention a deferred area, the current runtime must treat those references as historical evidence only and route user-visible attempts to a safe unavailable/unsupported result.
- Any future acceptance of a deferred area must update app-level/domain capability nodes, workstream surfaces, API contracts, authorization, traces, tests, and realization mappings together.
