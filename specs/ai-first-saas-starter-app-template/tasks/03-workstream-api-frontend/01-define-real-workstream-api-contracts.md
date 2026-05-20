# TASK-STARTER-03-001: Define real workstream browser API contracts

## Purpose

Turn the current fixture-friendly frontend contracts into explicit real backend API contracts for the starter app.

## Required reads

- `docs/workstream-ui-reference-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/web-ui-api-contract-patterns.md`
- `specs/core-app-full-stack-readiness/core-workstream-api-contracts.md`
- `frontend/src/api/WorkstreamApiClient.ts`
- `frontend/src/workstream/types/**`

## Expected outputs

- `specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md`
- Minimal contract updates if needed to align frontend/backend DTOs.

## Done criteria

- Workstream bootstrap, surface query, capability action, and realtime/stale event contracts are implementation-ready.
- Contracts specify auth, tenancy, trace/audit, idempotency, and denial behavior.
- Queue status is updated and changes are committed.
