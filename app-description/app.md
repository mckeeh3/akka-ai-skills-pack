# Secure Multi-tenant AI-first SaaS Core Starter

## Objective

Provide the canonical runnable secure AI-first SMB SaaS core app that teams can clone, run locally, and extend with business-specific domains while preserving tenant/customer scoping, backend authorization, governed AI behavior, audit/work traces, and browser secret boundaries.

The built-in Customer concept is a foundation customer boundary: a tenant-owned authorization, administration, redaction, and audit scope used by the secure SaaS substrate. It is not a CRM account, customer-success profile, sales opportunity, support case, billing subscription, or industry-specific customer domain object.

## Operating model

The app is a SaaS Foundation App with role-authorized functional-agent workstreams. Human users supervise work through a WorkOS/AuthKit-gated web shell; backend capabilities and governed tools enforce local authorization, policy denials, trace capture, and provider fail-closed behavior.

## Current domain

- Primary domain: [`domains/core-starter/domain.md`](domains/core-starter/domain.md)
- Built-in workstreams: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Business-specific extensions should be additive under repository extension zones rather than replacing this core starter graph.
- Deferred scope: [`deferred-scope.md`](deferred-scope.md)

## Foundation references

Reusable foundation doctrine is referenced rather than duplicated here:

- `skills-pack/docs/ai-first-saas-application-architecture.md`
- `skills-pack/docs/capability-first-backend-architecture.md`
- `skills-pack/docs/current-intent-model.md`
- `skills-pack/docs/intent-to-realization-flow.md`

## Tenant, security, and validation posture

- Every current-intent node must preserve tenant/customer scope, authenticated account context, backend authorization, audit/work trace obligations, and frontend secret boundaries.
- Normal runtime behavior must not depend on deterministic demos, mocks, fixtures, or model-less substitutes.
- Provider-dependent behavior must fail closed with actionable errors when required configuration is missing.
- Runtime readiness is proven through the intended local Akka/API/UI path at the stated scope, not by description alone.

## Generation and runtime guardrails

- Generators and maintainers must read this file, `deferred-scope.md`, global runtime contracts, workstream surface contracts, and realization caveats together before treating behavior as current intent.
- Archived specs, compatibility route names, retired action ids, legacy screen modules, fixtures, demo data, and test-only providers are never product authority and must not be used to infer missing behavior.
- Canonical workstream shell contracts are authoritative for browser behavior; compatibility `/api/admin/**` routes are protected service/API edges only.
- Model-backed, Resend-backed, WorkOS/AuthKit-backed, and outbox-backed behavior must either execute through configured runtime paths or return explicit fail-closed surfaces/results. Canned or model-less success is forbidden as normal runtime behavior.
- Deferred scope remains absent until an accepted intent change adds capability, surface, API, auth, trace, test, and realization mappings in one consistent update.

## Non-goals

- Do not add app-specific CRM, customer-success, sales/revenue, support/service, billing, procurement, customer intelligence, timer-backed reminder, or other business-domain workstreams to the core starter graph.
- Do not treat archived legacy app-description files as current product authority.
- Do not duplicate skills-pack foundation doctrine wholesale in this graph.
