# Sprint 03: Core App Package Migration

## Objective

Move built-in five-core-app workstream and operational app code into `*.coreapp.*` packages.

## Scope

- My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy services, agents/workers, APIs, DTOs, tests, and supporting code classified as coreapp.
- Core app workstream API routes and app-specific operational services.

## Acceptance criteria

- Built-in core app code lives under `api/application/domain.coreapp.*`.
- Runtime behavior and tests are preserved.
- Business-extension package zones remain untouched except for references/docs.
