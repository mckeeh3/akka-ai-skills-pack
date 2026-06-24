# Runtime Feature Verification

## Scope

- Task: `AADE-07-001` full-stack closure re-verification after `AADE-06-001` and `AADE-06-002` follow-up repairs.
- Workstream: Agent Admin doc editing (`agent-doc-administration`).
- Intent sources: `README.md`, `conversation-capture.md`, `app-description/domains/core-starter/workstreams/agent-admin/**`, and `app-description/domains/core-starter/capabilities/agent-doc-administration.md`.
- Queue result: mini-project is **not closed**. Follow-up task `AADE-08-001` and terminal re-verification task `AADE-09-001` were appended.

## First-runnable confirmation

- `AADE-06-001` is marked `done` with note `completed in this commit with message: Repair Agent Admin workstream smoke and stale tests`.
- `AADE-06-002` is marked `done` with note `completed in this commit with message: Repair collateral authorization smokes`; parent inspection identified commit `def23f05` for this task.
- `AADE-07-001` was the first non-`done` task in `pending-tasks.md`; its dependencies, `AADE-06-001` and `AADE-06-002`, were satisfied.
- `AADE-07-001` was marked `in-progress` before verification edits and is now marked `done` as a completed terminal verification pass with follow-up queue entries.

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
|---|---|---|---|---|---|
| SaaS-admin-only Agent Admin access | `access.md`, `capabilities/agent-doc-administration.md`, `tests/coverage.md` | Bearer token + selected `AuthContext` -> protected `/api/workstream/*` -> Agent Admin workstream actions -> safe denial surfaces | `mvn test` completed 434 tests with only one failure; prior stale tenant/governance and collateral authorization failures from `AADE-05-001` were absent. Isolated `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test` passed, including missing-token and tenant-admin denial checks. | `api-smoked` | Full-suite execution still fails in the same smoke before completing the end-to-end Agent Admin action chain. |
| Backend agent/doc browsing through protected workstream API | `surfaces.md`, `realization/api-contracts.md`, `tools/governed-tools.md` | `action-agent-admin-show-agents` / `list-agent-doc-agents` -> `surface-agent-admin-agent-list` -> detail/doc action chain | Isolated `AgentAdminBrowserWorkstreamSmokeTest` passed and exercised non-empty SaaS Owner/Admin list/detail/prompt/edit/save/history/diff/restore/create/delete/trace paths. Full `mvn test` failed at `AgentAdminBrowserWorkstreamSmokeTest.java:100` because the `User Admin` filtered `rows` list was empty. | `api-smoked` | Not stable under full-suite execution; appears order-dependent because the same test passes when run alone. |
| Prompt/skill/reference versioning, current-version-only edit, adjacent diff, restore, Save/Cancel, permanent skill/reference delete/cascade | `behavior.md`, `surfaces.md`, `tests/coverage.md`, `realization/api-contracts.md` | Agent Admin doc-admin service and workstream actions -> versioned document state -> structured surfaces | `mvn test` reached 434 tests with no service-layer Agent Admin doc-admin failures. Isolated smoke passed through prompt edit/save/history/diff/restore and skill/reference create/delete action paths. | `api-smoked` | Full-suite smoke failure blocks closure even though isolated path works. |
| Editing-agent runtime/fail-closed behavior | `agents/functional-agent.md`, `behavior.md`, `tests/coverage.md` | `AgentAdminDocEditingRuntime` / `AgentAdminDocEditingAgent` with Akka test provider and provider-missing fail-closed behavior | `mvn test` reported `AgentAdminDocEditingAgentTest` passed 2 tests. No provider secrets were required; missing normal provider remains fail-closed. | `backend-ready` | No real provider-backed local smoke was available; this is acceptable fail-closed evidence but not a provider-configured runtime smoke. |
| Runtime prompt + skill descriptor loading and `readSkill` / `readReferenceDoc` traces | `behavior.md`, `traces/work-traces.md`, `tests/coverage.md` | Runtime agent loading/tool resolver/trace sink -> Agent Admin trace surfaces | Full `mvn test` did not report runtime loader or trace test failures. Isolated Agent Admin smoke passed the runtime trace surface action. | `api-smoked` | Full-suite smoke failure prevents marking the overall mini-project `runtime-ready`. |
| Frontend current Agent Admin surfaces and stale governance UI cleanup | `surfaces.md`, `realization/frontend-routes.md`, `tests/coverage.md` | React/Vite frontend contract tests, typecheck, and production build | `npm --prefix frontend test -- --run` passed 180 tests, including Agent Admin doc-editing fixture/API/surface/router coverage. `npm --prefix frontend run typecheck` passed. `npm --prefix frontend run build` passed with existing chunk-size warnings only. | `frontend-rendered` | Frontend evidence remains contract/render/build-level; no browser automation against a locally running full stack was run in this task. |
| Full-stack local runtime readiness | `README.md` done state, `tasks/07-validation/01-reverify-fullstack-closure.md` | Full Maven suite + frontend tests/typecheck/build + diff check | Frontend checks and `git diff --check` passed. `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test` passed. Full `mvn test` failed with 1 failure, 0 errors, 2 skipped. | `api-smoked` | Not `runtime-ready`: full suite is still red due the order-dependent Agent Admin protected workstream smoke failure. |

## Maven failure inventory from `mvn test`

`mvn test` failed after 434 tests, with 1 failure, 0 errors, and 2 skipped:

- `AgentAdminBrowserWorkstreamSmokeTest.protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions` failed at line 100: `action-agent-admin-show-agents` returned an empty `rows` list for the `User Admin` filter during full-suite execution.

The same test passed when run alone with `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test`, which points to a full-suite/order-dependent state isolation or seeding gap rather than a simple missing isolated API path.

## Result

- readiness level: `api-smoked` for isolated Agent Admin protected API paths and `frontend-rendered` for browser contracts/build.
- runtime-ready: no.
- mini-project closed: no.

The `AADE-06-*` repairs removed the prior stale governance and collateral authorization failures from the full-suite inventory, but terminal closure remains blocked by the full-suite-only Agent Admin protected workstream smoke failure.

## Required repairs

- app-description gaps: none identified in the Agent Admin intent read for this verification pass.
- implementation/test gaps:
  - Repair the full-suite/order-dependent `AgentAdminBrowserWorkstreamSmokeTest` empty `User Admin` filtered rows condition while preserving SaaS-admin-only access and current doc-editing API coverage.
- provider/config blockers:
  - No real model/provider smoke was run. Editing-agent tests use Akka `TestModelProvider` for deterministic contract validation, and missing runtime/provider behavior is verified as fail-closed.
- queue changes:
  - Added `AADE-08-001` for Agent Admin full-suite smoke isolation repair.
  - Added `AADE-09-001` as the next terminal closure verification task.

## Checks run

- `mvn test` — failed. 434 tests run; 1 failure, 0 errors, 2 skipped. Failure: `AgentAdminBrowserWorkstreamSmokeTest.protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions:100` empty filtered Agent Admin rows in full-suite execution.
- `mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest' test` — passed. 1 test run; protected Agent Admin workstream/API smoke passed in isolation.
- `npm --prefix frontend test -- --run` — passed. 180 frontend tests passed.
- `npm --prefix frontend run typecheck` — passed. `tsc --noEmit` completed.
- `npm --prefix frontend run build` — passed. Vite production build completed; warnings only for existing chunk-size advisory.
- `git diff --check` — passed.
- `python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/agent-admin-doc-editing-realization/pending-tasks.md` — passed.
- `bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/agent-admin-doc-editing-realization/pending-tasks.md` — passed.

## Next step

Execute `AADE-08-001` next. Do not close the mini-project until `AADE-09-001` re-runs full terminal validation and records `runtime-ready` evidence or another bounded follow-up loop.
