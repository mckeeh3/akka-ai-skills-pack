# TASK-UAPRH-02-002: Add invitation delivery surfaces, tests, and smoke docs

## Intent

Wire hardened invitation delivery state into User Admin workstream/frontend surfaces and smoke documentation.

## Required reads

- prior task changes for `TASK-UAPRH-02-001`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/**`
- `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`
- `specs/user-admin-browser-workstream-smoke/smoke-command.md`

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `akka-resend-email-service`

## Expected outputs

- Workstream/frontend delivery-state rendering updates.
- Tests/smoke docs for provider-backed invitation delivery states.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminBrowserWorkstreamSmokeTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

## Done criteria

- Invitation detail and action result surfaces show delivery status/retry/recovery without secrets.
- Provider-blocked state renders typed `system_message`/surface state.
- Browser smoke or smoke docs include invitation delivery coverage and real-provider skip behavior.

## Vertical workstream contract

- Workstream: User Admin / `agent-user-admin`.
- Attention: invitation delivery failure/retry.
- Surfaces: invitation detail/resend/revoke/system-message.
- Tool/capability: invitation browser-tools and `user_admin.*invitation*` capabilities.
- AuthContext: selected scope, no hidden users/invites.
- Substrate: workstream service, frontend renderer, smoke test.
- Validation: Maven, frontend tests/typecheck, diff check.
