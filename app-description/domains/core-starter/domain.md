# Domain: Core Starter

## Purpose

The `core-starter` domain captures the built-in secure multi-tenant AI-first SaaS Foundation App behavior that is shipped with this repository.

## Boundary

In scope:

- My Account
- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy
- Shared foundation commitments needed by those workstreams, referenced from foundation doctrine and mapped to concrete starter bindings.
- Foundation customer-boundary administration: tenant-owned Customer records, Customer Admin bootstrap/maintenance, selected tenant/customer `AuthContext` semantics, redaction boundaries, denial behavior, and audit/work trace obligations used by the secure SaaS substrate.

Out of scope:

- User-owned business domains, which belong under additive extension zones.
- Business CRM/account profiles, contacts, opportunities, customer health, renewal plans, customer-success workflows, sales/revenue pipelines, support/service cases, billing subscriptions/entitlements, customer intelligence, and industry-specific customer objects.
- Deferred or app-specific billing and timer reminder behavior unless accepted by a later task.
- Legacy page-style frontend fixtures as primary runtime architecture.

## Foundation customer boundary

The core starter owns the generic Customer boundary that lets an Organization/Tenant partition administration and access by customer. A Customer record is intentionally small: it identifies a tenant-owned customer boundary, carries a safe display label and lifecycle state, anchors Customer Admin memberships and invitations, and scopes authorization, redaction, audit, work traces, support access, and cross-customer denial behavior.

Downstream business domains may own customer-scoped records and richer customer concepts, but they must treat the core-starter Customer as a referenced boundary rather than moving CRM, sales, support, billing, or industry-specific data into foundation identity state. Most business customer domains are organization-level domains whose records include explicit customer scope. Support/service domains may span organization-level configuration and customer-level case or escalation records, but every capability must declare whether it is organization-scoped, customer-scoped, assigned-case-scoped, or affected-customer-scoped.

## Graph conventions

- Capability nodes live in `capabilities/`.
- Durable state responsibility nodes live in `data-state/`.
- Workstream bindings live under `workstreams/<workstream-id>/` and keep access, behavior, surfaces, agents, tools, policies, traces, tests, and realization separate.
- Realization files map current intent to Akka components, API contracts, frontend routes, and validation evidence; they do not implement runtime behavior.
