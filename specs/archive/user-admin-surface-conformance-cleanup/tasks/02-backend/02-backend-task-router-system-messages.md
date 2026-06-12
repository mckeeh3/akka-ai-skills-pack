# TASK-UASCC-02-002: Repair backend task routing and typed system-message outcomes

## Intent

Ensure User Admin detail surfaces are inspection/task routers only, and backend action paths return dedicated task/result/system-message surfaces for consequential work and failure states.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/tasks/02-backend/02-backend-task-router-system-messages.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`

## Skills

- `akka-basic-user-admin`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`

## Expected outputs

- Backend routing/result-surface changes for User Detail, Invitation Detail, role/status/support/invitation actions, access review, identity exception, and `surface-user-admin-system-message`.
- Focused backend tests.

## Required checks

```bash
git diff --check
mvn -q -Dtest=WorkstreamServiceTest test
```

## Done criteria

- User Detail and Invitation Detail expose task-entry surface requests and do not represent direct mutation surfaces.
- Role changes route through `surface-user-admin-role-change-preview` before commit.
- Membership/account lifecycle routes through `surface-user-admin-membership-status-confirmation`.
- Support grant/revoke routes through dedicated support-access task surfaces.
- Invitation resend/revoke routes through confirmation surfaces.
- Denied, stale, hidden/not-found, no-op, validation, provider/outbox/model blocked, and unsupported action paths return typed safe system-message surfaces where possible.
- Access-review and identity-exception surfaces expose starter-scope durable task/decision semantics with no direct unauthorized mutation.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: task/action outcomes for role/status/support/invitation/access-review/identity exceptions.
- Role-specific dashboard / surface: user detail, invitation detail, role preview, membership status confirmation, support access grant/revoke, invitation resend/revoke, access review task, identity exception review, system message.
- Surface graph node/action edge: detail -> dedicated task surfaces; command submissions -> detail/result/system-message.
- Governed-tool id and exposure: browser-tool action path backed by User Admin service/tool capabilities.
- Capability id: exact `user_admin.*` capability per action plus audit read.
- AuthContext / roles / tenant scope: backend-selected actor/scope; cross-scope and last-admin/self-action denials fail closed.
- Akka substrate: workstream service, user admin services/views, action result envelopes.
- API / frontend / realtime path: workstream action API consumed by structured surface renderer.
- Audit/work trace requirements: audit/work traces for allow/deny/no-op/failure, correlation id retained, secrets redacted.
- Local validation path: focused Maven test plus diff check.
