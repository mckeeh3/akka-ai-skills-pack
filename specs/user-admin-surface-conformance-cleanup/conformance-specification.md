# User Admin Surface Conformance Specification

## Purpose

This specification captures the implementation decisions for `TASK-UASCC-01-001` so backend and frontend cleanup tasks can implement User Admin structured-surface conformance without guessing.

## Authoritative decisions

1. **Canonical surface types**: User Admin descendants use the canonical structured surface types documented in `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`: `dashboard`, `list-search`, `show-inspection`, `create-form`, `edit-form`, `lifecycle-confirmation`, `destructive-lifecycle-confirmation`, `decision-card`, `workflow-status`, `outcome-panel`, `diff`, and `system-message`. Compatibility types such as `detail-edit` may only be temporary mappings and must preserve the canonical semantics.
2. **Dashboard variants**: `surface-user-admin-dashboard` is the trunk. App Admin, Tenant Admin, Customer Admin, and Auditor differences are backend-authored variants of the trunk using selected `AuthContext`, admin level, visible attention queues, visible populations, and omitted-forbidden actions. The frontend must not infer hidden powers, hidden counts, or separate dashboard products.
3. **Functional-agent id**: `user-admin-agent` is the canonical concept. `agent-user-admin` is a runtime compatibility alias until implementation normalization, and does not represent a separate functional agent.
4. **Inspection/task-router detail surfaces**: user, invitation, and Organization detail surfaces are `show-inspection` surfaces. They expose task entry points but do not perform inline role, status, support-access, invitation, access-review, identity, or lifecycle mutations.
5. **Dedicated consequential surfaces**: invitation create/resend/revoke, membership lifecycle changes, role/capability changes, support-access grant/revoke, access-review decisions, identity-exception recovery, and Organization create/rename/suspend/reactivate route through dedicated task/form/decision/workflow/lifecycle surfaces with typed result surfaces.
6. **Backend-authored routing and options**: dashboard attention queues, administered population cards, branch actions, list row/card activation, return actions, target surface ids, target object types, action ids, eligibility, role options, expiry options, policy limits, and safe filters are backend-authored. Frontend action visibility is advisory only.
7. **Metadata visibility**: default UI payloads use product language and business outcomes. Raw capability/tool ids, correlation/idempotency mechanics, raw trace/event ids, provider/model/outbox internals, and implementation diagnostics are available only through role-gated audit/support/developer drilldowns or diagnostic metadata.
8. **Typed system-message outcomes**: denials, stale targets, hidden/not-found targets, validation failures, duplicate/open-invite conflicts, no-ops, provider/outbox/model blocked states, missing context, disabled actors, and tenant/customer isolation conflicts return `surface-user-admin-system-message` or another typed result surface with safe recovery and trace/correlation refs.
9. **Access-review semantics**: access review is durable task/review work. Worker/model output cannot directly mutate access; human decisions record review and route any deterministic access change through membership, role, support-access, or invitation surfaces.
10. **Identity-exception semantics**: identity exception review is fail-closed and provider-safe. Recovery routes through approved review/status surfaces and never exposes raw provider internals in default payloads.

## Impact and realization scope

Authoritative impact is localized to User Admin workstream/surface docs and the core-starter traceability map. Derived realization work remains in this mini-project's backend, frontend, and test tasks:

- `TASK-UASCC-02-001`: backend canonical envelopes, backend-authored dashboard/list payloads, and backend-shaped options;
- `TASK-UASCC-02-002`: backend task routing and typed system-message outcomes;
- `TASK-UASCC-03-001`: frontend canonical rendering and legacy admin-page retirement;
- `TASK-UASCC-04-001`: full-stack conformance tests;
- `TASK-UASCC-99-001`: verification and follow-up task append if gaps remain.

## Required proof for implementation tasks

Implementation tasks must prove, at their stated scope, that User Admin runtime paths use canonical surface semantics, backend-authored routing/options, inspection-only detail surfaces, typed result/system-message outcomes, role/scope authorization, tenant/customer redaction, audit/work trace links, and frontend secret boundaries.
