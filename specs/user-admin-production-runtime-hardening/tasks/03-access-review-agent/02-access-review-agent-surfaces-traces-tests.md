# TASK-UAPRH-04-002: Add access-review agent surfaces, traces, and tests

## Intent

Expose model-backed access-review automation progress/results/fail-closed states through User Admin workstream/frontend surfaces with trace-safe evidence.

## Required reads

- prior task changes for `TASK-UAPRH-04-001`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/surfaces/**`
- `src/test/java/ai/first/application/coreapp/workstream/**`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Skills

- `akka-agent-work-trace`
- `akka-agent-testing`
- `akka-web-ui-testing`
- `akka-http-endpoint-testing`

## Expected outputs

- Workstream/frontend access-review surface updates.
- Trace/evidence rendering changes.
- Backend/frontend tests.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest,UserAdminAccessReviewAutonomousAgentTest,UserAdminBrowserWorkstreamSmokeTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

## Done criteria

- Access-review task surface shows model-backed progress/result or typed blocked state.
- Human accept/reject remains explicit and routes mutations through deterministic task surfaces.
- Trace links summarize model/tool/data/policy usage without exposing prompts/provider secrets.
- Smoke/contract tests cover result and blocked states.

## Vertical workstream contract

- Workstream: User Admin / `agent-user-admin` plus access-review agent runtime.
- Attention: access-review result/blocker.
- Surfaces: access-review workflow/status, outcome/system-message, trace drilldowns.
- Capabilities: access-review and audit/work-trace read capabilities.
- AuthContext: scoped evidence only.
- Substrate: workstream service, frontend renderer, agent traces.
- Validation: Maven, frontend tests/typecheck, diff check.
