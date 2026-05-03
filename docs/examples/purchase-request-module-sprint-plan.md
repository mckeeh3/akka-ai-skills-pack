# Purchase Request Module/Sprint Planning Example

This is a compact reference example for `../module-sprint-planning.md`.

It shows how a larger PRD output can be organized so each sprint completes a testable module increment across backend and frontend.

## Module map

### Module 01 — Identity and access

- actors: requester, manager approver, finance approver, procurement operator, admin
- owns: user/account metadata, roles, `/api/me`, admin role assignment when in scope
- references: `specs/cross-cutting/01-auth-tenancy-audit.md`
- related sprints: `specs/sprints/01-identity-access-sprint.md`

### Module 02 — Purchase requests

- actors: requester, manager approver
- owns: purchase request lifecycle state, request submission/editing, requester dashboard
- backend: `PurchaseRequestEntity`, request endpoints, request status view
- frontend: request form, request detail, requester list
- related sprints: `specs/sprints/02-purchase-request-core-sprint.md`

### Module 03 — Approval operations

- actors: manager approver, finance approver
- owns: approval workflow, approval queues, reminder/expiry behavior
- backend: `PurchaseRequestApprovalWorkflow`, operational views, timed actions
- frontend: approver queue, approval decision screen, stale/expired indicators
- related sprints: `specs/sprints/03-approval-flow-sprint.md`

## Sprint outline

### Sprint 01 — Identity and access foundation

- backend scope:
  - JWT/request-context handling
  - `/api/me`
  - role model and basic admin endpoints if required
- frontend scope:
  - authenticated app shell
  - current-user load state
  - role-aware navigation shell
- full-stack done check:
  - signed-in user can load the app shell and `/api/me`
  - unauthorized calls are rejected

### Sprint 02 — Purchase request core

- backend scope:
  - domain/API records
  - `PurchaseRequestEntity`
  - request submit/get/list endpoints
  - request status view
- frontend scope:
  - request creation form
  - requester request list
  - request detail page
- full-stack done check:
  - requester creates a request in the UI
  - backend persists it
  - list/detail UI shows the submitted request

### Sprint 03 — Approval flow

- backend scope:
  - approval workflow with manager/finance branch
  - approval queue view
  - approval/rejection endpoints
  - reminder/expiry timed action
- frontend scope:
  - approver queue
  - approval decision form
  - expired/stale state display
- full-stack done check:
  - approver sees a pending request, approves or rejects it, and requester-visible status updates

## Matching backlog and queue shape

```text
specs/sprints/02-purchase-request-core-sprint.md
specs/backlog/02-purchase-request-core-build-backlog.md
```

Example task sequence:

```md
### TASK-02-001: Define purchase request domain and API records
### TASK-02-002: Implement PurchaseRequestEntity and entity tests
### TASK-02-003: Implement request status view and query tests
### TASK-02-004: Implement purchase request HTTP endpoints and endpoint tests
### TASK-02-005: Implement purchase request UI API client
### TASK-02-006: Implement request form/list/detail UI
### TASK-02-007: Add purchase request full-stack smoke test
```

Each task should read only the relevant solution plan, module spec, sprint spec, backlog, cross-cutting specs, and optional task brief.
