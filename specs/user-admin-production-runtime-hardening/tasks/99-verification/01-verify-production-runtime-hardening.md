# TASK-UAPRH-99-001: Verify User Admin production runtime hardening

## Intent

Verify the mini-project done state for provider-backed invitation delivery, production identity exception recovery workflows, and model-backed access-review automation.

## Required reads

- `AGENTS.md`
- `specs/user-admin-production-runtime-hardening/README.md`
- `specs/user-admin-production-runtime-hardening/conversation-capture.md`
- `specs/user-admin-production-runtime-hardening/pending-tasks.md`
- `specs/user-admin-production-runtime-hardening/backlog/01-user-admin-production-runtime-hardening-build-backlog.md`
- outputs from prior tasks
- `specs/archive/user-admin-surface-conformance-cleanup/conformance-verification.md`
- `specs/user-admin-browser-workstream-smoke/browser-smoke-verification.md`

## Skills

- `app-description-readiness-assessment`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `akka-agent-testing`

## Expected outputs

- `specs/user-admin-production-runtime-hardening/production-runtime-verification.md`
- Updated pending queue with done status if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn test
npm --prefix frontend run smoke:user-admin-workstream
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

## Done criteria

- Verification compares completed work against README done state, backlog, app-description, task criteria, and command evidence.
- No feature is marked complete based on fixture-only/model-less/provider-less normal runtime behavior.
- Missing external credentials only skip explicitly optional real-provider smoke tests.
- If gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Vertical workstream contract

- Workstream: User Admin / `agent-user-admin`.
- Attention: invitation delivery, identity exception, access-review blocker/result.
- Surfaces: all production hardening surfaces in scope.
- Tools/capabilities: invitation/outbox, identity recovery, access-review agent/model/tool boundary, audit/work trace.
- AuthContext: App/Tenant/Customer scoped allow/deny.
- Substrate: backend services/components, Akka Agent, workstream/frontend smoke.
- Validation: broad Maven/npm/smoke/build checks.
