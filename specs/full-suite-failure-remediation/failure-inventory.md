# Full Suite Failure Remediation Failure Inventory

Task: `TASK-FSFR-01-001`  
Captured: 2026-06-23  
Scope: baseline reproduction/classification only; no runtime, frontend, or backend repair attempted.

## Commands run

| Command | Result | Evidence summary |
|---|---|---|
| `npm --prefix frontend test -- --run` | failed | 177 tests: 176 pass, 1 fail. Failure is `surface intent router keeps destructive and approval-gated asks on safe fallback` in `frontend/src/workstream-surface-intent-routing.contract.test.mjs:39`. |
| `npm --prefix frontend run typecheck` | passed | `tsc --noEmit` completed with no TypeScript errors. |
| `mvn test` | failed | Full run was used, not a targeted substitute. Result: 431 tests, 16 failures, 4 errors, 2 skipped. Surefire reports are under `target/surefire-reports/`. |

## Current failure summary

| Area | Current count | Task mapping |
|---|---:|---|
| Frontend surface intent routing contract | 1 frontend test failure | `TASK-FSFR-02-001` |
| Governance/Policy lifecycle and browser runtime surfaces | 10 backend/browser failures | `TASK-FSFR-03-001` |
| Agent Admin artifact read/redaction | 1 backend failure | `TASK-FSFR-04-001` |
| User Admin status/support-access/system-message browser smoke | 3 backend/browser failures | `TASK-FSFR-05-001` |
| Bootstrap audit capability mapping | 1 backend failure | `TASK-FSFR-06-001` |
| Runtime seam/autonomous/My Account browser runtime | 4 backend/browser errors/failures | `TASK-FSFR-07-001` and, if still split after runtime seam work, `TASK-FSFR-08-001` |
| Attention producer governance submit | 1 backend error | `TASK-FSFR-09-001` |

## Frontend failures

### FSFR-FE-001 — destructive/approval-gated surface prompts contract drift

- Reproduction command: `npm --prefix frontend test -- --run`
- Affected test: `frontend/src/workstream-surface-intent-routing.contract.test.mjs:39`
- Exact failure: `AssertionError [ERR_ASSERTION]: The input did not match the regular expression /submitMessageFallsBackSafelyForUnauthorizedAmbiguousOrHighRiskSurfacePrompts/.`
- Observed suite result: 177 tests, 176 pass, 1 fail.
- Affected contract/source area: frontend surface intent routing contract and backend test/source evidence expected by that contract, specifically `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java` content referenced by the frontend contract.
- Suspected root cause: stale or missing backend regression evidence name for high-risk/destructive surface prompt fallback; the frontend contract still expects `submitMessageFallsBackSafelyForUnauthorizedAmbiguousOrHighRiskSurfacePrompts` to exist in the backend evidence file.
- Materiality: high for deterministic surface routing safety because the contract guards destructive/approval-gated prompts from unsafe routing or auto-execution.
- Task mapping: `TASK-FSFR-02-001`.
- Difference from prior verification notes: unchanged from both Workstream Chat Tool Catalog terminal verification entries; the current suite still reports 176 pass and this single pre-existing frontend failure.

## Backend failures and errors from `mvn test`

### FSFR-BE-001 — Governance/Policy service submit lifecycle expects `in_review`, implementation returns `simulation-required`

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java:92`
- Exact failure: `proposalDraftSubmitReadLifecycleIsIdempotentAndDoesNotMutateAuthority: expected: <in_review> but was: <simulation-required>`
- Suspected root cause: Governance proposal submit lifecycle is not aligned between accepted test intent and implementation; submit currently transitions to `simulation-required` instead of directly to `in_review`.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-002 — Governance/Policy simulation decision lifecycle expects `in_review`, implementation returns `simulation-required`

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java:110`
- Exact failure: `simulationDecisionActivationRollbackAreScopedIdempotentAndFailClosed: expected: <in_review> but was: <simulation-required>`
- Suspected root cause: same Governance/Policy lifecycle state mismatch as FSFR-BE-001.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-003 — Governance/Policy request-changes action expects accepted, implementation returns approval-required

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java:173`
- Exact failure: `requestChangesAndOutcomeNotesAreExplicitRetainedAuthorityActions: expected: <accepted> but was: <approval-required>`
- Suspected root cause: Governance/Policy retained-authority action semantics are not aligned between service implementation and stale/current test expectations.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-004 — Governance/Policy rejection action expects accepted, implementation returns approval-required

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/foundation/governance/GovernancePolicyServiceTest.java:206`
- Exact failure: `rejectionBlocksActivationAndRollbackRequiresActivatedProposal: expected: <accepted> but was: <approval-required>`
- Suspected root cause: same Governance/Policy retained-authority/approval state mismatch as FSFR-BE-003.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-005 — Governance/Policy workstream action surface does not include expected submit state

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java:3688`
- Exact failure: `governancePolicyBackendActionsExposeReadProposalSimulationApprovalAndBlockedRuntimeSurfaces: expected: <true> but was: <false>`
- Suspected root cause: workstream surface assertion expects an `in_review` submit-proposal state/copy, while the backend Governance/Policy workflow currently exposes `simulation-required` or otherwise different surface content.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-006 — Governance/Policy proposal browser runtime path expects empty/new draft but receives draft

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/GovernancePolicyBrowserWorkstreamSmokeTest.java:262`
- Exact failure: `hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicyProposalRuntimePath: expected: <empty/new-draft> but was: <draft>`
- Suspected root cause: browser/workstream smoke expectations are not aligned with current proposal draft surface state.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-007 — Governance/Policy simulation browser runtime path expects `in_review`, implementation returns `simulation-required`

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/GovernancePolicyBrowserWorkstreamSmokeTest.java:557`
- Exact failure: `hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicySimulationRuntimePath: expected: <in_review> but was: <simulation-required>`
- Suspected root cause: same Governance/Policy lifecycle state mismatch as FSFR-BE-001.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-008 — Governance/Policy dashboard browser runtime path expects empty/new draft but receives draft

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/GovernancePolicyBrowserWorkstreamSmokeTest.java:774`
- Exact failure: `hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicyDashboardRuntimePath: expected: <empty/new-draft> but was: <draft>`
- Suspected root cause: dashboard surface state/copy expectation is not aligned with the current proposal draft lifecycle.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-009 — Governance/Policy decision browser runtime path expects `in_review`, implementation returns `simulation-required`

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/GovernancePolicyBrowserWorkstreamSmokeTest.java:1326`
- Exact failure: `protectedWorkstreamApiExercisesGovernancePolicyDecisionRuntimePath: expected: <in_review> but was: <simulation-required>`
- Suspected root cause: same Governance/Policy lifecycle state mismatch as FSFR-BE-001.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-010 — Governance/Policy outcome browser runtime path expects `in_review`, implementation returns `simulation-required`

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/GovernancePolicyBrowserWorkstreamSmokeTest.java:1595`
- Exact failure: `protectedWorkstreamApiExercisesGovernancePolicyOutcomeRuntimePath: expected: <in_review> but was: <simulation-required>`
- Suspected root cause: same Governance/Policy lifecycle state mismatch as FSFR-BE-001.
- Task mapping: `TASK-FSFR-03-001`.

### FSFR-BE-011 — Agent Admin artifact read returns skill manifest diff surface instead of manifest surface

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java:1294`
- Exact failure: `agentAdminCatalogDetailAndArtifactReadsAreBackendAuthoritativeAndRedacted: expected: <agent_admin.manifest.v1> but was: <agent_admin.skill_manifest_diff.v1>`
- Suspected root cause: Agent Admin artifact read/redaction surface selection is returning the skill manifest diff surface where the test expects the backend-authoritative manifest surface.
- Task mapping: `TASK-FSFR-04-001`.

### FSFR-BE-012 — User Admin disable-member action returns no-op instead of accepted

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java:1575`
- Exact failure: `userAdminStatusActionsDisableReactivateNoOpAndDenyManualSelfDisable: expected: <accepted> but was: <no-op>`
- Suspected root cause: member disable lifecycle/idempotency behavior is not aligned with test expectations; could be stale assertion or implementation no-op for an already-disabled/mismatched target.
- Task mapping: `TASK-FSFR-05-001`.

### FSFR-BE-013 — User Admin system-message browser safety assertion sees unsafe content

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java:592` via `assertBrowserSafe` at line 4451
- Exact failure: `protectedWorkstreamApiExercisesUserAdminSystemMessageRuntimeCoverage: expected: <false> but was: <true>`
- Suspected root cause: User Admin system-message runtime coverage is exposing content or metadata that the browser-safety assertion treats as unsafe.
- Task mapping: `TASK-FSFR-05-001`.

### FSFR-BE-014 — User Admin hidden support-access grant target is not denied

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java:1013`
- Exact failure: `protectedWorkstreamApiExercisesUserAdminSupportAccessGrantRuntimePath: Hidden support-access grant targets must be denied without a successful browser payload. ==> Expected java.lang.RuntimeException to be thrown, but nothing was thrown.`
- Suspected root cause: support-access grant path is accepting or surfacing a hidden target where browser/API contract expects a denial.
- Task mapping: `TASK-FSFR-05-001`.

### FSFR-BE-015 — bootstrap SaaS owner audit capability id mismatch

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/foundation/identity/MeServiceTest.java:107`
- Exact failure: `configuredBootstrapAdminLinksOnlyExplicitSaasOwnerLocalAccount: expected: <[saas_owner.audit.read]> but was: <[audit.trace.read]>`
- Suspected root cause: bootstrap admin role/capability mapping changed from SaaS-owner audit capability naming to general Audit/Trace capability naming, or the test is stale relative to current authorization semantics.
- Task mapping: `TASK-FSFR-06-001`.

### FSFR-BE-016 — production WorkstreamRuntimeAgent seam still appears fake

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java:238`
- Exact failure: `starterSourceContainsConcreteAkkaWorkstreamRuntimeAgentAndInvokerSeam: Production workstream agent must not be a fake runtime ==> expected: <false> but was: <true>`
- Suspected root cause: `WorkstreamRuntimeAgent` or its invoker seam still contains fake/test-adapter wording or implementation rather than a concrete Akka Agent-backed runtime seam.
- Task mapping: `TASK-FSFR-07-001`.

### FSFR-BE-017 — Audit/Trace summary worker fail-closed test errors on missing durable Akka binding

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java:3608`
- Exact error: `auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists: AuditTraceSummaryTaskRepository unavailable: Durable Akka foundation repository binding is required for normal runtime. Test doubles are allowed only from test source; generated-app runtime must bind Akka-backed repositories before external use.`
- Suspected root cause: autonomous Audit/Trace summary runtime/repository seam is failing with an infrastructure binding error instead of the test's expected browser-safe fail-closed behavior.
- Task mapping: `TASK-FSFR-07-001`.

### FSFR-BE-018 — My Account profile browser runtime path errors after unsupported field denial

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`
- Exact error: `protectedWorkstreamApiExercisesMyProfileRuntimePathAndDenials: HTTP request for [http://localhost:39390/api/workstream/actions] failed with HTTP status 403 Forbidden: MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD:roleIds`
- Suspected root cause: browser smoke test continues through a denial scenario as an unhandled runtime error, or test/setup expectations were not updated after direct-action unsupported-field validation was added.
- Task mapping: `TASK-FSFR-07-001`; split to `TASK-FSFR-08-001` if runtime seam work does not cover browser smoke harness handling.

### FSFR-BE-019 — My Account settings browser runtime path errors after providerSecret unsupported field denial

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`
- Exact error: `protectedWorkstreamApiExercisesMySettingsRuntimePath: HTTP request for [http://localhost:39390/api/workstream/actions] failed with HTTP status 403 Forbidden: MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD:providerSecret`
- Suspected root cause: same My Account browser-smoke denial handling/update issue as FSFR-BE-018 after direct-action field validation.
- Task mapping: `TASK-FSFR-07-001`; split to `TASK-FSFR-08-001` if runtime seam work does not cover browser smoke harness handling.

### FSFR-BE-020 — Governance submit attention lookup finds no value

- Reproduction command: `mvn test`
- Affected test: `src/test/java/ai/first/application/foundation/attention/AttentionProducerServiceTest.java:150`
- Exact error: `governanceSubmitProducesApprovalAttentionAndDecisionResolvesWithoutLeakingToUnauthorizedOrOtherTenant: java.util.NoSuchElementException: No value present`
- Suspected root cause: Governance submit no longer produces the approval attention item expected by the producer test, likely tied to the lifecycle state mismatch or attention recipient lookup.
- Task mapping: `TASK-FSFR-09-001`; if Governance/Policy lifecycle repair proves this is the same root cause, `TASK-FSFR-03-001` may resolve it and `TASK-FSFR-09-001` should re-verify/close or adjust.

## Differences from prior Workstream Chat Tool Catalog verification notes

- Compared with `TASK-WCTC-99-002` re-verification notes, the current baseline is materially unchanged: frontend remains 176/177 with the same surface intent routing contract failure; typecheck remains clean; `mvn test` remains 431 tests with 16 failures and 4 errors.
- The current My Account browser errors now have exact reproduced messages in this inventory: `MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD:roleIds` and `MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD:providerSecret`.
- Compared with the earlier `TASK-WCTC-99-001` section, the backend failure list is larger because the later re-verification ran the expanded full suite and recorded 20 backend failures/errors instead of the earlier 7-item pre-existing list.
- No new cluster beyond the existing full-suite-remediation queue was discovered. Current failures map to existing `TASK-FSFR-02-001` through `TASK-FSFR-09-001`.

## Next task mapping

The next runnable queued task after this baseline is `TASK-FSFR-02-001: Repair frontend surface intent routing contract failure`.
