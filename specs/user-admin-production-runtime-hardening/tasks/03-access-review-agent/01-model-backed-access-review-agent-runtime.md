# TASK-UAPRH-04-001: Implement model-backed access-review agent runtime path

## Intent

Implement the governed model-backed access-review automation path so configured access-review tasks invoke a concrete Akka Agent with model policy, tools, traces, and fail-closed behavior.

## Required reads

- `AGENTS.md`
- production runtime contract from `TASK-UAPRH-01-001`
- `src/main/java/ai/first/application/foundation/agent/**`
- `src/main/java/ai/first/application/coreapp/useradmin/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/test/java/ai/first/application/coreapp/useradmin/**`

## Skills

- `akka-agent-component`
- `akka-agent-tools`
- `akka-agent-tool-boundaries`
- `akka-agent-model-governance`
- `akka-agent-work-trace`
- `akka-agent-testing`
- `akka-basic-user-admin`

## Expected outputs

- Access-review agent runtime integration.
- Tool boundary/model config/fail-closed handling.
- Durable task progress/result updates.
- Tests with deterministic test model provider and missing-config/denial cases.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=UserAdminAccessReviewServiceTest,UserAdminAccessReviewAutonomousAgentTest,WorkstreamServiceTest test
```

## Done criteria

- Configured access review invokes a concrete governed Akka Agent path.
- Missing model/provider/boundary config returns blocked/fail-closed status without fake success.
- Tool/data/policy/model usage emits work traces.
- Agent result informs but does not directly mutate access without human review.

## Vertical workstream contract

- Workstream: User Admin / Access Review agent path under `agent-user-admin`.
- Attention: access review running/result/blocker.
- Surfaces: access-review task/workflow status/result/system-message.
- Tools/capabilities: access-review start/read/accept/reject, user/audit evidence tools, model policy/tool boundary.
- AuthContext: selected scope and data-access authorization.
- Substrate: Akka Agent, service/task state, workstream API.
- Validation: focused Maven tests and diff check.
