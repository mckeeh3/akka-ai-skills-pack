# Sprint 02: Backend Service and Protected API

## Goal

Implement the backend-authorized Organization Admin substrate and protected Admin API routes.

## Scope

- Add tenant list/search repository support.
- Add SaaS Owner Organization Admin service with authorization, idempotency, validation, no-op handling, and audit events.
- Add protected API routes using Organization-facing DTOs.
- Add focused backend and endpoint tests.

## Acceptance

- SaaS Owner Admin can list/read/create/rename/suspend/reactivate Organizations through backend service/API paths.
- Tenant Admin and Customer Admin are denied safely.
- Missing capabilities, hidden targets, cross-scope attempts, duplicate/idempotent mutations, and no-op updates behave safely and are audited.
- API responses do not expose tenant application data, billing/provider secrets, or hidden cross-tenant facts.
