# Runtime Feature Verification

## Scope

- Task: `AADE-05-001` full-stack validation and terminal verification.
- Workstream: Agent Admin doc editing (`agent-doc-administration`).
- Intent sources: `README.md`, `conversation-capture.md`, `app-description/domains/core-starter/workstreams/agent-admin/**`, and `app-description/domains/core-starter/capabilities/agent-doc-administration.md`.
- Queue result: mini-project is **not closed**. Follow-up tasks `AADE-06-001`, `AADE-06-002`, and terminal re-verification task `AADE-07-001` were appended.

## First-runnable confirmation

- `AADE-04-003` is marked `done` with note `completed in this commit with message: Implement Agent Admin frontend edit flows`.
- `AADE-05-001` was the first `pending` task in `pending-tasks.md`; its only dependency, `AADE-04-003`, was satisfied.
- `AADE-05-001` was marked `in-progress` before verification edits and is now marked `done` as a completed verification pass with follow-up queue entries.

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
|---|---|---|---|---|---|
| SaaS-admin-only Agent Admin access | `access.md`, `capabilities/agent-doc-administration.md`, `coverage.md` | `AuthContext`/membership -> `AgentAdminDocAdministrationService`/workstream surfaces/actions | `AgentAdminDocAdministrationServiceTest` passed inside `mvn test` (6 tests). `WorkstreamServiceTest.customerAdminCannotAccessAgentAdminWorkstreamOrLegacyBehaviorManagement` now observes `agent-admin-requires-saas-owner-admin`, confirming current denial posture but stale expected reason remains. | `backend-ready` | Full `mvn test` fails because stale backend/workstream tests still expect tenant-scoped/governance Agent Admin behavior and because some SaaS-owner API smoke paths are not proven. |
| Backend agent/doc browsing | `surfaces.md`, `api-contracts.md`, `coverage.md` | Agent Admin workstream action `list-agent-doc-agents` -> backend service -> structured agent list/detail/doc surfaces | `AgentAdminDocAdministrationServiceTest.saasOwnerListsFiltersAndReadsAgentDocContracts` passed. `AgentAdminBrowserWorkstreamSmokeTest` reached protected Akka workstream API but `action-agent-admin-show-agents` returned an empty list for `User Admin`. | `backend-ready` | Not `api-smoked`: protected workstream/API smoke fails at `AgentAdminBrowserWorkstreamSmokeTest.java:100` (`rows.isEmpty()` unexpectedly true). |
| Prompt/skill/reference versioning, current-version-only edit, adjacent diff, restore, Save/Cancel, permanent skill/reference delete/cascade | `behavior.md`, `surfaces.md`, `coverage.md`, `api-contracts.md` | Service-layer doc administration -> version state/lifecycle -> workstream DTOs | `AgentAdminDocAdministrationServiceTest` passed tests for save/cancel/restore, stale save rejection, lifecycle deletion cascade, runtime trace rows, and SaaS owner profile updates. | `backend-ready` | Protected API/browser action round trips remain blocked by the Agent Admin workstream smoke failure. |
| Editing-agent runtime/fail-closed behavior | `agents/functional-agent.md`, `behavior.md`, `coverage.md` | `AgentAdminDocEditingRuntime` / `AgentAdminDocEditingAgent` via Akka test provider and fail-closed runtime | `AgentAdminDocEditingAgentTest` passed (2 tests): model-backed test provider draft/revise/save/cancel path and missing runtime fail-closed path. | `backend-ready` | Test provider validates deterministic contracts only. No normal local provider-backed runtime smoke was available; real provider absence is expected to fail closed. |
| Runtime prompt + skill descriptor loading and `readSkill` / `readReferenceDoc` traces | `behavior.md`, `traces/work-traces.md`, `coverage.md` | Runtime agent service/tool resolver/trace sink | `AgentRuntimeServiceTest` (33 tests), `AgentRuntimeToolResolverTest` (17 tests), and `AgentRuntimeTraceSinkTest` (2 tests) passed inside `mvn test`. | `backend-ready` | Not `runtime-ready`: end-to-end Agent Admin trace surface smoke remains tied to failing workstream/API smoke. |
| Frontend current Agent Admin surfaces and stale governance UI cleanup | `surfaces.md`, `realization/frontend-routes.md`, `coverage.md` | React/Vite frontend contract tests and production build | `npm --prefix frontend test -- --run` passed 180 tests, including Agent Admin doc-editing fixture/API/surface/router tests. `npm --prefix frontend run typecheck` and `npm --prefix frontend run build` passed. | `frontend-rendered` | Frontend evidence is contract/render/build-level; no browser/manual flow against the real protected API was completed in this verification pass. |
| Full-stack local runtime readiness | `README.md` done state, `05-validation-sprint.md` | Local Maven full suite + frontend tests/build + diff check | Frontend checks passed; `mvn test` failed with 3 failures and 7 errors. | `frontend-rendered` | Not `runtime-ready`: full Maven suite is red, Agent Admin workstream API smoke is red, stale governance backend tests remain, and collateral authorization smoke failures block closure. |

## Maven failure inventory from `mvn test`

`mvn test` failed after 434 tests, with 3 failures, 7 errors, and 2 skipped. Relevant failures/errors:

- `AgentAdminBrowserWorkstreamSmokeTest.protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions` failed at line 100: protected `action-agent-admin-show-agents` returned an empty `rows` list for the current doc-editing Agent Admin smoke.
- `WorkstreamServiceTest.agentAdminCatalogDetailAndArtifactReadsAreBackendAuthoritativeAndRedacted` errored with `AuthorizationException: agent-admin-requires-saas-owner-admin` while still exercising stale governance/catalog artifacts.
- `WorkstreamServiceTest.agentAdminDefinitionLifecycleAndSeedImportActionsAreGovernedAndIdempotent` errored with `agent-admin-requires-saas-owner-admin` while still exercising stale activation/seed/governance flows.
- `WorkstreamServiceTest.agentAdminCatalogIsTenantScoped` errored with `agent-admin-requires-saas-owner-admin`; current intent is platform-wide SaaS-admin-only, not tenant-scoped Agent Admin.
- `WorkstreamServiceTest.customerAdminCannotAccessAgentAdminWorkstreamOrLegacyBehaviorManagement` failed because the implementation now returns `agent-admin-requires-saas-owner-admin` instead of stale `agent-admin-requires-tenant-admin`.
- `WorkstreamServiceTest.submitMessageRoutesRepresentativeCoreWorkstreamSurfaceIntentsWithoutModelOrMutation`, `shellRequestsResolveRichSurfacesThroughBackendAndPreserveBootstrapGuard`, and `myAccountOpenWorkstreamActionReturnsBackendResolvedSurface` errored with `agent-admin-requires-saas-owner-admin` in representative workstream routing paths.
- `MyAccountBrowserWorkstreamSmokeTest.hostedShellAndProtectedWorkstreamApiExerciseMyAccountDashboardRuntimePath` errored on `/api/workstream/actions` with HTTP 403 `agent-admin-requires-saas-owner-admin`.
- `AuditTraceBrowserWorkstreamSmokeTest.protectedAuditTraceDashboardDeniesUnauthorizedAndDisabledContextsSafelyWhileScopingCustomers` failed because the SaaS Owner/Admin dashboard payload did not contain expected `scopeType=saas_owner` evidence.

## Result

- readiness level: `frontend-rendered` overall, with important backend slices at `backend-ready`.
- runtime-ready: no.
- mini-project closed: no.

The implemented slices have strong service, agent-test-provider, runtime-loader, frontend contract, typecheck, and build evidence. The terminal gate cannot mark the mini-project closed because the required Maven full suite is red and the current Agent Admin Akka workstream/API smoke path is not yet proven.

## Required repairs

- app-description gaps: none identified in the Agent Admin intent read for this verification pass.
- implementation/test gaps:
  - Repair current Agent Admin workstream/API smoke so `list-agent-doc-agents` returns non-empty current doc-editing rows for a SaaS Owner/Admin and continues through detail/doc/edit/version/create-delete/trace action paths.
  - Reconcile stale backend tests that still treat Agent Admin as tenant-scoped governance, prompt-risk, seed import, model ref, tool-boundary, activation, or rollback administration.
  - Repair collateral full-suite workstream authorization smoke failures in My Account and Audit/Trace without broadening Agent Admin access.
- provider/config blockers:
  - No real model/provider smoke was run. Editing-agent tests use Akka `TestModelProvider` for deterministic contract validation, and missing runtime/provider behavior is verified as fail-closed.
- queue changes:
  - Added `AADE-06-001` for Agent Admin workstream smoke/stale backend test drift.
  - Added `AADE-06-002` for collateral full-suite authorization smoke blockers.
  - Added `AADE-07-001` as the new terminal closure verification task.

## Checks run

- `mvn test` — failed. 434 tests run; 3 failures, 7 errors, 2 skipped. See failure inventory above and `target/surefire-reports/*`.
- `npm --prefix frontend test -- --run` — passed. 180 frontend tests passed, including Agent Admin doc-editing contracts.
- `npm --prefix frontend run typecheck` — passed. `tsc --noEmit` completed.
- `npm --prefix frontend run build` — passed. Vite production build completed; warnings only for chunk size.
- `git diff --check` — passed.
- `python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/agent-admin-doc-editing-realization/pending-tasks.md` — passed.
- `bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/agent-admin-doc-editing-realization/pending-tasks.md` — passed.

## Next step

Execute `AADE-06-001` next. Do not close the mini-project until `AADE-07-001` re-runs full terminal validation and records `runtime-ready` evidence or a further bounded follow-up loop.
