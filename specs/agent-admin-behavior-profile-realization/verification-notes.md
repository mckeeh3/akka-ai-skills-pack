# Runtime Feature Verification

## Scope

- Task: `AABP-05-003` rerun terminal verification for Agent Admin behavior-profile realization after `AABP-05-002` repaired the full-suite seed-count blocker.
- Target readiness from mini-project README: `api-smoked/frontend-rendered` for implemented current scope, with provider/manual residuals recorded explicitly.
- Date: 2026-06-27.

## Compile contract checked

- worker(s): SaaS Admin Human, Agent Admin Functional Agent Worker, Agent Behavior Editor Internal Agent Worker, Agent Runtime System Worker.
- harness/actor adapter(s): browser workstream shell with `surface_action`; confirmed `human_chat_tool_plan` posture documented; concrete Akka `AgentAdminDocEditingAgent` model-backed edit path under `TestModelProvider`; runtime resolver `internal_call` and governed loader tools.
- governed tool(s): `list-agent-doc-agents`, `read-agent-doc-agent`, `inspect-agent-runtime-profile`, `draft-agent-doc-edit`, `revise-agent-doc-edit`, `save-agent-doc-edit`, `approve-agent-doc-proposal`, `reject-agent-doc-proposal`, `activate-agent-doc-version`, `restore-agent-doc-version`, `assign-agent-skills`, `assign-agent-generated-tools`, `update-agent-model-config-ref`, `read-agent-doc-runtime-traces`, runtime `readSkill`, runtime `readReferenceDoc`.
- capability/capabilities: `agent-doc-administration`, `agent_admin.*`, `saas_owner.admin.manage`, runtime agent invocation capabilities, loader capabilities `agent.skills.read` / `agent.references.read`.
- API/Akka path(s): `/api/workstream/bootstrap`, `/api/workstream/surfaces/{surfaceId}`, `/api/workstream/actions` -> `WorkstreamService` Agent Admin action routing -> `AgentAdminDocAdministrationService` / behavior-profile repository / `AgentRuntimeService` / `AgentRuntimeToolResolver` / `ComponentClientAgentAdminDocEditingRuntime`.
- trace/result surface(s): Agent Admin surfaces `surface-agent-admin-*`, proposal review/version/history/diff/runtime-trace surfaces, `AgentRuntimeTrace` rows, `PROMPT_ASSEMBLY`, `SKILL_LOAD`, reference-load, generated-tool assignment, `EDIT_AGENT_INVOCATION`, save/activation/cancel proposal traces.

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
|---|---|---|---|---|---|
| SaaS Owner/Admin-only Agent Admin access and browser-safe non-admin denial | `access.md`, `tests/coverage.md`, capability doc | WorkOS/AuthKit bearer + selected context -> `/api/workstream/bootstrap`/surfaces -> WorkstreamService -> Agent Admin capability checks | `AgentAdminBrowserWorkstreamSmokeTest.protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions`; `AgentAdminDocAdministrationServiceTest.nonSaasAdminCannotSaveDraftOrActivateProposal`; `mvn test` passed. | `api-smoked` | None for stated target. |
| Current surface inventory excludes stale governance-console/direct whole-agent mutation exposure | `surfaces/surfaces.md`, `realization/frontend-routes.md` | browser surface -> workstream action catalog -> frontend renderer | Backend smoke asserts current action catalog and rejects stale product action; frontend contract tests assert current surface ids/contracts and stale ids absent; frontend test/typecheck/build passed. | `frontend-rendered` | Internal `updateAgentProfile` test seam remains de-exposed from product/API path; not a material blocker for stated target. |
| Proposal-first prompt edit lifecycle with separate activation | `behavior.md`, `tools/governed-tools.md`, `tests/coverage.md` | surface action -> `save-agent-doc-edit` -> proposal review -> `activate-agent-doc-version` -> active doc version | `AgentAdminBrowserWorkstreamSmokeTest` exercises start/revise/save/approve/activate/stale repeat activation via `/api/workstream/actions`; service tests verify Save Draft non-active and activation creates current version; `mvn test` passed. | `api-smoked` | None for stated target. |
| Stale/high-risk/authority-expanding activation denials keep active behavior unchanged | `behavior.md`, `policies/policy-bindings.md` | service proposal activation checks -> safe denial/system message | `AgentAdminDocAdministrationServiceTest.staleProposalActivationIsRejectedAfterCurrentVersionChanges`; `highRiskAuthorityExpandingProposalDirectActivationIsDeniedAndActiveVersionUnchanged`; browser smoke stale activation returns `surface-agent-admin-system-message`; `mvn test` passed. | `api-smoked` for stale denial; `backend-ready` for high-risk denial | High-risk denial remains service-level automated evidence, not separate browser smoke. |
| Restore creates proposal before active version changes | `behavior.md`, `tools/governed-tools.md` | doc version history -> restore proposal -> separate activation | `AgentAdminDocAdministrationServiceTest.editSessionSaveDraftActivationCancelAndRestoreContractsAreExposedAtServiceBoundary`; `mvn test` passed. | `backend-ready` | No dedicated browser/API restore action smoke in terminal path. |
| Skill/reference create/deprecate lifecycle removes hidden loader access | `behavior.md`, capability doc | Agent Admin service -> behavior repository -> `AgentRuntimeService.readSkill/readReferenceDoc` | `AgentAdminDocAdministrationServiceTest.skillAndReferenceCreateUsesProposalAndDeprecationRemovesLoaderAccessWithoutHardDelete`; `mvn test` passed. | `backend-ready` | Browser create/delete surfaces are frontend-rendered, not runtime-smoked end-to-end. |
| Behavior-profile model/skill/generated-tool assignment versions | `behavior.md`, `realization/api-contracts.md`, `surfaces/surfaces.md` | surface action -> `updateBehaviorProfileAssignments` -> active tenant behavior profile version -> runtime resolver | `AgentAdminDocAdministrationServiceTest.behaviorProfileAssignmentCreatesTenantScopedVersionWithoutMutatingSkillDocsOrGeneratedTools`; browser smoke opens assignment/model ref surfaces and no-op model ref action; frontend contracts render profile history/assignment surfaces; all required checks passed. | `api-smoked` | Assignment activation via browser is partially smoked; no manual browser run. |
| Runtime loader resolves active profiles/docs only, enforces assigned skill/reference/generated-tool boundaries, and emits safe traces | `traces/work-traces.md`, `tests/coverage.md` | runtime worker -> `AgentRuntimeService` -> `AgentRuntimeToolResolver` -> loader/evidence tools -> trace sink -> Agent Admin trace surface | `AgentRuntimeServiceTest.runtimeLoadsOnlyCurrentActiveBehaviorProfileNotProposedDraftOrMutatedAgentDefinition`; `readSkillRequiresActiveProfileAssignmentInAdditionToManifestAndBoundary`; `AgentRuntimeToolResolverTest.generatedToolsMustBeAssignedByActiveProfileAndBoundary`; `AgentRuntimeTraceSinkTest`; browser smoke opens runtime trace surface and asserts full doc content is not exposed; `mvn test` passed. | `api-smoked` | No real-provider model call smoke; provider path remains test-model/fail-closed evidence. |
| Model-backed editing-agent path uses concrete Akka Agent when configured and fails closed when missing | `agents/functional-agent.md`, `workers/agent-behavior-editor-internal-agent-worker.md` | service -> `ComponentClientAgentAdminDocEditingRuntime` -> Akka `AgentAdminDocEditingAgent` -> model provider -> save/activate proposal traces | `AgentAdminDocEditingAgentTest.draftsRevisesSavesAndCancelsThroughModelBackedAgentPath`; `missingEditingAgentModelRuntimeFailsClosedBeforeModelSuccessIsFaked`; `mvn test` passed. | `backend-ready` | Uses `TestModelProvider`; no real external provider configured or claimed. |
| Frontend renders current Agent Admin surfaces without raw JSON/secret exposure | `surfaces/surfaces.md`, `realization/frontend-routes.md` | frontend fixtures/types/API client -> `AgentAdminDocEditingSurface` -> action bar/rendered DOM contracts | `npm --prefix frontend test -- --run` passed; `npm --prefix frontend run typecheck` passed; `npm --prefix frontend run build` passed. | `frontend-rendered` | Automated contract/build evidence only; no local browser/manual smoke. |

## Result

- readiness level: `api-smoked` for the protected workstream/API path and `frontend-rendered` for frontend contracts/build.
- runtime-ready: no.
- mini-project closed: yes, for the README's stated `api-smoked/frontend-rendered` terminal target.
- closure basis: all required backend/frontend checks passed after `AABP-05-002`; no remaining material gap blocks the stated target. Remaining provider/manual/browser residuals are explicitly not counted as `runtime-ready` and do not require another bounded follow-up loop for this mini-project.

## Required repairs

- app-description gaps: none found for the implemented `api-smoked/frontend-rendered` scope.
- implementation gaps: no new Agent Admin product-path gap was found beyond provider/manual residuals and the service-internal whole-agent profile update seam being de-exposed rather than removed.
- test gaps/blockers: none blocking the stated closure target; the prior `AgentBehaviorSeedLoaderTest` full-suite blocker is resolved.
- provider/config blockers: no real external model provider smoke was run; provider-backed editing remains represented by `TestModelProvider` and fail-closed missing-runtime tests.
- queue changes: mark `AABP-05-003` done; no further Agent Admin behavior-profile realization follow-up task is appended.

## Checks run

- `mvn test` — passed (exit code 0): 450 tests run, 0 failures, 0 errors, 2 skipped; build success.
- `npm --prefix frontend test -- --run` — passed (exit code 0): 182 frontend contract tests passed.
- `npm --prefix frontend run typecheck` — passed (exit code 0): TypeScript completed with no errors.
- `npm --prefix frontend run build` — passed (exit code 0): Vite build completed; emitted existing chunk-size warnings only.
- `git diff --check` — passed (exit code 0): no whitespace errors in the final diff.

## Next step

No next runnable task is appended for this mini-project. Future elevation from `api-smoked/frontend-rendered` to `runtime-ready` requires a separate runtime/manual/provider smoke scope with real local API/UI/agent-path evidence and configured provider or explicit fail-closed runtime evidence.
