# TASK-UAPRH-05-001: Run integrated User Admin production runtime tests and update smoke coverage

## Intent

Validate the invitation delivery, identity recovery, and access-review agent paths together through backend/workstream/frontend smoke coverage.

## Required reads

- `AGENTS.md`
- `specs/user-admin-production-runtime-hardening/README.md`
- outputs from prior implementation tasks
- `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`
- `specs/user-admin-browser-workstream-smoke/smoke-command.md`
- relevant frontend/backend tests

## Skills

- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `akka-agent-testing`

## Expected outputs

- Updated integrated tests/smoke coverage.
- Documentation notes for provider/model credential optional smoke behavior.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,InvitationAndUserAdminServiceTest,UserAdminAccessReviewAutonomousAgentTest,UserAdminBrowserWorkstreamSmokeTest test
npm --prefix frontend run smoke:user-admin-workstream
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

## Done criteria

- Integrated tests cover representative success and fail-closed paths for all three production hardening areas.
- User Admin smoke command remains passing and includes updated production runtime assertions where safe.
- Optional real-provider/model smoke behavior is documented and skipped without credentials.

## Vertical workstream contract

- Workstream: User Admin / `agent-user-admin`.
- Attention: invitation delivery, identity exception, access review.
- Surfaces: dashboard/list/detail/task/workflow/system-message.
- Tools/capabilities: invitation, identity, access-review, audit/work-trace, model/tool boundaries.
- AuthContext: selected scope and hidden-target denials.
- Substrate: backend services, Akka Agent, workstream API, frontend smoke.
- Validation: integrated Maven/npm/smoke checks.
