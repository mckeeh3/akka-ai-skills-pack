# Full Suite Failure Remediation

## Purpose

Create a focused root-app mini-project to remediate pre-existing full-suite failures documented during Workstream Chat Tool Catalog Expansion verification.

The goal is to make the normal project verification path clean enough that future feature work can rely on full-suite checks instead of repeatedly documenting unrelated historical failures.

## Source discussion / trigger

The completed `specs/workstream-chat-tool-catalog-expansion/` mini-project closed successfully, but its terminal verification documented remaining pre-existing failures outside that mini-project's scope:

- `npm --prefix frontend test -- --run` has one pre-existing frontend contract failure.
- `mvn test` has multiple pre-existing backend/browser-smoke failures, including governance lifecycle state mismatches, Agent Admin artifact read mismatch, User Admin status/browser-smoke mismatches, bootstrap role/capability mismatch, Akka runtime seam/autonomous runtime failures, and an attention producer failure.

The user asked to create a mini-project for these pre-existing full-suite failures.

## Scope

Root app-facing assets only:

- `src/main/java/ai/first/**`
- `src/test/java/ai/first/**`
- `frontend/src/**`
- `app-description/**` only where current intent needs repair to match accepted behavior
- `specs/full-suite-failure-remediation/**`

## Done state

This mini-project is complete when:

- current full-suite failures are reproduced and classified in `failure-inventory.md`;
- each failure is resolved by either fixing implementation, fixing a stale/incorrect test, or updating current intent plus tests when implementation discovery changes accepted behavior;
- `npm --prefix frontend test -- --run` passes;
- `npm --prefix frontend run typecheck` passes;
- `mvn test` passes, or any remaining failures are explicitly moved to new bounded follow-up tasks with accepted blockers;
- targeted tests for each repaired cluster pass;
- security and runtime invariants remain intact: tenant/customer scoping, backend authorization, audit/work traces, provider fail-closed behavior, frontend secret boundaries, deterministic surface routing, and confirmed chat-tool semantics;
- terminal verification records the final command evidence and closes the queue or appends bounded follow-up tasks plus a new terminal verification task.

## Non-goals

- Do not change the completed Workstream Chat Tool Catalog Expansion semantics unless a failure is directly caused by them.
- Do not weaken tests just to make the suite green. If a test is stale, update it to the accepted current behavior and cite the current-intent or implementation evidence.
- Do not count mock/model-less normal runtime behavior as satisfying model-backed or autonomous-agent runtime expectations.
- Do not commit unrelated generated/static frontend assets unless a selected task explicitly requires production frontend build output.
- Do not broaden into new product features beyond fixing the documented full-suite failures.

## Failure clusters from prior verification

Initial known clusters from `specs/workstream-chat-tool-catalog-expansion/verification-notes.md`:

1. Frontend surface intent routing contract failure:
   - `workstream-surface-intent-routing` contract: destructive/approval-gated asks safe fallback.
2. Governance/Policy lifecycle and browser-smoke failures:
   - `GovernancePolicyServiceTest` proposal draft submit / rejection / request changes / simulation decision failures.
   - `WorkstreamServiceTest.governancePolicyBackendActionsExposeReadProposalSimulationApprovalAndBlockedRuntimeSurfaces`.
   - `GovernancePolicyBrowserWorkstreamSmokeTest.protectedWorkstreamApiExercisesGovernancePolicyOutcomeRuntimePath`.
3. Agent Admin artifact read mismatch:
   - `WorkstreamServiceTest.agentAdminCatalogDetailAndArtifactReadsAreBackendAuthoritativeAndRedacted`.
4. User Admin status and browser-smoke failures:
   - `WorkstreamServiceTest.userAdminStatusActionsDisableReactivateNoOpAndDenyManualSelfDisable`.
   - `UserAdminBrowserWorkstreamSmokeTest` two failures around support-access grant and system-message coverage.
5. MeService bootstrap role/capability mismatch:
   - `MeServiceTest.configuredBootstrapAdminLinksOnlyExplicitSaasOwnerLocalAccount`.
6. Runtime seam/autonomous/browser harness failures:
   - `WorkstreamServiceTest.starterSourceContainsConcreteAkkaWorkstreamRuntimeAgentAndInvokerSeam`.
   - `WorkstreamServiceTest.auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists`.
   - `MyAccountBrowserWorkstreamSmokeTest` two runtime errors.
7. Attention producer failure:
   - `AttentionProducerServiceTest.governanceSubmitProducesApprovalAttentionAndDecisionResolvesWithoutLeakingToUnauthorizedOrOtherTenant`.

The first implementation task must reproduce the current exact failure list because the suite may have changed since these notes were written.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run required checks or mark blocked with a precise reason, and make one focused git commit before being marked `done`.

The terminal verification task must compare completed work against this README done state. If material failures remain, it must append more bounded tasks plus a new terminal verification task.

## Read order for future task sessions

1. `AGENTS.md`
2. `specs/AGENTS.md`
3. `specs/full-suite-failure-remediation/README.md`
4. `specs/full-suite-failure-remediation/conversation-capture.md`
5. `specs/full-suite-failure-remediation/pending-tasks.md`
6. selected sprint/backlog/task brief
7. `specs/workstream-chat-tool-catalog-expansion/verification-notes.md`
8. task-specific source/test/current-intent files

## Sprint sequence

1. Sprint 01: Reproduce and classify failures.
2. Sprint 02: Repair frontend and focused backend mismatch clusters.
3. Sprint 03: Repair governance/user-admin/agent-admin/identity clusters.
4. Sprint 04: Repair runtime seam/browser smoke/attention clusters.
5. Sprint 05: Full-suite terminal verification and follow-up loop.

## Open concerns

- Some failures may reveal stale tests rather than broken runtime behavior. Repairs must document the accepted current behavior before changing assertions.
- Akka runtime seam and autonomous-agent failures may require broader runtime integration than one small patch. If they are too large, block or split them into smaller follow-up tasks rather than inventing fake runtime readiness.
