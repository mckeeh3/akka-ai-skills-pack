# TASK-UASCC-03-001: Repair frontend structured rendering and retire legacy admin page

## Intent

Update frontend User Admin rendering so it consumes backend-authored structured surfaces, removes inline mutations from detail views, supports canonical surface types, and retires or absorbs legacy Admin Users page behavior.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/tasks/03-frontend/01-frontend-conformance-and-legacy-retirement.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream/types/surfaces.ts`
- `frontend/src/screens/admin/AdminUsersPage.tsx`
- `frontend/src/main.tsx`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`

## Skills

- `akka-web-ui-apps`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Expected outputs

- Frontend User Admin surface renderer changes.
- Legacy page/route/test cleanup or explicit retirement path.
- Frontend contract tests updated.

## Required checks

```bash
git diff --check
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

## Done criteria

- `SurfaceRenderer` and specialized User Admin surfaces render canonical surface types without generic JSON fallback for required User Admin surfaces.
- User Detail and Invitation Detail render inspection/task entry points only; no inline role/status/support/invitation mutation forms remain.
- Dashboard uses backend-authored attention/population/action payloads rather than deriving queues from generic cards/sections for normal User Admin runtime.
- User Directory row activation uses backend-authored action/surface metadata; frontend inference is limited to explicit legacy compatibility and not the normal path.
- User Admin task forms consume backend-provided role/expiry/policy options.
- Default UI hides raw implementation ids/correlation/capability/trace diagnostics unless surfaced as role-gated diagnostic/audit drilldown.
- `AdminUsersPage` is removed, routed to structured surfaces, or clearly quarantined outside normal runtime with tests adjusted.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin` frontend workstream shell.
- Attention category or non-attention reason: dashboard attention and task surfaces rendered for human admins.
- Role-specific dashboard / surface: User Admin dashboard, User Directory, User Detail, Invitation Detail, task/decision/system-message surfaces.
- Surface graph node/action edge: dashboard/list/detail/task frontend rendering and browser-tool action submissions.
- Governed-tool id and exposure: browser-tool actions from backend envelopes only.
- Capability id: surfaced as backend metadata, not frontend authority.
- AuthContext / roles / tenant scope: frontend visibility advisory only; no hidden counts/targets inferred.
- Akka substrate: frontend React/Vite workstream UI.
- API / frontend / realtime path: workstream shell and `/api/workstream` clients.
- Audit/work trace requirements: browser-safe trace links/drilldowns only, no secrets or raw provider data.
- Local validation path: frontend tests, typecheck, diff check.
