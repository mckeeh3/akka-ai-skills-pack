# TASK-UABWS-00-001: Create User Admin browser smoke planning scaffold

## Intent

Create the mini-project scaffold and pending queue for User Admin browser/workstream smoke automation beyond starter-scope contract tests.

## Required reads

- `AGENTS.md`
- `specs/user-admin-browser-workstream-smoke/README.md`
- `specs/user-admin-browser-workstream-smoke/conversation-capture.md`
- `specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md`

## Expected outputs

- Mini-project README, conversation capture, sprint, backlog, task briefs, and pending queue.

## Required checks

```bash
git diff --check
```

## Done criteria

- Queue has a runnable first non-done task.
- Terminal verification task can append follow-up tasks and a new terminal verification task if gaps remain.
- Planning scaffold is committed without staging unrelated work.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin` runtime workstream.
- Attention category or non-attention reason: planning only; no runtime attention item.
- Role-specific dashboard / surface: User Admin dashboard, User Directory, User Detail, task and system-message surfaces.
- Surface graph node/action edge: planning for browser smoke traversal.
- Governed-tool id and exposure: none at planning scope.
- Capability id: planning inherits User Admin capability set.
- AuthContext / roles / tenant scope: preserve selected AuthContext and tenant/customer authorization in planned tests.
- Akka substrate: docs/specs only.
- API / frontend / realtime path: planned hosted frontend/workstream API smoke path.
- Audit/work trace requirements: planned assertions for trace/redaction visibility.
- Local validation path: `git diff --check`.
