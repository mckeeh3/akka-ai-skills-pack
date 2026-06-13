# Build Backlog: Operational Deployment Readiness

## Objective

Make the app deployable and diagnosable by operators without relying on implicit developer knowledge.

## Work item 1: Environment and secret inventory

Survey code/docs for environment variables and runtime configuration, including:

- WorkOS/AuthKit frontend/public config and backend JWT/auth config;
- Resend email provider config;
- model provider/model config policy;
- `ADMIN_USERS` bootstrap behavior;
- frontend static asset build/hosting behavior;
- smoke/test mode caveats.

## Work item 2: Environment docs and validation

Create/update docs that classify required vs optional env vars, frontend-public vs backend-secret values, local/test/prod guidance, and fail-closed behavior. Add a validation script or test if practical.

## Work item 3: Health/readiness diagnostics

Document or implement health/readiness checks for:

- app startup and hosted frontend;
- `/api/me` auth boundary;
- workstream API readiness;
- email provider readiness/fail-closed state;
- model provider/access-review readiness/fail-closed state;
- audit/trace persistence expectations.

## Work item 4: Deployment and operations runbook

Create an operator runbook covering startup, smoke commands, deployment validation, rollback, troubleshooting, and external-provider credentialed smoke expectations.

## Work item 5: Verification

Run/review checks, compare against README done state, append follow-up tasks if gaps remain.

## Suggested task breakdown

- `TASK-ODR-00-001`: create planning scaffold.
- `TASK-ODR-01-001`: inventory env/config/docs/scripts.
- `TASK-ODR-02-001`: document env/secret configuration and add validation.
- `TASK-ODR-03-001`: document or implement health/readiness diagnostics.
- `TASK-ODR-04-001`: create deployment runbook and smoke checklist.
- `TASK-ODR-99-001`: terminal verification.
