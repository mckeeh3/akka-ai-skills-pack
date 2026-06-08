# Secure Multi-tenant AI-first SaaS Core Starter

## Objective

Provide the canonical runnable secure AI-first SMB SaaS core app that teams can clone, run locally, and extend with business-specific domains while preserving tenant/customer scoping, backend authorization, governed AI behavior, audit/work traces, and browser secret boundaries.

## Operating model

The app is a SaaS Foundation App with role-authorized functional-agent workstreams. Human users supervise work through a WorkOS/AuthKit-gated web shell; backend capabilities and governed tools enforce local authorization, policy denials, trace capture, and provider fail-closed behavior.

## Current domain

- Primary domain: [`domains/core-starter/domain.md`](domains/core-starter/domain.md)
- Built-in workstreams: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Business-specific extensions should be additive under repository extension zones rather than replacing this core starter graph.

## Foundation references

Reusable foundation doctrine is referenced rather than duplicated here:

- `.agents/skills/docs/ai-first-saas-application-architecture.md`
- `.agents/skills/docs/capability-first-backend-architecture.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`

## Tenant, security, and validation posture

- Every current-intent node must preserve tenant/customer scope, authenticated account context, backend authorization, audit/work trace obligations, and frontend secret boundaries.
- Normal runtime behavior must not depend on deterministic demos, mocks, fixtures, or model-less substitutes.
- Provider-dependent behavior must fail closed with actionable errors when required configuration is missing.
- Runtime readiness is proven through the intended local Akka/API/UI path at the stated scope, not by description alone.

## Non-goals

- Do not add app-specific CRM, billing, procurement, or other business-domain workstreams to the core starter graph.
- Do not treat archived legacy app-description files as current product authority.
- Do not duplicate skills-pack foundation doctrine wholesale in this graph.
