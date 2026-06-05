# Capabilities Index

This conventional approval-workflow example is reference material for app-description mechanics, not the target generated SaaS foundation. Capability files still use the capability-first contract shape so downstream behavior, tests, security, observability, UI, and Akka planning do not invent semantics.

| Capability | Class | Primary actors/callers | Protected scope | Selected exposure surfaces |
|---|---|---|---|---|
| `purchase-request.submit` | command | employee submitter | request submitter within organization/tenant context | browser UI action, HTTP API |
| `purchase-request.approve` | approval | manager approver | assigned approver within organization/tenant context | browser UI action, HTTP API, workflow step |
| `purchase-request.reject` | approval | manager approver | assigned approver within organization/tenant context | browser UI action, HTTP API, workflow step |
| `purchase-request.view-status` | read/evidence | employee submitter, manager approver, operations reviewer | scoped request or approval queue | browser UI query, HTTP API, view/query |

Capability contract files:
- `01-submit-and-approve-purchase-requests.md`
