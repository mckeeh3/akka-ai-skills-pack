# TASK-UASCC-04-001: Add full-stack User Admin surface conformance tests

## Intent

Add or repair backend/frontend tests proving the implemented User Admin workstream surfaces follow the conformance cleanup decisions through the real local workstream/API/frontend paths at the stated starter scope.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/conversation-capture.md`
- `specs/user-admin-surface-conformance-cleanup/tasks/04-tests/01-fullstack-user-admin-conformance-tests.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- relevant backend/frontend files changed by prior tasks

## Skills

- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `akka-basic-user-admin`

## Expected outputs

- Backend tests for canonical payloads, row/action routing, task/result surfaces, typed system messages, and safe denials.
- Frontend tests for canonical rendering, no inline mutation detail, backend-authored dashboard/list behavior, legacy page retirement, diagnostics redaction, and frontend secret boundary.

## Required checks

```bash
git diff --check
mvn -q -Dtest=WorkstreamServiceTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

## Done criteria

- Tests prove detail surfaces are inspection/task routers and consequential actions use dedicated surfaces.
- Tests prove dashboard/list routing is backend-authored.
- Tests prove typed system-message behavior for at least representative denied, hidden/not-found, no-op/stale, and blocked paths.
- Tests prove backend-shaped options are rendered/used.
- Tests prove legacy Admin Users page is not the normal runtime path.
- Tests prove trace/audit redaction and no frontend secret leakage.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: validation for dashboard attention, task outcomes, denials, and system messages.
- Role-specific dashboard / surface: all User Admin conformance surfaces in scope.
- Surface graph node/action edge: dashboard -> list -> detail/task -> result/system-message.
- Governed-tool id and exposure: browser-tool/workstream action test coverage.
- Capability id: `user_admin.*`, `saas_owner.organization.*`, `admin.audit.read` as applicable.
- AuthContext / roles / tenant scope: App Admin/Tenant Admin/Customer Admin/Auditor allow/deny expectations.
- Akka substrate: backend workstream service tests plus frontend contract tests.
- API / frontend / realtime path: local workstream action/surface and frontend renderer paths.
- Audit/work trace requirements: audit/trace/correlation/redaction assertions.
- Local validation path: Maven focused tests, frontend tests, typecheck, diff check.
