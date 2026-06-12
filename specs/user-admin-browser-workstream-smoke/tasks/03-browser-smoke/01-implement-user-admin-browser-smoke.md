# TASK-UABWS-03-001: Implement User Admin browser/workstream smoke tests

## Intent

Implement the selected smoke approach so representative User Admin structured-surface flows are validated through the local hosted UI/workstream API path.

## Required reads

- `AGENTS.md`
- `specs/user-admin-browser-workstream-smoke/README.md`
- `specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md`
- `specs/user-admin-browser-workstream-smoke/tasks/03-browser-smoke/01-implement-user-admin-browser-smoke.md`
- files changed by `TASK-UABWS-02-001`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/main.tsx`
- `src/main/java/ai/first/api/coreapp/workstream/**`

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `akka-web-ui-accessibility-responsive`

## Expected outputs

- Browser/workstream smoke test code or script.
- Smoke assertions for User Admin dashboard/list/detail/task/system-message flows.

## Required checks

Use the smoke command selected by the survey, plus:

```bash
git diff --check
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

If the smoke is Maven-driven, include the focused Maven command specified by the implementation.

## Done criteria

- Smoke loads the hosted app/workstream shell.
- Smoke opens User Admin and User Directory through the real path.
- Smoke opens read-only User Detail or Invitation Detail.
- Smoke opens at least one dedicated task/confirmation surface.
- Smoke validates a representative typed `system_message` denied/blocked path.
- Smoke asserts no visible raw secrets/tokens/provider ids in the tested UI.
- Smoke can be run repeatably by a future task/session.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: smoke validates dashboard attention/action routing and task outcomes.
- Role-specific dashboard / surface: User Admin dashboard, User Directory, User Detail/Invitation Detail, task surface, system message.
- Surface graph node/action edge: dashboard -> users -> row detail -> task/system-message.
- Governed-tool id and exposure: browser-tool actions submitted through workstream UI/API.
- Capability id: User Admin capabilities required by smoke actor plus denial capability for blocked path.
- AuthContext / roles / tenant scope: deterministic smoke selected AuthContext; hidden target denial where applicable.
- Akka substrate: hosted frontend endpoint, workstream endpoint, backend services/views.
- API / frontend / realtime path: real local browser/UI path or documented equivalent scriptable UI path.
- Audit/work trace requirements: smoke checks trace/redaction visibility at safe level.
- Local validation path: smoke command plus frontend tests/typecheck/diff check.
