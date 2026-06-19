# Billing and Timer-Reminder Scope Decision

- task: TASK-FCSR-08-004
- date: 2026-06-04
- decision: billing implementation and timer-backed invitation reminders remain deferred for the current full-core local/test-scope readiness target

## Decision basis

The completed full-core readiness verification already proves the selected local/test-scope foundation across the five core functional-agent workstreams. It also identifies billing implementation and timer-backed invitation reminders as remaining gaps that require an explicit product decision before implementation.

No current requirement in this follow-up task expands the target product scope to include subscription lifecycle, entitlement enforcement, payment-failure workflows, billing UI, or timer-backed invitation reminder scheduling. Implementing those behaviors would be new product scope, not readiness repair for the validated foundation.

## Billing scope status

Billing remains deferred for the current full-core target.

Accepted current scope:

- preserve the billing-boundary invariant that SaaS Owner billing/subscription metadata does not grant Tenant application-data access;
- avoid billing-derived authorization bypasses;
- keep production readiness language honest: this app is not billing-implementation ready until a product-specific billing lifecycle is specified and implemented.

Deferred implementation scope:

- subscription lifecycle;
- plan and entitlement modeling/enforcement;
- payment-failure behavior;
- billing provider integration;
- billing UI and billing-related support/admin flows.

Readiness impact: the app may be described as full-core local/test-scope foundation validated, with billing implementation explicitly deferred. It must not be described as production billing-ready.

## Timer-backed invitation reminder status

Timer-backed invitation reminders remain deferred for the current full-core target.

Accepted current scope:

- invitation creation, delivery attempts, resend, revoke, expiry command behavior, acceptance constraints, audit/lifecycle history, idempotency, tenant scope, and captured outbox/provider fail-closed evidence remain the validated invitation readiness baseline.

Deferred implementation scope:

- scheduled reminder timers;
- reminder cancellation/replacement when invitations are accepted, revoked, expired, or resent;
- reminder notification/email content and cadence;
- timer-backed lifecycle tests.

Readiness impact: invitation onboarding remains validated for the selected local/test scope, but the app must not claim scheduled invitation reminders until a later product-scope task adds timer scheduling and tests.

## Follow-up policy

If a future target product requires either billing or timer reminders, append new bounded implementation tasks before coding. Those tasks must name the owning functional-agent surface/action, capability id, backend authorization and tenant scope, selected Akka substrate, audit/work trace requirements, provider/timer behavior, UI/API exposure, and runtime validation path.
