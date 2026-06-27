# Sprint 01: Backend proposal and behavior-profile foundation

## Goal

Replace stale direct Agent Admin mutation semantics with backend-owned proposal, review, activation, behavior-profile, and version seams sufficient for focused API/frontend realization.

## Scope

- Proposal lifecycle domain/service contracts.
- Non-active Save Draft behavior for prompt/skill/reference changes.
- Separate low-risk activation path with stale/risk checks.
- Restore-as-proposal semantics.
- Tenant-scoped behavior-profile version seams for model config ref, skill assignment, and generated tool assignment.
- Catalog/detail data enrichment needed by frontend surfaces.

## Runtime proof target

`backend-ready` for service-level behavior and `api-smoked` where workstream action wiring is included in tasks.
