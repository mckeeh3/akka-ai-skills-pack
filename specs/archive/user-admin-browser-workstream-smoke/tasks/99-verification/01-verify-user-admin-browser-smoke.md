# TASK-UABWS-99-001: Verify User Admin browser workstream smoke mini-project

## Intent

Verify the browser/workstream smoke mini-project against its done state, command evidence, and app-description/User Admin conformance artifacts. Append follow-up tasks plus a new terminal verification task if material gaps remain.

## Required reads

- `AGENTS.md`
- `specs/user-admin-browser-workstream-smoke/README.md`
- `specs/user-admin-browser-workstream-smoke/conversation-capture.md`
- `specs/user-admin-browser-workstream-smoke/pending-tasks.md`
- `specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md`
- `specs/user-admin-browser-workstream-smoke/tasks/99-verification/01-verify-user-admin-browser-smoke.md`
- `specs/archive/user-admin-surface-conformance-cleanup/conformance-verification.md`
- smoke docs/outputs produced by prior tasks

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `app-description-readiness-assessment`

## Expected outputs

- `specs/user-admin-browser-workstream-smoke/browser-smoke-verification.md`
- Updated pending queue with done status if complete, or appended follow-up tasks plus a new terminal verification task if gaps remain.

## Required checks

Run the smoke command established by prior tasks, plus relevant confidence checks such as:

```bash
git diff --check
env -u ADMIN_USERS mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

If any broad check is not run, record a clear reason and whether that blocks completion.

## Done criteria

- Verification compares completed work against README done state, backlog, task criteria, and app-description/conformance evidence.
- Verification records command evidence for the smoke command and required checks.
- If no material gaps remain, mark mini-project complete.
- If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: terminal verification of smoke coverage for attention/action/task/system-message flows.
- Role-specific dashboard / surface: all smoke-covered User Admin surfaces.
- Surface graph node/action edge: dashboard -> users -> detail/task/system-message verified.
- Governed-tool id and exposure: browser-tool/workstream action path verified.
- Capability id: User Admin capability and denial behavior verified.
- AuthContext / roles / tenant scope: deterministic smoke AuthContext and hidden-target denial behavior verified.
- Akka substrate: hosted frontend, workstream endpoint, backend services/views, smoke test harness.
- API / frontend / realtime path: local UI/API smoke command.
- Audit/work trace requirements: safe trace/redaction assertions verified.
- Local validation path: smoke command plus listed checks.
