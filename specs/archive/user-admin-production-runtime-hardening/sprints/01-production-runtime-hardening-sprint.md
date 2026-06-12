# Sprint 01: Production Runtime Hardening

## Goal

Implement production-ready runtime paths for User Admin invitation delivery, identity exception recovery, and model-backed access-review automation while preserving existing structured surfaces and browser smoke coverage.

## Sequence

1. Align app-description/spec decisions for production runtime behavior.
2. Harden provider-backed invitation delivery and outbox recovery.
3. Implement durable identity exception recovery workflow and surfaces.
4. Implement governed model-backed access-review automation.
5. Integrate frontend/workstream surfaces and full-stack tests across the three areas.
6. Verify mini-project done state and append follow-up tasks if gaps remain.

## Expected validation

Common commands:

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,InvitationAndUserAdminServiceTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run smoke:user-admin-workstream
```

Terminal verification should consider broader checks:

```bash
env -u ADMIN_USERS mvn test
npm --prefix frontend run build
```
