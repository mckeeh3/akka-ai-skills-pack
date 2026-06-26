# Audit Trace source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-26
Alignment state: stale-description-changed

This file records candidate source alignment for the v1 tenant-admin audit trace activity-log intent. It is not proof that implementation currently matches the updated app-description.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `audit-trace.v1-activity-log` | `../workstream.md`, `../access.md`, `../behavior.md`, `../surfaces/surfaces.md`, `../tools/governed-tools.md`, `../traces/work-traces.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/audit-and-trace-investigation.md` | Candidate paths: `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/agent/**`, `src/main/java/ai/first/application/foundation/workstream/**`, `src/main/java/ai/first/api/coreapp/**`, `frontend/src/workstream/**`, `frontend/src/api/**` | Candidate paths: `src/test/java/ai/first/application/foundation/audit/**`, `src/test/java/ai/first/application/foundation/agent/**`, `src/test/java/ai/first/application/foundation/workstream/**`, `frontend/src/*audit*contract*.mjs`, `frontend/src/workstream*contract*.mjs` | Description updated from Stage 1 input only; no runtime validation recorded. | Focused build planning must verify whether existing implementation supports v1 tenant-admin activity log, full-payload detail, tool-call linkage, and retention settings. |

## Unmapped current-intent files

- None known for the v1 description slice.

## Unmapped implementation files

- Unknown until a focused source-alignment review is run.

## Alignment notes

- Current state is `stale-description-changed` because the app-description has been narrowed and clarified for v1 after the prior broad Audit/Trace intent.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI validation for the selected scope.
