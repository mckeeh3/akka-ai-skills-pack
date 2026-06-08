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

Out of scope:

- User-owned business domains, which belong under additive extension zones.
- Deferred or app-specific billing and timer reminder behavior unless accepted by a later task.
- Legacy page-style frontend fixtures as primary runtime architecture.

## Graph conventions

- Capability nodes live in `capabilities/`.
- Durable state responsibility nodes live in `data-state/`.
- Workstream bindings live under `workstreams/<workstream-id>/` and keep access, behavior, surfaces, agents, tools, policies, traces, tests, and realization separate.
- Realization files map current intent to Akka components, API contracts, frontend routes, and validation evidence; they do not implement runtime behavior.
