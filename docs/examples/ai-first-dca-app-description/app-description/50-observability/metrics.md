# Metrics

## Purpose

Define product-accountability and operational metrics for the DCA vertical reference. Metrics should support supervision, safety, audit quality, outcome review, and future generation planning rather than vanity dashboards.

## Foundation metrics (`CAP-00`)

- Authentication and `/api/me`: success/error rate, latency, first-login linking outcomes, selected-context failures.
- Invitation lifecycle: created, delivered, delivery failed, resent, revoked, expired, accepted, duplicate/idempotent accept attempts, and aging pending invites.
- Membership/role/access: role changes, denied role/scope attempts, disabled-user attempts, last-admin risk blocks, access-review findings, and support-access grants/uses/expirations.
- Admin audit quality: protected reads/actions with durable audit records, audit write failures, audit view access denials, and trace completeness checks.
- Billing boundary: tenant setup/subscription/entitlement changes, owner-side billing operation failures, and unauthorized owner-to-tenant data access attempts.

## DCA operational metrics

- Lifecycle orchestration (`CAP-01`): workflow starts/completions/failures, blocked gate count, exception aging, lifecycle transition latency, and approval wait time.
- Telemetry intelligence (`CAP-02`): accepted/rejected telemetry events, stale telemetry count, missing collector/device data, scheduled refresh failures, forecast data quality, and telemetry-to-decision latency.
- Supplies autopilot (`CAP-03`): recommendations created/updated/suppressed, auto-ship count, approval-required count, approval/rejection/modification rates, duplicate/no-op retries, integration failures, fulfillment latency, pending decision age, and exception rate by policy clause.
- Service coordination (`CAP-04`): service recommendations, dispatch approvals, SLA exceptions, remote-fix success/failure, technician handoff latency, and repeat fault rate.
- Meter billing review (`CAP-05`): anomaly count, review age, approved/rejected billing adjustments, correction rate, and billing handoff failures.
- Onboarding/installation (`CAP-06`): onboarding workflow duration, installation validation failures, DCA collector health at activation, blocked customer/device setup, and customer-visible status age.
- Offboarding/retention (`CAP-07`): shipment suppression during offboarding, deauthorization completion, retention action completion/failure, archive/anonymization jobs, and final billing handoff status.
- Policy governance (`CAP-08`): proposals, simulations/replays, approvals/rejections, activations, rollbacks, evaluator findings, and policy version adoption.
- Owner command center (`CAP-09`): active objectives, open decisions, stuck workflows, supervisor acknowledgements, digest catch-up time, and realtime feed freshness.
- Audit/outcome review (`CAP-10`): audit searches, restricted access denials, export/retention/redaction requests, digest generation, and outcome-link completeness.

## Outcome metrics

Outcome metric definitions must include baseline/expected result, measurement source, measurement window, observed value, confidence/data quality, trace links, and review owner.

Priority DCA outcome categories:

- Business value: stockouts avoided, service issues resolved, cycle-time reduction, cost leakage prevented, customer/device risk reduced.
- Safety/risk: unauthorized attempts, approval bypass attempts, high-risk autonomous actions, rollback/suppression rates, policy violations, support-access findings.
- Quality/correctness: recommendation acceptance/correction rate, evidence completeness, false positive/negative indications, rationale quality, policy consistency.
- Timeliness/workload: decision turnaround, exception age, manual work avoided, supervisor interruption rate, agent retry/failure rate.
- Learning/governance: feedback converted to examples/proposals, replay/simulation impact, evaluator drift, policy rollback outcomes.

## Metric privacy and aggregation

- Aggregate metrics must preserve tenant/customer boundaries and role-based visibility.
- Outcome dashboards may show summaries and links but must not leak raw contract, supplier, billing, WorkOS, token, or unrelated customer data.
- Cross-tenant SaaS Owner metrics are limited to platform-safe operational and billing-boundary summaries unless tenant-created support access grants a scoped view.
