# Generation Policy

## Reference status

This app-description is a non-runnable vertical reference asset for the `akka-ai-skills-pack`. It must not be treated as a request to generate application code in this repository.

The canonical reusable foundation is `docs/examples/ai-first-saas-seed-app-description/`. The DCA tree supplies domain-rich lifecycle, telemetry, supplies, service, billing, policy, decision, audit, UI, and outcome semantics that a downstream project may adapt after explicitly choosing a realization scope.

## Generation allowed when

Generation is allowed only when all of the following are true:

- a user explicitly requests or accepts realization of a bounded DCA slice;
- the target is a downstream generated-app workspace or a clearly scoped executable reference slice, not an accidental mutation of this source repository's pack assets;
- the requested slice has current capability-first contracts with actors/callers, AuthContext, inputs/outputs, data access, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests;
- the secure SaaS foundation is defined for the slice, including WorkOS/AuthKit authentication, Akka-owned authorization state, Tenant/Customer boundaries, memberships, roles/scopes, invitations, `/api/me`, support access, admin audit, billing boundary, backend authorization, and tenant/customer isolation;
- tests are concrete enough for the slice, including success, validation, forbidden access, tenant isolation, disabled-user, role/scope denial, approval bypass, idempotency, audit/trace, frontend secret-boundary, and outcome checks as applicable;
- external integration contracts and fixtures required by the slice are defined or explicitly stubbed with accepted local/test adapters;
- UI surfaces, frontend API contracts, loading/empty/error/stale states, accessibility/responsive expectations, and style-guide constraints are defined for any generated browser scope.

## Generation blocked when

Generation remains blocked when it would require the harness to invent:

- WorkOS/AuthKit runtime settings, secret handling, or JWT validation behavior;
- local authorization records, tenant/customer boundaries, roles/scopes, invitations, support-access, billing-boundary, or `/api/me` semantics;
- protected capability authority, schemas, side effects, idempotency, approval gates, trace events, or exposure surfaces;
- numeric thresholds, risk/confidence policies, retention rules, redaction classes, external API contracts, or evaluation fixtures;
- acceptance, negative, regression, operational, security, trace, UI, or outcome tests;
- browser UI style, navigation, API contracts, realtime/stale behavior, or secret-boundary rules.

## Default regeneration preference

- Preserve this tree as the authoritative reference description.
- Prefer localized generation only after a bounded slice's impacted capabilities, behavior, tests, auth/security, observability, UI, and traceability are explicit.
- Prefer full regeneration only for a deliberately scoped downstream bootstrap where the secure SaaS foundation and selected DCA slice are ready together.
- Do not regenerate from derived `80-review/` summaries alone; use authoritative layers first.

## Assumption policy

- Assumptions must be recorded in `00-system/readiness-status.md`, relevant capability files, and readiness/review summaries.
- Security assumptions must never weaken tenant isolation, backend authorization, invitation/onboarding controls, support-access rules, auditability, or frontend secret boundaries.
- Agent/autonomy assumptions must never grant side-effecting authority without explicit policy, permission, approval, trace, and test semantics.
- External integration assumptions must identify whether the generated slice uses default Resend production delivery, an accepted alternate production provider, local captured adapter, deterministic fixture, or explicit stub.

## Required validation after any future generation

Any future generated DCA slice must include validation appropriate to its scope:

- backend unit/component tests for domain decisions, entities, workflows, agents, consumers, timed actions, and views;
- endpoint authorization tests for JWT, AuthContext, tenant/customer scope, disabled users, denied roles/scopes, and safe denial shapes;
- invitation/onboarding, support-access, admin-audit, billing-boundary, and `/api/me` tests when foundation scope is generated;
- tenant-isolation and idempotency regression tests;
- approval-gate, decision-card, policy, agent-tool, data-access, and audit/work-trace tests for AI-first behavior;
- frontend build/typecheck plus UI interaction tests for shell, context selection, admin, supervision, decision, governance, audit, and outcome surfaces in scope;
- frontend secret-boundary checks ensuring provider secrets and backend-only configuration are not exposed in `VITE_` variables or built assets;
- deterministic fixture or captured-adapter checks for DCA telemetry, fulfillment/ERP, billing, service, email, and identity seams used by the slice.
