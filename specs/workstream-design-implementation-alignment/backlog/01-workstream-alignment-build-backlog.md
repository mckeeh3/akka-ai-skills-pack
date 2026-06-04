# Backlog: Workstream Design/Implementation Alignment

## Goal

Close targeted design-to-implementation gaps identified by the workstream audit without redesigning the app or claiming full-core readiness prematurely.

## Implementation notes

- Treat `app-description/12-workstreams/**` as authoritative application meaning.
- Treat `app-description/55-ui/**` as browser realization, not a duplicate source of workstream meaning.
- Treat backend `WorkstreamService`/`WorkstreamEndpoint` and frontend `frontend/src/workstream/**` as current runtime implementation, not throwaway examples.
- Preserve all runtime completion and fail-closed rules from `AGENTS.md`.

## Suggested harness task breakdown

1. Create this mini-project scaffold and queue.
2. Add canonical id/alias traceability mapping across app-description, backend, frontend, and tests.
3. Expand surface/action-to-governed-tool mappings for core workstream actions.
4. Implement deterministic default dashboard loading/selection behavior and tests.
5. Add backend-authoritative prompt/surface alias resolution for common workstream requests.
6. Align realtime/SSE v1 semantics in docs/code/tests.
7. Refresh readiness docs and classify legacy page-style frontend artifacts.
8. Verify mini-project completion or append follow-up tasks.

## Dependencies

- Task 2 should precede tasks 3-7.
- Task 3 should precede prompt/surface alias implementation when aliases need exact governed-tool ids.
- Runtime/UI tasks depend on the relevant contract tasks.

## Required checks

Use the smallest checks that prove each task. Common checks:

```bash
git diff --check
mvn test -Dtest=WorkstreamServiceTest,WorkstreamRuntimeAgentTest
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

A task may use narrower checks if its brief states why they prove the bounded scope.

## Acceptance criteria

- App-description and implementation ids are connected by explicit traceability maps.
- Surface actions map to exact governed-tool ids and exposure labels.
- Workstream dashboard/surface request behavior is backend-authoritative, deterministic, and tested.
- Realtime semantics are explicit and tested.
- Readiness docs accurately distinguish implemented starter behavior from full-core gaps.
- Verification task records completion or appends new bounded work plus a new terminal verification task.
