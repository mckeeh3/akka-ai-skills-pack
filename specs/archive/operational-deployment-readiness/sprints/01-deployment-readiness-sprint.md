# Sprint 01: Deployment Readiness Baseline

## Goal

Create an operator-ready baseline for environment configuration, health/readiness validation, smoke commands, and runbooks.

## Sequence

1. Inventory env vars, secrets, provider/model/runtime config, and current docs/scripts.
2. Document env config and secret boundaries.
3. Add or document health/readiness diagnostics at the smallest necessary scope.
4. Create deployment/runbook docs and smoke checklist.
5. Validate docs/scripts/checks and verify mini-project completion.

## Expected checks

```bash
git diff --check
env -u ADMIN_USERS mvn test
npm --prefix frontend run smoke:user-admin-workstream
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```
