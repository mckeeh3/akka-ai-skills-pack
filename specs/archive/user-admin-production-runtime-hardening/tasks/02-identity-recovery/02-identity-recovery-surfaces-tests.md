# TASK-UAPRH-03-002: Wire identity recovery surfaces and tests

## Intent

Expose durable identity exception recovery through User Admin workstream/frontend surfaces with safe review, approval/denial, completion, and status behavior.

## Required reads

- prior task changes for `TASK-UAPRH-03-001`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `src/test/java/ai/first/application/coreapp/workstream/**`

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `akka-basic-user-admin`

## Expected outputs

- Workstream/frontend identity recovery surface changes.
- Backend/frontend tests.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminBrowserWorkstreamSmokeTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

## Done criteria

- Identity exception surface shows durable lifecycle/status and approval/denial/completion actions where authorized.
- Denied/hidden/provider-boundary states return safe typed surfaces.
- Tests prove no raw WorkOS ids/JWTs/provider payloads are exposed.

## Vertical workstream contract

- Workstream: User Admin / `agent-user-admin`.
- Attention: identity exception review/recovery.
- Surfaces: identity exception review, workflow/status, system-message.
- Capability: `user_admin.identity_relink.*`.
- AuthContext: role/scope-specific allow/deny.
- Substrate: workstream service/frontend renderer/smoke.
- Validation: Maven, frontend tests/typecheck, diff check.
