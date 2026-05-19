# TASK-CORE-06-001: Create full-core acceptance and security test matrix

## Purpose

Create a single matrix of required backend, frontend, agent-runtime, audit/governance, and security tests for full core generation readiness.

## Required reads

- `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- all module slice specs created in this migration
- `docs/agent-coverage-matrix.md`
- `docs/web-ui-quality-checklist.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/full-core-acceptance-test-matrix.md`

## Required checks

- Matrix covers `/api/me`, invitation lifecycle, admin views, tenant isolation, Agent Admin, `readSkill`, tool boundaries, traces, governance approvals, workstream surfaces, and frontend secret boundary.
- `git diff --check`

## Done criteria

- Full-core readiness has one authoritative test checklist.
- Queue status and changes are committed.
