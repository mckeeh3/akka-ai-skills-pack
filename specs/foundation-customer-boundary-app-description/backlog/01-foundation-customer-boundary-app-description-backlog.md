# Backlog: Foundation Customer Boundary App-description

## FCBAD-01: Inventory current customer-boundary evidence and graph gaps

Create a bounded inventory of active app-description nodes and runtime evidence related to the foundation customer boundary. Identify where the current graph is sufficient, missing, ambiguous, stale, or too implementation-specific.

Suggested task: `TASK-FCBAD-01-001`.

Evidence targets:

- `app-description/app.md`
- `app-description/global/**`
- `app-description/domains/core-starter/**`
- `src/main/java/ai/first/domain/foundation/identity/Customer.java`
- `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`
- `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/api/ApiClient.ts`
- `frontend/src/api/HttpApiClient.ts`
- `frontend/src/workstream/surfaces/UserAdminScopedAdminSurface.tsx`
- focused tests that prove currently implemented customer-boundary behavior.

## FCBAD-02: Capture domain, capability, and state intent

Update active app-description graph nodes for:

- foundation customer boundary purpose and non-goals;
- capability contracts for customer lifecycle and Customer Admin administration;
- durable state responsibilities and invariants for Customer/Tenant/Membership/AuthContext/invitation/audit linkage;
- organization-level vs customer-level scope language;
- explicit separation from CRM/customer-success/sales/billing/support business domains.

Suggested task: `TASK-FCBAD-01-002`.

## FCBAD-03: Capture workstream, surface, agent, tool, policy, trace, test, and realization bindings

Update User Admin workstream and related global/current-intent nodes for:

- customer directory/detail/lifecycle/Customer Admin surfaces;
- surface action edges and safe system-message outcomes;
- user-admin-agent authority limits and governed tool exposure;
- policy and denial behavior;
- audit/work trace obligations;
- tests and Akka/API/frontend realization mapping.

Suggested task: `TASK-FCBAD-01-003`.

## FCBAD-04: Verify unambiguous description and loop if needed

Run a terminal verification that asks whether the active description is sufficiently unambiguous. If not, append bounded follow-up tasks and a new terminal verification task.

Suggested task: `TASK-FCBAD-01-004`.
