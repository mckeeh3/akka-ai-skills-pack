# Java Package Boundaries

The root app keeps the standard Akka Java layers and partitions ownership inside each layer:

```text
ai.first.api.foundation.*          # reusable foundation API surfaces
ai.first.api.coreapp.*             # built-in core app API surfaces
ai.first.api.business.<area>.*     # user-owned business API surfaces

ai.first.application.foundation.*  # reusable SaaS foundation services/components
ai.first.application.coreapp.*     # built-in core app workstreams/components
ai.first.application.business.<area>.* # user-owned business services/components

ai.first.domain.foundation.*       # reusable foundation domain records/contracts
ai.first.domain.coreapp.*          # built-in core app workstream records/contracts
ai.first.domain.business.<area>.*  # user-owned business domain records/contracts
```

## Dependency direction

- `foundation` is the reusable platform layer. It must not depend on user-owned `business` packages. Foundation domain and API packages must also stay independent of `coreapp` packages.
- `coreapp` contains the built-in My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy workstreams. It must not depend on `business` packages.
- `business.<area>` is for CRM, ERP, billing, procurement, or other product-specific domains. Business code may depend on stable foundation contracts and approved core app extension hooks instead of modifying foundation/core internals.

## Checks

`mvn test` runs `JavaPackageBoundaryTest`, a lightweight source-level guard that verifies:

- production code under foundation/coreapp does not import `ai.first.*.business.*`;
- foundation domain/API code does not import coreapp packages;
- business production packages, when added, use an area segment after `business`.

Application-level foundation composition currently wires built-in core app runtime services, so the check deliberately keeps that integration seam documented rather than blocking migration work. If those composition seams are split later, tighten the test to forbid all `foundation -> coreapp` imports.
