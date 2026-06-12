# TASK-UABWS-02-001: Implement deterministic local smoke setup

## Intent

Provide the minimal safe local runtime setup needed for User Admin browser/workstream smoke tests to run without external provider credentials or production auth weakening.

## Required reads

- `AGENTS.md`
- `specs/user-admin-browser-workstream-smoke/README.md`
- `specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md`
- `specs/user-admin-browser-workstream-smoke/tasks/02-backend-fixtures/01-deterministic-local-smoke-setup.md`
- relevant backend/frontend files identified by the survey
- `src/main/java/ai/first/application/foundation/identity/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/test/java/ai/first/**`

## Skills

- `akka-basic-user-admin`
- `akka-http-endpoint-testing`
- `akka-workos-user-auth`

## Expected outputs

- Test-only seed/config/helper code or documented no-code setup.
- Focused tests proving setup is fail-closed outside test mode if code changes are needed.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest test
```

## Done criteria

- Smoke setup can produce an authorized User Admin context locally without external WorkOS/Resend/model credentials.
- Setup does not allow Tenant/Customer admin production bootstrap through `ADMIN_USERS` or expose hidden data.
- Any test-only bypass is explicit, scoped, and unavailable in normal production runtime.
- Next browser smoke task can run against deterministic data.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: test setup for browser smoke; no production attention item.
- Role-specific dashboard / surface: User Admin dashboard/list/detail smoke setup.
- Surface graph node/action edge: enables smoke traversal without changing product graph semantics.
- Governed-tool id and exposure: workstream/browser-tool path remains backend-authorized.
- Capability id: User Admin read/task capabilities needed by smoke actor.
- AuthContext / roles / tenant scope: deterministic SaaS Owner/Tenant/Customer scope as selected by survey; production fail-closed behavior preserved.
- Akka substrate: backend test setup, identity/bootstrap/workstream services if needed.
- API / frontend / realtime path: setup supports local hosted UI/API smoke.
- Audit/work trace requirements: setup preserves audit/trace emission and redaction.
- Local validation path: focused Maven test plus diff check.
