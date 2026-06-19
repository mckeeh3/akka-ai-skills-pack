# TASK-FCBAD-01-001: Inventory customer-boundary evidence and graph gaps

## Objective

Inventory active app-description and implementation evidence for the foundation customer boundary. Produce a gap map that identifies which current-intent nodes need edits before the description can be considered sufficiently unambiguous.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `app-description/AGENTS.md`
- `app-description/README.md`
- `app-description/app.md`
- `app-description/global/roles/foundation-roles.md`
- `app-description/global/agents/foundation-functional-agents.md`
- `app-description/domains/core-starter/domain.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/workstreams/user-admin/**`
- `specs/foundation-customer-boundary-app-description/README.md`
- `specs/foundation-customer-boundary-app-description/conversation-capture.md`
- this task brief

## Evidence sources to inspect selectively

- `src/main/java/ai/first/domain/foundation/identity/Customer.java`
- `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/api/ApiClient.ts`
- `frontend/src/api/HttpApiClient.ts`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- focused tests found by searching for `customer`, `TenantCustomer`, and `action-customer`.

## Expected output

- `specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md`

## Required checks

- `git diff --check`
- Search proof commands recorded in task notes, for example:
  - `rg -n "Customer|customer|tenant.customer|action-customer" app-description src/main/java frontend/src src/test/java --glob '!**/node_modules/**'`

## Done criteria

- Gap map identifies active graph nodes to edit for domain/capability/state and workstream/surface/agent/tool/policy/trace/test/realization coverage.
- Gap map distinguishes required app-description edits from non-goal runtime implementation work.
- Gap map states whether any ambiguity blocks the next description task; if yes, update the queue to block and create/point to a pending question.

## Vertical workstream contract

- Scope: docs-only current-intent inventory for foundation customer boundary.
- Attention/non-UI reason: no runtime attention item; planning-only evidence inventory.
- Capability/foundation scope: foundation customer boundary, User Admin customer lifecycle and Customer Admin branch.
- AuthContext/scope: inspect tenant/customer scoping expectations; do not change runtime behavior.
- Akka substrate: docs-only; runtime components are evidence.
- Audit/work trace: inventory expected audit/work trace coverage.
- Local validation path: `git diff --check` plus search proof.
