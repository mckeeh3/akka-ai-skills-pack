# Build Backlog: User Admin Surface Conformance Cleanup

## Backlog objective

Repair the runtime User Admin workstream surfaces so they match the defined AI-first workstream and structured-surface concepts. Prefer cleanup/removal over compatibility with legacy CRUD/admin behavior.

## Design notes

- User Admin owns access operations through `user-admin-agent` / `agent-user-admin` until naming is normalized or aliasing is documented.
- Dashboard, directory, detail, task, decision, workflow, lifecycle-confirmation, destructive-lifecycle-confirmation, and system-message surfaces are product semantics, not just routes/components.
- Backend is authoritative for actions, capability checks, row routing, dashboard attention, options/policies, result surfaces, traces, and denials.
- Frontend renders structured envelopes and submits governed browser-tool actions; it must not infer hidden authority or hidden targets from local state.
- Legacy `AdminUsersPage` behavior should be removed, redirected to structured surfaces, or quarantined as non-runtime demo code only if removal is unsafe.

## Work items

### 1. App-description conformance alignment

Update app-description and traceability docs to close modeling ambiguities before implementation:

- canonical surface type policy for User Admin descendants;
- dashboard trunk vs role-specific variant policy;
- functional-agent id alias/normalization policy;
- user detail and invitation detail as show/inspection task routers;
- row/card/dashboard backend-authored routing requirements;
- default vs diagnostic metadata visibility split;
- typed `surface-user-admin-system-message` result requirements;
- backend-shaped role/expiry/policy option requirements;
- access-review and identity-exception starter-scope semantics.

### 2. Backend canonical envelopes and authored payloads

Repair backend surface generation in `WorkstreamService`:

- emit canonical surface types or documented compatibility flags;
- add complete `attentionCounts[]`, `administeredPopulations[]`, `authorizedActions[]`, and branch actions where dashboard frontend needs them;
- ensure list rows carry complete target action/surface/object metadata;
- supply form option/policy payloads;
- reduce or move browser payload diagnostics into diagnostic/audit payload areas;
- preserve audit/trace/idempotency and selected `AuthContext` enforcement.

### 3. Backend inspection/task-router and typed result behavior

Repair action routing and result surfaces:

- user detail/invitation detail never mutate inline;
- role/status/support/invitation changes open or submit through dedicated surfaces;
- no-op/denied/stale/hidden/not-found/provider/outbox/model blocked paths return typed safe system-message surfaces where possible;
- access review and identity exception return durable starter-scope task/decision envelopes with fail-closed semantics.

### 4. Frontend surface cleanup and legacy retirement

Repair frontend structured rendering:

- remove inline mutation forms from `DetailEditSurface` User Admin detail rendering;
- support canonical surface types in `SurfaceRenderer` and specialized User Admin renderers;
- remove client-derived dashboard queues and row-routing inference from normal path;
- consume backend role/expiry/policy options;
- hide raw diagnostics by default behind authorized drilldowns;
- retire or absorb `frontend/src/screens/admin/AdminUsersPage.tsx` and update routes/tests.

### 5. Full-stack conformance tests

Add/repair focused tests that prove:

- canonical surface type rendering;
- dashboard authored attention/population/actions;
- row routing authored by backend;
- detail as inspection/task-router only;
- dedicated task surfaces for each consequential mutation;
- system-message denials/no-ops/stale/blocked states;
- backend-shaped options;
- legacy page retirement;
- authorization, tenant/customer isolation, audit/trace redaction, and frontend secret boundary.

### 6. Terminal verification

Verify current sprint and mini-project done state, run/review checks, document findings, and append new bounded tasks plus a new terminal verification task if gaps remain.

## Suggested task breakdown

- `TASK-UASCC-00-001`: create planning scaffold.
- `TASK-UASCC-01-001`: app-description/spec conformance alignment.
- `TASK-UASCC-02-001`: backend canonical envelopes and backend-authored dashboard/list payloads.
- `TASK-UASCC-02-002`: backend inspection/task-router and typed system-message outcomes.
- `TASK-UASCC-03-001`: frontend structured-surface cleanup and legacy page retirement.
- `TASK-UASCC-04-001`: full-stack conformance tests.
- `TASK-UASCC-99-001`: terminal verification.
